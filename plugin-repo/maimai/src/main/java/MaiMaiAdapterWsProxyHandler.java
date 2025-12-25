import api.OneBotRequestHandlerRegistry;
import api.OneBotWsRequestController;
import com.yuier.yuni.core.net.ws.BusinessMessageProxyHandler;
import com.yuier.yuni.core.net.ws.CommonWebSocketHandler;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.event.model.meta.HeartbeatEvent;
import com.yuier.yuni.event.model.meta.HeartbeatStatus;
import com.yuier.yuni.event.model.meta.LifeCycle;
import com.yuier.yuni.plugin.util.PluginUtils;
import config.MaiMaiAdapterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import util.MaiMaiAdapterUtils;

/**
 * @Title: MaiMaiAdapterWsProxyHandler
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws
 * @Date 2025/12/25 14:34
 * @description: 麦麦适配器 ws 代理 handler
 */

@Slf4j
public class MaiMaiAdapterWsProxyHandler implements BusinessMessageProxyHandler {

    private final OneBotRequestHandlerRegistry handlerRegistry;

    // 注册消息处理器
    public MaiMaiAdapterWsProxyHandler() {
        this.handlerRegistry = new OneBotRequestHandlerRegistry();
        handlerRegistry.registerHandlers(new OneBotWsRequestController());
    }


    @Override
    public void handleMessage(String connectionId, String message) {
        log.info("[默认处理]连接 {} 收到业务消息: {}", connectionId, message);
         handlerRegistry.handleMessage(connectionId, message);
    }

    @Override
    public void onConnectionEstablished(String connectionId, WebSocketSession session) {
        log.info("[默认处理]连接 {} 已建立", connectionId);
        // 发送一条生命周期事件
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        LifeCycle lifeCycle = new LifeCycle();
        lifeCycle.setTime(System.currentTimeMillis() / 1000);
        Long botQqId = PluginUtils.getBotModelConfig().getId();
        lifeCycle.setSelfId(botQqId);
        lifeCycle.setPostType("meta_event");
        lifeCycle.setMetaEventType("lifecycle");
        lifeCycle.setSubType("connect");
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(serialization.serialize(lifeCycle));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onConnectionError(String connectionId, Throwable exception) {
        log.error("[默认处理]连接 {} 错误: {}", connectionId, exception.getMessage());
    }

    @Override
    public void onConnectionClosed(String connectionId, CloseStatus status) {
        log.info("[默认处理]连接 {} 已关闭: {}", connectionId, status);
    }

    @Override
    public String getBusinessHeartBeatMessage() {
        HeartbeatStatus heartbeatStatus = new HeartbeatStatus();
        heartbeatStatus.setOnline(true);
        heartbeatStatus.setGood(true);
        // 获取当前 Unix 时间戳，单位为秒
        long time = System.currentTimeMillis() / 1000;
        Long botQqId = PluginUtils.getBotModelConfig().getId();
        MaiMaiAdapterConfig maiMaiAdapterConfig = PluginUtils.getBean(MaiMaiAdapterConfig.class);
        HeartbeatEvent heartbeatEvent = new HeartbeatEvent();
        heartbeatEvent.setTime(time);
        heartbeatEvent.setSelfId(botQqId);
        heartbeatEvent.setPostType("meta_event");
        heartbeatEvent.setMetaEventType("heartbeat");
        heartbeatEvent.setStatus(heartbeatStatus);
        heartbeatEvent.setInterval(maiMaiAdapterConfig.getHeartbeatInterval());
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            return serialization.serialize(heartbeatEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
