package com.yuier.yuni.webapi.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 群插件启用状态条目。
 */
@Data
@AllArgsConstructor
public class GroupPluginStatusItem {
    private String fullId;
    private String name;
    private String type;
    private boolean enabled;
}
