<template>
  <Card>
    <CardHeader>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <Calendar class="h-5 w-5 text-blue-600" />
          <CardTitle>Calendrier des Visites</CardTitle>
        </div>
        <div class="flex items-center gap-2">
          <Button variant="outline" size="sm" @click="previousMonth">
            <ChevronLeft class="h-4 w-4" />
          </Button>
          <span class="text-sm font-medium min-w-[200px] text-center">
            {{ currentMonthYear }}
          </span>
          <Button variant="outline" size="sm" @click="nextMonth">
            <ChevronRight class="h-4 w-4" />
          </Button>
          <Button variant="outline" size="sm" @click="goToToday">
            Aujourd'hui
          </Button>
        </div>
      </div>
    </CardHeader>
    <CardContent>
      <div v-if="loading" class="flex items-center justify-center py-12">
        <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
      <div v-else class="space-y-4">
        <!-- Légende -->
        <div class="flex items-center gap-4 text-sm">
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 rounded-full bg-yellow-500"></div>
            <span>En attente</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 rounded-full bg-blue-500"></div>
            <span>Confirmée</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 rounded-full bg-green-500"></div>
            <span>Complétée</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 rounded-full bg-red-500"></div>
            <span>Annulée</span>
          </div>
        </div>

        <!-- Calendrier -->
        <div class="border rounded-lg overflow-hidden">
          <!-- En-têtes des jours -->
          <div class="grid grid-cols-7 border-b bg-muted/50">
            <div
              v-for="day in daysOfWeek"
              :key="day"
              class="p-2 text-center text-sm font-medium"
            >
              {{ day }}
            </div>
          </div>

          <!-- Grille du calendrier -->
          <div class="grid grid-cols-7">
            <div
              v-for="(day, index) in calendarDays"
              :key="index"
              :class="[
                'min-h-[100px] border-r border-b p-2',
                day.isCurrentMonth ? 'bg-background' : 'bg-muted/30',
                day.isToday ? 'bg-blue-50 border-blue-200' : ''
              ]"
            >
              <div class="flex items-center justify-between mb-1">
                <span
                  :class="[
                    'text-sm font-medium',
                    day.isToday ? 'text-blue-600' : day.isCurrentMonth ? 'text-foreground' : 'text-muted-foreground'
                  ]"
                >
                  {{ day.day }}
                </span>
              </div>
              <div class="space-y-1">
                <div
                  v-for="visit in day.visits"
                  :key="visit.id"
                  :class="[
                    'text-xs p-1 rounded cursor-pointer hover:opacity-80 transition-opacity',
                    getStatusColorClass(visit.status)
                  ]"
                  @click="openVisitModal(visit)"
                >
                  <div class="font-medium truncate">{{ visit.visitorName || 'Visiteur' }}</div>
                  <div class="text-xs opacity-90">
                    {{ formatTime(visit.appointmentDate) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </CardContent>
  </Card>

  <!-- Modale de visualisation/édition -->
  <VisitModal
    v-model:open="modalOpen"
    :visit="selectedVisit"
    :properties="props.properties"
    @updated="handleVisitUpdated"
    @deleted="handleVisitDeleted"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { visitAppointmentService, propertyService } from '@viridial/shared'
import type { VisitAppointment, Property } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Calendar, ChevronLeft, ChevronRight, Loader2 } from 'lucide-vue-next'
import VisitModal from './VisitModal.vue'

interface Props {
  properties?: Property[]
}

const props = withDefaults(defineProps<Props>(), {
  properties: () => []
})

const emit = defineEmits<{
  visitSelected: [visit: VisitAppointment]
}>()

interface CalendarDay {
  day: number
  date: Date
  isCurrentMonth: boolean
  isToday: boolean
  visits: VisitAppointment[]
}

const loading = ref(false)
const visits = ref<VisitAppointment[]>([])
const currentDate = ref(new Date())
const modalOpen = ref(false)
const selectedVisit = ref<VisitAppointment | null>(null)

const daysOfWeek = ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam']

const currentMonthYear = computed(() => {
  return currentDate.value.toLocaleDateString('fr-FR', {
    month: 'long',
    year: 'numeric'
  })
})

const calendarDays = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  
  // Premier jour du mois
  const firstDay = new Date(year, month, 1)
  const firstDayOfWeek = firstDay.getDay()
  
  // Dernier jour du mois
  const lastDay = new Date(year, month + 1, 0)
  const daysInMonth = lastDay.getDate()
  
  // Aujourd'hui
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  const days: CalendarDay[] = []
  
  // Jours du mois précédent
  const prevMonthLastDay = new Date(year, month, 0).getDate()
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    const date = new Date(year, month - 1, prevMonthLastDay - i)
    days.push({
      day: prevMonthLastDay - i,
      date,
      isCurrentMonth: false,
      isToday: false,
      visits: getVisitsForDate(date)
    })
  }
  
  // Jours du mois actuel
  for (let day = 1; day <= daysInMonth; day++) {
    const date = new Date(year, month, day)
    date.setHours(0, 0, 0, 0)
    days.push({
      day,
      date,
      isCurrentMonth: true,
      isToday: date.getTime() === today.getTime(),
      visits: getVisitsForDate(date)
    })
  }
  
  // Jours du mois suivant pour compléter la grille
  const remainingDays = 42 - days.length // 6 semaines * 7 jours
  for (let day = 1; day <= remainingDays; day++) {
    const date = new Date(year, month + 1, day)
    days.push({
      day,
      date,
      isCurrentMonth: false,
      isToday: false,
      visits: getVisitsForDate(date)
    })
  }
  
  return days
})

const getVisitsForDate = (date: Date): VisitAppointment[] => {
  return visits.value.filter(visit => {
    const visitDate = new Date(visit.appointmentDate)
    visitDate.setHours(0, 0, 0, 0)
    return visitDate.getTime() === date.getTime()
  })
}

const getStatusColorClass = (status: string): string => {
  const classes: Record<string, string> = {
    PENDING: 'bg-yellow-100 text-yellow-800 border border-yellow-300',
    CONFIRMED: 'bg-blue-100 text-blue-800 border border-blue-300',
    COMPLETED: 'bg-green-100 text-green-800 border border-green-300',
    CANCELLED: 'bg-red-100 text-red-800 border border-red-300',
    NO_SHOW: 'bg-gray-100 text-gray-800 border border-gray-300'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const formatTime = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleTimeString('fr-FR', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const previousMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
}

const nextMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
}

const goToToday = () => {
  currentDate.value = new Date()
}

const openVisitModal = (visit: VisitAppointment) => {
  selectedVisit.value = visit
  modalOpen.value = true
  emit('visitSelected', visit)
}

const loadVisits = async () => {
  loading.value = true
  try {
    // Charger toutes les visites pour le mois en cours
    const allVisits: VisitAppointment[] = []
    let propsList = props.properties || []
    
    if (propsList.length === 0) {
      propsList = await propertyService.getAll({})
    }
    
    for (const prop of propsList.slice(0, 100)) {
      try {
        const propVisits = await visitAppointmentService.getByProperty(prop.id)
        allVisits.push(...propVisits)
      } catch (err) {
        console.warn(`Error loading visits for property ${prop.id}:`, err)
      }
    }
    
    visits.value = allVisits
  } catch (err: any) {
    console.error('Error loading visits:', err)
  } finally {
    loading.value = false
  }
}

const handleVisitUpdated = () => {
  loadVisits()
  modalOpen.value = false
}

const handleVisitDeleted = () => {
  loadVisits()
  modalOpen.value = false
}

onMounted(() => {
  loadVisits()
})
</script>

