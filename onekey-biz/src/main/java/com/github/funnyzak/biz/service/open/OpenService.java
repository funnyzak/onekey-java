package com.github.funnyzak.biz.service.open;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.bean.open.Connector;
import com.github.funnyzak.bean.open.enums.ConnectorPermission;
import com.github.funnyzak.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/16 5:54 下午
 * @description OpenService
 */
@Service
public class OpenService {
    private final NutMap authCodeMap = new NutMap();
    private final ConnectorLogService logService;

    /**
     * 是否开启连接器验证
     */
    @Value("${biz-system.open.enable:true}")
    private Boolean OPEN_SERVICE_ENABLE = true;

    /**
     * 时间戳过期时间
     */
    @Value("${biz-system.open.timestamp-expired-time:30000}")
    private Integer TIMESTAMP_EXPIRED_TIME = 30000;

    @Autowired
    public OpenService(ConnectorLogService logService) {
        this.logService = logService;

        authCodeMap.addv("10000", "成功");
        authCodeMap.addv("10001", "连接器不存在");
        authCodeMap.addv("10002", "权限有误");
        authCodeMap.addv("10003", "请求过期");
        authCodeMap.addv("10004", "非法IP");
        authCodeMap.addv("10005", "签名有误");
        authCodeMap.addv("10006", "请求过快");
    }

    public String codeMsg(int code) {
        return authCodeMap.containsKey(Integer.toString(code)) ? authCodeMap.getString(Integer.toString(code)) : "未知";
    }

    /**
     * 验证请求合法性
     *
     * @param connector 根据appkey获取的连接器
     * @param sign      获取的app token
     * @param url       绝对路径URL，如：/abc/ok
     * @param ip        客户端IP
     * @param requestTs 请求携带的时间戳
     * @return 10000 正常 10001 连接器不存在   10002 权限有误  10003 请求过期  10004非法IP  10005令牌有误  10006请求过快
     */
    public int auth(Connector connector, String sign, String url, String ip, Long requestTs, ConnectorPermission[] permissions, Logical logical) {
        if (!OPEN_SERVICE_ENABLE) {
            return 10000;
        }
        if (connector == null || !connector.getEnable()) {
            return 10001;
        }
        if (permissions != null && permissions.length > 0 && !ArrayUtils.contains(permissions, ConnectorPermission.NONE)) {
            if (connector.getPermissionList() == null || connector.getPermissionList().size() == 0) {
                return 10002;
            } else if (logical == null || logical.equals(Logical.OR)) {
                for (ConnectorPermission permission : permissions) {
                    if (connector.getPermissionList().contains(permission)) {
                        break;
                    }
                }
            } else if (logical.equals(Logical.AND)) {
                for (ConnectorPermission permission : permissions) {
                    if (!connector.getPermissionList().contains(permission)) {
                        return 10002;
                    }
                }
            }
        }
        if (connector.getVerifyTS() && (System.currentTimeMillis() > (requestTs + TIMESTAMP_EXPIRED_TIME))) {
            return 10003;
        }
        if (connector.getVerifyTS() && (System.currentTimeMillis() < (requestTs - TIMESTAMP_EXPIRED_TIME))) {
            return 10003;
        }
        if (connector.whiteIpList() != null && !StringUtils.isNullOrEmpty(ip) && connector.whiteIpList().contains(ip)) {
            return 10004;
        }
        if (connector.getVerifyAppSign() && (StringUtils.isNullOrEmpty(sign) || requestTs == null || requestTs <= 0)) {
            return 10005;
        }
        if (connector.getVerifyAppSign() && !sign.toLowerCase().equals(Lang.md5(url + requestTs.toString() + connector.getSecretKey()).toLowerCase())) {
            return 10005;
        }
        if (connector.getLimitApiCountMinute() > 0
                && logService.count(Cnd.where("id", ">", 0)
                .andEX("requestTime", ">", System.currentTimeMillis() - 60000)) > connector.getLimitApiCountMinute()) {
            return 10006;
        }
        return 10000;
    }
}