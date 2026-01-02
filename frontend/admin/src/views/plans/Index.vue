<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">{{ t('plans.title') }}</h1>
        <p class="text-muted-foreground mt-1">{{ t('plans.description') }}</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          {{ t('common.refresh') }}
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          {{ t('plans.createPlan') }}
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #33d484;" @click="filterByActive(true)">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('plans.activePlans') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(51, 212, 132, 0.1);">
            <CheckCircle class="h-5 w-5" style="color: #33d484;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #33d484;">{{ activeCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('plans.plans') }}</p>
        </CardContent>
      </Card>
      
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #fdb022;" @click="filterByActive(false)">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('plans.inactivePlans') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(253, 176, 34, 0.1);">
            <XCircle class="h-5 w-5" style="color: #fdb022;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #fdb022;">{{ inactiveCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('plans.plans') }}</p>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4" style="border-left-color: #04c9ff;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('plans.totalPlans') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(4, 201, 255, 0.1);">
            <CreditCard class="h-5 w-5" style="color: #04c9ff;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #04c9ff;">{{ plans.length }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('plans.plans') }}</p>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('plans.defaultPlans') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <Star class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ defaultCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('plans.plans') }}</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="space-y-2">
            <Label>{{ t('common.search') }}</Label>
            <Input v-model="searchQuery" :placeholder="t('plans.searchPlaceholder')" @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>{{ t('plans.billingPeriod') }}</Label>
            <Select v-model="selectedBillingPeriod" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue :placeholder="t('common.all')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ t('common.all') }}</SelectItem>
                <SelectItem value="MONTHLY">{{ t('plans.monthly') }}</SelectItem>
                <SelectItem value="YEARLY">{{ t('plans.yearly') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>{{ t('common.status') }}</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue :placeholder="t('common.all')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ t('common.all') }}</SelectItem>
                <SelectItem value="active">{{ t('plans.active') }}</SelectItem>
                <SelectItem value="inactive">{{ t('plans.inactive') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des plans -->
    <Card>
      <CardHeader>
        <CardTitle>{{ t('plans.plansList') }}</CardTitle>
      </CardHeader>
      <CardContent>
        <div v-if="loading" class="flex items-center justify-center py-12">
          <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
        </div>

        <div v-else-if="filteredPlans.length === 0" class="text-center py-12">
          <p class="text-muted-foreground">{{ t('plans.noPlansFound') }}</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="plan in paginatedPlans"
            :key="plan.id"
            class="p-6 border rounded-lg hover:shadow-md transition-shadow cursor-pointer"
            @click="viewPlan(plan.id)"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
                  <h3 class="text-xl font-semibold">{{ plan.name }}</h3>
                  <Badge v-if="plan.isDefault" variant="default">
                    {{ t('plans.default') }}
                  </Badge>
                  <Badge :variant="plan.active ? 'default' : 'secondary'">
                    {{ plan.active ? t('plans.active') : t('plans.inactive') }}
                  </Badge>
                </div>
                <p v-if="plan.description" class="text-muted-foreground mb-4">
                  {{ plan.description }}
                </p>
                
                <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                  <div>
                    <p class="text-sm text-muted-foreground">{{ t('plans.price') }}</p>
                    <p class="font-semibold text-lg">{{ formatPrice(plan.price, plan.currency) }}</p>
                    <p class="text-xs text-muted-foreground">{{ t(`plans.${plan.billingPeriod.toLowerCase()}`) }}</p>
                  </div>
                  <div v-if="plan.maxProperties">
                    <p class="text-sm text-muted-foreground">{{ t('plans.maxProperties') }}</p>
                    <p class="font-semibold">{{ plan.maxProperties === -1 ? t('plans.unlimited') : plan.maxProperties }}</p>
                  </div>
                  <div v-if="plan.maxUsers">
                    <p class="text-sm text-muted-foreground">{{ t('plans.maxUsers') }}</p>
                    <p class="font-semibold">{{ plan.maxUsers === -1 ? t('plans.unlimited') : plan.maxUsers }}</p>
                  </div>
                  <div v-if="plan.maxStorageGb">
                    <p class="text-sm text-muted-foreground">{{ t('plans.maxStorage') }}</p>
                    <p class="font-semibold">{{ plan.maxStorageGb === -1 ? t('plans.unlimited') : `${plan.maxStorageGb} GB` }}</p>
                  </div>
                </div>

                <!-- Fonctionnalités -->
                <div v-if="parsedFeatures(plan).length > 0" class="mt-4">
                  <p class="text-sm text-muted-foreground mb-2">{{ t('plans.featuresLabel') }}:</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge
                      v-for="(feature, index) in parsedFeatures(plan).slice(0, 5)"
                      :key="index"
                      variant="outline"
                    >
                      {{ getFeatureLabel(feature) }}
                    </Badge>
                    <Badge v-if="parsedFeatures(plan).length > 5" variant="outline">
                      +{{ parsedFeatures(plan).length - 5 }} {{ t('plans.more') }}
                    </Badge>
                  </div>
                </div>
              </div>

              <div class="flex gap-2 ml-4">
                <Button variant="ghost" size="sm" @click.stop="editPlan(plan.id)">
                  <Edit class="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  @click.stop="deletePlan(plan.id)"
                  class="text-destructive hover:text-destructive"
                >
                  <Trash2 class="h-4 w-4" />
                </Button>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="filteredPlans.length > pageSize" class="flex items-center justify-between mt-6">
          <p class="text-sm text-muted-foreground">
            {{ t('common.showing') }} {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, filteredPlans.length) }} {{ t('common.of') }} {{ filteredPlans.length }}
          </p>
          <div class="flex gap-2">
            <Button variant="outline" size="sm" @click="previousPage" :disabled="currentPage === 1">
              {{ t('common.previous') }}
            </Button>
            <Button variant="outline" size="sm" @click="nextPage" :disabled="currentPage === totalPages">
              {{ t('common.next') }}
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Dialog de création/édition -->
    <PlanDialog
      :open="dialogOpen"
      :plan="selectedPlan"
      @update:open="dialogOpen = $event"
      @saved="handleSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useToast } from '@/components/ui/toast'
import { planService, type Plan } from '@viridial/shared'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import {
  RefreshCw,
  Plus,
  CheckCircle,
  XCircle,
  CreditCard,
  Star,
  Edit,
  Trash2,
  Loader2
} from 'lucide-vue-next'
import PlanDialog from '@/components/plans/PlanDialog.vue'

const { t } = useI18n()
const router = useRouter()
const { toast } = useToast()

const loading = ref(false)
const searchQuery = ref('')
const selectedBillingPeriod = ref<string | null>(null)
const selectedStatus = ref<string | null>(null)
const currentPage = ref(1)
const pageSize = 10

const plans = ref<Plan[]>([])
const selectedPlan = ref<Plan | null>(null)
const dialogOpen = ref(false)

const activeCount = computed(() => plans.value.filter(p => p.active).length)
const inactiveCount = computed(() => plans.value.filter(p => !p.active).length)
const defaultCount = computed(() => plans.value.filter(p => p.isDefault).length)

const filteredPlans = computed(() => {
  let filtered = plans.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      plan =>
        plan.name.toLowerCase().includes(query) ||
        plan.description?.toLowerCase().includes(query)
    )
  }

  if (selectedBillingPeriod.value && selectedBillingPeriod.value !== 'all') {
    filtered = filtered.filter(plan => plan.billingPeriod === selectedBillingPeriod.value)
  }

  if (selectedStatus.value && selectedStatus.value !== 'all') {
    if (selectedStatus.value === 'active') {
      filtered = filtered.filter(plan => plan.active)
    } else if (selectedStatus.value === 'inactive') {
      filtered = filtered.filter(plan => !plan.active)
    }
  }

  return filtered
})

const totalPages = computed(() => Math.ceil(filteredPlans.value.length / pageSize))
const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return filteredPlans.value.slice(start, end)
})

const parsedFeatures = (plan: Plan): string[] => {
  if (!plan.features) return []
  try {
    if (typeof plan.features === 'string') {
      return JSON.parse(plan.features)
    }
    return Array.isArray(plan.features) ? plan.features : []
  } catch {
    return []
  }
}

const getFeatureLabel = (feature: string): string => {
  const featureKey = `plans.features.${feature.toLowerCase()}`
  const translated = t(featureKey)
  return translated !== featureKey ? translated : feature
}

const formatPrice = (price: number, currency: string = 'EUR'): string => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(price)
}

const loadPlans = async () => {
  loading.value = true
  try {
    plans.value = await planService.getAll()
  } catch (error: any) {
    console.error('Error loading plans:', error)
    toast({
      title: t('common.error'),
      description: error.message || t('plans.errors.loadFailed'),
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadPlans()
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const filterByActive = (active: boolean) => {
  selectedStatus.value = active ? 'active' : 'inactive'
  currentPage.value = 1
}

const previousPage = () => {
  if (currentPage.value > 1) currentPage.value--
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++
}

const openCreateDialog = () => {
  selectedPlan.value = null
  dialogOpen.value = true
}

const editPlan = (id: number) => {
  selectedPlan.value = plans.value.find(p => p.id === id) || null
  dialogOpen.value = true
}

const viewPlan = (id: number) => {
  router.push(`/plans/${id}`)
}

const deletePlan = async (id: number) => {
  if (!confirm(t('plans.confirmDelete'))) return

  loading.value = true
  try {
    await planService.delete(id)
    toast({
      title: t('common.success'),
      description: t('plans.deleted')
    })
    await loadPlans()
  } catch (error: any) {
    console.error('Error deleting plan:', error)
    toast({
      title: t('common.error'),
      description: error.message || t('plans.errors.deleteFailed'),
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const handleSaved = () => {
  dialogOpen.value = false
  loadPlans()
}

onMounted(() => {
  loadPlans()
})
</script>

