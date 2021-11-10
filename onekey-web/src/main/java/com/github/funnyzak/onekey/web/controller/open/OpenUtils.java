package com.github.funnyzak.onekey.web.controller.open;

import org.nutz.lang.util.NutMap;
import com.github.funnyzak.onekey.bean.member.MemberInfo;
import com.github.funnyzak.onekey.bean.open.TToken;
import com.github.funnyzak.onekey.bean.resource.ResourceInfo;
import com.github.funnyzak.onekey.biz.constant.BizConstants;
import com.github.funnyzak.onekey.common.utils.PUtils;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/20 7:29 下午
 * @description OpenConstants
 */
public class OpenUtils {
    public static class Json {
        public static final String MEMBER_NAME = "member";

        public static final String TOKEN_NAME = "token";

        public static NutMap member(MemberInfo info) {
            return PUtils.entityToNutMap(info, BizConstants.OpenConst.API_MEMBER_INFO_DISPLAY_FIELDS);
        }

        public static NutMap token(TToken info) {
            return PUtils.entityToNutMap(info, BizConstants.OpenConst.API_TOKEN_DISPLAY_FIELDS);
        }

        public static NutMap resource(ResourceInfo info) {
            return PUtils.entityToNutMap(info, BizConstants.OpenConst.API_ATT_RESOURCE_DISPLAY_FIELDS);
        }
    }
}
