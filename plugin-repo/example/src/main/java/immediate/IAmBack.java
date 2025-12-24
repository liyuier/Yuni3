package immediate;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.group.GroupListElement;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatelyActPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;

import java.util.List;

/**
 * @Title: IAmBack
 * @Author yuier
 * @Package Immediate
 * @Date 2025/12/24 23:43
 * @description: 我是后背
 */

public class IAmBack extends ImmediatelyActPlugin {
    @Override
    public Action getAction() {
        return () -> {
            OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
            List<GroupListElement> groupList = oneBotAdapter.getGroupList();
            groupList.forEach(group -> System.out.println(group.getGroupName()));
            oneBotAdapter.sendGroupMessage(930198267,new MessageChain("我是后背"));
        };
    }
}
