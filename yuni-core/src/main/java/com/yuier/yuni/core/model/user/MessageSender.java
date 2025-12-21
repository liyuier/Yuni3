package com.yuier.yuni.core.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: MessageSender
 * @Author yuier
 * @Package com.yuier.yuni.core.model.user
 * @Date 2025/12/22 5:06
 * @description: OneBot 协议中消息事件携带的 sender 字段实体类
 */

@Getter
@Setter
@NoArgsConstructor
public class MessageSender {

    // 发送者 QQ 号
    private Long userId;

    // 昵称
    private String nickname;

    /**
     * 性别
     * - male
     * - female
     * - unknown
     */
    private String sex;

    // 年龄
    private int age;

    /* 以下字段只有群消息才会携带 */

    // 群名片/备注
    private String card;

    // 地区
    private String area;

    // 成员等级
    private String level;

    /**
     * 角色
     * - owner
     * - admin
     * - member
     */
    private String role;

    // 专属头衔
    private String title;
}
