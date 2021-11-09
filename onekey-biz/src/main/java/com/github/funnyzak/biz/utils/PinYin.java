package com.github.funnyzak.biz.utils;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/2 4:42 PM
 */

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PinYin {
    private static final Logger logger = LoggerFactory.getLogger(PinYin.class);

    /**
     * 默认汉语拼音转换
     *
     * @param pinYinStr
     * @return
     */
    public static String convertPinyin(String pinYinStr) {
        return convertPinyin(pinYinStr, new HanyuPinyinOutputFormat());
    }

    /**
     * 转换为没有声调拼音
     *
     * @param pinYinStr
     * @return
     */
    public static String convertWithoutTonePinyin(String pinYinStr) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        return convertPinyin(pinYinStr, outputFormat);
    }


    /**
     * 转换汉字拼音首字母组合
     *
     * @param pinYinStr
     * @return
     */
    public static String convertFirstLetterPinyin(String pinYinStr) {
        String rltStr = "";
        for (char c : pinYinStr.toCharArray()) {
            String convertResult = convertPinyin(Character.toString(c));
            rltStr += convertResult.length() > 0 ? convertResult.substring(0, 1) : "";
        }
        return rltStr;
    }

    /**
     * 字符串转换拼音
     *
     * @param pinYinStr
     * @param outputFormat
     * @return
     */
    public static String convertPinyin(String pinYinStr, HanyuPinyinOutputFormat outputFormat) {
        String rltString = "";
        try {
            for (char c : pinYinStr.toCharArray()) {
                String[] rltStr = PinyinHelper.toHanyuPinyinStringArray(c, outputFormat);
                rltString += rltStr != null && rltStr.length > 0 ? rltStr[0] : "";
            }
        } catch (Exception e) {
            logger.error("汉语拼音转换失败，错误信息：{}", e.toString());
        }
        return rltString;
    }
}