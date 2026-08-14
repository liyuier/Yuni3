import { request } from './request'

/**
 * 搜索 Redis 键。
 * @param {string} pattern 匹配模式
 */
export function searchKeys(pattern = '') {
  return request('/admin/redis/keys', {
    method: 'POST',
    body: JSON.stringify({ pattern }),
  })
}

/**
 * 获取键的值。
 * @param {string} key 键名
 */
export function getKeyValue(key) {
  return request('/admin/redis/value', {
    method: 'POST',
    body: JSON.stringify({ key }),
  })
}
