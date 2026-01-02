<script setup lang="ts">
import { inject } from 'vue'
import type { ChartConfig } from './interface'

withDefaults(defineProps<{
  label?: string
  payload?: Array<{
    name: string
    value: any
    color?: string
  }>
  labelKey?: string
  nameKey?: string
  hideLabel?: boolean
  hideIndicator?: boolean
  indicator?: 'dot' | 'line' | 'dashed'
}>(), {
  hideLabel: false,
  hideIndicator: false,
  indicator: 'dot'
})

const props = defineProps<{
  label?: string
  payload?: Array<{
    name: string
    value: any
    color?: string
  }>
  labelKey?: string
  nameKey?: string
  hideLabel?: boolean
  hideIndicator?: boolean
  indicator?: 'dot' | 'line' | 'dashed'
}>()

const chartConfig = inject<{ value: ChartConfig }>('chartConfig', { value: {} })

const getColor = (name: string) => {
  const config = chartConfig.value[name]
  if (!config) return 'hsl(var(--chart-1))'
  if (config.theme) {
    // TODO: Support dark mode detection
    return config.theme.light
  }
  return config.color || 'hsl(var(--chart-1))'
}

const getLabel = (name: string) => {
  const config = chartConfig.value[name]
  return config?.label || name
}
</script>

<template>
  <div class="rounded-lg border bg-background p-2 shadow-sm">
    <div v-if="!hideLabel && label" class="mb-2 grid grid-cols-2 gap-2">
      <div class="flex flex-col">
        <span class="text-[0.70rem] uppercase text-muted-foreground">
          {{ label }}
        </span>
      </div>
    </div>
    <div class="grid gap-2">
      <div
        v-for="(item, index) in payload"
        :key="index"
        class="flex items-center gap-2"
      >
        <span
          v-if="!hideIndicator"
          class="flex h-2.5 w-2.5 rounded-full"
          :style="{ backgroundColor: item.color || getColor(item.name) }"
        />
        <span class="text-[0.70rem] text-muted-foreground">
          {{ getLabel(item.name) }}
        </span>
        <span class="ml-auto font-medium text-foreground">
          {{ item.value }}
        </span>
      </div>
    </div>
  </div>
</template>

