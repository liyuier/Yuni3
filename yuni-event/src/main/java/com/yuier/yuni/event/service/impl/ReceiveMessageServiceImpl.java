package com.yuier.yuni.event.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuier.yuni.event.mapper.ReceiveMessageMapper;
import com.yuier.yuni.event.service.ReceiveMessageService;
import com.yuier.yuni.event.domain.entity.ReceiveMessageEntity;
import org.springframework.stereotype.Service;

/**
 * (ReceiveMessage)表服务实现类
 *
 * @author liyuier
 * @since 2025-12-27 01:26:23
 */
@Service
public class ReceiveMessageServiceImpl extends ServiceImpl<ReceiveMessageMapper, ReceiveMessageEntity> implements ReceiveMessageService {

    @Override
    public void saveEvent(ReceiveMessageEntity receiveMessageEntity) {
        this.save(receiveMessageEntity);
    }
}

