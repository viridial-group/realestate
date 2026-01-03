<template>
  <div class="space-y-6">
    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>
    <div v-else-if="property">
      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold">{{ property.title }}</h1>
          <p class="text-muted-foreground mt-1">{{ property.address }}</p>
        </div>
        <div class="flex gap-2">
          <Button variant="outline" @click="router.push(`/properties/${property.id}/edit`)">
            <Edit class="mr-2 h-4 w-4" />
            {{ t('common.edit') }}
          </Button>
          <Button variant="outline" @click="router.push('/properties')">
            <ArrowLeft class="mr-2 h-4 w-4" />
            {{ t('common.back') }}
          </Button>
        </div>
      </div>

      <!-- Images -->
      <Card v-if="property.images && property.images.length > 0">
        <CardContent class="p-0">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-2 p-2">
            <img
              v-for="(image, index) in property.images"
              :key="index"
              :src="image"
              :alt="property.title"
              class="w-full h-48 object-cover rounded-lg"
            />
          </div>
        </CardContent>
      </Card>

      <!-- Informations principales -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>{{ t('common.details') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div class="flex items-center justify-between">
              <span class="text-muted-foreground">{{ t('common.status') }}</span>
              <Badge :variant="getStatusVariant(property.status)">
                {{ getStatusLabel(property.status) }}
              </Badge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-muted-foreground">{{ t('properties.type.type') }}</span>
              <span class="font-medium">{{ getPropertyTypeLabel(property.propertyType || property.type) }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-muted-foreground">{{ t('common.total') }}</span>
              <span class="text-2xl font-bold text-primary">{{ formatPrice(property.price) }}</span>
            </div>
            <div v-if="property.area" class="flex items-center justify-between">
              <span class="text-muted-foreground">Surface</span>
              <span class="font-medium">{{ property.area }} m²</span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Description</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-muted-foreground">{{ property.description || 'Aucune description' }}</p>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { propertyService, type Property } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Edit, ArrowLeft, Loader2 } from 'lucide-vue-next'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const { toast } = useToast()

const loading = ref(false)
const property = ref<Property | null>(null)

const loadProperty = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    property.value = await propertyService.getById(id)
  } catch (error) {
    console.error('Error loading property:', error)
    toast({
      title: t('common.error'),
      description: 'Erreur lors du chargement de la propriété',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

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

onMounted(() => {
  loadProperty()
})
</script>

