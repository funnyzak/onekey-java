package com.github.funnyzak.onekey.biz.service.member.impl;

import com.github.funnyzak.onekey.biz.constant.BizConstants;
import com.github.funnyzak.onekey.biz.enums.TimeIntervalType;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.biz.service.GeneralService;
import com.github.funnyzak.onekey.biz.service.member.MemberService;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.enums.Gender;
import com.github.funnyzak.onekey.bean.log.enums.PmType;
import com.github.funnyzak.onekey.bean.log.enums.PmUse;
import com.github.funnyzak.onekey.bean.member.MemberInfo;
import com.github.funnyzak.onekey.biz.service.log.PmService;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.MD5Utils;
import com.github.funnyzak.onekey.common.utils.PUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/15 9:47 上午
 * @description MemberInfoServiceImpl
 */
@Service
public class MemberServiceImpl extends GeneralService<MemberInfo> implements MemberService {
    private final PmService pmService;

    @Autowired
    public MemberServiceImpl(PmService pmService) {
        this.pmService = pmService;
    }

    @Override
    public Integer count(Cnd cnd) {
        return super.count(cnd);
    }

    private Cnd baseCnd() {
        return baseCnd(null);
    }

    private Cnd baseCndWithApp(String appId) {
        return baseCnd().andEX("appId", "=", appId);
    }

    private Cnd baseCnd(Cnd cnd) {
        return (cnd == null ? Cnd.NEW() : cnd).and("del", "=", false);
    }

    @Override
    public <T> List<T> setMemberInfoInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getMid", "setMemberInfo", BizConstants.MemberConst.MEMBER_SIMPLE_INFO_FIELD_NAME_LIST);
    }

    @Override
    public <T> List<T> setMemberInfoInfo(List<T> list, String getIdMethodName, String setMethodName) {
        return setListInfoByListColumnId(list, getIdMethodName, setMethodName, BizConstants.MemberConst.MEMBER_SIMPLE_INFO_FIELD_NAME_LIST);
    }

    @Override
    public String randomUserName() {
        return "id_" + StringUtils.getRandomLetterAndDigital(16).toLowerCase();
    }

    @Override
    public MemberInfo fetch(Long id) {
        return super.fetch(baseCnd().and("id", "=", id));
    }

    @Override
    public MemberInfo findByPhone(String appId, String phone) {
        return super.fetch(baseCndWithApp(appId).and("phone", "=", phone));
    }

    @Override
    public MemberInfo findByEmail(String appId, String email) {
        return super.fetch(baseCndWithApp(appId).and("email", "=", email));
    }

    @Override
    public MemberInfo findByWxMa(String appId, String num) {
        return super.fetch(baseCndWithApp(appId).and("weAppOpenId", "=", num));
    }

    @Override
    public MemberInfo findByWxMp(String appId, String num) {
        return super.fetch(baseCndWithApp(appId).and("weMpOpenId", "=", num));
    }

    @Override
    public MemberInfo findByWxUnion(String appId, String num) {
        return super.fetch(baseCndWithApp(appId).and("weChatUnionId", "=", num));
    }

    @Override
    public MemberInfo findByNameValue(String appId, String name, String value) {
        return super.fetch(baseCndWithApp(appId).and(name, "=", value));
    }

    @Override
    public MemberInfo findByIdNum(String appId, String idNum) {
        return super.fetch(baseCndWithApp(appId).and("idNum", "=", idNum));
    }

    @Override
    public MemberInfo findByUserName(String appId, String username) {
        return super.fetch(baseCndWithApp(appId).and("username", "=", username));
    }

    @Override
    public MemberInfo registerByPmCode(String app, String phone, String idNum, String pwd, String pmCode, String ip) throws Exception {
        checkIdNumExist(app, idNum);
        checkPhoneExist(app, phone);

        boolean success = pmService.verifyPm(app, PmType.SMS, PmUse.REGISTER, phone, pmCode, 300);
        if (!success) {
            throw new BizException("验证码错误");
        }
        MemberInfo info = new MemberInfo();
        info.setPhone(phone);
        info.setAppId(app);
        info.setIdNum(idNum);
        info.setPwd(md5Pwd(pwd));
        info = memberRegisterMemberInfo(info, ip);
        return super.save(info);
    }

    @Override
    public MemberInfo login(String appId, String phone, String pwd, String ip) throws Exception {
        if (StringUtils.isNullOrEmpty(phone) || StringUtils.isNullOrEmpty(pwd)) {
            throw new BizException("请填写完整登陆信息");
        }
        MemberInfo info = phone.length() >= 15 ? findByIdNum(appId, phone) : findByPhone(appId, phone);
        if (info == null) {
            throw new BizException("不存在此会员");
        }
        if (info.getPwd() != null && !info.getPwd().equals(md5Pwd(pwd))) {
            throw new BizException("密码错误");
        }
        if (info.getLocked()) {
            throw new BizException("已被锁定，请联系管理员解锁");
        }

        /*******如果未设置密码，使用身份后6+位作为默认密码*******/
        if (StringUtils.isNullOrEmpty(info.getPwd())
                && phone.length() >= 15
                && pwd.length() == 6
                && phone.endsWith(pwd)) {
            info.setPwd(md5Pwd(pwd));
        }

        info.setLastLoginIp(ip);
        info.setLastLoginTime(DateUtils.getTS());
        super.update(info);

        return info;
    }

    @Override
    public MemberInfo loginByWxMa(String appId, String openId, String ip) {
        return loginByIdentify(appId, "weAppOpenId", openId, ip, "findByWxMa");
    }

    @Override
    public MemberInfo loginByWxMp(String appId, String openId, String ip) {
        return loginByIdentify(appId, "weMpOpenId", openId, ip, "findByWxMp");
    }

    @Override
    public MemberInfo loginByWxUnion(String appId, String openId, String ip) {
        return loginByIdentify(appId, "weUnionId", openId, ip, "findByWxUnion");
    }

    MemberInfo loginByIdentify(String appId, String colName, String colVal, String ip, String fetchInfoMd) {
        try {
            Object dbInfoObj = PUtils.getMethod(this, fetchInfoMd, String.class, String.class).invoke(this, appId, colVal);
            if (dbInfoObj == null) {
                MemberInfo info = new MemberInfo();
                info.setAppId(appId);
                info.setRegisterIp(ip);
                PUtils.setEntityColumn(info, "set".concat(StringUtils.firstCodeToUpperCase(colName)), colVal, String.class);
                super.save(info);
                return info;
            } else {
                MemberInfo info = (MemberInfo) dbInfoObj;
                info.setLastLoginIp(ip);
                info.setLastLoginTime(DateUtils.getTS());
                super.update(info);
                return info;
            }
        } catch (Exception ex) {
            logger.error("根据唯一标识进行会员登陆失败==>", ex);
            return null;
        }
    }


    @Override
    public MemberInfo forget(String app, String phone, String pwd, String pmCode) throws Exception {
        boolean success = pmService.verifyPm(app, PmType.SMS, PmUse.FORGET_PASSWORD, phone, pmCode, 300);
        if (!success) {
            throw new BizException("验证码错误");
        }

        MemberInfo info = findByPhone(app, phone);
        if (info == null) {
            throw new BizException("不存在此会员");
        }

        info.setPwd(md5Pwd(pwd));
        super.update(info);
        return info;
    }

    @Override
    public MemberInfo add(@NotNull MemberInfo info, User currentUser, String ip) throws Exception {
        info = memberRegisterMemberInfo(info, ip);

        checkUsernameExist(info.getAppId(), info.getUsername());
        checkIdNumExist(info.getAppId(), info.getIdNum());
        checkPhoneExist(info.getAppId(), info.getPhone());
        checkEmailExist(info.getAppId(), info.getEmail());
        checkWxMaExist(info.getAppId(), info.getWeAppOpenId());
        checkWxMpExist(info.getAppId(), info.getWeMpOpenId());
        checkWxUnionExist(info.getAppId(), info.getWeUnionId());

        if (!StringUtils.isNullOrEmpty(info.getPwd())) {
            info.setPwd(md5Pwd(info.getPwd()));
        }
        if (currentUser != null) {
            info = userAddMemberInfo(info, currentUser, ip);
            addOperationLog(currentUser, BizConstants.MemberConst.NAME, "添加会员", info);
        }

        return super.save(info);
    }

    @Override
    public MemberInfo edit(@NotNull MemberInfo info, User currentUser) throws Exception {
        MemberInfo oldInfo = fetch(info.getId());
        checkNull(oldInfo);

        checkUsernameExist(info.getAppId(), info.getUsername(), oldInfo);
        checkIdNumExist(info.getAppId(), info.getIdNum(), oldInfo);
        checkPhoneExist(info.getAppId(), info.getPhone(), oldInfo);
        checkEmailExist(info.getAppId(), info.getEmail(), oldInfo);
        checkWxMaExist(info.getAppId(), info.getWeAppOpenId(), oldInfo);
        checkWxMpExist(info.getAppId(), info.getWeMpOpenId(), oldInfo);
        checkWxUnionExist(info.getAppId(), info.getWeUnionId(), oldInfo);

        if (currentUser != null) {
            info.setUpdateTime(DateUtils.getTS());
            info.setUpdateUserId(currentUser.getId());
            addOperationLog(currentUser, BizConstants.MemberConst.NAME, "编辑会员", info);
        }
        info.setUpdateTime(DateUtils.getTS());

        super.update(info, (currentUser != null ? BizConstants.MemberConst.MEMBER_CAN_EDIT_COLUMN_NAME_LIST : BizConstants.OpenConst.API_MEMBER_INFO_CAN_EDIT_FIELDS).split(","));

        return fetch(info.getId());
    }

    private MemberInfo checkInfo(Long mid) throws Exception {
        MemberInfo info = fetch(mid);
        if (info == null) {
            throw new BizException("不存在");
        }
        return info;
    }

    @Override
    public MemberInfo lock(@NotNull User user, Long mid, Boolean lock) throws Exception {
        MemberInfo info = checkInfo(mid);
        if (user != null) {
            addOperationLog(user, BizConstants.MemberConst.NAME, String.format("%s会员", lock ? "锁定" : "解锁"), info);
        }
        info.setLocked(lock);
        super.update(info);
        return fetch(mid);
    }

    @Override
    public void remove(@NotNull User user, Long mid) throws Exception {
        MemberInfo info = checkInfo(mid);

        if (user != null) {
            addOperationLog(user, BizConstants.MemberConst.NAME, "删除会员", info);
        }
        info.setDel(true);
        super.update(info);
    }

    @Override
    public String md5Pwd(String pwd) {
        return MD5Utils.encodeMD5Hex(pwd).substring(7, 23);
    }

    @Override
    public void resetPwd(@NotNull User user, Long mid, String newPwd) throws Exception {
        MemberInfo info = checkInfo(mid);

        if (user != null) {
            addOperationLog(user, BizConstants.MemberConst.NAME, "重置会员密码", info);
        }
        info.setPwd(md5Pwd(newPwd));
        info.setUpdateTime(DateUtils.getTS());
        super.update(info);
    }

    @Override
    public void resetPwd(Long mid, String oldPwd, String newPwd) throws Exception {
        MemberInfo info = checkInfo(mid);
        if (!StringUtils.isNullOrEmpty(oldPwd) && !md5Pwd(oldPwd).equals(info.getPwd())) {
            throw new BizException("原密码输入不正确");
        }
        info.setPwd(md5Pwd(newPwd));
        info.setUpdateTime(DateUtils.getTS());
        super.update(info);
    }

    @Override
    public void checkIdNumExist(String appId, String idNum) throws Exception {
        if (StringUtils.isNullOrEmpty(idNum)) {
            return;
        }

        MemberInfo info = findByIdNum(appId, idNum);
        if (info != null) {
            throw new BizException("身份证号已存在");
        }
    }

    @Override
    public void checkIdNumExist(String appId, String newIdNum, MemberInfo oldInfo) throws Exception {
        if (StringUtils.isNullOrEmpty(newIdNum)) {
            return;
        }

        if (oldInfo == null || !oldInfo.getIdNum().equals(newIdNum)) {
            checkIdNumExist(oldInfo.getAppId(), newIdNum);
        }
    }

    @Override
    public void checkUsernameExist(String appId, String username) throws Exception {
        if (StringUtils.isNullOrEmpty(username)) {
            return;
        }

        MemberInfo info = findByUserName(appId, username);
        if (info != null) {
            throw new BizException("用户名已存在");
        }
    }

    @Override
    public void checkUsernameExist(String appId, String newUsername, MemberInfo oldInfo) throws Exception {
        if (StringUtils.isNullOrEmpty(newUsername)) {
            return;
        }

        if (oldInfo == null || !oldInfo.getUsername().equals(newUsername)) {
            checkUsernameExist(appId, newUsername);
        }
    }

    @Override
    public void checkPhoneExist(String appId, String phone) throws Exception {
        if (StringUtils.isNullOrEmpty(phone)) {
            return;
        }

        MemberInfo info = findByPhone(appId, phone);
        if (info != null) {
            throw new BizException("手机号已存在");
        }
    }

    @Override
    public void checkPhoneExist(String appId, String phone, MemberInfo oldInfo) throws Exception {
        if (StringUtils.isNullOrEmpty(phone)) {
            return;
        }

        if (oldInfo == null || !oldInfo.getPhone().equals(phone)) {
            checkPhoneExist(appId, phone);
        }
    }

    void checkColumnExist(String appId, String columnValue, String columnDesc, Method fetchDbMethod) throws Exception {
        if (StringUtils.isNullOrEmpty(columnValue)) {
            return;
        }

        MemberInfo info = (MemberInfo) fetchDbMethod.invoke(this, appId, columnValue);
        if (info != null) {
            throw new BizException(columnDesc.concat("已存在"));
        }
    }

    void checkColumnExist(String appId, String columnName, String columnValue, String columnDesc, MemberInfo oldInfo, Method fetchDbMethod) throws Exception {
        if (StringUtils.isNullOrEmpty(columnValue)) {
            return;
        }

        Object oldColumnValue = PUtils.columnValue(oldInfo, "get" + StringUtils.firstCodeToUpperCase(columnName));
        if (oldInfo == null || oldColumnValue == null || !oldColumnValue.toString().equals(columnValue)) {
            checkColumnExist(appId, columnValue, columnDesc, fetchDbMethod);
        }
    }

    @Override
    public void checkWxMaExist(String appId, String wxAppId) throws Exception {
        checkColumnExist(appId, wxAppId, "微信小程序OpenId", PUtils.getMethod(this, "findByWxMa", String.class, String.class));
    }

    @Override
    public void checkWxMaExist(String appId, String wxAppId, MemberInfo oldInfo) throws Exception {
        checkColumnExist(appId, "weAppOpenId", wxAppId, "微信小程序OpenId", oldInfo, PUtils.getMethod(this, "findByWxMa", String.class, String.class));

    }

    @Override
    public void checkWxMpExist(String appId, String wxAppId) throws Exception {
        checkColumnExist(appId, wxAppId, "微信公众号OpenId", PUtils.getMethod(this, "findByWxMp", String.class, String.class));
    }

    @Override
    public void checkWxMpExist(String appId, String wxAppId, MemberInfo oldInfo) throws Exception {
        checkColumnExist(appId, "weMpOpenId", wxAppId, "微信公众号OpenId", oldInfo, PUtils.getMethod(this, "findByWxMp", String.class, String.class));
    }

    @Override
    public void checkWxUnionExist(String appId, String unionId) throws Exception {
        checkColumnExist(appId, unionId, "微信开放UnionId", PUtils.getMethod(this, "findByWxUnion", String.class, String.class));

    }

    @Override
    public void checkWxUnionExist(String appId, String unionId, MemberInfo oldInfo) throws Exception {
        checkColumnExist(appId, "weUnionId", unionId, "微信开放UnionId", oldInfo, PUtils.getMethod(this, "findByWxUnion", String.class, String.class));
    }

    @Override
    public void checkEmailExist(String appId, String email) throws Exception {
        checkColumnExist(appId, email, "电子邮件地址", PUtils.getMethod(this, "findByEmail", String.class, String.class));
    }

    @Override
    public void checkEmailExist(String appId, String newEmail, MemberInfo oldInfo) throws Exception {
        checkColumnExist(appId, "email", newEmail, "电子邮件地址", oldInfo, PUtils.getMethod(this, "findByEmail", String.class, String.class));
    }

    @Override
    public void resetIdNum(String app, Long mid, String newIdNum) throws Exception {
        MemberInfo info = checkInfo(mid);
        checkIdNumExist(app, newIdNum, info);

        info.setIdNum(newIdNum);
        info.setUpdateTime(DateUtils.getTS());
        super.update(info);
    }

    @Override
    public void checkExistByName(String appId, String name, String value) throws Exception {
        if (StringUtils.isNullOrEmpty(value)) {
            return;
        }

        MemberInfo info = findByNameValue(appId, name, value);
        if (info != null) {
            throw new BizException(name + " exist.");
        }
    }

    @Override
    public void checkExistByName(String appId, String name, String value, MemberInfo oldInfo) throws Exception {
        if (StringUtils.isNullOrEmpty(value)) {
            return;
        }
        Object oldValue = PUtils.columnValue(oldInfo, "get" + StringUtils.firstCodeToUpperCase(name));
        if (oldInfo == null || !oldValue.toString().equals(value)) {
            checkExistByName(appId, name, value);
        }
    }

    @Override
    public void resetPhone(String app, Long mid, String newPhone, String code) throws Exception {
        MemberInfo info = checkInfo(mid);
        boolean pmVerify = pmService.verifyPm(app, PmType.SMS, PmUse.RESET_PHONE, newPhone, code, 300);
        if (!pmVerify) {
            throw new BizException("短信验证失败");
        }
        checkPhoneExist(app, newPhone, info);

        info.setPhone(newPhone);
        info.setUpdateTime(DateUtils.getTS());
        super.update(info);
    }


    @Override
    public Cnd condition(Cnd cnd, User user, String appId, String phone, String email, String realName, String idNum, String username, Gender gender, String registerIp) {
        cnd = PUtils.cndBySearchKey(cnd, realName, "realName");
        cnd = PUtils.cndBySearchKey(cnd, phone, "phone");
        cnd = PUtils.cndBySearchKey(cnd, idNum, "idNum");
        cnd = PUtils.cndBySearchKey(cnd, email, "email");
        cnd = PUtils.cndBySearchKey(cnd, username, "username");

        cnd = baseCnd(cnd)
                .andEX("gender", "=", gender)
                .andEX("registerIp", "=", registerIp);
        return cnd;
    }

    @Override
    public PageredData<MemberInfo> pager(Integer page, Integer pageSize, Cnd cnd, String orderBy, Boolean orderDesc) {
        PageredData<MemberInfo> pager = super.searchByPage(page, pageSize, (cnd == null ? Cnd.NEW() : cnd).orderBy(StringUtils.isNullOrEmpty(orderBy) ? "id" : orderBy, orderDesc == null || orderDesc ? "desc" : "asc"));
        pager.setDataList(setListInfo(pager.getDataList()));
        return pager;
    }

    @Override
    public List<MemberInfo> list(Cnd cnd) {
        PageredData<MemberInfo> pager = super.searchByPage(1, 100000, cnd);
        pager.setDataList(setListInfo(pager.getDataList()));
        return pager.getDataList();
    }

    @Override
    public List<MemberInfo> setListInfo(List<MemberInfo> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        return list;
    }


    @Override
    public MemberInfo fullInfo(MemberInfo info) {
        if (info == null) {
            return null;
        }
        return setListInfo(Arrays.asList(info)).get(0);
    }

    Long parseDate(String dateString) {
        if (StringUtils.isNullOrEmpty(dateString)) {
            return null;
        }
        try {
            return DateUtils.parse("yyyy-MM-dd", dateString).getTime() / 1000;
        } catch (Exception ex) {
            return null;
        }
    }

    private MemberInfo memberRegisterMemberInfo(MemberInfo info, String ip) {
        info.setDel(false);
        if (StringUtils.isNullOrEmpty(info.getUsername()))
            info.setUsername(randomUserName());
        info.setRegisterIp(ip);
        info.setAddTime(DateUtils.getTS());
        info.setRegisterTime(DateUtils.getTS());
        info.setLocked(false);
        info.setLastLoginTime(DateUtils.getTS());
        info.setLastLoginIp(ip);
        return info;
    }

    private MemberInfo userAddMemberInfo(MemberInfo info, User addUser, String ip) {
        info = memberRegisterMemberInfo(info, ip);

        if (addUser == null) {
            return info;
        }
        if (!StringUtils.isNullOrEmpty(info.getPwd())) {
            info.setPwd(md5Pwd(info.getPwd()));
        }
        info.setAddUserId(addUser.getId());
        return info;
    }

    @Override
    public List<TimeIntervalType.TimePeriod> countStat(User currentUser, Long startTime, Long endTime, TimeIntervalType intervalType) {
        List<TimeIntervalType.TimePeriod> timeIntervals = TimeIntervalType.calcTimePeriod(intervalType, startTime, endTime);
        List<MemberInfo> list = super.query(condition(null, currentUser, null, null, null, null, null, null, null, null));

        for (TimeIntervalType.TimePeriod timeInterval : timeIntervals) {
            List<MemberInfo> matchRecords = list == null || list.size() == 0 ? null : list.stream()
                    .filter(v -> v.getAddTime() >= timeInterval.getStartTime() && v.getAddTime() < timeInterval.getEndTime())
                    .collect(Collectors.toList());
            timeInterval.setData(matchRecords == null ? 0L : matchRecords.size());
        }
        return timeIntervals;
    }
}