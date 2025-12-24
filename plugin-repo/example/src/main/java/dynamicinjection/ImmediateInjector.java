package dynamicinjection;

import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatelyActPlugin;
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
public class ImmediateInjector extends ImmediatelyActPlugin {
    @Override
    public Action getAction() {
        return () -> {
            PluginUtils.registerBeanUtil(new ExampleClass());
            log.info("[ImmediateInjector] 动态注入成功！");
        };
    }
}
