package notice;

import com.yuier.yuni.core.model.event.NoticeEvent;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.event.context.notice.PokeEvent;
import com.yuier.yuni.event.context.notice.YuniNoticeEvent;
import com.yuier.yuni.event.detector.notice.DefaultYuniNoticeDetector;
import com.yuier.yuni.plugin.model.passive.notice.NoticePlugin;
import com.yuier.yuni.plugin.util.PluginUtils;

/**
 * @Title: PokeNotice
 * @Author yuier
 * @Package notice
 * @Date 2026/1/1 16:37
 * @description: 群内戳一戳
 */

public class PokeNotice extends NoticePlugin {

    @Override
    public DefaultYuniNoticeDetector getDetector() {
        return new DefaultYuniNoticeDetector(event -> {
            if ("notify".equals(event.getNoticeType())) {
                NoticeEvent noticeEvent = PluginUtils.serialize(event.getRawJson(), NoticeEvent.class);
                assert noticeEvent != null;
                if ("poke".equals(noticeEvent.getSubType())) {
                    return PluginUtils.serialize(event.getRawJson(), PokeEvent.class);
                }
            }
            return null;
        });
    }

    @Override
    public void execute(YuniNoticeEvent eventContext) {
        PokeEvent event = (PokeEvent) eventContext;
        PluginUtils.getOneBotAdapter().sendGroupMessage(event.getGroupId(), new MessageChain("戳我干嘛？"));
    }
}
