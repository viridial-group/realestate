import { ref, computed, watch } from 'vue'

const STORAGE_KEY = 'realestate_favorites'
const MAX_FAVORITES = 50

/**
 * Composable pour gérer les favoris (localStorage)
 */
export function useFavorites() {
  const favorites = ref<number[]>([])

  // Charger les favoris depuis localStorage
  function loadFavorites() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored) {
        favorites.value = JSON.parse(stored)
      }
    } catch (error) {
      console.error('Error loading favorites:', error)
      favorites.value = []
    }
  }

  // Sauvegarder les favoris dans localStorage
  function saveFavorites() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(favorites.value))
    } catch (error) {
      console.error('Error saving favorites:', error)
    }
  }

  // Ajouter un favori
  function addFavorite(propertyId: number) {
    if (favorites.value.includes(propertyId)) {
      return false // Déjà dans les favoris
    }

    if (favorites.value.length >= MAX_FAVORITES) {
      // Retirer le plus ancien
      favorites.value.shift()
    }

    favorites.value.push(propertyId)
    saveFavorites()
    return true
  }

  // Retirer un favori
  function removeFavorite(propertyId: number) {
    const index = favorites.value.indexOf(propertyId)
    if (index > -1) {
      favorites.value.splice(index, 1)
      saveFavorites()
      return true
    }
    return false
  }

  // Toggle favori
  function toggleFavorite(propertyId: number) {
    if (isFavorite(propertyId)) {
      removeFavorite(propertyId)
      return false
    } else {
      addFavorite(propertyId)
      return true
    }
  }

  // Vérifier si un bien est en favori
  function isFavorite(propertyId: number): boolean {
    return favorites.value.includes(propertyId)
  }

  // Vider les favoris
  function clearFavorites() {
    favorites.value = []
    saveFavorites()
  }

  // Obtenir le nombre de favoris
  const favoritesCount = computed(() => favorites.value.length)

  // Charger au montage
  loadFavorites()

  // Sauvegarder automatiquement quand les favoris changent
  watch(favorites, () => {
    saveFavorites()
  }, { deep: true })

  return {
    favorites,
    favoritesCount,
    addFavorite,
    removeFavorite,
    toggleFavorite,
    isFavorite,
    clearFavorites,
    loadFavorites,
  }
}

