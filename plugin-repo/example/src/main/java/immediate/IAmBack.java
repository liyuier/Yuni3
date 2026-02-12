package immediate;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.message.SendGroupMessage;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.plugin.manage.enable.event.PluginDisableEvent;
import com.yuier.yuni.plugin.manage.enable.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatePlugin;
import com.yuier.yuni.plugin.util.PluginUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Title: IAmBack
 * @Author yuier
 * @Package Immediate
 * @Date 2025/12/24 23:43
 * @description: 我是后背
 */

@Slf4j
public class IAmBack extends ImmediatePlugin {
    @Override
    public Action getAction() {
        return () -> {
            TargetGroupConfig targetGroupConfig = PluginUtils.loadJsonConfigFromPlugin("i_m_back.json", TargetGroupConfig.class, this);
            OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
            targetGroupConfig.getTargetGroups().forEach(groupId -> {
                try {
                    oneBotAdapter.sendGroupMessage(groupId, new MessageChain("我是后背"));
                } catch (Exception e) {
                    log.warn("[IAmBack] 向 {} 发送群消息失败.", groupId);
                }
            });
        };
    }

    @Override
    public void enable(PluginEnableEvent event) {

    }

    @Override
    public void disable(PluginDisableEvent event) {

    }
}
