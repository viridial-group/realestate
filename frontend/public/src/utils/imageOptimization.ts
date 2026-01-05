/**
 * Utilitaires pour l'optimisation des images
 */

/**
 * Génère une URL d'image optimisée avec des paramètres de taille
 * @param imageUrl URL de l'image originale
 * @param width Largeur souhaitée
 * @param height Hauteur souhaitée (optionnel)
 * @param quality Qualité de l'image (1-100, par défaut 80)
 * @returns URL optimisée
 */
export function getOptimizedImageUrl(
  imageUrl: string, 
  width: number, 
  height?: number, 
  quality: number = 80
): string {
  if (!imageUrl) {
    return getPlaceholderImage(width, height || width * 0.75)
  }

  try {
    // Si c'est déjà une URL complète, l'utiliser directement
    const url = imageUrl.startsWith('http') 
      ? new URL(imageUrl) 
      : new URL(imageUrl, window.location.origin)
    
    // Si l'API supporte les paramètres de transformation d'image
    // (ex: Cloudinary, ImageKit, ou API personnalisée)
    const supportsImageTransformation = 
      url.hostname.includes('cloudinary.com') ||
      url.hostname.includes('imagekit.io') ||
      url.hostname.includes('res.cloudinary.com') ||
      url.pathname.includes('/transform/') ||
      url.pathname.includes('/resize/')
    
    if (supportsImageTransformation) {
      // Ajouter les paramètres de transformation selon le service
      if (url.hostname.includes('cloudinary.com')) {
        url.searchParams.set('w', width.toString())
        if (height) url.searchParams.set('h', height.toString())
        url.searchParams.set('q', quality.toString())
        url.searchParams.set('c', 'fill')
        url.searchParams.set('f', 'auto')
      } else if (url.hostname.includes('imagekit.io')) {
        url.searchParams.set('tr', `w-${width}${height ? `,h-${height}` : ''},q-${quality}`)
      }
    } else {
      // Pour les autres URLs, on peut utiliser un service de proxy d'images
      // ou retourner l'URL originale avec des paramètres de requête
      // Exemple avec un service de proxy (à configurer selon votre infrastructure)
      const proxyBase = import.meta.env.VITE_IMAGE_PROXY_URL || ''
      if (proxyBase) {
        const proxyUrl = new URL(proxyBase)
        proxyUrl.searchParams.set('url', imageUrl)
        proxyUrl.searchParams.set('w', width.toString())
        if (height) proxyUrl.searchParams.set('h', height.toString())
        proxyUrl.searchParams.set('q', quality.toString())
        return proxyUrl.toString()
      }
    }
    
    return url.toString()
  } catch (error) {
    console.warn('Erreur lors de l\'optimisation de l\'image:', error)
    // En cas d'erreur, retourner l'URL originale ou un placeholder
    return imageUrl || getPlaceholderImage(width, height || width * 0.75)
  }
}

/**
 * Génère un placeholder SVG pour les images manquantes
 */
export function getPlaceholderImage(width: number = 400, height: number = 300): string {
  return `data:image/svg+xml;base64,${btoa(`
    <svg width="${width}" height="${height}" xmlns="http://www.w3.org/2000/svg">
      <rect width="${width}" height="${height}" fill="#f3f4f6"/>
      <text x="50%" y="50%" text-anchor="middle" fill="#9ca3af" font-family="Arial" font-size="16" dy=".3em">
        Image non disponible
      </text>
    </svg>
  `)}`
}

/**
 * Vérifie si une image est chargée avec succès
 */
export function isImageLoaded(img: HTMLImageElement): boolean {
  return img.complete && img.naturalHeight !== 0
}

