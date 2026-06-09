/*
 Navicat Premium Data Transfer

 Source Server         : yunidb_out
 Source Server Type    : SQLite
 Source Server Version : 3035005 (3.35.5)
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3035005 (3.35.5)
 File Encoding         : 65001

 Date: 10/06/2026 01:42:59
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for plugin_call
-- ----------------------------
DROP TABLE IF EXISTS "plugin_call";
CREATE TABLE "plugin_call" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "time_stamp" INTEGER NOT NULL,
  "format_time" TEXT,
  "bot_id" INTEGER,
  "message_id" INTEGER NOT NULL,
  "to_log_str" TEXT,
  "sender_id" INTEGER NOT NULL,
  "group_id" INTEGER,
  "plugin_module" TEXT,
  "plugin_id" TEXT NOT NULL,
  "plugin_name" TEXT,
  "plugin_description" TEXT,
  "plugin_author" TEXT,
  "plugin_detector_type" TEXT NOT NULL,
  "plugin_function_type" TEXT,
  "icon" TEXT,
  "home_page" TEXT,
  "source_code_address" TEXT
);

-- ----------------------------
-- Auto increment value for plugin_call
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 721 WHERE name = 'plugin_call';

PRAGMA foreign_keys = true;
