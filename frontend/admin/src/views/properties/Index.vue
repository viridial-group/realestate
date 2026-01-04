<template>
  <div class="space-y-6">
    <!-- Header avec Actions Rapides -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-2.5xl font-bold">Gestion des Propriétés</h1>
        <p class="text-muted-foreground mt-1">Gérez toutes les propriétés immobilières</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="exportToCSV" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Export CSV
        </Button>
        <Button variant="outline" @click="exportToExcel" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Export Excel
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          Nouvelle Propriété
        </Button>
      </div>
    </div>

    <!-- Stats Cards avec Actions Rapides -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <!-- Disponibles Card -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #33d484;" @click="filterByStatus('AVAILABLE')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Disponibles</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(51, 212, 132, 0.1);">
            <Home class="h-5 w-5" style="color: #33d484;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #33d484;">{{ stats.available || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">En vente/location</p>
        </CardContent>
      </Card>
      
      <!-- Vendues Card -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #fdb022;" @click="filterByStatus('SOLD')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Vendues</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(253, 176, 34, 0.1);">
            <CheckCircle class="h-5 w-5" style="color: #fdb022;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #fdb022;">{{ stats.sold || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Vendues</p>
        </CardContent>
      </Card>
      
      <!-- Louées Card -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #04c9ff;" @click="filterByStatus('RENTED')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Louées</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(4, 201, 255, 0.1);">
            <Key class="h-5 w-5" style="color: #04c9ff;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #04c9ff;">{{ stats.rented || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">En location</p>
        </CardContent>
      </Card>
      
      <!-- Total Card -->
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Total</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <Home class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
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
        <div class="space-y-6">
          <!-- Recherche et Filtres de base -->
          <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
            <div class="space-y-2">
              <Label>Recherche</Label>
              <Input v-model="searchQuery" placeholder="Titre, adresse..." @input="handleSearch" />
            </div>
            <div class="space-y-2">
              <Label>Organisation</Label>
              <Select v-model="selectedOrganizationId" @update:model-value="handleFilter">
                <SelectTrigger>
                  <SelectValue placeholder="Toutes" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem :value="null">Toutes</SelectItem>
                  <SelectItem 
                    v-for="org in organizations" 
                    :key="org.id" 
                    :value="org.id"
                  >
                    {{ org.name }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div class="space-y-2">
              <Label>Type</Label>
              <Select v-model="selectedType" @update:model-value="handleFilter">
                <SelectTrigger>
                  <SelectValue placeholder="Tous" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem :value="null">Tous</SelectItem>
                  <SelectItem value="HOUSE">Maison</SelectItem>
                  <SelectItem value="APARTMENT">Appartement</SelectItem>
                  <SelectItem value="VILLA">Villa</SelectItem>
                  <SelectItem value="LAND">Terrain</SelectItem>
                  <SelectItem value="COMMERCIAL">Commercial</SelectItem>
                  <SelectItem value="OTHER">Autre</SelectItem>
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
                  <SelectItem :value="null">Tous</SelectItem>
                  <SelectItem value="AVAILABLE">Disponible</SelectItem>
                  <SelectItem value="SOLD">Vendu</SelectItem>
                  <SelectItem value="RENTED">Loué</SelectItem>
                  <SelectItem value="PENDING">En attente</SelectItem>
                  <SelectItem value="DRAFT">Brouillon</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div class="flex items-end">
              <Button variant="outline" @click="resetFilters" class="w-full">
                <X class="mr-2 h-4 w-4" />
                Réinitialiser
              </Button>
            </div>
          </div>

          <!-- Filtres avancés avec sliders -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 pt-4 border-t">
            <!-- Prix -->
            <div class="space-y-3">
              <div class="flex items-center justify-between">
                <Label>Prix (€)</Label>
                <span class="text-sm text-muted-foreground">
                  {{ formatPrice(priceRange[0]) }} - {{ formatPrice(priceRange[1]) }}
                </span>
              </div>
              <Slider
                v-model="priceRange"
                :min="0"
                :max="maxPrice"
                :step="1000"
                class="w-full"
              />
              <div class="flex gap-2">
                <Input
                  v-model.number="priceMin"
                  type="number"
                  placeholder="Min"
                  class="flex-1"
                  :min="0"
                  @input="handlePriceInput"
                />
                <Input
                  v-model.number="priceMax"
                  type="number"
                  placeholder="Max"
                  class="flex-1"
                  :min="0"
                  @input="handlePriceInput"
                />
              </div>
            </div>

            <!-- Surface -->
            <div class="space-y-3">
              <div class="flex items-center justify-between">
                <Label>Surface (m²)</Label>
                <span class="text-sm text-muted-foreground">
                  {{ surfaceRange[0] }} - {{ surfaceRange[1] }} m²
                </span>
              </div>
              <Slider
                v-model="surfaceRange"
                :min="0"
                :max="maxSurface"
                :step="5"
                class="w-full"
              />
              <div class="flex gap-2">
                <Input
                  v-model.number="surfaceMin"
                  type="number"
                  placeholder="Min"
                  class="flex-1"
                  :min="0"
                  @input="handleSurfaceInput"
                />
                <Input
                  v-model.number="surfaceMax"
                  type="number"
                  placeholder="Max"
                  class="flex-1"
                  :min="0"
                  @input="handleSurfaceInput"
                />
              </div>
            </div>

            <!-- Chambres -->
            <div class="space-y-2">
              <Label>Chambres</Label>
              <Select v-model="selectedBedrooms" @update:model-value="handleFilter">
                <SelectTrigger>
                  <SelectValue placeholder="Toutes" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem :value="null">Toutes</SelectItem>
                  <SelectItem :value="1">1+</SelectItem>
                  <SelectItem :value="2">2+</SelectItem>
                  <SelectItem :value="3">3+</SelectItem>
                  <SelectItem :value="4">4+</SelectItem>
                  <SelectItem :value="5">5+</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <!-- Salles de bain -->
            <div class="space-y-2">
              <Label>Salles de bain</Label>
              <Select v-model="selectedBathrooms" @update:model-value="handleFilter">
                <SelectTrigger>
                  <SelectValue placeholder="Toutes" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem :value="null">Toutes</SelectItem>
                  <SelectItem :value="1">1+</SelectItem>
                  <SelectItem :value="2">2+</SelectItem>
                  <SelectItem :value="3">3+</SelectItem>
                  <SelectItem :value="4">4+</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Vue Grille/Liste/Carte -->
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
        <Button
          variant="outline"
          size="sm"
          :class="{ 'bg-primary text-primary-foreground': viewMode === 'map' }"
          @click="viewMode = 'map'"
        >
          <MapPin class="h-4 w-4" />
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
              <span class="text-sm font-medium">{{ getPropertyTypeLabel(getPropertyType(property)) }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm text-muted-foreground">Prix</span>
              <span class="text-lg font-bold text-primary">{{ formatPrice(property.price) }}</span>
            </div>
              <div v-if="loadingUnreadCounts || property.unreadMessagesCount !== undefined" class="flex items-center justify-between pt-2 border-t">
              <span class="text-sm text-muted-foreground">Messages</span>
              <div v-if="loadingUnreadCounts" class="flex items-center gap-1 text-muted-foreground">
                <Loader2 class="h-3 w-3 animate-spin" />
              </div>
              <router-link
                v-else-if="property.unreadMessagesCount && property.unreadMessagesCount > 0"
                :to="`/contacts?propertyId=${property.id}`"
                @click.stop
                class="inline-flex items-center"
              >
                <Badge variant="default" class="bg-blue-500 text-white hover:bg-blue-600 cursor-pointer">
                  <Mail class="h-3 w-3 mr-1" />
                  {{ property.unreadMessagesCount }} non lu{{ property.unreadMessagesCount > 1 ? 's' : '' }}
                </Badge>
              </router-link>
              <span v-else class="text-xs text-muted-foreground">Aucun</span>
            </div>
            <div v-if="propertyReviewStats[property.id]" class="flex items-center justify-between pt-2 border-t">
              <span class="text-sm text-muted-foreground">Avis</span>
              <div class="flex items-center gap-1">
                <Star class="h-3 w-3 text-yellow-500 fill-current" />
                <span class="text-sm font-medium">
                  {{ propertyReviewStats[property.id].averageRating.toFixed(1) }}
                </span>
                <span class="text-xs text-muted-foreground">
                  ({{ propertyReviewStats[property.id].totalReviews }})
                </span>
              </div>
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
    <Card v-if="viewMode === 'list'">
      <CardContent class="p-0">
        <!-- Actions en masse -->
        <div
          v-if="selectedIds.length > 0"
          class="border-b bg-muted/30 p-4 flex items-center justify-between"
        >
          <div class="text-sm text-muted-foreground">
            {{ selectedIds.length }} propriété(s) sélectionnée(s)
          </div>
          <div class="flex gap-2">
            <Button variant="outline" size="sm" @click="bulkDelete" class="text-destructive" :disabled="loading">
              <Trash2 class="mr-2 h-4 w-4" />
              Supprimer
            </Button>
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">
                  <Checkbox v-model="selectAll" />
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Propriété</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Organisation</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Type</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Prix</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Statut</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Créée le</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Messages</th>
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
                <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                  <Checkbox
                    :model-value="selectedIds.includes(Number(property.id))"
                    @update:model-value="(val) => handlePropertyCheckboxChange(Number(property.id), Boolean(val))"
                  />
                </td>
                <td class="px-6 py-4">
                  <div>
                    <div class="text-sm font-medium">{{ property.title }}</div>
                    <div class="text-sm text-muted-foreground">{{ property.address }}</div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  {{ getPropertyOrganizationName(property) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  {{ getPropertyAssignedUserName(property) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">{{ getPropertyTypeLabel(getPropertyType(property)) }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatPrice(property.price) }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge :variant="getStatusVariant(property.status)">
                    {{ getStatusLabel(property.status) }}
                  </Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatDate(property.createdAt) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm" @click.stop>
                  <div v-if="loadingUnreadCounts" class="flex items-center gap-1 text-muted-foreground">
                    <Loader2 class="h-3 w-3 animate-spin" />
                  </div>
                  <router-link
                    v-else-if="property.unreadMessagesCount && property.unreadMessagesCount > 0"
                    :to="`/contacts?propertyId=${property.id}`"
                    class="inline-flex items-center"
                  >
                    <Badge variant="default" class="bg-blue-500 text-white hover:bg-blue-600 cursor-pointer">
                      <Mail class="h-3 w-3 mr-1" />
                      {{ property.unreadMessagesCount }}
                    </Badge>
                  </router-link>
                  <span v-else class="text-muted-foreground text-xs">-</span>
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

    <!-- Vue Carte -->
    <div v-if="viewMode === 'map'" class="space-y-4">
      <PropertyMap
        :properties="filteredProperties"
        :selected-property-id="selectedPropertyId"
        @property-click="handleMapPropertyClick"
      />
    </div>

    <!-- Pagination -->
    <div v-if="viewMode !== 'map'" class="flex items-center justify-between">
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
import { ref, computed, onMounted, watch } from 'vue'
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
  List,
  CheckCircle,
  Key,
  MapPin,
  Mail,
  Loader2
} from 'lucide-vue-next'
import PropertyMap from '@/components/properties/PropertyMap.vue'
import { Checkbox } from '@/components/ui/checkbox'
import { Slider } from '@/components/ui/slider'
import { propertyService, organizationService, reviewService, type Property, type Organization, type ReviewStats } from '@viridial/shared'
import { Star } from 'lucide-vue-next'

const router = useRouter()
const { toast } = useToast()
const loading = ref(false)
const loadingUnreadCounts = ref(false)
const searchQuery = ref('')
const selectedOrganizationId = ref<number | null>(null)
const selectedType = ref<string | null>(null)
const selectedStatus = ref<string | null>(null)
const organizations = ref<Organization[]>([])
const priceMin = ref<number | ''>('')
const priceMax = ref<number | ''>('')
const priceRange = ref<number[]>([0, 1000000])
const surfaceMin = ref<number | ''>('')
const surfaceMax = ref<number | ''>('')
const surfaceRange = ref<number[]>([0, 500])
const selectedBedrooms = ref<number | null>(null)
const selectedBathrooms = ref<number | null>(null)
const viewMode = ref<'grid' | 'list' | 'map'>('grid')
const selectedPropertyId = ref<number | null>(null)
const sortBy = ref('createdAt')
const currentPage = ref(1)
const pageSize = 12

const properties = ref<Property[]>([])
const statsData = ref({ total: 0, available: 0, sold: 0, rented: 0 })
const selectedIds = ref<number[]>([])
const propertyReviewStats = ref<Record<number, ReviewStats>>({})
const loadingReviewStats = ref(false)

// Calculer les valeurs max pour les sliders
const maxPrice = computed(() => {
  if (properties.value.length === 0) return 1000000
  return Math.max(...properties.value.map(p => p.price), 1000000)
})

const maxSurface = computed(() => {
  if (properties.value.length === 0) return 500
  return Math.max(...properties.value.filter(p => p.area).map(p => p.area || 0), 500)
})

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

const getPropertyType = (property: Property) => {
  return (property as any).type || property.propertyType
}

const getPropertyOrganizationId = (property: Property) => {
  return (property as any).organizationId
}

const getPropertyOrganizationName = (property: Property) => {
  return (property as any).organizationName || getOrganizationName(getPropertyOrganizationId(property))
}

const getPropertyAssignedUserName = (property: Property) => {
  return (property as any).assignedUserName || '-'
}

const total = computed(() => properties.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize))
const startIndex = computed(() => (currentPage.value - 1) * pageSize)
const endIndex = computed(() => Math.min(startIndex.value + pageSize, total.value))

const selectAll = computed({
  get: (): boolean | 'indeterminate' => {
    if (filteredProperties.value.length === 0) return false
    if (selectedIds.value.length === 0) return false
    const pageIds = filteredProperties.value.map(p => Number(p.id))
    const allSelected = pageIds.every(id => selectedIds.value.includes(id))
    const someSelected = pageIds.some(id => selectedIds.value.includes(id))
    if (allSelected) return true
    if (someSelected) return 'indeterminate'
    return false
  },
  set: (value: boolean | 'indeterminate') => {
    const pageIds = filteredProperties.value.map(p => Number(p.id))
    if (value === true || value === 'indeterminate') {
      pageIds.forEach(id => {
        if (!selectedIds.value.includes(id)) {
          selectedIds.value.push(id)
        }
      })
    } else {
      selectedIds.value = selectedIds.value.filter(id => !pageIds.includes(id))
    }
  }
})

const handlePropertyCheckboxChange = (id: number, checked: boolean) => {
  if (checked) {
    if (!selectedIds.value.includes(id)) {
      selectedIds.value.push(id)
    }
  } else {
    const index = selectedIds.value.indexOf(id)
    if (index > -1) {
      selectedIds.value.splice(index, 1)
    }
  }
}

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
  selectedOrganizationId.value = null
  selectedType.value = null
  selectedStatus.value = null
  priceMin.value = ''
  priceMax.value = ''
  priceRange.value = [0, maxPrice.value]
  surfaceMin.value = ''
  surfaceMax.value = ''
  surfaceRange.value = [0, maxSurface.value]
  selectedBedrooms.value = null
  selectedBathrooms.value = null
  currentPage.value = 1
  loadProperties()
}

// Watch pour synchroniser les sliders avec les inputs
watch(priceRange, (newValue) => {
  if (newValue && newValue.length === 2) {
    priceMin.value = newValue[0] === 0 ? '' : newValue[0]
    priceMax.value = newValue[1] === maxPrice.value ? '' : newValue[1]
    handleFilter()
  }
}, { deep: true })

watch(surfaceRange, (newValue) => {
  if (newValue && newValue.length === 2) {
    surfaceMin.value = newValue[0] === 0 ? '' : newValue[0]
    surfaceMax.value = newValue[1] === maxSurface.value ? '' : newValue[1]
    handleFilter()
  }
}, { deep: true })

const handlePriceInput = () => {
  const min = typeof priceMin.value === 'number' ? priceMin.value : 0
  const max = typeof priceMax.value === 'number' ? priceMax.value : maxPrice.value
  priceRange.value = [min, max]
}

const handleSurfaceInput = () => {
  const min = typeof surfaceMin.value === 'number' ? surfaceMin.value : 0
  const max = typeof surfaceMax.value === 'number' ? surfaceMax.value : maxSurface.value
  surfaceRange.value = [min, max]
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

const handleMapPropertyClick = (id: number) => {
  selectedPropertyId.value = id
  // Optionnel: ouvrir un dialog ou naviguer vers la page de détail
  // viewProperty(id)
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

const bulkDelete = async () => {
  if (!confirm(`Êtes-vous sûr de vouloir supprimer ${selectedIds.value.length} propriété(s) ?`)) return
  loading.value = true
  try {
    await Promise.all(selectedIds.value.map((id) => propertyService.delete(id)))
    toast({
      title: 'Propriétés supprimées',
      description: `${selectedIds.value.length} propriété(s) supprimée(s)`
    })
    selectedIds.value = []
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

// Export functions
const exportToCSV = async () => {
  try {
    // Charger toutes les propriétés avec les filtres actuels
    const params: any = {}
    if (searchQuery.value) params.search = searchQuery.value
    if (selectedType.value) params.propertyType = selectedType.value
    if (selectedStatus.value) params.status = selectedStatus.value
    if (priceMin.value) params.minPrice = Number(priceMin.value)
    if (priceMax.value) params.maxPrice = Number(priceMax.value)

    const propertiesToExport = await propertyService.getAll(params)

    // Créer le contenu CSV
    const headers = ['ID', 'Titre', 'Type', 'Prix', 'Adresse', 'Ville', 'Statut', 'Chambres', 'Salles de bain', 'Surface', 'Créée le']
    const rows = propertiesToExport.map((property: any) => [
      property.id,
      property.title,
      getPropertyTypeLabel(getPropertyType(property)),
      formatPrice(property.price),
      property.address,
      property.city,
      getStatusLabel(property.status),
      property.bedrooms || '',
      property.bathrooms || '',
      property.area || '',
      formatDate(property.createdAt)
    ])

    const csvContent = [
      headers.join(','),
      ...rows.map((row: any[]) => row.map((cell: any) => `"${String(cell).replace(/"/g, '""')}"`).join(','))
    ].join('\n')

    // Télécharger le fichier
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `properties_export_${new Date().toISOString().split('T')[0]}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    toast({
      title: 'Export réussi',
      description: `${propertiesToExport.length} propriété(s) exportée(s) en CSV`
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'exporter les données',
      variant: 'destructive'
    })
  }
}

const exportToExcel = async () => {
  try {
    // Charger toutes les propriétés avec les filtres actuels
    const params: any = {}
    if (searchQuery.value) params.search = searchQuery.value
    if (selectedType.value) params.propertyType = selectedType.value
    if (selectedStatus.value) params.status = selectedStatus.value
    if (priceMin.value) params.minPrice = Number(priceMin.value)
    if (priceMax.value) params.maxPrice = Number(priceMax.value)

    const propertiesToExport = await propertyService.getAll(params)

    // Créer le contenu Excel (format TSV avec en-têtes)
    const headers = ['ID', 'Titre', 'Type', 'Prix', 'Adresse', 'Ville', 'Statut', 'Chambres', 'Salles de bain', 'Surface', 'Créée le']
    const rows = propertiesToExport.map((property: any) => [
      property.id,
      property.title,
      getPropertyTypeLabel(getPropertyType(property)),
      formatPrice(property.price),
      property.address,
      property.city,
      getStatusLabel(property.status),
      property.bedrooms || '',
      property.bathrooms || '',
      property.area || '',
      formatDate(property.createdAt)
    ])

    const excelContent = [
      headers.join('\t'),
      ...rows.map((row: any[]) => row.map((cell: any) => String(cell).replace(/\t/g, ' ')).join('\t'))
    ].join('\n')

    // Télécharger le fichier
    const blob = new Blob(['\ufeff' + excelContent], { type: 'application/vnd.ms-excel;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `properties_export_${new Date().toISOString().split('T')[0]}.xls`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    toast({
      title: 'Export réussi',
      description: `${propertiesToExport.length} propriété(s) exportée(s) en Excel`
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'exporter les données',
      variant: 'destructive'
    })
  }
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
    PENDING: 'En attente',
    DRAFT: 'Brouillon',
    PUBLISHED: 'Publié',
    ARCHIVED: 'Archivé'
  }
  return labels[status] || status
}

const getPropertyTypeLabel = (type: string) => {
  const labels: Record<string, string> = {
    APARTMENT: 'Appartement',
    HOUSE: 'Maison',
    VILLA: 'Villa',
    LAND: 'Terrain',
    COMMERCIAL: 'Commercial',
    OTHER: 'Autre'
  }
  return labels[type] || type
}

const getOrganizationName = (organizationId: number | undefined) => {
  if (!organizationId) return '-'
  const org = organizations.value.find(o => o.id === organizationId)
  return org?.name || '-'
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
    
    if (selectedOrganizationId.value) params.organizationId = selectedOrganizationId.value
    if (selectedType.value) params.propertyType = selectedType.value
    if (selectedStatus.value) params.status = selectedStatus.value
    if (searchQuery.value) params.search = searchQuery.value
    
    // Prix
    if (priceMin.value !== '' && typeof priceMin.value === 'number') {
      params.minPrice = priceMin.value
    }
    if (priceMax.value !== '' && typeof priceMax.value === 'number') {
      params.maxPrice = priceMax.value
    }
    
    // Surface
    if (surfaceMin.value !== '' && typeof surfaceMin.value === 'number') {
      params.minSurface = surfaceMin.value
    }
    if (surfaceMax.value !== '' && typeof surfaceMax.value === 'number') {
      params.maxSurface = surfaceMax.value
    }
    
    // Chambres et salles de bain
    if (selectedBedrooms.value !== null) {
      params.bedrooms = selectedBedrooms.value
    }
    if (selectedBathrooms.value !== null) {
      params.bathrooms = selectedBathrooms.value
    }

    const result = await propertyService.getAll(params)
    properties.value = Array.isArray(result) ? result : []

    // Mettre à jour les sliders avec les nouvelles valeurs max après chargement
    if (properties.value.length > 0) {
      const newMaxPrice = Math.max(...properties.value.map(p => p.price), 1000000)
      const newMaxSurface = Math.max(...properties.value.filter(p => p.area).map(p => p.area || 0), 500)
      
      // Mettre à jour les ranges si nécessaire
      if (priceRange.value[1] === maxPrice.value || priceRange.value[1] > newMaxPrice) {
        priceRange.value[1] = newMaxPrice
      }
      if (surfaceRange.value[1] === maxSurface.value || surfaceRange.value[1] > newMaxSurface) {
        surfaceRange.value[1] = newMaxSurface
      }
    }

    // Calculate stats
    statsData.value = {
      total: properties.value.length,
      available: properties.value.filter((p) => p.status === 'AVAILABLE').length,
      sold: properties.value.filter((p) => p.status === 'SOLD').length,
      rented: properties.value.filter((p) => p.status === 'RENTED').length
    }

    // Charger les comptes de messages non lus de façon asynchrone
    await loadUnreadMessagesCounts()
    await loadReviewStats()
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

const loadUnreadMessagesCounts = async () => {
  if (properties.value.length === 0) return
  
  loadingUnreadCounts.value = true
  try {
    const propertyIds = properties.value.map(p => Number(p.id))
    const counts = await propertyService.getUnreadMessagesCount(propertyIds)
    
    // Mettre à jour les propriétés avec les comptes
    properties.value = properties.value.map(property => ({
      ...property,
      unreadMessagesCount: counts[Number(property.id)] || 0
    }))
  } catch (error: any) {
    console.error('Error loading unread messages counts:', error)
    // Ne pas afficher d'erreur toast pour ne pas perturber l'utilisateur
    // Les comptes sont optionnels
  } finally {
    loadingUnreadCounts.value = false
  }
}

const loadReviewStats = async () => {
  if (properties.value.length === 0) return
  
  loadingReviewStats.value = true
  try {
    // Charger les stats pour toutes les propriétés en parallèle
    const statsPromises = properties.value.map(async (property) => {
      try {
        const stats = await reviewService.getStatsByProperty(Number(property.id))
        return { propertyId: Number(property.id), stats }
      } catch (error) {
        // Si une propriété n'a pas d'avis, on ignore l'erreur
        return null
      }
    })
    
    const results = await Promise.all(statsPromises)
    results.forEach((result) => {
      if (result && result.stats.totalReviews > 0) {
        propertyReviewStats.value[result.propertyId] = result.stats
      }
    })
  } catch (error: any) {
    console.error('Error loading review stats:', error)
  } finally {
    loadingReviewStats.value = false
  }
}

const loadOrganizations = async () => {
  try {
    const result = await organizationService.getAll()
    organizations.value = result.organizations
  } catch (error: any) {
    console.error('Error loading organizations:', error)
  }
}

onMounted(async () => {
  await loadOrganizations()
  await loadProperties()
})
</script>
