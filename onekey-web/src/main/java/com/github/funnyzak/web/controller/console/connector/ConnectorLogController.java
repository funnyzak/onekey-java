package com.github.funnyzak.web.controller.console.connector;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.util.Strings;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.open.ConnectorLog;
import com.github.funnyzak.bean.vo.InstallPermission;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.biz.service.open.ConnectorLogService;
import com.github.funnyzak.biz.service.open.ConnectorService;
import com.github.funnyzak.common.utils.PUtils;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.web.annotation.weblog.WebLogger;
import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/18 7:30 PM
 * @description ConnectorLogController
 */
@RestController
@RequestMapping("console/connector/log")
@Api(value = "ConnectorLog", tags = {"后端.日志相关"})
public class ConnectorLogController extends ConsoleBaseController {
    private final ConnectorLogService connectorLogService;
    private final ConnectorService connectorService;

    @Autowired
    public ConnectorLogController(ConnectorLogService connectorLogService, ConnectorService connectorService) {
        this.connectorLogService = connectorLogService;
        this.connectorService = connectorService;
    }

    @GetMapping("list")
    @RequiresPermissions(InstallPermission.CONNECTOR_LOG_LIST)
    @ApiOperation("连接器请求日志")
    @WebLogger()
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page,
                       @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize,
                       @RequestParam(value = "connectorId", required = false) @ApiParam("连接器ID") Long connectorId,
                       @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key) throws Exception {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isEmpty(key)) {
            cnd = PUtils.cndBySearchKey(cnd, key, "ip", "system", "methodName");
        }

        PageredData<ConnectorLog> pager = connectorLogService.searchByPage(_fixPage(page), pageSize, cnd.andEX("connectorId", "=", connectorId).desc("id"));


        pager.setDataList(connectorService.setListConnectorInfo(pager.getDataList(), "getConnectorId", "setConnector"));


        return Result.success().addData(JsonConstants.PAGER_NAME, pager);

    }
}