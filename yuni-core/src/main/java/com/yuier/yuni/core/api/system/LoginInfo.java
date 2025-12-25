package com.yuier.yuni.core.api.system;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class LoginInfo {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;
}
