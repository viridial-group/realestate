<template>
  <div class="space-y-6">
    <!-- Statistiques des avis -->
    <div v-if="stats" class="bg-white rounded-lg shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-gray-900">Avis clients</h3>
        <div class="flex items-center gap-2">
          <div class="flex items-center">
            <Star v-for="i in 5" :key="i" 
              :class="['h-5 w-5', i <= Math.round(stats.averageRating) ? 'text-yellow-400 fill-current' : 'text-gray-300']" />
          </div>
          <span class="text-lg font-semibold text-gray-900 ml-2">
            {{ stats.averageRating.toFixed(1) }}
          </span>
          <span class="text-sm text-gray-500 ml-1">
            ({{ stats.totalReviews }} {{ stats.totalReviews === 1 ? 'avis' : 'avis' }})
          </span>
        </div>
      </div>

      <!-- Distribution des notes -->
      <div v-if="stats.ratingDistribution" class="space-y-2 mt-4">
        <div v-for="rating in [5, 4, 3, 2, 1]" :key="rating" class="flex items-center gap-2">
          <span class="text-sm text-gray-600 w-8">{{ rating }}</span>
          <Star class="h-4 w-4 text-yellow-400 fill-current" />
          <div class="flex-1 bg-gray-200 rounded-full h-2 overflow-hidden">
            <div 
              class="bg-yellow-400 h-full transition-all duration-300"
              :style="{ width: `${stats.ratingPercentages?.[rating] || 0}%` }"
            ></div>
          </div>
          <span class="text-sm text-gray-500 w-12 text-right">
            {{ stats.ratingDistribution[rating] || 0 }}
          </span>
        </div>
      </div>
    </div>

    <!-- Liste des avis -->
    <div v-if="reviews.length > 0" class="space-y-4">
      <div 
        v-for="review in reviews" 
        :key="review.id"
        class="bg-white rounded-lg shadow-sm p-6"
      >
        <div class="flex items-start justify-between mb-3">
          <div class="flex-1">
            <div class="flex items-center gap-3 mb-2">
              <div class="flex items-center">
                <Star 
                  v-for="i in 5" 
                  :key="i"
                  :class="['h-4 w-4', i <= review.rating ? 'text-yellow-400 fill-current' : 'text-gray-300']"
                />
              </div>
              <span class="font-semibold text-gray-900">
                {{ review.authorName || 'Anonyme' }}
              </span>
              <span v-if="review.verifiedPurchase" 
                class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800">
                <CheckCircle class="h-3 w-3 mr-1" />
                Achat vérifié
              </span>
            </div>
            <p class="text-sm text-gray-500">
              {{ formatDate(review.createdAt) }}
            </p>
          </div>
        </div>
        
        <p class="text-gray-700 mb-3">{{ review.comment }}</p>
        
        <div class="flex items-center gap-4 text-sm text-gray-500">
          <button 
            @click="handleMarkHelpful(review.id)"
            class="flex items-center gap-1 hover:text-gray-700 transition-colors"
          >
            <ThumbsUp class="h-4 w-4" />
            Utile ({{ review.helpfulCount || 0 }})
          </button>
        </div>
      </div>
    </div>

    <!-- Message si aucun avis -->
    <div v-else class="bg-white rounded-lg shadow-sm p-6 text-center">
      <p class="text-gray-500">Aucun avis pour le moment. Soyez le premier à laisser un avis !</p>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center gap-2">
      <button
        v-for="page in totalPages"
        :key="page"
        @click="loadReviews(page - 1)"
        :class="[
          'px-4 py-2 rounded-lg transition-colors',
          currentPage === page - 1
            ? 'bg-blue-600 text-white'
            : 'bg-white text-gray-700 hover:bg-gray-100'
        ]"
      >
        {{ page }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { reviewService } from '@viridial/shared'
import type { Review, ReviewStats } from '@viridial/shared'
import { Star, CheckCircle, ThumbsUp } from 'lucide-vue-next'

interface Props {
  propertyId: number
}

const props = defineProps<Props>()

const reviews = ref<Review[]>([])
const stats = ref<ReviewStats | null>(null)
const currentPage = ref(0)
const totalPages = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const loadReviews = async (page: number = 0) => {
  loading.value = true
  try {
    const response = await reviewService.getApprovedByProperty(props.propertyId, {
      page,
      size: pageSize.value
    })
    reviews.value = response.content || []
    currentPage.value = response.number || 0
    totalPages.value = response.totalPages || 1
  } catch (error) {
    console.error('Error loading reviews:', error)
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    stats.value = await reviewService.getStatsByProperty(props.propertyId)
  } catch (error) {
    console.error('Error loading review stats:', error)
  }
}

const handleMarkHelpful = async (reviewId: number) => {
  try {
    await reviewService.markHelpful(reviewId)
    // Recharger les avis pour mettre à jour le compteur
    await loadReviews(currentPage.value)
  } catch (error) {
    console.error('Error marking review as helpful:', error)
  }
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }).format(date)
}

onMounted(() => {
  loadStats()
  loadReviews()
})

watch(() => props.propertyId, () => {
  loadStats()
  loadReviews()
})

defineExpose({
  refresh: () => {
    loadStats()
    loadReviews(currentPage.value)
  }
})
</script>

