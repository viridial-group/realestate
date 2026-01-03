<template>
  <div class="space-y-6">
    <!-- Header -->
    <div>
      <h1 class="text-3xl font-bold">{{ t('dashboard.title') }}</h1>
      <p class="text-muted-foreground mt-1">{{ t('dashboard.description') }}</p>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #667eea;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('dashboard.totalProperties') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(102, 126, 234, 0.1);">
            <Home class="h-5 w-5" style="color: #667eea;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #667eea;">{{ stats.totalProperties }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('properties.title') }}</p>
        </CardContent>
      </Card>

      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #10b981;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('dashboard.activeProperties') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(16, 185, 129, 0.1);">
            <CheckCircle class="h-5 w-5" style="color: #10b981;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #10b981;">{{ stats.activeProperties }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('properties.status.available') }}</p>
        </CardContent>
      </Card>

      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #f59e0b;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('dashboard.soldProperties') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(245, 158, 11, 0.1);">
            <Key class="h-5 w-5" style="color: #f59e0b;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #f59e0b;">{{ stats.soldProperties }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('properties.status.sold') }}</p>
        </CardContent>
      </Card>

      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #3b82f6;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('dashboard.totalClients') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(59, 130, 246, 0.1);">
            <Users class="h-5 w-5" style="color: #3b82f6;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #3b82f6;">{{ stats.totalClients }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('clients.title') }}</p>
        </CardContent>
      </Card>
    </div>

    <!-- Main Content Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Mes Propriétés -->
      <Card class="lg:col-span-2">
        <CardHeader>
          <div class="flex items-center justify-between">
            <CardTitle>{{ t('dashboard.myProperties') }}</CardTitle>
            <Button variant="outline" size="sm" @click="router.push('/properties')">
              {{ t('common.view') }} {{ t('common.details') }}
              <ChevronRight class="ml-2 h-4 w-4" />
            </Button>
          </div>
          <CardDescription>{{ t('dashboard.myPropertiesDescription') }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div v-if="loading" class="flex items-center justify-center py-8">
            <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
          </div>
          <div v-else-if="recentProperties.length === 0" class="text-center py-8 text-muted-foreground">
            {{ t('properties.noProperties') }}
          </div>
          <div v-else class="space-y-4">
            <div
              v-for="property in recentProperties"
              :key="property.id"
              class="flex items-center gap-4 p-4 border rounded-lg hover:bg-accent transition-colors cursor-pointer"
              @click="viewProperty(property.id)"
            >
              <div class="relative w-20 h-20 rounded-lg overflow-hidden bg-muted flex-shrink-0">
                <img
                  v-if="property.images && property.images.length > 0"
                  :src="property.images[0]"
                  :alt="property.title"
                  class="w-full h-full object-cover"
                />
                <div v-else class="w-full h-full flex items-center justify-center">
                  <Home class="h-8 w-8 text-muted-foreground" />
                </div>
              </div>
              <div class="flex-1 min-w-0">
                <h3 class="font-semibold truncate">{{ property.title }}</h3>
                <p class="text-sm text-muted-foreground truncate">{{ property.address }}</p>
                <div class="flex items-center gap-4 mt-2">
                  <Badge :variant="getStatusVariant(property.status)">
                    {{ t(`properties.status.${property.status.toLowerCase()}`) }}
                  </Badge>
                  <span class="text-sm font-semibold text-primary">
                    {{ formatPrice(property.price) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Activité Récente -->
      <Card>
        <CardHeader>
          <CardTitle>{{ t('dashboard.recentActivity') }}</CardTitle>
        </CardHeader>
        <CardContent>
          <div v-if="loading" class="flex items-center justify-center py-8">
            <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
          </div>
          <div v-else-if="recentActivity.length === 0" class="text-center py-8 text-sm text-muted-foreground">
            Aucune activité récente
          </div>
          <div v-else class="space-y-4">
            <div
              v-for="activity in recentActivity"
              :key="activity.id"
              class="flex items-start gap-3 p-3 rounded-lg hover:bg-accent transition-colors"
            >
              <div class="mt-0.5">
                <component :is="getActivityIcon(activity.type)" class="h-4 w-4 text-primary" />
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium">{{ activity.title }}</p>
                <p class="text-xs text-muted-foreground">{{ formatRelativeTime(activity.createdAt) }}</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { propertyService, type Property } from '@viridial/shared'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  Home,
  CheckCircle,
  Key,
  Users,
  ChevronRight,
  Loader2,
  Plus,
  Edit,
  Eye
} from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()
const { toast } = useToast()

const loading = ref(false)
const properties = ref<Property[]>([])

const stats = computed(() => {
  const total = properties.value.length
  const active = properties.value.filter(p => p.status === 'AVAILABLE' || p.status === 'PUBLISHED').length
  const sold = properties.value.filter(p => p.status === 'SOLD').length
  return {
    totalProperties: total,
    activeProperties: active,
    soldProperties: sold,
    totalClients: 0 // TODO: Implement client count
  }
})

const recentProperties = computed(() => {
  return properties.value
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5)
})

const recentActivity = computed(() => {
  // TODO: Implement real activity feed
  return []
})

const loadProperties = async () => {
  loading.value = true
  try {
    // Load only properties for the current user
    const response = await propertyService.search({
      page: 0,
      size: 100
    })
    properties.value = response.content || []
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

const viewProperty = (id: number) => {
  router.push(`/properties/${id}`)
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

const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price)
}

const getActivityIcon = (type: string) => {
  const icons: Record<string, any> = {
    'PROPERTY_CREATED': Plus,
    'PROPERTY_UPDATED': Edit,
    'PROPERTY_VIEWED': Eye
  }
  return icons[type] || Eye
}

const formatRelativeTime = (date: string | Date) => {
  if (!date) return ''
  const d = new Date(date)
  const now = new Date()
  const diffMs = now.getTime() - d.getTime()
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return 'À l\'instant'
  if (diffMins < 60) return `Il y a ${diffMins} min`
  if (diffHours < 24) return `Il y a ${diffHours}h`
  if (diffDays < 7) return `Il y a ${diffDays}j`
  return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
}

onMounted(() => {
  loadProperties()
})
</script>
