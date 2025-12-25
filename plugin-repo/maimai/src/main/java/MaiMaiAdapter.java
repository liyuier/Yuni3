import com.yuier.yuni.core.net.ws.CommonWebSocketHandler;
import com.yuier.yuni.core.net.ws.CommonWebSocketManager;
import com.yuier.yuni.plugin.model.active.Action;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatelyActPlugin;
import com.yuier.yuni.plugin.util.PluginUtils;

/**
 * @Title: MaiMaiAdapter
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/25 1:54
 * @description: maimai机器人适配器
 */

public class MaiMaiAdapter extends ImmediatelyActPlugin {
    @Override
    public Action getAction() {
        return () -> {
            /* 向 MaiBot-Napcat-Adapter 发起连接 */
            // 获取配置
            MaiMaiAdapterConfig config = PluginUtils.loadConfigJsonToBean("maimai_napcat_adapter_config.json", MaiMaiAdapterConfig.class, this);
            PluginUtils.registerBeanUtil(config, "maimaiAdapterPluginConfig");
            // 获取 wsManager
            CommonWebSocketManager wsManager = PluginUtils.getBean(CommonWebSocketManager.class);
            // 创建 handler
            CommonWebSocketHandler maimaiAdapterHandler = getCommonWebSocketHandler(config, wsManager);
            // 创建连接配置
            wsManager.createConnection(
                    config.getConnectionId(),
                    config.getServerUrl(),
                    maimaiAdapterHandler);
            wsManager.setAuthToken(config.getConnectionId(), config.getToken());
            // 启动连接
            wsManager.startConnection(config.getConnectionId());
        };
    }

    private static CommonWebSocketHandler getCommonWebSocketHandler(MaiMaiAdapterConfig config, CommonWebSocketManager wsManager) {
        // 自定义代理 handler
        MaiMaiAdapterWsProxyHandler maiMaiAdapterWsProxyHandler = new MaiMaiAdapterWsProxyHandler();
        CommonWebSocketHandler maimaiAdapterHandler = new CommonWebSocketHandler(
                config.getConnectionId(),
                config.getServerUrl(),
                wsManager,
                maiMaiAdapterWsProxyHandler,
                config.getHeartbeatInterval(),
                config.getReconnectInterval()
        );
        return maimaiAdapterHandler;
    }
}
