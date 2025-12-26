package com.yuier.yuni.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Title: YuniTimeUtil
 * @Author yuier
 * @Package com.yuier.yuni.core.util
 * @Date 2025/12/27 1:37
 * @description:
 */

public class YuniTimeUtil {

    public static String formatTimestamp(Long timestamp, String format) {
        if (timestamp == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 东八区
        return sdf.format(new Date(timestamp * 1000));
    }

    public static String formatTimestamp(Long timestamp) {
        return formatTimestamp(timestamp, "yyyy-MM-dd HH:mm:ss");
    }
}
