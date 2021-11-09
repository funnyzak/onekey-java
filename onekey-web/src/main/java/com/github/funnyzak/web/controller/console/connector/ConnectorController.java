package com.github.funnyzak.web.controller.console.connector;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.open.Connector;
import com.github.funnyzak.bean.open.enums.ConnectorPermission;
import com.github.funnyzak.bean.vo.InstallPermission;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.biz.service.open.ConnectorService;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.common.utils.PUtils;
import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/18 7:15 PM
 * @description ConnectorController
 */
@RestController
@RequestMapping("console/open/connector")
@Api(value = "Connector", tags = {"后端.连接器模块"})
public class ConnectorController extends ConsoleBaseController {
    private final ConnectorService connectorService;

    @Autowired
    public ConnectorController(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    /**
     * 连接器列表
     *
     * @return
     * @RequestParam page 页码
     */
    @GetMapping("list")
    @RequiresPermissions(InstallPermission.CONNECTOR_LIST)
    @ApiOperation("连接器列表")
    public Result list(
            @RequestParam(value = "name", required = false) @ApiParam("名称") String name
            , @RequestParam(value = "secretId", required = false) @ApiParam("连接器ID") String secretId
            , @RequestParam(value = "enable", required = false) @ApiParam("是否启用") Boolean enable
            , @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") Integer page
            , @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") Integer pageSize
    ) {
        PageredData<Connector> pager = connectorService.pager(page, pageSize, connectorService.condition(null, currentUser(), name, secretId, enable));
        pager.setDataList(pager.getDataList());
        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }

    @GetMapping("simple/list")
    @RequiresPermissions(InstallPermission.CONNECTOR_LIST)
    @ApiOperation("连接器列表")
    public Result list() {
        return Result.success().addData(JsonConstants.LIST_NAME, connectorService.allNames());
    }

    /**
     * 添加连接器
     *
     * @param connector 待添加连接器
     * @return
     */
    @PutMapping("")
    @RequiresPermissions(InstallPermission.CONNECTOR_ADD)
    @ApiOperation("添加连接器")
    public Result save(@RequestBody Connector connector) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, connectorService.add(currentUser(), connector));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    /**
     * 更新连接器
     *
     * @param connector
     * @return
     */
    @PostMapping("")
    @RequiresPermissions(InstallPermission.CONNECTOR_EDIT)
    @ApiOperation("更新连接器")
    public Result update(@RequestBody Connector connector) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, connectorService.edit(currentUser(), connector));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("reset/{secretId}")
    @RequiresPermissions(InstallPermission.CONNECTOR_EDIT)
    @ApiOperation("重置连接器密钥")
    public Result resetKey(@PathVariable("secretId") @ApiParam("连接器id") String secretId) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, connectorService.resetSecretKey(currentUser(), secretId));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    /**
     * 获取连接器详情
     *
     * @param secretId 连接器id
     * @return
     */
    @GetMapping("{secretId}")
    @RequiresPermissions(InstallPermission.CONNECTOR_EDIT)
    @ApiOperation("连接器详情")
    public Result detail(@PathVariable("secretId") @ApiParam("连接器id") String secretId) {
        return Result.success().addData(JsonConstants.INFO_NAME, connectorService.fetch(secretId));
    }

    /**
     * 删除连接器
     *
     * @param secretId 连接器id
     * @return
     */
    @DeleteMapping("{secretId}")
    @RequiresPermissions(InstallPermission.CONNECTOR_DELETE)
    @ApiOperation("删除连接器")
    public Result delete(@PathVariable("secretId") @ApiParam("连接器id") String secretId) {
        try {
            connectorService.remove(currentUser(), secretId);
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @GetMapping("permission/info/list")
    @RequiresAuthentication
    @ApiOperation("连接器所有权限列表")
    public Result permissionInfoList() {
        try {
            return Result.success().addData(JsonConstants.LIST_NAME, PUtils.enumsInfoList(ConnectorPermission.class));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }
}