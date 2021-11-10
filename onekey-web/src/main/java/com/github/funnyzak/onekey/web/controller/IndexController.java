package com.github.funnyzak.onekey.web.controller;

import club.zhcs.captcha.ImageVerification;
import com.github.funnyzak.onekey.web.WebApplication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import com.github.funnyzak.onekey.biz.config.aliyun.sms.AliSmsService;
import com.github.funnyzak.onekey.common.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

@Controller
@Api(value = "Index", tags = {"通用"})
public class IndexController {
    Log logger = Logs.get();

    @Autowired
    AliSmsService aliSmsService;

    @GetMapping("/")
    public String Home() {
        return RandomUtils.itemRandom(Arrays.asList("index", "index2"));
    }

    @GetMapping("/test/sms")
    public String testSms() throws Exception {
        aliSmsService.sendVerifySms("13611036038", "1232", 5);
        return RandomUtils.itemRandom(Arrays.asList("index", "index2"));
    }

    @GetMapping("/captcha")
    @ApiOperation("验证码")
    public void captcha(@RequestParam(value = "length", required = false, defaultValue = "4") @ApiParam(value = "验证码长度", required = false, defaultValue = "4") int length,
                        @ApiIgnore HttpServletResponse resp, @ApiIgnore HttpSession session) throws IOException {
        resp.setContentType("image/jpeg");
        resp.setHeader("Pragma", "No-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        OutputStream out = resp.getOutputStream();
        // 输出图象到页面
        ImageVerification iv = new ImageVerification();

        if (length != 0) {
            iv.setIMAGE_VERIFICATION_LENGTH(length);
        }
        if (ImageIO.write(iv.creatImage(), "JPEG", out)) {
            logger.debug("写入输出流成功:" + iv.getVerifyCode() + ".");
        } else {
            logger.debug("写入输出流失败:" + iv.getVerifyCode() + ".");
        }
        session.setAttribute(WebApplication.CAPTCHA_KEY, iv.getVerifyCode());
        // 以下关闭输入流！
        out.flush();
        out.close();
    }

}
