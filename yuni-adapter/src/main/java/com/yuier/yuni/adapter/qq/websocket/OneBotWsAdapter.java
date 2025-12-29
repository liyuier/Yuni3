package com.yuier.yuni.adapter.qq.websocket;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.group.GroupInfo;
import com.yuier.yuni.core.api.group.GroupListElement;
import com.yuier.yuni.core.api.group.GroupMemberInfo;
import com.yuier.yuni.core.api.message.GetMessage;
import com.yuier.yuni.core.api.message.GetRecord;
import com.yuier.yuni.core.api.message.SendGroupMessage;
import com.yuier.yuni.core.api.message.SendPrivateMessage;
import com.yuier.yuni.core.api.system.LoginInfo;
import com.yuier.yuni.core.api.user.GetStrangerInfo;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketManager;
import com.yuier.yuni.core.util.OneBotDeserializer;
import lombok.Data;

import java.util.List;

/**
 * @Title: OneBotWsAdapter
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.websocket
 * @Date 2025/12/28 23:26
 * @description:
 */

@Data
public class OneBotWsAdapter implements OneBotAdapter {

    private OneBotDeserializer deserializer;
    private OneBotApiWsClient apiClient;
    private OneBotWsSessionStarter wsSessionStarter;
    private YuniWebSocketManager manager;

    public OneBotWsAdapter(OneBotDeserializer deserializer, OneBotWsSessionStarter oneBotWsSessionStarter, YuniWebSocketManager webSocketManager) {
        this.deserializer = deserializer;
        wsSessionStarter = oneBotWsSessionStarter;
        manager = webSocketManager;
    }

    @Override
    public void startListening() {

    }

    @Override
    public void stopListening() {

    }

    @Override
    public OneBotEvent handleReportJson(String json) {
        try {
            return deserializer.deserializeEvent(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SendGroupMessage sendGroupMessage(long groupId, MessageChain message) {
        return apiClient.sendGroupMessage(groupId, message);
    }

    @Override
    public SendPrivateMessage sendPrivateMessage(long userId, MessageChain message) {
        return apiClient.sendPrivateMessage(userId, message);
    }

    @Override
    public void deleteMessage(long messageId) {

    }

    @Override
    public void setGroupKick(long groupId, long userId, boolean rejectAddRequest) {

    }

    @Override
    public void setGroupBan(long groupId, long userId, long duration) {

    }

    @Override
    public LoginInfo getLoginInfo() {
        return apiClient.getLoginInfo();
    }

    @Override
    public GetStrangerInfo getStrangerInfo(long userId, boolean noCache) {
        return apiClient.getStrangerInfo(userId, noCache);
    }

    @Override
    public GetMessage getMsg(long messageId) {
        return apiClient.getMsg(messageId);
    }

    @Override
    public GetRecord getRecord(String file, String outFormat) {
        return apiClient.getRecord(file, outFormat);
    }

    @Override
    public GroupInfo getGroupInfo(long groupId, boolean noCache) {
        return apiClient.getGroupInfo(groupId, noCache);
    }

    @Override
    public List<GroupListElement> getGroupList() {
        return apiClient.getGroupList();
    }

    @Override
    public GroupMemberInfo getGroupMemberInfo(long groupId, long userId, boolean noCache) {
        return apiClient.getGroupMemberInfo(groupId, userId, noCache);
    }
}
