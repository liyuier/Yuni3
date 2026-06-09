/*
 Navicat Premium Data Transfer

 Source Server         : yunidb_out
 Source Server Type    : SQLite
 Source Server Version : 3035005 (3.35.5)
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3035005 (3.35.5)
 File Encoding         : 65001

 Date: 10/06/2026 01:42:51
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for group_user_plugin_permission
-- ----------------------------
DROP TABLE IF EXISTS "group_user_plugin_permission";
CREATE TABLE "group_user_plugin_permission" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "group_id" BIGINT NOT NULL,
  "plugin_id" VARCHAR(255) NOT NULL,
  "user_id" BIGINT NOT NULL,
  "permission_level" INT NOT NULL,
  UNIQUE ("group_id" ASC, "plugin_id" ASC, "user_id" ASC)
);

PRAGMA foreign_keys = true;
