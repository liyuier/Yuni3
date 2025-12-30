import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.segment.TextSegment;
import com.yuier.yuni.core.task.DynamicTaskManager;
import com.yuier.yuni.core.util.LogStringUtil;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.detector.message.command.model.matched.CommandMatched;
import com.yuier.yuni.permission.manage.UserPermissionManager;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.PluginManager;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPluginInstance;
import com.yuier.yuni.plugin.util.PluginUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static util.PluginManagerConstants.PLUGIN_MANAGE_DISABLE;
import static util.PluginManagerConstants.PLUGIN_MANAGE_ENABLE;

/**
 * @Title: PluginEnable
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/30 22:34
 * @description: 开启 / 关闭 插件
 */

@Slf4j
@NoArgsConstructor
public class PluginEnable {
    public void enablePlugin(YuniMessageEvent eventContext, CommandMatched commandMatched) {
        // 检查权限
        UserPermissionManager permissionManager = PluginUtils.getBean(UserPermissionManager.class);
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        TextSegment pluginSeqSegment = (TextSegment) commandMatched.getOptionRequiredArgValue(PLUGIN_MANAGE_ENABLE);
        int pluginSeq = Integer.parseInt(pluginSeqSegment.getText());
        String pluginId = container.getPluginIdByIndex(pluginSeq);
        if (pluginId == null) {
            eventContext.getChatSession().response(new MessageChain(
                    new TextSegment("没有序号为 " + pluginSeq + " 的插件。")
            ));
            return;
        }
        PluginInstance pluginInstance = container.getPluginInstanceById(pluginId);
        UserPermission userPermission = permissionManager.getUserPermission(eventContext, pluginId);
        // 再检查是否内置插件
        if (pluginInstance.isBuiltIn() &&  userPermission.getPriority() < UserPermission.MASTER.getPriority()) {
             eventContext.getChatSession().response(new MessageChain(
                     new TextSegment(pluginSeq + " 号插件为内置插件，只有 bot 拥有者有权开启或关闭")
            ));
             return;
        }
        PluginManager pluginManager = PluginUtils.getBean(PluginManager.class);
        pluginManager.enablePlugin(eventContext, pluginId);

        // 如果是定时任务，需要重新添加任务
         if (pluginInstance instanceof ScheduledPluginInstance scheduledPluginInstance) {
            DynamicTaskManager dynamicTaskManager = PluginUtils.getBean(DynamicTaskManager.class);
             // 创建定时任务
             Runnable task = () -> {
                 try {
                     scheduledPluginInstance.getAction().execute();
                 } catch (Exception e) {
                     log.error("执行主动插件失败: {}", LogStringUtil.buildBrightBlueLog(pluginId), e);
                 }
             };

             // 注册到定时任务系统
             dynamicTaskManager.addCronTask(pluginId, scheduledPluginInstance.getCronExpression(), task);
        }

        eventContext.getChatSession().response(new MessageChain(
                new TextSegment("已开启 " + pluginSeq + " 号插件")
        ));
    }

    public void disablePlugin(YuniMessageEvent eventContext, CommandMatched commandMatched) {
        // 检查权限
        UserPermissionManager permissionManager = PluginUtils.getBean(UserPermissionManager.class);
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        TextSegment pluginSeqSegment = (TextSegment) commandMatched.getOptionRequiredArgValue(PLUGIN_MANAGE_DISABLE);
        int pluginSeq = Integer.parseInt(pluginSeqSegment.getText());
        String pluginId = container.getPluginIdByIndex(pluginSeq);
        if (pluginId == null) {
            eventContext.getChatSession().response(new MessageChain(
                    new TextSegment("没有序号为 " + pluginSeq + " 的插件。")
            ));
            return;
        }
        PluginInstance pluginInstance = container.getPluginInstanceById(pluginId);
        UserPermission userPermission = permissionManager.getUserPermission(eventContext, pluginId);
        // 再检查是否内置插件
        if (pluginInstance.isBuiltIn() &&  userPermission.getPriority() < UserPermission.MASTER.getPriority()) {
            eventContext.getChatSession().response(new MessageChain(
                    new TextSegment(pluginSeq + " 号插件为内置插件，只有 bot 拥有者有权开启或关闭")
            ));
            return;
        }
        PluginManager pluginManager = PluginUtils.getBean(PluginManager.class);
        pluginManager.disablePlugin(eventContext, pluginId);

        // 如果是定时插件，需要取消一下定时任务
        if (pluginInstance instanceof ScheduledPluginInstance) {
            DynamicTaskManager dynamicTaskManager = PluginUtils.getBean(DynamicTaskManager.class);
            dynamicTaskManager.cancelTask(pluginId);
        }

        eventContext.getChatSession().response(new MessageChain(
                new TextSegment("已关闭 " + pluginSeq + " 号插件")
        ));
    }

}
