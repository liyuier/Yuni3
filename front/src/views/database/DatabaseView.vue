<script setup>
import { ref, onMounted } from 'vue'
import { getTableList, queryTable } from '../../api/database'

const tables = ref([])
const activeTable = ref(null)
const columns = ref([])
const rows = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 50
const tableLoading = ref(false)

onMounted(async () => {
  try { tables.value = await getTableList() } catch (e) { tables.value = [] }
})

async function selectTable(table) {
  activeTable.value = table
  page.value = 1
  await loadData()
}

async function loadData() {
  if (!activeTable.value) return
  tableLoading.value = true
  try {
    const resp = await queryTable(activeTable.value.name, page.value, pageSize)
    columns.value = resp.columns || []
    rows.value = resp.rows || []
    total.value = resp.total || 0
  } catch (e) {
    columns.value = []
    rows.value = []
  } finally {
    tableLoading.value = false
  }
}

async function changePage(p) {
  page.value = p
  await loadData()
}

const totalPages = () => Math.max(1, Math.ceil(total.value / pageSize))

function cellValue(val) {
  if (val === null || val === undefined) return 'NULL'
  if (typeof val === 'object') return JSON.stringify(val)
  return String(val)
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

// ── 点击格子展开卡片 ──
const tooltip = ref({ show: false, text: '', x: 0, y: 0, col: '', row: -1 })
function toggleTooltip(e, val, col, rowIdx) {
  const text = cellValue(val)
  if (tooltip.value.show && tooltip.value.col === col && tooltip.value.row === rowIdx) {
    tooltip.value.show = false
    return
  }
  tooltip.value = { show: true, text, x: e.clientX, y: e.clientY, col, row: rowIdx }
}

async function copyTooltipText() {
  try {
    await navigator.clipboard.writeText(tooltip.value.text)
    tooltip.value.show = false
  } catch { /* ignore */ }
}
function closeTooltip() {
  tooltip.value.show = false
}

function tooltipStyle() {
  const x = tooltip.value.x
  const y = tooltip.value.y
  const w = window.innerWidth
  const h = window.innerHeight
  const rightHalf = x > w / 2
  const bottomHalf = y > h / 2

  return {
    left: rightHalf ? '' : (x + 12) + 'px',
    right: rightHalf ? (w - x + 12) + 'px' : '',
    top: bottomHalf ? '' : (y + 8) + 'px',
    bottom: bottomHalf ? (h - y + 8) + 'px' : '',
  }
}

// ── 页码跳转 ──
const jumpPage = ref('')
function goPage(p) {
  const n = Number(p)
  if (n >= 1 && n <= totalPages()) changePage(n)
  jumpPage.value = ''
}
</script>

<template>
  <div class="flex gap-0 flex-1 min-h-0 min-h-[400px]" @mousemove="onDrag" @mouseup="stopDrag" @click="closeTooltip">
    <!-- ====== 左侧表目录 ====== -->
    <div
      class="shrink-0 flex flex-col rounded-l-2xl overflow-hidden relative"
      :style="{ width: panelPct + '%', background: 'rgba(255,255,255,0.7)', border: '1px solid rgba(248,165,182,0.3)', borderRight: 'none' }"
    >
      <!-- 拖拽手柄 -->
      <div
        class="absolute top-0 right-0 w-2 h-full cursor-col-resize hover:bg-pink-300/50 transition-colors z-10"
        style="margin-right: -2px;"
        @mousedown.prevent="startDrag"
      ></div>
      <div class="px-4 py-4 border-b border-pink-100">
        <h3 class="font-bold text-pink-700">📊 数据表</h3>
      </div>
      <div class="flex-1 overflow-y-auto px-2 py-2">
        <div
          v-for="t in tables"
          :key="t.name"
          class="flex items-center justify-between px-3 py-2 rounded-xl cursor-pointer transition-all mb-0.5 text-sm"
          :class="activeTable?.name === t.name
            ? 'bg-pink-100/80 text-pink-700'
            : 'text-pink-600 hover:bg-pink-50/60'"
          @click="selectTable(t)"
        >
          <span class="truncate flex-1">{{ t.name }}</span>
          <span class="text-xs text-pink-400 ml-2 shrink-0">{{ t.row_count }}</span>
        </div>
      </div>
    </div>

    <!-- ====== 右侧数据展示 ====== -->
    <div
      class="flex-1 flex flex-col rounded-r-2xl overflow-hidden min-w-0"
      style="background: rgba(255,255,255,0.6); border: 1px solid rgba(248,165,182,0.3);"
    >
      <div
        class="flex items-center justify-between px-5 py-3 shrink-0"
        style="background: rgba(255,255,255,0.5); border-bottom: 1px solid rgba(248,165,182,0.2);"
      >
        <h3 class="font-bold text-pink-700 font-mono truncate">
          {{ activeTable ? activeTable.name : '选择一张表' }}
        </h3>
        <span v-if="activeTable" class="text-xs text-pink-400">{{ total }} 行</span>
      </div>

      <div class="flex-1 overflow-auto">
        <div v-if="tableLoading" class="text-center py-8 text-pink-400 text-sm">
          <span class="animate-pulse">🌸</span> 加载中...
        </div>
        <div v-else-if="!activeTable" class="text-center py-16">
          <div class="text-4xl mb-3">📊</div>
          <p class="text-pink-400">选择左侧数据表查看内容</p>
        </div>
        <div v-else-if="!rows.length" class="text-center py-12">
          <p class="text-pink-400">表中暂无数据</p>
        </div>
        <table v-else class="w-full text-xs 2xl:text-sm">
          <thead>
            <tr class="sticky top-0" style="background: rgba(254,242,245,0.95);">
              <th class="text-left px-3 py-2 text-pink-500 font-medium border-b border-pink-100 whitespace-nowrap"
                v-for="col in columns" :key="col">{{ col }}</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(row, i) in rows" :key="i"
              class="hover:bg-pink-50/40 transition-colors border-b border-pink-50"
            >
              <td
                v-for="col in columns" :key="col"
                class="px-3 py-1.5 whitespace-nowrap max-w-64 overflow-hidden text-ellipsis cursor-pointer transition-colors"
                :class="tooltip.show && tooltip.col === col && tooltip.row === i
                  ? 'text-pink-600 bg-pink-100/80'
                  : 'text-pink-700'"
                @click.stop="toggleTooltip($event, row[col], col, i)"
              >{{ cellValue(row[col]) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div
        v-if="activeTable && totalPages() > 1"
        class="flex items-center justify-center gap-2 px-5 py-3 border-t border-pink-50 shrink-0"
      >
        <button class="text-xs px-2 py-1 rounded-lg text-pink-500 hover:bg-pink-50 disabled:opacity-30"
          :disabled="page <= 1" @click="changePage(1)">首页</button>
        <button class="text-xs px-3 py-1 rounded-lg text-pink-500 hover:bg-pink-50 disabled:opacity-30"
          :disabled="page <= 1" @click="changePage(page - 1)">上一页</button>
        <span class="text-xs text-pink-400">{{ page }} / {{ totalPages() }}</span>
        <button class="text-xs px-3 py-1 rounded-lg text-pink-500 hover:bg-pink-50 disabled:opacity-30"
          :disabled="page >= totalPages()" @click="changePage(page + 1)">下一页</button>
        <button class="text-xs px-2 py-1 rounded-lg text-pink-500 hover:bg-pink-50 disabled:opacity-30"
          :disabled="page >= totalPages()" @click="changePage(totalPages())">末页</button>
        <form class="flex items-center gap-1 ml-3" @submit.prevent="goPage(jumpPage)">
          <span class="text-xs text-pink-400">跳至</span>
          <input
            v-model="jumpPage"
            type="number" :min="1" :max="totalPages()"
            class="w-14 px-2 py-1 text-xs rounded-lg border border-pink-200
                   bg-white/80 text-pink-700 text-center
                   focus:outline-none focus:border-pink-400"
          />
          <span class="text-xs text-pink-400">页</span>
        </form>
      </div>
    </div>
  </div>

  <!-- 值卡片：四象限对位弹出 -->
  <Teleport to="body">
    <div
      v-if="tooltip.show"
      class="fixed z-50 max-w-sm rounded-xl shadow-lg text-xs 2xl:text-sm"
      :style="{
        ...tooltipStyle(),
        background: 'rgba(255,255,255,0.97)',
        border: '1px solid rgba(248,165,182,0.3)',
        color: '#4a3448',
      }"
      @click.stop
    >
      <div class="px-3 py-2 whitespace-pre-wrap break-all leading-relaxed max-h-64 overflow-auto">{{ tooltip.text }}</div>
      <div class="flex justify-end border-t border-pink-50 px-2 py-1">
        <button
          class="text-xs text-pink-500 hover:text-pink-700 hover:bg-pink-50
                 px-2 py-0.5 rounded-lg transition-colors"
          @click="copyTooltipText"
        >📋 复制</button>
      </div>
    </div>
    <!-- 透明遮罩层，点击关闭 -->
    <div
      v-if="tooltip.show"
      class="fixed inset-0 z-40"
      @click="closeTooltip"
    ></div>
  </Teleport>
</template>
