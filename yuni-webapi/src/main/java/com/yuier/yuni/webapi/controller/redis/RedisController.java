package com.yuier.yuni.webapi.controller.redis;

import com.yuier.yuni.webapi.dto.Result;
import com.yuier.yuni.webapi.service.redis.RedisViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Redis 查看接口。
 */
@RestController
@RequestMapping("/admin/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisViewService redisViewService;

    /**
     * 搜索 Redis 键
     * @param body 请求体，含 pattern 字段
     * @return 键条目列表
     */
    @PostMapping("/keys")
    public Result<?> searchKeys(@RequestBody Map<String, String> body) {
        return Result.ok(redisViewService.searchKeys(body.get("pattern")));
    }

    /**
     * 获取指定键的值
     * @param body 请求体，含 key 字段
     * @return 值对象
     */
    @PostMapping("/value")
    public Result<?> getValue(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        if (key == null || key.isBlank()) {
            return Result.fail(400, "key 不能为空");
        }
        return Result.ok(redisViewService.getValue(key));
    }
}
