import { request } from './request'

/**
 * 获取群组列表。
 */
export function getGroupList() {
  return request('/admin/groups/list', { method: 'POST' })
}

/**
 * 获取指定群的聊天记录。
 * @param {number} groupId 群号
 * @param {number} page 页码
 * @param {number} size 每页条数
 */
export function getGroupMessages(groupId, page = 1, size = 50) {
  return request('/admin/groups/messages', {
    method: 'POST',
    body: JSON.stringify({ group_id: groupId, page, size }),
  })
}

/**
 * 获取指定群的插件启用状态。
 * @param {number} groupId 群号
 */
export function getGroupPluginStatus(groupId) {
  return request('/admin/groups/plugin-status', {
    method: 'POST',
    body: JSON.stringify({ group_id: groupId }),
  })
}
