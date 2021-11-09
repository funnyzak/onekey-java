package com.github.funnyzak.common.utils;

import org.nutz.lang.Lang;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串内码编码解码方法
 *
 * @author Kerbores
 */
public class CharSequence {
    /**
     * 解码
     *
     * @param data 数据数组
     * @return 原数据
     */
    public static String decode(Integer[] data) {
        return decode(Lang.array2list(data));
    }

    /**
     * 解码
     *
     * @param data 数据list
     * @return 原数据
     */
    public static String decode(List<Integer> data) {
        return new CharSequence(data).toString();
    }

    /**
     * 解码
     *
     * @param data 数据串 用 ',' 分隔
     * @return 原数据
     */
    public static String decode(String data) {
        List<Integer> value = parse(data);
        return decode(value);
    }

    /**
     * NB解码
     *
     * @param data
     * @param times
     * @return
     */
    public static String decode(String data, int times) {
        String target = data;
        String cache = null;
        for (int i = 0; i < times; i++) {
            try {
                target = decode(target);
            } catch (Exception e) {
                return cache;
            }
            cache = target;
        }
        return target;
    }

    /**
     * 编码
     *
     * @param info 带编码字符串
     * @return 编码结果串
     */
    public static List<Integer> encode(String info) {
        return new CharSequence(info).getValue();
    }

    /**
     * 编码成串
     *
     * @param info
     * @return
     */
    public static String encodeToString(String info) {
        StringBuilder builder = new StringBuilder();
        for (Integer i : encode(info)) {
            builder.append(i + ",");
        }
        if (builder.length() == 0) {
            return "";
        }
        return builder.substring(0, builder.length() - 1);
    }

    /**
     * 编码成串
     *
     * @param info
     * @return
     */
    public static String encodeToString(String info, int times) {
        String target = info;
        for (int i = 0; i < times; i++) {
            target = encodeToString(target);
        }
        return target;
    }

    private static List<Integer> parse(String data) {
        List<Integer> target = new ArrayList<Integer>();
        String[] infos = data.split(",");
        for (String info : infos) {
            target.add(Integer.parseInt(info.trim()));
        }
        return target;
    }

    private List<Integer> value = new ArrayList<Integer>();

    private String stringValue;

    public CharSequence(List<Integer> value) {
        this.value = value;
        this.stringValue = toString();
    }

    private CharSequence(String in) {
        for (char c : in.toCharArray()) {
            value.add((int) c);
        }
        this.stringValue = in;
    }

    @SuppressWarnings("unused")
    private String getStringValue() {
        return stringValue;
    }

    private List<Integer> getValue() {
        return value;
    }

    @SuppressWarnings("unused")
    private void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @SuppressWarnings("unused")
    private void setValue(List<Integer> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String target = "";
        for (int i : value) {
            target += (char) i;
        }
        return target;
    }

}
