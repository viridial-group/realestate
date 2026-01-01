<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold">Facturation</h1>
        <p class="text-muted-foreground mt-1">Gérez les abonnements et factures</p>
      </div>
      <Button @click="refreshData" variant="outline" :disabled="loading">
        <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
        Actualiser
      </Button>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByStatus('ACTIVE')">
        <CardHeader class="pb-2">
          <CardDescription>Abonnements Actifs</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-green-600">{{ stats.activeSubscriptions || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">En cours</p>
        </CardContent>
      </Card>
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByStatus('PENDING')">
        <CardHeader class="pb-2">
          <CardDescription>En Attente</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-yellow-600">{{ stats.pendingPayments || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Paiements</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Revenus ce Mois</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-primary">{{ formatCurrency(stats.monthlyRevenue || 0) }}</div>
          <p class="text-xs text-muted-foreground mt-1">EUR</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Revenus Total</CardDescription>
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
                <SelectItem value="">Tous</SelectItem>
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
                <SelectItem value="">Toutes</SelectItem>
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
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
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
                <td class="px-6 py-4">
                  <div class="text-sm font-medium">{{ subscription.organizationName }}</div>
                  <div class="text-sm text-muted-foreground">{{ subscription.organizationEmail }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge>{{ subscription.plan }}</Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  {{ formatCurrency(subscription.amount) }}
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
import { useToast } from '@/components/ui/toast'
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
import { RefreshCw, X, MoreVertical, Eye, FileText, XCircle } from 'lucide-vue-next'

const { toast } = useToast()
const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref('')
const dateRange = ref('')

interface Subscription {
  id: number
  organizationId: number
  organizationName: string
  organizationEmail: string
  plan: string
  amount: number
  status: 'ACTIVE' | 'PENDING' | 'CANCELLED' | 'EXPIRED'
  startDate: string
  endDate: string
}

const subscriptions = ref<Subscription[]>([])
const stats = ref({
  activeSubscriptions: 0,
  pendingPayments: 0,
  monthlyRevenue: 0,
  totalRevenue: 0
})

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

  if (selectedStatus.value) {
    filtered = filtered.filter((s) => s.status === selectedStatus.value)
  }

  return filtered
})

const handleSearch = () => {
  loadSubscriptions()
}

const handleFilter = () => {
  loadSubscriptions()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedStatus.value = ''
  dateRange.value = ''
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

const viewSubscription = (id: number) => {
  // TODO: Navigate to subscription details
  toast({
    title: 'Détails de l\'abonnement',
    description: `Affichage de l'abonnement #${id}`
  })
}

const viewInvoices = (id: number) => {
  // TODO: Navigate to invoices
  toast({
    title: 'Factures',
    description: `Affichage des factures pour l'abonnement #${id}`
  })
}

const cancelSubscription = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir annuler cet abonnement ?')) return
  loading.value = true
  try {
    // TODO: Implement cancel subscription
    toast({
      title: 'Abonnement annulé',
      description: 'L\'abonnement a été annulé avec succès'
    })
    await loadSubscriptions()
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

const loadSubscriptions = async () => {
  loading.value = true
  try {
    // TODO: Load from API
    subscriptions.value = []
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
    // TODO: Load from API
    stats.value = {
      activeSubscriptions: 0,
      pendingPayments: 0,
      monthlyRevenue: 0,
      totalRevenue: 0
    }
  } catch (error) {
    console.error('Error loading stats:', error)
  }
}

onMounted(() => {
  loadSubscriptions()
  loadStats()
})
</script>

