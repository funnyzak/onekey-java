package com.github.funnyzak.common.utils;

import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/24 3:40 PM
 * @description PdfUtils
 */
public class PdfUtils {
    private static final Logger logger = LoggerFactory.getLogger(PdfUtils.class);

    static PDDocument loadDocument(String pdfPath, String pdfPwd) throws Exception {
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            throw new Exception(pdfPath + "文件不存在。");
        }
        return PDDocument.load(pdfFile, pdfPwd);
    }

    /**
     * PDF转图片
     *
     * @param pdfPath    PDF文件路径
     * @param pdfPwd     PDF密码
     * @param dpi        转换图片的DPI  建议 300
     * @param outPutPath PDF图片输出文件夹路径
     * @throws Exception
     */
    public static void pdfToImage(String pdfPath, String pdfPwd, Integer dpi, String outPutPath) throws Exception {
        if (StringUtils.isNullOrEmpty(outPutPath)) {
            outPutPath = pdfPath.substring(0, pdfPath.length() - 4);
        }

        File outPutDir = new File(outPutPath);
        if (!outPutDir.exists()) {
            outPutDir.mkdirs();
        }

        try {
            PDDocument document = loadDocument(pdfPath, pdfPwd);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, dpi, ImageType.RGB);
                ImageIO.write(image, "JPEG", new File(outPutDir, (page + 1) + ".jpg"));
            }
            document.close();
        } catch (Exception ex) {
            logger.error("PDF转图片失败，错误信息=>", ex);
            throw new Exception("PDF转图片失败，错误：" + ex.getMessage());
        }
    }

    /**
     * PDF文档信息
     *
     * @param pdfPath PDF文件路径
     * @param pdfPwd  PDF密码
     * @return
     * @throws Exception
     */
    public static NutMap documentInfo(String pdfPath, String pdfPwd) throws Exception {
        PDDocument document = loadDocument(pdfPath, pdfPwd);
        PDDocumentInformation information = document.getDocumentInformation();
        NutMap nutMap = new NutMap();
        nutMap.put("author", information.getAuthor());
        nutMap.put("title", information.getTitle());
        nutMap.put("subject", information.getSubject());
        nutMap.put("creator", information.getCreator());
        nutMap.put("modificationDate", information.getModificationDate() != null ? information.getModificationDate().getTime() : null);
        nutMap.put("keywords", information.getKeywords());
        nutMap.put("pageCount", document.getNumberOfPages());
        document.close();
        return nutMap;
    }

    /**
     * PDF增加水印
     *
     * @param originPdfName    原始PDF文件路径
     * @param originPdfPwd     原始PDF密码
     * @param watermarkPdfName 水印PDF，一页
     * @param waterStartPage   水印从第几页开始
     * @param foreground       是否前置水印
     * @param finalPdfName     输出的PDF路径 默认路径为:原始PDF路径_final
     * @throws Exception
     */
    public static void addWatermark(String originPdfName, String originPdfPwd, String watermarkPdfName, Integer waterStartPage, Boolean foreground, String finalPdfName) throws Exception {
        PDDocument realDoc = loadDocument(originPdfName, originPdfPwd);

        if (StringUtils.isNullOrEmpty(finalPdfName)) {
            finalPdfName = originPdfName.substring(0, originPdfName.length() - 4) + "_final" + originPdfName.substring(originPdfName.length() - 4);
        }
        try {
            HashMap<Integer, String> overlayGuide = new HashMap<>();
            for (int i = waterStartPage; i < realDoc.getNumberOfPages(); i++) {
                overlayGuide.put(i, watermarkPdfName);
            }
            Overlay overlay = new Overlay();
            overlay.setInputPDF(realDoc);
            overlay.setOverlayPosition(foreground ? Overlay.Position.FOREGROUND : Overlay.Position.BACKGROUND);
            overlay.overlay(overlayGuide).save(finalPdfName);
            realDoc.close();
            overlay.close();
        } catch (Exception ex) {
            logger.error("PDF增加水印失败，错误信息：", ex);
            throw new Exception("PDF增加水印失败，" + ex.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
//        PdfUtils.pdfToImage("/Users/potato/Downloads/2021test.pdf", null, 300, null);
//        PdfUtils.addWatermark("/Users/potato/Desktop/TMP/2021dangdai2.pdf", null, "/Users/potato/Desktop/TMP/ctaacopyright5.pdf", 2, true, null);
        PdfUtils.pdfToImage("/Users/potato/Desktop/TMP/2021dangdai2_final.pdf", null, 100, null);

    }
}