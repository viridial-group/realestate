<template>
  <picture class="block">
    <!-- Source WebP pour les navigateurs qui le supportent -->
    <source
      v-if="webpUrl"
      :srcset="generateSrcSet(webpUrl)"
      :sizes="sizes"
      type="image/webp"
    />
    <!-- Image de fallback -->
    <img
      :src="src"
      :srcset="generateSrcSet(src)"
      :sizes="sizes"
      :alt="alt"
      :loading="loading"
      :decoding="decoding"
      :class="imgClass"
      :style="imgStyle"
      @load="onLoad"
      @error="onError"
    />
  </picture>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { preloadPropertyHeroImage } from '@/composables/useImagePreload'

interface Props {
  src: string
  alt: string
  webpUrl?: string
  width?: number
  height?: number
  sizes?: string
  loading?: 'lazy' | 'eager' | 'auto'
  decoding?: 'async' | 'auto' | 'sync'
  imgClass?: string
  imgStyle?: string | Record<string, string>
  aspectRatio?: string
}

const props = withDefaults(defineProps<Props>(), {
  loading: 'lazy',
  decoding: 'async',
  sizes: '(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw',
  aspectRatio: undefined,
  imgClass: '',
  imgStyle: undefined
})

const emit = defineEmits<{
  load: [event: Event]
  error: [event: Event]
}>()

const isLoaded = ref(false)
const hasError = ref(false)

/**
 * Génère un srcset avec différentes tailles pour le responsive
 * Format: url?w=400 400w, url?w=800 800w, url?w=1200 1200w
 */
const generateSrcSet = (baseUrl: string): string => {
  // Si l'URL contient déjà des paramètres, utiliser ceux-ci
  // Sinon, générer des tailles multiples
  const sizes = [400, 800, 1200, 1920]
  const separator = baseUrl.includes('?') ? '&' : '?'
  
  return sizes
    .map(size => `${baseUrl}${separator}w=${size} ${size}w`)
    .join(', ')
}

const onLoad = (event: Event) => {
  isLoaded.value = true
  emit('load', event)
}

const onError = (event: Event) => {
  hasError.value = true
  emit('error', event)
}

const imgClass = computed(() => {
  const classes = [props.imgClass]
  if (!isLoaded.value && !hasError.value) {
    classes.push('opacity-0 transition-opacity duration-300')
  }
  if (isLoaded.value) {
    classes.push('opacity-100')
  }
  return classes.filter(Boolean).join(' ')
})

// Précharger l'image si elle est critique (loading = 'eager')
onMounted(() => {
  if (props.loading === 'eager' && props.src) {
    preloadPropertyHeroImage(props.src, props.webpUrl)
  }
})
</script>

