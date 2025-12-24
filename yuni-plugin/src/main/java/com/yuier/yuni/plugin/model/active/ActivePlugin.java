package com.yuier.yuni.plugin.model.active;

import com.yuier.yuni.plugin.model.YuniPlugin;

/**
 * @Title: ActivePlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.active
 * @Date 2025/12/25 0:27
 * @description:
 */

public interface ActivePlugin extends YuniPlugin {

    /**
     * 获取执行动作，该动作会在注册完毕后立刻执行一次
     * @return 动作
     */
    Action getAction();
}
