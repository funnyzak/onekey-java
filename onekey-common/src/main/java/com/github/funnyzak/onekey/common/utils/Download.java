package com.github.funnyzak.onekey.common.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/5 4:50 PM
 */
public class Download {
    /**
     * 从URL下载，并保存到路径
     * @param fileUrl
     * @param savePath
     * @throws Exception
     */
    public static void downloadByUrl(String fileUrl, String savePath) throws Exception {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw e;
        }
    }
}