package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.core.anno.WsRequestHandlerMethod;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: OneBotRequestHandlerRegistry
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws
 * @Date 2025/12/25 19:24
 * @description: ws api 注册器
 */

@Slf4j
@NoArgsConstructor
public class OneBotRequestHandlerRegistry {

    private final Map<String, MethodHandler> routerToHandlerMethodMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 注册消息处理器
     * @param handlerObject 包含处理器方法的对象
     */
    public void registerHandlers(Object handlerObject) {
        Class<?> clazz = handlerObject.getClass();

        // 遍历类中的所有方法，寻找有 @WsRequestHandlerMethod 注解的方法，并注册
        for (Method method : clazz.getDeclaredMethods()) {
            WsRequestHandlerMethod annotation = method.getAnnotation(WsRequestHandlerMethod.class);
            if (annotation != null) {
                String messageType = annotation.value();
                // 构建路由 {注解 value 值}:{注解 subType 值}
                String router = annotation.subType().isEmpty() ?
                        messageType : messageType + ":" + annotation.subType();

                // 取出参数列表，其中第二个参数就是上报的请求参数
                Class<?>[] paramTypes = method.getParameterTypes();

                method.setAccessible(true);
                // 将路由与处理方法关联起来
                routerToHandlerMethodMap.put(router, new MethodHandler(handlerObject, method, paramTypes[1]));
            }
        }
    }

    /**
     * 使用注册的消息处理器处理消息
     * @param connectionId 连接ID
     * @param message 消息内容
     */
    public void handleMessage(String connectionId, String message) {
        try {
            // 解析消息
            OneBotPostModel wrapper = objectMapper.readValue(message, OneBotPostModel.class);
            String requestRouter = wrapper.getAction();

            // 寻找路由注册的处理方法
            MethodHandler handler = routerToHandlerMethodMap.get(requestRouter);
            if (handler == null) {
                // 如果没有找到，判断请求是否指向带有子类型的路由
                int colonIndex = requestRouter.indexOf(':');
                if (colonIndex > 0) {
                    String basicType = requestRouter.substring(0, colonIndex);
                    // 如果请求带有子路由，尝试应用到父路由
                    handler = routerToHandlerMethodMap.get(basicType);
                }
            }

            if (handler != null) {
                // 反序列化数据部分
                Object params = wrapper.getParams();
                if (params instanceof String) {
                    // 如果数据是字符串，尝试反序列化为Object
                    params = objectMapper.readValue((String) params, handler.paramType);
                }
                handler.invoke(connectionId, params, wrapper.getEcho());
            } else {
                log.warn("未找到消息类型 {} 的处理器", wrapper.getAction());
            }
        } catch (Exception e) {
            log.error("处理消息时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 方法处理器内部类
     */
    private static class MethodHandler {
        private final Object targetObject;
        private final Method method;
        private final Class<?> paramType;

        public MethodHandler(Object targetObject, Method method, Class<?> paramType) {
            this.targetObject = targetObject;
            this.method = method;
            this.paramType = paramType;
        }

        public void invoke(String connectionId, Object param, String echoId) {
            try {
                method.invoke(targetObject, connectionId, param, echoId);
            } catch (Exception e) {
                log.error("调用处理器方法时发生错误: {}", e.getMessage(), e);
            }
        }
    }
}
