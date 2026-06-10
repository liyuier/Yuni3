package com.yuier.yuni.adapter.onebot.api.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: LoginInfo
 * @Author yuier
 * @Package com.yuier.yuni.core.api.group
 * @Date 2025/12/26 1:12
 * @description: 登录信息
 */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginInfo {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;
}
