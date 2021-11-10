package com.github.funnyzak.onekey.biz.service.impl;

import com.github.funnyzak.onekey.biz.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019-08-13 17:57
 */
@Component
public class MailServiceImpl implements MailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.fromMail.addr}")
    private String from;

    /**
     * 发送文本邮件
     *
     * @param toAddr
     * @param title
     * @param content
     */
    @Override
    public void sendTextMail(String toAddr, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toAddr);
        message.setSubject(title);
        message.setText(content);

        try {
            javaMailSender.send(message);
            logger.info("Text邮件已经发送。发送内容：{}", message.toString());
        } catch (Exception e) {
            logger.error("发送Text邮件时发生异常！", e);
        }

    }

    /**
     * 发送html邮件
     *
     * @param toAddr
     * @param title
     * @param content
     */
    @Override
    public void sendHtmlMail(String toAddr, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(toAddr);
            helper.setSubject(title);
            helper.setText(content, true);

            javaMailSender.send(message);
            logger.info("html邮件发送成功。发送内容：{}", message.toString());
        } catch (MessagingException e) {
            logger.error("发送html邮件时发生异常！", e);
        }
    }


    /**
     * 发送带附件的邮件
     *
     * @param toAddr
     * @param title
     * @param content
     * @param filePath
     */
    @Override
    public void sendAttachmentsMail(String toAddr, String title, String content, String filePath) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(toAddr);
            helper.setSubject(title);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);

            javaMailSender.send(message);
            logger.info("带附件的邮件已经发送。发送内容：{}", message.toString());
        } catch (MessagingException e) {
            logger.error("发送带附件的邮件时发生异常！", e);
        }
    }


    /**
     * 发送正文中有静态资源（图片）的邮件
     *
     * @param toAddr
     * @param title
     * @param content
     * @param rscPath
     * @param rscId
     */
    @Override
    public void sendInlineResourceMail(String toAddr, String title, String content, String rscPath, String rscId) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(toAddr);
            helper.setSubject(title);
            helper.setText(content, true);

            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);

            javaMailSender.send(message);
            logger.info("嵌入静态资源的邮件已经发送。");
        } catch (MessagingException e) {
            logger.error("发送嵌入静态资源的邮件时发生异常！", e);
        }
    }
}