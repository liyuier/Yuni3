package com.yuier.yuni.core.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.event.model.GroupUploadFile;
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
public class NoticeEvent extends OneBotEvent {

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
    @JsonProperty("notice_type")
    private String noticeType;

    /**
     * 子类型
     * group_upload：
     *   - group_upload：群文件上传
     * group_admin：
     *   - set：群成员被设置管理员
     *   - unset：群成员被取消管理员
     * group_decrease：
     *   - leave：群成员主动离开
     *   - kick：群成员被踢
     *   - kick_me：登录号被踢
     * group_increase：
     *   - approve：群成员请求被同意
     *   - invite：群成员被邀请入群
     * group_ban：
     *   - ban：群成员被禁言
     *   - lift_ban：群成员被取消禁言
     * notify：
     *   - poke：群内戳一戳
     *   - lucky_king: 群红包运气王
     *   - honor: 群成员荣誉变更
     */
    @JsonProperty("sub_type")
    private String subType;

    /**
     * 用户 ID
     * - group_upload: 发送者 QQ 号
     * - group_admin: 管理员 QQ 号
     * - group_decrease: 离开者 QQ 号
     * - group_increase: 加入者 QQ 号
     * - group_ban: 被禁言 QQ 号。如果是全体禁言，为 0
     * - friend_add: 新添加的 QQ 号
     * - group_recall: 消息发送者 QQ 号
     * - friend_recall: 好友 QQ 号
     * - poke: 发送者 QQ 号
     * - group_lucky_king: 红包发送者 QQ 号
     * - honor: 成员 QQ 号
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 群号。以下事件都携带
     * group_upload 群文件上传
     * group_admin 群管理员变动
     * group_decrease 群成员减少
     * group_increase 群成员增加
     * group_ban 群成员禁言
     * group_recall 群消息撤回
     * poke 群内戳一戳
     * group_lucky_king 群红包运气王
     * group_honor 群成员荣誉变更
     */
    @JsonProperty("group_id")
    private Long groupId;

    /**
     * 操作者 QQ 号
     * - group_decrease: 操作者 QQ 号（如果是主动退群，则和 user_id 相同）
     * - group_increase: 操作者 QQ 号
     * - group_ban: 操作者 QQ 号
     * - group_recall: 操作者 QQ 号
     */
    @JsonProperty("operator_id")
    private Long operatorId;

    /* group_upload 群文件上传字段 */

    // 文件信息
    @JsonProperty("file")
    private GroupUploadFile file;

    /* group_ban 群禁言字段 */

    // 禁言时长，单位 秒
    @JsonProperty("duration")
    private Long duration;

    /* group_recall 群消息撤回 / friend_recall 好友消息撤回 字段 */

    // 撤回的消息 ID
    @JsonProperty("message_id")
    private Long messageId;

    /**
     * - poke: 被戳者 QQ 号
     * - lucky_king: 运气王 QQ 号
     */
    @JsonProperty("target_id")
    private Long targetId;

    /**
     * 荣誉类型
     * - group_honor: 群成员荣誉变更
     *   - talkative: 龙王
     *   - performer: 群聊之火
     *   - emotion: 群聊炽火
     */
    @JsonProperty("honor_type")
    private String HonorType;
}
