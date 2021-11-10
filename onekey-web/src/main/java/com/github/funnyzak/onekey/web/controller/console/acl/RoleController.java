package com.github.funnyzak.onekey.web.controller.console.acl;

import com.github.funnyzak.onekey.web.controller.console.base.ConsoleBaseController;
import com.github.funnyzak.onekey.web.dto.GrantDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.acl.DataRuleRelationMasterType;
import com.github.funnyzak.onekey.bean.acl.Role;
import com.github.funnyzak.onekey.bean.vo.InstallPermission;
import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.onekey.biz.service.acl.DataRuleRelationService;
import com.github.funnyzak.onekey.biz.service.acl.RoleService;
import com.github.funnyzak.onekey.common.utils.PUtils;
import com.github.funnyzak.onekey.common.Result;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * RoleController class
 *
 * @author potato
 * @date 2019/07/25
 */
@RestController
@RequestMapping("console/role")
@Api(value = "Role", tags = {"后端.访问控制"})
public class RoleController extends ConsoleBaseController {

    private final RoleService roleService;
    private final DataRuleRelationService dataRuleRelationService;

    @Autowired
    public RoleController(RoleService roleService, DataRuleRelationService dataRuleRelationService) {
        this.roleService = roleService;
        this.dataRuleRelationService = dataRuleRelationService;

        this.currentControllerName = "角色管理";
    }


    /**
     * 获取角色信息列表
     *
     * @param page     页码
     * @param pageSize 页大小
     * @param key      搜索关键字
     * @return 分页数据
     */
    @GetMapping("list")
    @RequiresPermissions(InstallPermission.ROLE_LIST)
    @ApiOperation("角色列表")
    @WebLogger()
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page,
                       @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize,
                       @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key) throws Exception {

        Cnd cnd = Cnd.NEW();
        if (!StringUtils.isNullOrEmpty(key)) {
            cnd = PUtils.cndBySearchKey(cnd, key, "name", "description");
        }

        PageredData<Role> pager = roleService.searchByPage(_fixPage(page), pageSize, cnd.desc("id"));

        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }


    /**
     * 添加一个角色
     *
     * @param role 角色信息实体
     * @return 返回角色实体
     */
    @PutMapping("add")
    @RequiresPermissions(InstallPermission.ROLE_ADD)
    @ApiOperation("添加角色")
    @WebLogger()
    public Result add(@RequestBody Role role) {
        Role addedRole = roleService.save(role);

        if (addedRole != null) {
            _addOperationLog("添加角色", role.toString());
        }

        return addedRole == null ? Result.fail("保存角色失败!") : Result.success().addData("role", addedRole);
    }


    /**
     * 删除角色
     *
     * @param id 角色id
     * @return 处理结果
     */
    @DeleteMapping("{id}")
    @RequiresPermissions(value = {InstallPermission.ROLE_DELETE})
    @ApiOperation("删除角色")
    @WebLogger()
    public Result remove(@PathVariable("id") @ApiParam("角色id") long id) {
        Role role = roleService.fetch(id);

        boolean opRlt = roleService.delete(id) == 1;

        if (opRlt) {
            _addOperationLog("删除角色", role.toString());
        }

        return opRlt ? Result.success() : Result.fail("删除角色失败!");
    }

    /**
     * 更新角色
     *
     * @param role 待更新角色
     * @return 角色信息
     */
    @PostMapping("edit")
    @RequiresPermissions(InstallPermission.ROLE_EDIT)
    @ApiOperation("编辑角色")
    @WebLogger()
    public Result update(@RequestBody Role role) {
        boolean opRlt = roleService.update(role, "name", "description");
        if (opRlt) {
            _addOperationLog("角色编辑", role.toString());
        }

        _addOperationLog("编辑角色", role.toString());

        return opRlt ? Result.success() : Result.fail("更新角色失败!");
    }

    /**
     * 获取角色的权限列表
     *
     * @param id 角色id
     * @return 权限列表
     */
    @GetMapping("permission/{id}")
    @RequiresPermissions(InstallPermission.ROLE_GRANT)
    @ApiOperation("获取角色的授权信息")
    @WebLogger()
    public Result permissionInfo(@PathVariable("id") @ApiParam("角色id") int id) {
        return Result.success().addData(JsonConstants.AclJson.PERMISSION_LIST_NAME, roleService.findPermissionsWithRolePowerdInfoByRoleId(id));
    }

    /**
     * 设置角色的权限
     *
     * @param dto 角色ID和权限IDs
     * @return 设置结果
     */
    @PostMapping("grant")
    @RequiresPermissions(InstallPermission.ROLE_GRANT)
    @ApiOperation("为角色授权")
    @WebLogger()
    public Result grantRole(@RequestBody GrantDTO dto) {
        _addOperationLog("角色赋权", _IdListString(dto.getGrantIds()));
        return roleService.setPermission(dto.getGrantIds(), dto.getId());
    }

    /**
     * 获取角色的数据权限设置情况
     */
    @GetMapping("dataRule/{id}")
    @RequiresPermissions(InstallPermission.ROLE_DATA_RULE)
    @ApiOperation(value = "获取角色的数据权限设置情况")
    @WebLogger()
    public Result dataRuleInfo(@PathVariable("id") @ApiParam("用户id") Long id) {
        Role role = roleService.fetch(id);
        if (role == null) {
            return Result.fail("该用户无相关信息");
        }
        return Result.success().addData(JsonConstants.LIST_NAME
                , dataRuleRelationService.findDataRulesWithUserPowerdInfoByUserId(id, DataRuleRelationMasterType.ROLE));
    }


    /**
     * 为角色设置数据权限
     *
     * @param dto 角色ID和数据权限ID
     * @return 处理结果
     */
    @PostMapping("/grant/dataRule")
    @RequiresPermissions(InstallPermission.ROLE_DATA_RULE)
    @ApiOperation("为角色设置数据权限")
    @WebLogger()
    public Result grantDataRule(@RequestBody GrantDTO dto) {
        Role role = roleService.fetch(dto.getId());
        if (role == null) {
            return Result.fail("该角色不需设置");
        }
        _addOperationLog("设置角色数据权限", String.format("权限IDs：%s", _IdListString(dto.getGrantIds())));
        return dataRuleRelationService.setDataRules(dto.getGrantIds(), dto.getId(), DataRuleRelationMasterType.ROLE) ? Result.success() : Result.fail("设置失败");
    }
}
