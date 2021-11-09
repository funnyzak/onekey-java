package com.github.funnyzak.web.controller.console.acl;

import com.github.funnyzak.web.controller.console.base.ConsoleBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.dao.Cnd;
import com.github.funnyzak.bean.acl.Permission;
import com.github.funnyzak.bean.vo.InstallPermission;
import com.github.funnyzak.biz.constant.JsonConstants;
import com.github.funnyzak.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.biz.service.acl.PermissionService;
import com.github.funnyzak.common.utils.PUtils;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.common.utils.StringUtils;
import com.github.funnyzak.web.annotation.weblog.WebLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * PermissionController class
 *
 * @author potato
 * @date 2019/07/25
 */
@RestController
@RequestMapping("console/permission")
@Api(value = "Permission", tags = {"后端.访问控制"})
public class PermissionController extends ConsoleBaseController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
        this.currentControllerName = "权限管理";
    }


    /**
     * 权限列表
     *
     * @param page     页码
     * @param pageSize 页大小
     * @param key      搜索关键字
     * @return 分页数据
     */
    @GetMapping("list")
    @RequiresPermissions(InstallPermission.PERMISSION_LIST)
    @ApiOperation("权限列表")
    @WebLogger()
    public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page
            , @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") int pageSize
            , @RequestParam(value = "key", required = false) @ApiParam("搜索关键字") String key) {
        Cnd cnd = Cnd.NEW();
        if (!StringUtils.isNullOrEmpty(key)) {
            cnd = PUtils.cndBySearchKey(cnd, key, "name", "description");
        }
        return Result.success().addData(JsonConstants.PAGER_NAME, permissionService.searchByPage(_fixPage(page), pageSize, cnd.desc("id")).addParam("key", key));
    }


    /**
     * 添加权限
     *
     * @return 权限实体
     */
    @PutMapping("add")
    @RequiresPermissions(InstallPermission.PERMISSION_ADD)
    @ApiOperation("添加权限")
    @WebLogger()
    public Result add(@RequestBody Permission permission) {
        Result result = validateFormFields(permission);
        if (!result.isSuccess()) {
            return result;
        }
        if (permissionService.fetch(permission.getName()) != null) {
            return Result.fail("名称已存在");
        }

        permission.setInstalled(false);
        _addOperationLog("添加权限", permission.toString());
        return permissionService.save(permission) == null ? Result.fail("保存权限失败!") : Result.success().addData(JsonConstants.AclJson.PERMISSION_NAME, permission);
    }

    /**
     * 权限详情
     *
     * @param id 权限id
     * @return 权限实体
     */
    @GetMapping("{id}")
    @RequiresPermissions(InstallPermission.PERMISSION_EDIT)
    @ApiOperation("权限详情")
    @WebLogger()
    public Result detail(@PathVariable("id") @ApiParam("权限id") long id) {
        return Result.success().addData(JsonConstants.AclJson.PERMISSION_NAME, permissionService.fetch(id));
    }

    /**
     * 删除权限
     *
     * @param id 权限id
     * @return 处理结果
     */
    @DeleteMapping("{id}")
    @RequiresPermissions(InstallPermission.PERMISSION_DELETE)
    @ApiOperation("删除权限")
    @WebLogger()
    public Result delete(@PathVariable("id") @ApiParam("权限id") long id) {
        Permission permission = permissionService.fetch(id);
        if (permission.isInstalled()) {
            return Result.fail("不能删除系统权限");
        }
        _addOperationLog("删除权限", String.format("id:%s", id));
        return permissionService.delete(id) == 1 ? Result.success() : Result.fail("删除权限失败!");
    }

    /**
     * 权限编辑
     *
     * @param permission 权限实体
     * @return 权限实体
     */
    @PostMapping("edit")
    @RequiresPermissions(InstallPermission.PERMISSION_EDIT)
    @ApiOperation("更新权限")
    @WebLogger()
    public Result update(@RequestBody Permission permission) {
        Permission realPermission = permissionService.fetch(permission.getId());

        String updateFields = realPermission.isInstalled() ? "intro" : "name,description,intro,group";

        _addOperationLog("编辑权限", permission.toString());

        return !permissionService.update(permission, updateFields.split(",")) ? Result.fail("更新权限失败!") : Result.success().addData("permission", permission);
    }

    private Result validateFormFields(Permission info) {
        if (StringUtils.isNullOrEmpty(info.getName()) || StringUtils.isNullOrEmpty(info.getDescription())) {
            return Result.fail("数据填写不完整");
        }
        return Result.success();
    }

}
