import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.CronExpressionBuilder;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.ScheduleActionPlugin;

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
                .everySeconds(2).build();
    }

    @Override
    public Action getAction() {
        return () -> {
            OneBotAdapter adapter = SpringContextUtil.getBean(OneBotAdapter.class);
            adapter.sendGroupMessage(930198267, new MessageChain("给我色图！"));
        };
    }
}
