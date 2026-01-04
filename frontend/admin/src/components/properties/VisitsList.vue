<template>
  <Card>
    <CardHeader>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <Calendar class="h-5 w-5 text-blue-600" />
          <CardTitle>Rendez-vous de Visite</CardTitle>
        </div>
        <div class="flex items-center gap-2">
          <Badge variant="secondary">{{ visits.length }} visite{{ visits.length > 1 ? 's' : '' }}</Badge>
          <Button variant="outline" size="sm" @click="loadVisits">
            <RefreshCw class="h-4 w-4" />
          </Button>
        </div>
      </div>
    </CardHeader>
    <CardContent>
      <div v-if="loading" class="flex items-center justify-center py-8">
        <Loader2 class="h-6 w-6 animate-spin text-muted-foreground" />
      </div>
      <div v-else-if="visits.length === 0" class="text-center py-8 text-muted-foreground">
        <Calendar class="h-12 w-12 mx-auto mb-4 opacity-50" />
        <p>Aucun rendez-vous de visite</p>
      </div>
      <div v-else class="space-y-3">
        <div
          v-for="visit in visits"
          :key="visit.id"
          class="p-4 border rounded-lg hover:bg-muted/50 transition-colors"
        >
          <div class="flex items-start justify-between mb-2">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <Badge :variant="getStatusVariant(visit.status)">
                  {{ getStatusLabel(visit.status) }}
                </Badge>
                <span class="text-sm font-medium">
                  {{ formatDateTime(visit.appointmentDate) }}
                </span>
              </div>
              <div class="text-sm text-muted-foreground space-y-1">
                <p><strong>Visiteur:</strong> {{ visit.visitorName || 'N/A' }}</p>
                <p v-if="visit.visitorEmail"><strong>Email:</strong> {{ visit.visitorEmail }}</p>
                <p v-if="visit.visitorPhone"><strong>Téléphone:</strong> {{ visit.visitorPhone }}</p>
                <p><strong>Durée:</strong> {{ visit.durationMinutes }} minutes</p>
                <p v-if="visit.notes" class="mt-2 italic">{{ visit.notes }}</p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                @click="openVisitModal(visit)"
              >
                <Eye class="h-4 w-4 mr-1" />
                Voir
              </Button>
            </div>
          </div>
        </div>
      </div>
    </CardContent>
  </Card>

  <!-- Modale de visite -->
  <VisitModal
    v-model:open="modalOpen"
    :visit="selectedVisit"
    @updated="handleVisitUpdated"
    @deleted="handleVisitDeleted"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { visitAppointmentService } from '@viridial/shared'
import type { VisitAppointment } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Calendar, Loader2, RefreshCw, Eye } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast/use-toast'
import VisitModal from '@/components/visits/VisitModal.vue'

interface Props {
  propertyId: number
}

const props = defineProps<Props>()
const { toast } = useToast()

const visits = ref<VisitAppointment[]>([])
const loading = ref(false)
const selectedVisit = ref<VisitAppointment | null>(null)
const modalOpen = ref(false)

const loadVisits = async () => {
  loading.value = true
  try {
    visits.value = await visitAppointmentService.getByProperty(props.propertyId)
  } catch (err: any) {
    console.error('Error loading visits:', err)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les visites',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const openVisitModal = (visit: VisitAppointment) => {
  selectedVisit.value = visit
  modalOpen.value = true
}

const handleVisitUpdated = () => {
  loadVisits()
  modalOpen.value = false
}

const handleVisitDeleted = () => {
  loadVisits()
  modalOpen.value = false
}

const getStatusVariant = (status: string): string => {
  const variants: Record<string, string> = {
    PENDING: 'secondary',
    CONFIRMED: 'default',
    CANCELLED: 'destructive',
    COMPLETED: 'outline',
    NO_SHOW: 'destructive'
  }
  return variants[status] || 'secondary'
}

const getStatusLabel = (status: string): string => {
  const labels: Record<string, string> = {
    PENDING: 'En attente',
    CONFIRMED: 'Confirmée',
    CANCELLED: 'Annulée',
    COMPLETED: 'Complétée',
    NO_SHOW: 'Absent'
  }
  return labels[status] || status
}

const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadVisits()
})
</script>

