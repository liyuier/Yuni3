package com.yuier.yuni.plugin.util;

import com.yuier.yuni.core.enums.YuniPluginType;
import com.yuier.yuni.event.detector.message.YuniEventDetector;
import com.yuier.yuni.event.detector.message.command.CommandDetector;
import com.yuier.yuni.event.detector.message.pattern.PatternDetector;
import com.yuier.yuni.plugin.model.YuniPlugin;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatePlugin;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPlugin;
import com.yuier.yuni.plugin.model.passive.message.CommandPlugin;
import com.yuier.yuni.plugin.model.passive.message.MessagePlugin;
import com.yuier.yuni.plugin.model.passive.message.PatternPlugin;
import com.yuier.yuni.plugin.model.passive.notice.NoticePlugin;
import com.yuier.yuni.plugin.model.passive.request.RequestPlugin;

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

    public static YuniPluginType getPluginType(YuniPlugin plugin) {
        return switch (plugin) {
            case ScheduledPlugin scheduledPlugin -> YuniPluginType.SCHEDULED;
            case ImmediatePlugin immediatePlugin -> YuniPluginType.IMMEDIATE;
            case CommandPlugin commandPlugin -> YuniPluginType.COMMAND;
            case PatternPlugin patternPlugin -> YuniPluginType.PATTERN;
            case NoticePlugin noticePlugin -> YuniPluginType.NOTICE;
            case RequestPlugin requestPlugin -> YuniPluginType.REQUEST;
            case MessagePlugin<?> messagePlugin -> YuniPluginType.META;
            case null, default -> YuniPluginType.UNKNOWN;
        };
    }
}
