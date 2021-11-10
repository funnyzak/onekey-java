package com.github.funnyzak.onekey.common.utils;

import org.nutz.filepool.NutFilePool;
import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.http.sender.PostSender;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.repo.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片base64处理
 *
 * @author Kerbores
 */
public class Images extends org.nutz.img.Images {
    static NutFilePool pool = new NutFilePool(System.getProperty("java.io.tmpdir"), 3000);// 创建一个临时文件池

    /**
     * 图片缩放裁剪
     *
     * @param src    源图
     * @param scale  缩放比例
     * @param startX 起点X坐标
     * @param startY 起点Y坐标
     * @param endX   终点X坐标
     * @param endY   终点Y坐标
     * @return 目标图片
     * @throws IOException 当读写文件失败时抛出
     */
    public static BufferedImage zoomAndClip(File src, double scale, int startX, int startY, int endX, int endY) throws IOException {
        BufferedImage bfi = read(src);
        bfi = zoomScale(bfi, (int) (bfi.getWidth() * scale), (int) (bfi.getHeight() * scale));// 等比缩放
        File f = pool.createFile("." + Files.getSuffixName(src));
        write(bfi, f);// 写入
        bfi = clipScale(f.getPath(), f.getPath(), new int[]{startX, startY}, new int[]{endX, endY});// 裁剪
        return read(f);
    }

    /**
     * @param src
     * @param scale
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public static BufferedImage zoomAndClip(File src, double scale, Point start, Point end) throws IOException {
        return zoomAndClip(src, scale, start.getX(), start.getY(), end.getX(), end.getY());
    }

    /**
     * 将 BufferedImage 写入文件
     *
     * @param bfi BufferedImage图像
     * @return 写入的文件对象
     */
    public static File toFile(BufferedImage bfi) {
        File f = pool.createFile(".jpg");
        writeJpeg(bfi, f, 1.0f);
        return f;
    }

    /**
     * @param src
     * @param scale
     * @param start
     * @param w
     * @param h
     * @return
     * @throws IOException
     */
    public static BufferedImage zoomAndClip(File src, double scale, Point start, int w, int h) throws IOException {
        return zoomAndClip(src, scale, start.getX(), start.getY(), start.getX() + w, start.getY() + h);
    }

    public static BufferedImage zoomAndClip(String srcPath, double scale, Point start, int w, int h) throws Exception {
        Downloader downloader = new Downloader();
        File f = null;
        downloader.setUrlAndFile(new URL(srcPath), f = pool.createFile(".png"));
        f = downloader.call();
        return zoomAndClip(f, scale, start, w, h);
    }

    /**
     * 坐标点
     *
     * @author wkipy
     */
    public static class Point {
        /**
         * x坐标
         */
        private int x;
        /**
         * y坐标
         */
        private int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public Point(int x, int y) {
            super();
            this.x = x;
            this.y = y;
        }

        public Point(Number x, Number y) {
            super();
            this.x = x.intValue();
            this.y = y.intValue();
        }

        public Point() {
            super();
        }

    }

    /**
     * @param imgStr  图片Base64数据
     * @param outFile 输出文件
     * @return
     */
    public static File GenerateImage(String imgStr, File outFile) {
        if (StringUtils.isNullOrEmpty(imgStr)) // 图像数据为空
            return null;
        if (imgStr.indexOf("base64,") > 0) {
            imgStr = imgStr.substring(imgStr.indexOf("base64,") + 7);
        }
        try {
            // Base64解码
            byte[] b = Base64.decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(outFile);
            out.write(b);
            out.flush();
            out.close();
            return outFile;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 根据base64串生成文件
     *
     * @param imgStr base64串
     * @param outDir 输出目录
     * @param suffix 扩展名
     * @return
     */
    public static File GenerateImage(String imgStr, File outDir, String suffix) {
        File file = new File(outDir.getPath() + File.separator + System.nanoTime() + "." + suffix);
        return GenerateImage(imgStr, file);
    }

    /**
     * 生成png图片
     *
     * @param imgStr base64 image信息串
     * @return 图片文件
     */
    public static File GeneratePngImage(String imgStr) {// 对字节数组字符串进行Base64解码并生成图片
        return GenerateImage(imgStr, new File(System.nanoTime() + ".png"));
    }

    /**
     * 图片转换成base64串
     *
     * @param imgFile 图片文件
     * @return base64 图片信息串
     */
    public static String GetImageStr(File imgFile) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        // 对字节数组Base64编码
        return Base64.encodeToString(data, false);
    }

    public static NutMap baiduOcr(String path, String key) {
        NutMap params = NutMap.NEW();
        Map<String, String> header = new HashMap<String, String>();
        header.put("apikey", key);

        params.put("fromdevice", "pc");
        params.put("clientip", "10.0.1.1");
        params.put("detecttype", "LocateRecognize");
        params.put("languagetype", "CHN_ENG");
        params.put("imagetype", "1");
        params.put("image", GetImageStr(new File(path)));

        Request request = Request.create("http://apis.baidu.com/apistore/idlocr/ocr", METHOD.POST, params, Header.create(header));
        Sender sender = PostSender.create(request);
        Response response = sender.send();
        String info = response.getContent();

        return Lang.map(info);
    }

    private static final Log log = Logs.get();


    /**
     * 几种常见的图片格式
     */
    public static String IMAGE_TYPE_GIF = "gif";// 图形交换格式
    public static String IMAGE_TYPE_JPG = "jpg";// 联合照片专家组
    public static String IMAGE_TYPE_JPEG = "jpeg";// 联合照片专家组
    public static String IMAGE_TYPE_BMP = "bmp";// 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
    public static String IMAGE_TYPE_PNG = "png";// 可移植网络图形
    public static String IMAGE_TYPE_PSD = "psd";// Photoshop的专用格式Photoshop

    /**
     * 缩放图像（按比例缩放）
     *
     * @param srcImageFile 源图像文件地址
     * @param result       缩放后的图像地址
     * @param scale        缩放比例
     * @param flag         缩放选择:true 放大; false 缩小;
     */
    public final static void scale(String srcImageFile, String result,
                                   int scale, boolean flag) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件
            int width = src.getWidth(); // 得到源图宽

            int height = src.getHeight(); // 得到源图长

            if (flag) {// 放大
                width = width * scale;
                height = height * scale;
            } else {// 缩小
                width = width / scale;
                height = height / scale;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_DEFAULT);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图

            g.dispose();
            ImageIO.write(tag, "JPEG", new File(result));// 输出到文件流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缩放图像（按高度和宽度缩放）
     *
     * @param srcImageFile 源图像文件地址
     * @param result       缩放后的图像地址
     * @param height       缩放后的高度
     * @param width        缩放后的宽度
     * @param bb           比例不对时是否需要补白：true为补白; false为不补白;
     */
    public final static void scale2(String srcImageFile, String result, int width, int height, boolean bb) {
        try {
            double ratio = 0.0; // 缩放比例
            File f = new File(srcImageFile);
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            // 计算比例
            if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
                if (bi.getHeight() > bi.getWidth()) {
                    ratio = (new Integer(height)).doubleValue()
                            / bi.getHeight();
                } else {
                    ratio = (new Integer(width)).doubleValue() / bi.getWidth();
                }
                AffineTransformOp op = new AffineTransformOp(AffineTransform
                        .getScaleInstance(ratio, ratio), null);
                itemp = op.filter(bi, null);
            }
            if (bb) {//补白
                BufferedImage image = new BufferedImage(width, height,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, width, height);
                if (width == itemp.getWidth(null))
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
                            itemp.getWidth(null), itemp.getHeight(null),
                            Color.white, null);
                else
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
                            itemp.getWidth(null), itemp.getHeight(null),
                            Color.white, null);
                g.dispose();
                itemp = image;
            }
            ImageIO.write((BufferedImage) itemp, "JPEG", new File(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缩放图像（按高度和宽度缩放）
     *
     * @param srcImageFile 源图像文件地址
     * @param result       缩放后的图像地址
     * @param height       缩放后的高度
     * @param width        缩放后的宽度
     * @param bb           比例不对时是否需要补白：true为补白; false为不补白;
     */
    public final static void scale3(String srcImageFile, String result, int width, int height, boolean bb) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件
            //int _width = src.getWidth(); // 得到源图宽

            //int _height = src.getHeight(); // 得到源图长


            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_DEFAULT);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图

            g.dispose();
            ImageIO.write(tag, "JPEG", new File(result));// 输出到文件流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImageFile 源图像地址
     * @param result       切片后的图像地址
     * @param x            目标切片起点坐标X
     * @param y            目标切片起点坐标Y
     * @param width        目标切片宽度
     * @param height       目标切片高度
     */
    public final static void cut(String srcImageFile, String result,
                                 int x, int y, int width, int height) {
        try {
            // 读取源图像

            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight(); // 源图宽度
            int srcHeight = bi.getWidth(); // 源图高度
            if (srcWidth > 0 && srcHeight > 0) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight,
                        Image.SCALE_DEFAULT);
                // 四个参数分别为图像起点坐标和宽高
                // 即: CropImageFilter(int x,int y,int width,int height)
                ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
                Image img = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(image.getSource(),
                                cropFilter));
                BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图

                g.dispose();
                // 输出为文件

                ImageIO.write(tag, "JPEG", new File(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像切割（指定切片的行数和列数）
     *
     * @param srcImageFile 源图像地址
     * @param descDir      切片目标文件夹
     * @param rows         目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols         目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public final static void cut2(String srcImageFile, String descDir,
                                  int rows, int cols) {
        try {
            if (rows <= 0 || rows > 20) rows = 2; // 切片行数
            if (cols <= 0 || cols > 20) cols = 2; // 切片列数
            // 读取源图像

            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight(); // 源图宽度
            int srcHeight = bi.getWidth(); // 源图高度
            if (srcWidth > 0 && srcHeight > 0) {
                Image img;
                ImageFilter cropFilter;
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                int destWidth = srcWidth; // 每张切片的宽度

                int destHeight = srcHeight; // 每张切片的高度

                // 计算切片的宽度和高度
                if (srcWidth % cols == 0) {
                    destWidth = srcWidth / cols;
                } else {
                    destWidth = (int) Math.floor(srcWidth / cols) + 1;
                }
                if (srcHeight % rows == 0) {
                    destHeight = srcHeight / rows;
                } else {
                    destHeight = (int) Math.floor(srcWidth / rows) + 1;
                }
                // 循环建立切片
                // 改进的想法:是否可用多线程加快切割速度
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                        // 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight,
                                destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                        cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null); // 绘制缩小后的图

                        g.dispose();
                        // 输出为文件

                        ImageIO.write(tag, "JPEG", new File(descDir
                                + "_r" + i + "_c" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像切割（指定切片的宽度和高度）
     *
     * @param srcImageFile 源图像地址
     * @param descDir      切片目标文件夹
     * @param destWidth    目标切片宽度。默认200
     * @param destHeight   目标切片高度。默认150
     */
    public final static void cut3(String srcImageFile, String descDir,
                                  int destWidth, int destHeight) {
        try {
            if (destWidth <= 0) destWidth = 200; // 切片宽度
            if (destHeight <= 0) destHeight = 150; // 切片高度
            // 读取源图像

            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight(); // 源图宽度
            int srcHeight = bi.getWidth(); // 源图高度
            if (srcWidth > destWidth && srcHeight > destHeight) {
                Image img;
                ImageFilter cropFilter;
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                int cols = 0; // 切片横向数量
                int rows = 0; // 切片纵向数量
                // 计算切片的横向和纵向数量
                if (srcWidth % destWidth == 0) {
                    cols = srcWidth / destWidth;
                } else {
                    cols = (int) Math.floor(srcWidth / destWidth) + 1;
                }
                if (srcHeight % destHeight == 0) {
                    rows = srcHeight / destHeight;
                } else {
                    rows = (int) Math.floor(srcHeight / destHeight) + 1;
                }
                // 循环建立切片
                // 改进的想法:是否可用多线程加快切割速度
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                        // 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight,
                                destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                        cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null); // 绘制缩小后的图

                        g.dispose();
                        // 输出为文件

                        ImageIO.write(tag, "JPEG", new File(descDir
                                + "_r" + i + "_c" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像类型转换：GIF->JPG、GIF->PNG、PNG->JPG、PNG->GIF(X)、BMP->PNG
     *
     * @param srcImageFile  源图像地址
     * @param formatName    包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destImageFile 目标图像地址
     */
    public final static void convert(String srcImageFile, String formatName, String destImageFile) {
        try {
            File f = new File(srcImageFile);
            f.canRead();
            f.canWrite();
            BufferedImage src = ImageIO.read(f);
            ImageIO.write(src, formatName, new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 彩色转为黑白
     *
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     */
    public final static void gray(String srcImageFile, String destImageFile) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            src = op.filter(src, null);
            ImageIO.write(src, "JPEG", new File(destImageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给图片添加文字水印
     *
     * @param pressText     水印文字
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param fontName      水印的字体名称
     * @param fontStyle     水印的字体样式
     * @param color         水印的字体颜色
     * @param fontSize      水印的字体大小
     * @param x             修正值
     * @param y             修正值
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public final static void pressText(String pressText,
                                       String srcImageFile, String destImageFile, String fontName,
                                       int fontStyle, Color color, int fontSize, int x,
                                       int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 在指定坐标绘制水印文字

            g.drawString(pressText, (width - (getLength(pressText) * fontSize))
                    / 2 + x, (height - fontSize) / 2 + y);
            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));// 输出到文件流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给图片添加文字水印
     *
     * @param pressText     水印文字
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param fontName      字体名称
     * @param fontStyle     字体样式
     * @param color         字体颜色
     * @param fontSize      字体大小
     * @param x             修正值
     * @param y             修正值
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public final static void pressText2(String pressText, String srcImageFile, String destImageFile,
                                        String fontName, int fontStyle, Color color, int fontSize, int x,
                                        int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 在指定坐标绘制水印文字

            g.drawString(pressText, (width - (getLength(pressText) * fontSize))
                    / 2 + x, (height - fontSize) / 2 + y);
            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给图片添加图片水印
     *
     * @param pressImg      水印图片
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param x             修正值。 默认在中间
     * @param y             修正值。 默认在中间
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public final static void pressImage(String pressImg, String srcImageFile, String destImageFile,
                                        int x, int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);
            // 水印文件
            Image src_biao = ImageIO.read(new File(pressImg));
            int wideth_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            g.drawImage(src_biao, (wideth - wideth_biao) / 2,
                    (height - height_biao) / 2, wideth_biao, height_biao, null);
            // 水印文件结束
            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 计算text的长度（一个中文算两个字符）
     *
     * @param text
     * @return
     */
    public final static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if ((text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }

    /**
     * <获取图片宽度>
     * add by jiang_yanyan 2015-01-04
     *
     * @param file 图片文件
     * @return 宽度
     */
    public static int getImgWidth(File file) {
        InputStream is = null;
        BufferedImage src = null;
        int ret = -1;
        try {
            is = new FileInputStream(file);
            src = javax.imageio.ImageIO.read(is);
            ret = src.getWidth(null); // 得到源图宽
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * <获取图片高度>
     * add by jiang_yanyan 2015-01-04
     *
     * @param file 图片文件
     * @return 高度
     */
    public static int getImgHeight(File file) {
        InputStream is = null;
        BufferedImage src = null;
        int ret = -1;
        try {
            is = new FileInputStream(file);
            src = javax.imageio.ImageIO.read(is);
            ret = src.getHeight(null); // 得到源图高
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
