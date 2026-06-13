package com.yuier.yuni.webapi.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 首页统计卡片响应。
 */
@Data
@AllArgsConstructor
public class DashboardStatsResp {
    /** 接入群组数 */
    private int groupCount;
    /** 应用运行时长，格式化后的字符串，如 "4h 32m" */
    private String uptime;
    /** 今日收/发消息总数（暂无数据源，预留） */
    private String todayMessageCount;
    /** 今日活跃群数（暂无数据源，预留） */
    private String todayActiveGroupCount;
}
