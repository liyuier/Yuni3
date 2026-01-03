import com.yuier.yuni.core.util.CronExpressionBuilder;
import com.yuier.yuni.plugin.event.PluginDisableEvent;
import com.yuier.yuni.plugin.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;

/**
 * @Title: DailyNews
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2026/1/2 0:41
 * @description: 每日早报
 */

public class DailyNews extends ScheduledPlugin {

    @Override
    public Action getAction() {
        return null;
    }

    @Override
    public String cronExpression() {
        return CronExpressionBuilder.create()
                .dailyAt(9, 0).build();
    }

    @Override
    public void enable(PluginEnableEvent event) {

    }

    @Override
    public void disable(PluginDisableEvent event) {

    }

    @Override
    public void initialize() {
        // 建表
        String appDatabaseUrl = PluginUtils.getAppDatabaseUrl();
    }
}
