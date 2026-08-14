<script setup>
import { watch } from 'vue'

const props = defineProps({
  src: { type: String, default: null },
  visible: { type: Boolean, default: false },
})

const emit = defineEmits(['close'])

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
    <Transition name="viewer">
      <div
        v-if="visible && src"
        class="fixed inset-0 z-[60] flex items-center justify-center p-8 bg-black/70"
        @click.self="emit('close')"
      >
        <button
          class="absolute top-4 right-4 w-10 h-10 flex items-center justify-center
                 rounded-full text-white/70 hover:text-white hover:bg-white/10
                 transition-colors text-xl"
          @click="emit('close')"
        >✕</button>
        <img
          :src="src"
          class="max-w-full max-h-full object-contain rounded-lg"
          referrerpolicy="no-referrer"
        />
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.viewer-enter-active { transition: opacity 0.2s ease-out; }
.viewer-leave-active { transition: opacity 0.15s ease-in; }
.viewer-enter-from,
.viewer-leave-to { opacity: 0; }
</style>
