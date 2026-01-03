import { ref, watch, type Ref } from 'vue'

/**
 * Composable pour créer une valeur debounced
 * Utile pour les champs de recherche et filtres
 */
export function useDebounce<T>(source: Ref<T>, delay: number = 500) {
  const debounced = ref<T>(source.value) as Ref<T>
  let timeoutId: ReturnType<typeof setTimeout> | null = null

  watch(source, (newValue) => {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    timeoutId = setTimeout(() => {
      debounced.value = newValue
    }, delay)
  }, { immediate: true })

  return debounced
}

