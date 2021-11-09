package com.github.funnyzak.biz.dto.open;

import lombok.Getter;
import lombok.Setter;
import org.nutz.lang.Lang;
import com.github.funnyzak.biz.constant.BizConstants;
import com.github.funnyzak.common.utils.TypeParse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/20 9:12 上午
 * @description 开放Web请求Header数据获取
 */
public class OpenRequestDTO {

    public static OpenRequestDTO getInstance(){
        return new OpenRequestDTO();
    }

    public OpenRequestDTO() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        this.request = sra.getRequest();

        this.reset();
    }

    public OpenRequestDTO(HttpServletRequest request) {
        this.request = request;
        this.reset();
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
        this.reset();
    }

    private void reset() {
        if (this.request == null) return;

        this.authToken = this.request.getHeader(BizConstants.OpenConst.RequestHeader.AUTH_TOKEN_HEADER_NAME);
        this.secretId = this.request.getHeader(BizConstants.OpenConst.RequestHeader.SECRET_ID_HEADER_NAME);
        this.appId = this.request.getHeader(BizConstants.OpenConst.RequestHeader.APP_ID_HEADER_NAME);
        this.appSign = this.request.getHeader(BizConstants.OpenConst.RequestHeader.APP_SIGN_HEADER_NAME);
        this.ts = TypeParse.parseLong(this.request.getHeader(BizConstants.OpenConst.RequestHeader.APP_TS_HEADER_NAME));
        this.weMpAppId = this.request.getHeader(BizConstants.OpenConst.RequestHeader.WE_CHAT_MP_APP_ID_HEADER_NAME);
        this.weMaAppId = this.request.getHeader(BizConstants.OpenConst.RequestHeader.WE_CHAT_MA_APP_ID_HEADER_NAME);
        this.ip = Lang.getIP(this.request);
        this.url = request.getRequestURI();
    }

    /**
     * Web Request请求
     */
    @Setter
    @Getter
    private HttpServletRequest request;

    /**
     * 前端Request令牌
     */
    @Getter
    private String authToken;

    /**
     * 连接器ID
     */
    @Getter
    private String secretId;

    /**
     * 和连接器设置的APP ID保持一致
     */
    @Getter
    private String appId;

    /**
     * 签名
     */
    @Getter
    private String appSign;

    /**
     * 时间戳
     */
    @Getter
    private Long ts;

    /**
     * 微信小程序appId
     */
    @Getter
    private String weMaAppId;

    /**
     * 微信公众号AppId
     */
    @Getter
    private String weMpAppId;

    /**
     * 来源IP
     */
    @Getter
    private String ip;

    /**
     * url
     */
    @Getter
    private String url;
}