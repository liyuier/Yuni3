import { request } from './request'

/**
 * 获取 Bot 运行状态。
 * @returns {Promise<Object>} BotStatusResp
 */
export function getBotStatus() {
  return request('/admin/dashboard/bot-status', { method: 'POST' })
}

/**
 * 获取首页统计卡片数据。
 * @returns {Promise<Object>} DashboardStatsResp
 */
export function getDashboardStats() {
  return request('/admin/dashboard/stats', { method: 'POST' })
}

/**
 * 获取系统资源信息。
 * @returns {Promise<Object>} SystemInfoResp
 */
export function getSystemInfo() {
  return request('/admin/dashboard/system-info', { method: 'POST' })
}
