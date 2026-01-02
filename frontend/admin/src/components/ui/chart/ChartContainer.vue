<script setup lang="ts">
import { provide, computed } from 'vue'
import type { ChartConfig } from './interface'

const props = defineProps<{
  config: ChartConfig
}>()

const chartConfig = computed(() => props.config)

provide('chartConfig', chartConfig)
</script>

<template>
  <div class="w-full">
    <slot />
  </div>
</template>

