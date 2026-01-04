<template>
  <section class="faq-section" :id="id">
    <h2 v-if="title" class="text-2xl font-bold mb-6">{{ title }}</h2>
    <div class="space-y-4">
      <div
        v-for="(item, index) in items"
        :key="index"
        class="faq-item border border-gray-200 dark:border-gray-700 rounded-lg overflow-hidden"
        itemscope
        itemtype="https://schema.org/Question"
      >
        <button
          @click="toggleItem(index)"
          class="w-full text-left p-4 bg-gray-50 dark:bg-gray-800 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors flex items-center justify-between"
          :aria-expanded="openItems.includes(index)"
          :aria-controls="`faq-answer-${index}`"
        >
          <h3 class="font-semibold text-lg" itemprop="name">{{ item.question }}</h3>
          <span class="text-gray-500 dark:text-gray-400 ml-4 flex-shrink-0">
            <svg
              class="w-5 h-5 transition-transform"
              :class="{ 'rotate-180': openItems.includes(index) }"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </span>
        </button>
        <div
          v-show="openItems.includes(index)"
          :id="`faq-answer-${index}`"
          class="p-4 bg-white dark:bg-gray-900"
          itemscope
          itemtype="https://schema.org/Answer"
          itemprop="acceptedAnswer"
        >
          <div class="text-gray-700 dark:text-gray-300" itemprop="text" v-html="item.answer"></div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useSEO } from '@/composables/useSEO'

interface FAQItem {
  question: string
  answer: string
}

interface Props {
  items: FAQItem[]
  title?: string
  id?: string
  openFirst?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Questions fréquentes',
  id: 'faq',
  openFirst: false
})

const openItems = ref<number[]>([])

// Ouvrir le premier item par défaut si demandé
onMounted(() => {
  if (props.openFirst && props.items.length > 0) {
    openItems.value = [0]
  }
  generateFAQStructuredData()
})

function toggleItem(index: number) {
  if (openItems.value.includes(index)) {
    openItems.value = openItems.value.filter(i => i !== index)
  } else {
    openItems.value.push(index)
  }
}

/**
 * Génère les données structurées Schema.org FAQPage
 */
function generateFAQStructuredData() {
  if (typeof document === 'undefined') return

  const siteUrl = window.location.origin
  const faqUrl = `${siteUrl}${window.location.pathname}#${props.id}`

  const faqData = {
    '@context': 'https://schema.org',
    '@type': 'FAQPage',
    '@id': faqUrl,
    mainEntity: props.items.map((item, index) => ({
      '@type': 'Question',
      '@id': `${faqUrl}#question-${index}`,
      name: item.question,
      acceptedAnswer: {
        '@type': 'Answer',
        '@id': `${faqUrl}#answer-${index}`,
        text: item.answer.replace(/<[^>]*>/g, '') // Retirer les balises HTML pour le texte brut
      }
    }))
  }

  // Supprimer l'ancien script FAQ s'il existe
  const existingScript = document.querySelector('script[data-faq-structured-data]')
  if (existingScript) {
    existingScript.remove()
  }

  // Ajouter le nouveau script
  const script = document.createElement('script')
  script.type = 'application/ld+json'
  script.setAttribute('data-faq-structured-data', 'true')
  script.textContent = JSON.stringify(faqData, null, 2)
  document.head.appendChild(script)
}

// Régénérer les données structurées si les items changent
watch(() => props.items, () => {
  generateFAQStructuredData()
}, { deep: true })
</script>

<style scoped>
.faq-section {
  @apply py-8;
}

.faq-item {
  @apply transition-all duration-200;
}

.faq-item:hover {
  @apply shadow-md;
}
</style>

