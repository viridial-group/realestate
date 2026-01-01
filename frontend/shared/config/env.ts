/**
 * Configuration des variables d'environnement pour le package shared
 * Note: Ce fichier doit être utilisé côté client uniquement
 */

// Pour le package shared, on utilise les variables d'environnement du projet parent
// ou des valeurs par défaut pour le développement local

const getApiBaseUrl = (): string => {
  // En développement, utiliser la variable d'environnement ou la valeur par défaut
  if (typeof window !== 'undefined') {
    // Côté client, on peut utiliser import.meta.env si disponible
    // Sinon, utiliser la valeur par défaut
    return '/api' // Proxy vers le gateway
  }
  // Côté serveur ou fallback
  return process.env.VITE_API_BASE_URL || 'http://localhost:8080'
}

export const env = {
  apiBaseUrl: getApiBaseUrl()
} as const

export default env

