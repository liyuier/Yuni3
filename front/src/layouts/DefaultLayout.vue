<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const navItems = [
  { path: '/', label: '首页', icon: '🌸' },
  { path: '/groups', label: '群组管理', icon: '🐧' },
  { path: '/plugins', label: '插件管理', icon: '🧩' },
  { path: '/database', label: '数据库', icon: '📊' },
  { path: '/settings', label: '系统设置', icon: '⚙' },
]
</script>

<template>
  <div class="flex min-h-screen">
    <!-- Sidebar -->
    <aside class="w-52 shrink-0 flex flex-col border-r" style="background:rgba(255,255,255,0.7);border-color:rgba(248,165,182,0.3);backdrop-filter:blur(8px)">
      <div class="flex items-center gap-2 px-5 pt-6 pb-8">
        <div class="w-9 h-9 rounded-full bg-pink-200 flex items-center justify-center text-lg">🌸</div>
        <span class="text-lg font-medium text-pink-800">Yuni</span>
      </div>
      <nav class="flex-1 px-3 space-y-1">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm transition-all"
          :class="item.path === '/'
            ? route.path === '/'
              ? 'bg-pink-100 text-pink-700 font-medium shadow-sm'
              : 'text-pink-600 hover:bg-pink-50'
            : route.path.startsWith(item.path)
              ? 'bg-pink-100 text-pink-700 font-medium shadow-sm'
              : 'text-pink-600 hover:bg-pink-50'"
        >
          <span>{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
      <div class="px-5 pb-6">
        <div class="text-xs text-pink-300 flex items-center gap-1.5">
          <span class="w-1.5 h-1.5 rounded-full bg-green-400"></span>
          运行中
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 p-8 flex flex-col overflow-y-auto" style="height: 100vh;">
      <router-view />
    </main>
  </div>
</template>
