<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAllConfig } from '../api/settings'

const config = ref(null)
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try { config.value = await getAllConfig() } catch (e) { error.value = e.message || '加载失败' } finally { loading.value = false }
})

function get(obj, path) {
  const keys = path.split('.')
  let cur = obj
  for (const k of keys) { if (!cur || typeof cur !== 'object') return null; cur = cur[k] }
  return cur
}
function str(path, fallback = '--') {
  const v = get(config.value, path)
  return v != null ? String(v) : fallback
}

const adapterMode = computed(() => {
  const mode = get(config.value, 'yuni.protocol.onebot.mode')
  return mode === 'ws' ? 'WebSocket' : mode === 'http' ? 'HTTP' : (mode || '--')
})

const sections = computed(() => [
  {
    icon: '🗄', title: '数据库',
    rows: [
      ['类型', 'SQLite'],
      ['文件路径', str('bot.app.sqlite-db-file')],
      ['ID 生成策略', str('mybatis-plus.global-config.db-config.id-type')],
    ]
  },
  {
    icon: '⚡', title: 'Redis',
    rows: [
      ['主机', str('spring.data.redis.host')],
      ['端口', str('spring.data.redis.port')],
      ['密码', str('spring.data.redis.password')],
      ['数据库索引', str('spring.data.redis.database')],
      ['连接超时', str('spring.data.redis.timeout')],
      ['最大活跃连接', str('spring.data.redis.lettuce.pool.max-active')],
      ['最大空闲连接', str('spring.data.redis.lettuce.pool.max-idle')],
    ]
  },
  {
    icon: '📋', title: '日志',
    rows: [
      ['配置文件', str('logging.config')],
      ['MyBatis 日志实现', str('mybatis-plus.configuration.log-impl')],
    ]
  },
  {
    icon: '🤖', title: '机器人',
    rows: [
      ['QQ', str('bot.qq')],
      ['昵称', str('bot.nickname')],
      ['主人 QQ', str('bot.master-qq')],
      ['命令前缀', str('bot.app.command-flag')],
      ['插件目录', str('bot.app.plugin.directory')],
      ['SQLite 数据库文件', str('bot.app.sqlite-db-file')],
    ]
  },
  {
    icon: '🔌', title: '适配器',
    rows: [
      ['协议类型', str('yuni.protocol.type')],
      ['通信模式', adapterMode.value],
      ['Token', str('yuni.protocol.onebot.token')],
      ['连接地址', str('yuni.protocol.onebot.ws.url')],
      ['心跳间隔', str('yuni.protocol.onebot.ws.heartbeat-interval')],
      ['连接超时', str('yuni.protocol.onebot.ws.timeout')],
      ['重连等待', str('yuni.protocol.onebot.ws.reconnect-wait-seconds') + 's'],
      ['最大重试次数', str('yuni.protocol.onebot.retry.max-retries') + ' 次'],
      ['重试退避基数', str('yuni.protocol.onebot.retry.backoff-base-ms') + 'ms'],
      ['服务端口', str('server.port')],
      ['版本', str('yuni.version')],
    ]
  },
])
</script>

<template>
  <div class="flex flex-col min-h-0 h-full">
    <div class="shrink-0">
      <h2 class="text-xl 2xl:text-2xl font-bold text-pink-800 mb-2">⚙ 系统设置</h2>
      <p class="text-sm 2xl:text-base text-pink-400 mb-6">当前运行配置（敏感字段已脱敏）</p>
    </div>

    <div v-if="loading" class="yuni-card"><div class="flex items-center gap-3 text-pink-400 py-6"><span class="animate-pulse text-xl">🌸</span><span class="text-base">加载配置中...</span></div></div>
    <div v-else-if="error" class="yuni-card"><div class="flex items-center justify-between py-4"><span class="text-pink-500 text-base">⚠ {{ error }}</span><button class="px-4 py-1.5 rounded-lg bg-pink-100 text-pink-600 font-medium hover:bg-pink-200 transition-colors" @click="onMounted">重试</button></div></div>

    <template v-else-if="config">
      <div class="flex-1 overflow-y-auto min-h-0 space-y-6">
        <div v-for="sec in sections" :key="sec.title" class="yuni-card">
          <h3 class="flex items-center gap-2.5 text-base 2xl:text-lg font-bold text-pink-700 mb-5 pb-3 border-b border-pink-100">
            <span class="text-xl">{{ sec.icon }}</span> {{ sec.title }}
          </h3>
          <div class="space-y-2">
            <div
              v-for="[label, value] in sec.rows" :key="label"
              class="flex items-center py-2.5 px-4 rounded-xl hover:bg-pink-100/60 transition-all duration-150"
            >
              <span class="text-sm 2xl:text-base text-pink-400 w-40 shrink-0">{{ label }}</span>
              <span class="text-sm 2xl:text-base text-pink-700 font-mono break-all">{{ value }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
