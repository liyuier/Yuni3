package com.yuier.yuni.core.bot;

import com.yuier.yuni.core.model.message.MessageChain;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @Title: YuniBot
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 聊天平台机器人账号抽象接口。
 *               适配器模块实现此接口，业务模块通过此接口与平台交互。
 *               所有返回类型均为协议无关的 core 层 POJO。
 */

public interface YuniBot {

    // ==================== 身份信息 ====================

    /** 平台标识，如 "onebot" */
    String getPlatform();

    /** 机器人自身账号 ID（QQ 号等） */
    String getBotId();

    /** 当前连接状态 */
    BotStatus getStatus();

    // ==================== 生命周期 ====================

    /**
     * 启动连接。包含建立传输通道、注册事件回调等。
     * @return 连接完成的 Future
     */
    CompletableFuture<Void> connect();

    /**
     * 断开连接。
     * @return 断开完成的 Future
     */
    CompletableFuture<Void> disconnect();

    /**
     * 注册业务层事件回调。适配器收到平台事件推送并完成协议翻译后调用此回调。
     * 事件回调类型由各适配器实现与业务层约定：
     * - OneBot 适配器约定回调实现 BotEventCallback（yuni-event 中定义），接收 YuniEvent。
     * 平台不支持回调时默认空实现。
     * @param callback 事件回调对象，为 null 时取消注册
     */
    default void setEventCallback(Object callback) {
        // 默认空实现，具体适配器按约定类型处理
    }

    // ==================== 消息发送 ====================

    /**
     * 发送消息
     * @param target 消息目标（群/私聊）
     * @param message 消息链
     * @return 发送结果
     */
    MessageSentResult sendMessage(MessageTarget target, MessageChain message);

    /**
     * 撤回消息
     * @param messageId 消息 ID
     */
    void recallMessage(String messageId);

    // ==================== 信息查询 ====================

    /**
     * 获取群列表
     * @return 群列表（平台不支持时返回 Optional.empty()）
     */
    Optional<List<BotGroupInfo>> getGroupList();

    /**
     * 获取群信息
     * @param groupId 群号
     * @return 群信息
     */
    Optional<BotGroupInfo> getGroupInfo(String groupId);

    /**
     * 获取群信息（可控制是否使用缓存）
     * @param groupId 群号
     * @param noCache true=穿透缓存获取最新数据, false=允许使用缓存
     * @return 群信息
     */
    Optional<BotGroupInfo> getGroupInfo(String groupId, boolean noCache);

    Optional<List<BotGroupMemberInfo>> getGroupMemberList(String groupId);
    Optional<List<BotGroupMemberInfo>> getGroupMemberList(String groupId, boolean noCache);

    /**
     * 获取群成员信息
     * @param groupId 群号
     * @param userId 用户账号
     * @return 群成员信息
     */
    Optional<BotGroupMemberInfo> getGroupMemberInfo(String groupId, String userId);

    /**
     * 获取群成员信息（可控制是否使用缓存）
     * @param groupId 群号
     * @param userId 用户账号
     * @param noCache true=穿透缓存获取最新数据, false=允许使用缓存
     * @return 群成员信息
     */
    Optional<BotGroupMemberInfo> getGroupMemberInfo(String groupId, String userId, boolean noCache);

    /**
     * 获取用户信息
     * @param userId 用户账号
     * @return 用户信息
     */
    Optional<BotUserInfo> getUserInfo(String userId);

    /**
     * 获取用户信息（可控制是否使用缓存）
     * @param userId 用户账号
     * @param noCache true=穿透缓存获取最新数据, false=允许使用缓存
     * @return 用户信息
     */
    Optional<BotUserInfo> getUserInfo(String userId, boolean noCache);

    /**
     * 获取历史消息
     * @param messageId 消息 ID
     * @return 消息内容
     */
    Optional<BotMessageInfo> getMessage(String messageId);

    // ==================== 群组管理 ====================

    /**
     * 踢出群成员
     * @param groupId 群号
     * @param userId 用户账号
     * @param rejectAddRequest 是否拒绝再次加群请求
     */
    void kickMember(String groupId, String userId, boolean rejectAddRequest);

    /**
     * 禁言群成员
     * @param groupId 群号
     * @param userId 用户账号
     * @param durationSeconds 禁言时长（秒），0 表示取消禁言
     */
    void banMember(String groupId, String userId, long durationSeconds);
}
