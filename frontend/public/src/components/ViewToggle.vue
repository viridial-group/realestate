<template>
  <div class="flex items-center gap-2 bg-white rounded-lg border border-gray-200 p-1">
    <button
      @click="setView('list')"
      class="px-3 py-1.5 rounded-md text-sm font-medium transition-colors"
      :class="view === 'list'
        ? 'bg-blue-600 text-white'
        : 'text-gray-600 hover:bg-gray-100'"
      title="Vue liste"
    >
      <span class="flex items-center gap-1">
        <span>☰</span>
        <span class="hidden sm:inline">Liste</span>
      </span>
    </button>
    <button
      @click="setView('grid')"
      class="px-3 py-1.5 rounded-md text-sm font-medium transition-colors"
      :class="view === 'grid'
        ? 'bg-blue-600 text-white'
        : 'text-gray-600 hover:bg-gray-100'"
      title="Vue grille"
    >
      <span class="flex items-center gap-1">
        <span>⊞</span>
        <span class="hidden sm:inline">Grille</span>
      </span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue?: 'list' | 'grid'
}>()

const emit = defineEmits<{
  'update:modelValue': [value: 'list' | 'grid']
}>()

const view = ref<'list' | 'grid'>(props.modelValue || 'list')

watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    view.value = newValue
  }
})

function setView(newView: 'list' | 'grid') {
  view.value = newView
  emit('update:modelValue', newView)
}
</script>

