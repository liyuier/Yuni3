package com.yuier.yuni.plugin.util;

import com.yuier.yuni.event.message.detector.YuniEventDetector;
import com.yuier.yuni.event.message.detector.command.CommandDetector;
import com.yuier.yuni.event.message.detector.pattern.PatternDetector;

import static com.yuier.yuni.core.constants.PluginDetectorType.COMMAND;
import static com.yuier.yuni.core.constants.PluginDetectorType.PATTERN;
import static com.yuier.yuni.core.constants.SystemConstants.UNKNOWN;

/**
 * @Title: PluginBusinessMapUtil
 * @Author yuier
 * @Package com.yuier.yuni.event.util
 * @Date 2025/12/27 3:20
 * @description:
 */

public class PluginBusinessMapUtil {



    public static String pluginDetectorTypeName(YuniEventDetector<?> detector) {
        if (detector instanceof CommandDetector) {
            return COMMAND;
        } else if (detector instanceof PatternDetector) {
            return PATTERN;
        } else {
            return UNKNOWN;
        }
    }
}
