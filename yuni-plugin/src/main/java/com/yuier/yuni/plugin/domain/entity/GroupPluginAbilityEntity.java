package com.yuier.yuni.plugin.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (GroupPluginAbility)表实体类
 *
 * @author liyuier
 * @since 2025-12-24 21:28:33
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("group_plugin_ability")
public class GroupPluginAbilityEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long groupId;

    private String pluginId;

    private Boolean ability;
}
