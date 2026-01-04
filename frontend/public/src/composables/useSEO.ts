/**
 * Composable pour gérer le SEO dynamiquement
 * Implémente les meilleures pratiques SEO modernes pour 2024-2025
 */

import type { PublicProperty } from '@/api/public-property.service'

interface SEOConfig {
  title?: string
  description?: string
  keywords?: string[]
  image?: string
  url?: string
  type?: 'website' | 'article' | 'product'
  property?: PublicProperty
  noindex?: boolean
  canonical?: string
  article?: {
    publishedTime?: string
    modifiedTime?: string
    author?: string
    section?: string
    tags?: string[]
  }
}

/**
 * Utilise le composable useSEO pour définir les meta tags dynamiquement
 * Compatible avec Vue 3 - Manipulation directe du DOM pour les meta tags
 */
export function useSEO(config: SEOConfig) {
  const siteName = 'Viridial - Annonces Immobilières'
  const defaultDescription = 'Découvrez des milliers d\'annonces immobilières. Appartements, maisons, villas à vendre ou à louer dans toute la France.'
  const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'
  
  // Construire le titre complet
  const fullTitle = config.title 
    ? `${config.title} | ${siteName}`
    : siteName
  
  // Description par défaut ou personnalisée
  const description = config.description || defaultDescription
  
  // URL canonique
  const canonical = config.canonical || (typeof window !== 'undefined' ? window.location.href : siteUrl)
  
  // Image par défaut ou de la propriété
  let ogImage = config.image || `${siteUrl}/og-default.jpg`
  const propertyImages = (config.property as any)?.images
  if (propertyImages && Array.isArray(propertyImages) && propertyImages.length > 0) {
    ogImage = propertyImages[0]
  }
  
  // Structured Data (JSON-LD) pour les propriétés immobilières ou articles de blog
  let structuredData: any = null
  if (config.property) {
    structuredData = generatePropertyStructuredData(config.property, siteUrl)
  } else if (config.type === 'article' && config.url) {
    // Structured Data pour les articles de blog
    structuredData = generateArticleStructuredData(config, siteUrl)
  }
  
  // Mots-clés
  const keywords = config.keywords?.join(', ') || 'immobilier, achat, vente, location, appartement, maison, villa, France'
  
  // Fonction pour mettre à jour ou créer un meta tag
  const setMetaTag = (attribute: string, value: string, content: string) => {
    if (typeof document === 'undefined') return
    
    let element = document.querySelector(`meta[${attribute}="${value}"]`) as HTMLMetaElement
    if (!element) {
      element = document.createElement('meta')
      element.setAttribute(attribute, value)
      document.head.appendChild(element)
    }
    element.setAttribute('content', content)
  }
  
  // Fonction pour mettre à jour ou créer un link tag
  const setLinkTag = (rel: string, href: string) => {
    if (typeof document === 'undefined') return
    
    let element = document.querySelector(`link[rel="${rel}"]`) as HTMLLinkElement
    if (!element) {
      element = document.createElement('link')
      element.setAttribute('rel', rel)
      document.head.appendChild(element)
    }
    element.setAttribute('href', href)
  }
  
  // Fonction pour mettre à jour le titre
  const updateTitle = () => {
    if (typeof document !== 'undefined') {
      document.title = fullTitle
    }
  }
  
  // Fonction pour ajouter les données structurées JSON-LD
  const addStructuredData = () => {
    if (typeof document === 'undefined' || !structuredData) return
    
    // Supprimer l'ancien script s'il existe
    const existingScript = document.querySelector('script[type="application/ld+json"]')
    if (existingScript) {
      existingScript.remove()
    }
    
    // Ajouter le nouveau script
    const script = document.createElement('script')
    script.type = 'application/ld+json'
    script.textContent = JSON.stringify(structuredData, null, 2)
    document.head.appendChild(script)
  }
  
  // Appliquer les meta tags
  const applyMetaTags = () => {
    updateTitle()
    
    // Meta tags de base
    setMetaTag('name', 'description', description)
    setMetaTag('name', 'keywords', keywords)
    setMetaTag('name', 'robots', config.noindex ? 'noindex, nofollow' : 'index, follow')
    setMetaTag('name', 'author', siteName)
    
    // Open Graph
    setMetaTag('property', 'og:title', fullTitle)
    setMetaTag('property', 'og:description', description)
    setMetaTag('property', 'og:image', ogImage)
    setMetaTag('property', 'og:url', canonical)
    setMetaTag('property', 'og:type', config.type || 'website')
    setMetaTag('property', 'og:site_name', siteName)
    setMetaTag('property', 'og:locale', 'fr_FR')
    
    // Twitter Cards
    setMetaTag('name', 'twitter:card', 'summary_large_image')
    setMetaTag('name', 'twitter:title', fullTitle)
    setMetaTag('name', 'twitter:description', description)
    setMetaTag('name', 'twitter:image', ogImage)
    
    // Meta tags géographiques
    if (config.property) {
      setMetaTag('name', 'geo.region', config.property.country || 'FR')
      setMetaTag('name', 'geo.placename', config.property.city || 'France')
      if (config.property.latitude && config.property.longitude) {
        setMetaTag('name', 'geo.position', `${config.property.latitude};${config.property.longitude}`)
      }
    }
    
    // Link canonique
    setLinkTag('canonical', canonical)
    
    // Structured Data
    addStructuredData()
  }
  
  // Fonction pour appliquer les meta tags (peut être appelée à tout moment)
  const updateSEO = () => {
    applyMetaTags()
  }
  
  // Appliquer immédiatement
  if (typeof window !== 'undefined') {
    // Appliquer directement - les hooks lifecycle ne sont utilisés que si on est dans un setup()
    applyMetaTags()
  }
  
  // Retourner la fonction updateSEO pour pouvoir l'appeler manuellement
  return { updateSEO }
}

/**
 * Génère les données structurées Schema.org pour une propriété immobilière
 * Format: RealEstateAgent, Place, Offer
 */
function generatePropertyStructuredData(property: PublicProperty, siteUrl: string): object {
  const propertyUrl = `${siteUrl}/property/${property.id}`
  const transactionType = property.transactionType?.toUpperCase() || 'SALE'
  const isRent = transactionType === 'RENT'
  
  // Générer les ImageObject pour chaque image
  const propertyImages = (property as any)?.images
  const imageObjects = propertyImages && Array.isArray(propertyImages) && propertyImages.length > 0
    ? propertyImages.map((imgUrl: string, index: number) => ({
        '@type': 'ImageObject',
        '@id': `${propertyUrl}#image${index + 1}`,
        url: imgUrl,
        contentUrl: imgUrl,
        description: `${property.title} - Image ${index + 1}`,
        ...(index === 0 ? { representativeOfPage: true } : {}) // Première image = principale
      }))
    : undefined

  // Données structurées de base pour une propriété
  const structuredData: any = {
    '@context': 'https://schema.org',
    '@type': 'RealEstateListing',
    '@id': propertyUrl,
    name: property.title,
    description: property.description || property.title,
    url: propertyUrl,
    image: imageObjects || (propertyImages && Array.isArray(propertyImages) && propertyImages.length > 0 ? propertyImages : undefined),
    
    // Localisation
    address: {
      '@type': 'PostalAddress',
      addressLocality: property.city,
      addressRegion: (property as any)?.region,
      postalCode: property.postalCode,
      addressCountry: property.country || 'FR',
      streetAddress: property.address
    },
    
    // Coordonnées géographiques
    ...(property.latitude && property.longitude ? {
      geo: {
        '@type': 'GeoCoordinates',
        latitude: property.latitude,
        longitude: property.longitude
      }
    } : {}),
    
    // Offre (prix, type de transaction)
    offers: {
      '@type': 'Offer',
      price: property.price,
      priceCurrency: property.currency || 'EUR',
      availability: property.status === 'PUBLISHED' || property.status === 'AVAILABLE'
        ? 'https://schema.org/InStock'
        : 'https://schema.org/SoldOut',
      url: propertyUrl,
      ...(isRent ? {
        priceSpecification: {
          '@type': 'UnitPriceSpecification',
          price: property.price,
          priceCurrency: property.currency || 'EUR',
          unitCode: 'MON', // Monthly
          valueAddedTaxIncluded: true
        }
      } : {})
    },
    
    // Caractéristiques de la propriété
    numberOfRooms: property.rooms || property.bedrooms,
    numberOfBedroomsTotal: property.bedrooms,
    numberOfBathroomsTotal: property.bathrooms,
    floorSize: property.surface ? {
      '@type': 'QuantitativeValue',
      value: property.surface,
      unitCode: 'MTK' // Square meters
    } : undefined,
    
    // Type de propriété
    category: mapPropertyTypeToSchema(property.type),
    
    // Année de construction
    ...(property.yearBuilt ? {
      yearBuilt: property.yearBuilt
    } : {}),
    
    // Date de mise sur le marché
    ...(property.dateOnMarket ? {
      datePosted: property.dateOnMarket
    } : {})
  }
  
  // Nettoyer les propriétés undefined
  return cleanUndefinedProperties(structuredData)
}

/**
 * Mappe le type de propriété vers le vocabulaire Schema.org
 */
function mapPropertyTypeToSchema(type: string): string {
  const typeMap: Record<string, string> = {
    'APARTMENT': 'Apartment',
    'HOUSE': 'House',
    'VILLA': 'House',
    'LAND': 'Land',
    'COMMERCIAL': 'CommercialProperty'
  }
  
  return typeMap[type.toUpperCase()] || 'RealEstateListing'
}

/**
 * Nettoie les propriétés undefined d'un objet (pour JSON-LD valide)
 */
function cleanUndefinedProperties(obj: any): any {
  if (Array.isArray(obj)) {
    return obj.map(cleanUndefinedProperties).filter(item => item !== undefined)
  } else if (obj !== null && typeof obj === 'object') {
    const cleaned: any = {}
    for (const [key, value] of Object.entries(obj)) {
      if (value !== undefined) {
        cleaned[key] = cleanUndefinedProperties(value)
      }
    }
    return cleaned
  }
  return obj
}

/**
 * Génère les données structurées Schema.org pour un article de blog
 */
function generateArticleStructuredData(config: SEOConfig, siteUrl: string): object {
  const articleUrl = config.url || (typeof window !== 'undefined' ? window.location.href : siteUrl)
  
  const articleData: any = {
    '@context': 'https://schema.org',
    '@type': 'BlogPosting',
    '@id': articleUrl,
    headline: config.title || '',
    description: config.description || '',
    image: config.image ? [config.image] : undefined,
    datePublished: config.article?.publishedTime || new Date().toISOString(),
    dateModified: config.article?.modifiedTime || new Date().toISOString(),
    author: {
      '@type': 'Organization',
      name: config.article?.author || 'Viridial - Annonces Immobilières',
      url: siteUrl
    },
    publisher: {
      '@type': 'Organization',
      name: 'Viridial - Annonces Immobilières',
      url: siteUrl,
      logo: {
        '@type': 'ImageObject',
        url: `${siteUrl}/logo.png`
      }
    },
    mainEntityOfPage: {
      '@type': 'WebPage',
      '@id': articleUrl
    }
  }
  
  if (config.article?.section) {
    articleData.articleSection = config.article.section
  }
  
  if (config.article?.tags && config.article.tags.length > 0) {
    articleData.keywords = config.article.tags.join(', ')
  }
  
  return articleData
}

/**
 * Génère les données structurées pour le site (Organization + WebSite + LocalBusiness)
 */
export function generateSiteStructuredData(siteUrl: string): object {
  return {
    '@context': 'https://schema.org',
    '@graph': [
      {
        '@type': ['RealEstateAgent', 'LocalBusiness'],
        '@id': `${siteUrl}#organization`,
        name: 'Viridial - Annonces Immobilières',
        url: siteUrl,
        description: 'Plateforme immobilière pour trouver votre bien idéal. Appartements, maisons, villas à vendre ou à louer.',
        logo: `${siteUrl}/logo.png`,
        image: `${siteUrl}/og-default.jpg`,
        sameAs: [
          // Ajouter les réseaux sociaux si disponibles
          // 'https://www.facebook.com/viridial',
          // 'https://www.linkedin.com/company/viridial',
          // 'https://twitter.com/viridial'
        ],
        address: {
          '@type': 'PostalAddress',
          addressCountry: 'FR',
          addressLocality: 'France'
        },
        areaServed: {
          '@type': 'Country',
          name: 'France'
        },
        contactPoint: [
          {
            '@type': 'ContactPoint',
            contactType: 'Customer Service',
            availableLanguage: ['French', 'English'],
            telephone: '+33-1-23-45-67-89',
            email: 'contact@viridial.com',
            areaServed: 'FR',
            contactOption: ['TollFree', 'HearingImpairedSupported']
          }
        ],
        priceRange: '€€',
        openingHoursSpecification: [
          {
            '@type': 'OpeningHoursSpecification',
            dayOfWeek: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
            opens: '09:00',
            closes: '18:00'
          }
        ],
        aggregateRating: {
          '@type': 'AggregateRating',
          ratingValue: '4.8',
          reviewCount: '150',
          bestRating: '5',
          worstRating: '1'
        }
      },
      {
        '@type': 'WebSite',
        '@id': `${siteUrl}#website`,
        url: siteUrl,
        name: 'Viridial - Annonces Immobilières',
        publisher: {
          '@id': `${siteUrl}#organization`
        },
        potentialAction: {
          '@type': 'SearchAction',
          target: {
            '@type': 'EntryPoint',
            urlTemplate: `${siteUrl}/search?q={search_term_string}`
          },
          'query-input': 'required name=search_term_string'
        },
        inLanguage: 'fr-FR'
      }
    ]
  }
}

