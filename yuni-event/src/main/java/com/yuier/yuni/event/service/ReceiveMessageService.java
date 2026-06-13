package com.yuier.yuni.event.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuier.yuni.core.event.YuniMessageEvent;
import com.yuier.yuni.event.domain.entity.ReceiveMessageEntity;

import java.util.List;

/**
 * (ReceiveMessage)表服务接口
 *
 * @author liyuier
 * @since 2025-12-27 01:26:23
 */
public interface ReceiveMessageService extends IService<ReceiveMessageEntity> {

    void saveEvent(ReceiveMessageEntity receiveMessageEntity);

    void flagAsCommandEvent(YuniMessageEvent event);

    /**
     * 统计今日收/发消息总数。
     * @return 消息总数
     */
    long countTodayMessages();

    /**
     * 统计今日有消息活动的群组数。
     * @return 活跃群组数量
     */
    long countTodayActiveGroups();

    /**
     * 分页查询指定群的聊天记录。
     * @param groupId 群号
     * @param page    页码（从 1 开始）
     * @param size    每页条数
     * @return 消息实体列表
     */
    List<ReceiveMessageEntity> listMessagesByGroup(Long groupId, int page, int size);

    /**
     * 统计指定群的消息总数。
     * @param groupId 群号
     * @return 消息总数
     */
    long countMessagesByGroup(Long groupId);
}

