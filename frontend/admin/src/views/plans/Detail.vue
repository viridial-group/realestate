<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="sm" @click="$router.push('/plans')">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">{{ plan?.name || t('plans.loading') }}</h1>
          <p class="text-muted-foreground mt-1" v-if="plan?.description">
            {{ plan.description }}
          </p>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          {{ t('common.refresh') }}
        </Button>
        <Button variant="outline" @click="editPlan" :disabled="!plan">
          <Edit class="mr-2 h-4 w-4" />
          {{ t('common.edit') }}
        </Button>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
    </div>

    <div v-else-if="!plan" class="text-center py-12">
      <p class="text-muted-foreground">{{ t('plans.notFound') }}</p>
    </div>

    <div v-else class="space-y-6">
      <!-- Informations principales -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 space-y-6">
          <!-- Informations du plan -->
          <Card>
            <CardHeader>
              <CardTitle>{{ t('plans.planInformation') }}</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">{{ t('plans.name') }}</p>
                  <p class="font-semibold text-lg">{{ plan.name }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">{{ t('common.status') }}</p>
                  <div class="flex items-center gap-2">
                    <Badge :variant="plan.active ? 'default' : 'secondary'">
                      {{ plan.active ? t('plans.active') : t('plans.inactive') }}
                    </Badge>
                    <Badge v-if="plan.isDefault" variant="default">
                      {{ t('plans.default') }}
                    </Badge>
                  </div>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">{{ t('plans.price') }}</p>
                  <p class="font-semibold text-lg">{{ formatPrice(plan.price, plan.currency) }}</p>
                  <p class="text-xs text-muted-foreground">{{ t(`plans.${plan.billingPeriod.toLowerCase()}`) }}</p>
                </div>
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">{{ t('plans.billingPeriod') }}</p>
                  <p class="font-semibold">{{ t(`plans.${plan.billingPeriod.toLowerCase()}`) }}</p>
                </div>
                <div class="space-y-1" v-if="plan.createdAt">
                  <p class="text-sm text-muted-foreground">{{ t('common.createdAt') }}</p>
                  <p class="font-semibold">{{ formatDate(plan.createdAt) }}</p>
                </div>
                <div class="space-y-1" v-if="plan.updatedAt">
                  <p class="text-sm text-muted-foreground">{{ t('common.updatedAt') }}</p>
                  <p class="font-semibold">{{ formatDate(plan.updatedAt) }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Quotas -->
          <Card>
            <CardHeader>
              <CardTitle>{{ t('plans.quotas') }}</CardTitle>
              <CardDescription>{{ t('plans.quotasDescription') }}</CardDescription>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div class="p-4 border rounded-lg">
                  <p class="text-sm text-muted-foreground mb-2">{{ t('plans.maxProperties') }}</p>
                  <p class="text-2xl font-bold">
                    {{ plan.maxProperties === undefined || plan.maxProperties === null ? t('plans.unlimited') : plan.maxProperties === -1 ? t('plans.unlimited') : plan.maxProperties }}
                  </p>
                </div>
                <div class="p-4 border rounded-lg">
                  <p class="text-sm text-muted-foreground mb-2">{{ t('plans.maxUsers') }}</p>
                  <p class="text-2xl font-bold">
                    {{ plan.maxUsers === undefined || plan.maxUsers === null ? t('plans.unlimited') : plan.maxUsers === -1 ? t('plans.unlimited') : plan.maxUsers }}
                  </p>
                </div>
                <div class="p-4 border rounded-lg">
                  <p class="text-sm text-muted-foreground mb-2">{{ t('plans.maxStorage') }}</p>
                  <p class="text-2xl font-bold">
                    {{ plan.maxStorageGb === undefined || plan.maxStorageGb === null ? t('plans.unlimited') : plan.maxStorageGb === -1 ? t('plans.unlimited') : `${plan.maxStorageGb} GB` }}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Fonctionnalités -->
          <Card>
            <CardHeader>
              <CardTitle>{{ t('plans.featuresLabel') }}</CardTitle>
              <CardDescription>{{ t('plans.featuresDescription') }}</CardDescription>
            </CardHeader>
            <CardContent>
              <div v-if="parsedFeatures.length > 0" class="space-y-2">
                <div
                  v-for="(feature, index) in parsedFeatures"
                  :key="index"
                  class="flex items-center gap-2 p-3 border rounded-lg"
                >
                  <CheckCircle class="h-5 w-5 text-green-500 flex-shrink-0" />
                  <span>{{ getFeatureLabel(feature) }}</span>
                </div>
              </div>
              <div v-else class="text-center py-8 text-muted-foreground">
                {{ t('plans.noFeatures') }}
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Colonne latérale -->
        <div class="space-y-6">
          <!-- Actions rapides -->
          <Card>
            <CardHeader>
              <CardTitle>{{ t('common.actions') }}</CardTitle>
            </CardHeader>
            <CardContent class="space-y-2">
              <Button variant="outline" class="w-full justify-start" @click="editPlan">
                <Edit class="mr-2 h-4 w-4" />
                {{ t('common.edit') }}
              </Button>
              <Button
                variant="outline"
                class="w-full justify-start text-destructive hover:text-destructive"
                @click="deletePlan"
              >
                <Trash2 class="mr-2 h-4 w-4" />
                {{ t('common.delete') }}
              </Button>
            </CardContent>
          </Card>

          <!-- Statistiques -->
          <Card>
            <CardHeader>
              <CardTitle>{{ t('plans.statistics') }}</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="space-y-4">
                <div>
                  <p class="text-sm text-muted-foreground mb-1">{{ t('plans.totalSubscriptions') }}</p>
                  <p class="text-2xl font-bold">{{ subscriptionCount }}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>

    <!-- Dialog d'édition -->
    <PlanDialog
      :open="editDialogOpen"
      :plan="plan"
      @update:open="editDialogOpen = $event"
      @saved="handlePlanSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useToast } from '@/components/ui/toast'
import { planService, billingService } from '@viridial/shared'
import type { Plan } from '@viridial/shared'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  ArrowLeft,
  RefreshCw,
  Edit,
  Trash2,
  CheckCircle
} from 'lucide-vue-next'
import PlanDialog from '@/components/plans/PlanDialog.vue'

const { t } = useI18n()
const { toast } = useToast()
const route = useRoute()
const router = useRouter()

const plan = ref<Plan | null>(null)
const loading = ref(false)
const editDialogOpen = ref(false)
const subscriptionCount = ref(0)

const planId = computed(() => Number(route.params.id))

const parsedFeatures = computed(() => {
  if (!plan.value?.features) return []
  try {
    if (typeof plan.value.features === 'string') {
      return JSON.parse(plan.value.features)
    }
    return Array.isArray(plan.value.features) ? plan.value.features : []
  } catch {
    return []
  }
})

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

const formatDate = (date: string): string => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const loadPlan = async () => {
  loading.value = true
  try {
    plan.value = await planService.getById(planId.value)
    await loadSubscriptionCount()
  } catch (error: any) {
    console.error('Error loading plan:', error)
    toast({
      title: t('common.error'),
      description: error.message || t('plans.errors.loadFailed'),
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadSubscriptionCount = async () => {
  try {
    const subscriptions = await billingService.getAllSubscriptions()
    subscriptionCount.value = subscriptions.filter(s => s.planId === planId.value).length
  } catch (error) {
    console.warn('Could not load subscription count:', error)
  }
}

const refreshData = async () => {
  await loadPlan()
}

const editPlan = () => {
  editDialogOpen.value = true
}

const handlePlanSaved = () => {
  editDialogOpen.value = false
  loadPlan()
}

const deletePlan = async () => {
  if (!confirm(t('plans.confirmDelete'))) return

  loading.value = true
  try {
    await planService.delete(planId.value)
    toast({
      title: t('common.success'),
      description: t('plans.deleted')
    })
    router.push('/plans')
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

onMounted(() => {
  loadPlan()
})
</script>

