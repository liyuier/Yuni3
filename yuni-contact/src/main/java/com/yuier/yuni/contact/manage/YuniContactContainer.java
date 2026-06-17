package com.yuier.yuni.contact.manage;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @Title: YuniContactContainer
 * @Author yuier
 * @Package com.yuier.yuni.contact.manage
 * @Date 2026/6/17 17:29
 * @description: 联系人容器
 */

@Data
@Component
public class YuniContactContainer {

    private HashSet<Long> groupIdSet = new HashSet<>();

    private HashMap<Long, HashSet<Long>> memberGroupMap = new HashMap<>();

    /**
     * 添加群组 ID
     * @param groupId 群组 ID
     */
    public void addGroupId(Long groupId) {
        groupIdSet.add(groupId);
    }

    /**
     * 添加成员到群组
     * @param memberId 成员 ID
     * @param groupId 群组 ID
     */
    public void addMemberGroupMap(Long memberId, Long groupId) {
        HashSet<Long> memberGroup = memberGroupMap.getOrDefault(memberId, new HashSet<>());
        memberGroup.add(groupId);
        memberGroupMap.put(memberId, memberGroup);
    }

    /**
     * 查询成员加入的群组
     * @param memberId 成员 ID
     * @return 群组 ID 列表
     */
    public HashSet<Long> findUserGroupIdSet(Long memberId) {
        return memberGroupMap.getOrDefault(memberId, new HashSet<>());
    }
}
