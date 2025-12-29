package com.yuier.yuni.core.model.event;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: NoticeEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event
 * @Date 2025/12/29 17:02
 * @description: 通知事件
 */

@Getter
@Setter
@NoArgsConstructor
@PolymorphicSubType
public class NoticeEvent extends OneBotEvent{

    /**
     * 通知类型
     * group_upload 群文件上传
     * group_admin 群管理员变动
     * group_decrease 群成员减少
     * group_increase 群成员增加
     * group_ban 群成员禁言
     * friend_add 好友添加
     * group_recall 群消息撤回
     * notify 群内戳一戳
     * friend_recall 好友消息撤回
     * group_lucky_king 群红包运气王
     * group_honor 群成员荣誉变更
     */
    private String noticeType;
}
