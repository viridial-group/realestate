import { useToast } from './useToast'

export interface SearchParams {
  query?: string
  type?: string
  status?: string
  maxPrice?: number | null
  minSurface?: number | null
  bedrooms?: number | null
  bathrooms?: number | null
  sortBy?: string
  dateFilter?: string
}

/**
 * Composable pour partager des recherches
 */
export function useShareSearch() {
  const { success } = useToast()

  /**
   * Génère un lien de partage pour la recherche actuelle
   */
  function generateShareLink(params: SearchParams): string {
    const baseUrl = window.location.origin
    const searchParams = new URLSearchParams()

    // Ajouter uniquement les paramètres non vides
    if (params.query && params.query.trim()) {
      searchParams.set('q', params.query.trim())
    }
    if (params.type && params.type !== 'Tous') {
      searchParams.set('type', params.type)
    }
    if (params.status && params.status !== 'Tous') {
      searchParams.set('status', params.status)
    }
    if (params.maxPrice) {
      searchParams.set('maxPrice', params.maxPrice.toString())
    }
    if (params.minSurface) {
      searchParams.set('minSurface', params.minSurface.toString())
    }
    if (params.bedrooms) {
      searchParams.set('bedrooms', params.bedrooms.toString())
    }
    if (params.bathrooms) {
      searchParams.set('bathrooms', params.bathrooms.toString())
    }
    if (params.sortBy && params.sortBy !== 'default') {
      searchParams.set('sortBy', params.sortBy)
    }
    if (params.dateFilter) {
      searchParams.set('dateFilter', params.dateFilter)
    }

    return `${baseUrl}/search?${searchParams.toString()}`
  }

  /**
   * Copie le lien dans le presse-papiers
   */
  async function copyShareLink(params: SearchParams): Promise<boolean> {
    try {
      const link = generateShareLink(params)
      await navigator.clipboard.writeText(link)
      success('Lien de recherche copié dans le presse-papiers !')
      return true
    } catch (error) {
      console.error('Error copying link:', error)
      return false
    }
  }

  /**
   * Partage via l'API Web Share (si disponible)
   */
  async function shareViaWebAPI(params: SearchParams): Promise<boolean> {
    if (!navigator.share) {
      return false
    }

    try {
      const link = generateShareLink(params)
      const title = 'Recherche immobilière'
      const text = params.query 
        ? `Découvrez cette recherche : ${params.query}`
        : 'Découvrez cette recherche immobilière'

      await navigator.share({
        title,
        text,
        url: link,
      })

      success('Recherche partagée avec succès !')
      return true
    } catch (error: any) {
      // L'utilisateur a annulé le partage
      if (error.name !== 'AbortError') {
        console.error('Error sharing:', error)
      }
      return false
    }
  }

  /**
   * Partage via email
   */
  function shareViaEmail(params: SearchParams): void {
    const link = generateShareLink(params)
    const subject = encodeURIComponent('Recherche immobilière')
    const body = encodeURIComponent(
      `Découvrez cette recherche immobilière :\n\n${link}`
    )
    window.location.href = `mailto:?subject=${subject}&body=${body}`
  }

  /**
   * Partage via WhatsApp
   */
  function shareViaWhatsApp(params: SearchParams): void {
    const link = generateShareLink(params)
    const text = encodeURIComponent(
      `Découvrez cette recherche immobilière : ${link}`
    )
    window.open(`https://wa.me/?text=${text}`, '_blank')
  }

  /**
   * Partage via Facebook
   */
  function shareViaFacebook(params: SearchParams): void {
    const link = generateShareLink(params)
    window.open(
      `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(link)}`,
      '_blank',
      'width=600,height=400'
    )
  }

  /**
   * Partage via Twitter
   */
  function shareViaTwitter(params: SearchParams): void {
    const link = generateShareLink(params)
    const text = encodeURIComponent(
      params.query 
        ? `Découvrez cette recherche : ${params.query}`
        : 'Découvrez cette recherche immobilière'
    )
    window.open(
      `https://twitter.com/intent/tweet?text=${text}&url=${encodeURIComponent(link)}`,
      '_blank',
      'width=600,height=400'
    )
  }

  /**
   * Charge les paramètres depuis l'URL
   */
  function loadParamsFromURL(): SearchParams {
    const urlParams = new URLSearchParams(window.location.search)
    const params: SearchParams = {}

    if (urlParams.get('q')) {
      params.query = urlParams.get('q') || undefined
    }
    if (urlParams.get('type')) {
      params.type = urlParams.get('type') || undefined
    }
    if (urlParams.get('status')) {
      params.status = urlParams.get('status') || undefined
    }
    if (urlParams.get('maxPrice')) {
      params.maxPrice = Number(urlParams.get('maxPrice'))
    }
    if (urlParams.get('minSurface')) {
      params.minSurface = Number(urlParams.get('minSurface'))
    }
    if (urlParams.get('bedrooms')) {
      params.bedrooms = Number(urlParams.get('bedrooms'))
    }
    if (urlParams.get('bathrooms')) {
      params.bathrooms = Number(urlParams.get('bathrooms'))
    }
    if (urlParams.get('sortBy')) {
      params.sortBy = urlParams.get('sortBy') || undefined
    }
    if (urlParams.get('dateFilter')) {
      params.dateFilter = urlParams.get('dateFilter') || undefined
    }

    return params
  }

  return {
    generateShareLink,
    copyShareLink,
    shareViaWebAPI,
    shareViaEmail,
    shareViaWhatsApp,
    shareViaFacebook,
    shareViaTwitter,
    loadParamsFromURL,
  }
}

