package com.yuier.yuni.adapter.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: OneBotCommunicate
 * @Author yuier
 * @Package com.yuier.yuni.adapter
 * @Date 2025/12/29 5:03
 * @description:
 */

@Data
@NoArgsConstructor
public class OneBotCommunicate {

    private String mode;
    private String token;
    private String wsUrl;
    private int wsHeartbeatInterval;
    private int wsTimeout;
    private String httpUrl;

    public String getWsUrl() {
        if (wsUrl.endsWith("/")) {
            return wsUrl.substring(0, wsUrl.length() - 1);
        }
        return wsUrl;
    }

    public String getHttpUrl() {
        if (httpUrl.endsWith("/")) {
            return httpUrl.substring(0, httpUrl.length() - 1);
        }
        return httpUrl;
    }
}
