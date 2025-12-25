package api;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.anno.WsRequestHandlerMethod;
import com.yuier.yuni.core.api.group.GroupInfo;
import com.yuier.yuni.core.api.group.GroupMemberInfo;
import com.yuier.yuni.core.api.message.GetMessage;
import com.yuier.yuni.core.api.message.GetRecord;
import com.yuier.yuni.core.api.message.SendGroupMessage;
import com.yuier.yuni.core.api.message.SendPrivateMessage;
import com.yuier.yuni.core.api.system.LoginInfo;
import com.yuier.yuni.core.api.user.GetStrangerInfo;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.net.ws.CommonWebSocketHandler;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.plugin.util.PluginUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.MaiMaiAdapterUtils;
import util.OneBotResponseWrapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Title: OneBotWsRequestController
 * @Author yuier
 * @Package api
 * @Date 2025/12/25 19:48
 * @description: OneBot 请求处理层
 */

@Slf4j
@NoArgsConstructor
public class OneBotWsRequestController {

    // 获取群信息
    @WsRequestHandlerMethod(value = "get_group_info")
    public void getGroupInfo(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        Object groupId = model.get("group_id");
        GroupInfo groupInfo = oneBotAdapter.getGroupInfo(((Integer) groupId).longValue(), true);
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(groupInfo), GroupInfo.class, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @WsRequestHandlerMethod(value = "get_group_member_info")
    public void getGroupMemberInfo(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GroupMemberInfo groupInfo = oneBotAdapter.getGroupMemberInfo(parseGroupIdFromModel(model), (Long) model.get("user_id"),true);
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(groupInfo), GroupMemberInfo.class, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @WsRequestHandlerMethod(value = "get_login_info")
    public void getLoginInfo(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        LoginInfo groupInfo = oneBotAdapter.getLoginInfo();
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(groupInfo), LoginInfo.class, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @WsRequestHandlerMethod(value = "get_stranger_info")
    public void getStrangerInfo(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GetStrangerInfo groupInfo = oneBotAdapter.getStrangerInfo(Long.parseLong((String) model.get("user_id")), true);
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(groupInfo), GetStrangerInfo.class, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @WsRequestHandlerMethod(value = "get_msg")
    public void getMsg(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GetMessage groupInfo = oneBotAdapter.getMsg(Long.parseLong((String) model.get("message_id")));
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(groupInfo), GetMessage.class, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @WsRequestHandlerMethod(value = "get_record")
    public void getRecord(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GetRecord groupInfo = oneBotAdapter.getRecord((String) model.get("file"), (String) model.get("out_format"));
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(groupInfo), GetRecord.class, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @WsRequestHandlerMethod(value = "send_group_msg")
    public void sendGroupMsg(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        MessageChain messageChain = parseMessageSegmentToChain(model);
        SendGroupMessage sendGroupMessage = oneBotAdapter.sendGroupMessage(parseGroupIdFromModel(model), messageChain);
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(sendGroupMessage), GetRecord.class, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @WsRequestHandlerMethod(value = "send_private_msg")
    public void sendPrivateMsg(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        MessageChain messageChain = parseMessageSegmentToChain(model);
        SendPrivateMessage sendPrivateMsg = oneBotAdapter.sendPrivateMessage(parseGroupIdFromModel(model), messageChain);
        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(sendPrivateMsg), GetRecord.class, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MessageChain parseMessageSegmentToChain(Map<String, Object> model) {
        MessageChain messageChain = new MessageChain();
        List<MessageSegment> messageSegmentList = new ArrayList<>();
        OneBotDeserializer deserializer = PluginUtils.getBean(OneBotDeserializer.class);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        List<Object> message = (List<Object>) model.get("message");
        for (Object messageItem : message) {
            try {
                String messageSegmentJson = serialization.serialize(messageItem);
                MessageSegment messageSegment = deserializer.deserialize(messageSegmentJson, MessageSegment.class);
                messageSegmentList.add(messageSegment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return messageChain.addAll(messageSegmentList);
    }
    
    private static Long parseGroupIdFromModel(Map<String, Object> model) {
        return Long.parseLong((String) model.get("group_id"));
    }
}
