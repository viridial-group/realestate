<template>
  <section class="testimonials-section" :id="id">
    <h2 v-if="title" class="text-2xl font-bold mb-6">{{ title }}</h2>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <article
        v-for="(testimonial, index) in testimonials"
        :key="index"
        class="testimonial-card bg-white dark:bg-gray-800 rounded-lg shadow-md p-6"
        itemscope
        itemtype="https://schema.org/Review"
      >
        <div class="flex items-center mb-4" itemprop="author" itemscope itemtype="https://schema.org/Person">
          <div
            v-if="testimonial.avatar"
            class="w-12 h-12 rounded-full overflow-hidden mr-4 flex-shrink-0"
          >
            <img
              :src="testimonial.avatar"
              :alt="testimonial.author"
              class="w-full h-full object-cover"
            />
          </div>
          <div v-else class="w-12 h-12 rounded-full bg-gray-300 dark:bg-gray-600 mr-4 flex-shrink-0 flex items-center justify-center">
            <span class="text-gray-600 dark:text-gray-300 font-semibold">
              {{ testimonial.author.charAt(0).toUpperCase() }}
            </span>
          </div>
          <div>
            <h3 class="font-semibold" itemprop="name">{{ testimonial.author }}</h3>
            <p v-if="testimonial.role" class="text-sm text-gray-600 dark:text-gray-400" itemprop="jobTitle">
              {{ testimonial.role }}
            </p>
          </div>
        </div>
        
        <div class="mb-4" itemprop="reviewRating" itemscope itemtype="https://schema.org/Rating">
          <meta itemprop="ratingValue" :content="testimonial.rating.toString()" />
          <meta itemprop="bestRating" content="5" />
          <div class="flex">
            <svg
              v-for="i in 5"
              :key="i"
              class="w-5 h-5"
              :class="i <= testimonial.rating ? 'text-yellow-400' : 'text-gray-300'"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>
          </div>
        </div>
        
        <p class="text-gray-700 dark:text-gray-300 mb-4" itemprop="reviewBody">
          "{{ testimonial.text }}"
        </p>
        
        <div v-if="testimonial.date" class="text-sm text-gray-500 dark:text-gray-400">
          <time :datetime="testimonial.date" itemprop="datePublished">
            {{ formatDate(testimonial.date) }}
          </time>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'

interface Testimonial {
  author: string
  text: string
  rating: number
  role?: string
  avatar?: string
  date?: string
}

interface Props {
  testimonials: Testimonial[]
  title?: string
  id?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Avis clients',
  id: 'testimonials'
})

/**
 * Génère les données structurées Schema.org pour les avis
 */
function generateReviewStructuredData() {
  if (typeof document === 'undefined') return

  const siteUrl = window.location.origin
  const reviewsUrl = `${siteUrl}${window.location.pathname}#${props.id}`

  const reviewData = {
    '@context': 'https://schema.org',
    '@type': 'Organization',
    '@id': `${siteUrl}#organization`,
    aggregateRating: {
      '@type': 'AggregateRating',
      ratingValue: calculateAverageRating(),
      reviewCount: props.testimonials.length,
      bestRating: 5,
      worstRating: 1
    },
    review: props.testimonials.map((testimonial, index) => ({
      '@type': 'Review',
      '@id': `${reviewsUrl}#review-${index}`,
      author: {
        '@type': 'Person',
        name: testimonial.author,
        ...(testimonial.role ? { jobTitle: testimonial.role } : {})
      },
      reviewRating: {
        '@type': 'Rating',
        ratingValue: testimonial.rating,
        bestRating: 5,
        worstRating: 1
      },
      reviewBody: testimonial.text,
      ...(testimonial.date ? { datePublished: testimonial.date } : {})
    }))
  }

  // Supprimer l'ancien script Review s'il existe
  const existingScript = document.querySelector('script[data-review-structured-data]')
  if (existingScript) {
    existingScript.remove()
  }

  // Ajouter le nouveau script
  const script = document.createElement('script')
  script.type = 'application/ld+json'
  script.setAttribute('data-review-structured-data', 'true')
  script.textContent = JSON.stringify(reviewData, null, 2)
  document.head.appendChild(script)
}

function calculateAverageRating(): number {
  if (props.testimonials.length === 0) return 0
  const sum = props.testimonials.reduce((acc, t) => acc + t.rating, 0)
  return Math.round((sum / props.testimonials.length) * 10) / 10
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }).format(date)
}

onMounted(() => {
  generateReviewStructuredData()
})

watch(() => props.testimonials, () => {
  generateReviewStructuredData()
}, { deep: true })
</script>

<style scoped>
.testimonials-section {
  @apply py-8;
}

.testimonial-card {
  @apply transition-all duration-200;
}

.testimonial-card:hover {
  @apply shadow-lg transform -translate-y-1;
}
</style>

