<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">Gestion des Visites</h1>
        <p class="text-muted-foreground mt-1">Gérez les rendez-vous de visite des propriétés</p>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card>
        <CardContent class="pt-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-muted-foreground">En attente</p>
              <p class="text-2xl font-bold">{{ stats.pending }}</p>
            </div>
            <Clock class="h-8 w-8 text-yellow-500" />
          </div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="pt-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-muted-foreground">Confirmées</p>
              <p class="text-2xl font-bold">{{ stats.confirmed }}</p>
            </div>
            <CheckCircle class="h-8 w-8 text-green-500" />
          </div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="pt-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-muted-foreground">Complétées</p>
              <p class="text-2xl font-bold">{{ stats.completed }}</p>
            </div>
            <CheckCircle2 class="h-8 w-8 text-blue-500" />
          </div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="pt-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-muted-foreground">Total</p>
              <p class="text-2xl font-bold">{{ stats.total }}</p>
            </div>
            <Calendar class="h-8 w-8 text-gray-500" />
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Filters -->
    <Card>
      <CardHeader>
        <CardTitle>Filtres</CardTitle>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-4">
          <div class="flex-1 min-w-[200px]">
            <Label>Recherche</Label>
            <div class="relative mt-1">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                v-model="searchQuery"
                placeholder="Rechercher par visiteur, propriété..."
                class="pl-9"
                @input="handleSearch"
              />
            </div>
          </div>
          <div class="w-[200px]">
            <Label>Statut</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger class="mt-1">
                <SelectValue placeholder="Tous les statuts" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous les statuts</SelectItem>
                <SelectItem value="PENDING">En attente</SelectItem>
                <SelectItem value="CONFIRMED">Confirmées</SelectItem>
                <SelectItem value="COMPLETED">Complétées</SelectItem>
                <SelectItem value="CANCELLED">Annulées</SelectItem>
                <SelectItem value="NO_SHOW">Absents</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="w-[200px]">
            <Label>Propriété</Label>
            <Select v-model="selectedPropertyId" @update:model-value="handleFilter">
              <SelectTrigger class="mt-1">
                <SelectValue placeholder="Toutes les propriétés" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Toutes les propriétés</SelectItem>
                <SelectItem
                  v-for="property in properties"
                  :key="property.id"
                  :value="property.id.toString()"
                >
                  {{ property.title }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex items-end gap-2">
            <Button variant="outline" @click="resetFilters">
              <X class="h-4 w-4 mr-2" />
              Réinitialiser
            </Button>
            <Button variant="outline" @click="loadVisits">
              <RefreshCw class="h-4 w-4 mr-2" />
              Actualiser
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Onglets Calendrier / Liste -->
    <Tabs v-model="activeTab" class="w-full">
      <TabsList class="grid w-full grid-cols-2">
        <TabsTrigger value="calendar" class="flex items-center gap-2">
          <Calendar class="h-4 w-4" />
          Calendrier
        </TabsTrigger>
        <TabsTrigger value="list" class="flex items-center gap-2">
          <List class="h-4 w-4" />
          Liste
        </TabsTrigger>
      </TabsList>

      <TabsContent value="calendar" class="mt-6">
        <VisitCalendar
          :properties="properties"
          @visitSelected="handleVisitSelected"
        />
      </TabsContent>

      <TabsContent value="list" class="mt-6">
        <!-- Visits Table -->
        <Card>
      <CardHeader>
        <CardTitle>Liste des Visites</CardTitle>
      </CardHeader>
      <CardContent>
        <div v-if="loading" class="flex items-center justify-center py-12">
          <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
        <div v-else-if="filteredVisits.length === 0" class="text-center py-12 text-muted-foreground">
          <Calendar class="h-12 w-12 mx-auto mb-4 opacity-50" />
          <p>Aucune visite trouvée</p>
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="border-b">
                <th class="px-4 py-3 text-left text-sm font-medium">Date</th>
                <th class="px-4 py-3 text-left text-sm font-medium">Visiteur</th>
                <th class="px-4 py-3 text-left text-sm font-medium">Propriété</th>
                <th class="px-4 py-3 text-left text-sm font-medium">Agent</th>
                <th class="px-4 py-3 text-left text-sm font-medium">Statut</th>
                <th class="px-4 py-3 text-left text-sm font-medium">Durée</th>
                <th class="px-4 py-3 text-right text-sm font-medium">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              <tr
                v-for="visit in filteredVisits"
                :key="visit.id"
                class="hover:bg-muted/50 transition-colors"
              >
                <td class="px-4 py-3 text-sm">
                  {{ formatDateTime(visit.appointmentDate) }}
                </td>
                <td class="px-4 py-3 text-sm">
                  <div>
                    <p class="font-medium">{{ visit.visitorName || 'N/A' }}</p>
                    <p class="text-muted-foreground text-xs">{{ visit.visitorEmail }}</p>
                  </div>
                </td>
                <td class="px-4 py-3 text-sm">
                  <router-link
                    :to="`/properties/${visit.propertyId}`"
                    class="text-blue-600 hover:underline"
                  >
                    {{ visit.propertyTitle || `Propriété #${visit.propertyId}` }}
                  </router-link>
                </td>
                <td class="px-4 py-3 text-sm">
                  {{ visit.agentName || `Agent #${visit.agentId}` }}
                </td>
                <td class="px-4 py-3">
                  <Badge :variant="getStatusVariant(visit.status)">
                    {{ getStatusLabel(visit.status) }}
                  </Badge>
                </td>
                <td class="px-4 py-3 text-sm">{{ visit.durationMinutes }} min</td>
                <td class="px-4 py-3 text-right">
                  <DropdownMenu>
                    <DropdownMenuTrigger as-child>
                      <Button variant="ghost" size="sm">
                        <MoreHorizontal class="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem @click="viewVisit(visit.id)">
                        <Eye class="mr-2 h-4 w-4" />
                        Voir détails
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        v-if="visit.status === 'PENDING'"
                        @click="confirmVisit(visit.id)"
                      >
                        <CheckCircle class="mr-2 h-4 w-4" />
                        Confirmer
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        v-if="visit.status === 'CONFIRMED'"
                        @click="completeVisit(visit.id)"
                      >
                        <CheckCircle2 class="mr-2 h-4 w-4" />
                        Compléter
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        v-if="visit.status !== 'CANCELLED' && visit.status !== 'COMPLETED'"
                        @click="cancelVisit(visit.id)"
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
      </TabsContent>
    </Tabs>

    <!-- Modale de visite -->
    <VisitModal
      v-model:open="visitModalOpen"
      :visit="selectedVisit"
      :properties="properties"
      @updated="handleVisitUpdated"
      @deleted="handleVisitDeleted"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
// import { useRouter } from 'vue-router' // Non utilisé
import { visitAppointmentService, propertyService } from '@viridial/shared'
import type { VisitAppointment, Property } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import {
  Calendar,
  Clock,
  CheckCircle,
  CheckCircle2,
  Search,
  RefreshCw,
  X,
  Loader2,
  MoreHorizontal,
  Eye,
  XCircle,
  List
} from 'lucide-vue-next'
import VisitCalendar from '@/components/visits/VisitCalendar.vue'
import VisitModal from '@/components/visits/VisitModal.vue'
import { useToast } from '@/components/ui/toast/use-toast'

// const router = useRouter() // Non utilisé
const { toast } = useToast()

const visits = ref<VisitAppointment[]>([])
const properties = ref<Property[]>([])
const loading = ref(false)
const activeTab = ref('calendar')
const searchQuery = ref('')
const selectedStatus = ref('all')
const selectedPropertyId = ref('all')
const selectedVisit = ref<VisitAppointment | null>(null)
const visitModalOpen = ref(false)

const stats = computed(() => {
  return {
    pending: visits.value.filter(v => v.status === 'PENDING').length,
    confirmed: visits.value.filter(v => v.status === 'CONFIRMED').length,
    completed: visits.value.filter(v => v.status === 'COMPLETED').length,
    total: visits.value.length
  }
})

const filteredVisits = computed(() => {
  let filtered = [...visits.value]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      v =>
        v.visitorName?.toLowerCase().includes(query) ||
        v.visitorEmail?.toLowerCase().includes(query) ||
        v.propertyTitle?.toLowerCase().includes(query)
    )
  }

  if (selectedStatus.value !== 'all') {
    filtered = filtered.filter(v => v.status === selectedStatus.value)
  }

  if (selectedPropertyId.value !== 'all') {
    filtered = filtered.filter(v => v.propertyId === Number(selectedPropertyId.value))
  }

  return filtered.sort((a, b) => 
    new Date(b.appointmentDate).getTime() - new Date(a.appointmentDate).getTime()
  )
})

const loadProperties = async () => {
  try {
    properties.value = await propertyService.getAll({})
  } catch (err: any) {
    console.error('Error loading properties:', err)
  }
}

const loadVisits = async () => {
  loading.value = true
  try {
    // Charger toutes les visites (pour l'admin, on pourrait charger toutes les visites)
    // Pour l'instant, on charge les visites de toutes les propriétés
    const allVisits: VisitAppointment[] = []
    
    // Charger les propriétés si pas déjà chargées
    if (properties.value.length === 0) {
      await loadProperties()
    }
    
    // Charger les visites pour chaque propriété
    for (const prop of properties.value.slice(0, 50)) { // Limiter à 50 propriétés pour éviter trop de requêtes
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
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les visites',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const confirmVisit = async (id: number) => {
  try {
    await visitAppointmentService.confirm(id)
    toast({
      title: 'Succès',
      description: 'Visite confirmée avec succès'
    })
    await loadVisits()
  } catch (err: any) {
    toast({
      title: 'Erreur',
      description: err.message || 'Impossible de confirmer la visite',
      variant: 'destructive'
    })
  }
}

const cancelVisit = async (id: number) => {
  try {
    await visitAppointmentService.cancel(id, 'Annulée par l\'administrateur')
    toast({
      title: 'Succès',
      description: 'Visite annulée avec succès'
    })
    await loadVisits()
  } catch (err: any) {
    toast({
      title: 'Erreur',
      description: err.message || 'Impossible d\'annuler la visite',
      variant: 'destructive'
    })
  }
}

const completeVisit = async (id: number) => {
  try {
    await visitAppointmentService.complete(id)
    toast({
      title: 'Succès',
      description: 'Visite marquée comme complétée'
    })
    await loadVisits()
  } catch (err: any) {
    toast({
      title: 'Erreur',
      description: err.message || 'Impossible de compléter la visite',
      variant: 'destructive'
    })
  }
}

const viewVisit = (id: number) => {
  const visit = visits.value.find(v => v.id === id)
  if (visit) {
    selectedVisit.value = visit
    visitModalOpen.value = true
  }
}

const handleVisitSelected = (visit: VisitAppointment) => {
  selectedVisit.value = visit
  visitModalOpen.value = true
}

const handleVisitUpdated = () => {
  loadVisits()
  visitModalOpen.value = false
}

const handleVisitDeleted = () => {
  loadVisits()
  visitModalOpen.value = false
}

const handleSearch = () => {
  // Le computed filteredVisits se mettra à jour automatiquement
}

const handleFilter = () => {
  // Le computed filteredVisits se mettra à jour automatiquement
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedStatus.value = 'all'
  selectedPropertyId.value = 'all'
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
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadProperties()
  loadVisits()
})
</script>

