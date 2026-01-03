<template>
  <nav class="flex items-center gap-2 text-sm text-gray-600 mb-4">
    <router-link
      to="/"
      class="hover:text-gray-900 transition-colors"
    >
      Accueil
    </router-link>
    <span v-if="items.length > 0">›</span>
    <template v-for="(item, index) in items" :key="index">
      <router-link
        v-if="item.to && index < items.length - 1"
        :to="item.to"
        class="hover:text-gray-900 transition-colors"
      >
        {{ item.label }}
      </router-link>
      <span v-else class="text-gray-900 font-medium">
        {{ item.label }}
      </span>
      <span v-if="index < items.length - 1" class="text-gray-400">›</span>
    </template>
  </nav>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'

defineProps<{
  items: Array<{
    label: string
    to?: string
  }>
}>()
</script>

