package com.yuier.yuni.adapter.qq.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OneBot API 响应模型
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // 忽略未知字段
public class OneBotResponse {
    private String status;
    private Integer retcode;
    private Object data;
}
