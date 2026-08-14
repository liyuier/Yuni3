<script setup>
defineProps({
  segments: { type: Array, default: () => [] },
  plainText: { type: Boolean, default: true },
  message: { type: String, default: '' },
})

defineEmits(['imgClick'])

/**
 * OneBot / Yuni 消息段 → 展示文本。
 * 字段选择参考 Stapxs-QQ-Lite-2.0 MsgBody.vue 的渲染逻辑。
 */
function segText(seg) {
  const d = seg.data || {}
  switch (seg.type) {
    // ---------- 文本 ----------
    case 'text':
      return d.text || ''
    // ---------- 图片 ----------
    case 'image':
      // 闪照
      if (d.type === 'flash') return '[闪照]'
      // 商城表情（file == 'marketface'）→ 归为表情
      if (d.file === 'marketface') return '[商城表情]'
      // 普通图片 → 由模板 img 标签渲染
      return ''
    // ---------- QQ 表情 ----------
    case 'face': {
      const id = Number(d.id)
      if (id >= 5000) {
        try { return String.fromCodePoint(id) } catch { return '[表情#' + id + ']' }
      }
      // id < 5000 → APNG 图片由模板 img 渲染
      return ''
    }
    // ---------- 商城表情 ----------
    case 'mface':
      return '[商城表情]'
    // ---------- @ ----------
    case 'at':
      return d.qq === 'all' ? '@全体成员' : ('@' + (d.qq || '某人'))
    // ---------- 回复 ----------
    case 'reply':
      return '[回复]'
    // ---------- 合并转发 ----------
    case 'forward':
      return '[合并转发消息]'
    // ---------- 文件 ----------
    case 'file': {
      const name = d.name || d.file_name || d.file || ''
      const size = d.size || d.file_size
      const sizeStr = size ? ' ' + formatFileSize(size) : ''
      return '[文件: ' + name + sizeStr + ']'
    }
    // ---------- 视频 ----------
    case 'video':
      return '[视频]'
    // ---------- 语音 ----------
    case 'record':
      return '[语音]'
    // ---------- 戳一戳 ----------
    case 'poke':
      return '[戳了戳]'
    // ---------- 骰子 / 猜拳 / 抖动 ----------
    case 'dice':
      return '[骰子]'
    case 'rps':
      return '[猜拳]'
    case 'shake':
      return '[窗口抖动]'
    // ---------- 分享 ----------
    case 'share':
      return '[链接: ' + (d.title || d.url || '') + ']'
    // ---------- 位置 ----------
    case 'location':
      return '[位置: ' + (d.title || d.address || '') + ']'
    // ---------- 音乐 ----------
    case 'music':
      return '[音乐: ' + (d.title || '') + ']'
    // ---------- 推荐联系人 ----------
    case 'contact':
      return '[推荐联系人]'
    // ---------- JSON / XML 卡片 ----------
    case 'json':
      return '[卡片消息]'
    case 'xml':
      return '[XML 消息]'
    // ---------- Markdown ----------
    case 'markdown':
      return '[Markdown]'
    // ---------- 回退 ----------
    default:
      return '[' + (seg.type || '?') + ']'
  }
}

/** 段是否可渲染为图片 */
function isImageSeg(seg) {
  if (seg.type !== 'image') return false
  const d = seg.data || {}
  if (d.type === 'flash') return false
  if (d.file === 'marketface') return false
  return !!d.url
}

/** QQ 表情 APNG URL（与 Stapxs 同源 CDN） */
function faceImgUrl(id) {
  return `https://lib.stapxs.cn/download/stapxs-qq-lite/qq_emoji/${id}/apng/${id}.png`
}

/** 获取图片 URL */
function imgUrl(seg) {
  return (seg.data || {}).url || ''
}

/** 图片是否为表情子类型（subType=1 热图, 7 表情） */
/** QQ 表情是否可渲染为 APNG 图片（id < 5000） */
function isQqFace(seg) {
  if (seg.type !== 'face') return false
  const id = Number((seg.data || {}).id)
  return !isNaN(id) && id < 5000
}

function isFaceImage(seg) {
  const d = seg.data || {}
  const st = d.subType ?? d.sub_type
  return st === 1 || st === 7 || d.asface === true
}

function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0B'
  if (bytes < 1024) return bytes + 'B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(0) + 'K'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(0) + 'M'
  return (bytes / (1024 * 1024 * 1024)).toFixed(1) + 'G'
}
</script>

<template>
  <!-- 纯文本：直接显示 -->
  <p v-if="plainText" class="whitespace-pre-wrap break-words">{{ message }}</p>

  <!-- 混合消息：逐段渲染 -->
  <div v-else class="space-y-1">
    <template v-for="(seg, i) in segments" :key="i">
      <!-- 图片段 -->
      <img
        v-if="isImageSeg(seg)"
        :src="imgUrl(seg)"
        :class="[
          'max-w-48 max-h-48 rounded-lg cursor-pointer hover:opacity-90 transition-opacity',
          isFaceImage(seg) ? 'max-h-28' : ''
        ]"
        :alt="(seg.data || {}).summary || '图片'"
        referrerpolicy="no-referrer"
        loading="lazy"
        @click="$emit('imgClick', imgUrl(seg))"
      />
      <!-- QQ 表情（小黄脸 APNG） -->
      <img
        v-else-if="isQqFace(seg)"
        :src="faceImgUrl((seg.data || {}).id)"
        class="w-6 h-6 inline-block align-middle"
        :alt="'表情#' + (seg.data || {}).id"
        loading="lazy"
      />
      <!-- 文本段 -->
      <span v-else class="whitespace-pre-wrap break-words">{{ segText(seg) }}</span>
    </template>
  </div>
</template>
