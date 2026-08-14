import { request } from './request'

/**
 * 获取插件列表。
 * @returns {Promise<Array>} 插件信息数组
 */
export function getPluginList() {
  return request('/admin/plugin/list', { method: 'POST' })
}

/**
 * 启用插件。
 * @param {string} pluginId 插件全限定ID
 * @param {number|null} groupId 群号，null 为全局
 */
export function enablePlugin(pluginId, groupId) {
  return request('/admin/plugin/enable', {
    method: 'POST',
    body: JSON.stringify({ plugin_id: pluginId, group_id: groupId }),
  })
}

/**
 * 禁用插件。
 * @param {string} pluginId 插件全限定ID
 * @param {number|null} groupId 群号，null 为全局
 */
export function disablePlugin(pluginId, groupId) {
  return request('/admin/plugin/disable', {
    method: 'POST',
    body: JSON.stringify({ plugin_id: pluginId, group_id: groupId }),
  })
}
