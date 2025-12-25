package com.yuier.yuni.core.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GetStrangerInfo
 * @Author yuier
 * @Package com.yuier.yuni.core.api.user
 * @Date 2025/12/26 1:15
 * @description:
 */

@Data
@NoArgsConstructor
public class GetStrangerInfo {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("sex")
    private String sex; // 通常为 "male" / "female" / "unknown"

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("qid")
    private String qid; // QQ 号的字符串形式（部分协议使用）

    @JsonProperty("level")
    private Integer level; // QQ 等级

    @JsonProperty("login_days")
    private Integer loginDays;

    @JsonProperty("reg_time")
    private Long regTime; // 注册时间 Unix 时间戳（秒）

    @JsonProperty("long_nick")
    private String longNick; // 个性签名或长昵称

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("birthday_year")
    private Integer birthdayYear;

    @JsonProperty("birthday_month")
    private Integer birthdayMonth;

    @JsonProperty("birthday_day")
    private Integer birthdayDay;
}
