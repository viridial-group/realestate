<template>
  <div class="flex items-center gap-1 bg-white rounded-md border border-gray-300 p-1 shadow-sm">
    <button
      @click="setView('list')"
      class="px-3 py-1.5 rounded-md text-sm font-medium transition-colors flex items-center gap-1.5"
      :class="view === 'list'
        ? 'bg-blue-600 text-white shadow-sm'
        : 'text-gray-700 hover:bg-gray-100'"
      title="Vue liste"
    >
      <List class="h-4 w-4" />
      <span class="hidden sm:inline">Liste</span>
    </button>
    <button
      @click="setView('grid')"
      class="px-3 py-1.5 rounded-md text-sm font-medium transition-colors flex items-center gap-1.5"
      :class="view === 'grid'
        ? 'bg-blue-600 text-white shadow-sm'
        : 'text-gray-700 hover:bg-gray-100'"
      title="Vue grille"
    >
      <Grid3x3 class="h-4 w-4" />
      <span class="hidden sm:inline">Grille</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { List, Grid3x3 } from 'lucide-vue-next'

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

