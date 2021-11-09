package com.github.funnyzak.biz.config.wx.mp.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.MenuButtonType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MenuHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        String msg = String.format("type:%s, event:%s, key:%s",
                wxMessage.getMsgType(), wxMessage.getEvent(),
                wxMessage.getEventKey());
        if (MenuButtonType.VIEW.equals(wxMessage.getEvent())) {
            return null;
        } else if (MenuButtonType.CLICK.equals(wxMessage.getEvent().toLowerCase())) {
            WxMpKefuMessage _msg = null;
            if (wxMessage.getEventKey() != null && wxMessage.getEventKey().indexOf("autoreplay_") == 0) {

            } else if (wxMessage.getEventKey() != null && wxMessage.getEventKey().indexOf("poster_tjhb") == 0) {

            }

            if (msg != null) {
                return null;
            }

            return WxMpXmlOutMessage.TEXT().content("程序猿小哥正在开发中..")
                    .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                    .build();
        }

        return WxMpXmlOutMessage.TEXT().content(msg)
                .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                .build();
    }

}
