import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.ImageSegment;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.PluginEnableProcessor;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.util.PluginUtils;
import entity.PluginListElement;
import entity.PluginListModule;
import entity.PluginListSinglePlugin;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import util.PluginManageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: PluginShow
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/28 3:11
 * @description: 插件信息展示
 */

@Slf4j
@NoArgsConstructor
public class PluginShow {

    private static final String PLUGIN_LIST_CACHE_IMAGE_NAME = "plugin-list-image-cache";

    public void showPluginList(YuniMessageEvent eventContext, PluginManage pluginManage) {

        Integer pluginModulesHashCode = PluginManageUtil.getPluginModulesHashCodeCache(eventContext);
        String pluginListImageCache = PluginManageUtil.getPluginListImageCache(eventContext);
        // 先计算插件列表的 hashCode 是否有变动
        int currPluginListHashCode = PluginManageUtil.calculateHashCodeForShowingPluginList(eventContext);
        if (pluginModulesHashCode != null && pluginModulesHashCode.equals(currPluginListHashCode)) {
            // 哈希没变，直接返回缓存图片
            if (pluginListImageCache != null) {
                eventContext.getChatSession().response(new MessageChain(
                        new ImageSegment().setFile(pluginListImageCache)
                ));
                return;
            }
        }
        PluginManageUtil.savePluginListHashCodeCache(eventContext, currPluginListHashCode);
        // 缓存图片不存在或者插件列表的 hashCode 发生了变化，则重新生成图片缓存
        String templateFilePath = "templates/plugin-list.html";
        String templateStr = PluginUtils.loadTextFromPluginJar(pluginManage, templateFilePath);
        Context context = new Context();
        // 组装数据
        List<PluginListElement> pluginListElements = assemblerPluginListData(eventContext);
        context.setVariable("pluginList", pluginListElements);
        // 渲染 HTML
        String htmlStr = PluginManageUtil.renderToHtml(templateStr, context);
        // TODO 这里应该做成可选本地图片还是 bas64。由于我的 OneBot 客户端与 Yuni 部署在不同机器上，所以返回 Base64
        String imgBase64 = PluginManageUtil.screenForHtmlStrToBase64(htmlStr, "result-container");
        eventContext.getChatSession().response(new MessageChain(
                new ImageSegment().setFile(buildImageDataFileNameForCache(imgBase64))
        ));
        // 保存图片缓存
        PluginManageUtil.savePluginListImageCache(eventContext, buildImageDataFileNameForCache(imgBase64));
    }

    private static String buildImageDataFileNameForCache(String fileName) {
        return "base64://" + fileName;
    }

    private static String buildPluginListCacheImageName(YuniMessageEvent eventContext) {
        return PLUGIN_LIST_CACHE_IMAGE_NAME + "-" + eventContext.getPosition() + ".png";
    }

    /**
     * 组装插件列表数据
     * @param eventContext  消息事件
     * @return  组装后的插件列表数据
     */
    public List<PluginListElement> assemblerPluginListData(YuniMessageEvent eventContext) {
        ArrayList<PluginListElement> pluginListElements = new ArrayList<>();
        // 组装数据
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        PluginEnableProcessor processor = PluginUtils.getBean(PluginEnableProcessor.class);
        int index = 0;
        for (PluginModuleInstance pluginModule : container.getPluginModules().values()) {
            List<PluginInstance> pluginInstances = pluginModule.getPluginInstances();
            if (pluginInstances == null || pluginInstances.isEmpty()) {
                continue;
            }
            if (pluginInstances.size() == 1) {
                // 组装单个插件数据
                PluginInstance pluginInstance = pluginInstances.get(0);
                index += 1;
                pluginListElements.add(new PluginListElement(new PluginListSinglePlugin(
                        index,
                        pluginInstance.getPluginMetadata().getName(),
                        pluginInstance.getPluginMetadata().getDescription(),
                        processor.isPluginEnabled(eventContext, pluginInstance)

                )));
            } else {
                // 组装插件模块
                PluginListModule pluginListModule = new PluginListModule();
                pluginListModule.setModuleName(pluginModule.getModuleName());
                for (PluginInstance pluginInstance : pluginInstances) {
                    index += 1;
                    pluginListModule.addSubPlugin(new PluginListSinglePlugin(
                            index,
                            pluginInstance.getPluginMetadata().getName(),
                            pluginInstance.getPluginMetadata().getDescription(),
                            processor.isPluginEnabled(eventContext, pluginInstance)
                    ));
                }
                pluginListElements.add(new PluginListElement(pluginListModule));
            }
        }
        return pluginListElements;
    }

    public void showPluginDetail(YuniMessageEvent eventContext, MessageSegment pluginSeqSegment, PluginManage pluginManage) {

    }


}
