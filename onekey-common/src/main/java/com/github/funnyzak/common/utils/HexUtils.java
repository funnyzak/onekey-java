package com.github.funnyzak.common.utils;

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
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return reverseOrder(bString);
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
        String[] datas = data.split(" ");
        String[] returnDatas = new String[50];
        for (int i = 0; i < datas.length; i++) {
            returnDatas[i] = hexString2binaryString(datas[i]);
        }
        return returnDatas;
    }

    /**
     * 单个十六进制转十进制
     *
     * @param hex16
     * @return
     */
    public static int get10HexBy16Hex(String hex16) {
        return Integer.parseInt(hex16, 16);
    }

    /**
     * 单个十进制转十六进制
     *
     * @param num
     * @return
     */
    public static String get16HexBy10Hex(Integer num) {
        return Integer.toHexString(num);
    }

    /**
     * 十进制转十六进制
     *
     * @param nums
     * @return
     */
    public static List<String> get16HexBy10Hex(List<Integer> nums) {
        if (nums == null || nums.size() == 0) {
            return null;
        }
        return nums.stream().map(num -> get16HexBy10Hex(num)).collect(Collectors.toList());
    }


    /**
     * Ascii转10进制
     *
     * @param str
     * @return
     */
    public static List<Integer> get10HexByAscii(String str) {
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
    public static List<String> get16HexByAscii(String str) {
        return get16HexBy10Hex(get10HexByAscii(str));
    }

    /**
     * 16进制字符串转Ascii
     *
     * @param hexs 16进制集合
     * @return
     */
    public static String getAsciiBy16Hex(List<String> hexs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hexs.size(); i++) {
            sb.append((char) get10HexBy16Hex(hexs.get(i)));
        }
        return sb.toString();
    }

    /**
     * 16进制字符串转Ascii
     *
     * @param hexString 16进制字符串，如：414243444546 对应 ABCDEF
     * @return
     */
    public static String getAsciiBy16HexString(String hexString) {
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
    public static String getAsciiBy16HexList(List<String> hexBytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hexBytes.size(); i++) {
            sb.append((char) get10HexBy16Hex(hexBytes.get(i)));
        }
        return sb.toString();
    }

    /**
     * 10进制字符串转Ascii
     *
     * @param hex10Bytes 10进制集合
     * @return
     */
    public static String getAsciiBy10Hex(List<Integer> hex10Bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex10Bytes.size(); i++) {
            sb.append((char) ((int) hex10Bytes.get(i)));
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
        System.out.println(HexUtils.get10HexByAscii("ABCDEF").isEmpty());
    }
}