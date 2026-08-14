/**
 * WebSocket 连接管理。
 * 连接 ws://localhost:11451/ws/chat，支持订阅/取消订阅群组消息推送。
 */

let ws = null
const listeners = new Map() // groupId → callback

export function connectChatWs() {
  if (ws && ws.readyState === WebSocket.OPEN) return

  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  ws = new WebSocket(`${protocol}//${location.host}/ws/chat`)

  ws.onopen = () => console.log('[WS] 聊天连接已建立')

  ws.onmessage = (event) => {
    try {
      const payload = JSON.parse(event.data)
      if (payload.type === 'new_message') {
        const msg = payload.data
        // 从 segments 反查 groupId —— 推送消息本身不含 group_id
        // 广播给所有已注册监听器，由监听器自行过滤
        for (const [, callback] of listeners) {
          callback(msg)
        }
      }
    } catch (e) {
      // ignore
    }
  }

  ws.onclose = () => console.log('[WS] 聊天连接关闭')
  ws.onerror = () => {} // ignore
}

export function disconnectChatWs() {
  if (ws) ws.close()
  ws = null
  listeners.clear()
}

/**
 * 订阅指定群组的消息推送。
 * @param {number} groupId 群号
 * @param {Function} callback 收到新消息时的回调
 * @returns {Function} 取消订阅函数
 */
export function subscribeGroup(groupId, callback) {
  listeners.set(groupId, callback)
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({ action: 'subscribe', group_id: groupId }))
  }
  // 延迟发送订阅——WS 可能尚未就绪
  const sendSubscribe = () => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({ action: 'subscribe', group_id: groupId }))
    }
  }
  if (ws?.readyState === WebSocket.CONNECTING) {
    ws.addEventListener('open', sendSubscribe, { once: true })
  }
  return () => {
    listeners.delete(groupId)
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({ action: 'unsubscribe', group_id: groupId }))
    }
  }
}
