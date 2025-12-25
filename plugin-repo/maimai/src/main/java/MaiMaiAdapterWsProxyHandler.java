import com.yuier.yuni.core.net.ws.BusinessMessageProxyHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Title: MaiMaiAdapterWsProxyHandler
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws
 * @Date 2025/12/25 14:34
 * @description: 麦麦适配器 ws 代理 handler
 */

@Slf4j
@NoArgsConstructor
public class MaiMaiAdapterWsProxyHandler implements BusinessMessageProxyHandler {

    @Override
    public void handleMessage(String connectionId, String message) {
        log.info("【默认处理】连接 {} 收到业务消息: {}", connectionId, message);
    }

    @Override
    public void onConnectionEstablished(String connectionId, WebSocketSession session) {
        log.info("【默认处理】连接 {} 已建立", connectionId);
    }

    @Override
    public void onConnectionError(String connectionId, Throwable exception) {
        log.error("【默认处理】连接 {} 错误: {}", connectionId, exception.getMessage());
    }

    @Override
    public void onConnectionClosed(String connectionId, CloseStatus status) {
        log.info("【默认处理】连接 {} 已关闭: {}", connectionId, status);
    }
}
