import { ref } from 'vue'
import { publicPropertyService, type SearchSuggestions } from '@/api/public-property.service'

/**
 * Composable pour générer des suggestions de recherche intelligentes
 */
export function useSearchSuggestions() {
  const suggestions = ref<SearchSuggestions>({
    cities: [],
    types: [],
    addresses: [],
    titles: [],
    popularSearches: []
  })

  const isLoading = ref(false)

  // Suggestions statiques de fallback
  const fallbackPopularSearches = [
    'Appartement Paris',
    'Villa Côte d\'Azur',
    'Studio étudiant',
    'Maison avec jardin',
    'Appartement T3',
    'Villa piscine',
    'Studio meublé',
    'Maison familiale',
    'Appartement centre-ville',
    'Villa vue mer',
  ]

  const fallbackCitySuggestions = [
    'Paris',
    'Lyon',
    'Marseille',
    'Bordeaux',
    'Nice',
    'Toulouse',
    'Nantes',
    'Strasbourg',
    'Montpellier',
    'Lille',
  ]

  const fallbackTypeSuggestions = [
    'Appartement',
    'Villa',
    'Studio',
    'Maison',
    'Terrain',
    'Bureau',
  ]

  /**
   * Charge les suggestions depuis l'API
   */
  async function loadSuggestions(query?: string) {
    if (isLoading.value) return

    isLoading.value = true
    try {
      const data = await publicPropertyService.getSearchSuggestions(query)
      suggestions.value = data
    } catch (error) {
      console.error('Error loading suggestions:', error)
      // Utiliser les suggestions de fallback en cas d'erreur
      suggestions.value = {
        cities: fallbackCitySuggestions,
        types: fallbackTypeSuggestions,
        addresses: [],
        titles: [],
        popularSearches: fallbackPopularSearches
      }
    } finally {
      isLoading.value = false
    }
  }

  /**
   * Génère des suggestions basées sur la requête actuelle
   * Combine les données de l'API avec des suggestions intelligentes
   */
  function generateSuggestions(query: string): string[] {
    const queryLower = query.toLowerCase().trim()
    const result: string[] = []

    // Si pas de requête, retourner les recherches populaires
    if (!query || queryLower.length === 0) {
      return suggestions.value.popularSearches.length > 0 
        ? suggestions.value.popularSearches.slice(0, 5)
        : fallbackPopularSearches.slice(0, 5)
    }

    // 1. Villes qui correspondent
    suggestions.value.cities.forEach(city => {
      if (city.toLowerCase().includes(queryLower) && !result.includes(city)) {
        result.push(city)
      }
    })

    // 2. Types qui correspondent
    suggestions.value.types.forEach(type => {
      if (type.toLowerCase().includes(queryLower) && !result.includes(type)) {
        result.push(type)
      }
    })

    // 3. Adresses qui correspondent
    suggestions.value.addresses.forEach(address => {
      if (address.toLowerCase().includes(queryLower) && !result.includes(address)) {
        result.push(address)
      }
    })

    // 4. Titres qui correspondent
    suggestions.value.titles.forEach(title => {
      if (title.toLowerCase().includes(queryLower) && !result.includes(title)) {
        result.push(title)
      }
    })

    // 5. Recherches populaires qui contiennent la requête
    suggestions.value.popularSearches.forEach(popular => {
      if (popular.toLowerCase().includes(queryLower) && !result.includes(popular)) {
        result.push(popular)
      }
    })

    // 6. Combinaisons intelligentes basées sur les données réelles
    if (queryLower.length >= 2) {
      // Si la requête ressemble à une ville, suggérer des types avec cette ville
      const matchingCity = suggestions.value.cities.find(city => 
        city.toLowerCase().startsWith(queryLower) || queryLower.includes(city.toLowerCase())
      )
      
      if (matchingCity) {
        suggestions.value.types.forEach(type => {
          const suggestion = `${type} ${matchingCity}`
          if (!result.includes(suggestion)) {
            result.push(suggestion)
          }
        })
      }

      // Si la requête ressemble à un type, suggérer des villes avec ce type
      const matchingType = suggestions.value.types.find(type => 
        type.toLowerCase().startsWith(queryLower) || queryLower.includes(type.toLowerCase())
      )
      
      if (matchingType) {
        suggestions.value.cities.forEach(city => {
          const suggestion = `${matchingType} ${city}`
          if (!result.includes(suggestion)) {
            result.push(suggestion)
          }
        })
      }
    }

    // 7. Suggestions avec guillemets pour recherche exacte
    if (queryLower.length >= 3 && !queryLower.startsWith('"')) {
      result.push(`"${query}"`)
    }

    // Limiter à 10 suggestions
    return result.slice(0, 10)
  }

  /**
   * Suggestions de recherche avancée
   */
  function getAdvancedSearchTips(): string[] {
    return [
      'Utilisez "phrase exacte" pour rechercher une phrase précise',
      'Utilisez -mot pour exclure un mot de la recherche',
      'Exemple: "appartement paris" -studio',
      'Recherchez plusieurs mots: appartement paris centre',
    ]
  }

  return {
    suggestions,
    isLoading,
    loadSuggestions,
    generateSuggestions,
    getAdvancedSearchTips,
  }
}

