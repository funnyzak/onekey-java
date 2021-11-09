package com.github.funnyzak.web.controller.console.label;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import com.github.funnyzak.bean.label.LabelInfo;
import com.github.funnyzak.bean.label.enums.LabelInfoType;
import com.github.funnyzak.bean.vo.InstallPermission;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.biz.service.label.LabelInfoService;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.web.annotation.weblog.WebLogger;
import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("console/label")
@Api(value = "Label", tags = {"标签管理"})
public class LabelController extends ConsoleBaseController {
    private final LabelInfoService labelService;

    @Autowired
    public LabelController(LabelInfoService labelService) {
        this.labelService = labelService;
    }

    @GetMapping("simple/list")
    @ApiOperation("简单列表")
    @WebLogger()
    public Result simpleList(@RequestParam(value = "type", required = true) @ApiParam("标签类型") LabelInfoType labelInfoType) {
        return Result.success().addData(JsonConstants.LIST_NAME, labelService.simpleListByFunctionType(labelInfoType));
    }

    @GetMapping("list")
    @ApiOperation("标签列表")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.LABEL_INFO_LIST})
    @WebLogger()
    public Result list(
            @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page
            , @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize
            , @RequestParam(value = "type", required = true) @ApiParam("标签类型") LabelInfoType labelInfoType
            , @RequestParam(value = "parentId", required = false) @ApiParam("父ID") Long parentId
            , @RequestParam(value = "system", required = false) @ApiParam("是否系统") Boolean isSystem
            , @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key
            , @RequestParam(value = "descBy", required = false, defaultValue = "id") @ApiParam("排序字段") String descBy
    ) {
        return Result.success().addData(JsonConstants.PAGER_NAME, labelService.userPage(currentUser(), page, pageSize, labelService.condition(null, labelInfoType, isSystem, parentId, key, null, null), descBy));
    }

    @DeleteMapping("{id}")
    @ApiOperation("删除信息")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.LABEL_INFO_ADD})
    @WebLogger()
    public Result remove(
            @PathVariable("id") @ApiParam("标签ID") Long id
            , @RequestParam(value = "type", required = true) @ApiParam("标签类型") LabelInfoType labelInfoType
    ) {
        try {
            labelService.userDelete(id, labelInfoType, currentUser());
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @GetMapping("{id}")
    @ApiOperation("标签详情")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.LABEL_INFO_DETAIL})
    @WebLogger()
    public Result detail(@PathVariable("id") @ApiParam("标签ID") Long id) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, labelService.fetch(id));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PutMapping("")
    @ApiOperation("添加信息")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.LABEL_INFO_ADD})
    @WebLogger()
    public Result add(@RequestBody LabelInfo info, @RequestParam(value = "type", required = true) @ApiParam("标签类型") LabelInfoType labelInfoType) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, labelService.userEdit(info, labelInfoType, currentUser(), true));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("")
    @ApiOperation("编辑信息")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.LABEL_INFO_EDIT})
    @WebLogger()
    public Result update(@RequestBody LabelInfo info, @RequestParam(value = "type", required = true) @ApiParam("标签类型") LabelInfoType labelInfoType) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, labelService.userEdit(info, labelInfoType, currentUser(), true));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }
}