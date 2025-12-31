package com.yuier.yuni.adapter.qq.websocket;

import com.yuier.yuni.adapter.qq.websocket.module.WsRequest;
import com.yuier.yuni.core.api.group.GroupInfo;
import com.yuier.yuni.core.api.group.GroupListElement;
import com.yuier.yuni.core.api.group.GroupMemberInfo;
import com.yuier.yuni.core.api.message.GetMessage;
import com.yuier.yuni.core.api.message.GetRecord;
import com.yuier.yuni.core.api.message.SendGroupMessage;
import com.yuier.yuni.core.api.message.SendPrivateMessage;
import com.yuier.yuni.core.api.system.LoginInfo;
import com.yuier.yuni.core.api.user.GetStrangerInfo;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketConnector;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.core.util.SpringContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: OneBotApiWsClient
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.websocket
 * @Date 2025/12/29 16:09
 * @description:  到 /api 的 ws 请求客户端
 */

public class OneBotApiWsClient {

    // 持有一下到 /api 的 ws 连接
    private YuniWebSocketConnector connector;
    private OneBotDeserializer deserializer;
    private OneBotSerialization serialization;

    public OneBotApiWsClient(YuniWebSocketConnector apiConnector, OneBotDeserializer deserializer, OneBotSerialization serialization) {
        connector = apiConnector;
        this.deserializer = deserializer;
        this.serialization = serialization;
    }

    private <T> T quickSendOneBotApiRequest(WsRequest request, Class<T> responseClass) {
        try {
            String responseDataJson = connector.sendAndReceive(serialization.serialize(request), request.getEcho());
            return deserializer.deserialize(responseDataJson, responseClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SendGroupMessage sendGroupMessage(long groupId, MessageChain message) {
        String action = "send_group_msg";
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("message", message.getContent());
        WsRequest request = new WsRequest(action, params);
        return quickSendOneBotApiRequest(request, SendGroupMessage.class);
    }

    public SendPrivateMessage sendPrivateMessage(long userId, MessageChain message) {
        String action = "send_private_msg";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("message", message.getContent());
        WsRequest request = new WsRequest(action, params);
        return quickSendOneBotApiRequest(request, SendPrivateMessage.class);
    }

    public LoginInfo getLoginInfo() {
        String action = "send_private_msg";
        WsRequest request = new WsRequest(action);
        return quickSendOneBotApiRequest(request, LoginInfo.class);
    }

    public GetStrangerInfo getStrangerInfo(long userId, boolean noCache) {
        String action = "get_stranger_info";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("no_cache", noCache);
        WsRequest request = new WsRequest(action, params);
        return quickSendOneBotApiRequest(request, GetStrangerInfo.class);
    }

    public GetMessage getMsg(long messageId) {
        String action = "get_msg";
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        WsRequest request = new WsRequest(action, params);
        return quickSendOneBotApiRequest(request, GetMessage.class);
    }

    public GetRecord getRecord(String file, String outFormat) {
        String action = "get_record";
        Map<String, Object> params = new HashMap<>();
        params.put("file", file);
        params.put("out_format", outFormat);
        WsRequest request = new WsRequest(action, params);
        return quickSendOneBotApiRequest(request, GetRecord.class);
    }

    public GroupInfo getGroupInfo(long groupId, boolean noCache) {
        String action = "get_group_info";
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("no_cache", noCache);
        WsRequest request = new WsRequest(action, params);
        return quickSendOneBotApiRequest(request, GroupInfo.class);
    }

    public List<GroupListElement> getGroupList() {
        String action = "get_group_list";
        WsRequest request = new WsRequest(action);
        GroupListElement[] groupListElements = quickSendOneBotApiRequest(request, GroupListElement[].class);
        return List.of(groupListElements);
    }

    public GroupMemberInfo getGroupMemberInfo(long groupId, long userId, boolean noCache) {
        String action = "get_group_member_info";
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("no_cache", noCache);
        WsRequest request = new WsRequest(action, params);
        return quickSendOneBotApiRequest(request, GroupMemberInfo.class);
    }
}
