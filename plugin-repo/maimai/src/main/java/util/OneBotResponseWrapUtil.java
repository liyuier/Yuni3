package util;

import com.yuier.yuni.adapter.qq.http.OneBotResponse;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.plugin.util.PluginUtils;

/**
 * @Title: OneBotResponseWrapUtil
 * @Author yuier
 * @Package util
 * @Date 2025/12/26 0:52
 * @description:
 */

public class OneBotResponseWrapUtil {

    public static <T> String wrapRawJson(String rawJson, Class<T> clazz, String echoId) {
        OneBotDeserializer deserializer = PluginUtils.getBean(OneBotDeserializer.class);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        String result = "";
        OneBotResponse response = new OneBotResponse();
        response.setStatus("ok");
        response.setRetcode(0);
        response.setEcho(echoId);
        try {
            T serialize = deserializer.deserialize(rawJson, clazz);
            response.setData(serialize);
            result = serialization.serialize(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
