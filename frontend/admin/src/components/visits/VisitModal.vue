<template>
  <Dialog :open="open" @update:open="updateOpen">
    <DialogContent class="max-w-2xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle class="flex items-center gap-2">
          <Calendar class="h-5 w-5" />
          {{ isEditing ? 'Modifier le rendez-vous' : 'Détails du rendez-vous' }}
        </DialogTitle>
        <DialogDescription v-if="visit">
          Rendez-vous du {{ formatDateTime(visit.appointmentDate) }}
        </DialogDescription>
      </DialogHeader>

      <div v-if="loading" class="flex items-center justify-center py-12">
        <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
      </div>

      <div v-else-if="visit" class="space-y-6">
        <!-- Informations de base -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label>Propriété</Label>
            <div class="flex items-center gap-2">
              <Home class="h-4 w-4 text-muted-foreground" />
              <span class="font-medium">{{ visit.propertyTitle || `Propriété #${visit.propertyId}` }}</span>
            </div>
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Badge :variant="getStatusVariant(visit.status)">
              {{ getStatusLabel(visit.status) }}
            </Badge>
          </div>
        </div>

        <!-- Formulaire d'édition -->
        <div v-if="isEditing" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label>Date et heure *</Label>
              <Input
                v-model="editForm.appointmentDate"
                type="datetime-local"
                :min="minDateTime"
                required
              />
            </div>
            <div class="space-y-2">
              <Label>Durée (minutes)</Label>
              <Input
                v-model.number="editForm.durationMinutes"
                type="number"
                min="30"
                max="180"
                step="30"
              />
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label>Nom du visiteur *</Label>
              <Input v-model="editForm.visitorName" required />
            </div>
            <div class="space-y-2">
              <Label>Email *</Label>
              <Input v-model="editForm.visitorEmail" type="email" required />
            </div>
          </div>

          <div class="space-y-2">
            <Label>Téléphone</Label>
            <Input v-model="editForm.visitorPhone" type="tel" />
          </div>

          <div class="space-y-2">
            <Label>Notes</Label>
            <Textarea
              v-model="editForm.notes"
              rows="3"
              placeholder="Notes additionnelles..."
            />
          </div>

          <div class="space-y-2">
            <Label>Notes de l'agent</Label>
            <Textarea
              v-model="editForm.agentNotes"
              rows="3"
              placeholder="Notes de l'agent après la visite..."
            />
          </div>

          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="editForm.status">
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="PENDING">En attente</SelectItem>
                <SelectItem value="CONFIRMED">Confirmée</SelectItem>
                <SelectItem value="COMPLETED">Complétée</SelectItem>
                <SelectItem value="CANCELLED">Annulée</SelectItem>
                <SelectItem value="NO_SHOW">Absent</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div v-if="editForm.status === 'CANCELLED'" class="space-y-2">
            <Label>Raison de l'annulation</Label>
            <Textarea
              v-model="editForm.cancellationReason"
              rows="2"
              placeholder="Raison de l'annulation..."
            />
          </div>
        </div>

        <!-- Vue en lecture seule -->
        <div v-else class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label>Visiteur</Label>
              <div class="flex items-center gap-2">
                <User class="h-4 w-4 text-muted-foreground" />
                <span>{{ visit.visitorName || 'N/A' }}</span>
              </div>
            </div>
            <div class="space-y-2">
              <Label>Email</Label>
              <div class="flex items-center gap-2">
                <Mail class="h-4 w-4 text-muted-foreground" />
                <span>{{ visit.visitorEmail || 'N/A' }}</span>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label>Téléphone</Label>
              <div class="flex items-center gap-2">
                <Phone class="h-4 w-4 text-muted-foreground" />
                <span>{{ visit.visitorPhone || 'N/A' }}</span>
              </div>
            </div>
            <div class="space-y-2">
              <Label>Durée</Label>
              <div class="flex items-center gap-2">
                <Clock class="h-4 w-4 text-muted-foreground" />
                <span>{{ visit.durationMinutes }} minutes</span>
              </div>
            </div>
          </div>

          <div v-if="visit.notes" class="space-y-2">
            <Label>Notes</Label>
            <p class="text-sm text-muted-foreground bg-muted p-3 rounded-md">
              {{ visit.notes }}
            </p>
          </div>

          <div v-if="visit.agentNotes" class="space-y-2">
            <Label>Notes de l'agent</Label>
            <p class="text-sm text-muted-foreground bg-muted p-3 rounded-md">
              {{ visit.agentNotes }}
            </p>
          </div>

          <div v-if="visit.cancellationReason" class="space-y-2">
            <Label>Raison de l'annulation</Label>
            <p class="text-sm text-red-600 bg-red-50 p-3 rounded-md">
              {{ visit.cancellationReason }}
            </p>
          </div>
        </div>

        <!-- Section échange de créneau (toujours visible si pas annulé/complété) -->
        <div v-if="visit.status !== 'CANCELLED' && visit.status !== 'COMPLETED'" class="border-t pt-4 space-y-4">
          <div class="flex items-center gap-2">
            <RefreshCw class="h-4 w-4 text-blue-600" />
            <Label class="text-base font-semibold">Proposer un échange de créneau</Label>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label>Nouvelle date et heure proposée</Label>
              <Input
                v-model="exchangeForm.proposedDate"
                type="datetime-local"
                :min="minDateTime"
              />
            </div>
            <div class="space-y-2">
              <Label>Message pour le visiteur (optionnel)</Label>
              <Textarea
                v-model="exchangeForm.message"
                rows="3"
                placeholder="Expliquez la raison de l'échange..."
              />
            </div>
          </div>
          <Button
            variant="outline"
            @click="handleProposeExchange"
            :disabled="!exchangeForm.proposedDate || exchanging"
            class="w-full"
          >
            <Loader2 v-if="exchanging" class="h-4 w-4 mr-2 animate-spin" />
            <RefreshCw v-else class="h-4 w-4 mr-2" />
            Proposer l'échange
          </Button>
        </div>

        <!-- Actions -->
        <DialogFooter class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <Button
              v-if="!isEditing"
              variant="destructive"
              @click="handleDelete"
              :disabled="deleting"
            >
              <Trash2 class="h-4 w-4 mr-2" />
              Supprimer
            </Button>
          </div>
          <div class="flex items-center gap-2">
            <Button variant="outline" @click="toggleEdit">
              {{ isEditing ? 'Annuler' : 'Modifier' }}
            </Button>
            <Button
              v-if="isEditing"
              @click="handleSave"
              :disabled="saving"
            >
              <Loader2 v-if="saving" class="h-4 w-4 mr-2 animate-spin" />
              <Save v-else class="h-4 w-4 mr-2" />
              Enregistrer
            </Button>
            <Button v-else variant="outline" @click="updateOpen(false)">
              Fermer
            </Button>
          </div>
        </DialogFooter>
      </div>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { visitAppointmentService } from '@viridial/shared'
import type { VisitAppointment, VisitAppointmentUpdate, Property } from '@viridial/shared'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import {
  Calendar,
  Home,
  User,
  Mail,
  Phone,
  Clock,
  Loader2,
  Trash2,
  Save,
  RefreshCw
} from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast/use-toast'

interface Props {
  open: boolean
  visit: VisitAppointment | null
  properties?: Property[]
}

const props = withDefaults(defineProps<Props>(), {
  properties: () => []
})

const emit = defineEmits<{
  'update:open': [value: boolean]
  updated: []
  deleted: []
}>()

const { toast } = useToast()

const isEditing = ref(false)
const saving = ref(false)
const deleting = ref(false)
const exchanging = ref(false)
const loading = ref(false)

const exchangeForm = ref({
  proposedDate: '',
  message: ''
})

const editForm = ref<VisitAppointmentUpdate>({
  appointmentDate: '',
  durationMinutes: 60,
  visitorName: '',
  visitorEmail: '',
  visitorPhone: '',
  notes: '',
  agentNotes: '',
  status: 'PENDING' as any,
  cancellationReason: ''
})

const minDateTime = computed(() => {
  const now = new Date()
  now.setHours(now.getHours() + 1)
  return now.toISOString().slice(0, 16)
})

watch(() => props.visit, (newVisit) => {
  if (newVisit) {
    resetForm()
    isEditing.value = false
  }
}, { immediate: true })

watch(() => props.open, (newOpen) => {
  if (!newOpen) {
    isEditing.value = false
  }
})

const resetForm = () => {
  if (props.visit) {
    const date = new Date(props.visit.appointmentDate)
    editForm.value = {
      appointmentDate: date.toISOString().slice(0, 16),
      durationMinutes: props.visit.durationMinutes,
      visitorName: props.visit.visitorName || '',
      visitorEmail: props.visit.visitorEmail || '',
      visitorPhone: props.visit.visitorPhone || '',
      notes: props.visit.notes || '',
      agentNotes: props.visit.agentNotes || '',
      status: props.visit.status as any,
      cancellationReason: props.visit.cancellationReason || ''
    }
    // Réinitialiser le formulaire d'échange
    exchangeForm.value = {
      proposedDate: '',
      message: ''
    }
  }
}

const updateOpen = (value: boolean) => {
  emit('update:open', value)
}

const toggleEdit = () => {
  if (isEditing.value) {
    resetForm()
  }
  isEditing.value = !isEditing.value
}

const handleSave = async () => {
  if (!props.visit) return

  saving.value = true
  try {
    const updateData: VisitAppointmentUpdate = {
      ...editForm.value,
      appointmentDate: new Date(editForm.value.appointmentDate!).toISOString()
    }
    
    await visitAppointmentService.update(props.visit.id, updateData)
    
    toast({
      title: 'Succès',
      description: 'Rendez-vous mis à jour avec succès'
    })
    
    isEditing.value = false
    emit('updated')
    updateOpen(false)
  } catch (err: any) {
    console.error('Error updating visit:', err)
    toast({
      title: 'Erreur',
      description: err.message || 'Impossible de mettre à jour le rendez-vous',
      variant: 'destructive'
    })
  } finally {
    saving.value = false
  }
}

const handleDelete = async () => {
  if (!props.visit) return

  if (!confirm('Êtes-vous sûr de vouloir supprimer ce rendez-vous ?')) {
    return
  }

  deleting.value = true
  try {
    await visitAppointmentService.delete(props.visit.id)
    
    toast({
      title: 'Succès',
      description: 'Rendez-vous supprimé avec succès'
    })
    
    emit('deleted')
    updateOpen(false)
  } catch (err: any) {
    console.error('Error deleting visit:', err)
    toast({
      title: 'Erreur',
      description: err.message || 'Impossible de supprimer le rendez-vous',
      variant: 'destructive'
    })
  } finally {
    deleting.value = false
  }
}

const getStatusVariant = (status: string): "default" | "destructive" | "outline" | "secondary" | null | undefined => {
  const variants: Record<string, "default" | "destructive" | "outline" | "secondary"> = {
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

const handleProposeExchange = async () => {
  if (!props.visit || !exchangeForm.value.proposedDate) return

  exchanging.value = true
  try {
    await visitAppointmentService.proposeExchange(props.visit.id, {
      newAgentId: props.visit.agentId || 0,
      newAppointmentDate: new Date(exchangeForm.value.proposedDate).toISOString(),
      message: exchangeForm.value.message
    })
    
    toast({
      title: 'Succès',
      description: 'Échange de créneau proposé avec succès'
    })
    
    // Réinitialiser le formulaire d'échange
    exchangeForm.value = {
      proposedDate: '',
      message: ''
    }
    
    emit('updated')
  } catch (err: any) {
    console.error('Error proposing exchange:', err)
    toast({
      title: 'Erreur',
      description: err.message || 'Impossible de proposer l\'échange',
      variant: 'destructive'
    })
  } finally {
    exchanging.value = false
  }
}
</script>

