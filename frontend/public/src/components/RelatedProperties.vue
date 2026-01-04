<template>
  <section v-if="properties.length > 0" class="mt-12" aria-labelledby="related-properties-title">
    <h2 id="related-properties-title" class="text-2xl font-bold text-gray-900 mb-6">Propriétés similaires</h2>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <PropertyCard
        v-for="property in properties"
        :key="property.id"
        :item="property"
        @details="handleDetails"
        @contact="handleContact"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'
import PropertyCard from './PropertyCard.vue'
import { documentService } from '@/api/document.service'

const props = defineProps<{
  currentPropertyId: number
  city?: string
  type?: string
  maxPrice?: number
}>()

const emit = defineEmits<{
  'property-click': [id: number]
}>()

const properties = ref<PublicProperty[]>([])

onMounted(async () => {
  await loadRelatedProperties()
})

async function loadRelatedProperties() {
  try {
    const params: any = {
      page: 0,
      size: 6,
    }
    
    // Filtrer par ville si disponible
    if (props.city) {
      params.city = props.city
    }
    
    // Filtrer par type si disponible
    if (props.type) {
      params.type = props.type
    }
    
    // Filtrer par prix similaire (±20%)
    if (props.maxPrice) {
      params.minPrice = Math.floor(props.maxPrice * 0.8)
      params.maxPrice = Math.ceil(props.maxPrice * 1.2)
    }
    
    const response = await publicPropertyService.getPublishedProperties(params)
    
    // Exclure la propriété actuelle et formater les propriétés
    properties.value = response.content
      .filter(p => p.id !== props.currentPropertyId)
      .map(p => ({
        ...p,
        status: formatStatus(p.status),
        price: p.price ? Number(p.price) : 0,
        surface: p.surface ? Number(p.surface) : 0,
        lat: p.latitude != null ? Number(p.latitude) : 48.8566,
        lng: p.longitude != null ? Number(p.longitude) : 2.3522,
        description: p.description || '',
        rating: 0,
        reviews: 0,
        city: p.city || 'Non spécifié',
        type: p.type || 'Non spécifié',
      }))
  } catch (error) {
    console.error('Error loading related properties:', error)
    properties.value = []
  }
}

function handleDetails(id: number) {
  emit('property-click', id)
  window.location.href = `/property/${id}`
}

function handleContact(id: number) {
  alert(`Contact pour la propriété ${id}`)
}

function formatStatus(status: string): 'Disponible' | 'Vendu' | 'Loué' {
  const apiStatus = status?.toUpperCase()
  if (apiStatus === 'PUBLISHED' || apiStatus === 'AVAILABLE') {
    return 'Disponible'
  } else if (apiStatus === 'RENTED' || apiStatus === 'LOUÉ') {
    return 'Loué'
  } else if (apiStatus === 'SOLD' || apiStatus === 'VENDU') {
    return 'Vendu'
  }
  return 'Disponible'
}
</script>

