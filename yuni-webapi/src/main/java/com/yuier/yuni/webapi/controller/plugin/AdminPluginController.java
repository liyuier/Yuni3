package com.yuier.yuni.webapi.controller.plugin;

import com.yuier.yuni.webapi.dto.plugin.PluginActionReq;
import com.yuier.yuni.webapi.dto.Result;
import com.yuier.yuni.webapi.service.plugin.PluginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插件管理接口。
 * 仅负责参数校验与 Result 包装，业务逻辑全部委托给 {@link PluginService}。
 */
@RestController
@RequestMapping("/admin/plugin")
@RequiredArgsConstructor
public class AdminPluginController {

    private final PluginService pluginService;

    /**
     * 获取插件列表
     * @return 插件列表
     */
    @PostMapping("/list")
    public Result<?> pluginList() {
        return Result.ok(pluginService.listPlugins());
    }

    /**
     * 启用插件
     * @param req 请求体，其中包括插件 ID 与 群组 ID
     *            群组 ID 为空时全局操作，非空时针对指定群组
     * @return 操作成功
     */
    @PostMapping("/enable")
    public Result<Void> pluginEnable(@RequestBody PluginActionReq req) {
        if (req.getPluginId() == null || req.getPluginId().isBlank()) {
            return Result.fail(400, "插件ID不能为空");
        }
        try {
            pluginService.enablePlugin(req.getPluginId(), req.getGroupId());
            return Result.ok(null, "插件已启用");
        } catch (IllegalArgumentException e) {
            return Result.fail(404, e.getMessage());
        }
    }

    /**
     * 禁用插件
     * @param req 插件禁用请求体，其中包括插件 ID 与 群组 ID
     *            群组 ID 为空时全局操作，非空时针对指定群组
     * @return 操作成功
     */
    @PostMapping("/disable")
    public Result<Void> pluginDisable(@RequestBody PluginActionReq req) {
        if (req.getPluginId() == null || req.getPluginId().isBlank()) {
            return Result.fail(400, "插件ID不能为空");
        }
        try {
            pluginService.disablePlugin(req.getPluginId(), req.getGroupId());
            return Result.ok(null, "插件已禁用");
        } catch (IllegalArgumentException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    /**
     * 重载插件
     * @param req 插件重载请求体，其中包括插件 ID
     * @return 操作成功
     */
    @PostMapping("/reload")
    public Result<Void> pluginReload(@RequestBody PluginActionReq req) {
        try {
            pluginService.reloadPlugin(req.getPluginId());
            return Result.ok(null,
                    req.getPluginId() == null || req.getPluginId().isBlank()
                            ? "已重载所有插件"
                            : "已重载插件");
        } catch (IllegalArgumentException e) {
            return Result.fail(404, e.getMessage());
        } catch (RuntimeException e) {
            return Result.fail(500, e.getMessage());
        }
    }
}
