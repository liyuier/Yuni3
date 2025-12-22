package com.yuier.yuni.plugin.model;

/**
 * @Title: Action
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/22 22:44
 * @description: 主动插件的动作
 */

@FunctionalInterface
public interface Action {
    void execute();
}
