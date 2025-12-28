package util;

import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.PluginEnableProcessor;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.util.PluginUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Title: PluginManageUtil
 * @Author yuier
 * @Package util
 * @Date 2025/12/28 19:49
 * @description:
 */

public class PluginManageUtil {

    public static int getHashCodeForShowingPluginList(YuniMessageEvent event) {
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        Map<String, PluginModuleInstance> pluginModules = container.getPluginModules();
        int[] singleHashes = new int[pluginModules.size()];
        int i = 0;
        for (PluginModuleInstance moduleInstance : pluginModules.values()) {
            singleHashes[i] = getModuleInstanceHashCodeForShowingPluginList(moduleInstance, event);
            i++;
        }
        Arrays.sort(singleHashes);
        return Arrays.hashCode(singleHashes);
    }

    private static int getModuleInstanceHashCodeForShowingPluginList(PluginModuleInstance moduleInstance, YuniMessageEvent event) {
        List<PluginInstance> pluginInstances = moduleInstance.getPluginInstances();
        int[] singleHashes = new int[pluginInstances.size()];
        PluginEnableProcessor processor = PluginUtils.getBean(PluginEnableProcessor.class);
        for (int i = 0; i < pluginInstances.size(); i++) {
            PluginInstance pluginInstance = pluginInstances.get(i);
            singleHashes[i] = getPluginHashCodeForShowingPluginList(pluginInstance, event);
        };
        // 排序，消除顺序影响
        Arrays.sort(singleHashes);
        return Arrays.hashCode(singleHashes);
    }

    private static int getPluginHashCodeForShowingPluginList(PluginInstance pluginInstance, YuniMessageEvent event) {
        PluginEnableProcessor processor = PluginUtils.getBean(PluginEnableProcessor.class);
        PluginMetadata pluginMetadata = pluginInstance.getPluginMetadata();
        return Objects.hash(
                pluginMetadata.getName(),  // 插件名称
                pluginMetadata.getDescription(),  // 插件描述
                processor.isPluginEnabled(event, pluginInstance)  // 插件使能情况
        );
    }

    public static Integer getPluginModulesHashCodeCache(YuniMessageEvent eventContext) {
        return null;  // TODO
    }

    public static String getPluginListImageCache(YuniMessageEvent eventContext) {
        return null;  // TODO
    }
}
