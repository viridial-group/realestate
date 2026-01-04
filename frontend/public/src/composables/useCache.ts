/**
 * Composable pour gérer le cache des requêtes API
 */

interface CacheEntry<T> {
  data: T
  timestamp: number
  ttl: number // Time to live en millisecondes
}

const cache = new Map<string, CacheEntry<any>>()

const DEFAULT_TTL = 5 * 60 * 1000 // 5 minutes par défaut

// Exposer le Map pour l'invalidation par pattern
export const cacheMap = cache

/**
 * Composable pour gérer le cache des requêtes API
 */
export function useCache() {
  /**
   * Obtenir une valeur du cache
   */
  function get<T>(key: string): T | null {
    const entry = cache.get(key)
    
    if (!entry) {
      return null
    }
    
    // Vérifier si l'entrée a expiré
    const now = Date.now()
    if (now - entry.timestamp > entry.ttl) {
      cache.delete(key)
      return null
    }
    
    return entry.data as T
  }
  
  /**
   * Mettre une valeur dans le cache
   */
  function set<T>(key: string, data: T, ttl: number = DEFAULT_TTL): void {
    cache.set(key, {
      data,
      timestamp: Date.now(),
      ttl,
    })
  }
  
  /**
   * Vérifier si une clé existe dans le cache (et n'est pas expirée)
   */
  function has(key: string): boolean {
    const entry = cache.get(key)
    
    if (!entry) {
      return false
    }
    
    const now = Date.now()
    if (now - entry.timestamp > entry.ttl) {
      cache.delete(key)
      return false
    }
    
    return true
  }
  
  /**
   * Supprimer une entrée du cache
   */
  function remove(key: string): void {
    cache.delete(key)
  }
  
  /**
   * Supprimer toutes les entrées du cache
   */
  function clear(): void {
    cache.clear()
  }
  
  /**
   * Supprimer les entrées expirées
   */
  function cleanExpired(): void {
    const now = Date.now()
    for (const [key, entry] of cache.entries()) {
      if (now - entry.timestamp > entry.ttl) {
        cache.delete(key)
      }
    }
  }
  
  /**
   * Obtenir la taille du cache
   */
  function size(): number {
    return cache.size
  }
  
  /**
   * Obtenir toutes les clés du cache
   */
  function keys(): string[] {
    return Array.from(cache.keys())
  }
  
  /**
   * Invalider les clés correspondant à un pattern
   */
  function invalidatePattern(pattern: string | RegExp): number {
    let count = 0
    const regex = typeof pattern === 'string' ? new RegExp(pattern) : pattern
    
    for (const key of cache.keys()) {
      if (regex.test(key)) {
        cache.delete(key)
        count++
      }
    }
    
    return count
  }
  
  /**
   * Wrapper pour une fonction async avec cache
   */
  async function cached<T>(
    key: string,
    fetcher: () => Promise<T>,
    ttl?: number
  ): Promise<T> {
    // Vérifier le cache
    const cached = get<T>(key)
    if (cached !== null) {
      return cached
    }
    
    // Récupérer les données
    const data = await fetcher()
    
    // Mettre en cache
    set(key, data, ttl)
    
    return data
  }
  
  // Nettoyer les entrées expirées toutes les 10 minutes
  if (typeof window !== 'undefined') {
    setInterval(() => {
      cleanExpired()
    }, 10 * 60 * 1000)
  }
  
  return {
    get,
    set,
    has,
    remove,
    clear,
    cleanExpired,
    size,
    keys,
    invalidatePattern,
    cached,
  }
}

/**
 * Générer une clé de cache à partir de paramètres
 */
export function generateCacheKey(prefix: string, params: Record<string, any>): string {
  const sortedParams = Object.keys(params)
    .sort()
    .map(key => `${key}=${JSON.stringify(params[key])}`)
    .join('&')
  
  return `${prefix}:${sortedParams}`
}

