package schedule;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.message.SendPrivateMessage;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.CronExpressionBuilder;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduleActionPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;

/**
 * @Title: active.OhaYo
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/24 0:38
 * @description: 要色图
 */

public class OhaYo extends ScheduleActionPlugin {

    @Override
    public String cronExpression() {
        return CronExpressionBuilder.create()
                .dailyAt(10, 0)
                .build();
    }

    /* WARNING: 由于作者没琢磨明白怎么控制主动插件的行为，因此无法对主动插件实行类似被动插件的以群组为粒度的开启/关闭的管理
    *           所以务必谨慎评估主动插件的行为，避免引入一些影响全局的严重后果 */

    @Override
    public Action getAction() {
        return () -> {
            OneBotAdapter adapter = SpringContextUtil.getBean(OneBotAdapter.class);
            SendPrivateMessage sendPrivateMessage = adapter.sendPrivateMessage(PluginUtils.getBotMasterId(), new MessageChain("偶哈哟，欧尼酱！"));
        };
    }
}
