/**
 * Utilitaires pour enregistrer et gérer le Service Worker
 */

/**
 * Enregistre le Service Worker pour améliorer les performances et le SEO
 */
export function registerServiceWorker() {
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker
        .register('/sw.js')
        .then((registration) => {
          console.log('[Service Worker] Registered successfully:', registration.scope)
          
          // Vérifier les mises à jour périodiquement
          setInterval(() => {
            registration.update()
          }, 60000) // Toutes les minutes
        })
        .catch((error) => {
          console.error('[Service Worker] Registration failed:', error)
        })
    })
  }
}

/**
 * Désenregistre le Service Worker (utile pour le développement)
 */
export function unregisterServiceWorker() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.ready.then((registration) => {
      registration.unregister().then((success) => {
        if (success) {
          console.log('[Service Worker] Unregistered successfully')
        }
      })
    })
  }
}

