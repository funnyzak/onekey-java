package com.github.funnyzak.web.controller.console.log;

import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.log.LoginLog;
import com.github.funnyzak.bean.vo.InstallPermission;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.biz.service.log.LoginLogService;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.web.annotation.weblog.WebLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Leon Yang
 */
@RestController
@RequestMapping("console/trace")
@Api(value = "Trace", tags = {"后端.日志相关"})
public class TraceController extends ConsoleBaseController {

    private final LoginLogService loginLogService;

    @Autowired
    public TraceController(LoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }

    /**
     * 登录日志列表
     *
     * @param page 页码
     * @return 分页数据
     */
    @GetMapping("list")
    @RequiresPermissions(InstallPermission.MONITOR_LOGIN)
    @ApiOperation("登录日志列表")
    @WebLogger()
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page,
                       @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize,
                       @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key) throws Exception {
        PageredData<LoginLog> pager = loginLogService.pager(page, pageSize, loginLogService.condition(null, key));
        pager.setDataList(loginLogService.setList(pager.getDataList(), true, true));
        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }

}
