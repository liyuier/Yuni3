package com.yuier.yuni.plugin.persistence;

import com.yuier.yuni.core.util.YuniTimeUtil;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.plugin.domain.entity.PluginCallEntity;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import com.yuier.yuni.plugin.service.PluginCallService;
import com.yuier.yuni.plugin.util.PluginBusinessMapUtil;
import com.yuier.yuni.plugin.util.PluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Title: SavePluginCallEvent
 * @Author yuier
 * @Package com.yuier.yuni.plugin.persistence
 * @Date 2025/12/27 2:30
 * @description:
 */

@Component
public class SavePluginCallEvent {

    @Autowired
    PluginCallService pluginCallService;

    public void saveEvent(YuniMessageEvent event, PassivePluginInstance instance) {
        PluginCallEntity pluginCallEntity = new PluginCallEntity();
        PluginMetadata pluginMetadata = instance.getPluginMetadata();
        pluginCallEntity.setTimeStamp(System.currentTimeMillis() / 1000L);
        pluginCallEntity.setFormatTime(YuniTimeUtil.formatTimestamp(System.currentTimeMillis() / 1000L));
        pluginCallEntity.setBotId(PluginUtils.getBotId());
        pluginCallEntity.setMessageId(event.getMessageId());
        pluginCallEntity.setToLogStr(event.getPlainLogStr());
        pluginCallEntity.setSenderId(event.getUserId());
        pluginCallEntity.setGroupId(event.getGroupId());
        pluginCallEntity.setPluginModule(pluginMetadata.getModuleName());
        pluginCallEntity.setPluginId(pluginMetadata.getId());
        pluginCallEntity.setPluginName(pluginMetadata.getName());
        pluginCallEntity.setPluginDescription(pluginMetadata.getDescription());
        pluginCallEntity.setPluginAuthor(pluginMetadata.getAuthor());
        pluginCallEntity.setPluginDetectorType(PluginBusinessMapUtil.pluginDetectorTypeName(instance.getDetector()));
        pluginCallEntity.setPluginFunctionType(pluginMetadata.getFunctionType());
        pluginCallEntity.setIcon(pluginMetadata.getIcon());
        pluginCallEntity.setHomePage(pluginMetadata.getHomepage());
        pluginCallEntity.setSourceCodeAddress(pluginMetadata.getSourceCodeAddress());
        pluginCallService.saveEvent(pluginCallEntity);
    }
}
