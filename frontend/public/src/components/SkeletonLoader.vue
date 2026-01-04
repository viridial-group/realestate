<template>
  <div class="animate-pulse" :aria-label="ariaLabel" role="status">
    <!-- Skeleton pour PropertyCard style Google -->
    <div v-if="type === 'property-card'" class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <div class="w-full h-48 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 bg-[length:200%_100%] animate-shimmer"></div>
      <div class="p-5 space-y-4">
        <div class="h-5 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-3/4 bg-[length:200%_100%] animate-shimmer"></div>
        <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-1/2 bg-[length:200%_100%] animate-shimmer"></div>
        <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-full bg-[length:200%_100%] animate-shimmer"></div>
        <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-2/3 bg-[length:200%_100%] animate-shimmer"></div>
        <div class="flex gap-4">
          <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-16 bg-[length:200%_100%] animate-shimmer"></div>
          <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-16 bg-[length:200%_100%] animate-shimmer"></div>
          <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-16 bg-[length:200%_100%] animate-shimmer"></div>
        </div>
        <div class="h-10 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded bg-[length:200%_100%] animate-shimmer"></div>
      </div>
    </div>

    <!-- Skeleton pour liste de propriétés style Google -->
    <div v-else-if="type === 'property-list'" class="bg-white border border-gray-200 rounded-lg p-6">
      <div class="flex gap-4">
        <div class="w-48 h-32 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded flex-shrink-0 bg-[length:200%_100%] animate-shimmer"></div>
        <div class="flex-1 space-y-3">
          <div class="h-5 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-3/4 bg-[length:200%_100%] animate-shimmer"></div>
          <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-1/2 bg-[length:200%_100%] animate-shimmer"></div>
          <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-full bg-[length:200%_100%] animate-shimmer"></div>
          <div class="flex gap-4">
            <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-20 bg-[length:200%_100%] animate-shimmer"></div>
            <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-20 bg-[length:200%_100%] animate-shimmer"></div>
            <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-20 bg-[length:200%_100%] animate-shimmer"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Skeleton pour PropertyDetail style Google -->
    <div v-else-if="type === 'property-detail'" class="space-y-6">
      <div class="h-96 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded-lg bg-[length:200%_100%] animate-shimmer"></div>
      <div class="space-y-4">
        <div class="h-7 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-3/4 bg-[length:200%_100%] animate-shimmer"></div>
        <div class="h-5 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-1/2 bg-[length:200%_100%] animate-shimmer"></div>
        <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-full bg-[length:200%_100%] animate-shimmer"></div>
        <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-5/6 bg-[length:200%_100%] animate-shimmer"></div>
        <div class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded w-4/6 bg-[length:200%_100%] animate-shimmer"></div>
      </div>
    </div>

    <!-- Skeleton générique style Google -->
    <div v-else class="space-y-2">
      <div v-for="i in lines" :key="i" class="h-4 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 rounded bg-[length:200%_100%] animate-shimmer" :style="{ width: i === lines ? '60%' : '100%' }"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  type?: 'property-card' | 'property-list' | 'property-detail' | 'generic'
  lines?: number
}>(), {
  type: 'generic',
  lines: 3
})

const ariaLabel = computed(() => {
  const labels: Record<string, string> = {
    'property-card': 'Chargement de la carte propriété',
    'property-list': 'Chargement de la liste de propriétés',
    'property-detail': 'Chargement des détails de la propriété',
    'generic': 'Chargement en cours'
  }
  return labels[props.type || 'generic']
})
</script>

<style scoped>
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.animate-shimmer {
  animation: shimmer 2s infinite;
}
</style>

