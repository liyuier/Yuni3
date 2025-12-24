package com.yuier.yuni.core.api.message;

import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.user.MessageSender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: GetMessage
 * @Author yuier
 * @Package com.yuier.yuni.core.api.message
 * @Date 2025/12/24 20:09
 * @description: 获取消息接口响应类
 */

@Getter
@Setter
@NoArgsConstructor
public class GetMessage {

    /**
     * 消息类型。
     * - private 私聊消息
     * - group 群聊消息
     */
    private String messageType;

    /**
     * 消息子类型
     * 私聊消息
     *   - friend 好友会话
     *   - group 群临时会话
     *   - other 其他
     * 群聊消息
     *   - normal 正常消息
     *   - anonymous 匿名消息
     *   - notice 系统提示
     */
    private String subType;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 发送者 QQ 号
     */
    private Long userId;

    /**
     * 消息内容
     */
    private List<MessageSegment> message = new ArrayList<>();

    /**
     * 原始消息内容
     */
    private String rawMessage;

    /**
     * 字体
     */
    private Long font;

    // 消息发送者
    private MessageSender sender;

    // 群号
    private Long groupId;

    /* 以下为 LLOneBot 在 OneBot 消息事件标准之外添加的字段 */

    // 消息类型，是数组还是 CQ 码
    private String messageFormat;

    // 真实 ID 就是最真实的 ID （划掉）
    // 其实这个字段在协议的 get_msg() 接口上会响应出来
    private Long realId;
}
