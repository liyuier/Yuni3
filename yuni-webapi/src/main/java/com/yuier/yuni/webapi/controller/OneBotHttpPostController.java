package com.yuier.yuni.webapi.controller;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title: OneBotHttpPostController
 * @Author yuier
 * @Package com.yuier.yuni.webapi.controller
 * @Date 2025/12/22 2:06
 * @description: OneBot 采用 HTTP POST 上报方式的接收处
 */

@RestController
@RequestMapping("/")
public class OneBotHttpPostController {

    @Autowired
    OneBotAdapter adapter;

    @PostMapping
    public void receiveOneBotHttpPost(@RequestBody String rawJson) {
        System.out.println("Hello Yuni!");
        adapter.handleReportJson(rawJson);
    }
}
