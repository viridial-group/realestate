<template>
  <div class="space-y-8">
    <!-- Section Nouveautés -->
    <section v-if="newProperties.length > 0" class="bg-white rounded-lg shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <div>
          <h2 class="text-2xl font-bold text-gray-900">Nouvelles annonces</h2>
          <p class="text-sm text-gray-600 mt-1">Découvrez les dernières propriétés ajoutées</p>
        </div>
        <router-link
          to="/search?sortBy=newest"
          class="text-sm text-blue-600 hover:text-blue-700 font-medium"
        >
          Voir toutes →
        </router-link>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <PropertyCard
          v-for="property in newProperties"
          :key="property.id"
          :item="property"
          @details="goToDetails"
          @contact="handleContact"
        />
      </div>
    </section>

    <!-- Section Populaires -->
    <section v-if="popularProperties.length > 0" class="bg-white rounded-lg shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <div>
          <h2 class="text-2xl font-bold text-gray-900">Annonces populaires</h2>
          <p class="text-sm text-gray-600 mt-1">Les propriétés les plus consultées</p>
        </div>
        <router-link
          to="/search?sortBy=popular"
          class="text-sm text-blue-600 hover:text-blue-700 font-medium"
        >
          Voir toutes →
        </router-link>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <PropertyCard
          v-for="property in popularProperties"
          :key="property.id"
          :item="property"
          @details="goToDetails"
          @contact="handleContact"
        />
      </div>
    </section>

    <!-- Section Recommandées -->
    <section v-if="recommendedProperties.length > 0" class="bg-white rounded-lg shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <div>
          <h2 class="text-2xl font-bold text-gray-900">Recommandations pour vous</h2>
          <p class="text-sm text-gray-600 mt-1">Basées sur vos recherches récentes</p>
        </div>
        <router-link
          to="/search"
          class="text-sm text-blue-600 hover:text-blue-700 font-medium"
        >
          Voir toutes →
        </router-link>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <PropertyCard
          v-for="property in recommendedProperties"
          :key="property.id"
          :item="property"
          @details="goToDetails"
          @contact="handleContact"
        />
      </div>
    </section>

    <!-- Section Tendances -->
    <section v-if="trendingProperties.length > 0" class="bg-white rounded-lg shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <div>
          <h2 class="text-2xl font-bold text-gray-900">Tendances du marché</h2>
          <p class="text-sm text-gray-600 mt-1">Les propriétés qui montent en popularité</p>
        </div>
        <router-link
          to="/search?sortBy=trending"
          class="text-sm text-blue-600 hover:text-blue-700 font-medium"
        >
          Voir toutes →
        </router-link>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <PropertyCard
          v-for="property in trendingProperties"
          :key="property.id"
          :item="property"
          @details="goToDetails"
          @contact="handleContact"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PropertyCard from '@/components/PropertyCard.vue'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'
import { useSearchHistory } from '@/composables/useSearchHistory'

const router = useRouter()
const { getRecentSearches, getFrequentSearches } = useSearchHistory()

const newProperties = ref<PublicProperty[]>([])
const popularProperties = ref<PublicProperty[]>([])
const recommendedProperties = ref<PublicProperty[]>([])
const trendingProperties = ref<PublicProperty[]>([])

onMounted(async () => {
  await loadSections()
})

async function loadSections() {
  try {
    // Charger les nouvelles annonces (triées par date de création)
    const newResponse = await publicPropertyService.getPublishedProperties({
      page: 0,
      size: 6,
      sortBy: 'newest',
    })
    newProperties.value = newResponse.content || []

    // Charger les annonces populaires (triées par vues)
    const popularResponse = await publicPropertyService.getPublishedProperties({
      page: 0,
      size: 6,
      sortBy: 'popular',
    })
    popularProperties.value = popularResponse.content || []

    // Charger les recommandations basées sur l'historique
    await loadRecommendations()

    // Charger les tendances (triées par croissance)
    const trendingResponse = await publicPropertyService.getPublishedProperties({
      page: 0,
      size: 6,
      sortBy: 'trending',
    })
    trendingProperties.value = trendingResponse.content || []
  } catch (error) {
    console.error('Error loading home sections:', error)
  }
}

async function loadRecommendations() {
  try {
    // Obtenir les recherches fréquentes
    const frequentSearches = getFrequentSearches(3)
    
    if (frequentSearches.length > 0) {
      // Utiliser la première recherche fréquente pour les recommandations
      const searchTerm = frequentSearches[0]
      const response = await publicPropertyService.getPublishedProperties({
        search: searchTerm,
        page: 0,
        size: 6,
      })
      recommendedProperties.value = response.content || []
    } else {
      // Fallback: charger des propriétés récentes
      const response = await publicPropertyService.getPublishedProperties({
        page: 0,
        size: 6,
        sortBy: 'newest',
      })
      recommendedProperties.value = response.content || []
    }
  } catch (error) {
    console.error('Error loading recommendations:', error)
  }
}

function goToDetails(id: number) {
  router.push(`/property/${id}`)
}

function handleContact(id: number) {
  router.push(`/property/${id}#contact`)
}
</script>

