package com.yuier.yuni.webapi.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 系统资源响应。
 */
@Data
@AllArgsConstructor
public class SystemInfoResp {
    /** 堆内存已用（格式化） */
    private String heapUsed;
    /** 堆内存上限（格式化） */
    private String heapMax;
    /** 磁盘已用（格式化） */
    private String diskUsed;
    /** 磁盘总容量（格式化） */
    private String diskTotal;
    /** CPU 使用率描述 */
    private String cpuUsage;
    /** 数据库简要信息 */
    private String dbInfo;
}
