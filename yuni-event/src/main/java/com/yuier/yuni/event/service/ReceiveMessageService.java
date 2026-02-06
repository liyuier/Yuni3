package com.yuier.yuni.event.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.domain.entity.ReceiveMessageEntity;

/**
 * (ReceiveMessage)表服务接口
 *
 * @author liyuier
 * @since 2025-12-27 01:26:23
 */
public interface ReceiveMessageService extends IService<ReceiveMessageEntity> {

    void saveEvent(ReceiveMessageEntity receiveMessageEntity);

    void flagAsCommandEvent(YuniMessageEvent event);
}

