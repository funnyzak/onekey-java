package com.github.funnyzak.biz.service.acl;

import com.github.funnyzak.biz.constant.BizConstants;
import com.github.funnyzak.biz.exception.BizException;
import com.github.funnyzak.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import com.github.funnyzak.bean.acl.DataRule;
import com.github.funnyzak.bean.acl.DataRuleDataPermissionType;
import com.github.funnyzak.bean.acl.DataRuleModule;
import com.github.funnyzak.bean.acl.User;
import org.springframework.stereotype.Service;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/21 7:07 PM
 * @description DataRuleService
 */
@Service
public class DataRuleService extends GeneralService<DataRule> {

    public DataRule exists(DataRuleModule dataRuleModule, DataRuleDataPermissionType dataPermissionType) {
        return fetch(Cnd.where("id", ">", 0).andEX("module", "=", dataRuleModule).andEX("ruleType", "=", dataPermissionType));
    }

    public DataRule userAdd(User currentUser, DataRule info) throws Exception {
        validateFormFields(info);

        if (exists(info.getModule(), info.getRuleType()) != null) {
            throw new BizException("规则已存在");
        }

        addOperationLog(currentUser, BizConstants.INFO_NAME, "添加" + BizConstants.DataRuleConst.NAME, info.toString());

        return super.save(info);
    }

    public DataRule userEdit(User currentUser, DataRule info) throws Exception {
        validateFormFields(info);

        DataRule realDataRule = super.fetch(info.getId());

        DataRule existInfo = exists(info.getModule(), info.getRuleType());
        if (existInfo != null && existInfo.getId() != realDataRule.getId()) {
            throw new BizException("规则已存在");
        }

        addOperationLog(currentUser, BizConstants.DataRuleConst.NAME, "编辑" + BizConstants.DataRuleConst.NAME, info.toString());
        super.update(info, BizConstants.DataRuleConst.RULE_CAN_EDIT_COLUMNS.split(","));

        return info;
    }

    public void userDel(User currentUser, Long ruleId) throws Exception {
        DataRule realDataRule = super.fetch(ruleId);

        addOperationLog(currentUser, BizConstants.DataRuleConst.NAME, "删除" + BizConstants.DataRuleConst.NAME, realDataRule.toString());

        super.delete(realDataRule);
    }

    private void validateFormFields(DataRule info) throws Exception {
        if (info.getRuleType() == null || info.getModule() == null) {
            throw new BizException("数据填写不完整");
        }
    }
}