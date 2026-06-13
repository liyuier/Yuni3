package com.yuier.yuni.event.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuier.yuni.core.event.YuniMessageEvent;
import com.yuier.yuni.event.mapper.ReceiveMessageMapper;
import com.yuier.yuni.event.service.ReceiveMessageService;
import com.yuier.yuni.event.domain.entity.ReceiveMessageEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

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

    @Override
    public void flagAsCommandEvent(YuniMessageEvent event) {
        LambdaQueryWrapper<ReceiveMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReceiveMessageEntity::getMessageId, event.getMessageId());
        wrapper.eq(ReceiveMessageEntity::getTimeStamp, event.getTime());
        ReceiveMessageEntity receiveMessageEntity = this.getOne(wrapper);
        // 因为一定已经把消息保存下来了，所以这里直接修改
        receiveMessageEntity.setIsCommand(true);
        this.updateById(receiveMessageEntity);
    }

    @Override
    public long countTodayMessages() {
        LambdaQueryWrapper<ReceiveMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReceiveMessageEntity::getTimeStamp, todayStartSeconds());
        return this.count(wrapper);
    }

    @Override
    public long countTodayActiveGroups() {
        return this.baseMapper.countDistinctGroupToday(todayStartSeconds());
    }

    /**
     * 今天零点的时间戳（秒）。
     */
    private long todayStartSeconds() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
    }

    @Override
    public List<ReceiveMessageEntity> listMessagesByGroup(Long groupId, int page, int size) {
        LambdaQueryWrapper<ReceiveMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReceiveMessageEntity::getGroupId, groupId)
                .orderByDesc(ReceiveMessageEntity::getTimeStamp);
        Page<ReceiveMessageEntity> p = new Page<>(page, size);
        return this.page(p, wrapper).getRecords();
    }

    @Override
    public long countMessagesByGroup(Long groupId) {
        LambdaQueryWrapper<ReceiveMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReceiveMessageEntity::getGroupId, groupId);
        return this.count(wrapper);
    }
}

