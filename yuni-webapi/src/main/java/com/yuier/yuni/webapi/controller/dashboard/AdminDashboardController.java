package com.yuier.yuni.webapi.controller.dashboard;

import com.yuier.yuni.webapi.dto.Result;
import com.yuier.yuni.webapi.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页 Dashboard 接口。
 * 仅负责委托给 {@link DashboardService} 并包装 Result。
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取 Bot 运行状态
     * @return Bot 状态信息
     */
    @PostMapping("/bot-status")
    public Result<?> botStatus() {
        return Result.ok(dashboardService.getBotStatus());
    }

    /**
     * 获取首页统计卡片数据
     * @return 统计数据
     */
    @PostMapping("/stats")
    public Result<?> stats() {
        return Result.ok(dashboardService.getStats());
    }
}
