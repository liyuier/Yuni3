const BASE = ''

/**
 * 统一请求封装。
 * - 自动拼接 Content-Type: application/json
 * - 解包 Result 结构，code !== 200 时抛错
 * @param {string} url    请求路径，如 /admin/plugin/list
 * @param {object} options fetch 配置
 * @returns {Promise<any>} 解包后的 data
 */
export async function request(url, options = {}) {
  const resp = await fetch(`${BASE}${url}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

  if (!resp.ok) {
    throw new Error(`请求失败 HTTP ${resp.status}`)
  }

  const json = await resp.json()
  if (json.code !== 200) {
    throw new Error(json.message || '请求失败')
  }

  return json.data
}
