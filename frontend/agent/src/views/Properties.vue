<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">{{ t('properties.title') }}</h1>
        <p class="text-muted-foreground mt-1">{{ t('properties.description') }}</p>
      </div>
      <Button @click="openCreateDialog" size="lg">
        <Plus class="mr-2 h-4 w-4" />
        {{ t('properties.createProperty') }}
      </Button>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow border-l-4" style="border-left-color: #667eea;" @click="filterByStatus('AVAILABLE')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('properties.status.available') }}</CardDescription>
          <Home class="h-5 w-5" style="color: #667eea;" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #667eea;">{{ stats.available }}</div>
        </CardContent>
      </Card>

      <Card class="cursor-pointer hover:shadow-md transition-shadow border-l-4" style="border-left-color: #f59e0b;" @click="filterByStatus('SOLD')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('properties.status.sold') }}</CardDescription>
          <Key class="h-5 w-5" style="color: #f59e0b;" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #f59e0b;">{{ stats.sold }}</div>
        </CardContent>
      </Card>

      <Card class="cursor-pointer hover:shadow-md transition-shadow border-l-4" style="border-left-color: #10b981;" @click="filterByStatus('PENDING')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('properties.status.pending') }}</CardDescription>
          <Clock class="h-5 w-5" style="color: #10b981;" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #10b981;">{{ stats.pending }}</div>
        </CardContent>
      </Card>

      <Card class="border-l-4" style="border-left-color: #3b82f6;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('common.total') }}</CardDescription>
          <Home class="h-5 w-5" style="color: #3b82f6;" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #3b82f6;">{{ stats.total }}</div>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres simples -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>{{ t('common.search') }}</Label>
            <Input v-model="searchQuery" :placeholder="t('properties.searchPlaceholder')" @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>{{ t('properties.status.status') }}</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue :placeholder="t('common.all')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="null">{{ t('common.all') }}</SelectItem>
                <SelectItem value="AVAILABLE">{{ t('properties.status.available') }}</SelectItem>
                <SelectItem value="SOLD">{{ t('properties.status.sold') }}</SelectItem>
                <SelectItem value="RENTED">{{ t('properties.status.rented') }}</SelectItem>
                <SelectItem value="PENDING">{{ t('properties.status.pending') }}</SelectItem>
                <SelectItem value="DRAFT">{{ t('properties.status.draft') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>{{ t('properties.type.type') }}</Label>
            <Select v-model="selectedType" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue :placeholder="t('common.all')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="null">{{ t('common.all') }}</SelectItem>
                <SelectItem value="APARTMENT">{{ t('properties.type.apartment') }}</SelectItem>
                <SelectItem value="HOUSE">{{ t('properties.type.house') }}</SelectItem>
                <SelectItem value="LAND">{{ t('properties.type.land') }}</SelectItem>
                <SelectItem value="COMMERCIAL">{{ t('properties.type.commercial') }}</SelectItem>
                <SelectItem value="OFFICE">{{ t('properties.type.office') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>{{ t('common.actions') }}</Label>
            <Button variant="outline" class="w-full" @click="resetFilters">
              <X class="mr-2 h-4 w-4" />
              {{ t('common.reset') }}
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Vue Grille -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>
    <div v-else-if="properties.length === 0" class="text-center py-12">
      <Home class="h-12 w-12 text-muted-foreground mx-auto mb-4" />
      <p class="text-muted-foreground">{{ t('properties.noProperties') }}</p>
      <Button class="mt-4" @click="openCreateDialog">
        <Plus class="mr-2 h-4 w-4" />
        {{ t('properties.createProperty') }}
      </Button>
    </div>
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <Card
        v-for="property in properties"
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
              <span class="text-sm text-muted-foreground">{{ t('properties.type.type') }}</span>
              <span class="text-sm font-medium">{{ getPropertyTypeLabel(property.propertyType || property.type) }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm text-muted-foreground">{{ t('common.status') }}</span>
              <Badge :variant="getStatusVariant(property.status)" size="sm">
                {{ getStatusLabel(property.status) }}
              </Badge>
            </div>
            <div class="flex items-center justify-between pt-2 border-t">
              <span class="text-sm text-muted-foreground">{{ t('common.total') }}</span>
              <span class="text-lg font-bold text-primary">{{ formatPrice(property.price) }}</span>
            </div>
          </div>
        </CardContent>
        <CardFooter class="flex justify-between gap-2">
          <Button variant="outline" size="sm" class="flex-1" @click.stop="editProperty(Number(property.id))">
            <Edit class="mr-2 h-4 w-4" />
            {{ t('common.edit') }}
          </Button>
          <Button variant="outline" size="sm" class="flex-1 text-destructive" @click.stop="deleteProperty(Number(property.id))">
            <Trash2 class="mr-2 h-4 w-4" />
            {{ t('common.delete') }}
          </Button>
        </CardFooter>
      </Card>
    </div>

    <!-- Pagination -->
    <div v-if="properties.length > 0" class="flex items-center justify-between">
      <div class="text-sm text-muted-foreground">
        {{ t('common.total') }}: {{ properties.length }} {{ t('properties.title') }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { propertyService, type Property } from '@viridial/shared'
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import {
  Plus,
  X,
  Home,
  Edit,
  Trash2,
  Key,
  Clock,
  Loader2
} from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()
const { toast } = useToast()

const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref<string | null>(null)
const selectedType = ref<string | null>(null)
const properties = ref<Property[]>([])

const stats = computed(() => {
  const total = properties.value.length
  const available = properties.value.filter(p => p.status === 'AVAILABLE' || p.status === 'PUBLISHED').length
  const sold = properties.value.filter(p => p.status === 'SOLD').length
  const pending = properties.value.filter(p => p.status === 'PENDING' || p.status === 'DRAFT').length
  return { total, available, sold, pending }
})

const getStatusVariant = (status: string): 'default' | 'secondary' | 'destructive' | 'outline' => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    'AVAILABLE': 'default',
    'PUBLISHED': 'default',
    'SOLD': 'secondary',
    'RENTED': 'secondary',
    'PENDING': 'outline',
    'DRAFT': 'outline',
    'ARCHIVED': 'secondary'
  }
  return variants[status] || 'outline'
}

const getStatusLabel = (status: string): string => {
  const statusKey = status.toLowerCase()
  return t(`properties.status.${statusKey}`) || status
}

const getPropertyTypeLabel = (type: string): string => {
  const typeKey = type?.toLowerCase() || ''
  return t(`properties.type.${typeKey}`) || type
}

const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price)
}

const loadProperties = async () => {
  loading.value = true
  try {
    const params: any = {}
    if (selectedStatus.value) params.status = selectedStatus.value
    if (selectedType.value) params.propertyType = selectedType.value
    if (searchQuery.value) params.search = searchQuery.value

    const result = await propertyService.search(params)
    properties.value = result.content || []
  } catch (error) {
    console.error('Error loading properties:', error)
    toast({
      title: t('common.error'),
      description: 'Erreur lors du chargement des propriétés',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadProperties()
}

const handleFilter = () => {
  loadProperties()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedStatus.value = null
  selectedType.value = null
  loadProperties()
}

const filterByStatus = (status: string) => {
  selectedStatus.value = status
  loadProperties()
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
  if (!confirm(t('properties.deleteProperty'))) return
  
  try {
    await propertyService.delete(id)
    toast({
      title: t('common.success'),
      description: 'Propriété supprimée avec succès'
    })
    loadProperties()
  } catch (error) {
    console.error('Error deleting property:', error)
    toast({
      title: t('common.error'),
      description: 'Erreur lors de la suppression',
      variant: 'destructive'
    })
  }
}

onMounted(() => {
  loadProperties()
})
</script>
