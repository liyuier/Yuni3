package com.yuier.yuni.core.constants;

import java.util.List;

/**
 * @Title: MessageSegmentTypes
 * @Author yuier
 * @Package com.yuier.yuni.core.constants
 * @Date 2025/12/23 0:36
 * @description: 消息段类型
 */
public final class MessageSegmentTypes {

    public static final String ANONYMOUS = "anonymous";
    public static final String AT = "at";
    public static final String CONTACT = "contact";
    public static final String DICE = "dice";
    public static final String FACE = "face";
    public static final String FILE = "file";
    public static final String FORWARD = "forward";
    public static final String IMAGE = "image";
    public static final String JSON = "json";
    public static final String LOCATION = "location";
    public static final String MARKDOWN = "markdown";
    public static final String MARKETFACE = "mface";
    public static final String MUSIC = "music";
    public static final String NODE = "node";
    public static final String POKE = "poke";
    public static final String RECORD = "record";
    public static final String REPLY = "reply";
    public static final String RPS = "rps";
    public static final String SHAKE = "shake";
    public static final String SHARE = "share";
    public static final String TEXT = "text";
    public static final String VIDEO = "video";
    public static final String XML = "xml";
    private static final String[] allArr = new String[]{
            TEXT, ANONYMOUS, AT, CONTACT, DICE, FACE, FILE, FORWARD, IMAGE, JSON, LOCATION, MARKDOWN, MARKETFACE, MUSIC,
            NODE, POKE, RECORD, REPLY, RPS, SHAKE, SHARE, VIDEO, XML
    };
    public static final List<String> ALL = List.of(allArr);
}
