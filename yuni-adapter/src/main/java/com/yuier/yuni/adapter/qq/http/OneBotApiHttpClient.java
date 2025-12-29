package com.yuier.yuni.adapter.qq.http;

/**
 * @Title: OneBotApiHttpClient
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.http
 * @Date 2025/12/23 2:50
 * @description: OneBot API 调用 HTTP 工具类
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.core.model.message.MessageChain;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class OneBotApiHttpClient {

    private static final Logger log = LoggerFactory.getLogger(OneBotApiHttpClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    private final ObjectMapper objectMapper;

    public OneBotApiHttpClient(@Value("${onebot.communication.http.url:http://localhost:3000}") String baseUrl, ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    /**
     * POST请求方法
     * @param endpoint API端点
     * @param jsonBody 请求参数
     * @return API响应结果
     */
    public String postJsonForRawJson(String endpoint, String jsonBody) {
        String url = baseUrl + endpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    /**
     * POST请求方法
     * @param endpoint API端点
     * @param params 请求参数
     * @return API响应结果
     */
    public String postForRawJson(String endpoint, Map<String, Object> params) {
        String url = baseUrl + endpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    /**
     * 通用POST请求方法
     * @param endpoint API端点
     * @param params 请求参数
     * @return API响应结果
     */
    private OneBotResponse post(String endpoint, Map<String, Object> params) {
        try {
            String url = baseUrl + endpoint;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // 解析响应
            return objectMapper.readValue(response.getBody(), OneBotResponse.class);
        } catch (Exception e) {
            log.error("调用OneBot API失败: {}", endpoint, e);
            return new OneBotResponse();
        }
    }

    /**
     * 发送私聊消息
     * @param userId 用户ID
     * @param message 消息内容
     * @return API响应结果
     */
    public OneBotResponse sendPrivateMessage(Long userId, MessageChain message) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("message", message.getContent());
        return post("/send_private_msg", params);
    }

    /**
     * 发送群消息
     * @param groupId 群ID
     * @param message 消息内容
     * @return API响应结果
     */
    public OneBotResponse sendGroupMessage(Long groupId, MessageChain message) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("message", message.getContent());
        return post("/send_group_msg", params);
    }

    /**
     * 撤回消息
     * @param messageId 消息ID
     * @return API响应结果
     */
    public OneBotResponse deleteMessage(Long messageId) {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return post("/delete_msg", params);
    }

    /**
     * 群组踢人
     * @param groupId 群ID
     * @param userId 用户ID
     * @param rejectAddRequest 是否拒绝再次添加好友请求
     * @return API响应结果
     */
    public OneBotResponse setGroupKick(Long groupId, Long userId, Boolean rejectAddRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("reject_add_request", rejectAddRequest);
        return post("/set_group_kick", params);
    }

    /**
     * 群组单人禁言
     * @param groupId 群ID
     * @param userId 用户ID
     * @param duration 禁言时长（秒），0表示取消禁言
     * @return API响应结果
     */
    public OneBotResponse setGroupBan(Long groupId, Long userId, Long duration) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("duration", duration);
        return post("/set_group_ban", params);
    }

    /**
     * 获取登录号信息
     * @return 登录号信息
     */
    public OneBotResponse getLoginInfo() {
        return post("/get_login_info", new HashMap<>());
    }

    /**
     * 获取陌生人信息
     * @param userId 用户ID
     * @param noCache 是否不使用缓存
     * @return 陌生人信息
     */
    public OneBotResponse getStrangerInfo(Long userId, Boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("no_cache", noCache);
        return post("/get_stranger_info", params);
    }

    /**
     * 获取群信息
     * @param groupId 群ID
     * @param noCache 是否不使用缓存
     * @return 群信息
     */
    public OneBotResponse getGroupInfo(Long groupId, Boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("no_cache", noCache);
        return post("/get_group_info", params);
    }

    /**
     * 获取群成员信息
     * @param groupId 群ID
     * @param userId 用户ID
     * @param noCache 是否不使用缓存
     * @return 群成员信息
     */
    public OneBotResponse getGroupMemberInfo(Long groupId, Long userId, Boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("no_cache", noCache);
        return post("/get_group_member_info", params);
    }

    /**
     * 获取消息
     * @param messageId 消息ID
     * @return 消息内容
     */
    public OneBotResponse getMsg(Long messageId) {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return post("/get_msg", params);
    }

    /**
     * 获取群列表
     * @return 群列表
     */
    public OneBotResponse getGroupList() {
        Map<String, Object> params = new HashMap<>();
        return post("/get_group_list", params);
    }

    /**
     * 获取语音
     * @param file 语音文件名
     * @param outFormat 输出格式
     * @return 语音内容
     */
    public OneBotResponse getRecord(String file, String outFormat) {
        Map<String, Object> params = new HashMap<>();
        params.put("file", file);
        params.put("out_format", outFormat);
        return post("/get_record", params);
    }
}

