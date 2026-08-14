import { request } from './request'

/**
 * 获取所有系统配置。
 */
export function getAllConfig() {
  return request('/admin/settings/all', { method: 'POST' })
}
