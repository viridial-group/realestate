<template>
  <div class="animate-pulse">
    <!-- Skeleton pour PropertyCard -->
    <div v-if="type === 'property-card'" class="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
      <div class="w-full h-48 bg-gray-200 dark:bg-gray-700"></div>
      <div class="p-5 space-y-4">
        <div class="h-6 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
        <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/2"></div>
        <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-full"></div>
        <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-2/3"></div>
        <div class="flex gap-4">
          <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-16"></div>
          <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-16"></div>
          <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-16"></div>
        </div>
        <div class="h-10 bg-gray-200 dark:bg-gray-700 rounded"></div>
      </div>
    </div>

    <!-- Skeleton pour liste de propriétés -->
    <div v-else-if="type === 'property-list'" class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
      <div class="flex gap-4">
        <div class="w-48 h-32 bg-gray-200 dark:bg-gray-700 rounded flex-shrink-0"></div>
        <div class="flex-1 space-y-3">
          <div class="h-6 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
          <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/2"></div>
          <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-full"></div>
          <div class="flex gap-4">
            <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-20"></div>
            <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-20"></div>
            <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-20"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Skeleton pour PropertyDetail -->
    <div v-else-if="type === 'property-detail'" class="space-y-6">
      <div class="h-96 bg-gray-200 dark:bg-gray-700 rounded-lg"></div>
      <div class="space-y-4">
        <div class="h-8 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
        <div class="h-6 bg-gray-200 dark:bg-gray-700 rounded w-1/2"></div>
        <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-full"></div>
        <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-5/6"></div>
        <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-4/6"></div>
      </div>
    </div>

    <!-- Skeleton générique -->
    <div v-else class="space-y-2">
      <div v-for="i in lines" :key="i" class="h-4 bg-gray-200 dark:bg-gray-700 rounded" :style="{ width: i === lines ? '60%' : '100%' }"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  type?: 'property-card' | 'property-list' | 'property-detail' | 'generic'
  lines?: number
}>()
</script>

