/**
 * Types pour le module de facturation
 */

export interface Subscription {
  id: number
  planId: number
  planName?: string
  organizationId: number
  organizationName?: string
  organizationEmail?: string
  status: 'ACTIVE' | 'PENDING' | 'CANCELLED' | 'EXPIRED'
  startDate: string
  endDate: string
  trialEndDate?: string
  autoRenew?: boolean
  cancelledAt?: string
  cancelledBy?: number
  active?: boolean
  amount?: number // Montant du plan
}

export interface Invoice {
  id: number
  subscriptionId: number
  invoiceNumber: string
  organizationId: number
  amount: number
  taxAmount: number
  totalAmount: number
  currency: string
  status: 'DRAFT' | 'PENDING' | 'PAID' | 'OVERDUE' | 'CANCELLED'
  dueDate: string
  paidAt?: string
  billingPeriodStart?: string
  billingPeriodEnd?: string
}

export interface Plan {
  id: number
  name: string
  description?: string
  price: number
  currency: string
  billingCycle: 'MONTHLY' | 'YEARLY'
  features?: string[] | string // Peut être un tableau ou une chaîne JSON
  quotas?: Record<string, number>
  active: boolean
}

export interface BillingStats {
  activeSubscriptions: number
  pendingPayments: number
  monthlyRevenue: number
  totalRevenue: number
}

export interface SubscriptionCreate {
  planId: number
  organizationId: number
}

export interface SubscriptionUpdate {
  status?: 'ACTIVE' | 'PENDING' | 'CANCELLED' | 'EXPIRED'
  endDate?: string
  autoRenew?: boolean
  active?: boolean
}

