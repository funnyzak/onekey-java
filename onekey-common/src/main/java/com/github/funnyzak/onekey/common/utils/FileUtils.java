package com.github.funnyzak.onekey.common.utils;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;


public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 清除SVN
     *
     * @param dir 待清除的目录
     * @return 清除成功状态标识
     */
    public static boolean cleanSvn(File dir) {
        try {
            Files.cleanAllFolderInSubFolderes(dir, ".svn");
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 统计文件或者目录下的java代码的行数
     *
     * @param file 文件或者目录
     * @return java代码行数
     */
    public static long countJAVACodeLines(File file) {
        return countLines(file, ".java");
    }

    public static void main(String[] args) {
        cleanSvn(new File("H:\\代码备份"));
        System.err.println("代码备份目录大小:" + formatFileSize(getDirSize(new File("H:\\代码备份"))));
        System.err.println("JAVA代码行数:" + countJAVACodeLines(new File("H:\\代码备份")));
        System.err.println("HTML代码行数:" + countLines(new File("H:\\代码备份"), ".html"));
        System.err.println("js代码行数:" + countLines(new File("H:\\代码备份"), ".js"));
        System.err.println("XML代码行数:" + countLines(new File("H:\\代码备份"), ".xml"));
        System.err.println("OC代码行数:" + countLines(new File("H:\\代码备份"), ".m"));
        System.err.println("VM代码行数:" + countLines(new File("H:\\代码备份"), ".vm"));
        System.err.println("SQL配置代码行数:" + countLines(new File("H:\\代码备份"), ".properties"));
    }

    public static double getDirSize(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                double size = file.length();
                return size;
            }
        } else {
            return 0.0;
        }
    }

    /**
     * 转换文件大小
     *
     * @param length
     * @return
     */
    public static String formatFileSize(double length) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (length < 1024) {
            fileSizeString = df.format(length) + "B";
        } else if (length < 1048576) {
            fileSizeString = df.format(length / 1024) + "K";
        } else if (length < 1073741824) {
            fileSizeString = df.format(length / 1048576) + "M";
        } else {
            fileSizeString = df.format(length / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 计算文件行数
     *
     * @param file 文件(非目录类型)
     * @return 行数
     */
    public static long countLine(File file) {
        long target = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            if (reader != null) {
                while (reader.readLine() != null) {
                    target++;
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return target;
    }

    /**
     * 统计文件或者目录下的指定类型文件的行数
     *
     * @param file 文件或者目录
     * @param suf  扩展名
     * @return 行数
     */
    public static long countLines(File file, String suf) {
        long target = 0;
        if (file.isFile() && file.getName().endsWith(suf)) {
            return countLine(file);
        } else if (file.isFile()) {
            return 0;
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                target += countLines(f, suf);
            }
        }
        return target;
    }

    /**
     * 快速查找
     *
     * @param dir  基目录
     * @param name 待查找文件名
     * @return 文件
     */
    public static File fastFindFile(File dir, String name) {
        return fastFindFile(dir, name, 1);
    }

    // /**
    // * 获取APK版本信息
    // *
    // * @param filePath
    // * 文件路径
    // * @return APK内置版本信息
    // */
    // public static String getApkVersionInfo(String filePath) {
    // try {
    // return GetApkInfo.getApkInfoByFilePath(filePath).getVersionName();
    // } catch (IOException e) {
    // logger.error(e.getMwssage());
    // return null;
    // }
    // }

    /**
     * 快速查找文件
     *
     * @param dir    基目录
     * @param name   文件名
     * @param method 查找方法 1 全等查询 2模糊查找 3 忽略大小写全等 4忽略大小写模糊
     * @return 文件
     */
    public static File fastFindFile(File dir, String name, int method) {
        File target = null;
        File[] dirs = Files.dirs(dir);// 获取目录
        File[] files = Files.files(dir, name);// 获取文件
        // 优先扫描文件
        if (files != null) {
            for (File file : files) {
                if (method == 1 ? Strings.equals(file.getName(), name) : method == 2 ? file.getName().endsWith(name) : method == 3 ? Strings.equals(file.getName().toUpperCase(),
                        name.toUpperCase()) : file.getName().toUpperCase().endsWith(name.toUpperCase())) {
                    return file;
                }
            }
        }
        // 然后扫目录
        if (dirs != null) {
            for (File file : dirs) {
                target = findFile(file, name);
                if (target != null) {
                    return target;
                }
            }
        }
        return target;
    }

    public static File fastFindFile(String dir, String name) {
        return fastFindFile(new File(dir), name, 1);
    }

    /**
     * 快速查找
     *
     * @param dir  基目录
     * @param name 待查找文件名
     * @return 文件
     */
    public static File fastFindFileLikeName(File dir, String name) {
        return fastFindFile(dir, name, 2);
    }

    /**
     * 基本实现 文件查找
     *
     * @param dir  查找的开始位置
     * @param name 查找的文件的名字
     * @return 文件
     */
    public static File findFile(File dir, String name) {
        File target = null;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && Strings.equals(file.getName(), name)) {
                    return file;
                } else if (file.isDirectory()) {
                    target = findFile(file, name);
                    if (target != null) {
                        return target;
                    }
                }
            }
        }
        return target;
    }

    public static boolean saveFile(byte[] file, String filePath, String fileName) {
        try {
            File targetFile = new File(filePath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(new String((filePath + fileName).getBytes(StandardCharsets.UTF_8)));
            out.write(file);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            logger.info("保存文件失败");
            return false;
        }

    }

    public static void deleteFile(String filePath) {
        File targetFile = new File(filePath);
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static boolean mkDir(String path) {
        try {
            File targetFile = new File(path);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            return true;
        } catch (Exception e) {
            logger.info("创建文件夹失败");
            return false;
        }

    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getFileExt(String filename) {
        String[] array = filename.trim().split("\\.");
        Lang.reverse(array);
        return array[0];
    }

    /**
     * 获取文件夹下所有文件，包含子目录
     */
    public static List<File> getDirFiles(String dirPath) {
        return getDirFiles(dirPath, null);
    }

    /**
     * 获取文件夹下所有文件，包含子目录
     */
    public static List<File> getDirFiles(String dirPath, List<File> fileList) {
        if (fileList == null) {
            fileList = new ArrayList<>();
        }

        File dir = new File(dirPath);
        File[] files = dir.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory() && !files[i].getName().equals("__MACOSX")) {
                    getDirFiles(files[i].getAbsolutePath(), fileList);
                } else {
                    fileList.add(files[i]);
                }
            }
        }
        return fileList;
    }


    /**
     * 寻找指定目录下，具有指定后缀名的所有文件。
     *
     * @param fileExt : 文件后缀名,如 jpg,png
     * @param dirPath : 当前使用的文件目录
     */
    public static List<File> searchFiles(String fileExt, String dirPath) {
        return searchFiles(fileExt, dirPath, null);
    }

    /**
     * 寻找指定目录下，具有指定后缀名的所有文件。
     *
     * @param fileExt  : 文件后缀名,如 jpg,png
     * @param dirPath  : 当前使用的文件目录
     * @param fileList ：当前符合条件的文件列表
     */
    public static List<File> searchFiles(String fileExt, String dirPath, List<File> fileList) {

        if (fileList == null) {
            fileList = new ArrayList<>();
        }

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return fileList;
        }

        for (File file : dir.listFiles()) {
            if (file.isDirectory() && !file.getName().equals("__MACOSX")) {
                searchFiles(fileExt, file.getAbsolutePath(), fileList);
            } else {
                String ext = getFileExt(file.getName()).toLowerCase();
                if (Lang.contains(fileExt.split(","), ext)) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * 移动文件
     */
    public boolean move(String originPath, String targetPath) {
        try {
            return new File(originPath).renameTo(new File(targetPath));
        } catch (Exception ex) {
            logger.error("移动文件失败，原因：", ex);
            return false;
        }
    }

    /**
     * 获取文件content-type
     *
     * @param fileExt 如：jpg
     * @return
     */
    public static String getContentType(String fileExt) {
        switch (fileExt) {
            case "json":
                return "application/json";
            case "jpeg":
                return "image/jpeg";
            case "jpg":
                return "image/jpeg";
            case "js":
                return "application/x-javascript";
            case "jsp":
                return "text/html";
            case "gif":
                return "image/gif";
            case "htm":
                return "text/html";
            case "html":
                return "text/html";
            case "asf":
                return "video/x-ms-asf";
            case "avi":
                return "video/avi";
            case "bmp":
                return "image/bmp";
            case "asp":
                return "text/asp";
            case "wma":
                return "audio/x-ms-wma";
            case "wav":
                return "audio/wav";
            case "wmv":
                return "video/x-ms-wmv";
            case "ra":
                return "audio/vnd.rn-realaudio";
            case "ram":
                return "audio/x-pn-realaudio";
            case "rm":
                return "application/vnd.rn-realmedia";
            case "rmvb":
                return "application/vnd.rn-realmedia-vbr";
            case "xhtml":
                return "text/html";
            case "png":
                return "image/png";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "tif":
                return "image/tiff";
            case "tiff":
                return "image/tiff";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlw":
                return "application/x-xlw";
            case "xml":
                return "text/xml";
            case "xpl":
                return "audio/scpls";
            case "swf":
                return "application/x-shockwave-flash";
            case "torrent":
                return "application/x-bittorrent";
            case "dll":
                return "application/x-msdownload";
            case "asa":
                return "text/asa";
            case "asx":
                return "video/x-ms-asf";
            case "au":
                return "audio/basic";
            case "css":
                return "text/css";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "exe":
                return "application/x-msdownload";
            case "mp1":
                return "audio/mp1";
            case "mp2":
                return "audio/mp2";
            case "mp2v":
                return "video/mpeg";
            case "mp3":
                return "audio/mp3";
            case "mp4":
                return "video/mp4";
            case "mpa":
                return "video/x-mpg";
            case "mpd":
                return "application/vnd.ms-project";
            case "mpe":
                return "video/x-mpeg";
            case "mpeg":
                return "video/mpg";
            case "mpg":
                return "video/mpg";
            case "mpga":
                return "audio/rn-mpeg";
            case "mpp":
                return "application/vnd.ms-project";
            case "mps":
                return "video/x-mpeg";
            case "mpt":
                return "application/vnd.ms-project";
            case "mpv":
                return "video/mpg";
            case "mpv2":
                return "video/mpeg";
            case "wml":
                return "text/vnd.wap.wml";
            case "wsdl":
                return "text/xml";
            case "xsd":
                return "text/xml";
            case "xsl":
                return "text/xml";
            case "xslt":
                return "text/xml";
            case "htc":
                return "text/x-component";
            case "mdb":
                return "application/msaccess";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";

            case "*":
                return "application/octet-stream";
            case "001":
                return "application/x-001";
            case "301":
                return "application/x-301";
            case "323":
                return "text/h323";
            case "906":
                return "application/x-906";
            case "907":
                return "drawing/907";
            case "a11":
                return "application/x-a11";
            case "acp":
                return "audio/x-mei-aac";
            case "ai":
                return "application/postscript";
            case "aif":
                return "audio/aiff";
            case "aifc":
                return "audio/aiff";
            case "aiff":
                return "audio/aiff";
            case "anv":
                return "application/x-anv";
            case "awf":
                return "application/vnd.adobe.workflow";
            case "biz":
                return "text/xml";
            case "bot":
                return "application/x-bot";
            case "c4t":
                return "application/x-c4t";
            case "c90":
                return "application/x-c90";
            case "cal":
                return "application/x-cals";
            case "cat":
                return "application/vnd.ms-pki.seccat";
            case "cdf":
                return "application/x-netcdf";
            case "cdr":
                return "application/x-cdr";
            case "cel":
                return "application/x-cel";
            case "cer":
                return "application/x-x509-ca-cert";
            case "cg4":
                return "application/x-g4";
            case "cgm":
                return "application/x-cgm";
            case "cit":
                return "application/x-cit";
            case "class":
                return "java/*";
            case "cml":
                return "text/xml";
            case "cmp":
                return "application/x-cmp";
            case "cmx":
                return "application/x-cmx";
            case "cot":
                return "application/x-cot";
            case "crl":
                return "application/pkix-crl";
            case "crt":
                return "application/x-x509-ca-cert";
            case "csi":
                return "application/x-csi";
            case "cut":
                return "application/x-cut";
            case "dbf":
                return "application/x-dbf";
            case "dbm":
                return "application/x-dbm";
            case "dbx":
                return "application/x-dbx";
            case "dcd":
                return "text/xml";
            case "dcx":
                return "application/x-dcx";
            case "der":
                return "application/x-x509-ca-cert";
            case "dgn":
                return "application/x-dgn";
            case "dib":
                return "application/x-dib";
            case "dot":
                return "application/msword";
            case "drw":
                return "application/x-drw";
            case "dtd":
                return "text/xml";
            case "dwf":
                return "application/x-dwf";
            case "dwg":
                return "application/x-dwg";
            case "dxb":
                return "application/x-dxb";
            case "dxf":
                return "application/x-dxf";
            case "edn":
                return "application/vnd.adobe.edn";
            case "emf":
                return "application/x-emf";
            case "eml":
                return "message/rfc822";
            case "ent":
                return "text/xml";
            case "epi":
                return "application/x-epi";
            case "eps":
                return "application/x-ps";
            case "etd":
                return "application/x-ebx";
            case "fax":
                return "image/fax";
            case "fdf":
                return "application/vnd.fdf";
            case "fif":
                return "application/fractals";
            case "fo":
                return "text/xml";
            case "frm":
                return "application/x-frm";
            case "g4":
                return "application/x-g4";
            case "gbr":
                return "application/x-gbr";
            case "gcd":
                return "application/x-gcd";

            case "gl2":
                return "application/x-gl2";
            case "gp4":
                return "application/x-gp4";
            case "hgl":
                return "application/x-hgl";
            case "hmr":
                return "application/x-hmr";
            case "hpg":
                return "application/x-hpgl";
            case "hpl":
                return "application/x-hpl";
            case "hqx":
                return "application/mac-binhex40";
            case "hrf":
                return "application/x-hrf";
            case "hta":
                return "application/hta";
            case "htt":
                return "text/webviewhtml";
            case "htx":
                return "text/html";
            case "icb":
                return "application/x-icb";
            case "ico":
                return "application/x-ico";
            case "iff":
                return "application/x-iff";
            case "ig4":
                return "application/x-g4";
            case "igs":
                return "application/x-igs";
            case "iii":
                return "application/x-iphone";
            case "img":
                return "application/x-img";
            case "ins":
                return "application/x-internet-signup";
            case "isp":
                return "application/x-internet-signup";
            case "IVF":
                return "video/x-ivf";
            case "java":
                return "java/*";
            case "jfif":
                return "image/jpeg";
            case "jpe":
                return "application/x-jpe";
            case "la1":
                return "audio/x-liquid-file";
            case "lar":
                return "application/x-laplayer-reg";
            case "latex":
                return "application/x-latex";
            case "lavs":
                return "audio/x-liquid-secure";
            case "lbm":
                return "application/x-lbm";
            case "lmsff":
                return "audio/x-la-lms";
            case "ls":
                return "application/x-javascript";
            case "ltr":
                return "application/x-ltr";
            case "m1v":
                return "video/x-mpeg";
            case "m2v":
                return "video/x-mpeg";
            case "m3u":
                return "audio/mpegurl";
            case "m4e":
                return "video/mpeg4";
            case "mac":
                return "application/x-mac";
            case "man":
                return "application/x-troff-man";
            case "math":
                return "text/xml";
            case "mfp":
                return "application/x-shockwave-flash";
            case "mht":
                return "message/rfc822";
            case "mhtml":
                return "message/rfc822";
            case "mi":
                return "application/x-mi";
            case "mid":
                return "audio/mid";
            case "midi":
                return "audio/mid";
            case "mil":
                return "application/x-mil";
            case "mml":
                return "text/xml";
            case "mnd":
                return "audio/x-musicnet-download";
            case "mns":
                return "audio/x-musicnet-stream";
            case "mocha":
                return "application/x-javascript";
            case "movie":
                return "video/x-sgi-movie";
            case "mpw":
                return "application/vnd.ms-project";
            case "mpx":
                return "application/vnd.ms-project";
            case "mtx":
                return "text/xml";
            case "mxp":
                return "application/x-mmxp";
            case "net":
                return "image/pnetvue";
            case "nrf":
                return "application/x-nrf";
            case "nws":
                return "message/rfc822";
            case "odc":
                return "text/x-ms-odc";
            case "out":
                return "application/x-out";
            case "p10":
                return "application/pkcs10";
            case "p12":
                return "application/x-pkcs12";
            case "p7b":
                return "application/x-pkcs7-certificates";
            case "p7c":
                return "application/pkcs7-mime";
            case "p7m":
                return "application/pkcs7-mime";
            case "p7r":
                return "application/x-pkcs7-certreqresp";
            case "p7s":
                return "application/pkcs7-signature";
            case "pc5":
                return "application/x-pc5";
            case "pci":
                return "application/x-pci";
            case "pcl":
                return "application/x-pcl";
            case "pcx":
                return "application/x-pcx";
            case "pdf":
                return "application/pdf";
            case "pdx":
                return "application/vnd.adobe.pdx";
            case "pfx":
                return "application/x-pkcs12";
            case "pgl":
                return "application/x-pgl";
            case "pic":
                return "application/x-pic";
            case "pko":
                return "application/vnd.ms-pki.pko";
            case "pl":
                return "application/x-perl";
            case "plg":
                return "text/html";
            case "pls":
                return "audio/scpls";
            case "plt":
                return "application/x-plt";
            case "pot":
                return "application/vnd.ms-powerpoint";
            case "ppa":
                return "application/vnd.ms-powerpoint";
            case "ppm":
                return "application/x-ppm";
            case "pps":
                return "application/vnd.ms-powerpoint";
            case "pr":
                return "application/x-pr";
            case "prf":
                return "application/pics-rules";
            case "prn":
                return "application/x-prn";
            case "prt":
                return "application/x-prt";
            case "ps":
                return "application/x-ps";
            case "ptn":
                return "application/x-ptn";
            case "pwz":
                return "application/vnd.ms-powerpoint";
            case "r3t":
                return "text/vnd.rn-realtext3d";
            case "ras":
                return "application/x-ras";
            case "rat":
                return "application/rat-file";
            case "rdf":
                return "text/xml";
            case "rec":
                return "application/vnd.rn-recording";
            case "red":
                return "application/x-red";
            case "rgb":
                return "application/x-rgb";
            case "rjs":
                return "application/vnd.rn-realsystem-rjs";
            case "rjt":
                return "application/vnd.rn-realsystem-rjt";
            case "rlc":
                return "application/x-rlc";
            case "rle":
                return "application/x-rle";
            case "rmf":
                return "application/vnd.adobe.rmf";
            case "rmi":
                return "audio/mid";
            case "rmj":
                return "application/vnd.rn-realsystem-rmj";
            case "rmm":
                return "audio/x-pn-realaudio";
            case "rmp":
                return "application/vnd.rn-rn_music_package";
            case "rms":
                return "application/vnd.rn-realmedia-secure";
            case "rmx":
                return "application/vnd.rn-realsystem-rmx";
            case "rnx":
                return "application/vnd.rn-realplayer";
            case "rp":
                return "image/vnd.rn-realpix";
            case "rpm":
                return "audio/x-pn-realaudio-plugin";
            case "rsml":
                return "application/vnd.rn-rsml";
            case "rt":
                return "text/vnd.rn-realtext";
            case "rtf":
                return "application/msword";
            case "rv":
                return "video/vnd.rn-realvideo";
            case "sam":
                return "application/x-sam";
            case "sat":
                return "application/x-sat";
            case "sdp":
                return "application/sdp";
            case "sdw":
                return "application/x-sdw";
            case "sit":
                return "application/x-stuffit";
            case "slb":
                return "application/x-slb";
            case "sld":
                return "application/x-sld";
            case "slk":
                return "drawing/x-slk";
            case "smi":
                return "application/smil";
            case "smil":
                return "application/smil";
            case "smk":
                return "application/x-smk";
            case "snd":
                return "audio/basic";
            case "sol":
                return "text/plain";
            case "sor":
                return "text/plain";
            case "spc":
                return "application/x-pkcs7-certificates";
            case "spl":
                return "application/futuresplash";
            case "spp":
                return "text/xml";
            case "ssm":
                return "application/streamingmedia";
            case "sst":
                return "application/vnd.ms-pki.certstore";
            case "stl":
                return "application/vnd.ms-pki.stl";
            case "stm":
                return "text/html";
            case "sty":
                return "application/x-sty";
            case "svg":
                return "text/xml";
            case "tdf":
                return "application/x-tdf";
            case "tg4":
                return "application/x-tg4";
            case "tga":
                return "application/x-tga";
            case "tld":
                return "text/xml";
            case "top":
                return "drawing/x-top";
            case "tsd":
                return "text/xml";
            case "txt":
                return "text/plain";
            case "uin":
                return "application/x-icq";
            case "uls":
                return "text/iuls";
            case "vcf":
                return "text/x-vcard";
            case "vda":
                return "application/x-vda";
            case "vdx":
                return "application/vnd.visio";
            case "vml":
                return "text/xml";
            case "vpg":
                return "application/x-vpeg005";
            case "vsd":
                return "application/vnd.visio";
            case "vss":
                return "application/vnd.visio";
            case "vst":
                return "application/vnd.visio";
            case "vsw":
                return "application/vnd.visio";
            case "vsx":
                return "application/vnd.visio";
            case "vtx":
                return "application/vnd.visio";
            case "vxml":
                return "text/xml";
            case "wax":
                return "audio/x-ms-wax";
            case "wb1":
                return "application/x-wb1";
            case "wb2":
                return "application/x-wb2";
            case "wb3":
                return "application/x-wb3";
            case "wbmp":
                return "image/vnd.wap.wbmp";
            case "wiz":
                return "application/msword";
            case "wk3":
                return "application/x-wk3";
            case "wk4":
                return "application/x-wk4";
            case "wkq":
                return "application/x-wkq";
            case "wks":
                return "application/x-wks";
            case "wm":
                return "video/x-ms-wm";
            case "wmd":
                return "application/x-ms-wmd";
            case "wmf":
                return "application/x-wmf";
            case "wmx":
                return "video/x-ms-wmx";
            case "wmz":
                return "application/x-ms-wmz";
            case "wp6":
                return "application/x-wp6";
            case "wpd":
                return "application/x-wpd";
            case "wpg":
                return "application/x-wpg";
            case "wpl":
                return "application/vnd.ms-wpl";
            case "wq1":
                return "application/x-wq1";
            case "wr1":
                return "application/x-wr1";
            case "wri":
                return "application/x-wri";
            case "wrk":
                return "application/x-wrk";
            case "ws":
                return "application/x-ws";
            case "ws2":
                return "application/x-ws";
            case "wsc":
                return "text/scriptlet";
            case "wvx":
                return "video/x-ms-wvx";
            case "xdp":
                return "application/vnd.adobe.xdp";
            case "xdr":
                return "text/xml";
            case "xfd":
                return "application/vnd.adobe.xfd";
            case "xfdf":
                return "application/vnd.adobe.xfdf";
            case "xq":
                return "text/xml";
            case "xql":
                return "text/xml";
            case "xquery":
                return "text/xml";
            case "xwd":
                return "application/x-xwd";
            case "x_b":
                return "application/x-x_b";
            case "x_t":
                return "application/x-x_t";
        }
        return "application/octet-stream";
    }

    /**
     * 是否固定格式的文件
     *
     * @param fileUrl 文件路径或URL
     * @param extList 后缀列表，如：png,jpeg,jpg,bmp,gif
     * @return
     */
    public static boolean isExtFile(String fileUrl, String extList) {
        return Arrays.asList(extList.split(","))
                .stream()
                .filter(type -> type.equals(FileUtils.getFileExt(fileUrl.toLowerCase())))
                .toArray().length > 0;
    }


    /**
     * 读取文件
     *
     * @param Path
     * @return
     */
    public static String ReadFile(String Path) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    /**
     * 获取文件夹下所有文件的名称 + 模糊查询（当不需要模糊查询时，queryStr传空或null即可）
     * 1.当路径不存在时，map返回retType值为1
     * 2.当路径为文件路径时，map返回retType值为2，文件名fileName值为文件名
     * 3.当路径下有文件夹时，map返回retType值为3，文件名列表fileNameList，文件夹名列表folderNameList
     *
     * @param folderPath 路径
     * @param queryStr   模糊查询字符串
     * @return
     */
    public static HashMap<String, Object> getFilesName(String folderPath, String queryStr) {
        HashMap<String, Object> map = new HashMap<>();
        List<String> fileNameList = new ArrayList<>();//文件名列表
        List<String> folderNameList = new ArrayList<>();//文件夹名列表
        File f = new File(folderPath);
        if (!f.exists()) { //路径不存在
            map.put("retType", "1");
        } else {
            boolean flag = f.isDirectory();
            if (flag == false) { //路径为文件
                map.put("retType", "2");
                map.put("fileName", f.getName());
            } else { //路径为文件夹
                map.put("retType", "3");
                File fa[] = f.listFiles();
                queryStr = queryStr == null ? "" : queryStr;//若queryStr传入为null,则替换为空（indexOf匹配值不能为null）
                for (int i = 0; i < fa.length; i++) {
                    File fs = fa[i];
                    if (fs.getName().indexOf(queryStr) != -1) {
                        if (fs.isDirectory()) {
                            folderNameList.add(fs.getName());
                        } else {
                            fileNameList.add(fs.getName());
                        }
                    }
                }
                map.put("fileNameList", fileNameList);
                map.put("folderNameList", folderNameList);
            }
        }
        return map;
    }

    public static String readFileContent(String filePath) {
        return String.join("", readFileLines(filePath));
    }

    /**
     * 以行为单位读取文件，读取到最后一行
     *
     * @param filePath
     * @return
     */
    public static List<String> readFileLines(String filePath) {
        BufferedReader reader = null;
        List<String> listContent = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                listContent.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return listContent;
    }

    /**
     * 读取指定行数据 ，注意：0为开始行
     *
     * @param filePath
     * @param lineNumber
     * @return
     */
    public static String readFileLine(String filePath, int lineNumber) {
        BufferedReader reader = null;
        String lineContent = "";
        try {
            reader = new BufferedReader(new FileReader(filePath));
            int line = 0;
            while (line <= lineNumber) {
                lineContent = reader.readLine();
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return lineContent;
    }

    /**
     * 读取从beginLine到endLine数据（包含beginLine和endLine），注意：0为开始行
     *
     * @param filePath
     * @param beginLineNumber 开始行
     * @param endLineNumber   结束行
     * @return
     */
    public static List<String> readFileLines(String filePath, int beginLineNumber, int endLineNumber) {
        List<String> listContent = new ArrayList<>();
        try {
            int count = 0;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String content = reader.readLine();
            while (content != null) {
                if (count >= beginLineNumber && count <= endLineNumber) {
                    listContent.add(content);
                }
                content = reader.readLine();
                count++;
            }
        } catch (Exception e) {
        }
        return listContent;
    }

    /**
     * 读取若干文件中所有数据
     *
     * @param listFilePath
     * @return
     */
    public static List<String> readFilesAllLines(List<String> listFilePath) {
        List<String> listContent = new ArrayList<>();
        for (String filePath : listFilePath) {
            File file = new File(filePath);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                int line = 1;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    listContent.add(tempString);
                    line++;
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
        return listContent;
    }

    /**
     * 文件数据写入（如果文件夹和文件不存在，则先创建，再写入）
     *
     * @param filePath
     * @param content
     * @param flag     true:如果文件存在且存在内容，则内容换行追加；false:如果文件存在且存在内容，则内容替换
     */
    public static String fileLinesWrite(String filePath, String content, boolean flag) {
        String filedo = "write";
        FileWriter fw = null;
        try {
            File file = new File(filePath);
            //如果文件夹不存在，则创建文件夹
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {//如果文件不存在，则创建文件,写入第一行内容
                file.createNewFile();
                fw = new FileWriter(file);
                filedo = "create";
            } else {//如果文件存在,则追加或替换内容
                fw = new FileWriter(file, flag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(content);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filedo;
    }

    /**
     * 写文件
     *
     * @param ins
     * @param out
     */
    public static void writeIntoOut(InputStream ins, OutputStream out) {
        byte[] bb = new byte[10 * 1024];
        try {
            int cnt = ins.read(bb);
            while (cnt > 0) {
                out.write(bb, 0, cnt);
                cnt = ins.read(bb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                ins.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断list中元素是否完全相同（完全相同返回true,否则返回false）
     *
     * @param list
     * @return
     */
    private static boolean hasSame(List<? extends Object> list) {
        if (null == list)
            return false;
        return 1 == new HashSet<Object>(list).size();
    }

    /**
     * 判断list中是否有重复元素（无重复返回true,否则返回false）
     *
     * @param list
     * @return
     */
    private static boolean hasSame2(List<? extends Object> list) {
        if (null == list)
            return false;
        return list.size() == new HashSet<Object>(list).size();
    }

    /**
     * 增加/减少天数
     *
     * @param date
     * @param num
     * @return
     */
    public static Date DateAddOrSub(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

}
