package com.github.funnyzak.biz.service.member;

import com.github.funnyzak.biz.enums.TimeIntervalType;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.enums.Gender;
import com.github.funnyzak.bean.member.MemberInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/15 9:46 上午
 * @description MemberInfoService
 */
public interface MemberService {
    List<TimeIntervalType.TimePeriod> countStat(User currentUser, Long startTime, Long endTime, TimeIntervalType intervalType);

    Integer count(Cnd cnd);

    void checkExistByName(String appId, String name, String value) throws Exception;

    void checkExistByName(String appId, String name, String value, MemberInfo oldInfo) throws Exception;

    void checkUsernameExist(String appId, String username) throws Exception;

    void checkUsernameExist(String appId, String username, MemberInfo oldInfo) throws Exception;

    void checkIdNumExist(String appId, String idNum) throws Exception;

    void checkIdNumExist(String appId, String newIdNum, MemberInfo oldInfo) throws Exception;

    void checkPhoneExist(String appId, String phone) throws Exception;

    void checkPhoneExist(String appId, String phone, MemberInfo oldInfo) throws Exception;

    void checkWxMaExist(String appId, String wxAppId) throws Exception;

    void checkWxMaExist(String appId, String wxAppId, MemberInfo oldInfo) throws Exception;

    void checkWxMpExist(String appId, String wxAppId) throws Exception;

    void checkWxMpExist(String appId, String wxAppId, MemberInfo oldInfo) throws Exception;

    void checkWxUnionExist(String appId, String unionId) throws Exception;

    void checkWxUnionExist(String appId, String unionId, MemberInfo oldInfo) throws Exception;

    void checkEmailExist(String appId, String email) throws Exception;

    void checkEmailExist(String appId, String newEmail, MemberInfo oldInfo) throws Exception;

    <T> List<T> setMemberInfoInfo(List<T> list);

    <T> List<T> setMemberInfoInfo(List<T> list, String getIdMethodName, String setMethodName);

    String randomUserName();

    String md5Pwd(String pwd);

    MemberInfo fetch(Long id);

    MemberInfo findByNameValue(String appId, String name, String value);

    MemberInfo findByPhone(String appId, String phone);

    MemberInfo findByEmail(String appId, String email);

    MemberInfo findByIdNum(String appId, String idNum);

    MemberInfo findByUserName(String appId, String num);

    MemberInfo findByWxMa(String appId, String num);

    MemberInfo findByWxMp(String appId, String num);

    MemberInfo findByWxUnion(String appId, String num);

    MemberInfo fullInfo(MemberInfo info);

    MemberInfo registerByPmCode(String app, String phone, String idNum, String pwd, String pmCode, String ip) throws Exception;

    MemberInfo login(String appId, String phone, String pwd, String ip) throws Exception;

    MemberInfo loginByWxMa(String appId, String openId, String ip);

    MemberInfo loginByWxMp(String appId, String openId, String ip);

    MemberInfo loginByWxUnion(String appId, String openId, String ip);

    MemberInfo forget(String app, String phone, String pwd, String pmCode) throws Exception;

    MemberInfo add(@NotNull MemberInfo info, User currentUser, String ip) throws Exception;

    MemberInfo edit(@NotNull MemberInfo info, User currentUser) throws Exception;

    MemberInfo lock(@NotNull User user, Long mid, Boolean lock) throws Exception;

    void remove(@NotNull User user, Long mid) throws Exception;

    void resetPwd(@NotNull User user, Long mid, String newPwd) throws Exception;

    void resetPwd(Long mid, String oldPwd, String newPwd) throws Exception;

    void resetIdNum(String app, Long mid, String newIdNum) throws Exception;

    void resetPhone(String app, Long mid, String newPhone, String code) throws Exception;

    Cnd condition(Cnd cnd, User user, String appId, String phone, String email, String realName, String idNum, String username, Gender gender, String registerIp);

    PageredData<MemberInfo> pager(Integer page, Integer pageSize, Cnd cnd, String orderBy, Boolean orderDesc);

    List<MemberInfo> list(Cnd cnd);

    List<MemberInfo> setListInfo(List<MemberInfo> list);
}