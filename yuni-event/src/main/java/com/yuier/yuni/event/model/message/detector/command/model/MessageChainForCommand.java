package com.yuier.yuni.event.model.message.detector.command.model;

import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.ReplySegment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: MessageChainForCommand
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command.model
 * @Date 2025/12/23 1:36
 * @description: 特化后供命令匹配器使用的消息链
 */

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageChainForCommand extends MessageChain {

    /**
     * 辅助字段，用于在匹配指令时，标识当前匹配的消息段在整个 cfo 中的位置
     * 从 0 开始
     */
    private int curSegIndex;

    /**
     * 回复消息
     * 如果消息链中包含回复消息，此处用于暂存，辅助后续解析逻辑
     */
    private ReplySegment replySegment;

    public MessageChainForCommand() {
        super();
        curSegIndex = 0;
    }

    /**
     * 获取当前消息段指针指向的消息段
     * @return  当前消息段指针指向的消息段
     */
    public MessageSegment getCurMessageSeg() {
        return getContent().get(curSegIndex);
    }

    /**
     * 消息段指针左移
     * @param step  左移步数
     */
    public void curSegIndexStepBackBy(Integer step) {
        curSegIndex -= step;
    }

    /**
     * 消息段指针右移
     * @param step  右移步数
     */
    public void curSegIndexStepForwardBy(Integer step) {
        curSegIndex += step;
    }

    /**
     * 消息段是否已经遍历完毕，即消息段指针是否越界
     * @return  消息段是否已经遍历完毕
     */
    public Boolean messageSegsMatchedEnd() {
        return curSegIndex >= getContent().size();
    }

    public Boolean indexOnLastSeg() {
        return curSegIndex == getContent().size() - 1;
    }

    /**
     * @return  从当前消息段开始（含），还剩多少消息段未匹配
     */
    public Integer restMessageSegNum() {
        return getContent().size() - curSegIndex;
    }

    public Boolean storesReplyData() {
        return replySegment == null;
    }

    public void resetCurSegIndex() {
        curSegIndex = 0;
    }
}
