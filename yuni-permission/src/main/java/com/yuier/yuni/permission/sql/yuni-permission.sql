CREATE TABLE IF NOT EXISTS `group_user_plugin_permission` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT, -- SQLite 自增主键
    `group_id` BIGINT NOT NULL,  -- 群组 ID
    `plugin_id` VARCHAR(255) NOT NULL,  -- 插件 ID
    `user_id` BIGINT NOT NULL,  -- 用户 ID
    `permission_level` INT NOT NULL,  -- 权限等级
    UNIQUE(`group_id`, `plugin_id`, `user_id`)
);