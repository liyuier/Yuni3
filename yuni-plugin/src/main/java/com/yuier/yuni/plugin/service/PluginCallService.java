package com.yuier.yuni.plugin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuier.yuni.plugin.domain.entity.PluginCallEntity;

/**
 * (PluginCall)表服务接口
 *
 * @author liyuier
 * @since 2025-12-27 03:02:12
 */
public interface PluginCallService extends IService<PluginCallEntity> {

    void saveEvent(PluginCallEntity pluginCallEntity);
}

