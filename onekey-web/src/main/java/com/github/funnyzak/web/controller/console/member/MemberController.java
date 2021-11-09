package com.github.funnyzak.web.controller.console.member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.enums.Gender;
import com.github.funnyzak.bean.member.MemberInfo;
import com.github.funnyzak.bean.open.TToken;
import com.github.funnyzak.bean.vo.InstallPermission;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.biz.service.member.MemberService;
import com.github.funnyzak.biz.service.open.TTokenService;
import com.github.funnyzak.biz.service.resource.ResourceService;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.web.annotation.weblog.WebLogger;
import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/19 3:21 下午
 * @description MemberController
 */
@RestController
@RequestMapping("console/open/member")
@Api(value = "Member", tags = {"服务端.开放会员"})
public class MemberController extends ConsoleBaseController {
    private final MemberService memberService;
    private final TTokenService tokenService;
    private final ResourceService resourceService;

    @Autowired
    public MemberController(MemberService memberService
            , TTokenService memberTokenService
            , ResourceService resourceService) {
        this.memberService = memberService;
        this.resourceService = resourceService;
        this.tokenService = memberTokenService;
    }

    @GetMapping("list")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.MEMBER_INFO_LIST})
    @ApiOperation("会员列表")
    @WebLogger
    public Result list(
            @RequestParam(value = "registerIp", required = false) @ApiParam("注册IP") String registerIp
            , @RequestParam(value = "appId", required = false) @ApiParam("AppId") String appId
            , @RequestParam(value = "realName", required = false) @ApiParam("真实姓名") String realName
            , @RequestParam(value = "gender", required = false) @ApiParam("性别") Gender gender
            , @RequestParam(value = "username", required = false) @ApiParam("用户名") String username
            , @RequestParam(value = "email", required = false) @ApiParam("邮件地址") String email
            , @RequestParam(value = "idNum", required = false) @ApiParam("身份证") String idNum
            , @RequestParam(value = "orderBy", required = false) @ApiParam("排序字段") String orderBy
            , @RequestParam(value = "phone", required = false) @ApiParam("手机号") String phone
            , @RequestParam(value = "orderDesc", required = false) @ApiParam("是否降序") Boolean orderDesc
            , @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") Integer page
            , @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") Integer pageSize) {

        Cnd cnd = memberService.condition(null, currentUser(), appId, phone, email, realName, idNum, username, gender, registerIp);
        PageredData<MemberInfo> pager = memberService.pager(page, pageSize, cnd, orderBy, orderDesc);
        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }


    @PutMapping("")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.MEMBER_INFO_ADD})
    @ApiOperation("添加会员信息")
    public Result save(@RequestBody MemberInfo info) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, memberService.add(info, currentUser(), _ip()));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @GetMapping("{id}")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.MEMBER_INFO_DETAIL})
    @ApiOperation("会员信息详情")
    public Result detail(@PathVariable("id") @ApiParam("会员信息id") Long id) throws Exception {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, checkNull(memberService.fullInfo(memberService.fetch(id))));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @DeleteMapping("{id}")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.MEMBER_INFO_DELETE})
    @ApiOperation("删除会员信息")
    public Result delete(@PathVariable("id") @ApiParam("会员信息id") Long id) {
        try {
            memberService.remove(currentUser(), id);
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.MEMBER_INFO_EDIT})
    @ApiOperation("更新会员信息")
    public Result update(@RequestBody MemberInfo info) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, memberService.edit(info, currentUser()));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("lock/{id}")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.MEMBER_INFO_EDIT})
    @ApiOperation("锁定会员")
    public Result lock(@PathVariable("id") @ApiParam("会员信息id") Long id
            , @RequestParam(value = "lock") @ApiParam("是否锁定") Boolean lock) {
        try {
            return Result.success().addData(JsonConstants.INFO_NAME, memberService.lock(currentUser(), id, lock));
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("logout/{app}/{id}")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.MEMBER_INFO_EDIT})
    @ApiOperation("强制退出")
    public Result logout(@PathVariable("app") @ApiParam("App") String app, @PathVariable("id") @ApiParam("会员信息id") Long id) {
        try {
            TToken token = tokenService.token(null, app, id);
            if (token != null) tokenService.remove(token.getToken());
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }

    @PostMapping("reset/pwd/{id}")
    @RequiresPermissions(logical = Logical.OR, value = {InstallPermission.MEMBER_INFO_RESET_PWD})
    @ApiOperation("重置会员密码")
    public Result resetPwd(@PathVariable("id") @ApiParam("会员信息id") Long id
            , @RequestParam(value = "pwd") @ApiParam("pwd") String pwd) {
        try {
            memberService.resetPwd(currentUser(), id, pwd);
            return Result.success();
        } catch (Exception ex) {
            return failResultByException(ex);
        }
    }
}