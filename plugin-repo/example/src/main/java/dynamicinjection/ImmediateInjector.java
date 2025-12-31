package dynamicinjection;

import com.yuier.yuni.plugin.event.PluginDisableEvent;
import com.yuier.yuni.plugin.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.immediate.ImmediateActionPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Title: ImmediateInjector
 * @Author yuier
 * @Package dynamicinjection
 * @Date 2025/12/25 1:05
 * @description: 即时插件动态注入
 */

@Slf4j
public class ImmediateInjector extends ImmediateActionPlugin {
    @Override
    public Action getAction() {
        return () -> {
            PluginUtils.registerBeanToSpring(new ExampleClass());
            log.info("[ImmediateInjector] 动态注入成功！");
        };
    }

    @Override
    public void enable(PluginEnableEvent event) {

    }

    @Override
    public void disable(PluginDisableEvent event) {

    }
}
