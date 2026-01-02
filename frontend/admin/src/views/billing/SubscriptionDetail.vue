<template>
  <div class="space-y-6">
    <!-- Header avec Actions -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="icon" @click="goBack">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">Détails de l'abonnement</h1>
          <p class="text-muted-foreground mt-1" v-if="subscription">
            {{ subscription.organizationName || `Organisation #${subscription.organizationId}` }}
          </p>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="viewInvoices" :disabled="loading">
          <FileText class="mr-2 h-4 w-4" />
          Voir les factures
        </Button>
        <Button 
          v-if="subscription?.status === 'ACTIVE'" 
          variant="destructive" 
          @click="cancelSubscription" 
          :disabled="loading"
        >
          <XCircle class="mr-2 h-4 w-4" />
          Annuler l'abonnement
        </Button>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>

    <div v-else-if="subscription" class="space-y-6">
      <!-- Informations principales -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Colonne principale -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Informations de l'abonnement -->
          <Card>
            <CardHeader>
              <CardTitle>Informations de l'abonnement</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Plan</p>
                  <p class="font-semibold text-lg">{{ subscription.planName || 'Plan inconnu' }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Statut</p>
                  <Badge :variant="getStatusVariant(subscription.status)">
                    {{ getStatusLabel(subscription.status) }}
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
                <div class="space-y-1" v-if="subscription.trialEndDate">
                  <p class="text-sm text-muted-foreground">Fin de période d'essai</p>
                  <p class="font-semibold">{{ formatDate(subscription.trialEndDate) }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Renouvellement automatique</p>
                  <Badge :variant="subscription.autoRenew ? 'default' : 'secondary'">
                    {{ subscription.autoRenew ? 'Activé' : 'Désactivé' }}
                  </Badge>
                </div>
                <div class="space-y-1" v-if="subscription.amount">
                  <p class="text-sm text-muted-foreground">Montant</p>
                  <p class="font-semibold text-lg">{{ formatCurrency(subscription.amount) }}</p>
                </div>
                <div class="space-y-1" v-if="subscription.cancelledAt">
                  <p class="text-sm text-muted-foreground">Date d'annulation</p>
                  <p class="font-semibold">{{ formatDate(subscription.cancelledAt) }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Fonctionnalités de l'abonnement -->
          <Card v-if="plan">
            <CardHeader>
              <CardTitle>Fonctionnalités de l'abonnement</CardTitle>
              <p v-if="plan.description" class="text-sm text-muted-foreground mt-1">
                {{ plan.description }}
              </p>
            </CardHeader>
            <CardContent>
              <!-- Liste des fonctionnalités -->
              <div v-if="parsedFeatures && parsedFeatures.length > 0" class="space-y-3">
                <h4 class="text-sm font-semibold mb-3">Fonctionnalités incluses</h4>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
                  <div
                    v-for="(feature, index) in parsedFeatures"
                    :key="index"
                    class="flex items-start gap-2 p-2 rounded-lg bg-muted/50"
                  >
                    <svg
                      class="h-5 w-5 text-primary mt-0.5 flex-shrink-0"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                    <span class="text-sm">{{ feature }}</span>
                  </div>
                </div>
              </div>
              <div v-else class="text-sm text-muted-foreground">
                Aucune fonctionnalité spécifiée pour ce plan
              </div>

              <!-- Quotas -->
              <div v-if="plan.quotas && Object.keys(plan.quotas).length > 0" class="mt-6 space-y-3">
                <h4 class="text-sm font-semibold mb-3">Limites et quotas</h4>
                <div class="space-y-2">
                  <div
                    v-for="(value, key) in plan.quotas"
                    :key="key"
                    class="flex items-center justify-between p-3 rounded-lg border"
                  >
                    <span class="text-sm font-medium capitalize">
                      {{ formatQuotaKey(key) }}
                    </span>
                    <Badge variant="outline">
                      {{ formatQuotaValue(value, key) }}
                    </Badge>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Organisation -->
          <Card>
            <CardHeader>
              <CardTitle>Organisation</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Nom</p>
                  <p class="font-semibold">{{ subscription.organizationName || `Organisation #${subscription.organizationId}` }}</p>
                </div>
                <div class="space-y-1" v-if="subscription.organizationEmail">
                  <p class="text-sm text-muted-foreground">Email</p>
                  <p class="font-semibold">{{ subscription.organizationEmail }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Factures récentes -->
          <Card>
            <CardHeader class="flex items-center justify-between">
              <CardTitle>Factures récentes</CardTitle>
              <Button variant="ghost" size="sm" @click="loadInvoices">
                <RefreshCw class="h-4 w-4" :class="{ 'animate-spin': loadingInvoices }" />
              </Button>
            </CardHeader>
            <CardContent>
              <div v-if="loadingInvoices" class="flex items-center justify-center py-8">
                <Loader2 class="h-6 w-6 animate-spin text-muted-foreground" />
              </div>
              <div v-else-if="invoices.length === 0" class="text-center py-8 text-muted-foreground">
                Aucune facture trouvée
              </div>
              <div v-else class="space-y-4">
                <div
                  v-for="invoice in invoices"
                  :key="invoice.id"
                  class="flex items-center justify-between p-4 border rounded-lg hover:bg-muted/50 transition-colors cursor-pointer"
                  @click="viewInvoice(invoice.id)"
                >
                  <div class="flex-1">
                    <div class="flex items-center gap-3">
                      <p class="font-semibold">{{ invoice.invoiceNumber }}</p>
                      <Badge :variant="getInvoiceStatusVariant(invoice.status)">
                        {{ getInvoiceStatusLabel(invoice.status) }}
                      </Badge>
                    </div>
                    <p class="text-sm text-muted-foreground mt-1">
                      {{ formatDate(invoice.dueDate) }} • {{ formatCurrency(invoice.totalAmount) }}
                    </p>
                  </div>
                  <div class="flex items-center gap-2">
                    <Button variant="ghost" size="icon" @click.stop="downloadInvoice(invoice.id)">
                      <Download class="h-4 w-4" />
                    </Button>
                    <Button variant="ghost" size="icon" @click.stop="viewInvoice(invoice.id)">
                      <Eye class="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Colonne latérale -->
        <div class="space-y-6">
          <!-- Actions rapides -->
          <Card>
            <CardHeader>
              <CardTitle>Actions</CardTitle>
            </CardHeader>
            <CardContent class="space-y-2">
              <Button variant="outline" class="w-full justify-start" @click="viewInvoices">
                <FileText class="mr-2 h-4 w-4" />
                Toutes les factures
              </Button>
              <Button 
                v-if="subscription.status === 'ACTIVE' && !subscription.autoRenew"
                variant="outline" 
                class="w-full justify-start"
                @click="enableAutoRenew"
              >
                <RefreshCw class="mr-2 h-4 w-4" />
                Activer le renouvellement
              </Button>
              <Button 
                v-if="subscription.status === 'ACTIVE' && subscription.autoRenew"
                variant="outline" 
                class="w-full justify-start"
                @click="disableAutoRenew"
              >
                <XCircle class="mr-2 h-4 w-4" />
                Désactiver le renouvellement
              </Button>
              <Button 
                v-if="subscription.status === 'CANCELLED' || subscription.status === 'EXPIRED'"
                variant="outline" 
                class="w-full justify-start"
                @click="renewSubscription"
              >
                <RefreshCw class="mr-2 h-4 w-4" />
                Renouveler l'abonnement
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
                <span class="text-sm font-mono">#{{ subscription.id }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-sm text-muted-foreground">Plan ID</span>
                <span class="text-sm font-mono">#{{ subscription.planId }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-sm text-muted-foreground">Organisation ID</span>
                <span class="text-sm font-mono">#{{ subscription.organizationId }}</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-12">
      <p class="text-muted-foreground">Abonnement introuvable</p>
      <Button variant="outline" class="mt-4" @click="goBack">
        Retour
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { billingService, useAuthStore, type Subscription, type Invoice, type Plan } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { ArrowLeft, FileText, XCircle, RefreshCw, Download, Eye, Loader2 } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()
const authStore = useAuthStore()

const subscriptionId = computed(() => Number(route.params.id))
const subscription = ref<Subscription | null>(null)
const plan = ref<Plan | null>(null)
const invoices = ref<Invoice[]>([])
const loading = ref(false)
const loadingInvoices = ref(false)
const loadingPlan = ref(false)

const loadSubscription = async () => {
  loading.value = true
  try {
    subscription.value = await billingService.getSubscriptionById(subscriptionId.value)
    await Promise.all([loadInvoices(), loadPlan()])
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger l\'abonnement',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadPlan = async () => {
  if (!subscription.value?.planId) return
  
  loadingPlan.value = true
  try {
    plan.value = await billingService.getPlanById(subscription.value.planId)
  } catch (error: any) {
    console.error('Error loading plan:', error)
    // Ne pas afficher d'erreur toast car ce n'est pas critique
  } finally {
    loadingPlan.value = false
  }
}

const loadInvoices = async () => {
  loadingInvoices.value = true
  try {
    invoices.value = await billingService.getInvoicesBySubscription(subscriptionId.value)
  } catch (error: any) {
    console.error('Error loading invoices:', error)
  } finally {
    loadingInvoices.value = false
  }
}

const goBack = () => {
  router.push('/billing')
}

const viewInvoices = () => {
  router.push(`/billing/subscriptions/${subscriptionId.value}/invoices`)
}

const viewInvoice = (invoiceId: number) => {
  router.push(`/billing/invoices/${invoiceId}`)
}

const cancelSubscription = async () => {
  if (!confirm('Êtes-vous sûr de vouloir annuler cet abonnement ?')) return
  loading.value = true
  try {
    const cancelledBy = authStore.user?.id || 0
    await billingService.cancelSubscription(subscriptionId.value, cancelledBy)
    toast({
      title: 'Abonnement annulé',
      description: 'L\'abonnement a été annulé avec succès'
    })
    await loadSubscription()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const renewSubscription = async () => {
  if (!confirm('Êtes-vous sûr de vouloir renouveler cet abonnement ?')) return
  loading.value = true
  try {
    await billingService.renewSubscription(subscriptionId.value)
    toast({
      title: 'Abonnement renouvelé',
      description: 'L\'abonnement a été renouvelé avec succès'
    })
    await loadSubscription()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const enableAutoRenew = async () => {
  if (!subscription.value) return
  loading.value = true
  try {
    await billingService.updateSubscription(subscriptionId.value, { autoRenew: true })
    toast({
      title: 'Renouvellement activé',
      description: 'Le renouvellement automatique a été activé'
    })
    await loadSubscription()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const disableAutoRenew = async () => {
  if (!subscription.value) return
  loading.value = true
  try {
    await billingService.updateSubscription(subscriptionId.value, { autoRenew: false })
    toast({
      title: 'Renouvellement désactivé',
      description: 'Le renouvellement automatique a été désactivé'
    })
    await loadSubscription()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const downloadInvoice = async (invoiceId: number) => {
  try {
    const invoice = invoices.value.find(i => i.id === invoiceId)
    if (!invoice) return

    toast({
      title: 'Téléchargement',
      description: `Téléchargement de la facture ${invoice.invoiceNumber}...`
    })

    const blob = await billingService.downloadInvoicePdf(invoiceId)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `invoice-${invoice.invoiceNumber}.html`
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

const getStatusVariant = (status: string) => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    ACTIVE: 'default',
    PENDING: 'outline',
    CANCELLED: 'destructive',
    EXPIRED: 'secondary'
  }
  return variants[status] || 'default'
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    ACTIVE: 'Actif',
    PENDING: 'En attente',
    CANCELLED: 'Annulé',
    EXPIRED: 'Expiré'
  }
  return labels[status] || status
}

const getInvoiceStatusVariant = (status: string) => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    PAID: 'default',
    PENDING: 'outline',
    OVERDUE: 'destructive',
    CANCELLED: 'secondary'
  }
  return variants[status] || 'default'
}

const getInvoiceStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    PENDING: 'En attente',
    PAID: 'Payée',
    OVERDUE: 'En retard',
    CANCELLED: 'Annulée'
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

const formatQuotaKey = (key: string) => {
  // Convertir les clés de quota en format lisible
  const keyMap: Record<string, string> = {
    'maxProperties': 'Propriétés maximum',
    'maxUsers': 'Utilisateurs maximum',
    'maxOrganizations': 'Organisations maximum',
    'maxStorage': 'Stockage maximum',
    'maxApiCalls': 'Appels API maximum',
    'maxDocuments': 'Documents maximum',
    'maxNotifications': 'Notifications maximum',
    'maxWorkflows': 'Workflows maximum',
    'storageSize': 'Taille de stockage',
    'apiCallsPerMonth': 'Appels API par mois',
    'documentsPerMonth': 'Documents par mois'
  }
  return keyMap[key] || key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase()).trim()
}

const formatQuotaValue = (value: number, key: string) => {
  // Formater les valeurs selon le type de quota
  if (key.toLowerCase().includes('storage') || key.toLowerCase().includes('size')) {
    if (value >= 1073741824) {
      return `${(value / 1073741824).toFixed(2)} GB`
    } else if (value >= 1048576) {
      return `${(value / 1048576).toFixed(2)} MB`
    } else if (value >= 1024) {
      return `${(value / 1024).toFixed(2)} KB`
    }
    return `${value} bytes`
  }
  
  if (value === -1 || value === Infinity) {
    return 'Illimité'
  }
  
  return value.toLocaleString('fr-FR')
}

const parsedFeatures = computed((): string[] => {
  if (!plan.value?.features) return []
  
  const features = plan.value.features as string | string[] | undefined
  
  // Si c'est déjà un tableau, le retourner tel quel
  if (Array.isArray(features)) {
    return features
      .filter((f: any) => f != null && String(f).trim().length > 0)
      .map((f: any) => String(f).trim())
  }
  
  // Si c'est une chaîne, essayer de la parser
  if (typeof features === 'string') {
    const featuresStr = (features as string).trim()
    if (!featuresStr) return []
    
    try {
      const parsed: any = JSON.parse(featuresStr)
      if (Array.isArray(parsed)) {
        return parsed
          .filter((f: any) => f != null && String(f).trim().length > 0)
          .map((f: any) => String(f).trim())
      }
      // Si ce n'est pas un tableau après parsing, retourner un tableau avec la chaîne
      return [featuresStr]
    } catch (e) {
      // Si ce n'est pas du JSON valide, traiter comme une seule chaîne
      return [featuresStr]
    }
  }
  
  return []
})

onMounted(() => {
  loadSubscription()
})
</script>

