/*
 Navicat Premium Data Transfer

 Source Server         : yunidb_out
 Source Server Type    : SQLite
 Source Server Version : 3035005 (3.35.5)
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3035005 (3.35.5)
 File Encoding         : 65001

 Date: 10/06/2026 01:43:05
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for receive_message
-- ----------------------------
DROP TABLE IF EXISTS "receive_message";
CREATE TABLE "receive_message" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "time_stamp" INTEGER NOT NULL,
  "format_time" TEXT,
  "self_id" INTEGER NOT NULL,
  "message_type" TEXT,
  "sub_type" TEXT,
  "message_id" INTEGER NOT NULL,
  "sender_id" INTEGER NOT NULL,
  "sender_name" TEXT,
  "role" TEXT,
  "to_log_str" TEXT,
  "raw_message" TEXT NOT NULL,
  "raw_json" TEXT,
  "group_id" INTEGER,
  "message_format" TEXT,
  "real_id" INTEGER,
  "is_plain_text" integer,
  "is_self_sent" integer,
  "is_command" integer
);

-- ----------------------------
-- Auto increment value for receive_message
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 11188 WHERE name = 'receive_message';

PRAGMA foreign_keys = true;
