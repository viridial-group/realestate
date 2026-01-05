<template>
  <div v-if="loading" class="text-center py-12">
    <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
  </div>

  <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
    <p class="text-red-800">{{ error }}</p>
  </div>

  <article v-else-if="post" class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Breadcrumbs -->
    <Breadcrumbs :items="breadcrumbItems" />

    <!-- Header -->
    <header class="mb-8">
      <div v-if="post.category" class="mb-4">
        <span class="inline-block px-4 py-2 rounded-full text-sm font-semibold bg-blue-100 text-blue-800">
          {{ post.category }}
        </span>
      </div>
      
      <h1 class="text-4xl font-bold text-gray-900 mb-4">{{ post.title }}</h1>
      
      <div class="flex items-center gap-4 text-sm text-gray-600 mb-6">
        <span v-if="post.authorName">Par {{ post.authorName }}</span>
        <span v-if="post.publishedAt">
          {{ formatDate(post.publishedAt) }}
        </span>
        <span>👁️ {{ post.viewCount || 0 }} vues</span>
      </div>

      <!-- Image principale -->
      <div v-if="post.featuredImage" class="mb-8">
        <ImageOptimized
          :src="post.featuredImage"
          :alt="post.title"
          :sizes="'100vw'"
          loading="eager"
          fetchpriority="high"
          img-class="w-full h-auto rounded-lg"
        />
      </div>
    </header>

    <!-- Contenu -->
    <div class="prose prose-lg max-w-none mb-8">
      <div v-html="post.content"></div>
    </div>

    <!-- Tags -->
    <div v-if="post.tags" class="mb-8 pt-8 border-t border-gray-200">
      <h3 class="text-sm font-semibold text-gray-700 mb-3">Tags</h3>
      <div class="flex flex-wrap gap-2">
        <router-link
          v-for="tag in getTags(post.tags)"
          :key="tag"
          :to="`/blog/tag/${tag}`"
          class="px-3 py-1 rounded-full text-sm bg-gray-100 text-gray-700 hover:bg-gray-200 transition-colors"
        >
          #{{ tag }}
        </router-link>
      </div>
    </div>

    <!-- Articles similaires -->
    <RelatedPosts v-if="post.category" :category="post.category" :exclude-id="post.id" />
  </article>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { blogService, type BlogPost } from '@/api/blog.service'
import Breadcrumbs from '@/components/Breadcrumbs.vue'
import ImageOptimized from '@/components/ImageOptimized.vue'
import RelatedPosts from '@/components/RelatedPosts.vue'
import { useSEO } from '@/composables/useSEO'

const route = useRoute()
// const router = useRouter()

const post = ref<BlogPost | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const breadcrumbItems = computed(() => [
  { label: 'Blog', to: '/blog' },
  { label: post.value?.title || 'Article', to: undefined }
])

const loadPost = async () => {
  const slug = route.params.slug as string
  if (!slug) {
    error.value = 'Slug manquant'
    return
  }

  loading.value = true
  error.value = null

  try {
    post.value = await blogService.getPostBySlug(slug)
    
    // SEO dynamique
    useSEO({
      title: post.value.title,
      description: post.value.metaDescription || post.value.excerpt || post.value.title,
      keywords: post.value.metaKeywords?.split(',').map(k => k.trim()) || [],
      image: post.value.ogImage || post.value.featuredImage,
      url: `${window.location.origin}/blog/${post.value.slug}`,
      type: 'article',
      canonical: `${window.location.origin}/blog/${post.value.slug}`
    })
  } catch (err: any) {
    error.value = err.response?.status === 404 
      ? 'Article non trouvé' 
      : err.message || 'Erreur lors du chargement de l\'article'
    console.error('Error loading blog post:', err)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getTags = (tagsString?: string): string[] => {
  if (!tagsString) return []
  return tagsString.split(',').map(tag => tag.trim()).filter(Boolean)
}

onMounted(() => {
  loadPost()
})
</script>

<style scoped>
.prose {
  @apply text-gray-700;
}

.prose h2 {
  @apply text-2xl font-bold text-gray-900 mt-8 mb-4;
}

.prose h3 {
  @apply text-xl font-semibold text-gray-900 mt-6 mb-3;
}

.prose p {
  @apply mb-4 leading-relaxed;
}

.prose ul, .prose ol {
  @apply mb-4 ml-6;
}

.prose li {
  @apply mb-2;
}

.prose a {
  @apply text-blue-600 hover:text-blue-800 underline;
}

.prose img {
  @apply rounded-lg my-6;
}
</style>

