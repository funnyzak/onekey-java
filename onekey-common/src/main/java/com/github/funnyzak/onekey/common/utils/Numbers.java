package com.github.funnyzak.onekey.common.utils;

import org.nutz.lang.Nums;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数字格式化工具类
 */
public class Numbers extends Nums {
    /**
     * 生成数值数组（主要用于索引遍历）
     *
     * @param endNum   结束数
     * @param step     步长
     * @param startNum 开始数
     * @return
     */
    public static List<Integer> generateNumArray(Integer endNum, Integer step, Integer startNum) {
        List<Integer> list = new ArrayList<>();
        int num = startNum;
        while (num <= endNum) {
            list.add(num);
            num += step;
        }
        return list;
    }

    public static List<Integer> generateNumArray(Integer endNum) {
        return generateNumArray(endNum, 1, 0);
    }

    /* *
     * 格式化数字
     *
     * @param number    待格式化数据
     * @param precision 小数位数
     * @return 格式结果
     */
    public static String format(double number, int precision) {
        if (number == 0) {
            String temp = "0.";
            for (int i = 0; i < precision; i++) {
                temp += "0";
            }
            return temp;
        }
        number = keepPrecision(number, precision);
        String base = "###,###.";
        for (int i = 0; i < precision; i++) {
            base += "#";
        }
        NumberFormat formatter = new DecimalFormat(base);
        String target = formatter.format(number);
        int ps = 0;
        if (target.split("\\.").length > 1) {
            ps = target.split("\\.")[1].length();
        } else if (precision == 0) {
            return target.substring(0, target.length() - 1);
        } else {
            target += ".";
        }
        if (ps != precision) {
            for (int i = 0; i < precision - ps; i++) {
                target += "0";
            }
        }
        return target;
    }

    /**
     * 格式化数字
     *
     * @param number    待格式化数据
     * @param precision 小数位数
     * @return 格式化结果
     */
    public static String format(float number, int precision) {
        if (number == 0) {
            String temp = "0.";
            for (int i = 0; i < precision; i++) {
                temp += "0";
            }
            return temp;
        }
        number = keepPrecision(number, precision);
        String base = "###,###.";
        for (int i = 0; i < precision; i++) {
            base += "#";
        }
        NumberFormat formatter = new DecimalFormat(base);
        String target = formatter.format(number);
        int ps = 0;
        if (target.split("\\.").length > 1) {
            ps = target.split("\\.")[1].length();
        } else if (precision == 0) {
            return target.substring(0, target.length() - 1);
        } else {
            target += ".";
        }
        if (ps != precision) {
            for (int i = 0; i < precision - ps; i++) {
                target += "0";
            }
        }
        return target;
    }

    /**
     * 格式化数字
     *
     * @param number    待格式化数据
     * @param precision 小数位数
     * @return 格式化结果
     */
    public static String format(Number number, int precision) {
        return format(number.toString(), precision);
    }

    /**
     * 格式化数字
     *
     * @param number    待格式化数据
     * @param precision 小数位数
     * @return 格式化结果
     */
    public static String format(String num, int precision) {
        return format(Double.parseDouble(num), precision);

    }

    /**
     * 会导致栈溢出 FIXED
     *
     * @param number    待格式化数据
     * @param precision 小数位数
     * @return 格式结果
     */
    @Deprecated
    public static String formatPrecission(String number, int precision) {
        return format(number, precision);
    }

    /**
     * 对double类型的数值保留指定位数的小数。<br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
     * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    要保留小数的数字
     * @param precision 小数位数
     * @return double 如果数值较大，则使用科学计数法表示
     */
    public static double keepPrecision(double number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 对float类型的数值保留指定位数的小数。<br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
     * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    要保留小数的数字
     * @param precision 小数位数
     * @return float 如果数值较大，则使用科学计数法表示
     */
    public static float keepPrecision(float number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。<br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
     * 如果给定的数字没有小数，则转换之后将以0填充；例如：int 123 1 --> 123.0<br>
     * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param String类型的数字对象
     * @param precision     小数精确度总位数,如2表示两位小数
     * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
     */
    public static String keepPrecision(Number number, int precision) {
        return keepPrecision(String.valueOf(number), precision);
    }

    /**
     * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。
     *
     * <pre>
     * 	"3.1415926", 1			--> 3.1
     * 	"3.1415926", 3			--> 3.142
     * 	"3.1415926", 4			--> 3.1416
     * 	"3.1415926", 6			--> 3.141593
     * 	"1234567891234567.1415926", 3	--> 1234567891234567.142
     * </pre>
     *
     * @param String    类型的数字对象
     * @param precision 小数精确度总位数,如2表示两位小数
     * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
     */
    public static String keepPrecision(String number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public static void main(String[] args) {
        System.err.println(keepPrecision(0.000000, 2));
    }

    /**
     * 把number列表字符串转为泛型Long
     *
     * @param numbers 如 1,2,3 ,1,2,
     * @param split   数字字符串的分隔符
     * @return
     */
    public static List<Long> parseNumbersString2LongList(String numbers, String split, boolean distinct) {
        if (StringUtils.isNullOrEmpty(numbers)) {
            return null;
        }
        List<Long> longList = Arrays.asList(numbers.split(split)).stream().filter(v -> !StringUtils.isNullOrEmpty(v)).map(v -> TypeParse.parseLong(v, null)).filter(v -> v != null).collect(Collectors.toList());

        if (distinct) {
            HashSet<Long> hashSet = new HashSet<>();
            hashSet.addAll(longList);
            return hashSet.stream().collect(Collectors.toList());
        }
        return longList;

    }

    /**
     * 把number列表字符串转为泛型Long
     *
     * @param numbers 如 1,2,3 ,1,2,
     * @return
     */
    public static List<Long> parseNumbersString2LongList(String numbers) {
        return parseNumbersString2LongList(numbers, ",", true);
    }
}
