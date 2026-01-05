<template>
  <Card>
    <CardHeader>
      <div class="flex items-center justify-between">
        <CardTitle class="flex items-center gap-2">
          <TrendingUp class="h-5 w-5 text-blue-600" />
          Données de Marché (DVF)
        </CardTitle>
        <Button variant="ghost" size="icon" @click="loadMarketData" :disabled="loading">
          <RefreshCw :class="['h-4 w-4', { 'animate-spin': loading }]" />
        </Button>
      </div>
    </CardHeader>
    <CardContent>
      <div v-if="loading" class="flex items-center justify-center py-8">
        <Loader2 class="h-6 w-6 animate-spin text-muted-foreground" />
      </div>

      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-8 w-8 mx-auto mb-2 text-destructive" />
        <p class="text-sm text-muted-foreground">{{ error }}</p>
        <Button variant="outline" size="sm" class="mt-4" @click="loadMarketData">
          Réessayer
        </Button>
      </div>

      <div v-else-if="marketData" class="space-y-6">
        <!-- Statistiques principales -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Prix moyen /m²</p>
            <p class="text-lg font-bold">
              {{ formatPrice(marketData.averagePricePerSquareMeter) }}
            </p>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Prix médian /m²</p>
            <p class="text-lg font-bold">
              {{ formatPrice(marketData.medianPricePerSquareMeter) }}
            </p>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Fourchette</p>
            <p class="text-sm font-semibold">
              {{ formatPrice(marketData.minPricePerSquareMeter) }} - 
              {{ formatPrice(marketData.maxPricePerSquareMeter) }}
            </p>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Transactions</p>
            <p class="text-lg font-bold">{{ marketData.transactionCount || 0 }}</p>
          </div>
        </div>

        <!-- Comparaison avec la propriété -->
        <div v-if="marketData.comparison" class="border-t pt-4">
          <div class="flex items-center justify-between mb-4">
            <h4 class="font-semibold">Comparaison avec le marché</h4>
            <Badge 
              :variant="getComparisonVariant(marketData.comparison.priceEvaluation)"
              class="text-sm"
            >
              {{ marketData.comparison.priceEvaluation }}
            </Badge>
          </div>
          
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-sm text-muted-foreground">Prix de la propriété /m²</span>
              <span class="font-semibold">
                {{ formatPrice(marketData.comparison.propertyPricePerSquareMeter) }}
              </span>
            </div>
            
            <div v-if="marketData.comparison.priceDifferencePercent !== undefined" class="flex items-center justify-between">
              <span class="text-sm text-muted-foreground">Écart avec le marché</span>
              <span 
                :class="[
                  'font-semibold',
                  marketData.comparison.priceDifferencePercent > 0 ? 'text-red-600' : 
                  marketData.comparison.priceDifferencePercent < 0 ? 'text-green-600' : 
                  'text-muted-foreground'
                ]"
              >
                {{ formatPercent(marketData.comparison.priceDifferencePercent) }}
              </span>
            </div>

            <div v-if="marketData.comparison.recommendation" class="mt-3 p-3 bg-muted rounded-lg">
              <p class="text-sm text-muted-foreground">{{ marketData.comparison.recommendation }}</p>
            </div>
          </div>
        </div>

        <!-- Évolution des prix -->
        <div v-if="marketData.priceEvolution && marketData.priceEvolution.length > 0" class="border-t pt-4">
          <div class="flex items-center justify-between mb-4">
            <h4 class="font-semibold">Évolution trimestrielle</h4>
            <Button variant="outline" size="sm" @click="exportChart">
              <Download class="h-4 w-4 mr-2" />
              Exporter
            </Button>
          </div>
          <!-- Graphique -->
          <div class="h-64 mb-4">
            <Line
              ref="chartRef"
              :data="chartData"
              :options="chartOptions"
            />
          </div>
          <!-- Liste détaillée -->
          <div class="space-y-2">
            <div
              v-for="(period, index) in marketData.priceEvolution"
              :key="index"
              class="flex items-center justify-between p-2 rounded hover:bg-muted/50"
            >
              <span class="text-sm font-medium">{{ period.period }}</span>
              <div class="flex items-center gap-4">
                <span class="text-sm text-muted-foreground">{{ period.count || 0 }} transactions</span>
                <span class="text-sm font-semibold">
                  {{ formatPrice(period.averagePrice) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Période analysée -->
        <div class="border-t pt-4">
          <p class="text-xs text-muted-foreground">
            Données du {{ formatDate(marketData.periodStart) }} au {{ formatDate(marketData.periodEnd) }}
          </p>
          <p class="text-xs text-muted-foreground mt-1">
            Source: DVF (Demandes de Valeurs Foncières) - data.gouv.fr
          </p>
        </div>
      </div>

      <div v-else class="text-center py-8 text-muted-foreground">
        <BarChart3 class="h-8 w-8 mx-auto mb-2 opacity-50" />
        <p class="text-sm">Aucune donnée de marché disponible</p>
        <p class="text-xs mt-1">
          Les données DVF sont uniquement disponibles pour les propriétés en France avec un code postal valide
        </p>
      </div>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { marketDataService } from '@viridial/shared'
import type { MarketData } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  TrendingUp,
  RefreshCw,
  Loader2,
  AlertCircle,
  BarChart3,
  Download
} from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast/use-toast'
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
  postalCode?: string
  propertyType?: string
}

const props = withDefaults(defineProps<Props>(), {
  propertyType: 'APARTMENT'
})

const { toast } = useToast()

const marketData = ref<MarketData | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const chartRef = ref<any>(null)

const loadMarketData = async () => {
  if (!props.propertyId) return

  loading.value = true
  error.value = null

  try {
    // Calculer les dates (2 dernières années par défaut)
    const endDate = new Date()
    const startDate = new Date()
    startDate.setFullYear(endDate.getFullYear() - 2)

    marketData.value = await marketDataService.getMarketDataForProperty(
      props.propertyId,
      startDate.toISOString().split('T')[0],
      endDate.toISOString().split('T')[0]
    )
  } catch (err: any) {
    console.error('Error loading market data:', err)
    const errorMessage = err.response?.data?.message || err.message || 'Impossible de charger les données de marché'
    error.value = errorMessage
    
    // Si l'erreur indique que la propriété n'est pas en France, afficher un message spécifique
    if (errorMessage.includes('France') || errorMessage.includes('code postal')) {
      error.value = 'Les données DVF ne sont disponibles que pour les propriétés en France avec un code postal français valide (5 chiffres)'
    }
    
    toast({
      title: 'Erreur',
      description: error.value || undefined,
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const formatPrice = (price?: number): string => {
  if (price === undefined || price === null) return 'N/A'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price)
}

const formatPercent = (percent?: number): string => {
  if (percent === undefined || percent === null) return 'N/A'
  const sign = percent > 0 ? '+' : ''
  return `${sign}${percent.toFixed(1)}%`
}

const formatDate = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getComparisonVariant = (evaluation?: string): "default" | "destructive" | "outline" | "secondary" | null | undefined => {
  switch (evaluation) {
    case 'SURESTIMÉ':
      return 'destructive'
    case 'SOUS-ESTIMÉ':
      return 'default'
    case 'CORRECT':
      return 'secondary'
    default:
      return 'secondary'
  }
}

// Données du graphique
const chartData = computed(() => {
  if (!marketData.value?.priceEvolution || marketData.value.priceEvolution.length === 0) {
    return {
      labels: [],
      datasets: []
    }
  }

  const sortedEvolution = [...marketData.value.priceEvolution].sort((a, b) => {
    const periodA = a.period || ''
    const periodB = b.period || ''
    return periodA.localeCompare(periodB)
  })

  return {
    labels: sortedEvolution.map(p => p.period),
    datasets: [
      {
        label: 'Prix moyen au m²',
        data: sortedEvolution.map(p => p.averagePrice || 0),
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        tension: 0.4,
        fill: true
      }
    ]
  }
})

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  interaction: {
    intersect: false,
    mode: 'index' as const
  },
  plugins: {
    legend: {
      display: true,
      position: 'top' as const
    },
    tooltip: {
      callbacks: {
        label: function(context: any) {
          return `Prix: ${formatPrice(context.parsed.y)}`
        }
      }
    }
  },
  scales: {
    x: {
      title: {
        display: true,
        text: 'Période'
      }
    },
    y: {
      beginAtZero: false,
      title: {
        display: true,
        text: 'Prix au m²'
      },
      ticks: {
        callback: function(value: any) {
          return formatPrice(value)
        }
      }
    }
  }
}))

const exportChart = () => {
  if (chartRef.value && chartRef.value.chart) {
    const url = chartRef.value.chart.toBase64Image('image/png', 1)
    const link = document.createElement('a')
    link.download = `evolution-prix-marche-${new Date().toISOString().split('T')[0]}.png`
    link.href = url
    link.click()
  }
}

onMounted(() => {
  loadMarketData()
})
</script>

