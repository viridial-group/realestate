<template>
  <Card>
    <CardHeader>
      <CardTitle class="flex items-center gap-2">
        <TrendingUp class="h-5 w-5 text-primary" />
        Historique des Prix
      </CardTitle>
      <CardDescription>
        Évolution du prix de la propriété dans le temps
      </CardDescription>
    </CardHeader>
    <CardContent>
      <div v-if="loading" class="flex items-center justify-center p-12">
        <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
      </div>

      <div v-else-if="error" class="text-center p-12">
        <AlertCircle class="h-8 w-8 text-destructive mx-auto mb-2" />
        <p class="text-sm text-muted-foreground">{{ error }}</p>
      </div>

      <div v-else-if="!stats || priceHistory.length === 0" class="text-center p-12">
        <TrendingUp class="h-12 w-12 text-muted-foreground mx-auto mb-4" />
        <p class="text-sm text-muted-foreground">Aucun historique de prix disponible</p>
        <p class="text-xs text-muted-foreground mt-2">L'historique sera créé automatiquement lors des changements de prix</p>
      </div>

      <div v-else class="space-y-6">
        <!-- Statistiques -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Prix actuel</p>
            <p class="text-lg font-bold">{{ formatPrice(stats.currentPrice) }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Prix initial</p>
            <p class="text-lg font-semibold">{{ formatPrice(stats.initialPrice) }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Variation totale</p>
            <div class="flex items-center gap-2">
              <p 
                :class="[
                  'text-lg font-semibold',
                  stats.totalChangePercent > 0 ? 'text-green-600' : 
                  stats.totalChangePercent < 0 ? 'text-red-600' : 'text-muted-foreground'
                ]"
              >
                {{ stats.totalChangePercent > 0 ? '+' : '' }}{{ stats.totalChangePercent.toFixed(2) }}%
              </p>
              <component 
                :is="stats.totalChangePercent > 0 ? TrendingUp : stats.totalChangePercent < 0 ? TrendingDown : Minus"
                :class="[
                  'h-4 w-4',
                  stats.totalChangePercent > 0 ? 'text-green-600' : 
                  stats.totalChangePercent < 0 ? 'text-red-600' : 'text-muted-foreground'
                ]"
              />
            </div>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Changements</p>
            <p class="text-lg font-semibold">{{ stats.totalChanges }}</p>
          </div>
        </div>

        <!-- Graphique -->
        <div class="h-64">
          <Line
            :data="chartData"
            :options="chartOptions"
          />
        </div>

        <!-- Bouton pour ajouter un changement de prix -->
        <div class="flex justify-end">
          <Button @click="openAddPriceDialog" variant="outline" size="sm">
            <Plus class="mr-2 h-4 w-4" />
            Ajouter un changement de prix
          </Button>
        </div>

        <!-- Liste des changements -->
        <div class="space-y-2">
          <h4 class="text-sm font-semibold">Historique détaillé</h4>
          <div class="space-y-2 max-h-64 overflow-y-auto">
            <div
              v-for="(entry, index) in priceHistory"
              :key="entry.id"
              class="flex items-center justify-between p-3 border rounded-lg hover:bg-muted/50 transition-colors"
            >
              <div class="flex items-center gap-4">
                <div class="flex flex-col items-center min-w-[60px]">
                  <div 
                    :class="[
                      'w-3 h-3 rounded-full',
                      index === 0 ? 'bg-primary' : 
                      getPriceChange(index) > 0 ? 'bg-green-500' : 
                      getPriceChange(index) < 0 ? 'bg-red-500' : 'bg-gray-400'
                    ]"
                  />
                  <div 
                    v-if="index < priceHistory.length - 1"
                    :class="[
                      'w-0.5 h-8',
                      getPriceChange(index) > 0 ? 'bg-green-500' : 
                      getPriceChange(index) < 0 ? 'bg-red-500' : 'bg-gray-400'
                    ]"
                  />
                </div>
                <div>
                  <p class="font-semibold">{{ formatPrice(entry.price) }}</p>
                  <p class="text-xs text-muted-foreground">
                    {{ formatDate(entry.changeDate) }}
                  </p>
                  <p v-if="entry.changeReason" class="text-xs text-muted-foreground mt-1">
                    {{ entry.changeReason }}
                  </p>
                </div>
              </div>
              <div class="text-right">
                <Badge 
                  v-if="index > 0"
                  :variant="getPriceChange(index) > 0 ? 'default' : getPriceChange(index) < 0 ? 'destructive' : 'secondary'"
                  class="text-xs"
                >
                  {{ getPriceChange(index) > 0 ? '+' : '' }}{{ formatPriceChange(index) }}
                </Badge>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Dialog pour ajouter un changement de prix -->
      <Dialog v-model:open="addPriceDialogOpen">
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Ajouter un changement de prix</DialogTitle>
          </DialogHeader>
          <div class="space-y-4 py-4">
            <div class="space-y-2">
              <Label for="price">Nouveau prix *</Label>
              <Input
                id="price"
                v-model="newPriceForm.price"
                type="number"
                step="0.01"
                placeholder="Ex: 250000"
                required
              />
            </div>
            <div class="space-y-2">
              <Label for="changeDate">Date du changement</Label>
              <Input
                id="changeDate"
                v-model="newPriceForm.changeDate"
                type="datetime-local"
              />
            </div>
            <div class="space-y-2">
              <Label for="changeReason">Raison du changement (optionnel)</Label>
              <Textarea
                id="changeReason"
                v-model="newPriceForm.changeReason"
                placeholder="Ex: Ajustement du marché, Rénovation, Réduction de prix..."
                rows="3"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" @click="addPriceDialogOpen = false">
              Annuler
            </Button>
            <Button @click="addPriceChange">
              Ajouter
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { priceHistoryService } from '@viridial/shared'
import type { PriceHistory, PriceHistoryStats } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { useToast } from '@/components/ui/toast'
import { Line } from 'vue-chartjs'
import {
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Chart as ChartJS
} from 'chart.js'
import { TrendingUp, TrendingDown, Minus, Loader2, AlertCircle, Plus } from 'lucide-vue-next'

// Enregistrer les composants Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
)

interface Props {
  propertyId: number
}

const props = defineProps<Props>()

const priceHistory = ref<PriceHistory[]>([])
const stats = ref<PriceHistoryStats | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const addPriceDialogOpen = ref(false)
const { toast } = useToast()

const newPriceForm = ref({
  price: '',
  changeReason: '',
  changeDate: new Date().toISOString().slice(0, 16) // Format datetime-local
})

// Données du graphique
const chartData = computed(() => {
  if (priceHistory.value.length === 0) {
    return {
      labels: [],
      datasets: []
    }
  }

  // Trier par date croissante pour le graphique
  const sortedHistory = [...priceHistory.value].sort((a, b) => 
    new Date(a.changeDate).getTime() - new Date(b.changeDate).getTime()
  )

  return {
    labels: sortedHistory.map(entry => formatDateShort(entry.changeDate)),
    datasets: [
      {
        label: 'Prix',
        data: sortedHistory.map(entry => entry.price),
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        tension: 0.4,
        fill: true
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        label: (context: any) => {
          return `Prix: ${formatPrice(context.parsed.y)}`
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: false,
      ticks: {
        callback: (value: any) => {
          return formatPrice(value)
        }
      }
    }
  }
}

// Methods
const loadPriceHistory = async () => {
  loading.value = true
  error.value = null
  try {
    const [history, statsData] = await Promise.all([
      priceHistoryService.getByProperty(props.propertyId),
      priceHistoryService.getStatsByProperty(props.propertyId)
    ])
    priceHistory.value = history
    stats.value = statsData
  } catch (err: any) {
    console.error('Error loading price history:', err)
    error.value = err.message || 'Impossible de charger l\'historique des prix'
  } finally {
    loading.value = false
  }
}

const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDateShort = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    month: 'short',
    day: 'numeric'
  })
}

const getPriceChange = (index: number): number => {
  if (index === 0 || index >= priceHistory.value.length) return 0
  if (index + 1 >= priceHistory.value.length) return 0
  const current = priceHistory.value[index]
  const previous = priceHistory.value[index + 1]
  if (!current || !previous) return 0
  return current.price - previous.price
}

const formatPriceChange = (index: number): string => {
  const change = getPriceChange(index)
  return formatPrice(Math.abs(change))
}

const openAddPriceDialog = () => {
  addPriceDialogOpen.value = true
  newPriceForm.value = {
    price: '',
    changeReason: '',
    changeDate: new Date().toISOString().slice(0, 16)
  }
}

const addPriceChange = async () => {
  if (!newPriceForm.value.price) {
    toast({
      title: 'Erreur',
      description: 'Le prix est requis',
      variant: 'destructive'
    })
    return
  }

  try {
    await priceHistoryService.create(props.propertyId, {
      propertyId: props.propertyId,
      price: Number(newPriceForm.value.price),
      changeReason: newPriceForm.value.changeReason || undefined,
      changeDate: newPriceForm.value.changeDate ? new Date(newPriceForm.value.changeDate).toISOString() : undefined
    })
    
    toast({
      title: 'Succès',
      description: 'Changement de prix ajouté avec succès'
    })
    
    addPriceDialogOpen.value = false
    await loadPriceHistory()
  } catch (err: any) {
    console.error('Error adding price change:', err)
    toast({
      title: 'Erreur',
      description: err.message || 'Impossible d\'ajouter le changement de prix',
      variant: 'destructive'
    })
  }
}

onMounted(() => {
  loadPriceHistory()
})
</script>

