CREATE TABLE IF NOT EXISTS `group_plugin_ability` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT, -- SQLite 自增主键
    `group_id` BIGINT NOT NULL,  -- 群组 ID
    `plugin_id` VARCHAR(255) NOT NULL,  -- 插件 ID
    `ability` INTEGER NOT NULL,  -- 是否使能，1 表示使能，0 表示禁用
    UNIQUE(`group_id`, `plugin_id`)
);