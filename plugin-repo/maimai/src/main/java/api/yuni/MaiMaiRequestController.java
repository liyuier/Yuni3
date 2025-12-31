package api.yuni;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.adapter.qq.http.OneBotResponse;
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
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketConnector;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.plugin.util.PluginUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.OneBotPostModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Title: MaiMaiRequestController
 * @Author yuier
 * @Package api.yuni
 * @Date 2025/12/31 11:10
 * @description:
 */

@Slf4j
@NoArgsConstructor
public class MaiMaiRequestController {

    private static YuniWebSocketConnector connector;
    private static OneBotAdapter oneBotAdapter;

    public MaiMaiRequestController(YuniWebSocketConnector connector) {
        MaiMaiRequestController.connector = connector;
        oneBotAdapter = PluginUtils.getOneBotAdapter();
    }

    @MaiMaiRequestHandler(value = "get_group_info")
    public void getGroupInfo(OneBotPostModel model) {
        GroupInfo groupInfo = oneBotAdapter.getGroupInfo(parseGroupIdFromModel(model.getParams()), true);
        quickSendOneBotResponse(groupInfo, model.getEcho());
    }

    @MaiMaiRequestHandler2(value = "get_group_member_info")
    public void getGroupMemberInfo(OneBotPostModel model) {
        GroupMemberInfo groupMemberInfo = oneBotAdapter.getGroupMemberInfo(parseGroupIdFromModel(model.getParams()), parseUserIdFromModel(model.getParams()),true);
        quickSendOneBotResponse(groupMemberInfo, model.getEcho());
    }

    @MaiMaiRequestHandler2(value = "get_login_info")
    public void getLoginInfo(OneBotPostModel model) {
        LoginInfo loginInfo = oneBotAdapter.getLoginInfo();
        quickSendOneBotResponse(loginInfo, model.getEcho());
    }

    @MaiMaiRequestHandler2(value = "get_stranger_info")
    public void getStrangerInfo(OneBotPostModel model) {
        GetStrangerInfo getStrangerInfo = oneBotAdapter.getStrangerInfo(parseUserIdFromModel(model.getParams()), true);
        quickSendOneBotResponse(getStrangerInfo, model.getEcho());
    }

    @MaiMaiRequestHandler2(value = "get_msg")
    public void getMsg(OneBotPostModel model) {
        GetMessage getMessage = oneBotAdapter.getMsg(parseMessageIdFromModel(model.getParams()));
        quickSendOneBotResponse(getMessage, model.getEcho());
    }

    @MaiMaiRequestHandler2(value = "get_record")
    public void getRecord(OneBotPostModel model) {
        GetRecord getRecord = oneBotAdapter.getRecord(parseFileFromModel(model.getParams()), parseOutFormatFromModel(model.getParams()));
        quickSendOneBotResponse(getRecord, model.getEcho());
    }

    @MaiMaiRequestHandler2(value = "send_group_msg")
    public void sendGroupMsg(OneBotPostModel model) {
        SendGroupMessage sendGroupMessage = oneBotAdapter.sendGroupMessage(parseGroupIdFromModel(model.getParams()), parseMessageSegmentToChain(model.getParams()));
        quickSendOneBotResponse(sendGroupMessage, model.getEcho());
    }

    @MaiMaiRequestHandler2(value = "send_private_msg")
    public void sendPrivateMsg(OneBotPostModel model) {
        SendPrivateMessage sendPrivateMessage = oneBotAdapter.sendPrivateMessage(parseUserIdFromModel(model.getParams()), parseMessageSegmentToChain(model.getParams()));
        quickSendOneBotResponse(sendPrivateMessage, model.getEcho());
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

    private String parseFileFromModel(Map<String, Object> model) {
        return (String) model.get("file");
    }

    private String parseOutFormatFromModel(Map<String, Object> model) {
        return (String) model.get("out_format");
    }

    private static MessageChain parseMessageSegmentToChain(Map<String, Object> model) {
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

    private static <T> void quickSendOneBotResponse(T responseBody, String echoId) {
        OneBotSerialization serialization = PluginUtils.getBean(OneBotSerialization.class);
        OneBotResponse response = new OneBotResponse();
        response.setStatus("ok");
        response.setRetcode(0);
        response.setEcho(echoId);
        response.setData(responseBody);
        try {
            String responseJson = serialization.serialize(response);
            connector.send(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
