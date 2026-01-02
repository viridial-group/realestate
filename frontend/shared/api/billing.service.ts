import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { Subscription, Invoice, Plan, BillingStats, SubscriptionCreate, SubscriptionUpdate } from '../types/billing.types'

/**
 * Service de gestion de la facturation
 */
export const billingService = {
  /**
   * Récupérer toutes les abonnements
   */
  async getAllSubscriptions(params?: {
    organizationId?: number
    status?: string
    page?: number
    size?: number
  }): Promise<Subscription[]> {
    const queryParams: any = {}
    if (params?.status) {
      queryParams.status = params.status
    }
    
    const response = await httpClient.get<Subscription[]>(
      API_ENDPOINTS.BILLING.SUBSCRIPTIONS.BASE,
      { params: queryParams }
    )
    let subscriptions = response.data || []
    
    // Filtrer par organizationId côté client si fourni (le backend ne le supporte pas encore)
    if (params?.organizationId) {
      subscriptions = subscriptions.filter(s => s.organizationId === params.organizationId)
    }
    
    return subscriptions
  },

  /**
   * Récupérer un abonnement par ID
   */
  async getSubscriptionById(id: number): Promise<Subscription> {
    const response = await httpClient.get<Subscription>(API_ENDPOINTS.BILLING.SUBSCRIPTIONS.BY_ID(id))
    return response.data
  },

  /**
   * Récupérer l'abonnement actif d'une organisation
   */
  async getActiveSubscriptionByOrganization(organizationId: number): Promise<Subscription> {
    const response = await httpClient.get<Subscription>(
      API_ENDPOINTS.BILLING.SUBSCRIPTIONS.ACTIVE_BY_ORGANIZATION(organizationId)
    )
    return response.data
  },

  /**
   * Créer un abonnement
   */
  async createSubscription(data: SubscriptionCreate): Promise<Subscription> {
    const response = await httpClient.post<Subscription>(API_ENDPOINTS.BILLING.SUBSCRIPTIONS.BASE, data)
    return response.data
  },

  /**
   * Mettre à jour un abonnement
   */
  async updateSubscription(id: number, data: SubscriptionUpdate): Promise<Subscription> {
    const response = await httpClient.put<Subscription>(API_ENDPOINTS.BILLING.SUBSCRIPTIONS.BY_ID(id), data)
    return response.data
  },

  /**
   * Créer une facture
   */
  async createInvoice(data: {
    subscriptionId: number
    amount: number
    taxAmount?: number
  }): Promise<Invoice> {
    const response = await httpClient.post<Invoice>(API_ENDPOINTS.BILLING.INVOICES.BASE, data)
    return response.data
  },

  /**
   * Récupérer une facture par numéro
   */
  async getInvoiceByNumber(invoiceNumber: string): Promise<Invoice> {
    const response = await httpClient.get<Invoice>(API_ENDPOINTS.BILLING.INVOICES.BY_NUMBER(invoiceNumber))
    return response.data
  },

  /**
   * Récupérer les factures en retard
   */
  async getOverdueInvoices(date?: string): Promise<Invoice[]> {
    const params = date ? { date } : {}
    const response = await httpClient.get<Invoice[]>(API_ENDPOINTS.BILLING.INVOICES.OVERDUE, { params })
    return response.data || []
  },

  /**
   * Mettre à jour le statut d'une facture
   */
  async updateInvoiceStatus(id: number, status: string): Promise<Invoice> {
    const response = await httpClient.put<Invoice>(API_ENDPOINTS.BILLING.INVOICES.UPDATE_STATUS(id), { status })
    return response.data
  },

  /**
   * Télécharger une facture en PDF
   */
  async downloadInvoicePdf(id: number): Promise<Blob> {
    return await httpClient.getBlob(`${API_ENDPOINTS.BILLING.INVOICES.BY_ID(id)}/download`)
  },

  /**
   * Générer automatiquement une facture pour un abonnement
   */
  async generateInvoiceForSubscription(subscriptionId: number): Promise<Invoice> {
    const response = await httpClient.post<Invoice>(
      `${API_ENDPOINTS.BILLING.INVOICES.BASE}/subscription/${subscriptionId}/generate`
    )
    return response.data
  },

  /**
   * Annuler un abonnement
   */
  async cancelSubscription(id: number, cancelledBy: number): Promise<Subscription> {
    const response = await httpClient.post<Subscription>(
      API_ENDPOINTS.BILLING.SUBSCRIPTIONS.CANCEL(id),
      { cancelledBy }
    )
    return response.data
  },

  /**
   * Renouveler un abonnement
   */
  async renewSubscription(id: number): Promise<Subscription> {
    const response = await httpClient.post<Subscription>(API_ENDPOINTS.BILLING.SUBSCRIPTIONS.RENEW(id))
    return response.data
  },

  /**
   * Récupérer les abonnements expirant bientôt
   */
  async getExpiringSubscriptions(days?: number): Promise<Subscription[]> {
    const params = days ? { days } : {}
    const response = await httpClient.get<Subscription[]>(
      API_ENDPOINTS.BILLING.SUBSCRIPTIONS.EXPIRING,
      { params }
    )
    return response.data || []
  },

  /**
   * Récupérer toutes les factures
   * Note: Le backend requiert organizationId comme paramètre obligatoire pour la liste
   * Si subscriptionId est fourni, utilise l'endpoint spécifique
   */
  async getAllInvoices(params?: {
    organizationId?: number
    subscriptionId?: number
    status?: string
    page?: number
    size?: number
  }): Promise<Invoice[]> {
    // Si subscriptionId est fourni, utiliser l'endpoint spécifique
    if (params?.subscriptionId) {
      return await this.getInvoicesBySubscription(params.subscriptionId)
    }
    
    // Sinon, utiliser l'endpoint de liste qui requiert organizationId
    if (!params?.organizationId) {
      console.warn('getAllInvoices: organizationId is required for listing invoices')
      return []
    }
    
    const queryParams: any = {
      organizationId: params.organizationId
    }
    if (params?.status) {
      queryParams.status = params.status
    }
    if (params?.page !== undefined) {
      queryParams.page = params.page
    }
    if (params?.size !== undefined) {
      queryParams.size = params.size
    }
    
    const response = await httpClient.get<Invoice[]>(API_ENDPOINTS.BILLING.INVOICES.BASE, { params: queryParams })
    // Le backend retourne une Page, extraire le contenu si nécessaire
    if (response.data && Array.isArray(response.data)) {
      return response.data
    }
    // Si c'est un objet Page avec une propriété content
    if (response.data && (response.data as any).content) {
      return (response.data as any).content
    }
    return []
  },

  /**
   * Récupérer une facture par ID
   */
  async getInvoiceById(id: number): Promise<Invoice> {
    const response = await httpClient.get<Invoice>(API_ENDPOINTS.BILLING.INVOICES.BY_ID(id))
    return response.data
  },

  /**
   * Récupérer les factures d'un abonnement
   */
  async getInvoicesBySubscription(subscriptionId: number): Promise<Invoice[]> {
    const response = await httpClient.get<Invoice[]>(
      API_ENDPOINTS.BILLING.INVOICES.BY_SUBSCRIPTION(subscriptionId)
    )
    return response.data || []
  },

  /**
   * Marquer une facture comme payée
   */
  async markInvoiceAsPaid(id: number): Promise<Invoice> {
    const response = await httpClient.post<Invoice>(API_ENDPOINTS.BILLING.INVOICES.MARK_PAID(id))
    return response.data
  },

  /**
   * Récupérer les statistiques de facturation
   * Note: Le backend n'a pas d'endpoint stats dédié
   * Les stats sont calculées côté frontend depuis les abonnements
   */
  async getStats(): Promise<BillingStats> {
    // Calculer les stats depuis tous les abonnements
    const subscriptions = await this.getAllSubscriptions()
    const activeSubscriptions = subscriptions.filter(s => s.status === 'ACTIVE').length
    const pendingPayments = subscriptions.filter(s => s.status === 'PENDING').length
    
    const now = new Date()
    const currentMonth = now.getMonth()
    const currentYear = now.getFullYear()
    
    const monthlyRevenue = subscriptions
      .filter(s => {
        if (s.status !== 'ACTIVE') return false
        const startDate = new Date(s.startDate)
        return startDate.getMonth() === currentMonth && startDate.getFullYear() === currentYear
      })
      .reduce((sum, s) => sum + (s.amount || 0), 0)
    
    const totalRevenue = subscriptions
      .filter(s => s.status === 'ACTIVE')
      .reduce((sum, s) => sum + (s.amount || 0), 0)
    
    return {
      activeSubscriptions,
      pendingPayments,
      monthlyRevenue,
      totalRevenue
    }
  },

  /**
   * Récupérer tous les plans
   */
  async getAllPlans(): Promise<Plan[]> {
    const response = await httpClient.get<Plan[]>(API_ENDPOINTS.BILLING.PLANS.BASE)
    return response.data || []
  },

  /**
   * Récupérer un plan par ID
   */
  async getPlanById(id: number): Promise<Plan> {
    const response = await httpClient.get<Plan>(API_ENDPOINTS.BILLING.PLANS.BY_ID(id))
    return response.data
  }
}

