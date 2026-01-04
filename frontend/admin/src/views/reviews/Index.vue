<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div class="flex-1">
        <div class="flex items-center gap-3">
          <h1 class="text-2.5xl font-bold">Modération des Avis</h1>
          <Badge 
            v-if="stats.pending > 0" 
            variant="default" 
            class="bg-orange-500 text-white px-3 py-1 text-sm font-semibold"
          >
            <Clock class="mr-1 h-4 w-4" />
            {{ stats.pending }} {{ stats.pending === 1 ? 'avis en attente' : 'avis en attente' }}
          </Badge>
        </div>
        <p class="text-muted-foreground mt-1">
          Gérez et modérez les avis clients sur vos propriétés
          <span v-if="stats.total > 0" class="ml-2 text-sm">
            ({{ stats.total }} {{ stats.total === 1 ? 'avis au total' : 'avis au total' }})
          </span>
        </p>
      </div>
      <div class="flex items-center gap-2">
        <Button variant="outline" @click="loadReviews" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          Actualiser
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <!-- Avis en attente -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4 border-l-orange-500" @click="filterByStatus('PENDING')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>En attente</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center bg-orange-500/10">
            <Clock class="h-5 w-5 text-orange-500" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-orange-500">{{ stats.pending || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">En attente de modération</p>
        </CardContent>
      </Card>

      <!-- Avis approuvés -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4 border-l-green-500" @click="filterByStatus('APPROVED')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Approuvés</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center bg-green-500/10">
            <CheckCircle class="h-5 w-5 text-green-500" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-green-500">{{ stats.approved || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Avis publiés</p>
        </CardContent>
      </Card>

      <!-- Avis rejetés -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4 border-l-red-500" @click="filterByStatus('REJECTED')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Rejetés</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center bg-red-500/10">
            <XCircle class="h-5 w-5 text-red-500" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-red-500">{{ stats.rejected || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Avis rejetés</p>
        </CardContent>
      </Card>

      <!-- Total -->
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Total</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <Star class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.total || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Tous les avis</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="flex flex-wrap items-center gap-4">
          <!-- Recherche -->
          <div class="flex-1 min-w-[200px] relative">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              v-model="searchQuery"
              placeholder="Rechercher par auteur, commentaire..."
              class="w-full pl-10"
              @input="handleSearch"
            />
          </div>

          <!-- Filtre par statut -->
          <Select v-model="selectedStatus" @update:model-value="handleFilter">
            <SelectTrigger class="w-[180px]">
              <SelectValue placeholder="Tous les statuts" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Tous les statuts</SelectItem>
              <SelectItem value="PENDING">En attente</SelectItem>
              <SelectItem value="APPROVED">Approuvés</SelectItem>
              <SelectItem value="REJECTED">Rejetés</SelectItem>
            </SelectContent>
          </Select>

          <!-- Filtre par propriété -->
          <Select v-model="selectedPropertyId" @update:model-value="handleFilter">
            <SelectTrigger class="w-[250px]">
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

          <!-- Filtre par note -->
          <Select v-model="selectedRating" @update:model-value="handleFilter">
            <SelectTrigger class="w-[150px]">
              <SelectValue placeholder="Toutes les notes" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Toutes les notes</SelectItem>
              <SelectItem value="5">5 étoiles</SelectItem>
              <SelectItem value="4">4 étoiles</SelectItem>
              <SelectItem value="3">3 étoiles</SelectItem>
              <SelectItem value="2">2 étoiles</SelectItem>
              <SelectItem value="1">1 étoile</SelectItem>
            </SelectContent>
          </Select>

          <!-- Bouton réinitialiser -->
          <Button variant="outline" @click="resetFilters" size="sm">
            <X class="mr-2 h-4 w-4" />
            Réinitialiser
          </Button>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des avis -->
    <Card>
      <CardContent class="p-0">
        <div v-if="loading" class="flex items-center justify-center p-12">
          <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
        </div>

        <div v-else-if="filteredReviews.length === 0" class="flex flex-col items-center justify-center p-12 text-center">
          <Star class="h-12 w-12 text-muted-foreground mb-4" />
          <p class="text-lg font-medium text-muted-foreground">Aucun avis trouvé</p>
          <p class="text-sm text-muted-foreground mt-2">
            {{ searchQuery || selectedStatus !== 'all' || selectedPropertyId !== 'all' || selectedRating !== 'all' 
              ? 'Essayez de modifier vos filtres' 
              : 'Aucun avis n\'a encore été soumis' }}
          </p>
        </div>

        <div v-else class="divide-y">
          <div
            v-for="review in filteredReviews"
            :key="review.id"
            class="p-6 hover:bg-muted/50 transition-colors"
          >
            <div class="flex items-start justify-between gap-4">
              <!-- Contenu principal -->
              <div class="flex-1">
                <div class="flex items-start gap-4">
                  <!-- Note -->
                  <div class="flex flex-col items-center gap-1 min-w-[60px]">
                    <div class="flex items-center gap-1">
                      <Star
                        v-for="i in 5"
                        :key="i"
                        :class="[
                          'h-5 w-5',
                          i <= review.rating ? 'text-yellow-400 fill-current' : 'text-gray-300'
                        ]"
                      />
                    </div>
                    <span class="text-xs text-muted-foreground">{{ review.rating }}/5</span>
                  </div>

                  <!-- Informations -->
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-2">
                      <h3 class="font-semibold text-lg">{{ review.authorName }}</h3>
                      <Badge
                        :variant="getStatusVariant(review.status)"
                        class="text-xs"
                      >
                        {{ getStatusLabel(review.status) }}
                      </Badge>
                      <Badge v-if="review.isVerifiedBuyer" variant="outline" class="text-xs">
                        <CheckCircle class="mr-1 h-3 w-3" />
                        Achat vérifié
                      </Badge>
                    </div>

                    <p class="text-sm text-muted-foreground mb-2">
                      {{ review.authorEmail }}
                    </p>

                    <p class="text-sm mb-3 whitespace-pre-wrap">{{ review.comment }}</p>

                    <div class="flex items-center gap-4 text-xs text-muted-foreground">
                      <div class="flex items-center gap-1">
                        <Home class="h-3 w-3" />
                        <span>{{ review.propertyTitle || `Propriété #${review.propertyId}` }}</span>
                      </div>
                      <div class="flex items-center gap-1">
                        <Clock class="h-3 w-3" />
                        <span>{{ formatDate(review.createdAt) }}</span>
                      </div>
                      <div v-if="review.helpfulCount > 0" class="flex items-center gap-1">
                        <ThumbsUp class="h-3 w-3" />
                        <span>{{ review.helpfulCount }} {{ review.helpfulCount === 1 ? 'utile' : 'utiles' }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Actions -->
              <div class="flex items-center gap-2">
                <DropdownMenu>
                  <DropdownMenuTrigger as-child>
                    <Button variant="ghost" size="sm">
                      <MoreHorizontal class="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem @click="viewProperty(review.propertyId)">
                      <Home class="mr-2 h-4 w-4" />
                      Voir la propriété
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      v-if="review.status === 'PENDING'"
                      @click="updateReviewStatus(review.id, 'APPROVED')"
                      class="text-green-600"
                    >
                      <CheckCircle class="mr-2 h-4 w-4" />
                      Approuver
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      v-if="review.status === 'PENDING'"
                      @click="updateReviewStatus(review.id, 'REJECTED')"
                      class="text-red-600"
                    >
                      <XCircle class="mr-2 h-4 w-4" />
                      Rejeter
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      v-if="review.status === 'APPROVED'"
                      @click="updateReviewStatus(review.id, 'REJECTED')"
                      class="text-red-600"
                    >
                      <XCircle class="mr-2 h-4 w-4" />
                      Rejeter
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      v-if="review.status === 'REJECTED'"
                      @click="updateReviewStatus(review.id, 'APPROVED')"
                      class="text-green-600"
                    >
                      <CheckCircle class="mr-2 h-4 w-4" />
                      Approuver
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      @click="deleteReview(review.id)"
                      class="text-red-600"
                    >
                      <Trash2 class="mr-2 h-4 w-4" />
                      Supprimer
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="flex items-center justify-between p-4 border-t">
          <div class="text-sm text-muted-foreground">
            Page {{ currentPage + 1 }} sur {{ totalPages }} ({{ totalElements }} avis)
          </div>
          <div class="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              @click="goToPage(currentPage - 1)"
              :disabled="currentPage === 0"
            >
              Précédent
            </Button>
            <Button
              variant="outline"
              size="sm"
              @click="goToPage(currentPage + 1)"
              :disabled="currentPage >= totalPages - 1"
            >
              Suivant
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { reviewService, propertyService } from '@viridial/shared'
import type { Review, ReviewStatus } from '@viridial/shared'
import type { Property } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from '@/components/ui/select'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import {
  Star,
  Clock,
  CheckCircle,
  XCircle,
  RefreshCw,
  Search,
  X,
  MoreHorizontal,
  Home,
  ThumbsUp,
  Trash2,
  Loader2
} from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast'

const router = useRouter()
const { toast } = useToast()

const reviews = ref<Review[]>([])
const properties = ref<Property[]>([])
const loading = ref(false)
const totalElements = ref(0)
const totalPages = ref(0)
const currentPage = ref(0)
const pageSize = 20

const searchQuery = ref('')
const selectedStatus = ref<string>('all')
const selectedPropertyId = ref<string>('all')
const selectedRating = ref<string>('all')

const stats = ref({
  pending: 0,
  approved: 0,
  rejected: 0,
  total: 0
})

// Computed - Les filtres sont appliqués côté serveur via loadReviews
const filteredReviews = computed(() => {
  let filtered = [...reviews.value]

  // Filtre par propriété (côté client car on a déjà chargé toutes les propriétés)
  if (selectedPropertyId.value !== 'all') {
    filtered = filtered.filter(r => r.propertyId === Number(selectedPropertyId.value))
  }

  // Filtre par note (côté client)
  if (selectedRating.value !== 'all') {
    filtered = filtered.filter(r => r.rating === Number(selectedRating.value))
  }

  // Recherche (côté client)
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(r =>
      r.authorName?.toLowerCase().includes(query) ||
      r.authorEmail?.toLowerCase().includes(query) ||
      r.comment?.toLowerCase().includes(query) ||
      (r.propertyTitle && r.propertyTitle.toLowerCase().includes(query))
    )
  }

  // Pagination côté client
  const start = currentPage.value * pageSize
  const end = start + pageSize
  return filtered.slice(start, end)
})

// Methods
const loadReviews = async () => {
  loading.value = true
  try {
    // Charger les avis par statut pour les statistiques
    const [pendingData, approvedData, rejectedData] = await Promise.all([
      reviewService.getByStatus('PENDING', { page: 0, size: 1 }),
      reviewService.getByStatus('APPROVED', { page: 0, size: 1 }),
      reviewService.getByStatus('REJECTED', { page: 0, size: 1 })
    ])

    stats.value = {
      pending: pendingData.totalElements,
      approved: approvedData.totalElements,
      rejected: rejectedData.totalElements,
      total: pendingData.totalElements + approvedData.totalElements + rejectedData.totalElements
    }

    // Charger tous les avis selon le filtre de statut sélectionné
    const allReviews: Review[] = []
    
    if (selectedStatus.value === 'all') {
      // Charger tous les statuts
      let page = 0
      let hasMore = true
      while (hasMore) {
        const data = await reviewService.getByStatus('PENDING', { page, size: 100 })
        allReviews.push(...data.content)
        if (data.content.length < 100 || data.content.length === 0) hasMore = false
        else page++
      }

      page = 0
      hasMore = true
      while (hasMore) {
        const data = await reviewService.getByStatus('APPROVED', { page, size: 100 })
        allReviews.push(...data.content)
        if (data.content.length < 100 || data.content.length === 0) hasMore = false
        else page++
      }

      page = 0
      hasMore = true
      while (hasMore) {
        const data = await reviewService.getByStatus('REJECTED', { page, size: 100 })
        allReviews.push(...data.content)
        if (data.content.length < 100 || data.content.length === 0) hasMore = false
        else page++
      }
    } else {
      // Charger uniquement le statut sélectionné
      let page = 0
      let hasMore = true
      while (hasMore) {
        const data = await reviewService.getByStatus(selectedStatus.value, { page, size: 100 })
        allReviews.push(...data.content)
        if (data.content.length < 100 || data.content.length === 0) hasMore = false
        else page++
      }
    }

    reviews.value = allReviews
    totalElements.value = allReviews.length
    totalPages.value = Math.ceil(allReviews.length / pageSize)
  } catch (error: any) {
    console.error('Error loading reviews:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les avis',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadProperties = async () => {
  try {
    const data = await propertyService.search({ page: 0, size: 1000 })
    properties.value = data.content || []
  } catch (error) {
    console.error('Error loading properties:', error)
  }
}

const handleSearch = () => {
  // Le computed filteredReviews se met à jour automatiquement
}

const handleFilter = () => {
  // Recharger les avis si le statut change
  if (selectedStatus.value !== 'all') {
    currentPage.value = 0
    loadReviews()
  } else {
    // Le computed filteredReviews se met à jour automatiquement
    currentPage.value = 0
  }
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedStatus.value = 'all'
  selectedPropertyId.value = 'all'
  selectedRating.value = 'all'
}

const filterByStatus = (status: string) => {
  selectedStatus.value = status === 'all' ? 'all' : status
  currentPage.value = 0
  loadReviews()
}

const updateReviewStatus = async (id: number, status: ReviewStatus) => {
  try {
    await reviewService.updateStatus(id, status)
    toast({
      title: 'Succès',
      description: `Avis ${status === 'APPROVED' ? 'approuvé' : 'rejeté'} avec succès`
    })
    await loadReviews()
  } catch (error: any) {
    console.error('Error updating review status:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de mettre à jour le statut de l\'avis',
      variant: 'destructive'
    })
  }
}

const deleteReview = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cet avis ?')) {
    return
  }

  try {
    await reviewService.delete(id)
    toast({
      title: 'Succès',
      description: 'Avis supprimé avec succès'
    })
    await loadReviews()
  } catch (error: any) {
    console.error('Error deleting review:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de supprimer l\'avis',
      variant: 'destructive'
    })
  }
}

const viewProperty = (propertyId: number) => {
  router.push(`/properties/${propertyId}`)
}

const goToPage = (page: number) => {
  currentPage.value = page
  // La pagination est gérée côté client avec filteredReviews
}

const getStatusVariant = (status: string): string => {
  switch (status) {
    case 'APPROVED':
      return 'default'
    case 'PENDING':
      return 'secondary'
    case 'REJECTED':
      return 'destructive'
    default:
      return 'outline'
  }
}

const getStatusLabel = (status: string): string => {
  switch (status) {
    case 'APPROVED':
      return 'Approuvé'
    case 'PENDING':
      return 'En attente'
    case 'REJECTED':
      return 'Rejeté'
    default:
      return status
  }
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(async () => {
  await Promise.all([loadProperties(), loadReviews()])
})
</script>

