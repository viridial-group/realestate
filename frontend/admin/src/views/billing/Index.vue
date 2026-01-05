<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2.5xl font-bold">Facturation</h1>
        <p class="text-muted-foreground mt-1">Gérez les abonnements et factures</p>
      </div>
      <Button @click="refreshData" variant="outline" :disabled="loading">
        <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
        Actualiser
      </Button>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <!-- Abonnements Actifs Card -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #33d484;" @click="filterByStatus('ACTIVE')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Abonnements Actifs</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(51, 212, 132, 0.1);">
            <CheckCircle class="h-5 w-5" style="color: #33d484;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #33d484;">{{ stats.activeSubscriptions || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">En cours</p>
        </CardContent>
      </Card>
      
      <!-- En Attente Card -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #fdb022;" @click="filterByStatus('PENDING')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>En Attente</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(253, 176, 34, 0.1);">
            <Clock class="h-5 w-5" style="color: #fdb022;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #fdb022;">{{ stats.pendingPayments || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Paiements</p>
        </CardContent>
      </Card>
      
      <!-- Revenus ce Mois Card -->
      <Card class="relative overflow-hidden border-l-4" style="border-left-color: #04c9ff;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Revenus ce Mois</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(4, 201, 255, 0.1);">
            <DollarSign class="h-5 w-5" style="color: #04c9ff;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #04c9ff;">{{ formatCurrency(stats.monthlyRevenue || 0) }}</div>
          <p class="text-xs text-muted-foreground mt-1">EUR</p>
        </CardContent>
      </Card>
      
      <!-- Revenus Total Card -->
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Revenus Total</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <TrendingUp class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ formatCurrency(stats.totalRevenue || 0) }}</div>
          <p class="text-xs text-muted-foreground mt-1">EUR</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Organisation, email..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous</SelectItem>
                <SelectItem value="ACTIVE">Actif</SelectItem>
                <SelectItem value="PENDING">En attente</SelectItem>
                <SelectItem value="CANCELLED">Annulé</SelectItem>
                <SelectItem value="EXPIRED">Expiré</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Période</Label>
            <Select v-model="dateRange" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Toutes" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Toutes</SelectItem>
                <SelectItem value="today">Aujourd'hui</SelectItem>
                <SelectItem value="week">Cette semaine</SelectItem>
                <SelectItem value="month">Ce mois</SelectItem>
                <SelectItem value="year">Cette année</SelectItem>
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

    <!-- Table des Abonnements -->
    <Card>
      <CardHeader>
        <CardTitle>Abonnements</CardTitle>
        <CardDescription>Liste de tous les abonnements actifs et inactifs</CardDescription>
      </CardHeader>
      <CardContent class="p-0">
        <!-- Actions en masse -->
        <div
          v-if="selectedIds.length > 0"
          class="border-b bg-muted/30 p-4 flex items-center justify-between"
        >
          <div class="text-sm text-muted-foreground">
            {{ selectedIds.length }} abonnement(s) sélectionné(s)
          </div>
          <div class="flex gap-2">
            <Button variant="outline" size="sm" @click="bulkCancel" class="text-destructive" :disabled="loading">
              <XCircle class="mr-2 h-4 w-4" />
              Annuler
            </Button>
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">
                  <Checkbox v-model="selectAll" />
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Organisation</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Plan</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Montant</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Statut</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Date début</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Date fin</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-muted-foreground uppercase">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-border">
              <tr
                v-for="subscription in filteredSubscriptions"
                :key="subscription.id"
                class="hover:bg-muted/50 transition-colors"
              >
                <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                  <Checkbox
                    :model-value="selectedIds.includes(subscription.id)"
                    @update:model-value="(val) => handleSubscriptionCheckboxChange(subscription.id, Boolean(val))"
                  />
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm font-medium">{{ subscription.organizationName }}</div>
                  <div class="text-sm text-muted-foreground">{{ subscription.organizationEmail }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge>{{ subscription.planName || 'Plan inconnu' }}</Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  {{ formatCurrency(subscription.amount || 0) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge :variant="getStatusVariant(subscription.status)">
                    {{ getStatusLabel(subscription.status) }}
                  </Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatDate(subscription.startDate) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatDate(subscription.endDate) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right" @click.stop>
                  <DropdownMenu>
                    <DropdownMenuTrigger as-child>
                      <Button variant="ghost" size="sm">
                        <MoreVertical class="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem @click.stop="viewSubscription(subscription.id)">
                        <Eye class="mr-2 h-4 w-4" />
                        Voir les détails
                      </DropdownMenuItem>
                      <DropdownMenuItem @click.stop="viewInvoices(subscription.id)">
                        <FileText class="mr-2 h-4 w-4" />
                        Factures
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        v-if="subscription.status === 'ACTIVE'"
                        @click.stop="cancelSubscription(subscription.id)"
                        class="text-destructive"
                      >
                        <XCircle class="mr-2 h-4 w-4" />
                        Annuler
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
import { useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { billingService, organizationService, useAuthStore, type Subscription, type BillingStats } from '@viridial/shared'
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
import { RefreshCw, X, MoreVertical, Eye, FileText, XCircle, CheckCircle, Clock, DollarSign, TrendingUp } from 'lucide-vue-next'
import { Checkbox } from '@/components/ui/checkbox'

const { toast } = useToast()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref('all')
const dateRange = ref('all')

const subscriptions = ref<Subscription[]>([])
const stats = ref<BillingStats>({
  activeSubscriptions: 0,
  pendingPayments: 0,
  monthlyRevenue: 0,
  totalRevenue: 0
})
const selectedIds = ref<number[]>([])
const organizationsMap = ref<Record<number, { name: string; email: string }>>({})

const filteredSubscriptions = computed(() => {
  let filtered = [...subscriptions.value]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (s) =>
        s.organizationName?.toLowerCase().includes(query) ||
        s.organizationEmail?.toLowerCase().includes(query)
    )
  }

  if (selectedStatus.value && selectedStatus.value !== 'all') {
    filtered = filtered.filter((s) => s.status === selectedStatus.value)
  }

  return filtered
})

const selectAll = computed({
  get: (): boolean | 'indeterminate' => {
    if (filteredSubscriptions.value.length === 0) return false
    if (selectedIds.value.length === 0) return false
    const allSelected = filteredSubscriptions.value.every(s => selectedIds.value.includes(s.id))
    const someSelected = filteredSubscriptions.value.some(s => selectedIds.value.includes(s.id))
    if (allSelected) return true
    if (someSelected) return 'indeterminate'
    return false
  },
  set: (value: boolean | 'indeterminate') => {
    if (value === true || value === 'indeterminate') {
      filteredSubscriptions.value.forEach(s => {
        if (!selectedIds.value.includes(s.id)) {
          selectedIds.value.push(s.id)
        }
      })
    } else {
      const filteredIds = filteredSubscriptions.value.map(s => s.id)
      selectedIds.value = selectedIds.value.filter(id => !filteredIds.includes(id))
    }
  }
})

const handleSubscriptionCheckboxChange = (id: number, checked: boolean) => {
  if (checked) {
    if (!selectedIds.value.includes(id)) {
      selectedIds.value.push(id)
    }
  } else {
    const index = selectedIds.value.indexOf(id)
    if (index > -1) {
      selectedIds.value.splice(index, 1)
    }
  }
}

const handleSearch = () => {
  loadSubscriptions()
}

const handleFilter = () => {
  loadSubscriptions()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedStatus.value = 'all'
  dateRange.value = 'all'
  loadSubscriptions()
}

const filterByStatus = (status: string) => {
  selectedStatus.value = status
  loadSubscriptions()
}

const refreshData = () => {
  loadSubscriptions()
  loadStats()
}

const viewSubscription = async (id: number) => {
  try {
    router.push(`/billing/subscriptions/${id}`)
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de naviguer vers les détails de l\'abonnement',
      variant: 'destructive'
    })
  }
}

const viewInvoices = async (id: number) => {
  try {
    router.push(`/billing/subscriptions/${id}/invoices`)
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de naviguer vers les factures',
      variant: 'destructive'
    })
  }
}

const cancelSubscription = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir annuler cet abonnement ?')) return
  loading.value = true
  try {
    const cancelledBy = authStore.user?.id || 0
    await billingService.cancelSubscription(id, cancelledBy)
    toast({
      title: 'Abonnement annulé',
      description: 'L\'abonnement a été annulé avec succès'
    })
    await loadSubscriptions()
    await loadStats()
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

const bulkCancel = async () => {
  if (!confirm(`Êtes-vous sûr de vouloir annuler ${selectedIds.value.length} abonnement(s) ?`)) return
  loading.value = true
  try {
    const cancelledBy = authStore.user?.id || 0
    const promises = selectedIds.value.map(id => billingService.cancelSubscription(id, cancelledBy))
    await Promise.all(promises)
    toast({
      title: 'Abonnements annulés',
      description: `${selectedIds.value.length} abonnement(s) annulé(s)`
    })
    selectedIds.value = []
    await loadSubscriptions()
    await loadStats()
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

const loadOrganizations = async () => {
  try {
    const result = await organizationService.getAll()
    const map: Record<number, { name: string; email: string }> = {}
    if (result.organizations && Array.isArray(result.organizations)) {
      result.organizations.forEach(org => {
        map[org.id] = {
          name: org.name,
          email: org.email || org.domain || ''
        }
      })
    }
    organizationsMap.value = map
  } catch (error) {
    console.error('Error loading organizations:', error)
  }
}

const loadSubscriptions = async () => {
  loading.value = true
  try {
    const params: any = {}
    if (selectedStatus.value && selectedStatus.value !== 'all') {
      params.status = selectedStatus.value
    }
    
    const subs = await billingService.getAllSubscriptions(params)
    
    // Enrichir avec les noms d'organisations
    subscriptions.value = subs.map(sub => ({
      ...sub,
      organizationName: sub.organizationName || organizationsMap.value[sub.organizationId]?.name || `Organisation #${sub.organizationId}`,
      organizationEmail: sub.organizationEmail || organizationsMap.value[sub.organizationId]?.email || '',
      plan: sub.planName || 'Plan inconnu',
      amount: sub.amount || 0
    }))
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les abonnements',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    // Utiliser le service pour récupérer les stats
    const billingStats = await billingService.getStats()
    stats.value = billingStats
  } catch (error) {
    console.error('Error loading stats:', error)
    // En cas d'erreur, calculer les stats depuis les abonnements comme fallback
    try {
      const subs = await billingService.getAllSubscriptions()
      const activeSubscriptions = subs.filter(s => s.status === 'ACTIVE').length
      const pendingPayments = subs.filter(s => s.status === 'PENDING').length
      
      // Calculer les revenus
      const now = new Date()
      const currentMonth = now.getMonth()
      const currentYear = now.getFullYear()
      
      const monthlyRevenue = subs
        .filter(s => {
          if (s.status !== 'ACTIVE') return false
          const startDate = new Date(s.startDate)
          return startDate.getMonth() === currentMonth && startDate.getFullYear() === currentYear
        })
        .reduce((sum, s) => sum + (s.amount || 0), 0)
      
      const totalRevenue = subs
        .filter(s => s.status === 'ACTIVE')
        .reduce((sum, s) => sum + (s.amount || 0), 0)
      
      stats.value = {
        activeSubscriptions,
        pendingPayments,
        monthlyRevenue,
        totalRevenue
      }
    } catch (fallbackError) {
      console.error('Error in fallback stats calculation:', fallbackError)
      // En cas d'erreur, garder les valeurs par défaut
      stats.value = {
        activeSubscriptions: 0,
        pendingPayments: 0,
        monthlyRevenue: 0,
        totalRevenue: 0
      }
    }
  }
}

onMounted(async () => {
  await loadOrganizations()
  await loadSubscriptions()
  await loadStats()
})
</script>

