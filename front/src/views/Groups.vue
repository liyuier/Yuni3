<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { getGroupList, getGroupMessages, getGroupPluginStatus } from '../api/groups'
import { enablePlugin, disablePlugin } from '../api/plugin'
import MessageBubble from '../components/MessageBubble.vue'
import ImageViewer from '../components/ImageViewer.vue'
import { connectChatWs, subscribeGroup, disconnectChatWs } from '../api/ws'

const groups = ref([])
const messages = ref([])
const plugins = ref([])
const activeGroup = ref(null)
const drawerOpen = ref(false)
const msgLoading = ref(false)
const pluginLoading = ref(false)

const searchQuery = ref('')

// 分页
const msgPage = ref(1)
const msgTotal = ref(0)
const msgHasMore = ref(true)
const loadingMore = ref(false)

// 聊天区 DOM 引用
const chatBody = ref(null)

const filteredGroups = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return groups.value
  return groups.value.filter(g =>
    (g.group_name || '').toLowerCase().includes(q) ||
    String(g.group_id).includes(q)
  )
})

const enabledCount = computed(() => plugins.value.filter(p => p.enabled).length)

// 当前群组 WS 取消订阅函数
let wsUnsub = null

onMounted(async () => {
  connectChatWs()
  try { groups.value = await getGroupList() } catch (e) { groups.value = [] }
  if (groups.value.length) selectGroup(groups.value[0])
})

let loadSeq = 0
// 首页加载完成前禁止 loadMore，避免 scrollToBottom 触发误翻页
let initDone = false

// 图片查看器
const viewerSrc = ref('')
const viewerVisible = ref(false)
function openViewer(src) {
  viewerSrc.value = src
  viewerVisible.value = true
}

async function selectGroup(group) {
  initDone = false
  // 切换 WS 订阅
  if (wsUnsub) wsUnsub()
  wsUnsub = subscribeGroup(group.group_id, (msg) => {
    messages.value = [...messages.value, msg]
    nextTick(() => scrollToBottom())
  })

  activeGroup.value = group
  messages.value = []
  msgPage.value = 1
  msgHasMore.value = true
  msgLoading.value = false
  loadingMore.value = false
  loadSeq++
  const seq = loadSeq
  await loadMessages(group.group_id, 1, seq)
  // 等 scrollToBottom 的 nextTick 全部走完再开锁
  await nextTick()
  initDone = true
}

async function loadMessages(groupId, page, seq) {
  if (seq !== loadSeq) return
  if (page === 1) msgLoading.value = true
  else loadingMore.value = true

  try {
    const resp = await getGroupMessages(groupId, page, 50)
    if (seq !== loadSeq) return
    const batch = (resp.messages || []).reverse()
    if (page === 1) {
      messages.value = batch
    } else {
      messages.value = [...batch, ...messages.value]
    }
    msgTotal.value = resp.total || 0
    msgHasMore.value = messages.value.length < msgTotal.value
    if (page === 1) {
      await nextTick()
      scrollToBottom()
    }
  } catch (e) {
    // ignore
  } finally {
    if (seq === loadSeq) {
      msgLoading.value = false
      loadingMore.value = false
    }
  }
}

async function loadMore() {
  if (!initDone || loadingMore.value || !msgHasMore.value || !activeGroup.value) return
  const page = msgPage.value + 1
  msgPage.value = page
  const el = chatBody.value
  const prevHeight = el ? el.scrollHeight : 0
  loadSeq++
  const seq = loadSeq
  await loadMessages(activeGroup.value.group_id, page, seq)
  await nextTick()
  if (el && seq === loadSeq) {
    el.scrollTop = el.scrollHeight - prevHeight
  }
}

function scrollToBottom() {
  const el = chatBody.value
  if (el) el.scrollTop = el.scrollHeight
}

function onChatScroll() {
  if (!initDone) return
  const el = chatBody.value
  if (!el) return
  if (el.scrollTop < 40 && !loadingMore.value) {
    loadMore()
  }
}

watch(drawerOpen, async (open) => {
  if (open && activeGroup.value) {
    pluginLoading.value = true
    try {
      plugins.value = await getGroupPluginStatus(activeGroup.value.group_id)
    } catch (e) {
      plugins.value = []
    } finally {
      pluginLoading.value = false
    }
  }
})

async function togglePlugin(plugin) {
  try {
    if (plugin.enabled) {
      await disablePlugin(plugin.full_id, activeGroup.value.group_id)
    } else {
      await enablePlugin(plugin.full_id, activeGroup.value.group_id)
    }
    plugin.enabled = !plugin.enabled
  } catch (e) { /* 失败静默 */ }
}

const typeLabel = {
  SCHEDULED: '定时', IMMEDIATE: '即时', COMMAND: '命令',
  PATTERN: '模式', NOTICE: '通知', REQUEST: '请求',
  META: '元事件', MESSAGE_SENT: '消息发送',
}

const typeColor = {
  SCHEDULED: 'bg-amber-50 text-amber-600',
  IMMEDIATE: 'bg-emerald-50 text-emerald-600',
  COMMAND: 'bg-blue-50 text-blue-600',
  PATTERN: 'bg-violet-50 text-violet-600',
  NOTICE: 'bg-cyan-50 text-cyan-600',
  REQUEST: 'bg-orange-50 text-orange-600',
  META: 'bg-rose-50 text-rose-600',
  MESSAGE_SENT: 'bg-teal-50 text-teal-600',
}
</script>

<template>
  <div class="flex-1 min-h-0 flex flex-col">
    <h2 class="text-xl 2xl:text-2xl font-bold text-pink-800 mb-4 shrink-0">🐧 群组管理</h2>
    <div class="flex gap-0 w-full flex-1 relative rounded-2xl overflow-hidden"
         style="background: rgba(255,255,255,0.6); border: 1px solid rgba(248,165,182,0.3);">

      <!-- ====== 左侧群组目录 ====== -->
      <div
        class="w-60 shrink-0 flex flex-col"
        style="background: rgba(255,255,255,0.7); border-right: 1px solid rgba(248,165,182,0.3);"
      >
        <div class="px-4 pt-4 pb-3">
          <div class="relative">
            <span class="absolute left-3 top-1/2 -translate-y-1/2 text-sm text-pink-300">🔍</span>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索群组..."
              class="pl-9 pr-3 py-1.5 text-sm 2xl:text-base rounded-xl border border-pink-200
                     bg-white/70 text-pink-700 placeholder-pink-300 w-full
                     focus:outline-none focus:border-pink-400 transition-colors"
            />
          </div>
        </div>

        <div class="flex-1 overflow-y-auto px-2 pb-2">
          <div
            v-for="g in filteredGroups"
            :key="g.group_id"
            class="flex items-center gap-3 px-3 py-2.5 rounded-xl cursor-pointer transition-all mb-0.5"
            :class="activeGroup?.group_id === g.group_id
              ? 'bg-pink-100/80'
              : 'hover:bg-pink-50/60'"
            @click="selectGroup(g)"
          >
            <div
              class="w-10 h-10 rounded-full shrink-0 flex items-center justify-center text-lg"
              :class="activeGroup?.group_id === g.group_id ? 'bg-pink-200' : 'bg-pink-100'"
            >🐧</div>
            <div class="flex-1 min-w-0">
              <span
                class="text-sm 2xl:text-base font-medium truncate block"
                :class="activeGroup?.group_id === g.group_id ? 'text-pink-700' : 'text-pink-600'"
              >{{ g.group_name || '未命名群组' }}</span>
              <span class="text-xs text-pink-400">{{ g.member_count }} 人</span>
            </div>
          </div>

          <div v-if="!filteredGroups.length" class="text-center py-8 text-sm text-pink-400">
            未找到匹配的群组
          </div>
        </div>
      </div>

      <!-- ====== 右侧聊天区域 ====== -->
      <div class="flex-1 flex flex-col min-w-0 relative">
        <!-- 聊天头部 -->
        <div
          class="flex items-center justify-between px-5 py-3 shrink-0"
          style="background: rgba(255,255,255,0.5); border-bottom: 1px solid rgba(248,165,182,0.2);"
        >
          <div class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-full bg-pink-100 flex items-center justify-center text-sm">🐧</div>
            <div>
              <h3 class="font-bold text-sm 2xl:text-base text-pink-700">
                {{ activeGroup?.group_name || '选择一个群组' }}
              </h3>
              <p class="text-xs text-pink-400">
                {{ activeGroup ? activeGroup.member_count + ' 名成员' : '' }}
              </p>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <button
              v-if="activeGroup"
              class="text-xs 2xl:text-sm px-2 py-1 rounded-lg
                     text-pink-400 hover:text-pink-600 hover:bg-pink-50 transition-colors"
              @click="selectGroup(activeGroup)"
              :disabled="msgLoading"
            >🔄</button>
            <button
              v-if="activeGroup"
              class="text-xs 2xl:text-sm px-3 py-1.5 rounded-lg
                     bg-pink-100 text-pink-600 font-medium
                     hover:bg-pink-200 hover:text-pink-700 transition-colors"
              @click="drawerOpen = true"
            >群详情</button>
          </div>
        </div>

        <!-- 消息列表 -->
        <div
          ref="chatBody"
          class="flex-1 overflow-y-auto overflow-x-hidden px-5 py-4 space-y-4"
          style="background: linear-gradient(180deg, rgba(255,255,255,0.3) 0%, rgba(254,242,245,0.2) 100%);"
          @scroll="onChatScroll"
        >
          <!-- 顶部加载更多指示器 -->
          <div v-if="loadingMore" class="text-center py-3">
            <span class="text-xs text-pink-400 animate-pulse">🌸 加载更多...</span>
          </div>
          <div v-else-if="msgHasMore" class="text-center py-3">
            <span class="text-xs text-pink-300">向上滚动加载更多</span>
          </div>

          <div v-if="msgLoading && !loadingMore" class="text-center py-8 text-pink-400 text-sm">
            <span class="animate-pulse">🌸</span> 加载中...
          </div>

          <div v-else-if="!messages.length && activeGroup" class="text-center py-12">
            <div class="text-3xl mb-2">💬</div>
            <p class="text-pink-400 text-sm">暂无聊天记录</p>
          </div>

          <div
            v-for="msg in messages"
            :key="msg.sender_name + msg.time"
            class="flex"
            :class="msg.self_sent ? 'justify-end' : 'justify-start'"
          >
            <div v-if="!msg.self_sent" class="flex items-start gap-2 min-w-0 max-w-[85%] sm:max-w-[75%] lg:max-w-[65%] xl:max-w-[55%] 2xl:max-w-[45%]">
              <div class="w-7 h-7 rounded-full bg-pink-100 flex items-center justify-center text-xs shrink-0 mt-1">
                {{ (msg.sender_name || '?').charAt(0) }}
              </div>
              <div>
                <span class="text-xs text-pink-400 mb-0.5 flex items-center gap-1.5">
                  <span>{{ msg.sender_name }}</span>
                  <span class="text-pink-300">{{ msg.time.slice(5) }}</span>
                </span>
                <div
                  class="rounded-2xl rounded-tl-sm px-3.5 py-2.5 text-sm 2xl:text-base"
                  style="background: rgba(255,255,255,0.85); color: #4a3448;"
                >
                  <MessageBubble
                    :segments="msg.segments"
                    :plain-text="msg.plain_text"
                    :message="msg.message"
                    @img-click="openViewer"
                  />
                </div>
              </div>
            </div>

            <div v-else class="flex items-start gap-2 flex-row-reverse min-w-0 max-w-[85%] sm:max-w-[75%] lg:max-w-[65%] xl:max-w-[55%] 2xl:max-w-[45%]">
              <div class="w-7 h-7 rounded-full bg-pink-100 flex items-center justify-center text-xs shrink-0 mt-1">
                {{ (msg.sender_name || '?').charAt(0) }}
              </div>
              <div>
                <span class="text-xs text-pink-400 mb-0.5 flex items-center gap-1.5 justify-end">
                  <span class="text-pink-300">{{ msg.time.slice(5) }}</span>
                  <span>{{ msg.sender_name }}</span>
                </span>
                <div
                  class="rounded-2xl rounded-tr-sm px-3.5 py-2.5 text-sm 2xl:text-base"
                  style="background: linear-gradient(135deg, #f8bbd0, #f48fb1); color: white;"
                >
                  <MessageBubble
                    :segments="msg.segments"
                    :plain-text="msg.plain_text"
                    :message="msg.message"
                    @img-click="openViewer"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- ====== 抽屉 ====== -->
        <Transition name="drawer-overlay">
          <div
            v-if="drawerOpen"
            class="absolute inset-0 z-10 bg-black/20"
            @click="drawerOpen = false"
          ></div>
        </Transition>

        <Transition name="drawer">
          <div
            v-if="drawerOpen"
            class="absolute top-0 right-0 z-20 h-full w-80 flex flex-col shadow-2xl rounded-r-2xl"
            style="background: rgba(255,255,255,0.97); border-left: 1px solid rgba(248,165,182,0.3);"
          >
            <div class="flex items-center justify-between px-5 py-4 border-b border-pink-100">
              <h3 class="font-bold text-base 2xl:text-lg text-pink-700">群组详情</h3>
              <button
                class="w-7 h-7 flex items-center justify-center rounded-full
                       text-pink-300 hover:text-pink-500 hover:bg-pink-50 transition-colors"
                @click="drawerOpen = false"
              >✕</button>
            </div>

            <div class="px-5 py-4 border-b border-pink-50 space-y-2 text-sm 2xl:text-base">
              <div class="flex justify-between">
                <span class="text-pink-400">群名</span>
                <span class="text-pink-700 font-medium">{{ activeGroup?.group_name }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-pink-400">群号</span>
                <span class="text-pink-700 font-mono">{{ activeGroup?.group_id }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-pink-400">成员</span>
                <span class="text-pink-700">{{ activeGroup?.member_count }} / {{ activeGroup?.max_member_count }}</span>
              </div>
            </div>

            <div class="flex-1 overflow-y-auto px-5 py-4">
              <div class="flex items-center justify-between mb-3">
                <p class="text-xs 2xl:text-sm font-medium text-pink-400 uppercase tracking-wider">插件启用状态</p>
                <span class="text-xs text-pink-400">{{ enabledCount }} / {{ plugins.length }}</span>
              </div>

              <div v-if="pluginLoading" class="text-center py-8 text-pink-400 text-sm">
                <span class="animate-pulse">🌸</span> 加载中...
              </div>

              <div v-else class="space-y-1">
                <div
                  v-for="p in plugins"
                  :key="p.full_id"
                  class="flex items-center justify-between py-2 px-3 rounded-xl
                         hover:bg-pink-50/60 transition-colors"
                >
                  <div class="flex items-center gap-2 min-w-0">
                    <span class="text-sm 2xl:text-base text-pink-700 truncate">{{ p.name }}</span>
                    <span
                      class="shrink-0 text-xs px-1.5 py-0.5 rounded-full font-medium"
                      :class="typeColor[p.type] || 'bg-gray-50 text-gray-500'"
                    >{{ typeLabel[p.type] || p.type }}</span>
                  </div>
                  <button
                    class="shrink-0 w-10 h-5 rounded-full transition-colors relative"
                    :class="p.enabled ? 'bg-pink-400' : 'bg-gray-200'"
                    @click="togglePlugin(p)"
                  >
                    <span
                      class="absolute top-0.5 w-4 h-4 rounded-full bg-white shadow transition-all"
                      :class="p.enabled ? 'left-5' : 'left-0.5'"
                    ></span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </Transition>
      </div>
    </div>
  </div>

  <!-- 图片查看器 -->
  <ImageViewer
    :src="viewerSrc"
    :visible="viewerVisible"
    @close="viewerVisible = false"
  />
</template>

<style scoped>
.drawer-overlay-enter-active { transition: opacity 0.25s ease-out; }
.drawer-overlay-leave-active { transition: opacity 0.2s ease-in; }
.drawer-overlay-enter-from,
.drawer-overlay-leave-to { opacity: 0; }

.drawer-enter-active { transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
.drawer-leave-active { transition: transform 0.2s ease-in; }
.drawer-enter-from,
.drawer-leave-to { transform: translateX(100%); }
</style>
