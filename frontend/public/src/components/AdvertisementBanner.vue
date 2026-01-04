<template>
  <div
    v-if="advertisement"
    class="advertisement-banner relative overflow-hidden rounded-lg shadow-sm hover:shadow-md transition-all"
    :class="getBannerClass()"
  >
    <a
      :href="advertisement.linkUrl || '#'"
      target="_blank"
      rel="noopener noreferrer sponsored"
      class="block relative"
      @click.prevent="handleClick"
    >
      <!-- Badge "Publicité" -->
      <div class="absolute top-2 right-2 z-10 bg-black bg-opacity-60 text-white text-xs px-2 py-1 rounded">
        Publicité
      </div>
      
      <!-- Image avec overlay -->
      <div class="relative">
        <img
          v-if="advertisement.imageUrl"
          :src="advertisement.imageUrl"
          :alt="advertisement.title"
          class="w-full h-full object-cover"
          :class="getImageClass()"
          loading="lazy"
        />
        <div v-else class="bg-gradient-to-br from-blue-500 to-purple-600" :class="getImageClass()">
          <div class="h-full flex flex-col items-center justify-center p-6 text-white">
            <h3 class="text-lg font-semibold mb-2 text-center">
              {{ advertisement.title }}
            </h3>
            <p v-if="advertisement.description" class="text-sm text-center opacity-90">
              {{ advertisement.description }}
            </p>
            <span v-if="advertisement.linkUrl" class="mt-3 text-xs underline">
              En savoir plus →
            </span>
          </div>
        </div>
        
        <!-- Overlay au survol -->
        <div class="absolute inset-0 bg-black bg-opacity-0 hover:bg-opacity-10 transition-all duration-300"></div>
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
    SIDEBAR: 'w-full',
    INLINE: 'w-full my-6',
    POPUP: 'fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center',
    SPONSORED_PROPERTY: 'w-full border-2 border-blue-500 rounded-lg'
  }
  return classes[props.advertisement.adType] || 'w-full'
}

function getImageClass(): string {
  const classes: Record<string, string> = {
    BANNER: 'h-32 md:h-40',
    SIDEBAR: 'h-48 md:h-64',
    INLINE: 'h-32 md:h-40',
    POPUP: 'h-96 w-96 max-w-[90vw]',
    SPONSORED_PROPERTY: 'h-48'
  }
  return classes[props.advertisement.adType] || 'h-32'
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
  transition: transform 0.2s ease-in-out;
}

.advertisement-banner:hover {
  transform: scale(1.02);
}
</style>

