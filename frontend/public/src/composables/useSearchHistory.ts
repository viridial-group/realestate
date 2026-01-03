import { ref, watch } from 'vue'

const MAX_HISTORY_ITEMS = 10
const STORAGE_KEY = 'realestate_search_history'

/**
 * Composable pour gérer l'historique de recherche
 */
export function useSearchHistory() {
  const history = ref<string[]>([])

  // Charger l'historique depuis localStorage au montage
  function loadHistory() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored) {
        history.value = JSON.parse(stored)
      }
    } catch (error) {
      console.error('Error loading search history:', error)
      history.value = []
    }
  }

  // Sauvegarder l'historique dans localStorage
  function saveHistory() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(history.value))
    } catch (error) {
      console.error('Error saving search history:', error)
    }
  }

  // Ajouter une recherche à l'historique
  function addToHistory(search: string) {
    if (!search || !search.trim()) return

    const trimmed = search.trim()
    
    // Retirer si déjà présent
    history.value = history.value.filter(item => item.toLowerCase() !== trimmed.toLowerCase())
    
    // Ajouter au début
    history.value.unshift(trimmed)
    
    // Limiter à MAX_HISTORY_ITEMS
    if (history.value.length > MAX_HISTORY_ITEMS) {
      history.value = history.value.slice(0, MAX_HISTORY_ITEMS)
    }
    
    saveHistory()
  }

  // Supprimer un élément de l'historique
  function removeFromHistory(search: string) {
    history.value = history.value.filter(item => item !== search)
    saveHistory()
  }

  // Vider l'historique
  function clearHistory() {
    history.value = []
    saveHistory()
  }

  // Charger l'historique au montage
  loadHistory()

  return {
    history,
    addToHistory,
    removeFromHistory,
    clearHistory,
    loadHistory,
  }
}

