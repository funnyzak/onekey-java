package com.github.funnyzak.biz.service.log;

import com.github.funnyzak.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.biz.service.CloudSmsService;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.log.PmLog;
import com.github.funnyzak.bean.log.enums.PmType;
import com.github.funnyzak.bean.log.enums.PmUse;
import com.github.funnyzak.bean.log.enums.SmsServerType;
import com.github.funnyzak.biz.service.acl.UserService;
import com.github.funnyzak.common.utils.DateUtils;
import com.github.funnyzak.common.utils.StringUtils;
import com.github.funnyzak.common.utils.TypeParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/8 2:25 PM
 * @description 短消息日志服务
 */
@Service
public class PmService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CloudSmsService jsSmsService;
    private final CloudSmsService tenSmsService;
    private final CloudSmsService aliSmsService;
    private final PmLogService pmLogService;
    private final UserService userService;

    @Autowired
    public PmService(
            @Qualifier("JsSuSmsService") CloudSmsService jsSmsService
            , @Qualifier("TenSmsService") CloudSmsService tenSmsService
            , @Qualifier("AliSmsService") CloudSmsService aliSmsService
            , UserService userService
            , PmLogService pmLogService) {
        this.userService = userService;
        this.jsSmsService = jsSmsService;
        this.aliSmsService = aliSmsService;
        this.tenSmsService = tenSmsService;
        this.pmLogService = pmLogService;
    }

    /**
     * 同一消息重发间隔
     */
    @Value("${collection-system.defend.same-pm-resend-interval:60}")
    private Integer SAME_PM_RESEND_INTERVAL = 60;

    /**
     * 同一接受者一天最大发送短信量
     */
    @Value("${collection-system.defend.send-sms-limit-count-same-receive-one-day:60}")
    private Integer SEND_SMS_LIMIT_COUNT_SAME_RECEIVE_ONE_DAY = 60;

    /**
     * 同一IP一天最大发送数量
     */
    @Value("${collection-system.defend.send-sms-limit-count-same-ip-one-day:60}")
    private Integer SEND_SMS_LIMIT_COUNT_SAME_IP_ONE_DAY = 60;

    /**
     * 获取接受对象一段时间内发送数量
     *
     * @param receive 接收对象地址 可以是邮件或手机号
     * @param second  检查多长时间的接收量
     */
    public int sendCountInSecond(String app, PmType type, String receive, int second) {
        return pmLogService.count(Cnd.where("id", ">", 0)
                .andEX("app", "=", app)
                .andEX("type", "=", type)
                .andEX("receive", "=", receive)
                .andEX("success", "=", 1)
                .andEX("addTime", ">", DateUtils.getTS() - second));
    }

    /**
     * 同一短消息类型检查是否可以重发
     */
    public boolean canResend(String app, PmType type, PmUse use, String receive) {
        return pmLogService.count(Cnd.where("id", ">", 0)
                .andEX("app", "=", app)
                .andEX("receive", "=", receive)
                .andEX("success", "=", 1)
                .andEX("use", "=", use)
                .andEX("type", "=", type)
                .andEX("addTime", ">",
                        DateUtils.getTS() - SAME_PM_RESEND_INTERVAL)) <= 0;
    }

    /**
     * 同一接受者一天内是否可以发送短信
     */
    public boolean sameReceiveCanSendSmsInOneDay(String app, PmType type, String receive) {
        return sendCountInSecond(app, type, receive, 86400) < SEND_SMS_LIMIT_COUNT_SAME_RECEIVE_ONE_DAY;
    }

    /**
     * 同一IP一天内是否可以发送短信
     */
    public boolean sameIpCanSendSmsInOneDay(String app, PmType type, String ip) {
        return pmLogService.count(Cnd.where("id", ">", 0)
                .andEX("app", "=", app)
                .andEX("ip", "=", ip)
                .andEX("type", "=", type)
                .andEX("success", "=", 1)
                .andEX("addTime", ">",
                        DateUtils.getTS() - 86400)) < SEND_SMS_LIMIT_COUNT_SAME_IP_ONE_DAY;
    }

    /**
     * 验证短消息
     */
    public boolean verifyPm(String app, PmType type, PmUse use, String receive, String code, int second) {
        List<PmLog> logs = pmLogService.query(Cnd.where("id", ">", 0)
                .andEX("app", "=", app)
                .andEX("type", "=", type)
                .andEX("use", "=", use)
                .andEX("receive", "=", receive)
                .andEX("code", "=", code)
                .andEX("success", "=", 1)
                .andEX("verify", "=", 0)
                .andEX("addTime", ">", DateUtils.getTS() - second)
                .orderBy("addTime", "desc")
        );
        if (logs == null || logs.size() == 0) {
            return false;
        }

        PmLog log = logs.get(0);
        if (!log.getCode().equals(code)) {
            return false;
        }

        log.setVerify(true);
        log.setUpdateTime(DateUtils.getTS());
        return pmLogService.update(log, "verify", "updateTime");
    }

    /**
     * 发送短消息
     */
    public PmLog sendPm(String app,
                        Long uid,
                        PmType type,
                        String receive,
                        PmUseType useType,
                        Map<String, String> pmMap,
                        String ip) {

        if (StringUtils.isNullOrEmpty(receive)) {
            return null;
        }

        try {
            String errMsg = "";
            if (!sameIpCanSendSmsInOneDay(app, type, ip) || !sameReceiveCanSendSmsInOneDay(app, type, receive)) {
                errMsg = "发送太频繁";
            } else if (StringUtils.isNullOrEmpty(errMsg) && !canResend(app, type, useType.getBelong(), receive)) {
                errMsg = String.format("请过 %s 秒重试", SAME_PM_RESEND_INTERVAL);
            } else if (useType.getBelong().equals(PmUse.FORGET_PASSWORD)) {
                User user = userService.fetch(Cnd.where("id", ">", 0).andEX("phone", "=", receive).andEX("name", "=", uid));
                if (user == null) {
                    errMsg = "用户不存在";
                }
            }

            PmLog log = new PmLog();
            log.setApp(app);
            log.setUserId(TypeParse.parseString(uid));
            log.setType(type);
            log.setReceive(receive);
            log.setUse(useType.getBelong());
            log.setIp(ip);

            if (!StringUtils.isNullOrEmpty(errMsg)) {
                log.setErrMsg(errMsg);
                return pmLogService.save(log);
            }

            if (type.equals(PmType.SMS)) {
                log = sendSms(log, pmMap, useType);
            } else if (PmType.EMAIL.equals(type)) {
                log = sendEmail(log);
            }
            return log;

        } catch (Exception ex) {
            logger.error("短信发送失败，错误信息：", ex);
            return null;
        }
    }

    /**
     * 发送短信
     */
    public PmLog sendSms(PmLog log, Map<String, String> pmMap, PmUseType useType) {
        if (PmUse.FORGET_PASSWORD.equals(log.getUse()) ||
                PmUse.REGISTER.equals(log.getUse()) ||
                PmUse.RESET_PHONE.equals(log.getUse()) ||
                PmUse.VERIFY_ACTION.equals(log.getUse()) ||
                PmUse.LOGIN.equals(log.getUse())
        ) {
            if (pmMap == null) {
                String code = StringUtils.getRandomDigital(4);
                log.setCode(code);

                pmMap = new LinkedHashMap<>();
                pmMap.put("code", code);
                pmMap.put("minute", "30");
            }
        }

        log = sendSms(log, pmMap, useType, SmsServerType.ALIYUN);
        if (!log.getSuccess()) {
            log = sendSms(log, pmMap, useType, SmsServerType.TENCENT);
        }
        if (!log.getSuccess()) {
            log = sendSms(log, pmMap, useType, SmsServerType.JISU);
        }

        return pmLogService.save(log);
    }

    public PmLog sendSms(PmLog log, Map<String, String> pmMap, PmUseType useType, SmsServerType sendByServer) {
        try {
            NutMap rltMap = (sendByServer == SmsServerType.JISU ? jsSmsService : sendByServer == SmsServerType.TENCENT ? tenSmsService : aliSmsService).sendSms(log.getReceive(), useType, pmMap);
            log.setParamData(pmMap != null ? Json.toJson(pmMap, JsonFormat.compact()) : null);
            log.setServer(sendByServer);
            log.setContent(rltMap.getString("msgContent"));
            log.setSuccess(true);
        } catch (Exception ex) {
            logger.error(sendByServer.toString() + "短信发送失败，错误信息：", ex);
        }
        return log;
    }

    /**
     * 发送邮件
     */
    public PmLog sendEmail(PmLog log) {
        try {

        } catch (Exception ex) {
            logger.error("邮件发送失败，错误信息:", ex);
            log.setErrMsg(ex.getMessage());
        }
        return pmLogService.save(log);
    }
}