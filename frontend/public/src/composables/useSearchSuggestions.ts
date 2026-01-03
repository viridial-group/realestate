import { ref, computed } from 'vue'

/**
 * Composable pour générer des suggestions de recherche intelligentes
 */
export function useSearchSuggestions() {
  const popularSearches = ref([
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
  ])

  const citySuggestions = ref([
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
  ])

  const typeSuggestions = ref([
    'Appartement',
    'Villa',
    'Studio',
    'Maison',
    'Terrain',
    'Bureau',
  ])

  /**
   * Génère des suggestions basées sur la requête actuelle
   */
  function generateSuggestions(query: string): string[] {
    if (!query || query.trim().length === 0) {
      return popularSearches.value.slice(0, 5)
    }

    const queryLower = query.toLowerCase().trim()
    const suggestions: string[] = []

    // 1. Suggestions de villes qui commencent par la requête
    citySuggestions.value.forEach(city => {
      if (city.toLowerCase().startsWith(queryLower)) {
        suggestions.push(city)
      }
    })

    // 2. Suggestions de types qui commencent par la requête
    typeSuggestions.value.forEach(type => {
      if (type.toLowerCase().startsWith(queryLower)) {
        suggestions.push(type)
      }
    })

    // 3. Recherches populaires qui contiennent la requête
    popularSearches.value.forEach(popular => {
      if (popular.toLowerCase().includes(queryLower) && !suggestions.includes(popular)) {
        suggestions.push(popular)
      }
    })

    // 4. Combinaisons intelligentes
    if (queryLower.length >= 2) {
      // Si la requête ressemble à une ville, suggérer des types avec cette ville
      const matchingCity = citySuggestions.value.find(city => 
        city.toLowerCase().startsWith(queryLower) || queryLower.includes(city.toLowerCase())
      )
      
      if (matchingCity) {
        typeSuggestions.value.forEach(type => {
          const suggestion = `${type} ${matchingCity}`
          if (!suggestions.includes(suggestion)) {
            suggestions.push(suggestion)
          }
        })
      }

      // Si la requête ressemble à un type, suggérer des villes avec ce type
      const matchingType = typeSuggestions.value.find(type => 
        type.toLowerCase().startsWith(queryLower) || queryLower.includes(type.toLowerCase())
      )
      
      if (matchingType) {
        citySuggestions.value.forEach(city => {
          const suggestion = `${matchingType} ${city}`
          if (!suggestions.includes(suggestion)) {
            suggestions.push(suggestion)
          }
        })
      }
    }

    // 5. Suggestions avec guillemets pour recherche exacte
    if (queryLower.length >= 3 && !queryLower.startsWith('"')) {
      suggestions.push(`"${query}"`)
    }

    // Limiter à 8 suggestions
    return suggestions.slice(0, 8)
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
    popularSearches,
    citySuggestions,
    typeSuggestions,
    generateSuggestions,
    getAdvancedSearchTips,
  }
}

