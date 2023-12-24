package com.github.vultr.core.util;

import lombok.SneakyThrows;

import java.text.SimpleDateFormat;

public class DateUtil {

    @SneakyThrows
    public static long toTime(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        return formatter.parse(String.valueOf(dateStr)).getTime();
    }

    public static String format(long duration) {
        long seconds = duration % 60;
        long minutes = (duration / 60) % 60;
        long hours = duration / (60 * 60);

        if (hours > 0) {
            return String.format("%d小时%d分钟%d秒", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds);
        }
        return String.format("%d秒", seconds);
    }

}
