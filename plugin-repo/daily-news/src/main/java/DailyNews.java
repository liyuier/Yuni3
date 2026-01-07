import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.group.GroupListElement;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.segment.ImageSegment;
import com.yuier.yuni.core.util.CronExpressionBuilder;
import com.yuier.yuni.plugin.manage.enable.event.PluginDisableEvent;
import com.yuier.yuni.plugin.manage.enable.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;
import db.DBHelper;
import db.DailyNewsGroupEnable;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Title: DailyNews
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2026/1/2 0:41
 * @description: 每日早报
 */

@Slf4j
public class DailyNews extends ScheduledPlugin {

    @Override
    public Action getAction() {
        return () -> {
            // 先获取每日早报内容
            DailyNewsResponse dailyNewsResponse = PluginUtils.simplePost("https://api.2xb.cn/zaob", null, DailyNewsResponse.class);
            MessageChain dailyNewsMessageChain = new MessageChain(new ImageSegment().setFile(dailyNewsResponse.getImageUrl()));
            // 先获取群列表
            OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
            List<GroupListElement> groupList = oneBotAdapter.getGroupList();
            List<Long> groupIdList = groupList.stream()
                    .map(GroupListElement::getGroupId)
                    .toList();
            log.info("群列表：{}", groupIdList);
            // 再根据策略发送
            DailyNewsStrategyConfig strategyConfig = PluginUtils.loadJsonConfigFromJar("daily_news_strategy.json", DailyNewsStrategyConfig.class, this);
            log.info("策略：{}", strategyConfig.getStrategy());
            if ("blacklist".equals(strategyConfig.getStrategy())) {
                log.info("黑名单模式");
                List<DailyNewsGroupEnable> blackLists = DBHelper.findBlackLists();
                log.info("黑名单：{}", blackLists);
                groupIdList.forEach(groupId -> {
                    if (blackLists.stream().noneMatch(blackGroup -> blackGroup.getGroupId().equals(groupId))) {
                        // 发送群消息
                        PluginUtils.sendGroupMessage(groupId, dailyNewsMessageChain);
                    }
                });
            } else if ("whitelist".equals(strategyConfig.getStrategy())) {
                log.info("白名单模式");
                List<DailyNewsGroupEnable> whiteLists = DBHelper.findWhiteLists();
                log.info("白名单：{}", whiteLists);
                groupIdList.forEach(groupId -> {
                    if (whiteLists.stream().anyMatch(whiteGroup -> whiteGroup.getGroupId().equals(groupId))) {
                        // 发送群消息
                        PluginUtils.sendGroupMessage(groupId, dailyNewsMessageChain);
                    }
                });
            }
        };
    }

    @Override
    public String cronExpression() {
        return CronExpressionBuilder.create()
                .dailyAt(8, 30).build();
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
        DBHelper.createJdbi();
    }
}
