package com.github.funnyzak.onekey.biz.constant;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019-08-12 18:30
 */
public interface BizConstants {

    String ADD_TIME_KEY_NAME = "addTime";

    String INFO_NAME = "info";

    String PROPERTY_NAME = "name";
    String COUNT_NAME = "count";
    String SIZE_NAME = "size";
    String ROW_INDEX_NAME = "rowIndex";
    String ERROR_REASON = "errorReason";

    String REMAIN_NAME = "remain";

    String SIMPLE_INFO_FIELD_NAME_LIST = "id,name";


    interface Resource {
        String NAME = "资源管理";

        String INFO_CAN_EDIT_FIELDS = "name,description,updateUser,updateTime,cover,source,ext,config";

    }

    interface UserConst {
        String NAME = "用户管理";

        /**
         * 简单用户信息信息列表
         */
        String SIMPLE_USER_INFO_FIELD_NAME_LIST = "id,name,realName,nickName,headKey,email,phone";
    }

    interface LabelConst {
        String CAN_EDIT_COLUMN_NAME_LIST = "updateTime,updateUserId,cover,name,description,value,orderId";

        String SIMPLE_INFO_NAME_LIST = "id,name,parentId,value";
    }

    interface DeptConst {
        String NAME = "部门机构";

    }

    /**
     * 数据权限
     */
    interface DataRuleConst {
        String NAME = "数据权限";

        String RULE_CAN_EDIT_COLUMNS = "name,module,ruleType,description";
    }

    interface Assets {
        /**
         * 水印设置默认示例图
         */
        String WATERMARK_CONFIG_EXAMPLE_IMG = "/static/assets/img/watermark.example.jpg";
    }

    /**
     * 资源管理
     */
    interface ResourceConst {
        String NAME = "藏品资源管理";

        String SIMPLE_INFO_FIELD_NAME_LIST = "name,description,cover,source,key";

        /**
         * 资源可编辑的资源
         */
        String RESOURCE_INFO_CAN_EDIT_FIELDS = "name,description,updateUserId,updateTime,cover,source,config";
    }

    interface MemberConst {
        String NAME = "会员信息管理";

        String MEMBER_SIMPLE_INFO_FIELD_NAME_LIST = "id,realName,phone,email";

        String MEMBER_CAN_EDIT_COLUMN_NAME_LIST = "appId,username,realName,idNum,email,phone,nickName,avatar,gender,birthDay,updateTime,updateUserId";
    }


    interface CmsConst {
        String NAME = "内容管理";

        String ARTICLE_CAN_EDIT_COLUMN_NAME_LIST = "title,cateId,subTitle,description,pic,audio,video,updateUserId,updateTime,author,source,viewCount,digCount,published,content,configS";

    }

    interface OpenConst {
        String NAME = "开放平台";

        String CONNECTOR_NAME = "连接器";

        String CONNECTOR_INFO_CAN_EDIT_COLUMN_NAME_LIST = "appId,name,intro,limitApiCountMinute,updateUserId,updateTime,verifyAppSign,verifyTS,whiteIps,permissions,enable";

        String API_MEMBER_INFO_DISPLAY_FIELDS = "username,nickName,idNum,birthDay,phone,registerTime,lastLoginTime,realName,signature,avatar,gender,weUnionId,weAppOpenId,weMpOpenId,email";

        String API_MEMBER_INFO_CAN_EDIT_FIELDS = "nickName,birthDay,realName,signature,avatar,gender";

        String API_TOKEN_DISPLAY_FIELDS = "token,expireTime";

        String API_ATT_RESOURCE_DISPLAY_FIELDS = "key,cate,description,width,height,config";

        /**
         * 开放RequestName集合
         */
        interface RequestHeader {
            /**
             * 用户令牌
             */
            String AUTH_TOKEN_HEADER_NAME = "X-AUTH-TOKEN";
            /**
             * 连接器SecretID
             */
            String SECRET_ID_HEADER_NAME = "X-CS-ID";
            /**
             * 应用自定义 APP ID（和连接器保持一致）
             */
            String APP_ID_HEADER_NAME = "X-APP-ID";
            /**
             * 签名
             */
            String APP_SIGN_HEADER_NAME = "X-APP-SIGN";
            /**
             * 时间戳
             */
            String APP_TS_HEADER_NAME = "X-TIMESTAMP";
            /**
             * 微信小程序APP ID
             */
            String WE_CHAT_MA_APP_ID_HEADER_NAME = "X-MA-APP-ID";
            /**
             * 微信公众号APP ID
             */
            String WE_CHAT_MP_APP_ID_HEADER_NAME = "X-MP-APP-ID";
        }
    }
}