package com.yuier.yuni.core.model.bot;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: Bot
 * @Author yuier
 * @Package com.yuier.yuni.core.model.bot
 * @Date 2025/12/24 3:39
 * @description: QQ 机器人实体类
 */

@Data
@NoArgsConstructor
public class Bot {
    private Long id;
    private String nickName;
    private Long masterId;
    private BotApp botApp;
}
