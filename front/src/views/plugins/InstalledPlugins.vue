<script setup>
import { ref, computed, onMounted } from 'vue'
import { getPluginList } from '../../api/plugin'
import PluginDetailModal from '../../components/PluginDetailModal.vue'

const modules = ref([])
const loading = ref(false)
const error = ref('')

const expandedModules = ref({})
const searchQuery = ref('')

const filteredModules = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return modules.value
  return modules.value.filter(mod => {
    if (mod.module_name?.toLowerCase().includes(q)) return true
    if (mod.module_id?.toLowerCase().includes(q)) return true
    return mod.plugins.some(p => p.name?.toLowerCase().includes(q))
  })
})

const totalPlugins = computed(() =>
  filteredModules.value.reduce((sum, m) => sum + m.plugins.length, 0)
)

onMounted(fetchList)

async function fetchList() {
  loading.value = true
  error.value = ''
  try {
    modules.value = await getPluginList()
  } catch (e) {
    error.value = e.message || '加载失败'
    modules.value = []
  } finally {
    loading.value = false
  }
}

function toggleExpand(moduleId) {
  expandedModules.value[moduleId] = !expandedModules.value[moduleId]
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

const PREVIEW_LIMIT = 2

const detailPlugin = ref(null)
const detailVisible = ref(false)

function openDetail(plugin) {
  detailPlugin.value = plugin
  detailVisible.value = true
}

function closeDetail() {
  detailVisible.value = false
  setTimeout(() => { detailPlugin.value = null }, 200)
}
</script>

<template>
  <div class="flex flex-col min-h-0 h-full">
    <!-- 搜索 + 统计 -->
    <div class="shrink-0 mb-5">
      <div class="relative w-56">
        <span class="absolute left-3 top-1/2 -translate-y-1/2 text-sm text-pink-300">🔍</span>
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索模块名或插件名..."
          class="pl-9 pr-4 py-1.5 text-sm 2xl:text-base rounded-xl border border-pink-200
                 bg-white/80 text-pink-700 placeholder-pink-300 w-full
                 focus:outline-none focus:border-pink-400 focus:bg-white transition-colors"
        />
      </div>
      <div
        v-if="!loading && !error && totalPlugins"
        class="flex items-center gap-4 mt-3 text-xs 2xl:text-sm text-pink-400"
      >
        <span>📦 {{ filteredModules.length }} 个模块</span>
        <span>🧩 {{ totalPlugins }} 个插件</span>
      </div>
    </div>

    <div class="flex-1 overflow-y-auto min-h-0 -mx-3 px-3">
    <!-- 加载态 -->
    <div v-if="loading" class="yuni-card">
      <div class="flex items-center gap-3 text-pink-400 py-4">
        <span class="animate-pulse text-lg">🌸</span>
        <span>正在加载插件列表...</span>
      </div>
    </div>

    <!-- 错误态 -->
    <div v-else-if="error" class="yuni-card">
      <div class="flex items-center justify-between py-2">
        <span class="text-pink-500">⚠ {{ error }}</span>
        <button
          class="text-sm px-4 py-1.5 rounded-lg bg-pink-100 text-pink-600
                 font-medium hover:bg-pink-200 transition-colors"
          @click="fetchList"
        >重试</button>
      </div>
    </div>

    <!-- 空态：无插件 -->
    <div v-else-if="!modules.length" class="yuni-card">
      <div class="text-center py-12">
        <div class="text-4xl mb-3">📦</div>
        <p class="text-pink-400 2xl:text-lg">暂未加载任何插件</p>
        <p class="text-sm 2xl:text-base text-pink-300 mt-1">
          请确认插件 JAR 包已放入 plugins 目录并重启服务
        </p>
      </div>
    </div>

    <!-- 空态：搜索无结果 -->
    <div v-else-if="!filteredModules.length" class="yuni-card">
      <div class="text-center py-12">
        <div class="text-4xl mb-3">🔍</div>
        <p class="text-pink-400 2xl:text-lg">未找到匹配的插件或模块</p>
        <p class="text-sm 2xl:text-base text-pink-300 mt-1">
          试试其他关键词？
        </p>
      </div>
    </div>

    <!-- 模块卡片网格 -->
    <div v-else class="grid gap-5 grid-cols-1 lg:grid-cols-3">
      <div
        v-for="mod in filteredModules"
        :key="mod.module_id"
        class="yuni-card !p-0 overflow-hidden flex flex-col"
      >
        <!-- 模块头部 -->
        <div class="flex items-center gap-2 px-5 py-3 bg-pink-50/80 border-b border-pink-100">
          <span class="text-base">📦</span>
          <h3 class="font-bold text-base 2xl:text-lg text-pink-700 flex-1">
            {{ mod.module_name || mod.module_id }}
          </h3>
          <span class="text-xs text-pink-400">{{ mod.plugins.length }} 插件</span>
        </div>

        <!-- 插件列表 -->
        <div class="flex-1 divide-y divide-pink-50 px-5">
          <div
            v-for="p in mod.plugins"
            :key="p.full_id"
            v-show="expandedModules[mod.module_id] || mod.plugins.indexOf(p) < PREVIEW_LIMIT"
            class="py-3"
          >
            <div class="flex items-center gap-2 mb-1">
              <span class="font-bold text-sm 2xl:text-base text-pink-800 flex-1">{{ p.name }}</span>
              <span
                class="shrink-0 text-xs px-1.5 py-0.5 rounded-full font-medium"
                :class="p.built_in ? 'bg-pink-100 text-pink-600' : 'bg-gray-100 text-gray-500'"
              >{{ p.built_in ? '内置' : '外部' }}</span>
              <span
                class="shrink-0 text-xs px-1.5 py-0.5 rounded-full font-medium"
                :class="typeColor[p.type] || 'bg-gray-50 text-gray-500'"
              >{{ typeLabel[p.type] || p.type }}</span>
              <button
                class="shrink-0 text-xs px-2.5 py-1 rounded-lg bg-pink-100 text-pink-600
                       font-medium hover:bg-pink-200 hover:text-pink-700
                       active:scale-95 transition-all border border-pink-200"
                @click="openDetail(p)"
              >详情 →</button>
            </div>
            <p class="text-xs 2xl:text-sm text-pink-400 leading-relaxed line-clamp-2">
              {{ p.description }}
            </p>
            <div class="flex items-center gap-3 mt-1 text-xs 2xl:text-sm text-pink-300">
              <span>v{{ p.version }}</span>
              <span>{{ p.author }}</span>
            </div>
          </div>
        </div>

        <button
          v-if="mod.plugins.length > PREVIEW_LIMIT"
          class="px-5 py-2.5 text-xs 2xl:text-sm text-pink-500 hover:text-pink-700
                 hover:bg-pink-50 transition-colors font-medium border-t border-pink-50"
          @click="toggleExpand(mod.module_id)"
        >
          {{ expandedModules[mod.module_id] ? '收起' : `展开全部 ${mod.plugins.length} 个插件` }}
        </button>
      </div>
    </div>
    </div>
    <!-- /滚动容器 -->

    <PluginDetailModal
      :plugin="detailPlugin"
      :visible="detailVisible"
      @close="closeDetail"
    />
  </div>
</template>
