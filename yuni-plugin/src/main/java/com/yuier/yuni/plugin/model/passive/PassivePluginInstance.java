package com.yuier.yuni.plugin.model.passive;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.model.context.SpringYuniEvent;
import com.yuier.yuni.event.model.message.detector.YuniEventDetector;
import com.yuier.yuni.plugin.model.AbstractPluginInstance;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.YuniPlugin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @Title: PassivePluginInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/23 20:54
 * @description: 被动插件实体类
 */

@Data
@NoArgsConstructor
public class PassivePluginInstance extends AbstractPluginInstance {

    private PassivePlugin<?, ?> passivePlugin;
    private PluginMetadata pluginMetadata;
    private UserPermission permission;
    private YuniEventDetector<?> detector;
    private Method executeMethod;

    @Override
    public YuniPlugin getPlugin() {
        return passivePlugin;
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }
}
