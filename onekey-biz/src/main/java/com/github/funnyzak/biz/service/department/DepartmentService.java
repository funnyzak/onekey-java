package com.github.funnyzak.biz.service.department;

import com.github.funnyzak.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.department.Department;
import com.github.funnyzak.biz.constant.BizConstants;
import com.github.funnyzak.common.utils.DateUtils;
import com.github.funnyzak.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService extends GeneralService<Department> {

    public <T> List<T> setDeptInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getDeptId", "setDept", BizConstants.SIMPLE_INFO_FIELD_NAME_LIST);
    }

    public Department userAdd(User currentUser, Department department) {
        department.setNum(StringUtils.getNumber(7));
        department.setAddTime(DateUtils.getTS());
        department.setAddUserId(currentUser != null ? currentUser.getId() : null);

        department = save(department);

        addOperationLog(currentUser, BizConstants.DeptConst.NAME, "添加部门", department);

        return department;
    }

    public List<Department> directList(Long parentId) {
        List<Department> list = query(Cnd.NEW().andEX("parentId", "=", parentId));
        return list;
    }

    public List<Department> list() {
        List<Department> list = query();
        return list;
    }

    public Long findIdByName(String name, List<Department> deptList) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }

        List<Department> rltList = deptList.stream().filter(v -> v.getName().equals(name)).collect(Collectors.toList());
        return rltList.size() == 0 ? null : rltList.get(0).getId();
    }

    /**
     * 获取该部门的所有子部门(包含该部门)
     *
     * @param all      所有的部门信息，从这里编辑子部门信息
     * @param parentId 父部门
     * @return
     */
    public List<Department> subList(List<Department> all, Long parentId) {
        if (all == null || all.size() == 0) {
            return null;
        }
        List<Department> parentMatch = all.stream().filter(d -> d.getId().equals(parentId)).collect(Collectors.toList());
        if (parentMatch == null || parentMatch.size() == 0) {
            return null;
        }

        List<Department> departments = new ArrayList<>();
        departments.add(parentMatch.get(0));

        List<Department> subList = all.stream().filter(d -> d.getParentId().equals(parentId)).collect(Collectors.toList());
        if (subList == null || subList.size() == 0) {
            return departments;
        }

        for (Department d : subList) {
            List<Department> dlist = subList(all, d.getId());
            if (dlist == null || dlist.size() == 0) {
                continue;
            }
            departments.addAll(dlist);
        }
        return departments;
    }


}