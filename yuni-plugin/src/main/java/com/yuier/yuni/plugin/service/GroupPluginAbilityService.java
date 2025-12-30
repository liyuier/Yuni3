package com.yuier.yuni.plugin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuier.yuni.plugin.domain.entity.GroupPluginAbilityEntity;

/**
 * (GroupPluginAbility)表服务接口
 *
 * @author liyuier
 * @since 2025-12-24 21:28:35
 */

public interface GroupPluginAbilityService extends IService<GroupPluginAbilityEntity> {

    Boolean getPluginAbility(Long groupId, String pluginId);

    void enablePlugin(Long groupId, String pluginId);

    void disablePlugin(Long groupId, String pluginId);
}

