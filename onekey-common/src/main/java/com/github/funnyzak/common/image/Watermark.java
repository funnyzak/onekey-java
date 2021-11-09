package com.github.funnyzak.common.image;

import com.github.funnyzak.common.utils.FileUtils;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/27 1:36 下午
 * @description Watermark
 */
public class Watermark {
    private static final Logger logger = LoggerFactory.getLogger(Watermark.class);

    /**
     * 输出图片水印图片
     *
     * @param sourcePath        原图图片路径
     * @param watermarkPath     水印图片路径
     * @param outputPath        输出加水印的图片
     * @param position          水印位置1-9
     * @param horizontalPadding 横向间距
     * @param verticalPadding   纵向间距
     * @param opacity           水印的不透明度 0-1
     * @throws Exception
     */
    public static void imgWatermark(String sourcePath, String watermarkPath, String outputPath, Integer position, Integer horizontalPadding, Integer verticalPadding, Float opacity) throws Exception {

        try {
            Image srcImg = ImageIO.read(new File(sourcePath));
            int srcImageWidth = srcImg.getWidth(null);
            int srcImageHeight = srcImg.getHeight(null);

            BufferedImage bufferedImage = new BufferedImage(srcImageWidth,
                    srcImageHeight, BufferedImage.TYPE_INT_RGB);

            // 得到画笔对象
            Graphics2D graphics2D = bufferedImage.createGraphics();
            // 设置对线段的锯齿状边缘处理
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            graphics2D.drawImage(srcImg.getScaledInstance(srcImageWidth, srcImageHeight, Image.SCALE_SMOOTH), 0, 0, null);

            Image watermarkImg = ImageIO.read(new File(watermarkPath));
            NutMap watermarkPos = calcWatermarkPosition(false, position, srcImageWidth, srcImageHeight, watermarkImg.getWidth(null), watermarkImg.getHeight(null), horizontalPadding, verticalPadding);
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
            graphics2D.drawImage(watermarkImg, watermarkPos.getInt("x"), watermarkPos.getInt("y"), null);
            graphics2D.dispose();

            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            ImageIO.write(bufferedImage, FileUtils.getFileExt(sourcePath), fileOutputStream);
            fileOutputStream.close();
        } catch (Exception ex) {
            logger.error("添加图片水印失败，错误信息：", ex);
            throw new Exception("添加图片水印失败");
        }
    }

    public static void textWatermark(String sourcePath, String watermarkText, String outputPath, Integer position, String fontColor, String fontName, int fontStyle, Integer fontSize, Integer horizontalPadding, Integer verticalPadding, Float opacity) throws Exception {
        try {
            Image srcImg = ImageIO.read(new File(sourcePath));
            int srcImageWidth = srcImg.getWidth(null);
            int srcImageHeight = srcImg.getHeight(null);

            BufferedImage bufferedImage = new BufferedImage(srcImageWidth,
                    srcImageHeight, BufferedImage.TYPE_INT_RGB);

            // 得到画笔对象
            Graphics2D graphics2D = bufferedImage.createGraphics();
            // 设置对线段的锯齿状边缘处理
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            graphics2D.drawImage(srcImg.getScaledInstance(srcImageWidth, srcImageHeight, Image.SCALE_SMOOTH), 0, 0, null);

            //设置水印文字（设置水印字体样式、粗细、大小）
            Font font = new Font(fontName, fontStyle, fontSize);
            graphics2D.setFont(font);
            //设置水印颜色
            graphics2D.setColor(Color.decode(fontColor));

            NutMap watermarkPos = calcWatermarkPosition(true, position, srcImageWidth, srcImageHeight, getTextWidth(watermarkText, fontSize), fontSize, horizontalPadding, verticalPadding);

            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
            graphics2D.drawString(watermarkText, watermarkPos.getInt("x"), watermarkPos.getInt("y"));
            graphics2D.dispose();

            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            ImageIO.write(bufferedImage, FileUtils.getFileExt(sourcePath), fileOutputStream);
            fileOutputStream.close();
        } catch (Exception ex) {
            logger.error("添加文字水印失败，错误信息：", ex);
            throw new Exception("添加图片水印失败");
        }
    }

    /**
     * 计算水印文本长度
     * <p>
     * 中文长度即文本长度 2、英文长度为文本长度二分之一
     *
     * @param text
     * @return
     */
    private static int getTextWidth(String text, Integer fontSize) {
        //水印文字长度
        int length = text.length();

        for (int i = 0; i < text.length(); i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.getBytes().length > 1) {
                length++;
            }
        }
        length = length % 2 == 0 ? length / 2 : length / 2 + 1;
        return length * fontSize;
    }

    /**
     * 计算水印的起始位置
     *
     * @param position          1-9 原图9个位置
     * @param imageWidth        画布宽
     * @param imageHeight       画布高
     * @param watermarkWidth    水印宽
     * @param watermarkHeight   水印高
     * @param horizontalPadding 横向间距
     * @param verticalPadding   纵向间距
     * @return
     */
    public static NutMap calcWatermarkPosition(Boolean isTextWatermark, Integer position, Integer imageWidth, Integer imageHeight, Integer watermarkWidth, Integer watermarkHeight, Integer horizontalPadding, Integer verticalPadding) {
        Integer xPos = 0, yPos = 0;
        switch (position) {
            case 1:
                xPos = horizontalPadding;
                yPos = verticalPadding;
                break;
            case 2:
                xPos = imageWidth / 2 - watermarkWidth / 2 + horizontalPadding;
                yPos = verticalPadding;
                break;
            case 3:
                xPos = imageWidth - watermarkWidth - horizontalPadding;
                yPos = verticalPadding;
                break;
            case 4:
                xPos = horizontalPadding;
                yPos = imageHeight / 2 - watermarkHeight / 2 + verticalPadding;
                break;
            case 5:
                xPos = imageWidth / 2 - watermarkWidth / 2 + horizontalPadding;
                yPos = imageHeight / 2 - watermarkHeight / 2 + verticalPadding;
                break;
            case 6:
                xPos = imageWidth - watermarkWidth - horizontalPadding;
                yPos = imageHeight / 2 - watermarkHeight / 2 + verticalPadding;
                break;
            case 7:
                xPos = horizontalPadding;
                yPos = imageHeight - watermarkHeight - verticalPadding;
                break;
            case 8:
                xPos = imageWidth / 2 - watermarkWidth / 2 + horizontalPadding;
                yPos = imageHeight - watermarkHeight - verticalPadding;
                break;
            case 9:
                xPos = imageWidth - watermarkWidth - horizontalPadding;
                yPos = imageHeight - watermarkHeight - verticalPadding;
                break;
        }
        if (isTextWatermark) {
            yPos = yPos + (yPos < watermarkHeight ? (watermarkHeight / 2 + watermarkHeight) : watermarkHeight / 2);
        }
        NutMap nutMap = new NutMap();
        nutMap.addv("x", xPos);
        nutMap.addv("y", yPos);
        return nutMap;
    }
}