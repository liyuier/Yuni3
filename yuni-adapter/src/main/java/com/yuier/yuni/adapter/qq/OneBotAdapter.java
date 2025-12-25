package com.yuier.yuni.adapter.qq;

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
    SendGroupMessage sendGroupMessage(long groupId, MessageChain message);
    SendPrivateMessage sendPrivateMessage(long userId, MessageChain message);

    // 消息管理相关
    void deleteMessage(long messageId);

    // 群组管理相关
    void setGroupKick(long groupId, long userId, boolean rejectAddRequest);
    void setGroupBan(long groupId, long userId, long duration);

    // 信息查询相关
    LoginInfo getLoginInfo();

    // 用户相关
    GetStrangerInfo getStrangerInfo(long userId, boolean noCache);

    // 消息相关
    GetMessage getMsg(long messageId);
    GetRecord getRecord(String file, String outFormat);

    // 群组相关
    GroupInfo getGroupInfo(long groupId, boolean noCache);
    List<GroupListElement> getGroupList();
    GroupMemberInfo getGroupMemberInfo(long groupId, long userId, boolean noCache);
}
