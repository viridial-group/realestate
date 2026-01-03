import { ref, computed } from 'vue'

const STORAGE_KEY = 'realestate_comparison'
const MAX_COMPARISON = 4 // Maximum 4 propriétés à comparer

/**
 * Composable pour gérer la comparaison de propriétés (localStorage)
 */
export function useComparison() {
  const comparison = ref<number[]>([])

  // Charger la comparaison depuis localStorage
  function loadComparison() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored) {
        comparison.value = JSON.parse(stored)
      }
    } catch (error) {
      console.error('Error loading comparison:', error)
      comparison.value = []
    }
  }

  // Sauvegarder la comparaison dans localStorage
  function saveComparison() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(comparison.value))
    } catch (error) {
      console.error('Error saving comparison:', error)
    }
  }

  // Ajouter une propriété à la comparaison
  function addToComparison(propertyId: number): boolean {
    if (comparison.value.includes(propertyId)) {
      return false // Déjà dans la comparaison
    }

    if (comparison.value.length >= MAX_COMPARISON) {
      return false // Limite atteinte
    }

    comparison.value.push(propertyId)
    saveComparison()
    return true
  }

  // Retirer une propriété de la comparaison
  function removeFromComparison(propertyId: number): boolean {
    const index = comparison.value.indexOf(propertyId)
    if (index > -1) {
      comparison.value.splice(index, 1)
      saveComparison()
      return true
    }
    return false
  }

  // Toggle comparaison
  function toggleComparison(propertyId: number): boolean {
    if (isInComparison(propertyId)) {
      removeFromComparison(propertyId)
      return false
    } else {
      return addToComparison(propertyId)
    }
  }

  // Vérifier si une propriété est en comparaison
  function isInComparison(propertyId: number): boolean {
    return comparison.value.includes(propertyId)
  }

  // Vider la comparaison
  function clearComparison() {
    comparison.value = []
    saveComparison()
  }

  // Obtenir le nombre de propriétés en comparaison
  const comparisonCount = computed(() => comparison.value.length)

  // Vérifier si la limite est atteinte
  const isFull = computed(() => comparison.value.length >= MAX_COMPARISON)

  // Charger au montage
  loadComparison()

  return {
    comparison,
    comparisonCount,
    isFull,
    addToComparison,
    removeFromComparison,
    toggleComparison,
    isInComparison,
    clearComparison,
    loadComparison,
  }
}

