/**
 * Service Worker pour le cache et les performances
 * Améliore le SEO via les Core Web Vitals
 */

const CACHE_NAME = 'viridial-v1'
const STATIC_CACHE = 'viridial-static-v1'
const IMAGE_CACHE = 'viridial-images-v1'
const API_CACHE = 'viridial-api-v1'

// Assets statiques à mettre en cache immédiatement
const STATIC_ASSETS = [
  '/',
  '/index.html',
  '/manifest.json',
  '/robots.txt',
  '/sitemap.xml'
]

// Installer le service worker
self.addEventListener('install', (event) => {
  console.log('[Service Worker] Installing...]')
  
  event.waitUntil(
    caches.open(STATIC_CACHE).then((cache) => {
      console.log('[Service Worker] Caching static assets')
      return cache.addAll(STATIC_ASSETS.map(url => new Request(url, { cache: 'reload' })))
    })
  )
  
  self.skipWaiting()
})

// Activer le service worker
self.addEventListener('activate', (event) => {
  console.log('[Service Worker] Activating...]')
  
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames
          .filter((name) => {
            // Supprimer les anciens caches
            return name !== CACHE_NAME && 
                   name !== STATIC_CACHE && 
                   name !== IMAGE_CACHE && 
                   name !== API_CACHE
          })
          .map((name) => {
            console.log('[Service Worker] Deleting old cache:', name)
            return caches.delete(name)
          })
      )
    })
  )
  
  return self.clients.claim()
})

// Intercepter les requêtes
self.addEventListener('fetch', (event) => {
  const { request } = event
  const url = new URL(request.url)
  
  // Stratégie de cache selon le type de ressource
  if (request.method !== 'GET') {
    return // Ne pas mettre en cache les requêtes non-GET
  }
  
  // Images : Cache First avec fallback réseau
  if (isImageRequest(request)) {
    event.respondWith(cacheFirst(request, IMAGE_CACHE))
    return
  }
  
  // API : Network First avec fallback cache
  if (isAPIRequest(request)) {
    event.respondWith(networkFirst(request, API_CACHE))
    return
  }
  
  // Assets statiques : Cache First
  if (isStaticAsset(request)) {
    event.respondWith(cacheFirst(request, STATIC_CACHE))
    return
  }
  
  // Autres : Network First
  event.respondWith(networkFirst(request, CACHE_NAME))
})

/**
 * Stratégie Cache First : Vérifie le cache d'abord, puis le réseau
 */
async function cacheFirst(request, cacheName) {
  const cache = await caches.open(cacheName)
  const cached = await cache.match(request)
  
  if (cached) {
    return cached
  }
  
  try {
    const response = await fetch(request)
    
    // Mettre en cache si la réponse est valide
    if (response.status === 200) {
      cache.put(request, response.clone())
    }
    
    return response
  } catch (error) {
    console.error('[Service Worker] Fetch failed:', error)
    
    // Retourner une réponse de fallback pour les images
    if (isImageRequest(request)) {
      return new Response(
        '<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300"><rect width="400" height="300" fill="#e5e7eb"/><text x="50%" y="50%" text-anchor="middle" fill="#9ca3af">Image non disponible</text></svg>',
        { headers: { 'Content-Type': 'image/svg+xml' } }
      )
    }
    
    throw error
  }
}

/**
 * Stratégie Network First : Essaie le réseau d'abord, puis le cache
 */
async function networkFirst(request, cacheName) {
  const cache = await caches.open(cacheName)
  
  try {
    const response = await fetch(request)
    
    // Mettre en cache si la réponse est valide
    if (response.status === 200) {
      cache.put(request, response.clone())
    }
    
    return response
  } catch (error) {
    console.log('[Service Worker] Network failed, trying cache:', error)
    
    const cached = await cache.match(request)
    if (cached) {
      return cached
    }
    
    throw error
  }
}

/**
 * Vérifie si la requête est pour une image
 */
function isImageRequest(request) {
  return request.destination === 'image' || 
         /\.(jpg|jpeg|png|gif|webp|svg|ico)$/i.test(request.url)
}

/**
 * Vérifie si la requête est pour l'API
 */
function isAPIRequest(request) {
  return request.url.includes('/api/')
}

/**
 * Vérifie si la requête est pour un asset statique
 */
function isStaticAsset(request) {
  return request.destination === 'script' ||
         request.destination === 'style' ||
         request.destination === 'font' ||
         /\.(js|css|woff|woff2|ttf|eot)$/i.test(request.url)
}

