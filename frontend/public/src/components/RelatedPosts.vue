<template>
  <div v-if="relatedPosts.length > 0" class="mt-12 pt-8 border-t border-gray-200">
    <h2 class="text-2xl font-bold text-gray-900 mb-6">Articles similaires</h2>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <BlogCard
        v-for="post in relatedPosts"
        :key="post.id"
        :post="post"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { blogService, type BlogPost } from '@/api/blog.service'
import BlogCard from './BlogCard.vue'

const props = defineProps<{
  category: string
  excludeId: number
}>()

const relatedPosts = ref<BlogPost[]>([])

const loadRelatedPosts = async () => {
  try {
    const result = await blogService.getPostsByCategory(props.category, 0, 3)
    relatedPosts.value = result.content.filter(post => post.id !== props.excludeId).slice(0, 3)
  } catch (err) {
    console.error('Error loading related posts:', err)
  }
}

onMounted(() => {
  loadRelatedPosts()
})
</script>

