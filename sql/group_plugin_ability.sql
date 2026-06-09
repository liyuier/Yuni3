/*
 Navicat Premium Data Transfer

 Source Server         : yunidb_out
 Source Server Type    : SQLite
 Source Server Version : 3035005 (3.35.5)
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3035005 (3.35.5)
 File Encoding         : 65001

 Date: 10/06/2026 01:42:43
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for group_plugin_ability
-- ----------------------------
DROP TABLE IF EXISTS "group_plugin_ability";
CREATE TABLE "group_plugin_ability" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "group_id" BIGINT NOT NULL,
  "plugin_id" VARCHAR(255) NOT NULL,
  "ability" INTEGER NOT NULL,
  UNIQUE ("group_id" ASC, "plugin_id" ASC)
);

-- ----------------------------
-- Auto increment value for group_plugin_ability
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 5 WHERE name = 'group_plugin_ability';

PRAGMA foreign_keys = true;
