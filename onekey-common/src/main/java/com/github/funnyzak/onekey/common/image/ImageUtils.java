package com.github.funnyzak.onekey.common.image;

import com.github.funnyzak.onekey.common.utils.FileUtils;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/27 2:39 下午
 * @description ImageUtils
 */
public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static boolean isImageFile(String sourceFile) {
        return isImageFile(new File(sourceFile));
    }

    /**
     * 监测是否为图片格式的文件
     *
     * @param imgFile 图片文件路径
     * @return
     */
    public static boolean isImageFile(File imgFile) {
        return imgFile.exists() &&
                imgFile.isFile() &&
                Arrays
                        .asList("png,jpeg,jpg,bmp,gif".split(","))
                        .stream()
                        .filter(type -> type.equals(FileUtils.getFileExt(imgFile.getName())))
                        .toArray().length > 0;
    }


    public static boolean isImage(String imageLink) {
        return Arrays.asList("png,jpeg,jpg,bmp,gif".split(","))
                .stream()
                .filter(type -> type.equals(FileUtils.getFileExt(imageLink.toLowerCase())))
                .toArray().length > 0;
    }

    /**
     * 获取图片宽高比例
     *
     * @param sourcePath 源图片路径
     * @return
     */
    public static NutMap imageScale(String sourcePath) {
        if (!isImageFile(sourcePath)) {
            return null;
        }
        try {
            BufferedImage sourceImg = ImageIO.read(new FileInputStream(sourcePath));
            NutMap scaleMap = new NutMap();
            scaleMap.addv("width", sourceImg.getWidth());
            scaleMap.addv("height", sourceImg.getHeight());
            return scaleMap;
        } catch (Exception ex) {
            logger.error("获取图片比例失败，错误信息:", ex);
            return null;
        }
    }


    public static BufferedImage readImage(File file) throws IOException {

        return readImage(ImageIO.createImageInputStream(file));
    }

    public static BufferedImage readImage(InputStream stream) throws IOException {

        return readImage(ImageIO.createImageInputStream(stream));
    }

    public static BufferedImage readImage(ImageInputStream input) throws IOException {
        Iterator<?> readers = ImageIO.getImageReaders(input);
        if (readers == null || !readers.hasNext()) {
            return null;
        }

        ImageReader reader = (ImageReader) readers.next();
        reader.setInput(input);

        BufferedImage image;
        try {
            // 尝试读取图片 (包括颜色的转换).
            image = reader.read(0); //RGB

        } catch (IIOException e) {
            // 读取Raster (没有颜色的转换).
            Raster raster = reader.readRaster(0, null);//CMYK
            image = createJPEG4(raster);
        }

        return image;
    }


    private static BufferedImage createJPEG4(Raster raster) {
        int w = raster.getWidth();
        int h = raster.getHeight();
        byte[] rgb = new byte[w * h * 3];

        //彩色空间转换
        float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
        float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
        float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
        float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);

        for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
            float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i],
                    cr = 255 - Cr[i];

            double val = y + 1.402 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);

            val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);

            val = y + 1.772 * (cb - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);
        }


        raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w * 3, 3, new int[]{0, 1, 2}, null);

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }
}