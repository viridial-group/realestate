<template>
  <div
    v-if="advertisement"
    class="advertisement-banner bg-white border border-gray-200 rounded-lg overflow-hidden transition-all duration-200 hover:shadow-lg"
    :class="getBannerClass()"
  >
    <a
      :href="advertisement.linkUrl || '#'"
      target="_blank"
      rel="noopener noreferrer sponsored"
      class="block"
      @click.prevent="handleClick"
    >
      <!-- Badge "Ad" style Google en haut à gauche -->
      <div class="absolute top-0 left-0 z-10 bg-gray-100 text-gray-600 text-[10px] font-medium px-2 py-0.5 rounded-br">
        Ad
      </div>
      
      <!-- Layout selon le type d'annonce -->
      <div v-if="advertisement.adType === 'SIDEBAR'" class="flex flex-col">
        <!-- Image en haut pour SIDEBAR -->
        <div class="relative w-full" :class="getImageClass()">
          <img
            v-if="advertisement.imageUrl"
            :src="advertisement.imageUrl"
            :alt="advertisement.title"
            class="w-full h-full object-cover"
            loading="lazy"
          />
          <div v-else class="w-full h-full bg-gradient-to-br from-blue-50 to-indigo-50 flex items-center justify-center">
            <div class="text-center p-4">
              <div class="w-12 h-12 mx-auto mb-2 bg-blue-100 rounded-full flex items-center justify-center">
                <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Contenu texte en dessous -->
        <div class="p-4 space-y-2">
          <h3 class="text-sm font-medium text-gray-900 leading-tight line-clamp-2">
            {{ advertisement.title }}
          </h3>
          <p v-if="advertisement.description" class="text-xs text-gray-600 leading-relaxed line-clamp-3">
            {{ advertisement.description }}
          </p>
          <div v-if="advertisement.linkUrl" class="flex items-center text-xs text-blue-600 font-medium pt-1">
            <span>{{ getDomainFromUrl(advertisement.linkUrl) }}</span>
            <svg class="w-3 h-3 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>
      </div>
      
      <!-- Layout horizontal pour BANNER et INLINE -->
      <div v-else class="flex items-center">
        <!-- Image à gauche -->
        <div class="relative flex-shrink-0" :class="getImageClass()">
          <img
            v-if="advertisement.imageUrl"
            :src="advertisement.imageUrl"
            :alt="advertisement.title"
            class="w-full h-full object-cover"
            loading="lazy"
          />
          <div v-else class="w-full h-full bg-gradient-to-br from-blue-50 to-indigo-50 flex items-center justify-center">
            <div class="text-center p-4">
              <div class="w-10 h-10 mx-auto mb-2 bg-blue-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Contenu texte à droite -->
        <div class="flex-1 p-4 space-y-2">
          <div class="flex items-start justify-between gap-2">
            <h3 class="text-base font-medium text-gray-900 leading-tight flex-1">
              {{ advertisement.title }}
            </h3>
          </div>
          <p v-if="advertisement.description" class="text-sm text-gray-600 leading-relaxed line-clamp-2">
            {{ advertisement.description }}
          </p>
          <div v-if="advertisement.linkUrl" class="flex items-center text-xs text-blue-600 font-medium pt-1">
            <span>{{ getDomainFromUrl(advertisement.linkUrl) }}</span>
            <svg class="w-3 h-3 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>
      </div>
    </a>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { advertisementService, type Advertisement } from '@viridial/shared'

const props = defineProps<{
  advertisement: Advertisement
  propertyId?: number
  city?: string
  postalCode?: string
  pageType?: string
}>()

onMounted(() => {
  // Enregistrer l'impression
  recordImpression()
})

function getBannerClass(): string {
  const classes: Record<string, string> = {
    BANNER: 'w-full',
    SIDEBAR: 'w-full relative',
    INLINE: 'w-full my-4',
    POPUP: 'fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center max-w-md mx-auto',
    SPONSORED_PROPERTY: 'w-full border-2 border-blue-200'
  }
  return classes[props.advertisement.adType] || 'w-full relative'
}

function getImageClass(): string {
  const classes: Record<string, string> = {
    BANNER: 'w-32 h-24',
    SIDEBAR: 'h-40',
    INLINE: 'w-32 h-24',
    POPUP: 'h-64 w-full',
    SPONSORED_PROPERTY: 'h-32 w-32'
  }
  return classes[props.advertisement.adType] || 'h-32'
}

function getDomainFromUrl(url: string): string {
  try {
    const urlObj = new URL(url)
    return urlObj.hostname.replace('www.', '')
  } catch {
    return url
  }
}

async function recordImpression() {
  try {
    await advertisementService.recordImpression(props.advertisement.id, {
      propertyId: props.propertyId,
      pageType: props.pageType || 'UNKNOWN',
      city: props.city,
      postalCode: props.postalCode
    })
  } catch (error) {
    console.error('Error recording impression:', error)
  }
}

async function handleClick() {
  try {
    // Ouvrir le lien immédiatement pour une meilleure UX
    if (props.advertisement.linkUrl) {
      window.open(props.advertisement.linkUrl, '_blank', 'noopener,noreferrer')
    }
    
    // Enregistrer le clic en arrière-plan (ne pas bloquer la navigation)
    advertisementService.recordClick(props.advertisement.id, {
      propertyId: props.propertyId,
      city: props.city,
      postalCode: props.postalCode
    }).catch(() => {
      // Erreur silencieuse
    })
  } catch (error) {
    // Erreur silencieuse
  }
}
</script>

<style scoped>
.advertisement-banner {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.advertisement-banner:hover {
  border-color: #dadce0;
}

/* Line clamp utilities */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
