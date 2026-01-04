<template>
  <nav 
    class="flex items-center gap-2 text-sm text-gray-600 mb-4"
    aria-label="Breadcrumb"
  >
    <ol class="flex items-center gap-2" itemscope itemtype="https://schema.org/BreadcrumbList">
      <li itemprop="itemListElement" itemscope itemtype="https://schema.org/ListItem">
        <router-link
          to="/"
          class="hover:text-gray-900 transition-colors"
          itemprop="item"
        >
          <span itemprop="name">Accueil</span>
        </router-link>
        <meta itemprop="position" content="1" />
      </li>
      <span v-if="items.length > 0" class="text-gray-400">›</span>
      <template v-for="(item, index) in items">
        <li 
          v-if="item.to && index < items.length - 1"
          :key="`link-${index}`"
          itemprop="itemListElement" 
          itemscope 
          itemtype="https://schema.org/ListItem"
        >
          <router-link
            :to="item.to"
            class="hover:text-gray-900 transition-colors"
            itemprop="item"
          >
            <span itemprop="name">{{ item.label }}</span>
          </router-link>
          <meta itemprop="position" :content="String(index + 2)" />
        </li>
        <li 
          v-else
          :key="`text-${index}`"
          itemprop="itemListElement" 
          itemscope 
          itemtype="https://schema.org/ListItem"
          class="text-gray-900 font-medium"
        >
          <span itemprop="name">{{ item.label }}</span>
          <meta itemprop="position" :content="String(index + 2)" />
        </li>
        <span v-if="index < items.length - 1" :key="`separator-${index}`" class="text-gray-400">›</span>
      </template>
    </ol>
  </nav>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, watch } from 'vue'
import { RouterLink } from 'vue-router'

const props = defineProps<{
  items: Array<{
    label: string
    to?: string
  }>
}>()

const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'

// Générer les données structurées JSON-LD pour les breadcrumbs
const structuredData = computed(() => {
  const breadcrumbList = {
    '@context': 'https://schema.org',
    '@type': 'BreadcrumbList',
    itemListElement: [
      {
        '@type': 'ListItem',
        position: 1,
        name: 'Accueil',
        item: siteUrl
      },
      ...props.items.map((item, index) => ({
        '@type': 'ListItem',
        position: index + 2,
        name: item.label,
        item: item.to ? `${siteUrl}${item.to}` : undefined
      })).filter(item => item.item) // Filtrer les items sans URL
    ]
  }
  
  return breadcrumbList
})

// Injecter le script JSON-LD dans le head
let scriptElement: HTMLScriptElement | null = null

// Fonction pour injecter/mettre à jour le script JSON-LD
const injectStructuredData = () => {
  if (typeof document === 'undefined' || !structuredData.value) return
  
  // Supprimer l'ancien script s'il existe
  const existing = document.querySelector('script[data-breadcrumb-ld]')
  if (existing) {
    existing.remove()
    scriptElement = null
  }
  
  // Créer et ajouter le nouveau script
  scriptElement = document.createElement('script')
  scriptElement.type = 'application/ld+json'
  scriptElement.setAttribute('data-breadcrumb-ld', 'true')
  scriptElement.textContent = JSON.stringify(structuredData.value, null, 2)
  document.head.appendChild(scriptElement)
}

onMounted(() => {
  injectStructuredData()
  
  // Surveiller les changements de structuredData
  watch(structuredData, () => {
    injectStructuredData()
  }, { deep: true })
})

onUnmounted(() => {
  if (scriptElement && typeof document !== 'undefined') {
    scriptElement.remove()
    scriptElement = null
  }
})
</script>

