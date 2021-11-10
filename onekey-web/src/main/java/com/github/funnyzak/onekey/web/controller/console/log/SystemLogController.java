package com.github.funnyzak.onekey.web.controller.console.log;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.log.OperationLog;
import com.github.funnyzak.onekey.bean.vo.InstallPermission;
import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.onekey.biz.service.log.OpLogService;
import com.github.funnyzak.onekey.common.Result;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import com.github.funnyzak.onekey.web.controller.console.base.ConsoleBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("console/log")
@Api(value = "Operation", tags = {"后端.日志相关"})
public class SystemLogController extends ConsoleBaseController {
    private final OpLogService logService;

    @Autowired
    public SystemLogController(OpLogService logService) {
        this.logService = logService;
    }

    @GetMapping("list")
    @RequiresPermissions(InstallPermission.MONITOR_OPERATION)
    @ApiOperation("操作日志列表")
    @WebLogger()
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page,
                       @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize,
                       @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key) {
        Cnd cnd = logService.condition(null, currentUser(), key);
        PageredData<OperationLog> pager = logService.pager(page, pageSize, cnd);
        pager.setDataList(logService.setList(pager.getDataList(), true));
        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }


    @GetMapping("latest")
    @RequiresPermissions(InstallPermission.MONITOR_OPERATION)
    @ApiOperation("最近操作日志")
    @WebLogger()
    public Result latest(@RequestParam(value = "getCount", defaultValue = "10") @ApiParam("获取操作日志数量，最多20") int getCount) {
        Cnd cnd = logService.condition(null, currentUser(), null);
        PageredData<OperationLog> pager = logService.pager(1, getCount, cnd);
        pager.setDataList(logService.setList(pager.getDataList(), true));
        return Result.success().addData(JsonConstants.LIST_NAME, pager.getDataList());
    }

}
