package com.github.funnyzak.onekey.common.utils;

import org.nutz.castor.Castors;
import org.nutz.lang.*;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ixion
 */
public class StringUtils {
    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (!StringUtils.isNullOrEmpty(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
    /**
     * 字符串去除空白字符后从左到右按照一定位数插入字符
     *
     * @param str 源
     * @param n   位数
     * @param sp  插入字符
     * @return
     */
    public static String change(String str, int n, String sp) {
        String info = "";
        String nstr = sTrim(str);
        for (int i = 0; i < nstr.length(); i++) {
            info += nstr.charAt(i);
            if (i > 0 && (i + 1) % n == 0) {
                info += sp;
            }
        }
        return info;
    }

    public static String getString(Object obj) {
        return isNullOrEmpty(obj) ? "" : obj.toString();
    }

    public static boolean isNullOrEmpty(Object object) {
        return object == null || Strings.isBlank(object.toString());
    }

    /**
     * 字符串去除空白字符后从右到左按照一定位数插入字符
     *
     * @param str 源
     * @param n   位数
     * @param sp  插入字符
     * @return
     */
    public static String rChange(String str, int n, String sp) {
        String info = "";
        String nstr = sTrim(str);
        for (int i = nstr.length() - 1; i >= 0; i--) {
            info = nstr.charAt(i) + info;
            if ((nstr.length() - i) % n == 0) {
                info = sp + info;
            }
        }
        return info;
    }

    /**
     * 去除全部空白
     *
     * @param in
     * @return
     */
    public static String sTrim(String in) {
        return in.replaceAll("\\s*", "");
    }

    /**
     * 将数字格式字符串转换成list
     *
     * @param source 源字符串，格式为1,2,3
     * @return
     */
    public static List<Integer> stringConvertList(String source) {
        return stringConvertList(source, Integer.class);
    }

    public static <T> List<T> stringConvertList(String[] source, final Class<T> clazz) {
        final List<T> target = new ArrayList<T>();
        Lang.each(source, new Each<String>() {

            @Override
            public void invoke(int index, String info, int length) throws ExitLoop, ContinueLoop, LoopException {
                target.add(Castors.me().castTo(info, clazz));
            }
        });
        return target;
    }

    public static <T> List<T> stringConvertList(String source, Class<T> clazz) {
        if (Strings.isBlank(source)) {
            return new ArrayList<T>();
        }
        String[] infos = source.split(",");
        return stringConvertList(infos, clazz);
    }

    /**
     * 随机获取数字和大写英文字母组合的字符串
     *
     * @param size 返回的字符串的位数，如果小于1，则默认是6
     * @return String
     * @since 2015-09-25
     */
    public static String getRandomLetterAndDigital(int size) {
        String str = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";//去掉容易混淆字符：0与O,1与I
        StringBuffer sb = new StringBuffer();
        if (size < 1) {
            size = 6;
        }
        for (int i = 0; i < size; i++) {
            int ran = (int) (Math.random() * str.length());
            sb.append(str.charAt(ran));
        }
        return sb.toString().trim();
    }

    /**
     * 随机获取大/小写英文字母组合的字符串
     *
     * @param size 返回的字符串的位数，如果小于1，则默认是6
     * @return String
     * @since 2015-09-25
     */
    public static String getLetter(int size) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuffer sb = new StringBuffer();
        if (size < 1) {
            size = 6;
        }
        for (int i = 0; i < size; i++) {
            int ran = (int) (Math.random() * str.length());
            sb.append(str.charAt(ran));
        }
        return sb.toString().trim();
    }

    /**
     * 随机获取大写英文字母组合的字符串
     *
     * @param size 返回的字符串的位数，如果小于1，则默认是6
     * @return String
     * @since 2015-09-25
     */
    public static String getUpperLetter(int size) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();
        if (size < 1) {
            size = 6;
        }
        for (int i = 0; i < size; i++) {
            int ran = (int) (Math.random() * str.length());
            sb.append(str.charAt(ran));
        }
        return sb.toString().trim();
    }

    /**
     * 随机获取数字字符串
     *
     * @param size 返回的字符串的位数
     * @return String
     * @since 2015-09-25
     */
    public static String getRandomDigital(int size) {
        String str = "1234567890";
        StringBuffer sb = new StringBuffer();
        if (size < 1) {
            return null;
        } else {
            for (int i = 0; i < size; i++) {
                int ran = (int) (Math.random() * 10);
                sb.append(str.charAt(ran));
            }
            return sb.toString().trim();
        }
    }

    /**
     * 获取随机数字，同getRandomDigital
     *
     * @param size
     * @return
     */
    public static String getNumber(int size) {
        return getRandomDigital(size);
    }

    /**
     * 生成年月日时分秒毫秒（20120905050602000）
     *
     * @return
     * @since 2015-09-25
     */
    public static String getYYYYMMDDHHmmssmilliSecond() {
        StringBuffer str = new StringBuffer();
        String strMonth = "";
        String strDate = "";
        String strHour = "";
        String strMinute = "";
        String strSecond = "";
        String strMilliSecond = "";
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int milliSecond = cal.get(Calendar.MILLISECOND);
        if (month < 10) {
            strMonth = "0" + month;
        } else {
            strMonth = String.valueOf(month);
        }
        if (date < 10) {
            strDate = "0" + date;
        } else {
            strDate = String.valueOf(date);
        }
        if (hour < 10) {
            strHour = "0" + hour;
        } else {
            strHour = String.valueOf(hour);
        }
        if (minute < 10) {
            strMinute = "0" + minute;
        } else {
            strMinute = String.valueOf(minute);
        }
        if (second < 10) {
            strSecond = "0" + second;
        } else {
            strSecond = String.valueOf(second);
        }
        if (milliSecond < 10) {
            strMilliSecond = "00" + milliSecond;
        } else if (milliSecond < 100) {
            strMilliSecond = "0" + milliSecond;
        } else {
            strMilliSecond = String.valueOf(milliSecond);
        }
        return str.append(String.valueOf(year).trim()).append(strMonth.trim()).append(strDate.trim()).append(strHour.trim()).append(strMinute.trim()).append(strSecond.trim()).append(strMilliSecond.trim()).toString();
    }

    /**
     * 生成年月日（20120905050602000）
     *
     * @return
     * @since 2015-09-25
     */
    public static String getYYYYMMDD() {
        StringBuffer str = new StringBuffer();
        String strMonth = "";
        String strDate = "";
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        if (month < 10) {
            strMonth = "0" + month;
        } else {
            strMonth = String.valueOf(month);
        }
        if (date < 10) {
            strDate = "0" + date;
        } else {
            strDate = String.valueOf(date);
        }
        return str.append(String.valueOf(year).trim()).append(strMonth.trim()).append(strDate.trim()).toString();
    }

    /**
     * 获取uuid，有横杠（36位）
     *
     * @return
     * @since 2015-10-14
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取uuid，无横杠(32位)
     *
     * @return
     * @author lqyao
     * @since 2015-10-14
     */
    public static String getUUIDNumberOnly() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 移除字符串最后一个字符
     *
     * @return
     * @since 2015-10-14
     */
    public static String removeLastCode(String str) {
        if (str == null || str.length() < 1) {
            return str;
        }
        return str.substring(0, str.length() - 1);
    }

    /**
     * 第一个字符变大写
     *
     * @param str
     * @return
     */
    public static String firstCodeToUpperCase(String str) {
        if (isBlank(str)) {
            return str;
        }
        String strTrim = str.trim();
        return String.valueOf(strTrim.charAt(0)).toUpperCase() + strTrim.substring(1);
    }

    /**
     * 获取字符串最后一个字符
     *
     * @return
     * @since 2016-01-13
     */
    public static String getLastCode(String str) {
        if (str == null || str.length() < 1) {
            return "";
        }
        return str.substring(str.length() - 1);
    }

    /**
     * 获取第一个id
     *
     * @param str 字符串
     * @return id
     */
    public static String getFirstId(String str, String spiltCode) {
        if (spiltCode == null) {
            spiltCode = ",";
        }
        if (!str.isEmpty()) {
            if (str.indexOf(spiltCode) > -1) {
                return str.substring(0, str.indexOf(spiltCode)).trim();
            }
        }
        return str;
    }

    /**
     * 去相同部分
     *
     * @param originalStr 原字符串
     * @param deleteStr   需要去掉的字符串
     * @return string
     * @author lqy
     */
    public static String removeSamePart(String originalStr, String deleteStr) {
        if (originalStr != null && deleteStr != null) {
            originalStr = originalStr.replaceAll("\\(", "（");
            originalStr = originalStr.replaceAll("\\)", "）");
            originalStr = originalStr.replaceAll(" |　", "");
            deleteStr = deleteStr.replaceAll("\\(", "（");
            deleteStr = deleteStr.replaceAll("\\)", "）");
            deleteStr = deleteStr.replaceAll(" |　", "");
            if (originalStr.indexOf(deleteStr) > -1) {
                originalStr = originalStr.replaceAll(deleteStr, "");
            }
        }
        return originalStr;
    }

    /**
     * 拆分字符串获取数组
     *
     * @param str       字符串
     * @param spiltCode 拆分符号
     * @return String[]
     */
    public static String[] getArrayAfterSpilt(String str, String spiltCode) {
        if (str == null || str.trim().equals("")) {
            return null;
        } else {
            if (spiltCode == null || spiltCode.trim().equals("")) {
                spiltCode = ",";
            }
            return str.split(spiltCode);
        }
    }

    /**
     * 拆分字符串获取Ids
     *
     * @param idsString id字符串
     * @param spiltCode 拆分符号
     * @return ids
     */
    public static int[] getIdsAfterSpilt(String idsString, String spiltCode) {
        List<Integer> idList = new ArrayList<Integer>();
        if (idsString == null || idsString.trim().equals("")) {
            return null;
        } else {
            if (spiltCode == null || spiltCode.trim().equals("")) {
                spiltCode = ",";
            }
            String[] idArray = idsString.split(spiltCode);
            if (idArray != null && idArray.length > 0) {
                for (String string : idArray) {
                    if (string != null && !string.trim().equals("")) {
                        idList.add(Integer.parseInt(string.trim()));
                    }
                }
            }
        }
        if (idList != null && idList.size() > 0) {
            int[] ids = new int[idList.size()];
            for (int j = 0; j < idList.size(); j++) {
                ids[j] = idList.get(j);
            }
            return ids;
        }
        return null;
    }

    /**
     * @param obj
     * @return obj == null;
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }


    /**
     * 判断list是否为Null
     *
     * @param list
     * @return
     */
    public static <T> boolean isNullList(List<T> list) {
        return (list == null);
    }

    /**
     * 判断list是否为空
     *
     * @param list
     * @return (list = = null) || (list.size() < 1)
     */
    public static <T> boolean isEmptyList(List<T> list) {
        return (list == null) || (list.size() < 1);
    }

    /**
     * 判断Map是否为Null
     *
     * @param map
     * @return
     */
    public static <K, V> boolean isNullMap(Map<K, V> map) {
        return (map == null);
    }

    /**
     * 判断Map是否为空
     *
     * @param map
     * @return
     */
    public static <K, V> boolean isEmptyMap(Map<K, V> map) {
        return (map == null || map.size() < 1);
    }

    /**
     * 判断数组是否为Null
     *
     * @param obj
     * @return
     */
    public static boolean isNullArray(Object[] obj) {
        return (obj == null);
    }

    /**
     * 判断数组是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmptyArray(Object[] obj) {
        return (obj == null || obj.length < 1);
    }

    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }


    /**
     * <p>Checks if the String contains only whitespace.</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isWhitespace(null)   = false
     * StringUtils.isWhitespace("")     = true
     * StringUtils.isWhitespace("  ")   = true
     * StringUtils.isWhitespace("abc")  = false
     * StringUtils.isWhitespace("ab2c") = false
     * StringUtils.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains whitespace, and is non-null
     * @since 2.0
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 变成中文括号
     *
     * @param str
     * @return
     */
    public static String bracketToChinese(String str) {
        if (isBlank(str)) {
            return str;
        }
        String strTrim = str.trim();
        strTrim = strTrim.replaceAll("\\(", "（").replaceAll("\\)", "）");
        return strTrim;
    }

    /**
     * 变成英文括号
     *
     * @param str
     * @return
     */
    public static String bracketToEnglish(String str) {
        if (isBlank(str)) {
            return str;
        }
        String strTrim = str.trim();
        strTrim = strTrim.replaceAll("（", "(").replaceAll("）", ")");
        return strTrim;
    }

    /**
     * 替换字符串
     *
     * @param str
     * @param sourceStr，如果是特殊字符，如英文()、[]等，要使用\\(
     * @param targetStr
     * @return
     */
    public static String replaceStr(String str, String sourceStr, String targetStr) {
        if (isBlank(str)) {
            return str;
        }
        String strTrim = str.trim();
        strTrim = strTrim.replaceAll(sourceStr, targetStr);
        return strTrim;
    }

    /**
     *  移除字符串HTML标签
     * @param htmlStr
     * @return
     */
    public static String removeHtmlTags(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script= Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    /**
     * Url查询字符串
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    /**
     * 比较APP版本号的大小
     * <p>
     * 1、前者大则返回一个正数
     * 2、后者大返回一个负数
     * 3、相等则返回0
     *
     * @param version1 app版本号
     * @param version2 app版本号
     * @return int
     */
    public static int compareAppVersion(String version1, String version2) {
        if (version1 == null || version2 == null) {
            throw new RuntimeException("版本号不能为空");
        }
        // 注意此处为正则匹配，不能用.
        String[] versionArray1 = version1.split("\\.");
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        // 取数组最小长度值
        int minLength = Math.min(versionArray1.length, versionArray2.length);
        int diff = 0;
        // 先比较长度，再比较字符
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {
            ++idx;
        }
        // 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }
}
