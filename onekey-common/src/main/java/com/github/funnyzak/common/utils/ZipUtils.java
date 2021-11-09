package com.github.funnyzak.common.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/13 6:45 下午
 * @description ZipCompressing
 */
public class ZipUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    private static final int BUFFER_SIZE = 1024 * 100;

    private ZipUtils() {
    }


    /**
     * 私有函数将文件集合压缩成tar包后返回
     *
     * @param files  要压缩的文件集合
     * @param target tar 输出流的目标文件
     * @return File  指定返回的目标文件
     */
    private static File pack(List<File> files, File target) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(target)) {
            try (BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE)) {
                try (TarArchiveOutputStream taos = new TarArchiveOutputStream(bos)) {
                    //解决文件名过长问题
                    taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
                    for (File file : files) {
                        //去掉文件前面的目录
                        taos.putArchiveEntry(new TarArchiveEntry(file, file.getName()));
                        try (FileInputStream fis = new FileInputStream(file)) {
                            IOUtils.copy(fis, taos);
                            taos.closeArchiveEntry();
                        }
                    }
                }
            }
        }
        return target;
    }

    /**
     * 压缩tar文件
     *
     * @param list
     * @param outPutPath
     * @param fileName
     */
    public static File compress(List<File> list, String outPutPath, String fileName) throws IOException {
        File outPutFile = new File(outPutPath + File.separator + fileName + ".tar.gz");
        File tempTar = new File("temp.tar");
        try (FileInputStream fis = new FileInputStream(pack(list, tempTar))) {
            try (BufferedInputStream bis = new BufferedInputStream(fis, BUFFER_SIZE)) {
                try (FileOutputStream fos = new FileOutputStream(outPutFile)) {
                    try (GZIPOutputStream gzp = new GZIPOutputStream(fos)) {
                        int count;
                        byte[] data = new byte[BUFFER_SIZE];
                        while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                            gzp.write(data, 0, count);
                        }
                    }
                }
            }
        }
        try {
            Files.deleteIfExists(tempTar.toPath());
        } catch (IOException e) {
            logger.warn("{}", e);
        }
        return outPutFile;
    }

    public static boolean decompress(String filePath, String outputDir, boolean isDeleted) {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("decompress file not exist.");
            return false;
        }
        try {
            if (filePath.endsWith(".zip")) {
                unZip(file, outputDir);
            }
            if (filePath.endsWith(".tar.gz") || filePath.endsWith(".tgz")) {
                decompressTarGz(file, outputDir);
            }
            if (filePath.endsWith(".tar.bz2")) {
                decompressTarBz2(file, outputDir);
            }
            filterFile(new File(outputDir));
            if (isDeleted) {
                FileUtils.deleteFile(file.getCanonicalPath());
            }
            return true;
        } catch (Exception e) {
            logger.error("decompress occur error.");
        }
        return false;
    }

    public static List<String> unZip(File zipFile, String outputDir) throws Exception {
        if (StringUtils.isNullOrEmpty(outputDir)) {
            outputDir = zipFile.getParent();
        }

        outputDir = outputDir.endsWith(File.separator) ? outputDir : outputDir + File.separator;
        ZipArchiveInputStream is = null;
        List<String> fileNames = new ArrayList<String>();

        try {
            is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            ZipArchiveEntry entry = null;

            while ((entry = is.getNextZipEntry()) != null) {
                fileNames.add(entry.getName());

                if (entry.isDirectory()) {
                    File directory = new File(outputDir, entry.getName());
                    directory.mkdirs();
                } else {
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(outputDir, entry.getName())), BUFFER_SIZE);
                        IOUtils.copy(is, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }

        return fileNames;
    }

    /**
     * 解压 .zip 文件（内置解压，不支持中文）
     *
     * @param file      要解压的zip文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @throws IOException
     */
    public static void unZip2(File file, String outputDir) throws IOException {
        try (ZipFile zipFile = new ZipFile(file, StandardCharsets.UTF_8)) {
            //创建输出目录
            createDirectory(outputDir, null);
            Enumeration<?> enums = zipFile.entries();
            while (enums.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) enums.nextElement();
                if (entry.isDirectory()) {
                    //创建空目录
                    createDirectory(outputDir, entry.getName());
                } else {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        try (OutputStream out = new FileOutputStream(
                                new File(outputDir + File.separator + entry.getName()))) {
                            writeFile(in, out);
                        }
                    }
                }
            }
        }
    }

    public static void decompressTarGz(File file, String outputDir) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(
                new GzipCompressorInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file))))) {
            //创建输出目录
            createDirectory(outputDir, null);
            TarArchiveEntry entry = null;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                //是目录
                if (entry.isDirectory()) {
                    //创建空目录
                    createDirectory(outputDir, entry.getName());
                } else {
                    //是文件
                    try (OutputStream out = new FileOutputStream(
                            new File(outputDir + File.separator + entry.getName()))) {
                        writeFile(tarIn, out);
                    }
                }
            }
        }

    }

    /**
     * 解压缩tar.bz2文件
     *
     * @param file      压缩包文件
     * @param outputDir 目标文件夹
     */
    public static void decompressTarBz2(File file, String outputDir) throws IOException {
        try (TarArchiveInputStream tarIn =
                     new TarArchiveInputStream(
                             new BZip2CompressorInputStream(
                                     new FileInputStream(file)))) {
            createDirectory(outputDir, null);
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    createDirectory(outputDir, entry.getName());
                } else {
                    try (OutputStream out = new FileOutputStream(
                            new File(outputDir + File.separator + entry.getName()))) {
                        writeFile(tarIn, out);
                    }
                }
            }
        }
    }

    /**
     * 写文件
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void writeFile(InputStream in, OutputStream out) throws IOException {
        int length;
        byte[] b = new byte[BUFFER_SIZE];
        while ((length = in.read(b)) != -1) {
            out.write(b, 0, length);
        }
    }

    /**
     * 创建目录
     *
     * @param outputDir
     * @param subDir
     */
    public static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        //子目录不为空
        if (!(subDir == null || subDir.trim().equals(""))) {
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.mkdirs();
        }
    }

    /**
     * 删除Mac压缩再解压产生的 __MACOSX 文件夹和 .开头的其他文件
     *
     * @param filteredFile
     */
    public static void filterFile(File filteredFile) throws IOException {
        if (filteredFile != null) {
            File[] files = filteredFile.listFiles();
            for (File file : files) {
                if (file.getName().startsWith(".") || (file.isDirectory() && file.getName().equals("__MACOSX"))) {
                    FileUtils.deleteFile(file.getCanonicalPath());
                }
            }
        }
    }

    public static void zipFiles(List<String> srcFiles, String targetPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(targetPath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
    }

    public static void zipDir(String dirPath, String zipPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipPath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(dirPath);
        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(File.separator)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + File.separator));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + File.separator + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
