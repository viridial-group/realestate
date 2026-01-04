/**
 * Utilitaires pour générer le sitemap XML dynamiquement
 * Optimisé pour le SEO des sites immobiliers
 */

import type { PublicProperty } from '@/api/public-property.service'

export interface SitemapUrl {
  loc: string
  lastmod?: string
  changefreq?: 'always' | 'hourly' | 'daily' | 'weekly' | 'monthly' | 'yearly' | 'never'
  priority?: number
}

/**
 * Génère un sitemap XML à partir d'une liste d'URLs
 */
export function generateSitemapXML(urls: SitemapUrl[]): string {
  const urlsXML = urls.map(url => {
    let urlXML = '  <url>\n'
    urlXML += `    <loc>${escapeXML(url.loc)}</loc>\n`
    
    if (url.lastmod) {
      urlXML += `    <lastmod>${url.lastmod}</lastmod>\n`
    }
    
    if (url.changefreq) {
      urlXML += `    <changefreq>${url.changefreq}</changefreq>\n`
    }
    
    if (url.priority !== undefined) {
      urlXML += `    <priority>${url.priority}</priority>\n`
    }
    
    urlXML += '  </url>'
    return urlXML
  }).join('\n')
  
  return `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
        xmlns:image="http://www.google.com/schemas/sitemap-image/1.1"
        xmlns:news="http://www.google.com/schemas/sitemap-news/0.9">
${urlsXML}
</urlset>`
}

/**
 * Génère un sitemap pour les propriétés immobilières
 */
export function generatePropertiesSitemap(
  properties: PublicProperty[],
  baseUrl: string = 'http://viridial.com'
): string {
  const urls: SitemapUrl[] = properties.map(property => {
    const propertyUrl = `${baseUrl}/property/${property.id}`
    const lastmod = property.updatedAt 
      ? new Date(property.updatedAt).toISOString().split('T')[0]
      : new Date().toISOString().split('T')[0]
    
    return {
      loc: propertyUrl,
      lastmod,
      changefreq: 'weekly' as const,
      priority: property.status === 'PUBLISHED' ? 0.8 : 0.5
    }
  })
  
  return generateSitemapXML(urls)
}

/**
 * Génère le sitemap principal avec les pages statiques
 */
export function generateMainSitemap(baseUrl: string = 'http://viridial.com'): string {
  const urls: SitemapUrl[] = [
    {
      loc: baseUrl,
      changefreq: 'daily',
      priority: 1.0
    },
    {
      loc: `${baseUrl}/search`,
      changefreq: 'hourly',
      priority: 0.9
    },
    {
      loc: `${baseUrl}/about`,
      changefreq: 'monthly',
      priority: 0.7
    },
    {
      loc: `${baseUrl}/publish`,
      changefreq: 'monthly',
      priority: 0.6
    }
  ]
  
  return generateSitemapXML(urls)
}

/**
 * Échappe les caractères XML spéciaux
 */
function escapeXML(str: string): string {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;')
}

