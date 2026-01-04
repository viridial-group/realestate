<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-4xl font-bold text-gray-900 mb-4">Blog Immobilier</h1>
      <p class="text-lg text-gray-600">
        Découvrez nos guides, conseils et actualités sur l'immobilier
      </p>
    </div>

    <!-- Filtres -->
    <div class="mb-6 flex flex-wrap gap-4">
      <button
        v-for="category in categories"
        :key="category"
        @click="selectedCategory = category"
        :class="[
          'px-4 py-2 rounded-lg transition-colors',
          selectedCategory === category
            ? 'bg-blue-600 text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
        ]"
      >
        {{ category }}
      </button>
      <button
        v-if="selectedCategory"
        @click="selectedCategory = null"
        class="px-4 py-2 rounded-lg bg-gray-100 text-gray-700 hover:bg-gray-200"
      >
        Tous
      </button>
    </div>

    <!-- Liste des articles -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
    </div>

    <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
      <p class="text-red-800">{{ error }}</p>
    </div>

    <div v-else>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <BlogCard
          v-for="post in posts"
          :key="post.id"
          :post="post"
        />
      </div>

      <!-- Pagination -->
      <div v-if="pagination && pagination.totalPages > 1" class="flex justify-center gap-2">
        <button
          @click="goToPage(pagination.number - 1)"
          :disabled="pagination.first"
          class="px-4 py-2 rounded-lg bg-gray-100 text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200"
        >
          Précédent
        </button>
        <span class="px-4 py-2 text-gray-700">
          Page {{ pagination.number + 1 }} sur {{ pagination.totalPages }}
        </span>
        <button
          @click="goToPage(pagination.number + 1)"
          :disabled="pagination.last"
          class="px-4 py-2 rounded-lg bg-gray-100 text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200"
        >
          Suivant
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { blogService, type BlogPost, type BlogPostPage } from '@/api/blog.service'
import BlogCard from '@/components/BlogCard.vue'
import { useSEO } from '@/composables/useSEO'

const route = useRoute()
const posts = ref<BlogPost[]>([])
const categories = ref<string[]>([])
const selectedCategory = ref<string | null>(route.params.category as string || null)
const selectedTag = ref<string | null>(route.params.tag as string || null)
const loading = ref(false)
const error = ref<string | null>(null)
const pagination = ref<{
  number: number
  totalPages: number
  totalElements: number
  first: boolean
  last: boolean
} | null>(null)

const currentPage = ref(0)
const pageSize = 12

// SEO dynamique selon la catégorie/tag
const seoTitle = computed(() => {
  if (selectedCategory.value) {
    return `Blog ${selectedCategory.value} - Guides Immobilier`
  }
  if (selectedTag.value) {
    return `Articles tagués "${selectedTag.value}" - Blog Immobilier`
  }
  return 'Blog Immobilier - Guides et Conseils'
})

useSEO({
  title: seoTitle.value,
  description: 'Découvrez nos guides, conseils et actualités sur l\'immobilier. Achat, vente, location : tous nos articles pour vous aider dans vos projets immobiliers.',
  keywords: ['blog immobilier', 'guides immobilier', 'conseils achat', 'conseils location', 'actualités immobilier'],
  type: 'website'
})

const loadPosts = async () => {
  loading.value = true
  error.value = null
  
  try {
    let result: BlogPostPage
    
    if (selectedCategory.value) {
      result = await blogService.getPostsByCategory(selectedCategory.value, currentPage.value, pageSize)
    } else if (selectedTag.value) {
      result = await blogService.getPostsByTag(selectedTag.value, currentPage.value, pageSize)
    } else {
      result = await blogService.getPublishedPosts(currentPage.value, pageSize)
    }
    
    posts.value = result.content
    pagination.value = {
      number: result.number,
      totalPages: result.totalPages,
      totalElements: result.totalElements,
      first: result.first,
      last: result.last
    }
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des articles'
    console.error('Error loading blog posts:', err)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    categories.value = await blogService.getCategories()
  } catch (err) {
    console.error('Error loading categories:', err)
  }
}

const goToPage = (page: number) => {
  currentPage.value = page
  loadPosts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  loadPosts()
  loadCategories()
})

watch([selectedCategory, selectedTag], () => {
  currentPage.value = 0
  loadPosts()
})
</script>

