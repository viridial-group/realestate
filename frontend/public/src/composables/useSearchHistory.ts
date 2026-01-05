import { ref } from 'vue'

const MAX_HISTORY_ITEMS = 10
const STORAGE_KEY = 'realestate_search_history'

/**
 * Composable pour gérer l'historique de recherche
 */
export interface SearchHistoryEntry {
  query: string
  timestamp: number
  resultCount?: number
}

export function useSearchHistory() {
  const history = ref<(string | SearchHistoryEntry)[]>([])

  // Charger l'historique depuis localStorage au montage
  function loadHistory() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored) {
        const parsed = JSON.parse(stored)
        // Migration: convertir les anciennes entrées string en objets
        history.value = parsed.map((item: string | SearchHistoryEntry) => {
          if (typeof item === 'string') {
            return {
              query: item,
              timestamp: Date.now(),
            }
          }
          return item
        })
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

  // Ajouter une recherche à l'historique avec métadonnées
  function addToHistory(search: string, metadata?: { timestamp?: number; resultCount?: number }) {
    if (!search || !search.trim()) return

    const trimmed = search.trim()
    
    // Retirer si déjà présent
    history.value = history.value.filter(item => {
      const itemStr = typeof item === 'string' ? item : item.query
      return itemStr.toLowerCase() !== trimmed.toLowerCase()
    })
    
    // Créer l'entrée avec métadonnées
    const entry = {
      query: trimmed,
      timestamp: metadata?.timestamp || Date.now(),
      resultCount: metadata?.resultCount,
    }
    
    // Ajouter au début
    history.value.unshift(entry)
    
    // Limiter à MAX_HISTORY_ITEMS
    if (history.value.length > MAX_HISTORY_ITEMS) {
      history.value = history.value.slice(0, MAX_HISTORY_ITEMS)
    }
    
    saveHistory()
  }

  // Supprimer un élément de l'historique
  function removeFromHistory(search: string) {
    history.value = history.value.filter(item => {
      const itemStr = typeof item === 'string' ? item : item.query
      return itemStr !== search
    })
    saveHistory()
  }

  // Obtenir les recherches récentes (7 derniers jours)
  function getRecentSearches(days: number = 7): SearchHistoryEntry[] {
    const cutoff = Date.now() - (days * 24 * 60 * 60 * 1000)
    return history.value
      .filter(item => {
        const entry = typeof item === 'string' ? { query: item, timestamp: Date.now() } : item
        return entry.timestamp >= cutoff
      })
      .map(item => typeof item === 'string' ? { query: item, timestamp: Date.now() } : item)
      .slice(0, 5)
  }

  // Obtenir les recherches les plus fréquentes
  function getFrequentSearches(limit: number = 5): string[] {
    const counts = new Map<string, number>()
    history.value.forEach(item => {
      const query = typeof item === 'string' ? item : item.query
      counts.set(query, (counts.get(query) || 0) + 1)
    })
    return Array.from(counts.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, limit)
      .map(([query]) => query)
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
    getRecentSearches,
    getFrequentSearches,
  }
}

