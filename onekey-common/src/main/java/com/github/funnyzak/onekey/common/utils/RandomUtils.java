package com.github.funnyzak.onekey.common.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Auther: (Leon)silenceace@gmail.com
 * @Date: 2018/12/19 16:08
 * @Description:
 */
public class RandomUtils {
    /**
     * 概率获取随机数
     *
     * @param keyChanceMap String为对应内容  int为数量
     * @return
     */
    public static String chanceRandom(Map<String, Integer> keyChanceMap) {
        if (keyChanceMap == null || keyChanceMap.size() == 0)
            return null;
        Integer sum = 0;
        for (Integer value : keyChanceMap.values()) {
            sum += value;
        }
        Integer rand = new Random().nextInt(sum) + 1;

        for (Map.Entry<String, Integer> entry : keyChanceMap.entrySet()) {
            rand -= entry.getValue();
            if (rand <= 0) {
                String item = entry.getKey();
                return item;
            }
        }
        return null;
    }

    public static <T> T itemRandom(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Collections.shuffle(list);
        return list.get(0);
    }
}