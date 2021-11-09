package com.github.funnyzak.web.controller.console.pm;

import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.log.PmLog;
import com.github.funnyzak.bean.log.enums.SmsServerType;
import com.github.funnyzak.bean.log.enums.PmType;
import com.github.funnyzak.bean.log.enums.PmUse;
import com.github.funnyzak.bean.vo.InstallPermission;
import com.github.funnyzak.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.biz.ext.shiro.matcher.SINOCredentialsMatcher;
import com.github.funnyzak.biz.service.log.PmLogService;
import com.github.funnyzak.biz.service.log.PmService;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.common.utils.DateUtils;
import com.github.funnyzak.common.utils.StringUtils;
import com.github.funnyzak.web.annotation.weblog.WebLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/8 5:59 PM
 * @description PmController
 */
@RestController
@RequestMapping("console/pm")
@Api(value = "PmLog", tags = {"后端.短消息模块"})
public class PmController extends ConsoleBaseController {
    private final PmService pmService;
    private final PmLogService pmLogService;

    @Autowired
    public PmController(PmService pmService
            , PmLogService pmLogService) {
        this.pmLogService = pmLogService;
        this.pmService = pmService;
    }


    @GetMapping("log/list")
    @RequiresPermissions(InstallPermission.MONITOR_PM_LOG)
    @ApiOperation("短信日志")
    @WebLogger
    public Result logList(@RequestParam(value = "uid_list", required = false) @ApiParam("对应的用户ID列表，每个用半角逗号隔开") String uidList
            , @RequestParam(value = "pm_type", required = false) @ApiParam("短消息类型") PmType pmType
            , @RequestParam(value = "receive", required = false) @ApiParam("接受者") String receive
            , @RequestParam(value = "use", required = false) @ApiParam("用途") PmUse pmUse
            , @RequestParam(value = "add_time_start", required = false) @ApiParam("日志时间范围") Long addTimeStart
            , @RequestParam(value = "add_time_end", required = false) @ApiParam("日志时间范围") Long addTimeEnd
            , @RequestParam(value = "success", required = false) @ApiParam("是否发送成功") Boolean success
            , @RequestParam(value = "verify", required = false) @ApiParam("是否完成验证") Boolean verify
            , @RequestParam(value = "server", required = false) @ApiParam("锁使用的短信服务商") SmsServerType server
            , @RequestParam(value = "ip", required = false) @ApiParam("发送着IP") String ip
            , @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") Integer page
            , @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") Integer pageSize) {
        PageredData<PmLog> pager = pmLogService.pager(page, pageSize
                , pmLogService.condition(null, StringUtils.stringConvertList(uidList, Long.class)
                        , pmType, receive, pmUse, addTimeStart, addTimeEnd, success, verify, server, ip));
        pager.setDataList(pmLogService.setList(pager.getDataList(), true));
        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }


    @PostMapping("send/sms")
    @ApiOperation("发送验证短信")
    @WebLogger
    public Result sendSms(@RequestBody(required = false) Map<String, String> pmMap,
                          @RequestParam(value = "uid") @ApiParam("对应用户ID") Long userId,
                          @RequestParam(value = "app") @ApiParam("app") String app,
                          @RequestParam(value = "phone") @ApiParam("手机号") String phone,
                          @RequestParam(value = "use", defaultValue = "COMMON_VERIFY") @ApiParam("用途") PmUseType useType) {
        PmLog log = pmService.sendPm(app, userId, PmType.SMS, phone, useType, pmMap, _ip());
        if (log == null) {
            return Result.fail("发送失败");
        }
        return log.getSuccess() ? Result.success() : Result.fail(log.getErrMsg());
    }

    @GetMapping("verify")
    @ApiOperation("验证短消息")
    @WebLogger
    public Result verify(@RequestParam(value = "uid") @ApiParam("对应用户ID") String userId,
                         @RequestParam(value = "app") @ApiParam("app") String app,
                         @RequestParam(value = "receive") @ApiParam("接受者") String receive,
                         @RequestParam(value = "attach") @ApiParam("附加内容") String attach,
                         @RequestParam(value = "code") @ApiParam("验证内容") String code,
                         @RequestParam(value = "type", defaultValue = "SMS") @ApiParam("消息类型") PmType type,
                         @RequestParam(value = "use", defaultValue = "FORGET_PASSWORD") @ApiParam("验证消息用途") PmUse use) {

        if (PmUse.FORGET_PASSWORD.equals(use) && (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(attach))) {
            return Result.fail("验证数据有误");
        }

        boolean success = pmService.verifyPm(app, type, use, receive, code, 1800);
        if (!success) {
            return Result.fail("验证失败, 请检查");
        }

        if (PmUse.FORGET_PASSWORD.equals(use)) {
            User user = userService.fetch(userId);
            if (user == null && !receive.equals(user.getPhone())) {
                return Result.fail("用户不存在");
            }

            user.setUpdateTime(DateUtils.getTS());
            user.setPassword(SINOCredentialsMatcher.password(user.getName(), attach));
            return userService.update(user) > 0 ? Result.success() : Result.fail("密码重置失败");
        }

        return Result.success();
    }

}