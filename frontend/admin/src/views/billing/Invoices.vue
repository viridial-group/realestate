<template>
  <div class="space-y-6">
    <!-- Header avec Actions -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="icon" @click="goBack">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">Factures</h1>
          <p class="text-muted-foreground mt-1" v-if="subscription">
            Abonnement #{{ subscriptionId }} - {{ subscription.organizationName || `Organisation #${subscription.organizationId}` }}
          </p>
          <p class="text-muted-foreground mt-1" v-else-if="organizationId">
            Organisation #{{ organizationId }}
          </p>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          Actualiser
        </Button>
        <Button v-if="subscriptionId" @click="generateInvoice" :disabled="loading">
          <FileText class="mr-2 h-4 w-4" />
          Générer une facture
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Total</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ invoices.length }}</div>
          <p class="text-xs text-muted-foreground mt-1">Factures</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>En attente</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-warning">{{ pendingCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">Factures</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Payées</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-success">{{ paidCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">Factures</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Montant total</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ formatCurrency(totalAmount) }}</div>
          <p class="text-xs text-muted-foreground mt-1">EUR</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Numéro de facture..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous</SelectItem>
                <SelectItem value="PENDING">En attente</SelectItem>
                <SelectItem value="PAID">Payée</SelectItem>
                <SelectItem value="OVERDUE">En retard</SelectItem>
                <SelectItem value="CANCELLED">Annulée</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex items-end">
            <Button variant="outline" @click="resetFilters" class="w-full">
              <X class="mr-2 h-4 w-4" />
              Réinitialiser
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Table des Factures -->
    <Card>
      <CardHeader>
        <CardTitle>Liste des factures</CardTitle>
        <CardDescription>Gérez toutes les factures de l'abonnement</CardDescription>
      </CardHeader>
      <CardContent class="p-0">
        <div v-if="loading" class="flex items-center justify-center py-12">
          <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
        <div v-else-if="filteredInvoices.length === 0" class="text-center py-12 text-muted-foreground">
          Aucune facture trouvée
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Numéro</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Montant HT</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">TVA</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Montant TTC</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Statut</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Date d'échéance</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Période</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-muted-foreground uppercase">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-border">
              <tr
                v-for="invoice in filteredInvoices"
                :key="invoice.id"
                class="hover:bg-muted/50 transition-colors cursor-pointer"
                @click="viewInvoice(invoice.id)"
              >
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium">{{ invoice.invoiceNumber }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  {{ formatCurrency(invoice.amount) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatCurrency(invoice.taxAmount) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold">
                  {{ formatCurrency(invoice.totalAmount) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge :variant="getStatusVariant(invoice.status)">
                    {{ getStatusLabel(invoice.status) }}
                  </Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatDate(invoice.dueDate) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  <div v-if="invoice.billingPeriodStart && invoice.billingPeriodEnd">
                    {{ formatDateRange(invoice.billingPeriodStart, invoice.billingPeriodEnd) }}
                  </div>
                  <span v-else>-</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right" @click.stop>
                  <DropdownMenu>
                    <DropdownMenuTrigger as-child>
                      <Button variant="ghost" size="sm">
                        <MoreVertical class="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem @click.stop="viewInvoice(invoice.id)">
                        <Eye class="mr-2 h-4 w-4" />
                        Voir les détails
                      </DropdownMenuItem>
                      <DropdownMenuItem @click.stop="downloadInvoice(invoice.id)">
                        <Download class="mr-2 h-4 w-4" />
                        Télécharger PDF
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        v-if="invoice.status === 'PENDING'"
                        @click.stop="markAsPaid(invoice.id)"
                      >
                        <CheckCircle class="mr-2 h-4 w-4" />
                        Marquer comme payée
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { billingService, type Invoice, type Subscription } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { ArrowLeft, RefreshCw, X, MoreVertical, Eye, FileText, Download, CheckCircle, Loader2 } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()

const subscriptionId = computed(() => {
  const id = route.params.subscriptionId || route.params.id
  return id ? Number(id) : null
})

const organizationId = computed(() => {
  const id = route.query.organizationId
  return id ? Number(id) : null
})

const subscription = ref<Subscription | null>(null)
const invoices = ref<Invoice[]>([])
const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref('all')

const filteredInvoices = computed(() => {
  let filtered = [...invoices.value]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(invoice =>
      invoice.invoiceNumber.toLowerCase().includes(query)
    )
  }

  if (selectedStatus.value && selectedStatus.value !== 'all') {
    filtered = filtered.filter(invoice => invoice.status === selectedStatus.value)
  }

  return filtered
})

const pendingCount = computed(() => invoices.value.filter(i => i.status === 'PENDING').length)
const paidCount = computed(() => invoices.value.filter(i => i.status === 'PAID').length)
const totalAmount = computed(() => invoices.value.reduce((sum, i) => sum + i.totalAmount, 0))

const loadSubscription = async () => {
  if (!subscriptionId.value) return
  try {
    subscription.value = await billingService.getSubscriptionById(subscriptionId.value)
  } catch (error: any) {
    console.error('Error loading subscription:', error)
  }
}

const loadInvoices = async () => {
  loading.value = true
  try {
    if (subscriptionId.value) {
      invoices.value = await billingService.getInvoicesBySubscription(subscriptionId.value)
    } else if (organizationId.value) {
      invoices.value = await billingService.getAllInvoices({ organizationId: organizationId.value })
    } else {
      invoices.value = await billingService.getAllInvoices()
    }
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les factures',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadInvoices()
  if (subscriptionId.value) {
    loadSubscription()
  }
}

const generateInvoice = async () => {
  if (!subscriptionId.value) return
  if (!confirm('Générer une nouvelle facture pour cet abonnement ?')) return
  
  loading.value = true
  try {
    const invoice = await billingService.generateInvoiceForSubscription(subscriptionId.value)
    toast({
      title: 'Facture générée',
      description: `La facture ${invoice.invoiceNumber} a été créée avec succès`
    })
    await loadInvoices()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de générer la facture',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const viewInvoice = (invoiceId: number) => {
  router.push(`/billing/invoices/${invoiceId}`)
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

const markAsPaid = async (invoiceId: number) => {
  if (!confirm('Marquer cette facture comme payée ?')) return
  loading.value = true
  try {
    await billingService.markInvoiceAsPaid(invoiceId)
    toast({
      title: 'Facture mise à jour',
      description: 'La facture a été marquée comme payée'
    })
    await loadInvoices()
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

const handleSearch = () => {
  // Filtering is handled by computed property
}

const handleFilter = () => {
  // Filtering is handled by computed property
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedStatus.value = 'all'
}

const goBack = () => {
  if (subscriptionId.value) {
    router.push(`/billing/subscriptions/${subscriptionId.value}`)
  } else {
    router.push('/billing')
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

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(amount)
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR')
}

const formatDateRange = (start: string, end: string) => {
  if (!start || !end) return '-'
  const startDate = new Date(start).toLocaleDateString('fr-FR', { month: 'short', day: 'numeric' })
  const endDate = new Date(end).toLocaleDateString('fr-FR', { month: 'short', day: 'numeric' })
  return `${startDate} - ${endDate}`
}

onMounted(async () => {
  if (subscriptionId.value) {
    await loadSubscription()
  }
  await loadInvoices()
})
</script>

