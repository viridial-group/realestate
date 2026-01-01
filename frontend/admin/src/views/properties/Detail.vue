<template>
  <div class="space-y-6">
    <!-- Header avec Actions -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="icon" @click="goBack">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-3xl font-bold">{{ property?.title || 'Chargement...' }}</h1>
          <p class="text-muted-foreground mt-1">{{ property?.address }}, {{ property?.city }}</p>
        </div>
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
        <Button variant="outline" @click="editProperty" :disabled="loading">
          <Edit class="mr-2 h-4 w-4" />
          Modifier
        </Button>
        <Button variant="destructive" @click="deleteProperty" :disabled="loading">
          <Trash2 class="mr-2 h-4 w-4" />
          Supprimer
        </Button>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>

    <div v-else-if="property" class="space-y-6">
      <!-- Galerie d'images -->
      <Card v-if="property.images && property.images.length > 0">
        <CardHeader>
          <CardTitle>Galerie d'images</CardTitle>
        </CardHeader>
        <CardContent>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div
              v-for="(image, index) in property.images"
              :key="index"
              class="relative group cursor-pointer"
              @click="openImageModal(image)"
            >
              <img
                :src="image"
                :alt="`Image ${index + 1}`"
                class="w-full h-48 object-cover rounded-lg"
              />
              <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity rounded-lg flex items-center justify-center">
                <Eye class="h-6 w-6 text-white" />
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Informations principales -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Colonne principale -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Description -->
          <Card>
            <CardHeader>
              <CardTitle>Description</CardTitle>
            </CardHeader>
            <CardContent>
              <p class="text-muted-foreground whitespace-pre-wrap">{{ property.description || 'Aucune description' }}</p>
            </CardContent>
          </Card>

          <!-- Caractéristiques -->
          <Card>
            <CardHeader>
              <CardTitle>Caractéristiques</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Type</p>
                  <p class="font-semibold">{{ getTypeLabel(property.propertyType) }}</p>
                </div>
                <div class="space-y-1" v-if="property.bedrooms">
                  <p class="text-sm text-muted-foreground">Chambres</p>
                  <p class="font-semibold">{{ property.bedrooms }}</p>
                </div>
                <div class="space-y-1" v-if="property.bathrooms">
                  <p class="text-sm text-muted-foreground">Salles de bain</p>
                  <p class="font-semibold">{{ property.bathrooms }}</p>
                </div>
                <div class="space-y-1" v-if="property.area">
                  <p class="text-sm text-muted-foreground">Surface</p>
                  <p class="font-semibold">{{ property.area }} m²</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Localisation -->
          <Card>
            <CardHeader>
              <CardTitle>Localisation</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="space-y-2">
                <div class="flex items-start gap-2">
                  <MapPin class="h-5 w-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p class="font-semibold">{{ property.address }}</p>
                    <p class="text-sm text-muted-foreground">{{ property.city }}</p>
                    <p class="text-sm text-muted-foreground">{{ property.country }}</p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Colonne latérale -->
        <div class="space-y-6">
          <!-- Statut et Prix -->
          <Card>
            <CardHeader>
              <CardTitle>Informations</CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <p class="text-sm text-muted-foreground mb-1">Statut</p>
                <Badge :variant="getStatusVariant(property.status)">
                  {{ getStatusLabel(property.status) }}
                </Badge>
              </div>
              <div>
                <p class="text-sm text-muted-foreground mb-1">Prix</p>
                <p class="text-2xl font-bold text-primary">{{ formatPrice(property.price) }}</p>
              </div>
            </CardContent>
          </Card>

          <!-- Dates -->
          <Card>
            <CardHeader>
              <CardTitle>Dates</CardTitle>
            </CardHeader>
            <CardContent class="space-y-2">
              <div>
                <p class="text-sm text-muted-foreground">Créée le</p>
                <p class="font-semibold">{{ formatDate(property.createdAt) }}</p>
              </div>
              <div v-if="property.updatedAt">
                <p class="text-sm text-muted-foreground">Modifiée le</p>
                <p class="font-semibold">{{ formatDate(property.updatedAt) }}</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>

    <!-- Modal d'image -->
    <Dialog v-model:open="imageModalOpen">
      <DialogContent class="max-w-4xl">
        <DialogHeader>
          <DialogTitle>Image</DialogTitle>
        </DialogHeader>
        <div class="flex items-center justify-center">
          <img
            v-if="selectedImage"
            :src="selectedImage"
            alt="Image"
            class="max-w-full max-h-[80vh] object-contain"
          />
        </div>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import {
  ArrowLeft,
  Edit,
  Trash2,
  Download,
  MapPin,
  Eye,
  Loader2
} from 'lucide-vue-next'
import { propertyService, type Property, PropertyType, PropertyStatus } from '@viridial/shared'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()

const property = ref<Property | null>(null)
const loading = ref(false)
const imageModalOpen = ref(false)
const selectedImage = ref<string | null>(null)

const propertyId = computed(() => Number(route.params.id))

onMounted(() => {
  loadProperty()
})

const loadProperty = async () => {
  loading.value = true
  try {
    property.value = await propertyService.getById(propertyId.value)
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger la propriété',
      variant: 'destructive'
    })
    router.push('/properties')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/properties')
}

const editProperty = () => {
  router.push(`/properties/${propertyId.value}/edit`)
}

const deleteProperty = async () => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette propriété ?')) return
  loading.value = true
  try {
    await propertyService.delete(propertyId.value)
    toast({
      title: 'Propriété supprimée',
      description: 'La propriété a été supprimée avec succès'
    })
    router.push('/properties')
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de supprimer la propriété',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const openImageModal = (image: string) => {
  selectedImage.value = image
  imageModalOpen.value = true
}

// Export functions
const exportToCSV = () => {
  if (!property.value) return
  const headers = ['ID', 'Titre', 'Type', 'Prix', 'Adresse', 'Ville', 'Statut', 'Chambres', 'Salles de bain', 'Surface', 'Créée le']
  const row = [
    property.value.id,
    property.value.title,
    property.value.propertyType,
    formatPrice(property.value.price),
    property.value.address,
    property.value.city,
    getStatusLabel(property.value.status),
    property.value.bedrooms || '',
    property.value.bathrooms || '',
    property.value.area || '',
    formatDate(property.value.createdAt)
  ]
  const csvContent = [
    headers.join(','),
    row.map((cell: any) => `"${String(cell).replace(/"/g, '""')}"`).join(',')
  ].join('\n')
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', `property_${property.value.id}_export_${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  toast({
    title: 'Export réussi',
    description: 'Propriété exportée en CSV'
  })
}

const exportToExcel = () => {
  if (!property.value) return
  const headers = ['ID', 'Titre', 'Type', 'Prix', 'Adresse', 'Ville', 'Statut', 'Chambres', 'Salles de bain', 'Surface', 'Créée le']
  const row = [
    property.value.id,
    property.value.title,
    property.value.propertyType,
    formatPrice(property.value.price),
    property.value.address,
    property.value.city,
    getStatusLabel(property.value.status),
    property.value.bedrooms || '',
    property.value.bathrooms || '',
    property.value.area || '',
    formatDate(property.value.createdAt)
  ]
  const excelContent = [
    headers.join('\t'),
    row.map((cell: any) => String(cell).replace(/\t/g, ' ')).join('\t')
  ].join('\n')
  const blob = new Blob(['\ufeff' + excelContent], { type: 'application/vnd.ms-excel;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', `property_${property.value.id}_export_${new Date().toISOString().split('T')[0]}.xls`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  toast({
    title: 'Export réussi',
    description: 'Propriété exportée en Excel'
  })
}

// Utility functions
const getTypeLabel = (type: PropertyType) => {
  const labels: Record<PropertyType, string> = {
    [PropertyType.APARTMENT]: 'Appartement',
    [PropertyType.HOUSE]: 'Maison',
    [PropertyType.VILLA]: 'Villa',
    [PropertyType.LAND]: 'Terrain',
    [PropertyType.COMMERCIAL]: 'Commercial',
    [PropertyType.OTHER]: 'Autre'
  }
  return labels[type] || type
}

const getStatusLabel = (status: PropertyStatus) => {
  const labels: Record<PropertyStatus, string> = {
    [PropertyStatus.AVAILABLE]: 'Disponible',
    [PropertyStatus.SOLD]: 'Vendu',
    [PropertyStatus.RENTED]: 'Loué',
    [PropertyStatus.PENDING]: 'En attente',
    [PropertyStatus.DRAFT]: 'Brouillon'
  }
  return labels[status] || status
}

const getStatusVariant = (status: PropertyStatus): 'default' | 'secondary' | 'destructive' | 'outline' => {
  const variants: Record<PropertyStatus, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    [PropertyStatus.AVAILABLE]: 'default',
    [PropertyStatus.SOLD]: 'secondary',
    [PropertyStatus.RENTED]: 'outline',
    [PropertyStatus.PENDING]: 'destructive',
    [PropertyStatus.DRAFT]: 'outline'
  }
  return variants[status] || 'default'
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

