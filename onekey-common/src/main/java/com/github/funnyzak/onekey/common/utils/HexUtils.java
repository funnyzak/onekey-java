package com.github.funnyzak.onekey.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/8/3 2:58 下午
 * @description HexUtils 进制转换工具类
 */
public class HexUtils {
    /**
     * 获得倒序二进制数据
     *
     * @param hexString
     * @return
     */
    public static String binaryFromHex(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * 将数据部分拆分成二进制
     *
     * @param bytes
     * @param begin
     * @param end
     * @return
     */
    public static String bytesToHex(byte[] bytes, int begin, int end) {
        StringBuilder hexBuilder = new StringBuilder(2 * (end - begin));
        for (int i = begin; i < end; i++) {
            hexBuilder.append(Character.forDigit((bytes[i] & 0xF0) >> 4, 16)); // 转化高四位
            hexBuilder.append(Character.forDigit((bytes[i] & 0x0F), 16)); // 转化低四位
            hexBuilder.append(' '); // 加一个空格将每个字节分隔开
        }
        return hexBuilder.toString().toUpperCase();
    }

    /**
     * 接受数据 00 0A 4B 55
     * 返回00001010 00001010 数组
     *
     * @param data
     * @return
     */
    public static String[] getDate(String data) {
        String[] dataArr = data.split(" ");
        String[] returnDataArr = new String[50];
        for (int i = 0; i < dataArr.length; i++) {
            returnDataArr[i] = binaryFromHex(dataArr[i]);
        }
        return returnDataArr;
    }

    /**
     * 单个十六进制转十进制
     *
     * @param hex16
     * @return
     */
    public static int getDecimalFromHex(String hex16) {
        return Integer.parseInt(hex16, 16);
    }

    /**
     * 单个十进制转十六进制
     *
     * @param num
     * @return
     */
    public static String getHexFromDecimal(Integer num) {
        return Integer.toHexString(num);
    }

    /**
     * 十进制转十六进制
     *
     * @param nums
     * @return
     */
    public static List<String> getHexListFromDecimalList(List<Integer> nums) {
        if (nums == null || nums.size() == 0) {
            return null;
        }
        return nums.stream().map(num -> getHexFromDecimal(num)).collect(Collectors.toList());
    }


    /**
     * Ascii转10进制
     *
     * @param str
     * @return
     */
    public static List<Integer> getDecimalListFromAscii(String str) {
        List<Integer> hex10 = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            hex10.add(Integer.parseInt(Integer.toString(c, 10), 10));
        }
        return hex10;
    }

    /**
     * Ascii转16进制
     *
     * @param str
     * @return
     */
    public static List<String> getHexFromAscii(String str) {
        return getHexListFromDecimalList(getDecimalListFromAscii(str));
    }

    /**
     * 16进制字符串转Ascii
     *
     * @param hexList 16进制集合
     * @return
     */
    public static String getAsciiByHex(List<String> hexList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hexList.size(); i++) {
            sb.append((char) getDecimalFromHex(hexList.get(i)));
        }
        return sb.toString();
    }

    /**
     * 16进制字符串转Ascii
     *
     * @param hexString 16进制字符串，如：414243444546 对应 ABCDEF
     * @return
     */
    public static String getAsciiFromHexString(String hexString) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hexString.length() - 1; i += 2) {
            String h = hexString.substring(i, (i + 2));
            int decimal = Integer.parseInt(h, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    /**
     * 16进制字符串转Ascii
     *
     * @param hexBytes 16进制集合
     * @return
     */
    public static String getAsciiFromHexList(List<String> hexBytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hexBytes.size(); i++) {
            sb.append((char) getDecimalFromHex(hexBytes.get(i)));
        }
        return sb.toString();
    }

    /**
     * 10进制字符串转Ascii
     *
     * @param decimalList 10进制集合
     * @return
     */
    public static String getAsciiFromDecimalList(List<Integer> decimalList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < decimalList.size(); i++) {
            sb.append((char) ((int) decimalList.get(i)));
        }
        return sb.toString();
    }

    /**
     * 倒序字符串
     *
     * @param old
     * @return
     */
    public static String reverseOrder(String old) {
        return new StringBuffer(old).reverse().toString();
    }

    public static void main(String[] arg) {
//        System.out.println(HexUtils.getAsciiBy10Hex(Arrays.asList(65)));
        System.out.println(HexUtils.getDecimalListFromAscii("ABCDEF").isEmpty());
    }
}