package com.github.funnyzak.web.controller.wx.mp;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import com.github.funnyzak.biz.config.wx.mp.WxMpConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Edward
 */
@Controller
@RequestMapping("/wx/redirect/{appid}")
public class WxRedirectController {

    @RequestMapping("/greet")
    public String greetUser(@PathVariable String appid, @RequestParam String code, ModelMap map) throws Exception {

        WxMpService mpService = WxMpConfiguration.getMpServices().get(appid);

        try {
            WxMpOAuth2AccessToken accessToken = mpService.getOAuth2Service().getAccessToken(code);
            WxMpUser user = mpService.getUserService().userInfo(accessToken.getAccessToken());
            map.put("user", user);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        return "greet_user";
    }
}
