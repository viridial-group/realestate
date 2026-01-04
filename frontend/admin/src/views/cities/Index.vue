<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">Gestion des Villes</h1>
        <p class="text-muted-foreground mt-1">Configuration SaaS - Gérer les villes et leurs informations</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          Actualiser
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          Ajouter une ville
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #33d484;" @click="filterByActive(true)">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Villes actives</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(51, 212, 132, 0.1);">
            <CheckCircle class="h-5 w-5" style="color: #33d484;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #33d484;">{{ activeCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">villes</p>
        </CardContent>
      </Card>
      
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #fdb022;" @click="filterByActive(false)">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Villes inactives</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(253, 176, 34, 0.1);">
            <XCircle class="h-5 w-5" style="color: #fdb022;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #fdb022;">{{ inactiveCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">villes</p>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4" style="border-left-color: #04c9ff;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Total villes</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(4, 201, 255, 0.1);">
            <MapPin class="h-5 w-5" style="color: #04c9ff;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #04c9ff;">{{ cities.length }}</div>
          <p class="text-xs text-muted-foreground mt-1">villes</p>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Pays représentés</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <Globe class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ uniqueCountriesCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">pays</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Rechercher une ville..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Pays</Label>
            <Select v-model="selectedCountryId" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous les pays" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous les pays</SelectItem>
                <SelectItem
                  v-for="country in countries"
                  :key="country.id"
                  :value="String(country.id)"
                >
                  {{ country.flagEmoji || '🌍' }} {{ country.name }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous</SelectItem>
                <SelectItem value="active">Actives</SelectItem>
                <SelectItem value="inactive">Inactives</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Région</Label>
            <Input v-model="regionFilter" placeholder="Filtrer par région..." @input="handleFilter" />
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des villes -->
    <Card>
      <CardHeader>
        <CardTitle>Liste des villes</CardTitle>
      </CardHeader>
      <CardContent>
        <div v-if="loading" class="flex items-center justify-center py-12">
          <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
        </div>

        <div v-else-if="filteredCities.length === 0" class="text-center py-12">
          <p class="text-muted-foreground">Aucune ville trouvée</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="city in paginatedCities"
            :key="city.id"
            class="p-6 border rounded-lg hover:shadow-md transition-shadow"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
                  <MapPin class="h-5 w-5 text-muted-foreground" />
                  <div>
                    <h3 class="text-xl font-semibold">{{ city.name }}</h3>
                    <p class="text-sm text-muted-foreground">
                      {{ city.countryName || 'Pays inconnu' }}
                      <span v-if="city.postalCode"> - {{ city.postalCode }}</span>
                    </p>
                  </div>
                  <Badge :variant="city.active ? 'default' : 'secondary'">
                    {{ city.active ? 'Active' : 'Inactive' }}
                  </Badge>
                </div>
                
                <p v-if="city.description" class="text-muted-foreground mb-4">
                  {{ city.description }}
                </p>
                
                <div class="grid grid-cols-2 md:grid-cols-5 gap-4 mb-4">
                  <div v-if="city.region">
                    <p class="text-sm text-muted-foreground">Région</p>
                    <p class="font-semibold">{{ city.region }}</p>
                  </div>
                  <div v-if="city.department">
                    <p class="text-sm text-muted-foreground">Département</p>
                    <p class="font-semibold">{{ city.department }}</p>
                  </div>
                  <div v-if="city.timezone">
                    <p class="text-sm text-muted-foreground">Fuseau horaire</p>
                    <p class="font-semibold text-sm">{{ city.timezone }}</p>
                  </div>
                  <div v-if="city.latitude && city.longitude">
                    <p class="text-sm text-muted-foreground">Coordonnées</p>
                    <p class="font-semibold text-sm">{{ city.latitude.toFixed(4) }}, {{ city.longitude.toFixed(4) }}</p>
                  </div>
                  <div>
                    <p class="text-sm text-muted-foreground">Ordre</p>
                    <p class="font-semibold">{{ city.displayOrder }}</p>
                  </div>
                </div>

                <!-- Données importantes -->
                <div v-if="city.importantData" class="mt-4">
                  <p class="text-sm text-muted-foreground mb-2">Données importantes:</p>
                  <div class="bg-muted p-3 rounded-lg">
                    <pre class="text-xs overflow-x-auto">{{ formatImportantData(city.importantData) }}</pre>
                  </div>
                </div>
              </div>

              <div class="flex gap-2 ml-4">
                <Button variant="ghost" size="sm" @click="editCity(city)">
                  <Edit class="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  @click="deleteCity(city.id)"
                  class="text-destructive hover:text-destructive"
                >
                  <Trash2 class="h-4 w-4" />
                </Button>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="flex items-center justify-between mt-6 pt-4 border-t">
          <div class="text-sm text-muted-foreground">
            Page {{ currentPage + 1 }} sur {{ totalPages }} ({{ filteredCities.length }} villes)
          </div>
          <div class="flex gap-2">
            <Button variant="outline" @click="prevPage" :disabled="currentPage === 0">
              Précédent
            </Button>
            <Button variant="outline" @click="nextPage" :disabled="currentPage >= totalPages - 1">
              Suivant
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Dialog pour créer/éditer une ville -->
    <CityDialog
      :open="dialogOpen"
      :city="selectedCity"
      :countries="countries"
      @update:open="dialogOpen = $event"
      @saved="handleCitySaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { cityService, countryService } from '@viridial/shared'
import type { City, Country } from '@viridial/shared'
import { useToast } from '@/components/ui/toast'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { CheckCircle, XCircle, Globe, MapPin, Plus, RefreshCw, Edit, Trash2, Loader2 } from 'lucide-vue-next'
import CityDialog from './CityDialog.vue'

const route = useRoute()
const { toast } = useToast()

const cities = ref<City[]>([])
const countries = ref<Country[]>([])
const loading = ref(false)
const searchQuery = ref('')
const selectedCountryId = ref<string>('all')
const selectedStatus = ref('all')
const regionFilter = ref('')
const currentPage = ref(0)
const pageSize = 10
const dialogOpen = ref(false)
const selectedCity = ref<City | null>(null)

const activeCount = computed(() => cities.value.filter((c: City) => c.active).length)
const inactiveCount = computed(() => cities.value.filter((c: City) => !c.active).length)
const uniqueCountriesCount = computed(() => {
  const countryIds = new Set(cities.value.map((c: City) => c.countryId))
  return countryIds.size
})

const filteredCities = computed(() => {
  let filtered = [...cities.value]

  // Filtre par recherche
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(c => 
      c.name.toLowerCase().includes(query) ||
      (c.postalCode && c.postalCode.toLowerCase().includes(query)) ||
      (c.countryName && c.countryName.toLowerCase().includes(query))
    )
  }

  // Filtre par pays
  if (selectedCountryId.value !== 'all') {
    const countryId = Number(selectedCountryId.value)
    filtered = filtered.filter(c => c.countryId === countryId)
  }

  // Filtre par statut
  if (selectedStatus.value === 'active') {
    filtered = filtered.filter(c => c.active)
  } else if (selectedStatus.value === 'inactive') {
    filtered = filtered.filter(c => !c.active)
  }

  // Filtre par région
  if (regionFilter.value) {
    const region = regionFilter.value.toLowerCase()
    filtered = filtered.filter(c => 
      c.region?.toLowerCase().includes(region) ||
      c.department?.toLowerCase().includes(region)
    )
  }

  // Trier par pays, puis par displayOrder, puis par nom
  return filtered.sort((a, b) => {
    if (a.countryName !== b.countryName) {
      return (a.countryName || '').localeCompare(b.countryName || '')
    }
    if (a.displayOrder !== b.displayOrder) {
      return a.displayOrder - b.displayOrder
    }
    return a.name.localeCompare(b.name)
  })
})

const totalPages = computed(() => Math.ceil(filteredCities.value.length / pageSize))
const paginatedCities = computed(() => {
  const start = currentPage.value * pageSize
  return filteredCities.value.slice(start, start + pageSize)
})

const loadCities = async () => {
  loading.value = true
  try {
    cities.value = await cityService.getAllActive()
  } catch (error: any) {
    console.error('Error loading cities:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les villes',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadCountries = async () => {
  try {
    countries.value = await countryService.getAllActive()
  } catch (error: any) {
    console.error('Error loading countries:', error)
  }
}

const refreshData = async () => {
  await Promise.all([loadCities(), loadCountries()])
}

const handleSearch = () => {
  currentPage.value = 0
}

const handleFilter = () => {
  currentPage.value = 0
}

const filterByActive = (active: boolean) => {
  selectedStatus.value = active ? 'active' : 'inactive'
  currentPage.value = 0
}

const openCreateDialog = () => {
  selectedCity.value = null
  dialogOpen.value = true
}

const editCity = (city: City) => {
  selectedCity.value = city
  dialogOpen.value = true
}

const deleteCity = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette ville ?')) {
    return
  }

  try {
    await cityService.delete(id)
    toast({
      title: 'Succès',
      description: 'Ville supprimée avec succès'
    })
    await loadCities()
  } catch (error: any) {
    console.error('Error deleting city:', error)
    toast({
      title: 'Erreur',
      description: error.response?.data?.message || 'Impossible de supprimer la ville',
      variant: 'destructive'
    })
  }
}

const handleCitySaved = () => {
  dialogOpen.value = false
  loadCities()
}

const formatImportantData = (data: string | undefined): string => {
  if (!data) return ''
  try {
    const parsed = JSON.parse(data)
    return JSON.stringify(parsed, null, 2)
  } catch {
    return data
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
  }
}

onMounted(async () => {
  await loadCountries()
  await loadCities()
  
  // Si un countryId est passé en paramètre, filtrer par ce pays
  const countryId = route.query.countryId
  if (countryId) {
    selectedCountryId.value = String(countryId)
  }
})
</script>

