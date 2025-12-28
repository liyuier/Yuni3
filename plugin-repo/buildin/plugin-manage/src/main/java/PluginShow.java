import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.ImageSegment;
import com.yuier.yuni.event.context.YuniMessageEvent;
import lombok.NoArgsConstructor;
import org.thymeleaf.context.Context;
import util.PluginManageUtil;

/**
 * @Title: PluginShow
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/28 3:11
 * @description: 插件信息展示
 */

@NoArgsConstructor
public class PluginShow {

    public void showPluginList(YuniMessageEvent eventContext) {

        Integer pluginModulesHashCode = PluginManageUtil.getPluginModulesHashCodeCache(eventContext);
        String pluginListImageCache = PluginManageUtil.getPluginListImageCache(eventContext);
        // 先计算插件列表的 hashCode 是否有变动
        if (pluginModulesHashCode != null && pluginModulesHashCode.equals(PluginManageUtil.getHashCodeForShowingPluginList(eventContext))) {
            // 哈希没变，直接返回缓存图片
            if (pluginListImageCache != null) {
                eventContext.getChatSession().response(new MessageChain(
                        new ImageSegment().setFile(pluginListImageCache)
                ));
                return;
            }
        }






        String templateFilePath = "plugin-list.html";

        Context context = new Context();
    }

    public void showPluginDetail(YuniMessageEvent eventContext, MessageSegment pluginSeqSegment) {

    }


}
