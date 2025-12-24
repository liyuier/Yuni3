CREATE TABLE `group_user_plugin_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, -- 数据库自增ID
    `group_id` BIGINT NOT NULL,  -- 群组 ID
    `plugin_id` VARCHAR(255) NOT NULL,  -- 插件 ID
    `user_id` BIGINT NOT NULL,  -- 用户 ID
    `permission_level` VARCHAR(10) NOT NULL,  -- 用户在该群组下对插件拥有的权限等级
    PRIMARY KEY (`id`), -- 主键用自增ID
    UNIQUE KEY `uk_group_plugin_user` (`group_id`, `plugin_id`, `user_id`) -- 业务唯一约束
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;