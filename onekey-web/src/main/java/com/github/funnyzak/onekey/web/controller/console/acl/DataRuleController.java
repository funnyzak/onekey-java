package com.github.funnyzak.onekey.web.controller.console.acl;

import com.github.funnyzak.onekey.web.controller.console.base.ConsoleBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.acl.DataRule;
import com.github.funnyzak.onekey.bean.acl.DataRuleDataPermissionType;
import com.github.funnyzak.onekey.bean.acl.DataRuleModule;
import com.github.funnyzak.onekey.bean.vo.InstallPermission;
import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.onekey.biz.service.acl.DataRuleService;
import com.github.funnyzak.onekey.common.Result;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/11/1 5:59 PM
 * @description DataPermissionController
 */
@RestController
@RequestMapping("console/dataRule")
@Api(value = "DataPermission", tags = {"后端.访问控制"})
public class DataRuleController extends ConsoleBaseController {

    private final DataRuleService dataRuleService;

    @Autowired
    public DataRuleController(DataRuleService dataRuleService) {
        this.dataRuleService = dataRuleService;
    }

    /**
     * 数据权限列表
     */
    @GetMapping("list")
    @RequiresPermissions(InstallPermission.DATA_RULE_LIST)
    @ApiOperation("数据权限列表")
    @WebLogger()
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page
            , @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize
            , @RequestParam(value = "module", required = false) @ApiParam("关联模块") DataRuleModule module
            , @RequestParam(value = "dataRuleType", required = false) @ApiParam("数据权限类型") DataRuleDataPermissionType type) {

        Cnd cnd = Cnd.where("id", ">", 0);

        cnd = cnd.andEX("module", "=", module)
                .andEX("ruleType", "=", type);
        PageredData<DataRule> pager = dataRuleService.searchByPage(_fixPage(page), pageSize, cnd.desc("id"));

        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }


    /**
     * 添加数据权限
     *
     * @return 数据权限实体
     */
    @PutMapping("add")
    @RequiresPermissions(InstallPermission.DATA_RULE_ADD)
    @ApiOperation("添加数据权限")
    @WebLogger()
    public Result add(@RequestBody DataRule dataRule) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, dataRuleService.userAdd(currentUser(), dataRule));
        } catch (Exception ex) {
            return Result.fail(ex.getMessage());
        }
    }

    /**
     * 数据权限编辑
     *
     * @param dataRule 数据权限实体
     * @return 数据权限实体
     */
    @PostMapping("edit")
    @RequiresPermissions(InstallPermission.DATA_RULE_EDIT)
    @ApiOperation("更新数据权限")
    @WebLogger()
    public Result update(@RequestBody DataRule dataRule) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, dataRuleService.userEdit(currentUser(), dataRule));
        } catch (Exception ex) {
            return Result.fail(ex.getMessage());
        }
    }

    /**
     * 数据权限详情
     *
     * @param id 数据权限id
     * @return 数据权限实体
     */
    @GetMapping("{id}")
    @RequiresPermissions(InstallPermission.DATA_RULE_EDIT)
    @ApiOperation("数据权限详情")
    @WebLogger()
    public Result detail(@PathVariable("id") @ApiParam("数据权限id") long id) {
        return Result.success().addData(JsonConstants.INFO_NAME, dataRuleService.fetch(id));
    }

    /**
     * 删除数据权限
     *
     * @param id 数据权限id
     * @return 处理结果
     */
    @DeleteMapping("{id}")
    @RequiresPermissions(InstallPermission.DATA_RULE_DELETE)
    @ApiOperation("删除数据权限")
    @WebLogger()
    public Result delete(@PathVariable("id") @ApiParam("数据权限id") long id) {
        try {
            dataRuleService.userDel(currentUser(), id);
            return Result.success();
        } catch (Exception ex) {
            return Result.fail(ex.getMessage());
        }
    }
}