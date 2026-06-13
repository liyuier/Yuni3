package com.yuier.yuni.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuier.yuni.event.domain.entity.ReceiveMessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * (ReceiveMessage)表数据库访问层
 *
 * @author liyuier
 * @since 2025-12-27 01:26:24
 */
@Mapper
public interface ReceiveMessageMapper extends BaseMapper<ReceiveMessageEntity> {

    /** 统计今日有消息活动的群组数（按 group_id 去重） */
    @Select("SELECT COUNT(DISTINCT group_id) FROM receive_message WHERE time_stamp >= #{todayStart}")
    long countDistinctGroupToday(@Param("todayStart") long todayStart);
}

