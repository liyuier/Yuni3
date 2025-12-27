package com.yuier.yuni.core.util;

import org.springframework.stereotype.Component;

/**
 * @Title: LogStringUtil
 * @Author yuier
 * @Package com.yuier.yuni.event.model.util
 * @Date 2025/12/24 18:19
 * @description: 适用于日志的一些字符串处理工具
 */

@Component
public class LogStringUtil {

    // 控制台输出字符串时设置颜色 👇
    // 靛青
    public static String buildCyanLog(String input) {
        return "\033[36m" + input + "\033[0m";
    }
    // 亮红
    public static String buildBrightRedLog(String input) {
        return "\033[91m" + input + "\033[0m";
    }
    // 亮蓝
    public static String buildBrightBlueLog(String input) {
        return "\033[92m" + input + "\033[0m";
    }
    // 紫色
    public static String buildPurpleLog(String input) {
        return "\033[35m" + input + "\033[0m";
    }
    // 黄色
    public static String buildYellowLog(String input) {
        return "\033[33m" + input + "\033[0m";
    }

    /**
     * 特殊字符转换
     * @param input 含有特殊字符的字符串
     * @return 处理后的字符串
     */
    public static String escapeString(String input) {
        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                default:
                    escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
