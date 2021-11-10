package com.github.funnyzak.onekey.web.controller.console.department;

import com.github.funnyzak.onekey.web.dto.GrantDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.department.Department;
import com.github.funnyzak.onekey.bean.department.UserDepartment;
import com.github.funnyzak.onekey.bean.vo.InstallPermission;
import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.onekey.biz.service.acl.UserService;
import com.github.funnyzak.onekey.biz.service.department.DepartmentService;
import com.github.funnyzak.onekey.biz.service.department.UserDepartmentService;
import com.github.funnyzak.onekey.common.utils.PUtils;
import com.github.funnyzak.onekey.common.Result;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import com.github.funnyzak.onekey.web.controller.console.base.ConsoleBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/11/1 6:37 PM
 * @description DepartmentController
 */
@RestController
@RequestMapping("console/dept")
@Api(value = "Department", tags = {"后端.组织机构"})
public class DepartmentController extends ConsoleBaseController {
    private final DepartmentService departmentService;
    private final UserDepartmentService userDepartmentService;
    private final UserService userService;

    @Autowired
    public DepartmentController(DepartmentService departmentService,
                                UserDepartmentService userDepartmentService,
                                UserService userService) {
        this.departmentService = departmentService;
        this.userService = userService;
        this.userDepartmentService = userDepartmentService;

        this.currentControllerName = "部门管理";
    }

    /**
     * 获取该部门的用户设置情况
     */
    @GetMapping("user/{id}")
    @RequiresPermissions(InstallPermission.DEPT_LIST)
    @ApiOperation(value = "获取该部门的用户设置情况")
    @WebLogger()
    public Result userSetInfo(@PathVariable("id") @ApiParam("部门ID") Long id) {
        return Result.success().addData(JsonConstants.LIST_NAME, userDepartmentService.findUsersWithDepartmentInfoByDepartmentId(id));
    }


    /**
     * 为机构设置用户
     *
     * @param dto 部门ID和用户ID列表
     * @return 处理结果
     */
    @PostMapping("/grant/user")
    @RequiresPermissions(InstallPermission.DEPT_USER_EDIT)
    @ApiOperation("为机构设置用户")
    @WebLogger()
    public Result grantUser(@RequestBody GrantDTO dto) {
        _addOperationLog("设置机构用户列表", String.format("相关IDs：%s", _IdListString(dto.getGrantIds())));
        return userDepartmentService.setDepartmentUsers(dto.getGrantIds(), dto.getId()) ? Result.success() : Result.fail("设置失败");
    }


    @GetMapping("list/user")
    @RequiresPermissions(InstallPermission.DEPT_LIST)
    @ApiOperation("获取部门下的直接用户(所有)")
    public Result search(@RequestParam(value = "departmentId", defaultValue = "0") @ApiParam("部门ID") int departmentId) {

        if (departmentId <= 0) {
            return Result.fail("无效参数");
        }

        List<Long> userIdList = userDepartmentService.findUserRelationByDepartmentIds(new long[]{departmentId});
        if (userIdList == null || userIdList.size() == 0) {
            return Result.success();
        }

        Cnd cnd = Cnd.NEW().andEX("id", "in", userIdList);
        return Result.success().addData(JsonConstants.LIST_NAME, userService.query(cnd));
    }

    /**
     * 部门检索
     *
     * @param key  关键词
     * @param page 页码
     * @return
     */
    @GetMapping("list")
    @RequiresPermissions(InstallPermission.DEPT_LIST)
    @ApiOperation("部门检索")
    public Result list(
            @RequestParam(value = "key", required = false) @ApiParam("关键词") String key,
            @RequestParam(value = "parentId", defaultValue = "0", required = false) @ApiParam("父ID") Long parentId,
            @RequestParam(value = "pageSize", defaultValue = "15") @ApiParam("页大小") Integer pageSize,
            @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") Integer page) {

        Cnd cnd = PUtils.cndBySearchKey(null, key, "name", "description")
                .andEX("id", "in", userDepartmentService.findDepartmentRelationByUserId(currentUser().getId()))
                .andEX("parentId", "=", parentId);

        PageredData<Department> pager = departmentService.searchByPage(_fixPage(page), pageSize, cnd.orderBy("id", "desc"));

        return Result.success().addData(JsonConstants.PAGER_NAME, pager);
    }

    /**
     * 添加部门
     *
     * @param department 待添加部门
     * @return
     */
    @PostMapping("add")
    @RequiresPermissions(InstallPermission.DEPT_ADD)
    @ApiOperation("添加部门")
    public Result save(@RequestBody Department department) {
        return Result.success().addData(JsonConstants.INFO_NAME, departmentService.userAdd(currentUser(), department));
    }

    /**
     * 部门数据详情
     *
     * @param id 部门id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions(InstallPermission.DEPT_EDIT)
    @ApiOperation("部门详情")
    public Result detail(@PathVariable("id") @ApiParam("部门id") long id) {
        return Result.success().addData(JsonConstants.INFO_NAME, departmentService.fetch(id));
    }

    /**
     * 删除部门
     *
     * @param id 部门id
     * @return
     */
    @DeleteMapping("{id}")
    @RequiresPermissions(InstallPermission.DEPT_DELETE)
    @ApiOperation("删除部门")
    public Result delete(@PathVariable("id") @ApiParam("部门id") long id) {
        Department department = departmentService.fetch(id);

        if (department == null) {
            return Result.fail("不存在的数据");
        }

        List<Department> departments = departmentService.query(Cnd.where("parentId", "=", id));
        if (departments != null && departments.size() > 0) {
            return Result.fail("还有子部门未删除。");
        }
        List<UserDepartment> userDepartments = userDepartmentService.query(Cnd.where("departmentId", "=", id));
        if (userDepartments != null && userDepartments.size() > 0) {
            return Result.fail("请先移除部门下的人员。");
        }

        _addOperationLog("删除部门", department.toString());

        return departmentService.delete(id) == 1 ? Result.success() : Result.fail("删除数据失败!");
    }

    @DeleteMapping("user/{id}")
    @RequiresPermissions(InstallPermission.DEPT_USER_EDIT)
    @ApiOperation("移除部门员工")
    public Result userDelete(@PathVariable("id") @ApiParam("部门id") long id, @RequestParam(value = "uid", defaultValue = "0") @ApiParam("用户ID") long uid) {
        UserDepartment userDepartment = userDepartmentService.fetch(Cnd.where("departmentId", "=", id).and("userId", "=", uid));

        if (userDepartment == null) {
            return Result.fail("不存在的数据");
        }
        _addOperationLog("移除部门员工", userDepartment.toString());

        return userDepartmentService.delete(userDepartment) > 0 ? Result.success() : Result.fail("移除失败!");
    }

    /**
     * 编辑部门
     *
     * @param department 待编辑部门
     * @return
     */
    @PostMapping("edit")
    @RequiresPermissions(InstallPermission.DEPT_EDIT)
    @ApiOperation("编辑部门")
    public Result update(@RequestBody Department department) {
        _addOperationLog("编辑部门", department.toString());
        return !departmentService.update(department, "name", "orderId", "description") ? Result.fail("更新数据失败!") : Result.success().addData(JsonConstants.INFO_NAME, department);
    }
}