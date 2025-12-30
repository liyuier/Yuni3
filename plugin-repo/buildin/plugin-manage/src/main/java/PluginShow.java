import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.segment.ImageSegment;
import com.yuier.yuni.core.model.message.segment.TextSegment;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.detector.message.command.model.matched.CommandMatched;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.PluginEnableProcessor;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.util.PluginUtils;
import entity.PluginDetail;
import entity.PluginListElement;
import entity.PluginListModule;
import entity.PluginListSinglePlugin;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import util.PluginManageUtil;

import java.util.ArrayList;
import java.util.List;

import static util.PluginManagerConstants.PLUGIN_MANAGE_VIEW;

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

    /**
     * 显示插件列表
     * @param eventContext  消息事件
     * @param pluginManage  插件管理器
     */
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
        for (String moduleId : container.getPluginModuleIds()) {
            PluginModuleInstance pluginModule = container.getPluginModuleById(moduleId);
            List<PluginInstance> pluginInstances = pluginModule.getPluginInstances();
            if (pluginInstances == null || pluginInstances.isEmpty()) {
                continue;
            }
            if (pluginInstances.size() == 1) {
                // 组装单个插件数据
                PluginInstance pluginInstance = pluginInstances.get(0);
                pluginListElements.add(new PluginListElement(new PluginListSinglePlugin(
                        pluginInstance.getIndex(),
                        pluginInstance.getPluginMetadata().getName(),
                        pluginInstance.getPluginMetadata().getDescription(),
                        processor.isPluginEnabled(eventContext, pluginInstance)

                )));
            } else {
                // 组装插件模块
                PluginListModule pluginListModule = new PluginListModule();
                pluginListModule.setModuleName(pluginModule.getModuleName());
                for (PluginInstance pluginInstance : pluginInstances) {
                    pluginListModule.addSubPlugin(new PluginListSinglePlugin(
                            pluginInstance.getIndex(),
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

    /**
     * 显示插件详情
     * @param eventContext  消息事件
     * @param commandMatched   命令匹配结果
     * @param pluginManage  插件管理器
     */
    public void showPluginDetail(YuniMessageEvent eventContext, CommandMatched commandMatched, PluginManage pluginManage) {
        // TODO 插件详情需要显示插件序号与插件是否开启
        TextSegment pluginSeqSegment = (TextSegment) commandMatched.getOptionOptionalArgValue(PLUGIN_MANAGE_VIEW);
        Integer pluginSeq = Integer.parseInt(pluginSeqSegment.getText());
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        String pluginId = container.getPluginIndexToIdMap().get(pluginSeq);
        if (pluginId == null) {
            eventContext.getChatSession().response("没有序号为" + pluginSeq + "的插件。");
            return;
        }
        PluginInstance pluginInstance = container.getPluginInstanceById(pluginId);
        Integer pluginDetailHashCodeCache = PluginManageUtil.getPluginDetailHashCodeCache(pluginId);
        String pluginDetailImageCache = PluginManageUtil.getPluginDetailImageCache(pluginId);
        Integer currentPluginHash = PluginManageUtil.calculateHashCodeForShowingPluginDetail(pluginId);
        if (pluginDetailHashCodeCache != null && pluginDetailHashCodeCache.equals(currentPluginHash)) {
            // 哈希没变，直接返回缓存图片
            if (pluginDetailImageCache != null) {
                eventContext.getChatSession().response(new MessageChain(
                        new ImageSegment().setFile(pluginDetailImageCache)
                ));
                return;
            }
        }
        PluginManageUtil.savePluginDetailHashCodeCache(pluginId, currentPluginHash);
        // 获取模板
        String templateFilePath = "templates/plugin-detail.html";
        String templateStr = PluginUtils.loadTextFromPluginJar(pluginManage, templateFilePath);
        Context context = new Context();
        // 组装数据
        PluginDetail pluginDetail = new PluginDetail();
        pluginDetail.setName(pluginInstance.getPluginMetadata().getName());
        pluginDetail.setDescription(pluginInstance.getPluginMetadata().getDescription());
        pluginDetail.setAuthor(pluginInstance.getPluginMetadata().getAuthor());
        pluginDetail.setVersion(pluginInstance.getPluginMetadata().getVersion());
        pluginDetail.setTips(pluginInstance.getPluginMetadata().getTips());
        context.setVariable("plugin", pluginDetail);
        // 渲染 HTML
        String htmlStr = PluginManageUtil.renderToHtml(templateStr, context);
        String imgBase64 = PluginManageUtil.screenForHtmlStrToBase64(htmlStr, "result-container");
        eventContext.getChatSession().response(new MessageChain(
                new ImageSegment().setFile(buildImageDataFileNameForCache(imgBase64))
        ));
        PluginManageUtil.savePluginDetailImageCache(pluginId, buildImageDataFileNameForCache(imgBase64));
    }


}
