package com.yuier.yuni.contact.manage;

import com.yuier.yuni.core.bot.BotGroupInfo;
import com.yuier.yuni.core.bot.BotGroupMemberInfo;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.core.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @Title: YuniContactManager
 * @Author yuier
 * @Package com.yuier.yuni.contact.manage
 * @Date 2026/6/17 17:26
 * @description: 联系人管理
 */

@Slf4j
@Component
public class YuniContactManager {

    @Autowired
    YuniContactContainer yuniContactContainer;

    private YuniBot getYuniBot() {
        return SpringContextUtil.getBean(YuniBot.class);
    }

    public void init() {
        initGroupIdSet();
        initMemberGroupMap();
        log.info("初始化联系人系统完成");
    }

    public void update() {
        updateGroupIdSet();
        updateMemberGroupMap();
    }

    private void updateGroupIdSet() {
        yuniContactContainer.getGroupIdSet().clear();
        initGroupIdSet();
    }

    private void updateMemberGroupMap() {
        yuniContactContainer.getMemberGroupMap().clear();
        initMemberGroupMap();
    }

    /**
     * 初始化群组 ID 集合
     */
    private void initGroupIdSet() {
        getYuniBot().getGroupList().ifPresent(groupList -> {
            for (BotGroupInfo groupInfo : groupList) {
                yuniContactContainer.addGroupId(groupInfo.getGroupId());
            }
        });
    }

    /**
     * 初始化成员群组映射
     */
    private void initMemberGroupMap() {
        yuniContactContainer.getGroupIdSet().forEach(groupId -> {
            getYuniBot().getGroupMemberList(groupId.toString()).ifPresent(memberList -> {
                for (BotGroupMemberInfo memberInfo : memberList) {
                    yuniContactContainer.addMemberGroupMap(memberInfo.getUserId(), groupId);
                }
            });
        });
    }

    // 根据用户 ID 查找用户所在的群组
    public HashSet<Long> findUserGroupIdSet(Long memberId) {
        return yuniContactContainer.findUserGroupIdSet(memberId);
    }
}
