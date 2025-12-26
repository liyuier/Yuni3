package com.yuier.yuni.plugin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuier.yuni.plugin.mapper.PluginCallMapper;
import com.yuier.yuni.plugin.service.PluginCallService;
import com.yuier.yuni.plugin.domain.entity.PluginCallEntity;
import org.springframework.stereotype.Service;

/**
 * (PluginCall)表服务实现类
 *
 * @author liyuier
 * @since 2025-12-27 03:02:12
 */
@Service
public class PluginCallServiceImpl extends ServiceImpl<PluginCallMapper, PluginCallEntity> implements PluginCallService {

    @Override
    public void saveEvent(PluginCallEntity pluginCallEntity) {
        this.save(pluginCallEntity);
    }
}

