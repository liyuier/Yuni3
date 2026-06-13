package com.yuier.yuni.event.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.util.YuniTimeUtil;
import com.yuier.yuni.core.event.YuniMessageEvent;
import com.yuier.yuni.core.event.YuniMessageSentEvent;
import com.yuier.yuni.event.domain.entity.ReceiveMessageEntity;
import com.yuier.yuni.event.service.ReceiveMessageService;
import com.yuier.yuni.event.util.EventLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Title: YuniEventSaver
 * @Author yuier
 * @Package com.yuier.yuni.event.persistence
 * @Date 2025/12/27 1:30
 * @description: 接收发送消息事件
 */

@Component
public class YuniEventSaver {

    @Autowired
    ReceiveMessageService receiveMessageService;

    @Autowired
    ObjectMapper objectMapper;

    public void saveEvent(YuniMessageEvent event) {
        ReceiveMessageEntity receiveMessageEntity = new ReceiveMessageEntity();
        receiveMessageEntity.setTimeStamp(event.getTime());
        receiveMessageEntity.setFormatTime(YuniTimeUtil.formatTimestamp(event.getTime()));
        receiveMessageEntity.setSelfId(event.getSelfId());
        receiveMessageEntity.setMessageType(event.getMessageType());
        receiveMessageEntity.setSubType(event.getSubType());
        receiveMessageEntity.setMessageId(event.getMessageId());
        receiveMessageEntity.setSenderId(event.getUserId());
        receiveMessageEntity.setSenderName(event.getSender().getNickname());
        receiveMessageEntity.setRole(event.getSender().getRole());
        receiveMessageEntity.setToLogStr(EventLogUtil.toPlainLog(event));
        receiveMessageEntity.setRawMessage(event.getRawMessage());
        receiveMessageEntity.setRawJson(event.getRawJson());
        receiveMessageEntity.setGroupId(event.getGroupId());
        receiveMessageEntity.setMessageFormat(event.getMessageFormat());
        receiveMessageEntity.setRealId(event.getRealId());
        receiveMessageEntity.setIsPlainText(event.getMessageChain().isPlainText());
        receiveMessageEntity.setIsSelfSent(false);
        serializeMessageSegments(receiveMessageEntity, event.getMessage());
        receiveMessageService.saveEvent(receiveMessageEntity);
    }

    public void saveEvent(YuniMessageSentEvent event) {
        ReceiveMessageEntity receiveMessageEntity = new ReceiveMessageEntity();
        receiveMessageEntity.setTimeStamp(event.getTime());
        receiveMessageEntity.setFormatTime(YuniTimeUtil.formatTimestamp(event.getTime()));
        receiveMessageEntity.setSelfId(event.getSelfId());
        receiveMessageEntity.setMessageType(event.getMessageType());
        receiveMessageEntity.setSubType(event.getSubType());
        receiveMessageEntity.setMessageId(event.getMessageId());
        receiveMessageEntity.setSenderId(event.getUserId());
        receiveMessageEntity.setSenderName(event.getSender().getNickname());
        receiveMessageEntity.setRole(event.getSender().getRole());
        receiveMessageEntity.setToLogStr(EventLogUtil.toPlainLog(event));
        receiveMessageEntity.setRawMessage(event.getRawMessage());
        receiveMessageEntity.setRawJson(event.getRawJson());
        receiveMessageEntity.setGroupId(event.getGroupId());
        receiveMessageEntity.setMessageFormat(event.getMessageFormat());
        receiveMessageEntity.setRealId(event.getRealId());
        receiveMessageEntity.setIsPlainText(event.getMessageChain().isPlainText());
        receiveMessageEntity.setIsSelfSent(true);
        serializeMessageSegments(receiveMessageEntity, event.getMessage());
        receiveMessageService.saveEvent(receiveMessageEntity);
    }

    private void serializeMessageSegments(ReceiveMessageEntity entity, List<MessageSegment> segments) {
        try {
            entity.setMessageSegments(objectMapper.writeValueAsString(segments));
        } catch (Exception e) {
            // 序列化失败不阻塞事件保存
        }
    }
}
