<template>
  <div class="space-y-6">
    <!-- Header avec Actions -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="icon" @click="goBack">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">Facture {{ invoice?.invoiceNumber || '' }}</h1>
          <p class="text-muted-foreground mt-1" v-if="invoice">
            {{ formatDate(invoice.dueDate) }}
          </p>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="downloadInvoice" :disabled="loading || !invoice">
          <Download class="mr-2 h-4 w-4" />
          Télécharger PDF
        </Button>
        <Button 
          v-if="invoice?.status === 'PENDING'" 
          @click="markAsPaid" 
          :disabled="loading"
        >
          <CheckCircle class="mr-2 h-4 w-4" />
          Marquer comme payée
        </Button>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>

    <div v-else-if="invoice" class="space-y-6">
      <!-- Informations principales -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Colonne principale -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Détails de la facture -->
          <Card>
            <CardHeader>
              <CardTitle>Détails de la facture</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Numéro de facture</p>
                  <p class="font-semibold text-lg">{{ invoice.invoiceNumber }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Statut</p>
                  <Badge :variant="getStatusVariant(invoice.status)">
                    {{ getStatusLabel(invoice.status) }}
                  </Badge>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Date d'échéance</p>
                  <p class="font-semibold">{{ formatDate(invoice.dueDate) }}</p>
                </div>
                <div class="space-y-1" v-if="invoice.paidAt">
                  <p class="text-sm text-muted-foreground">Date de paiement</p>
                  <p class="font-semibold">{{ formatDate(invoice.paidAt) }}</p>
                </div>
                <div class="space-y-1" v-if="invoice.billingPeriodStart && invoice.billingPeriodEnd">
                  <p class="text-sm text-muted-foreground">Période facturée</p>
                  <p class="font-semibold">{{ formatDateRange(invoice.billingPeriodStart, invoice.billingPeriodEnd) }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Montants -->
          <Card>
            <CardHeader>
              <CardTitle>Montants</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="space-y-4">
                <div class="flex justify-between items-center">
                  <span class="text-muted-foreground">Montant HT</span>
                  <span class="font-semibold">{{ formatCurrency(invoice.amount) }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-muted-foreground">TVA</span>
                  <span class="font-semibold">{{ formatCurrency(invoice.taxAmount) }}</span>
                </div>
                <div class="border-t pt-4 flex justify-between items-center">
                  <span class="text-lg font-semibold">Total TTC</span>
                  <span class="text-2xl font-bold">{{ formatCurrency(invoice.totalAmount) }}</span>
                </div>
                <div class="text-sm text-muted-foreground">
                  Devise: {{ invoice.currency }}
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Informations de l'abonnement -->
          <Card v-if="subscription">
            <CardHeader>
              <CardTitle>Abonnement associé</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Plan</p>
                  <p class="font-semibold">{{ subscription.planName || 'Plan inconnu' }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Statut</p>
                  <Badge :variant="getSubscriptionStatusVariant(subscription.status)">
                    {{ getSubscriptionStatusLabel(subscription.status) }}
                  </Badge>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Date de début</p>
                  <p class="font-semibold">{{ formatDate(subscription.startDate) }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Date de fin</p>
                  <p class="font-semibold">{{ formatDate(subscription.endDate) }}</p>
                </div>
              </div>
              <div class="mt-4">
                <Button variant="outline" @click="viewSubscription">
                  Voir l'abonnement
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Colonne latérale -->
        <div class="space-y-6">
          <!-- Actions -->
          <Card>
            <CardHeader>
              <CardTitle>Actions</CardTitle>
            </CardHeader>
            <CardContent class="space-y-2">
              <Button variant="outline" class="w-full justify-start" @click="downloadInvoice">
                <Download class="mr-2 h-4 w-4" />
                Télécharger PDF
              </Button>
              <Button 
                v-if="invoice.status === 'PENDING'"
                variant="outline" 
                class="w-full justify-start"
                @click="markAsPaid"
              >
                <CheckCircle class="mr-2 h-4 w-4" />
                Marquer comme payée
              </Button>
              <Button 
                variant="outline" 
                class="w-full justify-start"
                @click="viewSubscription"
              >
                <FileText class="mr-2 h-4 w-4" />
                Voir l'abonnement
              </Button>
            </CardContent>
          </Card>

          <!-- Informations système -->
          <Card>
            <CardHeader>
              <CardTitle>Informations système</CardTitle>
            </CardHeader>
            <CardContent class="space-y-3">
              <div class="flex justify-between">
                <span class="text-sm text-muted-foreground">ID</span>
                <span class="text-sm font-mono">#{{ invoice.id }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-sm text-muted-foreground">Abonnement ID</span>
                <span class="text-sm font-mono">#{{ invoice.subscriptionId }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-sm text-muted-foreground">Organisation ID</span>
                <span class="text-sm font-mono">#{{ invoice.organizationId }}</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-12">
      <p class="text-muted-foreground">Facture introuvable</p>
      <Button variant="outline" class="mt-4" @click="goBack">
        Retour
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { billingService, type Invoice, type Subscription } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { ArrowLeft, Download, CheckCircle, FileText, Loader2 } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()

const invoiceId = computed(() => Number(route.params.id))
const invoice = ref<Invoice | null>(null)
const subscription = ref<Subscription | null>(null)
const loading = ref(false)

const loadInvoice = async () => {
  loading.value = true
  try {
    invoice.value = await billingService.getInvoiceById(invoiceId.value)
    if (invoice.value) {
      await loadSubscription()
    }
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger la facture',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadSubscription = async () => {
  if (!invoice.value) return
  try {
    subscription.value = await billingService.getSubscriptionById(invoice.value.subscriptionId)
  } catch (error: any) {
    console.error('Error loading subscription:', error)
  }
}

const goBack = () => {
  if (invoice.value) {
    router.push(`/billing/subscriptions/${invoice.value.subscriptionId}/invoices`)
  } else {
    router.push('/billing')
  }
}

const viewSubscription = () => {
  if (invoice.value) {
    router.push(`/billing/subscriptions/${invoice.value.subscriptionId}`)
  }
}

const downloadInvoice = async () => {
  if (!invoice.value) return
  try {
    toast({
      title: 'Téléchargement',
      description: `Téléchargement de la facture ${invoice.value.invoiceNumber}...`
    })

    const blob = await billingService.downloadInvoicePdf(invoiceId.value)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `invoice-${invoice.value.invoiceNumber}.html`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    toast({
      title: 'Téléchargement réussi',
      description: 'La facture a été téléchargée'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de télécharger la facture',
      variant: 'destructive'
    })
  }
}

const markAsPaid = async () => {
  if (!invoice.value) return
  if (!confirm('Marquer cette facture comme payée ?')) return
  loading.value = true
  try {
    await billingService.markInvoiceAsPaid(invoiceId.value)
    toast({
      title: 'Facture mise à jour',
      description: 'La facture a été marquée comme payée'
    })
    await loadInvoice()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de mettre à jour la facture',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const getStatusVariant = (status: string) => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    PAID: 'default',
    PENDING: 'outline',
    OVERDUE: 'destructive',
    CANCELLED: 'secondary'
  }
  return variants[status] || 'default'
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    PENDING: 'En attente',
    PAID: 'Payée',
    OVERDUE: 'En retard',
    CANCELLED: 'Annulée'
  }
  return labels[status] || status
}

const getSubscriptionStatusVariant = (status: string) => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    ACTIVE: 'default',
    PENDING: 'outline',
    CANCELLED: 'destructive',
    EXPIRED: 'secondary'
  }
  return variants[status] || 'default'
}

const getSubscriptionStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    ACTIVE: 'Actif',
    PENDING: 'En attente',
    CANCELLED: 'Annulé',
    EXPIRED: 'Expiré'
  }
  return labels[status] || status
}

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(amount)
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatDateRange = (start: string, end: string) => {
  if (!start || !end) return '-'
  const startDate = new Date(start).toLocaleDateString('fr-FR', { month: 'short', day: 'numeric' })
  const endDate = new Date(end).toLocaleDateString('fr-FR', { month: 'short', day: 'numeric' })
  return `${startDate} - ${endDate}`
}

onMounted(() => {
  loadInvoice()
})
</script>

