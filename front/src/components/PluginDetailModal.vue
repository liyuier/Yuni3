<script setup>
import { watch } from 'vue'

const props = defineProps({
  plugin: { type: Object, default: null },
  visible: { type: Boolean, default: false },
})

const emit = defineEmits(['close'])

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

function onKeydown(e) {
  if (e.key === 'Escape') emit('close')
}
watch(() => props.visible, (v) => {
  if (v) document.addEventListener('keydown', onKeydown)
  else document.removeEventListener('keydown', onKeydown)
})
</script>

<template>
  <Teleport to="body">
    <div
      v-if="plugin"
      class="fixed inset-0 z-50 flex items-center justify-center p-6"
      :class="visible ? 'pointer-events-auto' : 'pointer-events-none'"
    >
      <!-- 遮罩，点击关闭 -->
      <Transition name="overlay">
        <div v-if="visible" class="absolute inset-0 bg-black/40" @click="emit('close')"></div>
      </Transition>

      <!-- 弹窗卡片 -->
      <Transition name="card">
        <div
          v-if="visible"
          class="relative w-full max-w-lg max-h-[85vh] overflow-y-auto rounded-2xl shadow-xl"
          style="background: rgba(255,255,255,0.95); border: 1px solid rgba(248,165,182,0.3)"
          @click.stop
        >
            <!-- 关闭按钮 -->
            <button
              class="absolute top-4 right-4 w-8 h-8 flex items-center justify-center
                     rounded-full text-pink-300 hover:text-pink-500 hover:bg-pink-50
                     transition-colors text-lg 2xl:text-xl leading-none"
              @click="emit('close')"
            >✕</button>

            <!-- 头部 -->
            <div class="px-6 pt-6 pb-4 border-b border-pink-100">
              <h3 class="font-bold text-lg 2xl:text-xl text-pink-800 mb-2">{{ plugin.name }}</h3>
              <div class="flex items-center gap-1.5">
                <span
                  class="text-xs px-2 py-0.5 rounded-full font-medium"
                  :class="plugin.built_in
                    ? 'bg-pink-100 text-pink-600'
                    : 'bg-gray-100 text-gray-500'"
                >
                  {{ plugin.built_in ? '内置' : '外部' }}
                </span>
                <span
                  class="text-xs px-2 py-0.5 rounded-full font-medium"
                  :class="typeColor[plugin.type] || 'bg-gray-50 text-gray-500'"
                >
                  {{ typeLabel[plugin.type] || plugin.type }}
                </span>
              </div>
            </div>

            <!-- 内容 -->
            <div class="px-6 py-4 space-y-5">
              <div>
                <p class="text-xs 2xl:text-sm font-medium text-pink-500 bg-pink-50 rounded-lg px-2 py-0.5 inline-block mb-1.5">描述</p>
                <p class="text-sm 2xl:text-base text-pink-700 leading-relaxed">
                  {{ plugin.description }}
                </p>
              </div>

              <div v-if="plugin.tips && plugin.tips.length">
                <p class="text-xs 2xl:text-sm font-medium text-pink-500 bg-pink-50 rounded-lg px-2 py-0.5 inline-block mb-1.5">使用提示</p>
                <ul class="space-y-1">
                  <li
                    v-for="(tip, i) in plugin.tips"
                    :key="i"
                    class="text-sm 2xl:text-base text-pink-600 flex items-start gap-1.5"
                  >
                    <span class="text-pink-300 mt-1 shrink-0">▸</span>
                    <span>{{ tip }}</span>
                  </li>
                </ul>
              </div>

              <div class="grid grid-cols-2 gap-3">
                <div>
                  <p class="text-xs 2xl:text-sm font-medium text-pink-500 bg-pink-50 rounded-lg px-2 py-0.5 inline-block mb-0.5">版本</p>
                  <p class="text-sm 2xl:text-base text-pink-700">v{{ plugin.version }}</p>
                </div>
                <div>
                  <p class="text-xs 2xl:text-sm font-medium text-pink-500 bg-pink-50 rounded-lg px-2 py-0.5 inline-block mb-0.5">作者</p>
                  <p class="text-sm 2xl:text-base text-pink-700">{{ plugin.author }}</p>
                </div>
                <div>
                  <p class="text-xs 2xl:text-sm font-medium text-pink-500 bg-pink-50 rounded-lg px-2 py-0.5 inline-block mb-0.5">所属模块</p>
                  <p class="text-sm 2xl:text-base text-pink-700">{{ plugin.module_id }}</p>
                </div>
                <div>
                  <p class="text-xs 2xl:text-sm font-medium text-pink-500 bg-pink-50 rounded-lg px-2 py-0.5 inline-block mb-0.5">插件ID</p>
                  <p class="text-sm 2xl:text-base text-pink-400 font-mono break-all">{{ plugin.full_id }}</p>
                </div>
              </div>
            </div>
          </div>
        </Transition>
      </div>
  </Teleport>
</template>

<style scoped>
/* 遮罩 */
.overlay-enter-active { transition: opacity 0.2s ease-out; }
.overlay-leave-active { transition: opacity 0.2s ease-in; }
.overlay-enter-from,
.overlay-leave-to { opacity: 0; }

/* 卡片 */
.card-enter-active {
  transition: opacity 0.25s ease-out,
              transform 0.3s cubic-bezier(0.34, 1.3, 0.64, 1);
}
.card-leave-active { transition: all 0.2s ease-in; }
.card-enter-from {
  opacity: 0;
  transform: scale(0.9) translateY(10px);
}
.card-leave-to {
  opacity: 0;
  transform: scale(0.95) translateY(4px);
}
</style>
