CREATE TABLE receive_message (
     id INTEGER PRIMARY KEY AUTOINCREMENT,  -- 自增ID（主键，唯一标识每条记录）
     time_stamp INTEGER NOT NULL,           -- 消息时间戳（毫秒级，用于快速时间范围查询）
     format_time TEXT,                      -- 格式化时间（如"2025-01-01 12:30:00"，业务层填充）
     self_id INTEGER NOT NULL,              -- 机器人QQ号（发送方ID，必须存在）
     message_type TEXT,                     -- 消息类型（如"group", "private"）
     sub_type TEXT,                         -- 消息子类型（如"friend", "group"）
     message_id INTEGER NOT NULL,           -- 消息唯一ID（QQ消息ID，必须存在）
     sender_id INTEGER NOT NULL,            -- 发送者QQ号（必须存在，用于关联用户）
     sender_name TEXT,                      -- 发送者名称（可为空，如匿名消息）
     role TEXT,                             -- 角色（如"member", "admin"，群消息有用）
     to_log_str TEXT,                       -- 日志格式化内容（用于日志系统，如"用户: hello"）
     raw_message TEXT NOT NULL,             -- 原始消息内容（必须存在，核心业务数据）
     raw_json TEXT,                         -- 原始JSON（可选，用于调试）
     group_id INTEGER,                      -- 群号（私聊消息为NULL）
     message_format TEXT,                   -- 消息格式（如"array", "cqcode"）
     real_id INTEGER                        -- 真实ID（用于消息溯源，可为空）
);