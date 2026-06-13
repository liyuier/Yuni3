package com.yuier.yuni.webapi.controller.settings;

import com.yuier.yuni.webapi.dto.Result;
import com.yuier.yuni.webapi.service.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统设置接口。
 */
@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    /**
     * 获取所有配置项
     * @return 嵌套的配置树
     */
    @PostMapping("/all")
    public Result<?> allConfig() {
        return Result.ok(settingsService.getConfig());
    }
}
