<template>
  <Card>
    <CardHeader>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <Bell class="h-5 w-5 text-blue-600" />
          <CardTitle>Alertes de Prix</CardTitle>
        </div>
        <Badge variant="secondary">{{ alerts.length }} active{{ alerts.length > 1 ? 's' : '' }}</Badge>
      </div>
    </CardHeader>
    <CardContent>
      <div v-if="loading" class="flex items-center justify-center py-8">
        <Loader2 class="h-6 w-6 animate-spin text-muted-foreground" />
      </div>
      <div v-else-if="alerts.length === 0" class="text-center py-8 text-muted-foreground">
        <Bell class="h-12 w-12 mx-auto mb-4 opacity-50" />
        <p>Aucune alerte de prix active</p>
      </div>
      <div v-else class="space-y-3">
        <div
          v-for="alert in alerts"
          :key="alert.id"
          class="flex items-center justify-between p-4 border rounded-lg hover:bg-muted/50 transition-colors"
        >
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-1">
              <Badge :variant="alert.notified ? 'default' : 'secondary'">
                {{ getAlertTypeLabel(alert.alertType) }}
              </Badge>
              <Badge v-if="alert.notified" variant="outline" class="text-xs">
                Notifiée
              </Badge>
            </div>
            <div class="text-sm text-muted-foreground">
              <p v-if="alert.isPercentage">
                Seuil: {{ alert.percentageThreshold }}%
              </p>
              <p v-else>
                Prix cible: {{ formatPrice(alert.targetPrice) }}
              </p>
              <p class="mt-1">
                Créée le {{ formatDate(alert.createdAt) }}
              </p>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              @click="deactivateAlert(alert.id)"
              :disabled="deactivating === alert.id"
            >
              <X class="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { priceAlertService } from '@viridial/shared'
import type { PriceAlert } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Bell, Loader2, X } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast/use-toast'

interface Props {
  propertyId: number
}

const props = defineProps<Props>()
const { toast } = useToast()

const alerts = ref<PriceAlert[]>([])
const loading = ref(false)
const deactivating = ref<number | null>(null)

const loadAlerts = async () => {
  loading.value = true
  try {
    alerts.value = await priceAlertService.getByProperty(props.propertyId)
  } catch (err: any) {
    console.error('Error loading price alerts:', err)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les alertes de prix',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const deactivateAlert = async (id: number) => {
  deactivating.value = id
  try {
    await priceAlertService.deactivate(id)
    toast({
      title: 'Succès',
      description: 'Alerte désactivée avec succès'
    })
    await loadAlerts()
  } catch (err: any) {
    console.error('Error deactivating alert:', err)
    toast({
      title: 'Erreur',
      description: 'Impossible de désactiver l\'alerte',
      variant: 'destructive'
    })
  } finally {
    deactivating.value = null
  }
}

const getAlertTypeLabel = (type: string): string => {
  const labels: Record<string, string> = {
    PRICE_DROP: 'Baisse de prix',
    PRICE_INCREASE: 'Hausse de prix',
    PERCENTAGE_DROP: 'Baisse %',
    PERCENTAGE_INCREASE: 'Hausse %'
  }
  return labels[type] || type
}

const formatPrice = (price?: number): string => {
  if (!price) return 'N/A'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

const formatDate = (dateString?: string): string => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

onMounted(() => {
  loadAlerts()
})
</script>

