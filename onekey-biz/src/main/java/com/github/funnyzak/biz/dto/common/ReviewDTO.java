package com.github.funnyzak.biz.dto.common;

import lombok.Data;
import com.github.funnyzak.bean.enums.ReviewAction;
import com.github.funnyzak.bean.enums.ReviewStatus;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/3/18 2:45 下午
 * @description ReviewDTO
 */
@Data
public class ReviewDTO {
    /**
     * 业务ID
     */
    private Long id;

    /**
     * 审核动作
     */
    private ReviewAction action;

    /**
     * 审核状态
     */
    private ReviewStatus status;

    /**
     * 审核携带附件信息
     */
    private Object data;
}