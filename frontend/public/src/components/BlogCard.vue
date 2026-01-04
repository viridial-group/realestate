<template>
  <article
    class="bg-white rounded-lg shadow-md hover:shadow-lg transition-all duration-200 overflow-hidden flex flex-col h-full cursor-pointer"
    @click="goToPost"
  >
    <!-- Image -->
    <div v-if="post.featuredImage" class="w-full h-48 flex-shrink-0 relative">
      <ImageOptimized
        :src="post.featuredImage"
        :alt="post.title"
        :sizes="'(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw'"
        loading="lazy"
        img-class="w-full h-full object-cover"
      />
    </div>
    <div v-else class="w-full h-48 flex-shrink-0 bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
      <span class="text-white text-4xl">📝</span>
    </div>

    <!-- Contenu -->
    <div class="flex-1 p-6 flex flex-col">
      <!-- Catégorie -->
      <div v-if="post.category" class="mb-2">
        <span class="inline-block px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-800">
          {{ post.category }}
        </span>
      </div>

      <!-- Titre -->
      <h2 class="text-xl font-bold text-gray-900 mb-3 line-clamp-2">
        {{ post.title }}
      </h2>

      <!-- Excerpt -->
      <p v-if="post.excerpt" class="text-gray-600 text-sm mb-4 line-clamp-3 flex-1">
        {{ post.excerpt }}
      </p>

      <!-- Métadonnées -->
      <div class="mt-auto pt-4 border-t border-gray-100 flex items-center justify-between text-xs text-gray-500">
        <div class="flex items-center gap-2">
          <span v-if="post.authorName">{{ post.authorName }}</span>
          <span v-if="post.publishedAt">
            {{ formatDate(post.publishedAt) }}
          </span>
        </div>
        <div class="flex items-center gap-1">
          <span>👁️</span>
          <span>{{ post.viewCount || 0 }}</span>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { BlogPost } from '@/api/blog.service'
import ImageOptimized from './ImageOptimized.vue'

const props = defineProps<{
  post: BlogPost
}>()

const router = useRouter()

const goToPost = () => {
  router.push(`/blog/${props.post.slug}`)
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

