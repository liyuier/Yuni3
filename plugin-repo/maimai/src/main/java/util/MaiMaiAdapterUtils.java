package util;

import com.yuier.yuni.core.net.ws.CommonWebSocketHandler;
import com.yuier.yuni.core.net.ws.CommonWebSocketManager;
import com.yuier.yuni.plugin.util.PluginUtils;
import config.MaiMaiAdapterConfig;

/**
 * @Title: util.MaiMaiAdapterUtils
 * @Author yuier
 * @Package PACKAGE_NAME
 * @Date 2025/12/26 0:37
 * @description: 工具类
 */

public class MaiMaiAdapterUtils {

    public static CommonWebSocketHandler getMaiMaiAdapterHandler(String connectionId) {
        CommonWebSocketManager manager = PluginUtils.getBean(CommonWebSocketManager.class);
        MaiMaiAdapterConfig config = PluginUtils.getBean(MaiMaiAdapterConfig.class);
        return (CommonWebSocketHandler) manager.getWebSocketHandlers().get(connectionId);
    }
}
