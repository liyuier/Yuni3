<script setup>
import { ref } from 'vue'
import { searchKeys, getKeyValue } from '../../api/redis'

function formatVal(v) {
  if (v === null || v === undefined) return ''
  if (typeof v === 'object') {
    try { return JSON.stringify(v, null, 2) } catch { return String(v) }
  }
  if (typeof v === 'string' && (v.startsWith('{') || v.startsWith('['))) {
    try { return JSON.stringify(JSON.parse(v), null, 2) } catch { return v }
  }
  return v
}

// ── 点击格子展开卡片 ──
const tooltip = ref({ show: false, text: '', x: 0, y: 0 })
function showDetail(e, val) {
  const text = formatVal(val)
  if (tooltip.value.show && tooltip.value.text === text) {
    tooltip.value.show = false
    return
  }
  tooltip.value = { show: true, text, x: e.clientX, y: e.clientY }
}
function closeTooltip() { tooltip.value.show = false }

async function copyTooltip() {
  try { await navigator.clipboard.writeText(tooltip.value.text); tooltip.value.show = false } catch {}
}

function tooltipStyle() {
  const x = tooltip.value.x; const y = tooltip.value.y
  const w = window.innerWidth; const h = window.innerHeight
  return {
    left: x > w / 2 ? '' : (x + 12) + 'px',
    right: x > w / 2 ? (w - x + 12) + 'px' : '',
    top: y > h / 2 ? '' : (y + 8) + 'px',
    bottom: y > h / 2 ? (h - y + 8) + 'px' : '',
  }
}

const pattern = ref('')
const keys = ref([])
const loading = ref(false)
const selectedKey = ref(null)
const keyValue = ref(null)
const valueLoading = ref(false)

async function doSearch() {
  loading.value = true
  selectedKey.value = null
  keyValue.value = null
  try { keys.value = await searchKeys(pattern.value) } catch (e) { keys.value = [] } finally { loading.value = false }
}

async function selectKey(key) {
  selectedKey.value = key
  valueLoading.value = true
  try { keyValue.value = await getKeyValue(key.key) } catch (e) { keyValue.value = null } finally { valueLoading.value = false }
}

// ── 左侧面板拖拽（百分比） ──
const panelPct = ref(14)
const dragState = ref({ active: false, startX: 0, startPct: 0, containerW: 0 })
function startDrag(e) {
  const container = e.target.closest('.flex.gap-0')
  dragState.value = { active: true, startX: e.clientX, startPct: panelPct.value, containerW: container?.offsetWidth || window.innerWidth }
  document.body.style.userSelect = 'none'
}
function onDrag(e) {
  if (!dragState.value.active) return
  const deltaPct = ((e.clientX - dragState.value.startX) / dragState.value.containerW) * 100
  panelPct.value = Math.max(8, Math.min(40, dragState.value.startPct + deltaPct))
}
function stopDrag() { dragState.value.active = false; document.body.style.userSelect = '' }

// 页面加载时搜全量
doSearch()
</script>

<template>
  <div class="flex gap-0 flex-1 min-h-0 min-h-[400px]" @mousemove="onDrag" @mouseup="stopDrag" @click="closeTooltip">
    <!-- ====== 左侧键列表 ====== -->
    <div
      class="shrink-0 flex flex-col rounded-l-2xl overflow-hidden relative"
      :style="{ width: panelPct + '%', background: 'rgba(255,255,255,0.7)', border: '1px solid rgba(248,165,182,0.3)', borderRight: 'none' }"
    >
      <div class="absolute top-0 right-0 w-2 h-full cursor-col-resize hover:bg-pink-300/50 transition-colors z-10" style="margin-right: -2px;" @mousedown.prevent="startDrag"></div>
      <div class="px-4 py-3 border-b border-pink-100">
        <form @submit.prevent="doSearch" class="flex gap-2">
          <input
            v-model="pattern"
            type="text" placeholder="搜索 key..."
            class="flex-1 px-3 py-1.5 text-sm rounded-lg border border-pink-200
                   bg-white/80 text-pink-700 placeholder-pink-300
                   focus:outline-none focus:border-pink-400 transition-colors"
          />
          <button
            type="submit"
            class="shrink-0 text-sm px-3 py-1.5 rounded-lg bg-pink-100 text-pink-600
                   font-medium hover:bg-pink-200 transition-colors"
          >搜索</button>
        </form>
      </div>
      <div class="flex-1 overflow-y-auto px-2 py-2">
        <div v-if="loading" class="text-center py-8 text-pink-400 text-sm">
          <span class="animate-pulse">🌸</span> 搜索中...
        </div>
        <div v-else-if="!keys.length" class="text-center py-8 text-pink-400 text-sm">
          无匹配键
        </div>
        <div
          v-for="k in keys" :key="k.key"
          class="px-3 py-2 rounded-xl cursor-pointer transition-all mb-0.5"
          :class="selectedKey?.key === k.key
            ? 'bg-pink-100/80 text-pink-700'
            : 'text-pink-600 hover:bg-pink-50/60'"
          @click="selectKey(k)"
        >
          <div class="text-sm truncate font-mono">{{ k.key }}</div>
          <div class="flex items-center gap-2 mt-0.5">
            <span class="text-xs px-1 py-0 rounded font-medium"
              :class="{
                'bg-blue-50 text-blue-600': k.type === 'string',
                'bg-purple-50 text-purple-600': k.type === 'hash',
                'bg-emerald-50 text-emerald-600': k.type === 'list',
                'bg-amber-50 text-amber-600': k.type === 'set',
                'bg-gray-100 text-gray-500': !['string','hash','list','set'].includes(k.type),
              }"
            >{{ k.type }}</span>
            <span class="text-xs text-pink-300">{{ k.ttl === -1 ? '永不过期' : k.ttl === -2 ? '' : k.ttl + 's' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ====== 右侧值展示 ====== -->
    <div
      class="flex-1 flex flex-col rounded-r-2xl overflow-hidden min-w-0"
      style="background: rgba(255,255,255,0.6); border: 1px solid rgba(248,165,182,0.3);"
    >
      <div
        class="flex items-center px-5 py-3 shrink-0 border-b border-pink-50"
        style="background: rgba(255,255,255,0.5);"
      >
        <h3 class="font-bold text-pink-700 font-mono truncate">
          {{ selectedKey ? selectedKey.key : '选择左侧键查看值' }}
        </h3>
      </div>

      <div class="flex-1 overflow-auto p-5">
        <div v-if="valueLoading" class="text-center py-8 text-pink-400 text-sm">
          <span class="animate-pulse">🌸</span> 加载中...
        </div>
        <div v-else-if="!selectedKey" class="text-center py-16">
          <div class="text-4xl mb-3">🔑</div>
          <p class="text-pink-400">选择左侧 Key 查看值</p>
        </div>
        <div v-else-if="keyValue" class="space-y-4">
          <div class="flex items-center gap-4 text-xs text-pink-400">
            <span>类型: <b class="text-pink-600">{{ keyValue.type }}</b></span>
            <span>TTL: <b class="text-pink-600">{{ keyValue.ttl === -1 ? '永不过期' : keyValue.ttl === -2 ? '不存在' : keyValue.ttl + 's' }}</b></span>
          </div>

          <!-- 值展示 -->
          <div v-if="keyValue.type === 'hash'" class="yuni-card !bg-white/90 !p-0 overflow-hidden">
            <!-- 表头 -->
            <div class="flex text-xs 2xl:text-sm" style="background: rgba(254,242,245,0.95);">
              <div class="px-3 py-2 text-pink-500 font-medium border-b border-pink-100 shrink-0" style="width: 30%;">Field</div>
              <div class="px-3 py-2 text-pink-500 font-medium border-b border-pink-100 flex-1">Value</div>
            </div>
            <!-- 表体 -->
            <div class="divide-y divide-pink-50">
              <div
                v-for="(val, hk) in keyValue.hash_value" :key="hk"
                class="flex hover:bg-pink-50/40 transition-colors text-xs 2xl:text-sm"
              >
                <div
                  class="px-3 py-1.5 text-pink-500 font-mono overflow-hidden text-ellipsis shrink-0 cursor-pointer"
                  style="width: 30%;"
                  @click.stop="showDetail($event, hk)"
                >{{ hk }}</div>
                <div
                  class="px-3 py-1.5 text-pink-700 font-mono overflow-hidden text-ellipsis flex-1 cursor-pointer"
                  :class="tooltip.show && tooltip.text === formatVal(val) ? 'text-pink-600 bg-pink-100/80' : ''"
                  @click.stop="showDetail($event, val)"
                >{{ formatVal(val) }}</div>
              </div>
            </div>
          </div>
          <div v-else class="yuni-card !bg-white/90 max-h-96 overflow-auto">
            <pre class="text-sm text-pink-700 whitespace-pre-wrap break-all font-mono">{{ formatVal(keyValue.string_value) }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 值卡片：四象限对位弹出 -->
  <Teleport to="body">
    <div
      v-if="tooltip.show"
      class="fixed z-50 max-w-sm rounded-xl shadow-lg text-xs 2xl:text-sm"
      :style="{ ...tooltipStyle(), background: 'rgba(255,255,255,0.97)', border: '1px solid rgba(248,165,182,0.3)', color: '#4a3448' }"
      @click.stop
    >
      <div class="px-3 py-2 whitespace-pre-wrap break-all leading-relaxed max-h-64 overflow-auto">{{ tooltip.text }}</div>
      <div class="flex justify-end border-t border-pink-50 px-2 py-1">
        <button class="text-xs text-pink-500 hover:text-pink-700 hover:bg-pink-50 px-2 py-0.5 rounded-lg transition-colors" @click="copyTooltip">📋 复制</button>
      </div>
    </div>
    <div v-if="tooltip.show" class="fixed inset-0 z-40" @click="closeTooltip"></div>
  </Teleport>
</template>
