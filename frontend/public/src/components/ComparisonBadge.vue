<template>
  <span
    v-if="comparisonCount > 0"
    class="absolute -top-1 -right-1 bg-blue-600 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center"
  >
    {{ comparisonCount }}
  </span>
</template>

<script setup lang="ts">
import { useComparison } from '@/composables/useComparison'

const { comparisonCount } = useComparison()
</script>

