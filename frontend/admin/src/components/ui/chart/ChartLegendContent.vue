<script setup lang="ts">
import { inject, computed } from 'vue'
import type { ChartConfig } from './interface'

withDefaults(defineProps<{
  nameKey?: string
}>(), {
  nameKey: undefined
})

const chartConfig = inject<{ value: ChartConfig }>('chartConfig', { value: {} })

const items = computed(() => {
  return Object.entries(chartConfig.value).map(([key, config]) => ({
    key,
    label: config.label || key,
    color: config.theme?.light || config.color || 'hsl(var(--chart-1))'
  }))
})
</script>

<template>
  <div class="flex items-center justify-center gap-4">
    <div
      v-for="item in items"
      :key="item.key"
      class="flex items-center gap-2"
    >
      <span
        class="flex h-2.5 w-2.5 rounded-full"
        :style="{ backgroundColor: item.color }"
      />
      <span class="text-sm text-muted-foreground">
        {{ item.label }}
      </span>
    </div>
  </div>
</template>

