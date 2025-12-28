package com.yuier.yuni.event.context;

import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.user.MessageSender;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.event.message.detector.command.model.matched.CommandMatched;
import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.yuier.yuni.core.constants.OneBotMessageType.GROUP_MESSAGE;
import static com.yuier.yuni.core.constants.OneBotMessageType.PRIVATE_MESSAGE;
import static com.yuier.yuni.core.constants.SystemConstants.UNKNOWN;

/**
 * @Title: YuniMessageEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event.spring
 * @Date 2025/12/22 17:19
 * @description: Yuni 机器人内部定义的消息事件
 */

@Data
public class YuniMessageEvent extends SpringYuniEvent {

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

    // 消息链
    private MessageChain messageChain;

    // 会话状态
    private ChatSession chatSession;

    // 命令匹配结果
    private CommandMatched commandMatched;

    private MessageEvent messageEvent;

    /**
     * 纯日志字符串
     */
    private String plainLogStr;

    public Boolean isPrivate() {
        return PRIVATE_MESSAGE.equals(messageType);
    }

    public Boolean isGroup() {
        return GROUP_MESSAGE.equals(messageType);
    }

    @Override
    public String toLogString() {
        return EventLogUtil.toLog(this);
    }

    public String toPlainLogString() {
        String plainLog = EventLogUtil.toPlainLog(this);
        setPlainLogStr(plainLog);
        return plainLog;
    }

    public String getPosition() {
        if (isPrivate()) {
            return PRIVATE_MESSAGE + "@" + userId;
        } else if (isGroup()) {
            return GROUP_MESSAGE + "@" + groupId;
        } else {
            return UNKNOWN + "@" + userId;
        }
    }

}
