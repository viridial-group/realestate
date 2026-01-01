<template>
  <div class="space-y-6">
    <!-- Header avec Actions Rapides -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">Gestion des Propriétés</h1>
        <p class="text-muted-foreground mt-1">Gérez toutes les propriétés immobilières</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="exportData" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Exporter
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          Nouvelle Propriété
        </Button>
      </div>
    </div>

    <!-- Stats Cards avec Actions Rapides -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByStatus('AVAILABLE')">
        <CardHeader class="pb-2">
          <CardDescription>Disponibles</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-green-600">{{ stats.available || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">En vente/location</p>
        </CardContent>
      </Card>
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByStatus('SOLD')">
        <CardHeader class="pb-2">
          <CardDescription>Vendues</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-blue-600">{{ stats.sold || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Vendues</p>
        </CardContent>
      </Card>
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByStatus('RENTED')">
        <CardHeader class="pb-2">
          <CardDescription>Louées</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-purple-600">{{ stats.rented || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">En location</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Total</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.total || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Toutes propriétés</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres Avancés -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-6 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Titre, adresse..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Type</Label>
            <Select v-model="selectedType" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="">Tous</SelectItem>
                <SelectItem value="HOUSE">Maison</SelectItem>
                <SelectItem value="APARTMENT">Appartement</SelectItem>
                <SelectItem value="LAND">Terrain</SelectItem>
                <SelectItem value="COMMERCIAL">Commercial</SelectItem>
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
                <SelectItem value="">Tous</SelectItem>
                <SelectItem value="AVAILABLE">Disponible</SelectItem>
                <SelectItem value="SOLD">Vendu</SelectItem>
                <SelectItem value="RENTED">Loué</SelectItem>
                <SelectItem value="PENDING">En attente</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Prix min</Label>
            <Input v-model="priceMin" type="number" placeholder="0" @input="handleFilter" />
          </div>
          <div class="space-y-2">
            <Label>Prix max</Label>
            <Input v-model="priceMax" type="number" placeholder="∞" @input="handleFilter" />
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

    <!-- Vue Grille/Liste -->
    <div class="flex items-center justify-between">
      <div class="flex gap-2">
        <Button
          variant="outline"
          size="sm"
          :class="{ 'bg-primary text-primary-foreground': viewMode === 'grid' }"
          @click="viewMode = 'grid'"
        >
          <Grid3x3 class="h-4 w-4" />
        </Button>
        <Button
          variant="outline"
          size="sm"
          :class="{ 'bg-primary text-primary-foreground': viewMode === 'list' }"
          @click="viewMode = 'list'"
        >
          <List class="h-4 w-4" />
        </Button>
      </div>
      <div class="flex items-center gap-2">
        <Label>Trier par:</Label>
        <Select v-model="sortBy" @update:model-value="handleSort">
          <SelectTrigger class="w-40">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="createdAt">Date de création</SelectItem>
            <SelectItem value="price">Prix</SelectItem>
            <SelectItem value="title">Titre</SelectItem>
            <SelectItem value="updatedAt">Dernière modification</SelectItem>
          </SelectContent>
        </Select>
      </div>
    </div>

    <!-- Vue Grille -->
    <div v-if="viewMode === 'grid'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <Card
        v-for="property in filteredProperties"
        :key="property.id"
        class="cursor-pointer hover:shadow-lg transition-shadow overflow-hidden"
        @click="viewProperty(Number(property.id))"
      >
        <div class="relative h-48 bg-muted overflow-hidden">
          <img
            v-if="property.images && property.images.length > 0"
            :src="property.images[0]"
            :alt="property.title"
            class="w-full h-full object-cover"
          />
          <div v-else class="w-full h-full flex items-center justify-center">
            <Home class="h-12 w-12 text-muted-foreground" />
          </div>
          <Badge class="absolute top-2 right-2" :variant="getStatusVariant(property.status)">
            {{ getStatusLabel(property.status) }}
          </Badge>
        </div>
        <CardHeader>
          <CardTitle class="line-clamp-1">{{ property.title }}</CardTitle>
          <CardDescription class="line-clamp-1">{{ property.address }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="space-y-2">
            <div class="flex items-center justify-between">
              <span class="text-sm text-muted-foreground">Type</span>
              <span class="text-sm font-medium">{{ property.propertyType }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm text-muted-foreground">Prix</span>
              <span class="text-lg font-bold text-primary">{{ formatPrice(property.price) }}</span>
            </div>
          </div>
        </CardContent>
        <CardFooter class="flex justify-between">
          <Button variant="ghost" size="sm" @click.stop="editProperty(Number(property.id))">
            <Edit class="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="sm" @click.stop="deleteProperty(Number(property.id))" class="text-destructive">
            <Trash2 class="h-4 w-4" />
          </Button>
        </CardFooter>
      </Card>
    </div>

    <!-- Vue Liste -->
    <Card v-else>
      <CardContent class="p-0">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Propriété</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Type</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Prix</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Statut</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Créée le</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-muted-foreground uppercase">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-border">
              <tr
                v-for="property in filteredProperties"
                :key="property.id"
                class="hover:bg-muted/50 transition-colors cursor-pointer"
                @click="viewProperty(Number(property.id))"
              >
                <td class="px-6 py-4">
                  <div>
                    <div class="text-sm font-medium">{{ property.title }}</div>
                    <div class="text-sm text-muted-foreground">{{ property.address }}</div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">{{ property.propertyType }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatPrice(property.price) }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge :variant="getStatusVariant(property.status)">
                    {{ getStatusLabel(property.status) }}
                  </Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatDate(property.createdAt) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right" @click.stop>
                  <DropdownMenu>
                    <DropdownMenuTrigger as-child>
                      <Button variant="ghost" size="sm">
                        <MoreVertical class="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem @click.stop="viewProperty(Number(property.id))">
                        <Eye class="mr-2 h-4 w-4" />
                        Voir
                      </DropdownMenuItem>
                      <DropdownMenuItem @click.stop="editProperty(Number(property.id))">
                        <Edit class="mr-2 h-4 w-4" />
                        Modifier
                      </DropdownMenuItem>
                      <DropdownMenuSeparator />
                      <DropdownMenuItem @click.stop="deleteProperty(Number(property.id))" class="text-destructive">
                        <Trash2 class="mr-2 h-4 w-4" />
                        Supprimer
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

    <!-- Pagination -->
    <div class="flex items-center justify-between">
      <div class="text-sm text-muted-foreground">
        Affichage de {{ startIndex + 1 }} à {{ endIndex }} sur {{ total }}
      </div>
      <div class="flex gap-2">
        <Button variant="outline" size="sm" :disabled="currentPage === 1" @click="previousPage">
          Précédent
        </Button>
        <Button variant="outline" size="sm" :disabled="currentPage === totalPages" @click="nextPage">
          Suivant
        </Button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import {
  Plus,
  Download,
  X,
  Home,
  Edit,
  Trash2,
  MoreVertical,
  Eye,
  Grid3x3,
  List
} from 'lucide-vue-next'
import { propertyService, type Property } from '@viridial/shared'

const router = useRouter()
const { toast } = useToast()
const loading = ref(false)
const searchQuery = ref('')
const selectedType = ref('')
const selectedStatus = ref('')
const priceMin = ref('')
const priceMax = ref('')
const viewMode = ref<'grid' | 'list'>('grid')
const sortBy = ref('createdAt')
const currentPage = ref(1)
const pageSize = 12

const properties = ref<Property[]>([])
const statsData = ref({ total: 0, available: 0, sold: 0, rented: 0 })

const stats = computed(() => statsData.value)

const filteredProperties = computed(() => {
  // Properties are already filtered by API, just apply local sorting and pagination
  let filtered = [...properties.value]

  // Sort
  filtered.sort((a, b) => {
    if (sortBy.value === 'price') return b.price - a.price
    if (sortBy.value === 'title') return a.title.localeCompare(b.title)
    if (sortBy.value === 'createdAt') {
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    }
    if (sortBy.value === 'updatedAt') {
      return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
    }
    return 0
  })

  // Pagination
  const start = (currentPage.value - 1) * pageSize
  return filtered.slice(start, start + pageSize)
})

const total = computed(() => properties.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize))
const startIndex = computed(() => (currentPage.value - 1) * pageSize)
const endIndex = computed(() => Math.min(startIndex.value + pageSize, total.value))

const handleSearch = () => {
  currentPage.value = 1
  loadProperties()
}

const handleFilter = () => {
  currentPage.value = 1
  loadProperties()
}

const handleSort = () => {
  currentPage.value = 1
  loadProperties()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = ''
  selectedStatus.value = ''
  priceMin.value = ''
  priceMax.value = ''
  currentPage.value = 1
}

const filterByStatus = (status: string) => {
  selectedStatus.value = status
  currentPage.value = 1
}

const previousPage = () => {
  if (currentPage.value > 1) currentPage.value--
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++
}

const openCreateDialog = () => {
  router.push('/properties/new')
}

const viewProperty = (id: number) => {
  router.push(`/properties/${id}`)
}

const editProperty = (id: number) => {
  router.push(`/properties/${id}/edit`)
}

const deleteProperty = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette propriété ?')) return
  loading.value = true
  try {
    await propertyService.delete(id)
    toast({
      title: 'Propriété supprimée',
      description: 'La propriété a été supprimée avec succès'
    })
    await loadProperties()
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

const exportData = () => {
  toast({
    title: 'Export en cours',
    description: 'Les données seront téléchargées sous peu'
  })
}

const getStatusVariant = (status: string) => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    AVAILABLE: 'default',
    SOLD: 'secondary',
    RENTED: 'outline',
    PENDING: 'destructive'
  }
  return variants[status] || 'default'
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    AVAILABLE: 'Disponible',
    SOLD: 'Vendu',
    RENTED: 'Loué',
    PENDING: 'En attente'
  }
  return labels[status] || status
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR')
}

const loadProperties = async () => {
  loading.value = true
  try {
    const params: any = {}
    
    if (selectedType.value) params.propertyType = selectedType.value
    if (selectedStatus.value) params.status = selectedStatus.value
    if (searchQuery.value) params.search = searchQuery.value
    if (priceMin.value) params.priceMin = Number(priceMin.value)
    if (priceMax.value) params.priceMax = Number(priceMax.value)

    const result = await propertyService.getAll(params)
    properties.value = result

    // Calculate stats
    statsData.value = {
      total: properties.value.length,
      available: properties.value.filter((p) => p.status === 'AVAILABLE').length,
      sold: properties.value.filter((p) => p.status === 'SOLD').length,
      rented: properties.value.filter((p) => p.status === 'RENTED').length
    }
  } catch (error: any) {
    console.error('Error loading properties:', error)
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les propriétés',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProperties()
})
</script>
