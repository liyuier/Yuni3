package com.yuier.yuni.webapi.service.group;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.core.bot.BotGroupInfo;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.event.domain.entity.ReceiveMessageEntity;
import com.yuier.yuni.event.service.ReceiveMessageService;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.enable.PluginEnableProcessor;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.webapi.dto.group.GroupMessageItem;
import com.yuier.yuni.webapi.dto.group.GroupMessagesResp;
import com.yuier.yuni.webapi.dto.group.GroupPluginStatusItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 群组管理服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private final ReceiveMessageService receiveMessageService;
    private final PluginContainer pluginContainer;
    private final PluginEnableProcessor pluginEnableProcessor;
    private final ObjectMapper objectMapper;
    private final ImageRkeyService imageRkeyService;

    /**
     * 获取群组列表。
     * @return 群组信息列表
     */
    public List<BotGroupInfo> listGroups() {
        YuniBot yuniBot = SpringContextUtil.getBean(YuniBot.class);
        return yuniBot.getGroupList().orElse(List.of());
    }

    /**
     * 获取指定群的聊天记录。
     * @param groupId 群号
     * @param page    页码
     * @param size    每页条数
     * @return 消息列表 + 总数
     */
    public GroupMessagesResp getMessages(Long groupId, int page, int size) {
        List<ReceiveMessageEntity> entities = receiveMessageService.listMessagesByGroup(groupId, page, size);
        List<GroupMessageItem> items = entities.stream()
                .map(e -> {
                    List<Map<String, Object>> segments = parseSegments(e);
                    imageRkeyService.refreshSegments(segments);
                    return new GroupMessageItem(
                            e.getSenderName(),
                            e.getRawMessage() != null ? e.getRawMessage() : "",
                            e.getFormatTime(),
                            e.getIsSelfSent() != null && e.getIsSelfSent(),
                            e.getIsPlainText() != null && e.getIsPlainText(),
                            segments
                    );
                })
                .toList();
        long total = receiveMessageService.countMessagesByGroup(groupId);
        return new GroupMessagesResp(items, total);
    }

    /**
     * 解析 rawJson 为消息段数组。解析失败返回空列表。
     */
    private List<Map<String, Object>> parseSegments(ReceiveMessageEntity e) {
        String json = e.getMessageSegments();
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception ex) {
            log.debug("解析消息段失败: {}", e.getMessageId(), ex);
            return List.of();
        }
    }

    /**
     * 获取指定群的插件启用状态。
     * @param groupId 群号
     * @return 插件启用状态列表
     */
    public List<GroupPluginStatusItem> getPluginStatus(Long groupId) {
        List<GroupPluginStatusItem> result = new ArrayList<>();
        for (String fullId : pluginContainer.getPluginFullIds()) {
            PluginInstance instance = pluginContainer.getPluginInstanceByFullId(fullId);
            if (instance == null) continue;
            boolean enabled;
            try {
                enabled = pluginEnableProcessor.isPluginEnabled(groupId, instance.getPluginClass());
            } catch (Exception e) {
                enabled = instance.getPluginMetadata().getDefaultEnable();
            }
            result.add(new GroupPluginStatusItem(
                    fullId,
                    instance.getPluginName(),
                    instance.getPluginType().name(),
                    enabled
            ));
        }
        return result;
    }
}
