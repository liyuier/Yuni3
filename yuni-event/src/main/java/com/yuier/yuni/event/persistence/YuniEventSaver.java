package com.yuier.yuni.event.persistence;

import com.yuier.yuni.core.util.YuniTimeUtil;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.context.YuniMessageSentEvent;
import com.yuier.yuni.event.domain.entity.ReceiveMessageEntity;
import com.yuier.yuni.event.service.ReceiveMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        receiveMessageEntity.setToLogStr(event.toPlainLogString());
        receiveMessageEntity.setRawMessage(event.getRawMessage());
        receiveMessageEntity.setRawJson(event.getRawJson());
        receiveMessageEntity.setGroupId(event.getGroupId());
        receiveMessageEntity.setMessageFormat(event.getMessageFormat());
        receiveMessageEntity.setRealId(event.getRealId());
        receiveMessageEntity.setIsPlainText(event.getMessageChain().isPlainText());
        receiveMessageEntity.setIsSelfSent(false);
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
        receiveMessageEntity.setToLogStr(event.toPlainLogString());
        receiveMessageEntity.setRawMessage(event.getRawMessage());
        receiveMessageEntity.setRawJson(event.getRawJson());
        receiveMessageEntity.setGroupId(event.getGroupId());
        receiveMessageEntity.setMessageFormat(event.getMessageFormat());
        receiveMessageEntity.setRealId(event.getRealId());
        receiveMessageEntity.setIsPlainText(event.getMessageChain().isPlainText());
        receiveMessageEntity.setIsSelfSent(true);
        receiveMessageService.saveEvent(receiveMessageEntity);
    }
}
