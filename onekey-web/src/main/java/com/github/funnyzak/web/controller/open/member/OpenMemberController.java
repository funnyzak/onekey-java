package com.github.funnyzak.web.controller.open.member;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.github.funnyzak.web.controller.open.dto.WxMaUserInfoCheckDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.funnyzak.bean.member.MemberInfo;
import com.github.funnyzak.bean.open.TToken;
import com.github.funnyzak.biz.config.wx.minapp.WxMaConfiguration;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.dto.open.OpenRequestDTO;
import com.github.funnyzak.biz.exception.BizException;
import com.github.funnyzak.biz.service.CacheService;
import com.github.funnyzak.biz.service.member.MemberService;
import com.github.funnyzak.biz.service.open.TTokenService;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.web.annotation.auth.CurrentMember;
import com.github.funnyzak.web.annotation.auth.CurrentOpenRequest;
import com.github.funnyzak.web.annotation.auth.CurrentTToken;
import com.github.funnyzak.web.annotation.auth.OpenMemberAuth;
import com.github.funnyzak.web.annotation.weblog.WebLogger;
import com.github.funnyzak.web.controller.open.OpenBaseController;
import com.github.funnyzak.web.controller.open.OpenUtils;
import com.github.funnyzak.web.controller.open.dto.MemberPhoneRegisterDTO;
import com.github.funnyzak.web.controller.open.dto.ResetPwdDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/20 2:56 下午
 * @description OpenMemberController
 */
@RestController
@RequestMapping("open/member")
@Api(value = "OpenMember", tags = {"开放接口.会员"})
public class OpenMemberController extends OpenBaseController {
    private CacheService cacheService;
    private MemberService memberService;
    private TTokenService tokenService;

    @Autowired
    public OpenMemberController(
            CacheService cacheService
            , TTokenService tokenService
            , MemberService memberService
    ) {
        this.tokenService = tokenService;
        this.memberService = memberService;
        this.cacheService = cacheService;
    }

    /**
     * 小程序登陆接口
     */
    @PostMapping("wx/minapp/login")
    @OpenMemberAuth(mustLogin = false)
    @ApiOperation(value = "微信小程序登陆")
    @WebLogger()
    public Result wxMinAppLogin(
            @CurrentOpenRequest OpenRequestDTO openRequestDTO
            , @RequestParam("code") @ApiParam("小程序前端授权获取的js_code") String jsCode
    ) {
        try {
            checkNull(jsCode);
            WxMaService wxService = WxMaConfiguration.getMaService(openRequestDTO.getWeMaAppId());
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(jsCode);

            MemberInfo info = memberService.loginByWxMa(openRequestDTO.getAppId(), session.getOpenid(), openRequestDTO.getIp());
            TToken token = tokenService.fetchOrCreate(openRequestDTO.getSecretId(), openRequestDTO.getAppId(), info.getId(), true);
            cacheService.removeByMemberId(info.getId());
            cacheService.removeToken(token.getToken());

            return Result.success().addData(OpenUtils.Json.MEMBER_NAME, OpenUtils.Json.member(info)).addData(OpenUtils.Json.TOKEN_NAME, OpenUtils.Json.token(token));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("wx/minapp/user/info")
    @OpenMemberAuth()
    @ApiOperation(value = "小程序获取用户信息（用于微信授权获取用户信息）")
    @WebLogger()
    public Result wxMinAppUserInfo(
            @CurrentOpenRequest OpenRequestDTO openRequestDTO
            , @RequestBody WxMaUserInfoCheckDTO dto) {
        try {
            WxMaService wxService = WxMaConfiguration.getMaService(openRequestDTO.getWeMaAppId());

            // 用户信息校验
            if (!wxService.getUserService().checkUserInfo(dto.getSessionKey(), dto.getRawData(), dto.getSignature())) {
                throw new BizException("用户校验信息有误");
            }
            // 解密用户信息
            WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(dto.getSessionKey(), dto.getEncryptedData(), dto.getIv());
            return Result.success().addData(JsonConstants.INFO_NAME, userInfo);
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @GetMapping("profile")
    @ApiOperation(value = "我的会员信息")
    @OpenMemberAuth()
    @WebLogger()
    public Result profile(@CurrentMember MemberInfo memberInfo) {
        try {
            return Result.success().addData(OpenUtils.Json.MEMBER_NAME, OpenUtils.Json.member(memberInfo));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("forget/pwd")
    @ApiOperation("找回密码")
    @OpenMemberAuth(mustLogin = false)
    @WebLogger()
    public Result forgetPwd(
            @CurrentOpenRequest OpenRequestDTO openRequestDTO
            , @RequestBody MemberPhoneRegisterDTO dto) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, OpenUtils.Json.member(memberService.forget(openRequestDTO.getAppId(), dto.getReceive(), dto.getPwd(), dto.getVerifyCode())));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("phone/register")
    @ApiOperation("手机号会员注册")
    @OpenMemberAuth(mustLogin = false)
    @WebLogger()
    public Result phoneRegister(
            @CurrentOpenRequest OpenRequestDTO openRequestDTO
            , @RequestBody MemberPhoneRegisterDTO dto) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, OpenUtils.Json.member(memberService.registerByPmCode(openRequestDTO.getAppId(), dto.getReceive(), dto.getIdNum(), dto.getPwd(), dto.getVerifyCode(), _ip())));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("phone/login")
    @ApiOperation("手机号密码登陆")
    @OpenMemberAuth(mustLogin = false)
    @WebLogger()
    public Result phoneLogin(
            @CurrentOpenRequest OpenRequestDTO openRequestDTO
            , @RequestBody MemberPhoneRegisterDTO dto) {
        try {
            MemberInfo info = memberService.login(openRequestDTO.getAppId(), dto.getReceive(), dto.getPwd(), openRequestDTO.getIp());
            TToken token = tokenService.fetchOrCreate(openRequestDTO.getSecretId(), openRequestDTO.getAppId(), info.getId(), true);
            cacheService.removeByMemberId(info.getId());

            return Result.success().addData(OpenUtils.Json.MEMBER_NAME, OpenUtils.Json.member(info))
                    .addData(OpenUtils.Json.TOKEN_NAME, OpenUtils.Json.token(token));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("reset/pwd")
    @ApiOperation("重设密码")
    @OpenMemberAuth()
    @WebLogger()
    public Result resetPwd(@CurrentMember MemberInfo info
            , @RequestBody ResetPwdDTO dto) {
        try {
            memberService.resetPwd(info.getId(), dto.getOldPwd(), dto.getNewPwd());
            cacheService.removeByMemberId(info.getId());
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("bind/phone")
    @ApiOperation("绑定手机号")
    @OpenMemberAuth()
    @WebLogger()
    public Result bindPhone(
            @CurrentOpenRequest OpenRequestDTO openRequestDTO
            , @CurrentMember MemberInfo info
            , @RequestBody MemberPhoneRegisterDTO dto) {
        try {
            memberService.resetPhone(openRequestDTO.getAppId(), info.getId(), dto.getReceive(), dto.getVerifyCode());
            cacheService.removeByMemberId(info.getId());
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("reset/idNum")
    @ApiOperation("修改我的身份证")
    @OpenMemberAuth()
    @WebLogger()
    public Result editIdNum(@CurrentOpenRequest OpenRequestDTO openRequestDTO
            , @CurrentMember MemberInfo info
            , @RequestParam(value = "idNum") @ApiParam("身份证号") String idNum) {
        try {
            memberService.resetIdNum(openRequestDTO.getAppId(), info.getId(), idNum);
            cacheService.removeByMemberId(info.getId());
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("logout")
    @ApiOperation("退出")
    @OpenMemberAuth()
    @WebLogger()
    public Result logout(@CurrentTToken TToken info) {
        try {
            cacheService.removeToken(info.getToken());
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("profile")
    @ApiOperation("修改我的信息")
    @OpenMemberAuth()
    @WebLogger()
    public Result editProfile(@CurrentMember MemberInfo info
            , @RequestBody MemberInfo postInfo) {
        try {
            info.setNickName(postInfo.getNickName());
            info.setBirthDay(postInfo.getBirthDay());
            info.setRealName(postInfo.getRealName());
            info.setSignature(postInfo.getSignature());
            info.setAvatar(postInfo.getAvatar());
            info.setGender(postInfo.getGender());
            MemberInfo newInfo = memberService.edit(info, null);
            cacheService.removeByMemberId(info.getId());
            return Result.success().addData(JsonConstants.INFO_NAME, OpenUtils.Json.member(newInfo));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }
}