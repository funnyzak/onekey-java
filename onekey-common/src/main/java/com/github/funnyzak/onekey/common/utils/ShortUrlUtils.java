package com.github.funnyzak.onekey.common.utils;

import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * 生成短网址
 * @author potato
 */
public class ShortUrlUtils {
    private static Log log = Logs.get();

    /**
     * 生成百度短网址
     *
     * @param url
     * @param def
     * @return
     */
    public static String baiduShortUrl(String url, String def) {
        try {
            Response response = Http.post3("http://dwz.cn/create.php", "{\"url\":\"" + url + "\"}\n", Header.create().set("Content-Type", "application/json; charset=UTF-8"), 3 * 1000);
            if (!response.isOK()) return url;
            LinkedHashMap rlt = (LinkedHashMap) Json.fromJson(response.getContent());
            if (rlt.get("status").toString().equals("0")) return rlt.get("tinyurl").toString();
            else return def;
        } catch (Exception e) {
            log.errorf("生成百度短网址失败,%s", e.getMessage());
            return def;
        }
    }

    public static String suoShortUrl(String url, String def) {
        try {
            Response response = Http.get(String.format("http://suo.im/api.php?url=%s,", URLEncoder.encode(url, "UTF-8")), 1500);
            if (!response.isOK()) return def;
            return StringUtils.isNullOrEmpty(response.getContent()) ? def : response.getContent();
        } catch (Exception e) {
            log.errorf("生成Suo短网址失败,%s", e.getMessage());
            return def;
        }
    }

    public static String nefShortUrl(String url, String def) {
        Date start = new Date();
        try {
            String[] apiHosts = new String[]{
                    "c7.gg", "kks.me", "u6.gg", "uee.me", "rrd.me"
            };
            Response response = Http.get(String.format("http://api.%s/api.php?url=%s&apikey=wNNNfgfre2hd24@ddd", apiHosts[new Random().nextInt(apiHosts.length)], URLEncoder.encode(url, "UTF-8")), 1000, 1000);
            if (!response.isOK() || response.getContent().startsWith("{")) return def;
            return StringUtils.isNullOrEmpty(response.getContent()) ? def : response.getContent();
        } catch (Exception e) {
            log.errorf("生成985短网址失败,%s", e.getMessage());
            return def;
        } finally {
            log.info("长连接生成时间：" + TypeParse.parseString(new Date().getTime() - start.getTime()) + ",连接：" + url);
        }
    }

    public static String tinyShortUrlBy(String url, String alias, String def) {
        try {
            Response response = Http.get(String.format("https://tinyurl.com/create.php?url=%s&alias=%s", URLEncoder.encode(url, "UTF-8"), alias), 1500);
            if (!response.isOK()) return def;
            String shortUrl = Regex.matchString(response.getContent(), "\\<div class=\"indent\"\\><b>([^\\<]+)\\<\\/b\\>", 1);
            return shortUrl == null ? def : shortUrl;
        } catch (Exception e) {
            log.errorf("生成Tinyurl短网址失败,%s", e.getMessage());
            return def;
        }
    }
}
