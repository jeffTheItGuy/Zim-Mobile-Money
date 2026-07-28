<template>
  <div class="toast-container">
    <transition-group name="toast">
      <div v-for="t in toasts" :key="t.id" :class="['toast', t.type]">
        {{ t.message }}
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
const toasts = ref([])
let id = 0

function push(message, type = 'success') {
  const i = ++id
  toasts.value.push({ id: i, message, type })
  setTimeout(() => { toasts.value = toasts.value.filter(t => t.id !== i) }, 3500)
}

function handler(e) {
  push(e.detail.message, e.detail.type || 'success')
}

onMounted(() => window.addEventListener('toast', handler))
onUnmounted(() => window.removeEventListener('toast', handler))

defineExpose({ push })
</script>

<style scoped>
.toast-container { position: fixed; top: 1rem; right: 1rem; z-index: 9999; display: flex; flex-direction: column; gap: 0.5rem; }
.toast { padding: 0.75rem 1rem; border-radius: 6px; color: #fff; font-weight: 600; box-shadow: 0 4px 6px rgba(0,0,0,0.1); min-width: 220px; }
.toast.success { background: #059669; }
.toast.error { background: #dc2626; }
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from { transform: translateX(100%); opacity: 0; }
.toast-leave-to { transform: translateX(100%); opacity: 0; }
</style>
