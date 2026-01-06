import com.yuier.yuni.core.model.message.segment.TextSegment;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.detector.message.command.model.matched.CommandMatched;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.load.PluginLoadProcessor;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.util.PluginUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.PluginManageUtil;
import util.PluginReloadProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static util.PluginManagerConstants.PLUGIN_MANAGE_RELOAD;

/**
 * @Title: PluginReload
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2026/1/6 0:03
 * @description: 重载插件
 */

@Slf4j
@NoArgsConstructor
public class PluginReload {

    public void reloadSpecifiedPlugin(YuniMessageEvent eventContext, CommandMatched commandMatched) {
        // 先判断插件序号是否越界
        TextSegment pluginSeqSegment = (TextSegment) commandMatched.getOptionOptionalArgValue(PLUGIN_MANAGE_RELOAD);
        int pluginSeq = Integer.parseInt(pluginSeqSegment.getText());
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        if (pluginSeq <= 0 || pluginSeq > container.getPluginCount()) {
            eventContext.getChatSession().response("没有序号为" + pluginSeq + "的插件。");
            return;
        }
        // 寻找插件模块以及其对应的 jar 包
        String pluginFullId = container.getPluginFullIdByIndex(pluginSeq);
        PluginInstance pluginInstance = container.getPluginInstanceByFullId(pluginFullId);
        List<File> possibleJarFiles = findSpecifiedPlugin(pluginFullId);
        if (possibleJarFiles.isEmpty()) {
            eventContext.getChatSession().response(
                    "没有找到插件 " + pluginInstance.getPluginName() + " 对应的 jar 包，已退出重载流程。\n" +
                    "请检查插件 jar 包是否正确放置到插件目录下。");
            return;
        } else if (possibleJarFiles.size() > 1) {
            eventContext.getChatSession().response("找到多个插件 " + pluginInstance.getPluginName() + " 可能对应的 jar 包，已退出重载流程。\n" +
                    "请确保插件目录下 jar 包名称不冲突。");
            return;
        }
        // 重载插件
        File jarFile = possibleJarFiles.get(0);
        PluginReloadProcessor pluginReloadProcessor = new PluginReloadProcessor();
        pluginReloadProcessor.reloadPlugin(pluginFullId, jarFile);
        eventContext.getChatSession().response("已重载插件 " + pluginInstance.getPluginName());
    }

    /**
     * 寻找指定插件可能存在的 jar 包
     * @param pluginFullId 插件全 ID
     * @return 插件 jar 包
     */
    public List<File> findSpecifiedPlugin(String pluginFullId) {
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        PluginModuleInstance pluginModuleInstance = container.getPluginModuleByPluginFullId(pluginFullId);
        String moduleId = pluginModuleInstance.getModuleId();
        String jarFileName = pluginModuleInstance.getJarFileName();
        String appPluginDirectoryPath = PluginManageUtil.getAppPluginDirectoryPath();

        File pluginDir = new File(appPluginDirectoryPath);
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.warn("插件目录不存在: {}", appPluginDirectoryPath);
            return new ArrayList<>();
        }
        File[] jarFiles = pluginDir.listFiles((dir, name) ->
                name.endsWith(".jar") &&  // 筛选 jar 包
                (name.equals(jarFileName) || name.startsWith(moduleId)));  // 筛选插件缓存的 jar 包名称，或者以模块 ID 开头的 jar 包名称
        if (jarFiles == null) {
            log.warn("插件目录为空: {}", appPluginDirectoryPath);
            return new ArrayList<>();
        }
        return List.of(jarFiles);
    }

    public void reloadAllPlugins(YuniMessageEvent eventContext, PluginManage pluginManage) {
        PluginReloadProcessor pluginReloadProcessor = new PluginReloadProcessor();
        pluginReloadProcessor.reloadAllPlugins();
        eventContext.getChatSession().response("已重载所有插件");
    }
}
