package com.github.funnyzak.onekey.biz.service;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019-08-13 17:55
 */
public interface MailService {
    /**
     * 发送纯文本邮件
     *
     * @param toAddr  发送给谁
     * @param title   标题
     * @param content 内容
     */
    void sendTextMail(String toAddr, String title, String content);

    /**
     * 发送 html 邮件
     *
     * @param toAddr
     * @param title
     * @param content 内容（HTML）
     */
    void sendHtmlMail(String toAddr, String title, String content);

    /**
     * 发送待附件的邮件
     *
     * @param toAddr
     * @param title
     * @param content
     * @param filePath 附件地址
     */
    void sendAttachmentsMail(String toAddr, String title, String content, String filePath);

    /**
     * 发送文本中有静态资源（图片）的邮件
     *
     * @param toAddr
     * @param title
     * @param content
     * @param rscPath 资源路径
     * @param rscId   资源id (可能有多个图片)
     */
    void sendInlineResourceMail(String toAddr, String title, String content, String rscPath, String rscId);
}