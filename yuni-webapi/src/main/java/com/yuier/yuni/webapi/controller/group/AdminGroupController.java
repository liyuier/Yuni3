package com.yuier.yuni.webapi.controller.group;

import com.yuier.yuni.webapi.dto.Result;
import com.yuier.yuni.webapi.dto.group.GroupMessagesReq;
import com.yuier.yuni.webapi.dto.group.GroupPluginStatusReq;
import com.yuier.yuni.webapi.service.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 群组管理接口。
 */
@RestController
@RequestMapping("/admin/groups")
@RequiredArgsConstructor
public class AdminGroupController {

    private final GroupService groupService;

    /**
     * 获取群组列表
     * @return 群组信息列表
     */
    @PostMapping("/list")
    public Result<?> groupList() {
        return Result.ok(groupService.listGroups());
    }

    /**
     * 获取指定群的聊天记录
     * @param req 请求体，包含群号 groupId、页码 page、每页条数 size
     * @return 消息列表 + 总数
     */
    @PostMapping("/messages")
    public Result<?> messages(@RequestBody GroupMessagesReq req) {
        if (req.getGroupId() == null) {
            return Result.fail(400, "群号不能为空");
        }
        return Result.ok(groupService.getMessages(
                req.getGroupId(),
                Math.max(1, req.getPage()),
                Math.max(1, Math.min(100, req.getSize()))
        ));
    }

    /**
     * 获取指定群的插件启用状态
     * @param req 请求体，包含群号 groupId
     * @return 插件启用状态列表
     */
    @PostMapping("/plugin-status")
    public Result<?> pluginStatus(@RequestBody GroupPluginStatusReq req) {
        if (req.getGroupId() == null) {
            return Result.fail(400, "群号不能为空");
        }
        return Result.ok(groupService.getPluginStatus(req.getGroupId()));
    }

}
