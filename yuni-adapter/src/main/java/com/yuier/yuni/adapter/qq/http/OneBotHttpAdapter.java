package com.yuier.yuni.adapter.qq.http;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.group.GroupInfo;
import com.yuier.yuni.core.api.group.GroupListElement;
import com.yuier.yuni.core.api.message.GetMessage;
import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.OneBotSerialization;

import java.util.List;

/**
 * @Title: OneBotHttpAdapter
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.http
 * @Date 2025/12/22 3:57
 * @description: OneBot HTTP 通信方式
 */

public class OneBotHttpAdapter implements OneBotAdapter {

    private OneBotDeserializer deserializer;
    private OneBotApiClient apiClient;
    private OneBotSerialization serialization;

    public OneBotHttpAdapter(OneBotDeserializer deserializer, OneBotSerialization serialization, OneBotApiClient apiClient) {
        this.deserializer = deserializer;
        this.apiClient = apiClient;
        this.serialization = serialization;
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

    /**
     * 撤回消息
     */
    @Override
    public void deleteMessage(long messageId) {
        apiClient.deleteMessage(messageId);
    }

    /**
     * 群组踢人
     */
    @Override
    public void setGroupKick(long groupId, long userId, boolean rejectAddRequest) {
        apiClient.setGroupKick(groupId, userId, rejectAddRequest);
    }

    /**
     * 群组单人禁言
     */
    @Override
    public void setGroupBan(long groupId, long userId, long duration) {
        apiClient.setGroupBan(groupId, userId, duration);
    }

    /**
     * 获取登录号信息
     */
    @Override
    public OneBotResponse getLoginInfo() {
        return apiClient.getLoginInfo();
    }

    /**
     * 获取陌生人信息
     */
    @Override
    public OneBotResponse getStrangerInfo(long userId, boolean noCache) {
        return apiClient.getStrangerInfo(userId, noCache);
    }

    /**
     * 获取群信息
     */
    @Override
    public GroupInfo getGroupInfo(long groupId, boolean noCache) {
        OneBotResponse msg = apiClient.getGroupInfo(groupId, noCache);
        return getData(msg, GroupInfo.class);
    }

    /**
     * 获取群成员信息
     */
    @Override
    public OneBotResponse getGroupMemberInfo(long groupId, long userId, boolean noCache) {
        return apiClient.getGroupMemberInfo(groupId, userId, noCache);
    }

    /**
     * 获取消息
     * @param messageId 消息ID
     * @return 消息内容
     */
    @Override
    public GetMessage getMsg(long messageId) {
        OneBotResponse msg = apiClient.getMsg(messageId);
        return getData(msg, GetMessage.class);
    }

    @Override
    public List<GroupListElement> getGroupList() {
        OneBotResponse msg = apiClient.getGroupList();
        return List.of(getData(msg, GroupListElement[].class));
    }


    @Override
    public void sendGroupMessage(long groupId, MessageChain message) {
        apiClient.sendGroupMessage(groupId, message);
    }

    @Override
    public void sendPrivateMessage(long userId, MessageChain message) {
        apiClient.sendPrivateMessage(userId, message);
    }

    // 从 OneBotResponse 的 data 字段中通过 ObjectMapper 先序列化再反序列化得到需要的类
    private <T> T getData(OneBotResponse response, Class<T> clazz) {
        String rawJson = null;
        try {
            rawJson = serialization.serialize(response.getData());
            return deserializer.deserialize(rawJson, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
