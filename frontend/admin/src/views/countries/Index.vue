<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">Gestion des Pays</h1>
        <p class="text-muted-foreground mt-1">Configuration SaaS - Gérer les pays et leurs informations</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          Actualiser
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          Ajouter un pays
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #33d484;" @click="filterByActive(true)">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Pays actifs</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(51, 212, 132, 0.1);">
            <CheckCircle class="h-5 w-5" style="color: #33d484;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #33d484;">{{ activeCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">pays</p>
        </CardContent>
      </Card>
      
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #fdb022;" @click="filterByActive(false)">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Pays inactifs</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(253, 176, 34, 0.1);">
            <XCircle class="h-5 w-5" style="color: #fdb022;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #fdb022;">{{ inactiveCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">pays</p>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4" style="border-left-color: #04c9ff;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Total pays</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(4, 201, 255, 0.1);">
            <Globe class="h-5 w-5" style="color: #04c9ff;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #04c9ff;">{{ countries.length }}</div>
          <p class="text-xs text-muted-foreground mt-1">pays</p>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Total villes</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <MapPin class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ totalCities }}</div>
          <p class="text-xs text-muted-foreground mt-1">villes</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Rechercher un pays..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous</SelectItem>
                <SelectItem value="active">Actifs</SelectItem>
                <SelectItem value="inactive">Inactifs</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Devise</Label>
            <Input v-model="currencyFilter" placeholder="Filtrer par devise..." @input="handleFilter" />
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des pays -->
    <Card>
      <CardHeader>
        <CardTitle>Liste des pays</CardTitle>
      </CardHeader>
      <CardContent>
        <div v-if="loading" class="flex items-center justify-center py-12">
          <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
        </div>

        <div v-else-if="filteredCountries.length === 0" class="text-center py-12">
          <p class="text-muted-foreground">Aucun pays trouvé</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="country in paginatedCountries"
            :key="country.id"
            class="p-6 border rounded-lg hover:shadow-md transition-shadow"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
                  <span class="text-2xl">{{ country.flagEmoji || '🌍' }}</span>
                  <div>
                    <h3 class="text-xl font-semibold">{{ country.name }}</h3>
                    <p class="text-sm text-muted-foreground">Code: {{ country.code }} {{ country.code3 ? `(${country.code3})` : '' }}</p>
                  </div>
                  <Badge :variant="country.active ? 'default' : 'secondary'">
                    {{ country.active ? 'Actif' : 'Inactif' }}
                  </Badge>
                </div>
                
                <p v-if="country.description" class="text-muted-foreground mb-4">
                  {{ country.description }}
                </p>
                
                <div class="grid grid-cols-2 md:grid-cols-5 gap-4 mb-4">
                  <div v-if="country.phoneCode">
                    <p class="text-sm text-muted-foreground">Code téléphonique</p>
                    <p class="font-semibold">{{ country.phoneCode }}</p>
                  </div>
                  <div v-if="country.currency">
                    <p class="text-sm text-muted-foreground">Devise</p>
                    <p class="font-semibold">{{ country.currencySymbol || country.currency }} ({{ country.currency }})</p>
                  </div>
                  <div v-if="country.timezone">
                    <p class="text-sm text-muted-foreground">Fuseau horaire</p>
                    <p class="font-semibold text-sm">{{ country.timezone }}</p>
                  </div>
                  <div v-if="country.latitude && country.longitude">
                    <p class="text-sm text-muted-foreground">Coordonnées</p>
                    <p class="font-semibold text-sm">{{ country.latitude.toFixed(4) }}, {{ country.longitude.toFixed(4) }}</p>
                  </div>
                  <div>
                    <p class="text-sm text-muted-foreground">Ordre</p>
                    <p class="font-semibold">{{ country.displayOrder }}</p>
                  </div>
                </div>

                <!-- Données importantes -->
                <div v-if="country.importantData" class="mt-4">
                  <p class="text-sm text-muted-foreground mb-2">Données importantes:</p>
                  <div class="bg-muted p-3 rounded-lg">
                    <pre class="text-xs overflow-x-auto">{{ formatImportantData(country.importantData) }}</pre>
                  </div>
                </div>
              </div>

              <div class="flex gap-2 ml-4">
                <Button variant="ghost" size="sm" @click="viewCities(country.id)">
                  <MapPin class="h-4 w-4 mr-1" />
                  Villes
                </Button>
                <Button variant="ghost" size="sm" @click="editCountry(country)">
                  <Edit class="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  @click="deleteCountry(country.id)"
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
            Page {{ currentPage + 1 }} sur {{ totalPages }} ({{ filteredCountries.length }} pays)
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

    <!-- Dialog pour créer/éditer un pays -->
    <CountryDialog
      :open="dialogOpen"
      :country="selectedCountry"
      @update:open="dialogOpen = $event"
      @saved="handleCountrySaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { countryService } from '@viridial/shared'
import type { Country } from '@viridial/shared'
import { useToast } from '@/components/ui/toast'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { CheckCircle, XCircle, Globe, MapPin, Plus, RefreshCw, Edit, Trash2, Loader2 } from 'lucide-vue-next'
import CountryDialog from './CountryDialog.vue'

const router = useRouter()
const { toast } = useToast()

const countries = ref<Country[]>([])
const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref('all')
const currencyFilter = ref('')
const currentPage = ref(0)
const pageSize = 10
const dialogOpen = ref(false)
const selectedCountry = ref<Country | null>(null)

const activeCount = computed(() => countries.value.filter((c: Country) => c.active).length)
const inactiveCount = computed(() => countries.value.filter((c: Country) => !c.active).length)
const totalCities = computed(() => {
  // Compter les villes depuis tous les pays (approximation)
  return countries.value.reduce((sum: number, country: Country) => sum + (country.cities?.length || 0), 0)
})

const filteredCountries = computed(() => {
  let filtered = [...countries.value]

  // Filtre par recherche
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(c => 
      c.name.toLowerCase().includes(query) ||
      c.code.toLowerCase().includes(query) ||
      (c.code3 && c.code3.toLowerCase().includes(query))
    )
  }

  // Filtre par statut
  if (selectedStatus.value === 'active') {
    filtered = filtered.filter(c => c.active)
  } else if (selectedStatus.value === 'inactive') {
    filtered = filtered.filter(c => !c.active)
  }

  // Filtre par devise
  if (currencyFilter.value) {
    const currency = currencyFilter.value.toLowerCase()
    filtered = filtered.filter(c => 
      c.currency?.toLowerCase().includes(currency) ||
      c.currencySymbol?.toLowerCase().includes(currency)
    )
  }

  // Trier par displayOrder puis par nom
  return filtered.sort((a, b) => {
    if (a.displayOrder !== b.displayOrder) {
      return a.displayOrder - b.displayOrder
    }
    return a.name.localeCompare(b.name)
  })
})

const totalPages = computed(() => Math.ceil(filteredCountries.value.length / pageSize))
const paginatedCountries = computed(() => {
  const start = currentPage.value * pageSize
  return filteredCountries.value.slice(start, start + pageSize)
})

const loadCountries = async () => {
  loading.value = true
  try {
    countries.value = await countryService.getAllActive()
  } catch (error: any) {
    console.error('Error loading countries:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les pays',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const refreshData = async () => {
  await loadCountries()
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
  selectedCountry.value = null
  dialogOpen.value = true
}

const editCountry = (country: Country) => {
  selectedCountry.value = country
  dialogOpen.value = true
}

const deleteCountry = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer ce pays ? Cette action supprimera également toutes les villes associées.')) {
    return
  }

  try {
    await countryService.delete(id)
    toast({
      title: 'Succès',
      description: 'Pays supprimé avec succès'
    })
    await loadCountries()
  } catch (error: any) {
    console.error('Error deleting country:', error)
    toast({
      title: 'Erreur',
      description: error.response?.data?.message || 'Impossible de supprimer le pays',
      variant: 'destructive'
    })
  }
}

const viewCities = (countryId: number) => {
  router.push(`/cities?countryId=${countryId}`)
}

const handleCountrySaved = () => {
  dialogOpen.value = false
  loadCountries()
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

onMounted(() => {
  loadCountries()
})
</script>

