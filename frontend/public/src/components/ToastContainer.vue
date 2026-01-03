<template>
  <div class="fixed top-4 right-4 z-50 space-y-2">
    <TransitionGroup name="toast" tag="div">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="min-w-[300px] max-w-md bg-white rounded-lg shadow-lg border p-4 flex items-start gap-3"
        :class="getToastClasses(toast.type)"
      >
        <div class="flex-shrink-0">
          <span class="text-xl">{{ getToastIcon(toast.type) }}</span>
        </div>
        <div class="flex-1">
          <p class="text-sm font-medium" :class="getToastTextClasses(toast.type)">
            {{ toast.message }}
          </p>
        </div>
        <button
          @click="removeToast(toast.id)"
          class="flex-shrink-0 text-gray-400 hover:text-gray-600 transition-colors"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
import { useToast, type ToastType } from '@/composables/useToast'

const { toasts, removeToast } = useToast()

function getToastClasses(type: ToastType): string {
  switch (type) {
    case 'success':
      return 'border-green-200 bg-green-50'
    case 'error':
      return 'border-red-200 bg-red-50'
    case 'warning':
      return 'border-yellow-200 bg-yellow-50'
    case 'info':
    default:
      return 'border-blue-200 bg-blue-50'
  }
}

function getToastTextClasses(type: ToastType): string {
  switch (type) {
    case 'success':
      return 'text-green-800'
    case 'error':
      return 'text-red-800'
    case 'warning':
      return 'text-yellow-800'
    case 'info':
    default:
      return 'text-blue-800'
  }
}

function getToastIcon(type: ToastType): string {
  switch (type) {
    case 'success':
      return '✅'
    case 'error':
      return '❌'
    case 'warning':
      return '⚠️'
    case 'info':
    default:
      return 'ℹ️'
  }
}
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.toast-move {
  transition: transform 0.3s ease;
}
</style>

