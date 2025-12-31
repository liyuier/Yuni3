import com.yuier.yuni.core.net.ws.CommonWebSocketHandler;
import com.yuier.yuni.core.net.ws.CommonWebSocketManager;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.detector.message.pattern.PatternDetector;
import com.yuier.yuni.plugin.model.passive.message.PatternPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;
import config.MaiMaiAdapterConfig;
import org.springframework.web.socket.TextMessage;

/**
 * @Title: OneBotMessageForward
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/25 23:17
 * @description: OneBot 消息转发
 */

public class OneBotMessageForward extends PatternPlugin {
    @Override
    public void execute(YuniMessageEvent eventContext) {
        // 原样转发 OneBot 的消息
        // TODO 转发消息除外，因为还没做转发消息相关的接口
        if (eventContext.getMessageChain().containsForwardMessage()) {
            return;
        }
        CommonWebSocketManager manager = PluginUtils.getBean(CommonWebSocketManager.class);
        MaiMaiAdapterConfig config = PluginUtils.getBean(MaiMaiAdapterConfig.class);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        CommonWebSocketHandler webSocketHandler = (CommonWebSocketHandler) manager.getWebSocketHandlers().get(config.getConnectionId());
        try {
            String messageEventJson = serialization.serialize(eventContext.getMessageEvent());
            webSocketHandler.getSession().sendMessage(new TextMessage(messageEventJson));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PatternDetector getDetector() {
        return new PatternDetector(chain -> false);
    }
}
