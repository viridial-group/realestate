/**
 * Service API pour les articles de blog
 */

import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api/public/blog',
  headers: {
    'Content-Type': 'application/json',
  },
})

export interface BlogPost {
  id: number
  title: string
  excerpt?: string
  content: string
  slug: string
  category?: string
  tags?: string
  featuredImage?: string
  metaDescription?: string
  metaKeywords?: string
  ogImage?: string
  status: string
  publishedAt?: string
  authorId: number
  authorName?: string
  organizationId?: number
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface BlogPostPage {
  content: BlogPost[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export const blogService = {
  /**
   * Récupère la liste paginée des articles publiés
   */
  async getPublishedPosts(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'publishedAt',
    sortDir: string = 'DESC'
  ): Promise<BlogPostPage> {
    const response = await apiClient.get<BlogPostPage>('', {
      params: { page, size, sortBy, sortDir }
    })
    return response.data
  },

  /**
   * Récupère les articles récents
   */
  async getRecentPosts(limit: number = 5): Promise<BlogPost[]> {
    const response = await apiClient.get<BlogPost[]>('/recent', {
      params: { limit }
    })
    return response.data
  },

  /**
   * Récupère un article par son slug
   */
  async getPostBySlug(slug: string): Promise<BlogPost> {
    const response = await apiClient.get<BlogPost>(`/slug/${slug}`)
    return response.data
  },

  /**
   * Récupère les articles par catégorie
   */
  async getPostsByCategory(
    category: string,
    page: number = 0,
    size: number = 10
  ): Promise<BlogPostPage> {
    const response = await apiClient.get<BlogPostPage>(`/category/${category}`, {
      params: { page, size }
    })
    return response.data
  },

  /**
   * Récupère les articles par tag
   */
  async getPostsByTag(
    tag: string,
    page: number = 0,
    size: number = 10
  ): Promise<BlogPostPage> {
    const response = await apiClient.get<BlogPostPage>(`/tag/${tag}`, {
      params: { page, size }
    })
    return response.data
  },

  /**
   * Recherche dans les articles
   */
  async searchPosts(
    query: string,
    page: number = 0,
    size: number = 10
  ): Promise<BlogPostPage> {
    const response = await apiClient.get<BlogPostPage>('/search', {
      params: { q: query, page, size }
    })
    return response.data
  },

  /**
   * Récupère toutes les catégories
   */
  async getCategories(): Promise<string[]> {
    const response = await apiClient.get<string[]>('/categories')
    return response.data
  }
}

