package com.yuier.yuni.plugin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuier.yuni.plugin.mapper.GroupPluginAbilityMapper;
import com.yuier.yuni.plugin.service.GroupPluginAbilityService;
import com.yuier.yuni.plugin.domain.entity.GroupPluginAbilityEntity;
import org.springframework.stereotype.Service;

/**
 * (GroupPluginAbility)表服务实现类
 *
 * @author liyuier
 * @since 2025-12-24 21:28:35
 */
@Service
public class GroupPluginAbilityServiceImpl extends ServiceImpl<GroupPluginAbilityMapper, GroupPluginAbilityEntity> implements GroupPluginAbilityService {

    @Override
    public Boolean getPluginAbility(Long groupId, String pluginId) {
        // 使用 wrapper 查询
        LambdaQueryWrapper<GroupPluginAbilityEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupPluginAbilityEntity::getGroupId, groupId)
                .eq(GroupPluginAbilityEntity::getPluginId, pluginId);
        GroupPluginAbilityEntity entity = getOne(queryWrapper);
        return entity != null ? entity.getAbility() : null;
    }
}

