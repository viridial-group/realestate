<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <!-- Breadcrumbs -->
    <Breadcrumbs :items="breadcrumbItems" />
    
    <!-- Header de la ville -->
    <header class="mb-8">
      <h1 class="text-4xl font-bold mb-4">{{ cityName }} - Immobilier</h1>
      <p class="text-xl text-gray-600 dark:text-gray-400 mb-6">
        Découvrez toutes les annonces immobilières à {{ cityName }}. 
        Appartements, maisons, villas à vendre ou à louer.
      </p>
      
      <!-- Stats rapides -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <div class="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-4">
          <div class="text-2xl font-bold text-blue-600 dark:text-blue-400">{{ stats.total }}</div>
          <div class="text-sm text-gray-600 dark:text-gray-400">Annonces</div>
        </div>
        <div class="bg-green-50 dark:bg-green-900/20 rounded-lg p-4">
          <div class="text-2xl font-bold text-green-600 dark:text-green-400">{{ stats.vente }}</div>
          <div class="text-sm text-gray-600 dark:text-gray-400">À vendre</div>
        </div>
        <div class="bg-purple-50 dark:bg-purple-900/20 rounded-lg p-4">
          <div class="text-2xl font-bold text-purple-600 dark:text-purple-400">{{ stats.location }}</div>
          <div class="text-sm text-gray-600 dark:text-gray-400">À louer</div>
        </div>
        <div class="bg-orange-50 dark:bg-orange-900/20 rounded-lg p-4">
          <div class="text-2xl font-bold text-orange-600 dark:text-orange-400">{{ stats.prixMoyen }}€</div>
          <div class="text-sm text-gray-600 dark:text-gray-400">Prix moyen</div>
        </div>
      </div>
    </header>

    <!-- Contenu SEO -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Colonne principale -->
      <div class="lg:col-span-2">
        <!-- Description de la ville -->
        <section class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
          <h2 class="text-2xl font-bold mb-4">Immobilier à {{ cityName }}</h2>
          <div class="prose dark:prose-invert max-w-none" v-html="cityDescription"></div>
        </section>

        <!-- Quartiers populaires -->
        <section class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
          <h2 class="text-2xl font-bold mb-4">Quartiers populaires à {{ cityName }}</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div
              v-for="neighborhood in neighborhoods"
              :key="neighborhood.name"
              class="border border-gray-200 dark:border-gray-700 rounded-lg p-4 hover:shadow-md transition-shadow"
            >
              <h3 class="font-semibold mb-2">{{ neighborhood.name }}</h3>
              <p class="text-sm text-gray-600 dark:text-gray-400 mb-2">{{ neighborhood.description }}</p>
              <router-link
                :to="`/search?city=${cityName}&neighborhood=${neighborhood.name}`"
                class="text-blue-600 dark:text-blue-400 hover:underline text-sm"
              >
                Voir les annonces →
              </router-link>
            </div>
          </div>
        </section>

        <!-- Propriétés -->
        <section class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
          <h2 class="text-2xl font-bold mb-4">Annonces récentes à {{ cityName }}</h2>
          <div v-if="loading" class="text-center py-8">
            <p class="text-gray-600 dark:text-gray-400">Chargement...</p>
          </div>
          <div v-else-if="properties.length === 0" class="text-center py-8">
            <p class="text-gray-600 dark:text-gray-400">Aucune annonce disponible pour le moment.</p>
          </div>
          <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <PropertyCard
              v-for="property in properties"
              :key="property.id"
              :item="property"
            />
          </div>
        </section>
      </div>

      <!-- Sidebar -->
      <aside class="lg:col-span-1">
        <!-- Filtres rapides -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6 sticky top-4">
          <h3 class="font-semibold mb-4">Rechercher à {{ cityName }}</h3>
          <router-link
            :to="`/search?city=${cityName}&transactionType=SALE`"
            class="block w-full text-left px-4 py-2 mb-2 bg-blue-50 dark:bg-blue-900/20 rounded hover:bg-blue-100 dark:hover:bg-blue-900/30 transition-colors"
          >
            🏠 Acheter à {{ cityName }}
          </router-link>
          <router-link
            :to="`/search?city=${cityName}&transactionType=RENT`"
            class="block w-full text-left px-4 py-2 mb-2 bg-green-50 dark:bg-green-900/20 rounded hover:bg-green-100 dark:hover:bg-green-900/30 transition-colors"
          >
            🔑 Louer à {{ cityName }}
          </router-link>
        </div>

        <!-- Guide d'achat -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
          <h3 class="font-semibold mb-4">Guide d'achat immobilier</h3>
          <ul class="space-y-2 text-sm">
            <li>
              <router-link to="/blog/guide-achat-immobilier" class="text-blue-600 dark:text-blue-400 hover:underline">
                Guide complet pour acheter
              </router-link>
            </li>
            <li>
              <router-link to="/blog/guide-location" class="text-blue-600 dark:text-blue-400 hover:underline">
                Guide pour louer
              </router-link>
            </li>
            <li>
              <router-link to="/contact" class="text-blue-600 dark:text-blue-400 hover:underline">
                Contacter un conseiller
              </router-link>
            </li>
          </ul>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { usePublicProperties, type PublicProperty } from '@/composables/usePublicProperties'
import { useSEO } from '@/composables/useSEO'
import Breadcrumbs from '@/components/Breadcrumbs.vue'
import PropertyCard from '@/components/PropertyCard.vue'

const route = useRoute()
const cityName = computed(() => route.params.city as string || 'Paris')

const { formattedProperties, loading, loadProperties } = usePublicProperties()
const properties = computed(() => formattedProperties.value.slice(0, 6))

const stats = ref({
  total: 0,
  vente: 0,
  location: 0,
  prixMoyen: 0
})

const neighborhoods = ref([
  { name: 'Centre-ville', description: 'Quartier central avec commerces et transports' },
  { name: 'Quartier résidentiel', description: 'Zone calme et familiale' },
  { name: 'Quartier historique', description: 'Patrimoine et charme ancien' }
])

const cityDescription = ref(`
  <p>
    <strong>${cityName.value}</strong> est une ville dynamique offrant de nombreuses opportunités immobilières. 
    Que vous cherchiez à acheter ou à louer, vous trouverez une large sélection de biens adaptés à tous les budgets.
  </p>
  <p>
    Le marché immobilier de ${cityName.value} est varié, avec des appartements en centre-ville, 
    des maisons en périphérie, et des biens de prestige pour les investisseurs.
  </p>
  <p>
    Nos conseillers immobiliers locaux vous accompagnent dans votre projet, 
    que ce soit pour un achat, une location ou un investissement.
  </p>
`)

const breadcrumbItems = computed(() => [
  { label: 'Recherche', to: '/search' },
  { label: cityName.value, to: undefined }
])

// Charger les propriétés de la ville
onMounted(async () => {
  await loadProperties({ city: cityName.value, size: 6 })
  
  // Calculer les stats
  stats.value.total = formattedProperties.value.length
  stats.value.vente = formattedProperties.value.filter(p => p.transactionType === 'SALE').length
  stats.value.location = formattedProperties.value.filter(p => p.transactionType === 'RENT').length
  
  const prices = formattedProperties.value.map(p => p.price).filter(p => p > 0)
  if (prices.length > 0) {
    stats.value.prixMoyen = Math.round(prices.reduce((a, b) => a + b, 0) / prices.length)
  }
  
  // SEO
  useSEO({
    title: `Immobilier ${cityName.value} - Annonces à vendre et à louer`,
    description: `Découvrez toutes les annonces immobilières à ${cityName.value}. Appartements, maisons, villas à vendre ou à louer. Prix, photos, localisation.`,
    keywords: [`immobilier ${cityName.value}`, `acheter ${cityName.value}`, `louer ${cityName.value}`, 'appartement', 'maison', 'villa'],
    url: `/city/${cityName.value}`,
    canonical: `/city/${cityName.value}`
  })
})
</script>

