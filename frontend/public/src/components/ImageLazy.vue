<template>
  <div
    ref="containerRef"
    class="relative overflow-hidden"
    :class="containerClass"
    :style="containerStyle"
  >
    <!-- Placeholder pendant le chargement -->
    <div
      v-if="!isLoaded && !hasError"
      class="absolute inset-0 bg-gray-200 animate-pulse flex items-center justify-center"
      :class="placeholderClass"
    >
      <svg
        v-if="showPlaceholderIcon"
        class="w-12 h-12 text-gray-400"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
        />
      </svg>
    </div>

    <!-- Image -->
    <img
      v-if="shouldLoad"
      :src="src"
      :srcset="srcset"
      :sizes="sizes"
      :alt="alt"
      :class="[
        'transition-opacity duration-300',
        isLoaded ? 'opacity-100' : 'opacity-0',
        imgClass
      ]"
      :style="imgStyle"
      loading="lazy"
      decoding="async"
      @load="handleLoad"
      @error="handleError"
    />

    <!-- Erreur -->
    <div
      v-if="hasError"
      class="absolute inset-0 bg-gray-100 flex items-center justify-center"
      :class="errorClass"
    >
      <div class="text-center p-4">
        <svg
          class="w-12 h-12 text-gray-400 mx-auto mb-2"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
          />
        </svg>
        <p class="text-sm text-gray-500">Image non disponible</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

interface Props {
  src: string
  alt: string
  srcset?: string
  sizes?: string
  containerClass?: string
  containerStyle?: string | Record<string, string>
  imgClass?: string
  imgStyle?: string | Record<string, string>
  placeholderClass?: string
  errorClass?: string
  showPlaceholderIcon?: boolean
  rootMargin?: string
  threshold?: number
}

const props = withDefaults(defineProps<Props>(), {
  sizes: '(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw',
  rootMargin: '50px',
  threshold: 0.01,
  showPlaceholderIcon: true,
})

const emit = defineEmits<{
  load: [event: Event]
  error: [event: Error]
}>()

const containerRef = ref<HTMLElement | null>(null)
const isLoaded = ref(false)
const hasError = ref(false)
const shouldLoad = ref(false)
let observer: IntersectionObserver | null = null

// Générer srcset si non fourni
const srcset = computed(() => {
  if (props.srcset) return props.srcset
  
  // Générer des tailles multiples pour le responsive
  const sizes = [400, 800, 1200, 1920]
  const separator = props.src.includes('?') ? '&' : '?'
  
  return sizes
    .map(size => `${props.src}${separator}w=${size} ${size}w`)
    .join(', ')
})

onMounted(() => {
  if (!containerRef.value) return

  // Utiliser Intersection Observer pour le lazy loading
  if ('IntersectionObserver' in window) {
    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            shouldLoad.value = true
            observer?.unobserve(entry.target)
          }
        })
      },
      {
        rootMargin: props.rootMargin,
        threshold: props.threshold,
      }
    )

    observer.observe(containerRef.value)
  } else {
    // Fallback pour les navigateurs plus anciens
    shouldLoad.value = true
  }
})

onUnmounted(() => {
  if (observer && containerRef.value) {
    observer.unobserve(containerRef.value)
  }
})

// Précharger l'image si elle devient visible
watch(shouldLoad, (newValue) => {
  if (newValue && !isLoaded.value && !hasError.value) {
    // L'image sera chargée automatiquement par le navigateur
    // grâce à l'attribut src
  }
})

function handleLoad(event: Event) {
  isLoaded.value = true
  emit('load', event)
}

function handleError(event: Event) {
  hasError.value = true
  const error = new Error(`Failed to load image: ${props.src}`)
  emit('error', error)
}
</script>

