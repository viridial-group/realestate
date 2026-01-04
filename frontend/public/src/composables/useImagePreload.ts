/**
 * Composable pour précharger les images critiques
 * Améliore le LCP (Largest Contentful Paint) pour le SEO
 */

import { onMounted, onBeforeUnmount } from 'vue'

interface PreloadImage {
  src: string
  srcset?: string
  type?: string
  as?: 'image'
  fetchpriority?: 'high' | 'low' | 'auto'
}

/**
 * Précharge une image critique
 */
export function useImagePreload(images: PreloadImage[]) {
  const preloadedLinks: HTMLLinkElement[] = []

  const preloadImage = (image: PreloadImage) => {
    if (typeof document === 'undefined') return

    const link = document.createElement('link')
    link.rel = 'preload'
    link.as = image.as || 'image'
    link.href = image.src
    
    if (image.srcset) {
      link.setAttribute('imagesrcset', image.srcset)
    }
    
    if (image.type) {
      link.type = image.type
    }
    
    if (image.fetchpriority) {
      link.setAttribute('fetchpriority', image.fetchpriority)
    }

    document.head.appendChild(link)
    preloadedLinks.push(link)
  }

  onMounted(() => {
    images.forEach(image => {
      preloadImage(image)
    })
  })

  onBeforeUnmount(() => {
    preloadedLinks.forEach(link => {
      if (link.parentNode) {
        link.parentNode.removeChild(link)
      }
    })
    preloadedLinks.length = 0
  })

  return {
    preloadImage
  }
}

/**
 * Précharge la première image d'une propriété (image hero)
 */
export function preloadPropertyHeroImage(imageUrl: string, webpUrl?: string) {
  if (typeof document === 'undefined') return

  const images: PreloadImage[] = []
  
  // Précharger WebP si disponible
  if (webpUrl) {
    images.push({
      src: webpUrl,
      type: 'image/webp',
      fetchpriority: 'high',
      as: 'image'
    })
  }
  
  // Précharger l'image de fallback
  images.push({
    src: imageUrl,
    fetchpriority: 'high',
    as: 'image'
  })

  images.forEach(img => {
    const link = document.createElement('link')
    link.rel = 'preload'
    link.as = 'image'
    link.href = img.src
    if (img.type) {
      link.type = img.type
    }
    if (img.fetchpriority) {
      link.setAttribute('fetchpriority', img.fetchpriority)
    }
    document.head.appendChild(link)
  })
}

