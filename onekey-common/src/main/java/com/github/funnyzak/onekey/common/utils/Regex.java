package com.github.funnyzak.onekey.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author silenceace@gmail.com
 */
public class Regex {

    public static Boolean isMatch(String source, String pattern) {
        return Pattern.matches(pattern, source);
    }

    public static String matchString(String source, String pattern) {
        List<String> list = matchList(source, pattern);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public static String matchString(String source, String pattern, int groupId) {
        List<String> list = matchList(source, pattern, groupId);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 搜索匹配的项
     *
     * @param source
     * @param pattern
     * @return
     */
    public static List<String> matchList(String source, String pattern) {
        return matchList(source, pattern, 0);
    }

    /**
     * 搜索匹配的项
     *
     * @param source
     * @param pattern
     * @param groupId
     * @return
     */
    public static List<String> matchList(String source, String pattern, int groupId) {
        List<String> strList = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(source);
        while (m.find()) {
            strList.add(m.group(groupId));
        }
        return strList;
    }

    /**
     * 搜索URL
     *
     * @param source
     * @return
     */
    public static List<String> matchUrls(String source) {
        return matchList(source, "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?");
    }

    public static Matcher match(String source, String pattern) {
        return Pattern.compile(pattern).matcher(source);
    }
}