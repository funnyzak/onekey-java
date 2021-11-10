package com.github.funnyzak.onekey.web.controller.console.acl;

import com.github.funnyzak.onekey.web.WebApplication;
import com.github.funnyzak.onekey.web.controller.console.base.ConsoleBaseController;
import com.github.funnyzak.onekey.web.dto.GrantDTO;
import com.github.funnyzak.onekey.web.dto.UserLoginDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.onekey.bean.acl.DataRuleRelationMasterType;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.enums.UserStatus;
import com.github.funnyzak.onekey.bean.vo.InstallPermission;
import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.onekey.biz.ext.shiro.matcher.SINOCredentialsMatcher;
import com.github.funnyzak.onekey.biz.service.acl.DataRuleRelationService;
import com.github.funnyzak.onekey.biz.service.acl.PermissionService;
import com.github.funnyzak.onekey.biz.service.acl.ShiroUserService;
import com.github.funnyzak.onekey.biz.service.department.UserDepartmentService;
import com.github.funnyzak.onekey.common.Result;
import com.github.funnyzak.onekey.common.codec.DES;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * UserController class
 *
 * @author potato
 * @date 2019/07/25
 */
@RestController
@RequestMapping("console/user")
@Api(value = "User", tags = {"后端.访问控制"})
public class UserController extends ConsoleBaseController {

    private final ShiroUserService shiroUserService;
    private final PermissionService permissionService;
    private final UserDepartmentService userDepartmentService;
    private final DataRuleRelationService dataRuleRelationService;

    @Autowired
    public UserController(ShiroUserService shiroUserService, PermissionService permissionService, UserDepartmentService userDepartmentService, DataRuleRelationService dataRuleRelationService) {
        this.shiroUserService = shiroUserService;
        this.permissionService = permissionService;
        this.userDepartmentService = userDepartmentService;
        this.dataRuleRelationService = dataRuleRelationService;

        this.currentControllerName = "用户管理";
    }


    /**
     * 获取用户列表
     *
     * @param page     页码
     * @param pageSize 页大小
     * @param key      搜索关键字
     * @return 分页数据
     */
    @GetMapping("list")
    @RequiresPermissions(InstallPermission.USER_LIST)
    @ApiOperation(value = "用户列表")
    @WebLogger(name = "用户列表")
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") Integer page,
                       @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") Integer pageSize,
                       @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key) throws Exception {
        return Result.success().addData(JsonConstants.PAGER_NAME, userService.userPager(page, pageSize, userService.condition(null, currentUser(), key, key, key)));
    }


    /**
     * 启用用户
     *
     * @param id 用户ID
     * @return 处理结果
     */
    @GetMapping("active/{id}")
    @RequiresPermissions(InstallPermission.USER_ACTIVE)
    @ApiOperation(value = "启用用户")
    @WebLogger(name = "启用用户")
    public Result active(@PathVariable("id") @ApiParam("待启用用户id") long id) {
        User user = userService.findByField("id", id);

        user.setStatus(UserStatus.ACTIVE);
        user.setUpdateTime(DateUtils.getTS());

        _addOperationLog("用户启用", user.toString());
        return userService.update(user) > 0 ? Result.success() : Result.fail("启用用户失败");
    }

    /**
     * 禁用用户
     *
     * @param id 用户ID
     * @return 处理结果
     */
    @GetMapping("disabled/{id}")
    @RequiresPermissions(InstallPermission.USER_DISABLED)
    @ApiOperation(value = "禁用用户")
    @WebLogger(name = "禁用用户")
    public Result disabled(@PathVariable("id") @ApiParam("待禁用用户id") long id) {
        User user = userService.findByField("id", id);

        user.setStatus(UserStatus.DISABLED);
        user.setUpdateTime(DateUtils.getTS());

        _addOperationLog("用户禁用", user.toString());

        return userService.update(user) > 0 ? Result.success() : Result.fail("禁用用户失败");

    }


    /**
     * 添加用户
     *
     * @param user 用户实体
     * @return 添加后的用户信息实体
     */
    @PutMapping(value = "add")
    @RequiresPermissions(InstallPermission.USER_ADD)
    @ApiOperation(value = "新增用户")
    @WebLogger(name = "新增用户")
    public Result add(@RequestBody User user) {
        Result result = validateFormFields(user);
        if (!result.isSuccess()) {
            return result;
        }

        if (userService.fetch(user.getName()) != null) {
            return Result.fail("该用户已存在");
        }

        user.setPassword(SINOCredentialsMatcher.password(user.getName(), user.getPassword()));

        User addedUser = userService.save(user);

        if (addedUser != null) {
            _addOperationLog("用户添加", user.toString());
        }
        return addedUser == null ? Result.fail("保存用户失败!") : Result.success().addData(JsonConstants.AclJson.USER_INFO_NAME, addedUser);
    }

    /**
     * 编辑用户信息
     *
     * @param user 用户Bean
     * @return 用户实体
     */
    @PostMapping(value = "edit")
    @RequiresPermissions(InstallPermission.USER_EDIT)
    @ApiOperation(value = "修改用户基本信息", notes = "仅修改姓名,电话,邮箱和状态信息")
    @WebLogger(name = "修改用户基本信息")
    public Result edit(@RequestBody User user) {
        User realUser = userService.findByField("id", user.getId());

        if (StringUtils.isNullOrEmpty(user.getEmail())
                || StringUtils.isNullOrEmpty(user.getPhone())
        ) {
            return Result.fail("数据填写不完整");
        }

        user.setUpdateTime(DateUtils.getTS());
        // 如果密码未设置则保持旧密码，否则加密
        user.setPassword(
                StringUtils.isNullOrEmpty(user.getPassword())
                        ? realUser.getPassword()
                        : SINOCredentialsMatcher.password(user.getName(), user.getPassword())
        );

        boolean opRlt = userService.update(user, "realName", "phone", "email", "status", "headKey", "nickName", "password");
        if (opRlt) {
            _addOperationLog("用户编辑", user.toString());
        }

        return opRlt ? Result.success() : Result.fail("更新用户失败!");
    }


    /**
     * 编辑我的信息
     *
     * @param user 用户实体
     * @return 处理结果
     */
    @RequiresAuthentication
    @PostMapping(value = "settings/profile")
    @ApiOperation(value = "修改我的信息", notes = "修改姓名,电话,密码,邮箱和状态信息")
    @WebLogger(name = "修改我的信息")
    public Result editProfile(@RequestBody User user) {

        User realUser = currentUser();
        user.setName(realUser.getName());
        user.setId(realUser.getId());
        user.setUpdateTime(DateUtils.getTS());

        String editFields = "realName,phone,email,headKey,nickName";
        if (!user.getPassword().isEmpty()) {
            editFields += ",password";

            // 密码密文转换
            user.setPassword(SINOCredentialsMatcher.password(user.getName(), user.getPassword()));
        }

        boolean opRlt = userService.update(user, editFields.split(","));
        if (opRlt) {
            _addOperationLog("编辑用户", user.toString());
        }
        return opRlt ? Result.success() : Result.fail("更新用户失败!");
    }


    /**
     * 重设密码
     *
     * @param user 用户实体
     * @return 处理结果
     */
    @PostMapping(value = "settings/password")
    @RequiresPermissions(InstallPermission.USER_SETTING_PASSWORD)
    @ApiOperation(value = "重置用户密码")
    @WebLogger(name = "重置用户密码")
    public Result resetPassword(@RequestBody User user) {
        User realUser = userService.fetch(user.getId());

        user.setUpdateTime(DateUtils.getTS());
        user.setPassword(SINOCredentialsMatcher.password(realUser.getName(), user.getPassword()));
        boolean opRlt = userService.updateFields(user, "password") == 1;

        if (opRlt) {
            _addOperationLog("重置密码", user.toString());
        }
        return !opRlt ? Result.fail("保存用户失败!") : Result.success().addData(JsonConstants.AclJson.USER_INFO_NAME, user);
    }


    @DeleteMapping("{id}")
    @RequiresPermissions(InstallPermission.USER_DELETE)
    @ApiOperation(value = "删除用户")
    @WebLogger(name = "删除用户")
    public Result remove(@PathVariable("id") @ApiParam("待删除用户id") long id) {
        User user = userService.fetch(id);

        if (user.getName().equals(currentUser().getName())) {
            return Result.fail("不能删除自己哦");
        }

        if ("admin".equals(user.getName()) || user.getId() == 1) {
            return Result.fail("不能删除超级用户");
        }

        boolean opRlt = userService.delete(id) == 1;

        if (opRlt) {
            _addOperationLog("删除用户", user.toString());
        }
        return opRlt ? Result.success() : Result.fail("删除用户失败!");
    }


    @GetMapping("{id}")
    @RequiresPermissions(InstallPermission.USER_DETAIL)
    @ApiOperation(value = "用户详情")
    @WebLogger(name = "用户详情")
    public Result detail(@PathVariable("id") @ApiParam("用户id") long id) {
        return Result.success().addData(JsonConstants.AclJson.USER_INFO_NAME, userService.fetch(id));
    }


    /**
     * 获取用户的所有权限的信息列表（包含所属角色和直接权限的总合集）
     *
     * @param id 用户ID
     * @return JSON
     */
    @GetMapping("permission/{id}")
    @RequiresPermissions(InstallPermission.USER_GRANT)
    @ApiOperation("获取用户的所有权限的信息列表（包含所属角色和直接权限的总合集）")
    @WebLogger()
    public Result userPermissions(@PathVariable("id") @ApiParam("用户ID") long id) {
        return Result.success().addData(JsonConstants.AclJson.PERMISSION_LIST_NAME, permissionService.getAllPermissionsByUserId(id));
    }


    @GetMapping("permission/direct/{id}")
    @RequiresPermissions(InstallPermission.USER_GRANT)
    @ApiOperation("获取用户的直接权限合集")
    @WebLogger()
    public Result userDirectPermissions(@PathVariable("id") @ApiParam("用户ID") long id) {
        return Result.success().addData(JsonConstants.AclJson.PERMISSION_LIST_NAME, permissionService.listDirectPermissionsByUserId(id));
    }


    @GetMapping("permissions")
    @RequiresAuthentication
    @ApiOperation("获取我的所有权限列表")
    @WebLogger()
    public Result permissions() {
        List<String> myPermissions = shiroUserService.permissionInfos(_loginUserName());
        return Result.success().addData(JsonConstants.AclJson.PERMISSION_LIST_NAME, permissionService.searchByPage(1, 500, Cnd.where("name", "in", myPermissions).asc("description")).getDataList());
    }


    /**
     * 获取用户的角色信息
     *
     * @param id 用户id
     * @return 获取用户所属的角色信息列表
     */
    @GetMapping("role/{id}")
    @RequiresPermissions(InstallPermission.USER_ROLE)
    @ApiOperation(value = "用户角色授权信息")
    @WebLogger()
    public Result roleInfo(@PathVariable("id") @ApiParam("用户id") int id) {
        return Result.success().addData(JsonConstants.AclJson.ROLE_LIST_NAME
                , userService.findRolesWithUserPowerdInfoByUserId(id));
    }


    /**
     * 为用户设置角色
     *
     * @param dto 用户ID和角色ID
     * @return 处理结果
     */
    @PostMapping("/grant/role")
    @RequiresPermissions(InstallPermission.USER_ROLE)
    @ApiOperation("为用户设置角色")
    @WebLogger()
    public Result grantRole(@RequestBody GrantDTO dto) {
        User realUser = userService.fetch(dto.getId());

        if (dto.getId() == currentUser().getId()) {
            return Result.fail("不能给自己设置角色哦");
        }

        _addOperationLog("设置用户角色", String.format("角色IDs：%s", _IdListString(dto.getGrantIds())));
        return userService.setRole(dto.getGrantIds(), dto.getId());
    }


    /**
     * 为用户设置权限
     *
     * @param dto 用户ID和权限ID列表
     * @return 返回处理结果
     */
    @PostMapping("/grant/permission")
    @RequiresPermissions(InstallPermission.USER_GRANT)
    @ApiOperation("为用户设置权限")
    @WebLogger()
    public Result grantPermission(@RequestBody GrantDTO dto) {
        User realUser = userService.fetch(dto.getId());

        if (dto.getId() == currentUser().getId()) {
            return Result.fail("不能给自己设置权限哦");
        }

        _addOperationLog("设置用户权限", String.format("权限IDs：%s", _IdListString(dto.getGrantIds())));
        return userService.setPermission(dto.getGrantIds(), dto.getId());
    }


    @RequiresAuthentication
    @GetMapping("/profile")
    @ApiOperation("获取我自己的信息")
    @WebLogger()
    public Result profile() {
        User user = shiroUserService.findByName(_loginUserName());
        return Result.success().addData(JsonConstants.AclJson.USER_INFO_NAME, user)
                .addData(JsonConstants.AclJson.ROLE_LIST_NAME, shiroUserService.roleInfos(_loginUserName()))
                .addData(JsonConstants.AclJson.PERMISSION_LIST_NAME, shiroUserService.permissionInfos(_loginUserName()))
                .addData(JsonConstants.AclJson.USER_TOKEN_NAME, SecurityUtils.getSubject().getSession().getId().toString());
    }


    /**
     * 用户登录
     *
     * @param userLoginDto 登录信息
     * @return 返回用户相关信息
     */
    @PostMapping("login")
    @ApiOperation(value = "用户登录")
    @WebLogger()
    public Result login(@RequestBody UserLoginDto userLoginDto) {
        Result result = shiroUserService.login(userLoginDto.getUserName(), userLoginDto.getPassword(), Lang.getIP(request()));
        if (result.isSuccess()) {
            // 登录成功处理
            _putSession(WebApplication.USER_KEY, result.getData().get(JsonConstants.AclJson.USER_INFO_NAME));
            if (userLoginDto.isRememberMe()) {
                NutMap data = NutMap.NEW();
                data.put("user", userLoginDto.getUserName());
                data.put("password", userLoginDto.getPassword());
                data.put("rememberMe", userLoginDto.getPassword());
                _addCookie("user_remember", DES.encrypt(Json.toJson(data)), 24 * 60 * 60 * 365);
            }
            return result
                    .addData(JsonConstants.AclJson.ROLE_LIST_NAME, shiroUserService.roleInfos(userLoginDto.getUserName()))
                    .addData(JsonConstants.AclJson.PERMISSION_LIST_NAME, shiroUserService.permissionInfos(userLoginDto.getUserName()))
                    .addData(JsonConstants.AclJson.USER_TOKEN_NAME, SecurityUtils.getSubject().getSession().getId().toString());
        }
        return result;
    }

    /**
     * 获取用户的分支机构信息
     *
     * @param id 用户id
     * @return 获取用户所属的分支信息设置情况
     */
    @GetMapping("department/{id}")
    @RequiresPermissions(InstallPermission.USER_DEPARTMENT)
    @ApiOperation(value = "用户分支机构信息")
    @WebLogger()
    public Result departmentInfo(@PathVariable("id") @ApiParam("用户id") Long id) {
        User user = userService.fetch(id);
        if (user == null) {
            return Result.fail("该用户无分支机构信息");
        }
        return Result.success().addData(JsonConstants.LIST_NAME
                , userDepartmentService.findDepartmentsWithUserPowerdInfoByUserId(user));
    }


    /**
     * 为用户设置机构
     *
     * @param dto 用户ID和机构ID
     * @return 处理结果
     */
    @PostMapping("/grant/department")
    @RequiresPermissions(InstallPermission.USER_DEPARTMENT)
    @ApiOperation("为用户设置机构")
    @WebLogger()
    public Result grantBranch(@RequestBody GrantDTO dto) {
        User user = userService.fetch(dto.getId());
        if (user == null) {
            return Result.fail("该用户不需设置分支信息");
        }
        _addOperationLog("设置用户机构", String.format("机构IDs：%s", _IdListString(dto.getGrantIds())));
        return userDepartmentService.setDepartment(dto.getGrantIds(), dto.getId()) ? Result.success() : Result.fail("设置失败");
    }


    /**
     * 获取用户的数据权限设置情况
     */
    @GetMapping("dataRule/{id}")
    @RequiresPermissions(InstallPermission.USER_DATA_RULE)
    @ApiOperation(value = "获取用户的数据权限设置情况")
    @WebLogger()
    public Result dataRuleInfo(@PathVariable("id") @ApiParam("用户id") Long id) {
        User user = userService.fetch(id);
        if (user == null) {
            return Result.fail("该用户无相关信息");
        }
        return Result.success().addData(JsonConstants.LIST_NAME
                , dataRuleRelationService.findDataRulesWithUserPowerdInfoByUserId(id, DataRuleRelationMasterType.USER));
    }


    /**
     * 为用户设置数据权限
     *
     * @param dto 用户ID和数据权限ID
     * @return 处理结果
     */
    @PostMapping("/grant/dataRule")
    @RequiresPermissions(InstallPermission.USER_DATA_RULE)
    @ApiOperation("为用户设置数据权限")
    @WebLogger()
    public Result grantDataRule(@RequestBody GrantDTO dto) {
        User user = userService.fetch(dto.getId());
        if (user == null) {
            return Result.fail("该用户无相关信息");
        }
        _addOperationLog("设置用户数据权限", String.format("权限IDs：%s", _IdListString(dto.getGrantIds())));
        return dataRuleRelationService.setDataRules(dto.getGrantIds(), dto.getId(), DataRuleRelationMasterType.USER) ? Result.success() : Result.fail("设置失败");
    }


    @GetMapping("logout")
    @ApiOperation(value = "退出登录")
    @WebLogger()
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.success();
    }

    private Result validateFormFields(User info) {
        if (StringUtils.isNullOrEmpty(info.getName())
                || StringUtils.isNullOrEmpty(info.getPassword())
                || StringUtils.isNullOrEmpty(info.getEmail())
                || StringUtils.isNullOrEmpty(info.getPhone())
        ) {
            return Result.fail("数据填写不完整");
        }
        if (info.getPassword().length() < 6 || info.getPassword().length() > 16) {
            return Result.fail("密码请设置6到16位数");
        }
        return Result.success();
    }
}
