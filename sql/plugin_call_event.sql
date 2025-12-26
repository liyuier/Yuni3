CREATE TABLE plugin_call (
     id INTEGER PRIMARY KEY AUTOINCREMENT,  -- 自增ID（主键，唯一标识每条插件调用记录）
     time_stamp INTEGER NOT NULL,           -- 消息时间戳（秒级，必须！）
     format_time TEXT,                      -- 格式化时间（东八区，如"2025-01-01 20:30:00"）
     bot_id INTEGER,                        -- 机器人ID
     message_id INTEGER NOT NULL,           -- 消息ID（必须！）
     to_log_str TEXT,                       -- 消息日志（如"用户: 你好"）
     sender_id INTEGER NOT NULL,            -- 发送者ID（必须！）
     group_id INTEGER,                      -- 群组ID（私聊消息为NULL）
     plugin_module TEXT,                    -- 插件所在模块（如"weather", "news"）
     plugin_id TEXT NOT NULL,               -- 插件唯一ID（如"weather-1.0"）（必须！）
     plugin_name TEXT,                      -- 插件名称（如"天气查询"）
     plugin_description TEXT,               -- 插件描述（如"查询实时天气"）
     plugin_author TEXT,                    -- 插件作者（如"阿里云"）
     plugin_detector_type TEXT NOT NULL,    -- 插件触发器类别（主动、被动等）
     plugin_function_type TEXT,             -- 插件功能类别，元数据里写的那个
     key_words TEXT,                        -- 关键词（逗号分隔，如"天气,预报"，业务填充）
     icon TEXT,                             -- 插件图标（URL）
     home_page TEXT,                        -- 插件主页（URL）
     source_code_address TEXT               -- 源码地址（URL）
);