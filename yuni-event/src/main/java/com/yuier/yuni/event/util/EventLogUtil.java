package com.yuier.yuni.event.util;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.group.GroupInfo;
import com.yuier.yuni.core.api.group.GroupMemberInfo;
import com.yuier.yuni.core.api.user.GetStrangerInfo;
import com.yuier.yuni.core.util.LogStringUtil;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.event.context.YuniMessageEvent;
import org.springframework.stereotype.Component;


/**
 * @Title: EventLogUtil
 * @Author yuier
 * @Package com.yuier.yuni.core.util
 * @Date 2025/12/24 18:07
 * @description:
 */

@Component
public class EventLogUtil {

    /**
     * 将消息事件转为日志
     * {群/私} -> {bot}|[群聊(群号)]{发送人(发送人QQ)}: {消息}
     * @param event 消息事件
     * @return 日志
     */
    public static String toLog(YuniMessageEvent event) {
        // 群/私->收丨[群聊(群号)]发送人(发送人QQ): 消息
        String receiveDescription = "";
        String groupInfoLog = "";
        String senderInfoLog = "";
        String messageLog = "";
        String senderName = event.getSender().getNickname();
        if (event.isGroup()) {
            receiveDescription = LogStringUtil.buildPurpleLog("群->收");
            Long groupId = event.getGroupId();
            String groupIdStr = String.valueOf(groupId);
            String groupName = getOneBotAdapter().getGroupInfo(groupId, true).getGroupName();
            groupInfoLog = "[" + LogStringUtil.buildBrightRedLog(groupName) + "(" + groupIdStr + ")]";
            String card = event.getSender().getCard();
            senderName = card != null && !card.isEmpty() ? card : senderName;
        } else if (event.isPrivate()) {
            receiveDescription = LogStringUtil.buildPurpleLog("私->收");
        }
        senderInfoLog = LogStringUtil.buildCyanLog(senderName) + "(" + event.getUserId() + "): ";
        messageLog = LogStringUtil.buildBrightBlueLog(event.getMessageChain().toString());
        String logStr = receiveDescription + "丨" + groupInfoLog + senderInfoLog + messageLog;
        return LogStringUtil.escapeString(logStr);
    }

    public static String toPlainLog(YuniMessageEvent event) {
        // 群/私->收丨[群聊(群号)]发送人(发送人QQ): 纯文本消息
        String receiveDescription = "";
        String groupInfoLog = "";
        String senderInfoLog = "";
        String messageLog = "";
        String senderName = event.getSender().getNickname();
        if (event.isGroup()) {
            receiveDescription = "群->收";
            Long groupId = event.getGroupId();
            String groupIdStr = String.valueOf(groupId);
            String groupName = getOneBotAdapter().getGroupInfo(groupId, true).getGroupName();
            groupInfoLog = "[" + groupName + "(" + groupIdStr + ")]";
            String card = event.getSender().getCard();
            senderName = card != null && !card.isEmpty() ? card : senderName;
        } else if (event.isPrivate()) {
            receiveDescription = "私->收";
        }
        senderInfoLog = senderName+ "(" + event.getUserId() + "): ";
        messageLog = event.getMessageChain().toString();
        String logStr = receiveDescription + "丨" + groupInfoLog + senderInfoLog + messageLog;
        return LogStringUtil.escapeString(logStr);
    }

    private static OneBotAdapter getOneBotAdapter() {
        return SpringContextUtil.getBean(OneBotAdapter.class);
    }

    /**
     * 获取群成员信息
     * @param groupId 群号
     * @param userId 成员QQ号
     * @return 群成员信息
     */
    public static GroupMemberInfo getGroupMemberInfo(Long groupId, Long userId) {
        return getOneBotAdapter().getGroupMemberInfo(groupId, userId, true);
    }

    /**
     * 获取群成员名称
     * @param groupId 群号
     * @param userId 成员QQ号
     * @return 成员名称
     */
    public static String getGroupMemberName(Long groupId, Long userId) {
        GroupMemberInfo groupMemberInfo = getGroupMemberInfo(groupId, userId);
        return groupMemberInfo.getCardOrNickname();
    }

    public static GroupInfo getGroupInfo(Long groupId) {
        return getOneBotAdapter().getGroupInfo(groupId, true);
    }

    public static String getGroupName(Long groupId) {
        GroupInfo groupInfo = getGroupInfo(groupId);
        return groupInfo.getGroupName();
    }

    /**
     * 拼接 "用户: xxx(1234567) 在 群名yyy(7654321) " 的描述字符串
     * @param groupId 群号
     * @param userId 群成员QQ号
     * @return 群成员在群中的描述
     */
    public static String memberAtGroupLogString(Long groupId, Long userId) {
        return "用户: " + EventLogUtil.getGroupMemberName(groupId, userId) + "(" + userId + ") " +
                "在 " + EventLogUtil.getGroupName(groupId) + "(" + groupId + ") ";
    }

    /**
     * 拼接 "用户: xxx(1234567) " 的描述字符串
     * @param groupId 群号
     * @param userId 群成员QQ号
     * @return 群成员在群中的描述
     */
    public static String memberNameAndIdLogString(Long groupId, Long userId) {
        return "用户: " + EventLogUtil.getGroupMemberName(groupId, userId) + "(" + userId + ") ";
    }

    public static String groupNameAndIdLogString(Long groupId) {
        return "群: " + EventLogUtil.getGroupName(groupId) + "(" + groupId + ") ";
    }

    public static String userNameAndIdLogString(Long userId) {
        GetStrangerInfo strangerInfo = getOneBotAdapter().getStrangerInfo(userId, true);
        return "好友: " + strangerInfo.getNickname() + "(" + userId + ") ";
    }
}
