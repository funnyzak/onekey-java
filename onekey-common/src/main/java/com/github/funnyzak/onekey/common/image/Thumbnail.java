package com.github.funnyzak.onekey.common.image;

import com.github.funnyzak.onekey.common.utils.FileUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/27 1:36 下午
 * @description Thumbnail
 */
public class Thumbnail {
    private static final Logger logger = LoggerFactory.getLogger(Thumbnail.class);

    public static Boolean thumbnail(String sourcePath, String outPath, Integer thumbWidth) throws Exception {
        return thumbnail(sourcePath, outPath, thumbWidth, thumbWidth, 1F);
    }

    public static Boolean thumbnail(String sourcePath, String outPath, Integer thumbWidth, Float outputQuality) throws Exception {
        return thumbnail(sourcePath, outPath, thumbWidth, thumbWidth, outputQuality);
    }

    /**
     * 生成缩略图
     *
     * @param sourcePath    原图文件路径
     * @param outPath       要保存的缩略图路径
     * @param thumbWidth    缩略图宽
     * @param thumbHeight   缩略图高
     * @param outputQuality 输出质量0-1
     * @return
     * @throws Exception
     */
    public static Boolean thumbnail(String sourcePath, String outPath, Integer thumbWidth, Integer thumbHeight, Float outputQuality) throws Exception {
        if (!ImageUtils.isImageFile(sourcePath)) {
            throw new Exception("无效的图片路径");
        }

        try {
            FileUtils.mkDir(outPath.substring(0, outPath.lastIndexOf(File.separator)));

            Thumbnails.of(new File(sourcePath))
                    .size(thumbWidth, thumbHeight)
                    .outputQuality(outputQuality)
                    .toFile(outPath);
            return true;
        } catch (Exception ex) {
            logger.error("缩率图生成失败，文件信息：{}, 错误信息：{}", sourcePath, ex);
            throw new Exception("生成缩略图失败");
        }

    }
}