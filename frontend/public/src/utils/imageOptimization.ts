/**
 * Utilitaires pour l'optimisation des images
 */

/**
 * Génère une URL d'image optimisée avec des paramètres de taille
 * @param imageUrl URL de l'image originale
 * @param width Largeur souhaitée
 * @param height Hauteur souhaitée (optionnel)
 * @returns URL optimisée
 */
export function getOptimizedImageUrl(imageUrl: string, width: number, height?: number): string {
  // Si l'URL contient déjà des paramètres, les préserver
  const url = new URL(imageUrl, window.location.origin)
  
  // Ajouter des paramètres de taille si l'API le supporte
  // Pour l'instant, on retourne l'URL originale
  // TODO: Implémenter la génération d'URLs optimisées si l'API le supporte
  return imageUrl
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

