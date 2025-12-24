import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.CronExpressionBuilder;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.ScheduleActionPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;

/**
 * @Title: AskForSeTu
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/24 0:38
 * @description: 要色图
 */

public class AskForSeTu extends ScheduleActionPlugin {


    @Override
    public String cronExpression() {
        return CronExpressionBuilder.create()
                .dailyAt(10, 0)
                .build();
    }

    @Override
    public Action getAction() {
        return () -> {
            OneBotAdapter adapter = SpringContextUtil.getBean(OneBotAdapter.class);
            adapter.sendPrivateMessage(PluginUtils.getBotMasterId(), new MessageChain("偶哈哟，欧尼酱！"));
        };
    }
}
