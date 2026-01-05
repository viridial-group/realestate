<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Hero Section -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-700 text-white py-12">
      <div class="max-w-7xl mx-auto px-4">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">
          Trouvez votre agence immobilière
        </h1>
        <p class="text-xl text-blue-100">
          Comparez les meilleures agences immobilières avec leurs avis clients vérifiés
        </p>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 py-8">
      <!-- Barre de recherche principale -->
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6 mb-8 -mt-8 relative z-10">
        <div class="grid md:grid-cols-4 gap-4">
          <!-- Recherche par nom -->
          <div class="md:col-span-2">
            <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
              Rechercher une agence
            </label>
            <div class="relative">
              <input
                v-model="filters.search"
                type="text"
                placeholder="Nom de l'agence..."
                class="w-full px-4 py-3 pl-10 border-2 border-gray-300 dark:border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                @input="debouncedSearch"
              />
              <svg class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>

          <!-- Ville -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
              Ville
            </label>
            <input
              v-model="filters.city"
              type="text"
              placeholder="Ex: Paris"
              class="w-full px-4 py-3 border-2 border-gray-300 dark:border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
              @input="debouncedSearch"
            />
          </div>

          <!-- Code postal -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
              Code postal
            </label>
            <input
              v-model="filters.postalCode"
              type="text"
              placeholder="Ex: 75001"
              class="w-full px-4 py-3 border-2 border-gray-300 dark:border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
              @input="debouncedSearch"
            />
          </div>
        </div>

        <!-- Filtres avancés -->
        <div class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
          <div class="flex flex-wrap items-center gap-4">
            <div class="flex items-center gap-2">
              <label class="text-sm font-medium text-gray-700 dark:text-gray-300">Note minimale:</label>
              <select
                v-model="filters.minRating"
                class="px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
                @change="searchAgencies"
              >
                <option :value="null">Toutes</option>
                <option :value="4.5">4.5+ ⭐</option>
                <option :value="4.0">4.0+ ⭐</option>
                <option :value="3.5">3.5+ ⭐</option>
                <option :value="3.0">3.0+ ⭐</option>
              </select>
            </div>

            <div class="flex items-center gap-2">
              <label class="text-sm font-medium text-gray-700 dark:text-gray-300">Tri:</label>
              <select
                v-model="sortBy"
                class="px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
                @change="applySort"
              >
                <option value="rating">Note (décroissant)</option>
                <option value="reviews">Nombre d'avis</option>
                <option value="properties">Nombre de propriétés</option>
                <option value="name">Nom (A-Z)</option>
              </select>
            </div>

            <button
              @click="clearFilters"
              class="ml-auto px-4 py-2 text-sm text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white font-medium"
            >
              Réinitialiser
            </button>
          </div>
        </div>
      </div>

      <!-- Statistiques globales -->
      <div v-if="!loading && agencies.length > 0" class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-4 text-center">
          <div class="text-3xl font-bold text-blue-600 dark:text-blue-400">{{ agencies.length }}</div>
          <div class="text-sm text-gray-600 dark:text-gray-400 mt-1">Agences trouvées</div>
        </div>
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-4 text-center">
          <div class="text-3xl font-bold text-yellow-500">{{ averageRating.toFixed(1) }}</div>
          <div class="text-sm text-gray-600 dark:text-gray-400 mt-1">Note moyenne</div>
        </div>
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-4 text-center">
          <div class="text-3xl font-bold text-green-600 dark:text-green-400">{{ totalReviews }}</div>
          <div class="text-sm text-gray-600 dark:text-gray-400 mt-1">Avis au total</div>
        </div>
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-4 text-center">
          <div class="text-3xl font-bold text-purple-600 dark:text-purple-400">{{ totalProperties }}</div>
          <div class="text-sm text-gray-600 dark:text-gray-400 mt-1">Propriétés</div>
        </div>
      </div>

      <!-- État de chargement -->
      <div v-if="loading" class="text-center py-16">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600 dark:text-gray-400 text-lg">Recherche des agences...</p>
      </div>

      <!-- Message d'erreur -->
      <div v-else-if="error" class="bg-red-50 dark:bg-red-900/20 border-2 border-red-200 dark:border-red-800 rounded-xl p-6">
        <div class="flex items-center gap-3">
          <svg class="h-6 w-6 text-red-600 dark:text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p class="text-red-800 dark:text-red-200 font-medium">{{ error }}</p>
        </div>
      </div>

      <!-- Résultats -->
      <div v-else-if="agencies.length > 0" class="space-y-6">
        <div class="flex items-center justify-between mb-4">
          <p class="text-gray-600 dark:text-gray-400 font-medium">
            {{ agencies.length }} agence{{ agencies.length > 1 ? 's' : '' }} trouvée{{ agencies.length > 1 ? 's' : '' }}
          </p>
        </div>

        <div class="grid gap-6">
          <div
            v-for="agency in sortedAgencies"
            :key="agency.id"
            class="bg-white dark:bg-gray-800 rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-200 dark:border-gray-700"
          >
            <div class="p-6">
              <div class="flex flex-col lg:flex-row gap-6">
                <!-- Logo et Note -->
                <div class="flex-shrink-0 flex flex-col items-center">
                  <div
                    v-if="agency.logoUrl"
                    class="w-28 h-28 rounded-2xl bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden shadow-lg mb-4"
                  >
                    <img :src="agency.logoUrl" :alt="agency.name" class="w-full h-full object-cover" />
                  </div>
                  <div
                    v-else
                    class="w-28 h-28 rounded-2xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center shadow-lg mb-4"
                  >
                    <span class="text-4xl text-white font-bold">{{ agency.name.charAt(0).toUpperCase() }}</span>
                  </div>
                  
                  <!-- Note principale -->
                  <div v-if="agency.reviewStats" class="text-center">
                    <div class="flex items-center justify-center gap-1 mb-1">
                      <span class="text-3xl font-bold text-gray-900 dark:text-white">
                        {{ formatRating(agency.reviewStats.averageRating) }}
                      </span>
                      <div class="flex text-yellow-500">
                        <svg v-for="i in 5" :key="i" class="h-6 w-6" :class="i <= Math.round(agency.reviewStats.averageRating) ? 'fill-current' : 'text-gray-300'" viewBox="0 0 20 20">
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                      </div>
                    </div>
                    <p class="text-sm text-gray-600 dark:text-gray-400">
                      {{ agency.reviewStats.totalReviews }} avis
                    </p>
                  </div>
                </div>

                <!-- Informations principales -->
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between mb-3">
                    <div>
                      <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-2 hover:text-blue-600 dark:hover:text-blue-400 cursor-pointer" @click="goToAgencyDetail(agency.id)">
                        {{ agency.name }}
                      </h2>
                      <div class="flex flex-wrap items-center gap-3 text-sm text-gray-600 dark:text-gray-400 mb-3">
                        <div v-if="agency.city" class="flex items-center gap-1">
                          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                          </svg>
                          <span>{{ agency.city }}{{ agency.postalCode ? ` ${agency.postalCode}` : '' }}</span>
                        </div>
                        <div v-if="agency.phone" class="flex items-center gap-1">
                          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                          </svg>
                          <span>{{ agency.phone }}</span>
                        </div>
                        <div v-if="agency.propertyCount" class="flex items-center gap-1">
                          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                          </svg>
                          <span class="font-semibold">{{ agency.propertyCount }}</span>
                          <span>propriété{{ agency.propertyCount > 1 ? 's' : '' }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <p v-if="agency.description" class="text-gray-700 dark:text-gray-300 mb-4 line-clamp-2">
                    {{ agency.description }}
                  </p>

                  <!-- Statistiques d'avis détaillées -->
                  <div v-if="agency.reviewStats && agency.reviewStats.ratingDistribution" class="mb-4">
                    <div class="flex items-center gap-2 mb-2">
                      <span class="text-sm font-semibold text-gray-700 dark:text-gray-300">Distribution des notes:</span>
                    </div>
                    <div class="space-y-1.5">
                      <div
                        v-for="(count) in [
                          { stars: 5, count: agency.reviewStats.ratingDistribution.fiveStars, label: '5 étoiles' },
                          { stars: 4, count: agency.reviewStats.ratingDistribution.fourStars, label: '4 étoiles' },
                          { stars: 3, count: agency.reviewStats.ratingDistribution.threeStars, label: '3 étoiles' }
                        ]"
                        :key="count.stars"
                        class="flex items-center gap-2"
                      >
                        <span class="text-xs text-gray-600 dark:text-gray-400 w-16">{{ count.label }}</span>
                        <div class="flex-1 bg-gray-200 dark:bg-gray-700 rounded-full h-2 overflow-hidden">
                          <div
                            class="bg-yellow-500 h-2 rounded-full transition-all duration-500"
                            :style="{ width: `${getPercentage(count.count, agency.reviewStats.totalReviews)}%` }"
                          ></div>
                        </div>
                        <span class="text-xs font-semibold text-gray-900 dark:text-white w-8 text-right">{{ count.count }}</span>
                      </div>
                    </div>
                  </div>

                  <!-- Badges -->
                  <div class="flex flex-wrap gap-2 mb-4">
                    <span
                      v-if="agency.reviewStats && agency.reviewStats.averageRating >= 4.5"
                      class="px-3 py-1 bg-green-100 dark:bg-green-900/20 text-green-800 dark:text-green-200 text-xs font-semibold rounded-full"
                    >
                      ⭐ Excellente note
                    </span>
                    <span
                      v-if="agency.reviewStats && agency.reviewStats.totalReviews >= 10"
                      class="px-3 py-1 bg-blue-100 dark:bg-blue-900/20 text-blue-800 dark:text-blue-200 text-xs font-semibold rounded-full"
                    >
                      ✓ Nombreux avis
                    </span>
                    <span
                      v-if="agency.reviewStats && agency.reviewStats.verifiedClientPercentage >= 80"
                      class="px-3 py-1 bg-purple-100 dark:bg-purple-900/20 text-purple-800 dark:text-purple-200 text-xs font-semibold rounded-full"
                    >
                      ✓ Clients vérifiés
                    </span>
                    <span
                      v-if="agency.propertyCount && agency.propertyCount >= 50"
                      class="px-3 py-1 bg-orange-100 dark:bg-orange-900/20 text-orange-800 dark:text-orange-200 text-xs font-semibold rounded-full"
                    >
                      🏠 Large portefeuille
                    </span>
                  </div>
                </div>

                <!-- Actions -->
                <div class="flex flex-col gap-3 lg:min-w-[180px]">
                  <router-link
                    :to="`/agencies/${agency.id}`"
                    class="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl transition-all duration-200 text-center shadow-md hover:shadow-lg"
                  >
                    Voir les détails
                  </router-link>
                  <button
                    v-if="agency.phone"
                    @click.stop="callAgency(agency.phone)"
                    class="px-6 py-3 border-2 border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 font-semibold rounded-xl transition-all duration-200"
                  >
                    📞 Appeler
                  </button>
                  <button
                    v-if="agency.email"
                    @click.stop="emailAgency(agency.email)"
                    class="px-6 py-3 border-2 border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 font-semibold rounded-xl transition-all duration-200"
                  >
                    📧 Email
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Aucun résultat -->
      <div v-else class="text-center py-16 bg-white dark:bg-gray-800 rounded-2xl shadow-sm">
        <svg class="mx-auto h-16 w-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <p class="text-gray-600 dark:text-gray-400 text-lg font-medium mb-2">Aucune agence trouvée</p>
        <p class="text-gray-500 dark:text-gray-500 text-sm">Essayez de modifier vos critères de recherche</p>
        <button
          @click="clearFilters"
          class="mt-4 px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors"
        >
          Réinitialiser les filtres
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { organizationReviewService, type OrganizationWithReviews } from '@viridial/shared'
import { useSEO } from '@/composables/useSEO'

const router = useRouter()
const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'

const agencies = ref<OrganizationWithReviews[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const sortBy = ref('rating')

const filters = ref({
  search: '',
  city: '',
  postalCode: '',
  minRating: null as number | null
})

let debounceTimer: ReturnType<typeof setTimeout> | null = null
const debouncedSearch = () => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }
  debounceTimer = setTimeout(() => {
    searchAgencies()
  }, 500)
}

// Statistiques calculées
const averageRating = computed(() => {
  if (agencies.value.length === 0) return 0
  const sum = agencies.value.reduce((acc, agency) => {
    return acc + (agency.reviewStats?.averageRating || 0)
  }, 0)
  return sum / agencies.value.length
})

const totalReviews = computed(() => {
  return agencies.value.reduce((acc, agency) => {
    return acc + (agency.reviewStats?.totalReviews || 0)
  }, 0)
})

const totalProperties = computed(() => {
  return agencies.value.reduce((acc, agency) => {
    return acc + (agency.propertyCount || 0)
  }, 0)
})

// Tri des agences
const sortedAgencies = computed(() => {
  const sorted = [...agencies.value]
  
  switch (sortBy.value) {
    case 'rating':
      return sorted.sort((a, b) => {
        const ratingA = a.reviewStats?.averageRating || 0
        const ratingB = b.reviewStats?.averageRating || 0
        return ratingB - ratingA
      })
    case 'reviews':
      return sorted.sort((a, b) => {
        const reviewsA = a.reviewStats?.totalReviews || 0
        const reviewsB = b.reviewStats?.totalReviews || 0
        return reviewsB - reviewsA
      })
    case 'properties':
      return sorted.sort((a, b) => {
        const propsA = a.propertyCount || 0
        const propsB = b.propertyCount || 0
        return propsB - propsA
      })
    case 'name':
      return sorted.sort((a, b) => a.name.localeCompare(b.name))
    default:
      return sorted
  }
})

onMounted(() => {
  // SEO
  useSEO({
    title: 'Recherche d\'agences immobilières | Viridial',
    description: 'Trouvez et comparez les meilleures agences immobilières en France. Consultez les avis clients, notes et statistiques pour choisir votre agence idéale.',
    keywords: ['agence immobilière', 'recherche agence', 'avis agence immobilière', 'meilleure agence', 'agence immobilière France'],
    type: 'website',
    canonical: `${siteUrl}/agencies`,
    url: `${siteUrl}/agencies`
  })

  searchAgencies()
})

async function searchAgencies() {
  loading.value = true
  error.value = null

  try {
    const params: any = {}
    if (filters.value.city) params.city = filters.value.city
    if (filters.value.postalCode) params.postalCode = filters.value.postalCode
    if (filters.value.minRating) params.minRating = filters.value.minRating

    let results = await organizationReviewService.searchOrganizations(params)
    
    // Filtrer par recherche de nom côté client
    if (filters.value.search) {
      const searchLower = filters.value.search.toLowerCase()
      results = results.filter(agency => 
        agency.name.toLowerCase().includes(searchLower) ||
        (agency.description && agency.description.toLowerCase().includes(searchLower)) ||
        (agency.city && agency.city.toLowerCase().includes(searchLower))
      )
    }
    
    agencies.value = results
  } catch (err: any) {
    console.error('Error searching agencies:', err)
    error.value = err.message || 'Une erreur est survenue lors de la recherche'
  } finally {
    loading.value = false
  }
}

function applySort() {
  // Le tri est géré par computed property sortedAgencies
}

function clearFilters() {
  filters.value = {
    search: '',
    city: '',
    postalCode: '',
    minRating: null
  }
  sortBy.value = 'rating'
  searchAgencies()
}

function formatRating(rating?: number): string {
  if (rating === undefined || rating === null) return 'N/A'
  return rating.toFixed(1)
}

function getPercentage(count: number, total: number): number {
  if (total === 0) return 0
  return Math.round((count / total) * 100)
}

function goToAgencyDetail(agencyId: number) {
  router.push(`/agencies/${agencyId}`)
}

function callAgency(phone: string) {
  window.location.href = `tel:${phone}`
}

function emailAgency(email: string) {
  window.location.href = `mailto:${email}`
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
