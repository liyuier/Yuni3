import com.yuier.yuni.core.net.ws.CommonWebSocketHandler;
import com.yuier.yuni.core.net.ws.CommonWebSocketManager;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketConnector;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketManager;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.plugin.event.PluginDisableEvent;
import com.yuier.yuni.plugin.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.immediate.ImmediateActionPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;
import config.MaiMaiAdapterConfig;
import okhttp3.Request;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.client.WebSocketConnectionManager;

/**
 * @Title: MaiMaiAdapterBooter
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/25 1:54
 * @description: maimai机器人适配器
 */

public class MaiMaiAdapterBooter extends ImmediateActionPlugin {

    private static final String WS_CONNECT_TO_MAIMAI_ADAPTER = "ws_connect_to_maimai_adapter";

    @Override
    public Action getAction() {
        return () -> {
            // 获取配置
            MaiMaiAdapterConfig config = PluginUtils.loadJsonConfigFromJar("maimai_napcat_adapter_config.json", MaiMaiAdapterConfig.class, this);
            PluginUtils.registerBeanToSpring(config, "maimaiAdapterPluginConfig");
            Request request = new Request.Builder()
                    .url(config.getServerUrl())
                    .addHeader("Authorization", "Bearer " + config.getToken())
                    .build();
            OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
            MaiMaiAdapterWsProxyListener maiMaiAdapterWsProxyListener = new MaiMaiAdapterWsProxyListener(serialization);
            // 创建连接器
            YuniWebSocketConnector maimaiAdapterConnector = new YuniWebSocketConnector(request, maiMaiAdapterWsProxyListener);
            maiMaiAdapterWsProxyListener.setConnector(maimaiAdapterConnector);
            YuniWebSocketManager manager = PluginUtils.getBean(YuniWebSocketManager.class);
            // 启动连接器
            manager.startNewConnection(WS_CONNECT_TO_MAIMAI_ADAPTER, maimaiAdapterConnector);
        };
    }

    @Override
    public void enable(PluginEnableEvent event) {
        // TODO 启动连接
    }

    @Override
    public void disable(PluginDisableEvent event) {
        // TODO 停止连接
    }
}
