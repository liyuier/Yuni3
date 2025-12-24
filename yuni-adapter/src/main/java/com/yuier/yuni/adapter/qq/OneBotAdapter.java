package com.yuier.yuni.adapter.qq;

import com.yuier.yuni.adapter.qq.http.OneBotResponse;
import com.yuier.yuni.core.api.group.GroupInfo;
import com.yuier.yuni.core.api.group.GroupListElement;
import com.yuier.yuni.core.api.message.GetMessage;
import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.model.message.MessageChain;

import java.util.List;

/**
 * @Title: OneBotAdapter
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq
 * @Date 2025/12/22 3:50
 * @description: QQ 协议适配器接口
 */

public interface OneBotAdapter {

    void startListening();
    void stopListening();
    OneBotEvent handleReportJson(String json);
    void sendGroupMessage(long groupId, MessageChain message);
    void sendPrivateMessage(long userId, MessageChain message);

    // 消息管理相关
    void deleteMessage(long messageId);

    // 群组管理相关
    void setGroupKick(long groupId, long userId, boolean rejectAddRequest);
    void setGroupBan(long groupId, long userId, long duration);

    // 信息查询相关
    OneBotResponse getLoginInfo();
    OneBotResponse getStrangerInfo(long userId, boolean noCache);
    GroupInfo getGroupInfo(long groupId, boolean noCache);
    OneBotResponse getGroupMemberInfo(long groupId, long userId, boolean noCache);
    GetMessage getMsg(long messageId);

    List<GroupListElement> getGroupList();
}
