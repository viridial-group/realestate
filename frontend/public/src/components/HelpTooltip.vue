<template>
  <div class="relative inline-block">
    <button
      @click="showTooltip = !showTooltip"
      @mouseenter="showTooltip = true"
      @mouseleave="showTooltip = false"
      class="inline-flex items-center justify-center w-5 h-5 rounded-full bg-gray-200 hover:bg-gray-300 text-gray-600 hover:text-gray-800 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500"
      :aria-label="title"
    >
      <HelpCircle class="w-3.5 h-3.5" />
    </button>

    <Transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0 translate-y-1"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 translate-y-1"
    >
      <div
        v-if="showTooltip"
        :class="[
          'absolute z-50 w-64 p-3 bg-gray-900 text-white text-sm rounded-lg shadow-lg',
          position === 'top' ? 'bottom-full mb-2' : '',
          position === 'bottom' ? 'top-full mt-2' : '',
          position === 'left' ? 'right-full mr-2 top-0' : '',
          position === 'right' ? 'left-full ml-2 top-0' : ''
        ]"
        @mouseenter="showTooltip = true"
        @mouseleave="showTooltip = false"
      >
        <div class="font-semibold mb-1">{{ title }}</div>
        <div class="text-gray-300">{{ content }}</div>
        <div
          :class="[
            'absolute w-0 h-0 border-4',
            position === 'top' ? 'top-full left-4 border-t-gray-900 border-r-transparent border-b-transparent border-l-transparent' : '',
            position === 'bottom' ? 'bottom-full left-4 border-b-gray-900 border-r-transparent border-t-transparent border-l-transparent' : '',
            position === 'left' ? 'left-full top-2 border-l-gray-900 border-r-transparent border-t-transparent border-b-transparent' : '',
            position === 'right' ? 'right-full top-2 border-r-gray-900 border-l-transparent border-t-transparent border-b-transparent' : ''
          ]"
        />
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { HelpCircle } from 'lucide-vue-next'

withDefaults(defineProps<{
  title: string
  content: string
  position?: 'top' | 'bottom' | 'left' | 'right'
}>(), {
  position: 'top'
})

const showTooltip = ref(false)
</script>

