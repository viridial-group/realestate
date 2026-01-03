<template>
  <div class="relative">
    <!-- Image principale -->
    <div class="relative w-full h-96 md:h-[500px] rounded-lg overflow-hidden bg-gray-100">
      <img
        v-if="currentImage"
        :src="getImageUrl(currentImage)"
        :alt="currentImage.name || 'Image propriété'"
        class="w-full h-full object-cover transition-opacity duration-300"
        loading="eager"
        decoding="async"
        @error="handleImageError"
      />
      <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
        <span>Aucune image disponible</span>
      </div>
      
      <!-- Navigation précédent/suivant -->
      <button
        v-if="images.length > 1"
        @click="previousImage"
        class="absolute left-4 top-1/2 -translate-y-1/2 bg-white/90 hover:bg-white rounded-full p-2 shadow-lg transition-all"
        :disabled="currentIndex === 0"
      >
        <svg class="w-6 h-6 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      <button
        v-if="images.length > 1"
        @click="nextImage"
        class="absolute right-4 top-1/2 -translate-y-1/2 bg-white/90 hover:bg-white rounded-full p-2 shadow-lg transition-all"
        :disabled="currentIndex === images.length - 1"
      >
        <svg class="w-6 h-6 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
      
      <!-- Compteur d'images -->
      <div
        v-if="images.length > 1"
        class="absolute bottom-4 left-1/2 -translate-x-1/2 bg-black/60 text-white px-3 py-1 rounded-full text-sm"
      >
        {{ currentIndex + 1 }} / {{ images.length }}
      </div>
    </div>
    
    <!-- Miniatures -->
    <div
      v-if="images.length > 1"
      class="mt-4 flex gap-2 overflow-x-auto pb-2 scrollbar-hide"
    >
      <button
        v-for="(img, index) in images"
        :key="img.id"
        @click="goToImage(index)"
        class="flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 transition-all"
        :class="index === currentIndex ? 'border-blue-600 ring-2 ring-blue-300' : 'border-gray-200 hover:border-gray-300'"
      >
        <img
          :src="getImageUrl(img)"
          :alt="`Miniature ${index + 1}`"
          class="w-full h-full object-cover"
          loading="lazy"
          @error="handleImageError"
        />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Document } from '@/api/document.service'
import { documentService } from '@/api/document.service'
import { getPlaceholderImage } from '@/utils/imageOptimization'

const props = defineProps<{
  images: Document[]
}>()

const currentIndex = ref(0)

const currentImage = computed(() => {
  return props.images[currentIndex.value] || null
})

watch(() => props.images, (newImages) => {
  if (newImages.length > 0 && currentIndex.value >= newImages.length) {
    currentIndex.value = 0
  }
}, { immediate: true })

function getImageUrl(doc: Document): string {
  return documentService.getViewUrl(doc.id)
}

function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement
  img.src = getPlaceholderImage(800, 600)
}

function nextImage() {
  if (currentIndex.value < props.images.length - 1) {
    currentIndex.value++
  } else {
    currentIndex.value = 0 // Loop back to start
  }
}

function previousImage() {
  if (currentIndex.value > 0) {
    currentIndex.value--
  } else {
    currentIndex.value = props.images.length - 1 // Loop to end
  }
}

function goToImage(index: number) {
  if (index >= 0 && index < props.images.length) {
    currentIndex.value = index
  }
}

// Navigation clavier
function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'ArrowLeft') {
    previousImage()
  } else if (event.key === 'ArrowRight') {
    nextImage()
  }
}

// Ajouter les écouteurs d'événements clavier
if (typeof window !== 'undefined') {
  window.addEventListener('keydown', handleKeydown)
}
</script>

<style scoped>
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>

