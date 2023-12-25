package com.github.vultr.core.util;

import java.util.Random;

public class RandomUtil {

    /**
     * 取随机文本
     *
     * @param values
     * @return
     */
    public static String random(String[] values) {
        if (values == null) {
            return null;
        }
        // 创建一个Random对象
        Random random = new Random();
        // 生成一个随机索引
        int index = random.nextInt(values.length);
        // 获取随机选择的元素
        return values[index];
    }

}
