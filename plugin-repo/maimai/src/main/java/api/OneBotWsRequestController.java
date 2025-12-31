package api;

import api.yuni.MaiMaiRequestHandler2;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
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
    @MaiMaiRequestHandler2(value = "get_group_info")
    public void getGroupInfo(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GroupInfo groupInfo = oneBotAdapter.getGroupInfo(parseGroupIdFromModel(model), true);
        quickSendOneBotResponse(groupInfo, GroupInfo.class, connectionId, echoId);
    }

    @MaiMaiRequestHandler2(value = "get_group_member_info")
    public void getGroupMemberInfo(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GroupMemberInfo groupMemberInfo = oneBotAdapter.getGroupMemberInfo(parseGroupIdFromModel(model), parseUserIdFromModel(model),true);
        quickSendOneBotResponse(groupMemberInfo, GroupMemberInfo.class, connectionId, echoId);
    }

    @MaiMaiRequestHandler2(value = "get_login_info")
    public void getLoginInfo(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        LoginInfo loginInfo = oneBotAdapter.getLoginInfo();
        quickSendOneBotResponse(loginInfo, LoginInfo.class, connectionId, echoId);
    }

    @MaiMaiRequestHandler2(value = "get_stranger_info")
    public void getStrangerInfo(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GetStrangerInfo strangerInfo = oneBotAdapter.getStrangerInfo(parseUserIdFromModel(model), true);
        quickSendOneBotResponse(strangerInfo, GetStrangerInfo.class, connectionId, echoId);
    }

    @MaiMaiRequestHandler2(value = "get_msg")
    public void getMsg(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GetMessage getMessage = oneBotAdapter.getMsg(parseMessageIdFromModel(model));
        quickSendOneBotResponse(getMessage, GetMessage.class, connectionId, echoId);
    }

    @MaiMaiRequestHandler2(value = "get_record")
    public void getRecord(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        GetRecord getRecord = oneBotAdapter.getRecord((String) model.get("file"), (String) model.get("out_format"));
        quickSendOneBotResponse(getRecord, GetRecord.class, connectionId, echoId);
    }

    @MaiMaiRequestHandler2(value = "send_group_msg")
    public void sendGroupMsg(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        MessageChain messageChain = parseMessageSegmentToChain(model);
        SendGroupMessage sendGroupMessage = oneBotAdapter.sendGroupMessage(parseGroupIdFromModel(model), messageChain);
        quickSendOneBotResponse(sendGroupMessage, SendGroupMessage.class, connectionId, echoId);
    }

    @MaiMaiRequestHandler2(value = "send_private_msg")
    public void sendPrivateMsg(String connectionId, Map<String, Object> model, String echoId) {
        OneBotAdapter oneBotAdapter = PluginUtils.getOneBotAdapter();
        MessageChain messageChain = parseMessageSegmentToChain(model);
        SendPrivateMessage sendPrivateMsg = oneBotAdapter.sendPrivateMessage(parseGroupIdFromModel(model), messageChain);
        quickSendOneBotResponse(sendPrivateMsg, SendPrivateMessage.class, connectionId, echoId);
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
        return Long.parseLong(String.valueOf(model.get("group_id")));
    }

    private static Long parseUserIdFromModel(Map<String, Object> model) {
        return Long.parseLong(String.valueOf(model.get("user_id")));
    }

    private static Long parseMessageIdFromModel(Map<String, Object> model) {
        return Long.parseLong(String.valueOf(model.get("message_id")));
    }

    private static <T> void quickSendOneBotResponse(T OneBotResponse, Class<T> responseClass, String connectionId, String echoId) {

        CommonWebSocketHandler handler = MaiMaiAdapterUtils.getMaiMaiAdapterHandler(connectionId);
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        try {
            handler.sendMessage(OneBotResponseWrapUtil.wrapRawJson(serialization.serialize(OneBotResponse), responseClass, echoId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
