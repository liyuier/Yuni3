package immediate;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.message.SendGroupMessage;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.plugin.manage.enable.event.PluginDisableEvent;
import com.yuier.yuni.plugin.manage.enable.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatePlugin;
import com.yuier.yuni.plugin.util.PluginUtils;

/**
 * @Title: IAmBack
 * @Author yuier
 * @Package Immediate
 * @Date 2025/12/24 23:43
 * @description: 我是后背
 */

public class IAmBack extends ImmediatePlugin {
    @Override
    public Action getAction() {
        return () -> {
            OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
            SendGroupMessage sendGroupMessage = oneBotAdapter.sendGroupMessage(930198267, new MessageChain("我是后背"));
        };
    }

    @Override
    public void enable(PluginEnableEvent event) {

    }

    @Override
    public void disable(PluginDisableEvent event) {

    }
}
