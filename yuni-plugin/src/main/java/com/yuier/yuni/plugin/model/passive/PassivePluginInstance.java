package com.yuier.yuni.plugin.model.passive;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.detector.message.YuniEventDetector;
import com.yuier.yuni.plugin.model.PluginInstance;
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
public class PassivePluginInstance extends PluginInstance {

    private UserPermission permission;
    private YuniEventDetector<?> detector;
    private Method executeMethod;

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }
}
