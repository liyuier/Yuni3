package com.yuier.yuni.event.model.util;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.util.LogStringUtil;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.event.model.context.YuniMessageEvent;
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
        if (event.isGroup()) {
            receiveDescription = LogStringUtil.buildPurpleLog("群->收");
            Long groupId = event.getGroupId();
            String groupIdStr = String.valueOf(groupId);
            String groupName = getOneBotAdapter().getGroupInfo(groupId, true).getGroupName();
            groupInfoLog = "[" + LogStringUtil.buildBrightRedLog(groupName) + "(" + groupIdStr + ")]";
        } else if (event.isPrivate()) {
            receiveDescription = LogStringUtil.buildPurpleLog("私->收");
        }
        senderInfoLog = LogStringUtil.buildCyanLog(event.getSender().getNickname()) + "(" + event.getUserId() + "): ";
        messageLog = LogStringUtil.buildBrightBlueLog(event.getMessageChain().toString());
        String logStr = receiveDescription + "丨" + groupInfoLog + senderInfoLog + messageLog;
        return LogStringUtil.escapeString(logStr);
    }

    private static OneBotAdapter getOneBotAdapter() {
        return SpringContextUtil.getBean(OneBotAdapter.class);
    }
}
