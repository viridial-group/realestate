<template>
  <div class="text-center py-16 px-4">
    <div class="max-w-md mx-auto">
      <div class="mb-6">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gray-100 mb-4">
          <component :is="iconComponent" class="w-8 h-8 text-gray-400" />
        </div>
      </div>
      <h3 class="text-xl font-medium text-gray-900 mb-2">
        {{ title }}
      </h3>
      <p class="text-gray-600 mb-8">
        {{ description }}
      </p>
      <div v-if="showAction" class="flex flex-col sm:flex-row gap-3 justify-center">
        <button
          v-if="actionLabel"
          @click="$emit('action')"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm font-medium shadow-sm"
        >
          {{ actionLabel }}
        </button>
        <button
          v-if="secondaryActionLabel"
          @click="$emit('secondary-action')"
          class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors text-sm font-medium shadow-sm"
        >
          {{ secondaryActionLabel }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Search, Inbox, AlertCircle, Info } from 'lucide-vue-next'

const props = defineProps<{
  title: string
  description: string
  icon?: 'search' | 'empty' | 'error' | 'info'
  actionLabel?: string
  secondaryActionLabel?: string
  showAction?: boolean
}>()

defineEmits<{
  action: []
  'secondary-action': []
}>()

const iconComponent = computed(() => {
  switch (props.icon) {
    case 'search':
      return Search
    case 'error':
      return AlertCircle
    case 'info':
      return Info
    case 'empty':
    default:
      return Inbox
  }
})
</script>

