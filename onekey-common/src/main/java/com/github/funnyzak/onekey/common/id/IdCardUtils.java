package com.github.funnyzak.onekey.common.id;


import com.github.funnyzak.onekey.common.utils.DateUtils;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/31 10:36 上午
 * @description IdCardUtils
 */
public class IdCardUtils {

    private static Logger logger = LoggerFactory.getLogger(IdCardUtils.class);
    private static final String AREA_CODE = "/constant/area-code.properties";


    public static NutMap analysisIdCard(String idNumber) {
        NutMap map = new NutMap();
        map.put("address", area(idNumber));
        map.put("birthday", birthday(idNumber));
        map.put("sex", sex(idNumber));
        map.put("age", age(idNumber));
        map.put("province", province(idNumber));
        return map;
    }

    public static String province(String idNumber) {
        String address = area(idNumber);
        int provinceCharIdx = address.lastIndexOf('省');
        provinceCharIdx = provinceCharIdx > 0 ? provinceCharIdx : address.lastIndexOf('市');
        provinceCharIdx = provinceCharIdx > 0 ? provinceCharIdx : address.lastIndexOf("区");
        return provinceCharIdx > 0 ? address.substring(0, provinceCharIdx + 1) : address;
    }

    /**
     * 根据身份证解析 籍贯所在地
     *
     * @param idNumber
     * @return
     */
    public static String area(String idNumber) {
        Properties properties = new Properties();
        String area;
        try {
            properties.load(IdCardUtils.class.getResourceAsStream(AREA_CODE));
            area = properties.getProperty(idNumber.substring(0, 6));
            if (area == null) {
                return "未知区域";
            }
            area = new String(area.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            return area;
        } catch (IOException ioe) {
            logger.error("解析身份证地址异常===>", ioe);
        }
        return null;
    }

    /**
     * 根据身份证解析 出生年月
     *
     * @param idNumber
     * @return
     */
    public static String birthday(String idNumber) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = birthDate(idNumber);
        return format.format(date);
    }

    public static Date birthDate(String idNumber) {
        String birthday = null;
        SimpleDateFormat format = null;
        Date date = new Date();
        if (idNumber.length() == 18) {
            birthday = idNumber.substring(6, 14);
            format = new SimpleDateFormat("yyyyMMdd");
        } else if (idNumber.length() == 15) {
            birthday = idNumber.substring(6, 12);
            format = new SimpleDateFormat("yyMMdd");
        }
        try {
            date = format.parse(birthday);
        } catch (ParseException pe) {
            logger.error("日期转换异常===>", pe);
        }
        return date;
    }

    /**
     * 根据身份证 解析年龄
     *
     * @param idNumber
     * @return
     */
    public static int age(String idNumber) {
        Date date = birthDate(idNumber);
        int age = DateUtils.yearDiff(DateUtils.sD(date), DateUtils.sD(new Date()));
        return age;
    }


    /**
     * 根据身份证 解析性别 身份证倒数第二位 奇数代表男 偶数代表女
     *
     * @param idNumber
     * @return
     */
    public static String sex(String idNumber) {
        String sex = null;
        if (idNumber.length() == 15) {
            sex = idNumber.substring(14, 15);
        } else if (idNumber.length() == 18) {
            sex = idNumber.substring(16, 17);
        }
        int se = Integer.parseInt(sex);
        return se % 2 == 0 ? "WOMEN" : "MAN";
    }

}