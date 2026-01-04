<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between z-10">
        <h2 class="text-2xl font-bold text-gray-900">Aperçu de l'annonce</h2>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-600 transition-colors"
        >
          <X class="h-6 w-6" />
        </button>
      </div>

      <!-- Contenu -->
      <div class="p-6 space-y-6">
        <!-- Images -->
        <div v-if="images && images.length > 0" class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-900">Images</h3>
          <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
            <div
              v-for="(image, index) in images"
              :key="index"
              class="relative aspect-square rounded-lg overflow-hidden border border-gray-200"
            >
              <img
                :src="image.preview || image.url"
                :alt="`Image ${index + 1}`"
                class="w-full h-full object-cover"
              />
              <div
                v-if="image.isPrimary"
                class="absolute top-2 left-2 px-2 py-1 bg-blue-600 text-white text-xs rounded"
              >
                Principale
              </div>
            </div>
          </div>
        </div>
        <div v-else class="bg-gray-100 rounded-lg aspect-video flex items-center justify-center">
          <p class="text-gray-500">Aucune image</p>
        </div>

        <!-- Informations principales -->
        <div class="space-y-4">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ property.title }}</h1>
            <div class="flex items-center gap-4 text-gray-600">
              <span class="flex items-center gap-1">
                <MapPin class="h-4 w-4" />
                {{ property.address }}, {{ property.city }}{{ property.postalCode ? ` ${property.postalCode}` : '' }}
              </span>
            </div>
          </div>

          <div class="flex items-center gap-6 py-4 border-y border-gray-200">
            <div>
              <p class="text-sm text-gray-600">Prix</p>
              <p class="text-2xl font-bold text-gray-900">{{ formatPrice(property.price) }}</p>
            </div>
            <div v-if="property.area">
              <p class="text-sm text-gray-600">Surface</p>
              <p class="text-xl font-semibold text-gray-900">{{ property.area }} m²</p>
            </div>
            <div v-if="property.bedrooms">
              <p class="text-sm text-gray-600">Chambres</p>
              <p class="text-xl font-semibold text-gray-900">{{ property.bedrooms }}</p>
            </div>
            <div v-if="property.bathrooms">
              <p class="text-sm text-gray-600">Salles de bain</p>
              <p class="text-xl font-semibold text-gray-900">{{ property.bathrooms }}</p>
            </div>
          </div>

          <div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Description</h3>
            <p class="text-gray-700 whitespace-pre-wrap">{{ property.description }}</p>
          </div>

          <div class="grid grid-cols-2 md:grid-cols-4 gap-4 pt-4 border-t border-gray-200">
            <div>
              <p class="text-sm text-gray-600">Type</p>
              <p class="font-medium text-gray-900">{{ getPropertyTypeLabel(property.propertyType || property.type) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Transaction</p>
              <p class="font-medium text-gray-900">{{ property.transactionType === 'SALE' ? 'Vente' : 'Location' }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Ville</p>
              <p class="font-medium text-gray-900">{{ property.city }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Pays</p>
              <p class="font-medium text-gray-900">{{ property.country }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer avec actions -->
      <div class="sticky bottom-0 bg-white border-t border-gray-200 px-6 py-4 flex items-center justify-end gap-3">
        <button
          @click="$emit('close')"
          class="px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md transition-colors"
        >
          Retour à l'édition
        </button>
        <button
          @click="$emit('publish')"
          class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
        >
          Publier maintenant
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { X, MapPin } from 'lucide-vue-next'

defineProps<{
  property: any
  images?: any[]
}>()

defineEmits<{
  close: []
  publish: []
}>()

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
  }).format(price)
}

function getPropertyTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    APARTMENT: 'Appartement',
    HOUSE: 'Maison',
    VILLA: 'Villa',
    LAND: 'Terrain',
    COMMERCIAL: 'Commercial',
    OTHER: 'Autre',
  }
  return labels[type] || type
}
</script>

