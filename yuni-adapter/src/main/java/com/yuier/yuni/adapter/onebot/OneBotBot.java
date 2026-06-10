package com.yuier.yuni.adapter.onebot;

import com.yuier.yuni.adapter.config.OneBotProperties;
import com.yuier.yuni.adapter.onebot.model.OneBotEvent;
import com.yuier.yuni.adapter.onebot.transport.OneBotTransport;
import com.yuier.yuni.adapter.onebot.api.group.GroupInfo;
import com.yuier.yuni.adapter.onebot.api.group.GroupMemberInfo;
import com.yuier.yuni.adapter.onebot.api.message.GetMessage;
import com.yuier.yuni.adapter.onebot.api.message.SendGroupMessage;
import com.yuier.yuni.adapter.onebot.api.message.SendPrivateMessage;
import com.yuier.yuni.adapter.onebot.api.user.GetStrangerInfo;
import com.yuier.yuni.core.bot.*;
import com.yuier.yuni.core.event.YuniEvent;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.event.BotEventCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Title: OneBotBot
 * @Author yuier
 * @Package com.yuier.yuni.adapter.onebot
 * @Date 2026/06/09
 * @description: YuniBot 的 OneBot 实现。
 *               在适配器内部完成: raw JSON → OneBotEvent → YuniEvent，
 *               然后通过 BotEventCallback 将装配好的 YuniEvent 传递给业务层。
 */

@Slf4j
public class OneBotBot implements YuniBot {

    private final OneBotProperties properties;
    private final OneBotProtocolHandler protocolHandler;
    private final OneBotTransport transport;
    private final JsonCodec jsonCodec;
    private final EventTranslator eventTranslator;
    private BotEventCallback eventCallback;
    private BotStatus status = BotStatus.OFFLINE;
    private final String botId;

    public OneBotBot(OneBotProperties properties,
                     OneBotProtocolHandler protocolHandler,
                     OneBotTransport transport,
                     JsonCodec jsonCodec,
                     String botId) {
        this.properties = properties;
        this.protocolHandler = protocolHandler;
        this.transport = transport;
        this.jsonCodec = jsonCodec;
        this.botId = botId;
        this.eventTranslator = new EventTranslator(this);
    }

    // ==================== 身份信息 ====================

    @Override
    public String getPlatform() {
        return "onebot";
    }

    @Override
    public String getBotId() {
        return botId;
    }

    @Override
    public BotStatus getStatus() {
        return status;
    }

    // ==================== 生命周期 ====================

    @Override
    public CompletableFuture<Void> connect() {
        status = BotStatus.CONNECTING;
        // 传输层收到原始 JSON 后：反序列化为 OneBotEvent → 翻译为 YuniEvent → 回调业务层
        transport.setEventCallback(rawJson -> {
            try {
                OneBotEvent oneBotEvent = protocolHandler.deserializeEvent(rawJson);
                oneBotEvent.setRawJson(rawJson);
                YuniEvent yuniEvent = eventTranslator.translate(oneBotEvent);
                if (eventCallback != null) {
                    eventCallback.onEvent(yuniEvent);
                }
            } catch (Exception e) {
                log.error("[OneBotBot] 事件处理失败: {}", rawJson, e);
            }
        });

        return transport.connect().thenRun(() -> {
            status = BotStatus.ONLINE;
            log.info("[OneBotBot] OneBot 机器人 {} 已上线", botId);
        }).exceptionally(e -> {
            status = BotStatus.ERROR;
            log.error("[OneBotBot] 连接失败", e);
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        status = BotStatus.OFFLINE;
        return transport.disconnect();
    }

    @Override
    public void setEventCallback(Object callback) {
        if (callback instanceof BotEventCallback) {
            this.eventCallback = (BotEventCallback) callback;
        } else {
            log.warn("[OneBotBot] 不支持的回调类型: {}", callback != null ? callback.getClass().getName() : "null");
        }
    }

    // ==================== 消息发送 ====================

    @Override
    public MessageSentResult sendMessage(MessageTarget target, MessageChain message) {
        try {
            return executeWithRetry(() -> {
                Map<String, Object> params = new HashMap<>();
                params.put("message", message.getContent());
                if (target.getTargetType() == MessageTarget.TargetType.GROUP) {
                    params.put("group_id", target.getTargetId());
                    String responseJson = transport.sendApiRequest("send_group_msg", params);
                    SendGroupMessage result = extractResponseData(responseJson, SendGroupMessage.class);
                    return MessageSentResult.ok(String.valueOf(result.getMessageId()));
                } else {
                    params.put("user_id", target.getTargetId());
                    String responseJson = transport.sendApiRequest("send_private_msg", params);
                    SendPrivateMessage result = extractResponseData(responseJson, SendPrivateMessage.class);
                    return MessageSentResult.ok(String.valueOf(result.getMessageId()));
                }
            }, "sendMessage", properties.getMaxRetries());
        } catch (Exception e) {
            log.error("[OneBotBot] 消息发送失败", e);
            return MessageSentResult.fail(e.getMessage());
        }
    }

    @Override
    public void recallMessage(String messageId) {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", Long.parseLong(messageId));
        transport.sendApiRequest("delete_msg", params);
    }

    // ==================== 信息查询 ====================

    @Override
    public Optional<List<BotGroupInfo>> getGroupList() {
        try {
            String responseJson = transport.sendApiRequest("get_group_list", Map.of());
            GroupInfo[] groups = extractResponseData(responseJson, GroupInfo[].class);
            if (groups == null) return Optional.empty();
            return Optional.of(Arrays.stream(groups).map(this::convert).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("[OneBotBot] 获取群列表失败", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<BotGroupInfo> getGroupInfo(String groupId) {
        return getGroupInfo(groupId, true);
    }

    @Override
    public Optional<BotGroupInfo> getGroupInfo(String groupId, boolean noCache) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("group_id", Long.parseLong(groupId));
            params.put("no_cache", noCache);
            String responseJson = transport.sendApiRequest("get_group_info", params);
            return Optional.ofNullable(convert(extractResponseData(responseJson, GroupInfo.class)));
        } catch (Exception e) {
            log.error("[OneBotBot] 获取群信息失败: groupId={}", groupId, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<BotGroupMemberInfo> getGroupMemberInfo(String groupId, String userId) {
        return getGroupMemberInfo(groupId, userId, true);
    }

    @Override
    public Optional<BotGroupMemberInfo> getGroupMemberInfo(String groupId, String userId, boolean noCache) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("group_id", Long.parseLong(groupId));
            params.put("user_id", Long.parseLong(userId));
            params.put("no_cache", noCache);
            String responseJson = transport.sendApiRequest("get_group_member_info", params);
            return Optional.ofNullable(convert(extractResponseData(responseJson, GroupMemberInfo.class)));
        } catch (Exception e) {
            log.error("[OneBotBot] 获取群成员信息失败: groupId={} userId={}", groupId, userId, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<BotUserInfo> getUserInfo(String userId) {
        return getUserInfo(userId, true);
    }

    @Override
    public Optional<BotUserInfo> getUserInfo(String userId, boolean noCache) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", Long.parseLong(userId));
            params.put("no_cache", noCache);
            String responseJson = transport.sendApiRequest("get_stranger_info", params);
            return Optional.ofNullable(convert(extractResponseData(responseJson, GetStrangerInfo.class)));
        } catch (Exception e) {
            log.error("[OneBotBot] 获取用户信息失败: userId={}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<BotMessageInfo> getMessage(String messageId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("message_id", Long.parseLong(messageId));
            String responseJson = transport.sendApiRequest("get_msg", params);
            return Optional.ofNullable(convert(extractResponseData(responseJson, GetMessage.class)));
        } catch (Exception e) {
            log.error("[OneBotBot] 获取消息失败: messageId={}", messageId, e);
            return Optional.empty();
        }
    }

    // ==================== 群组管理 ====================

    @Override
    public void kickMember(String groupId, String userId, boolean rejectAddRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", Long.parseLong(groupId));
        params.put("user_id", Long.parseLong(userId));
        params.put("reject_add_request", rejectAddRequest);
        transport.sendApiRequest("set_group_kick", params);
    }

    @Override
    public void banMember(String groupId, String userId, long durationSeconds) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", Long.parseLong(groupId));
        params.put("user_id", Long.parseLong(userId));
        params.put("duration", durationSeconds);
        transport.sendApiRequest("set_group_ban", params);
    }

    // ==================== OneBot API 类型 → 核心类型转换 ====================

    private BotGroupInfo convert(GroupInfo g) {
        if (g == null) return null;
        BotGroupInfo b = new BotGroupInfo();
        b.setGroupId(g.getGroupId());
        b.setGroupName(g.getGroupName());
        b.setGroupMemo(g.getGroupMemo());
        b.setMemberCount(g.getMemberCount());
        b.setMaxMemberCount(g.getMaxMemberCount());
        b.setGroupCreateTime(g.getGroupCreateTime());
        return b;
    }

    private BotGroupMemberInfo convert(GroupMemberInfo m) {
        if (m == null) return null;
        BotGroupMemberInfo b = new BotGroupMemberInfo();
        b.setGroupId(m.getGroupId());
        b.setUserId(m.getUserId());
        b.setNickname(m.getNickname());
        b.setCard(m.getCard());
        b.setRole(m.getRole());
        b.setTitle(m.getTitle());
        return b;
    }

    private BotUserInfo convert(GetStrangerInfo u) {
        if (u == null) return null;
        BotUserInfo b = new BotUserInfo();
        b.setUserId(u.getUserId());
        b.setNickname(u.getNickname());
        return b;
    }

    private BotMessageInfo convert(GetMessage m) {
        if (m == null) return null;
        BotMessageInfo b = new BotMessageInfo();
        b.setMessageId(m.getMessageId());
        b.setUserId(m.getUserId());
        b.setGroupId(m.getGroupId());
        b.setMessageType(m.getMessageType());
        return b;
    }

    // ==================== 重试工具 ====================

    /**
     * 对可能因断联/超时等瞬时故障失败的操作进行重试。
     * 传输层（OneBotWsTransport）已处理一次断联重试；
     * 此处作为业务层的兜底，处理剩余的瞬时失败。
     */
    private <T> T executeWithRetry(RetryableOperation<T> operation, String actionName, int maxRetries) {
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return operation.execute();
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    log.error("[OneBotBot] {} 失败，已重试{}次: {}", actionName, maxRetries, e.getMessage());
                    throw new RuntimeException("已到达最大重试次数: " + maxRetries, e);
                }
                log.warn("[OneBotBot] {} 失败，第{}/{}次重试: {}", actionName, attempt + 1, maxRetries, e.getMessage());
                try {
                    Thread.sleep((long) properties.getRetryBackoffBaseMs() * (attempt + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断: " + actionName, e);
                }
            }
        }
        throw new RuntimeException("不可达: " + actionName); // 逻辑上不会到达
    }

    @FunctionalInterface
    private interface RetryableOperation<T> {
        T execute() throws Exception;
    }

    // ==================== 响应解析 ====================

    @SuppressWarnings("unchecked")
    private <T> T extractResponseData(String responseJson, Class<T> clazz) {
        try {
            Map<String, Object> responseMap = jsonCodec.fromJson(responseJson, Map.class);
            Object data = responseMap.get("data");
            if (data == null) return null;
            return jsonCodec.convertValue(data, clazz);
        } catch (Exception e) {
            log.error("[OneBotBot] 响应解析失败: target={}", clazz.getName(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T extractResponseDataAsArray(String responseJson, Class<T> arrayClass) {
        try {
            Map<String, Object> responseMap = jsonCodec.fromJson(responseJson, Map.class);
            Object data = responseMap.get("data");
            if (data == null) return null;
            return jsonCodec.convertValue(data, arrayClass);
        } catch (Exception e) {
            log.error("[OneBotBot] 响应解析失败", e);
            return null;
        }
    }
}
