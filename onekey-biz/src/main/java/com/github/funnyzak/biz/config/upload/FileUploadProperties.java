package com.github.funnyzak.biz.config.upload;

import lombok.Data;
import com.github.funnyzak.common.utils.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


/**
 * @author Leon Yang
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true,
        prefix = "file-upload")
public class FileUploadProperties {

    /**
     * 是否上传到云
     */
    private String cloud;

    /**
     * 本地上传所保存的文件夹爱
     */
    private String localSavePath;

    /**
     * 限制图片文件类型
     */
    private List<String> limitImageType;

    /**
     * 限制其他文件类型
     */
    private List<String> limitOtherType;

    /**
     * 限制其他文件大小 Byte
     */
    private long limitOtherSize;

    /**
     * 限制图片大小 Byte
     */
    private long limitImageSize;

    /**
     * 绑定虚拟主机地址
     */
    private String virtualHost;

    public String getVirtualHost() {
        return StringUtils.isNullOrEmpty(this.virtualHost) ? "" : this.virtualHost;
    }

    /**
     * 绑定虚拟url
     */
    private String virtualPath;

    /**
     * 图片缩率图宽度的几个尺寸
     */
    private List<Integer> imageThumbSize;

    /**
     * 是否生成缩率图
     */
    private boolean thumbImage;

}
