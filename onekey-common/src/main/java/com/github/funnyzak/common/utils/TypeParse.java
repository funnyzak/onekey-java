package com.github.funnyzak.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TypeParse {
    private static final String boolTrueString = "true";
    private static final String boolFalseString = "false";

    public static Boolean parseBool(Object obj, Boolean defValue) {
        if (obj != null) {
            if (boolTrueString.equals(obj.toString().toLowerCase())) {
                return true;
            } else if (boolFalseString.equals(obj.toString().toLowerCase())) {
                return false;
            }
        }
        return defValue;
    }

    public static Long parseLong(Object obj, Long defValue) {
        if (StringUtils.isNull(obj)) {
            return defValue;
        }

        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return defValue;
        }
    }
    public static Long parseLong(Object obj) {
       return parseLong(obj, null);
    }


    public static Integer parseInt(Object obj, Integer defValue) {
        if (StringUtils.isNull(obj)) {
            return defValue;
        }

        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return defValue;
        }
    }

    public static Integer parseInt(Object obj) {
        return parseInt(obj, null);
    }

    public static Date parseDate(Object obj) {
        return parseDate(obj, null);
    }


    public static Date parseDate(Object obj, Date defValue) {
        if (!StringUtils.isNull(obj)) {
            try {
                SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sformat.parse(obj.toString());
            } catch (Exception e) {
                return defValue;
            }

        }
        return defValue;
    }


    public static Float parseFloat(Object obj) {
        return parseFloat(obj, null);
    }

    public static Float parseFloat(Object obj, Float defValue) {
        if (obj == null) {
            return defValue;
        }
        Float floatValue = defValue;
        Boolean IsFloat = Regex.isMatch(obj.toString().trim(), "^([-]|[0-9])[0-9]*(\\.\\w*)?$");
        if (IsFloat) {
            try {
                return Float.parseFloat(obj.toString());
            } catch (Exception e) {
                return defValue;
            }
        }
        return defValue;
    }

    public static String parseString(Object obj, String defValue) {
        if (obj == null) {
            return defValue;
        }
        return obj.toString();
    }

    public static String parseString(Object strValue) {
        return parseString(strValue, null);
    }


    /**
     * 进制转换
     *
     * @param number 要转换的进制数据
     * @param sBase  要转换的源数据进制
     * @param dBase  要转换的目标进制
     * @return
     */
    public static String baseConversion(String number,
                                        int sBase, int dBase) {
        // Parse the number with source radix
        // and return in specified radix(base)
        return Integer.toString(
                Integer.parseInt(number, sBase),
                dBase);
    }

}
