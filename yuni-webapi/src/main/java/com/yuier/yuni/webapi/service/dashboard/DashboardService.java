package com.yuier.yuni.webapi.service.dashboard;

import com.yuier.yuni.core.bot.BotStatus;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.core.model.bot.Bot;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.event.service.ReceiveMessageService;
import com.yuier.yuni.webapi.dto.dashboard.BotStatusResp;
import com.yuier.yuni.webapi.dto.dashboard.DashboardStatsResp;
import com.yuier.yuni.webapi.dto.dashboard.SystemInfoResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * 首页 Dashboard 数据服务。
 * 负责组装 Bot 状态与统计卡片数据。
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ReceiveMessageService receiveMessageService;

    /**
     * 获取 Bot 运行状态。
     * @return Bot 状态信息
     */
    public BotStatusResp getBotStatus() {
        YuniBot yuniBot = SpringContextUtil.getBean(YuniBot.class);
        Bot bot = SpringContextUtil.getBean(Bot.class);
        BotStatus status = yuniBot.getStatus();
        return new BotStatusResp(
                yuniBot.getBotId(),
                bot.getNickName(),
                status == BotStatus.ONLINE,
                statusText(status),
                yuniBot.getPlatform()
        );
    }

    /**
     * 获取首页统计卡片数据。
     * @return 统计数据
     */
    public DashboardStatsResp getStats() {
        return new DashboardStatsResp(
                getGroupCount(),
                formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()),
                String.valueOf(receiveMessageService.countTodayMessages()),
                String.valueOf(receiveMessageService.countTodayActiveGroups())
        );
    }

    /**
     * 获取系统资源信息。
     * @return 系统资源数据
     */
    public SystemInfoResp getSystemInfo() {
        // 物理内存
        com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalRam = osBean.getTotalMemorySize();
        long freeRam = osBean.getFreeMemorySize();
        long usedRam = totalRam - freeRam;

        // CPU
        double cpuLoad = osBean.getCpuLoad();
        String cpuStr = cpuLoad < 0 ? "--" : String.format("%.1f%%", cpuLoad * 100);

        // 磁盘（取工作目录所在分区）
        File workDir = new File("").getAbsoluteFile();
        long diskTotal = workDir.getTotalSpace();
        long diskFree = workDir.getFreeSpace();
        long diskUsed = diskTotal - diskFree;

        return new SystemInfoResp(
                formatBytes(usedRam),
                formatBytes(totalRam),
                formatBytes(diskUsed),
                formatBytes(diskTotal),
                cpuStr,
                "SQLite"
        );
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取接入群组数。
     * 通过 YuniBot 接口查询群列表，不支持时返回 0。
     * @return 群组数量
     */
    private int getGroupCount() {
        YuniBot yuniBot = SpringContextUtil.getBean(YuniBot.class);
        return yuniBot.getGroupList()
                .map(list -> list.size())
                .orElse(0);
    }

    /**
     * 格式化运行时长。
     * @param uptimeMillis JVM 启动以来的毫秒数
     * @return 格式化字符串，如 "4h 32m"
     */
    private String formatUptime(long uptimeMillis) {
        long totalSeconds = uptimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }

    /**
     * BotStatus 枚举转中文描述。
     * @param status 连接状态枚举
     * @return 中文状态文字
     */
    /**
     * 字节转可读格式。
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024L * 1024) return String.format("%.0fK", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.0fM", bytes / (1024.0 * 1024));
        if (bytes < 1024L * 1024 * 1024 * 1024) return String.format("%.1fG", bytes / (1024.0 * 1024 * 1024));
        return String.format("%.1fT", bytes / (1024.0 * 1024 * 1024 * 1024));
    }

    private String statusText(BotStatus status) {
        return switch (status) {
            case ONLINE -> "在线";
            case OFFLINE -> "离线";
            case CONNECTING -> "连接中";
            case ERROR -> "异常";
        };
    }
}
