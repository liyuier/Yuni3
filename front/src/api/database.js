import { request } from './request'

/**
 * 获取数据库表列表。
 */
export function getTableList() {
  return request('/admin/database/tables', { method: 'POST' })
}

/**
 * 查询表数据。
 * @param {string} tableName 表名
 * @param {number} page 页码
 * @param {number} size 每页条数
 */
export function queryTable(tableName, page = 1, size = 50) {
  return request('/admin/database/query', {
    method: 'POST',
    body: JSON.stringify({ table_name: tableName, page, size }),
  })
}
