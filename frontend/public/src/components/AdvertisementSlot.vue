<template>
  <div v-if="advertisements.length > 0" class="advertisement-slot">
    <AdvertisementBanner
      v-for="ad in advertisements"
      :key="ad.id"
      :advertisement="ad"
      :property-id="propertyId"
      :city="city"
      :postal-code="postalCode"
      :page-type="pageType"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { advertisementService, type Advertisement } from '@viridial/shared'
import AdvertisementBanner from './AdvertisementBanner.vue'

const props = defineProps<{
  adType?: 'BANNER' | 'SIDEBAR' | 'INLINE' | 'POPUP' | 'SPONSORED_PROPERTY'
  position?: 'TOP' | 'BOTTOM' | 'SIDEBAR_LEFT' | 'SIDEBAR_RIGHT' | 'INLINE' | 'HEADER' | 'FOOTER'
  city?: string
  postalCode?: string
  propertyType?: string
  propertyId?: number
  pageType?: string
}>()

const advertisements = ref<Advertisement[]>([])

onMounted(async () => {
  await loadAdvertisements()
})

async function loadAdvertisements() {
  try {
    advertisements.value = await advertisementService.getActive({
      adType: props.adType,
      position: props.position,
      city: props.city,
      postalCode: props.postalCode,
      propertyType: props.propertyType
    })
  } catch (error) {
    console.error('Error loading advertisements:', error)
  }
}
</script>

<style scoped>
.advertisement-slot {
  width: 100%;
}
</style>

