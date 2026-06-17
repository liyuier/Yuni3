package com.yuier.yuni.contact.manage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 联系人关系定时同步任务。
 * 每隔 5 分钟全量刷新群组与成员关系。
 */
@Slf4j
@Component
public class ContactScheduledTask {

    @Autowired
    private YuniContactManager contactManager;

    @Scheduled(initialDelay = 5 * 60 * 1000, fixedRate = 5 * 60 * 1000)
    public void syncContacts() {
        log.debug("定时同步联系人关系...");
        contactManager.update();
    }
}
