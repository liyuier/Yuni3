package com.yuier.yuni.webapi.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Bot 状态响应。
 */
@Data
@AllArgsConstructor
public class BotStatusResp {
    /** Bot QQ 号 */
    private String botId;
    /** Bot 昵称 */
    private String nickname;
    /** 是否在线 */
    private boolean online;
    /** 连接状态文字描述 */
    private String statusText;
    /** 通信协议 */
    private String protocol;
}
