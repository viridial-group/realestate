<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent class="max-w-3xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{{ isEdit ? t('plans.editPlan') : t('plans.createPlan') }}</DialogTitle>
        <DialogDescription>
          {{ isEdit ? t('plans.editPlanDescription') : t('plans.createPlanDescription') }}
        </DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <!-- Informations de base -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="name">{{ t('plans.name') }} *</Label>
            <Input
              id="name"
              v-model="form.name"
              :placeholder="t('plans.namePlaceholder')"
              required
            />
          </div>

          <div class="space-y-2">
            <Label for="price">{{ t('plans.price') }} *</Label>
            <Input
              id="price"
              v-model.number="form.price"
              type="number"
              step="0.01"
              min="0"
              :placeholder="t('plans.pricePlaceholder')"
              required
            />
          </div>
        </div>

          <div class="space-y-2">
            <Label for="description">{{ t('plans.descriptionLabel') }}</Label>
          <Textarea
            id="description"
            v-model="form.description"
            :placeholder="t('plans.descriptionPlaceholder')"
            rows="3"
          />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="space-y-2">
            <Label for="currency">{{ t('plans.currency') }}</Label>
            <Select v-model="form.currency">
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="EUR">EUR (€)</SelectItem>
                <SelectItem value="USD">USD ($)</SelectItem>
                <SelectItem value="GBP">GBP (£)</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="billingPeriod">{{ t('plans.billingPeriod') }}</Label>
            <Select v-model="form.billingPeriod">
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="MONTHLY">{{ t('plans.monthly') }}</SelectItem>
                <SelectItem value="YEARLY">{{ t('plans.yearly') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="active">{{ t('common.status') }}</Label>
            <Select v-model="activeValue">
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="true">{{ t('plans.active') }}</SelectItem>
                <SelectItem value="false">{{ t('plans.inactive') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <!-- Quotas -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold">{{ t('plans.quotas') }}</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="space-y-2">
              <Label for="maxProperties">{{ t('plans.maxProperties') }}</Label>
              <Input
                id="maxProperties"
                v-model.number="form.maxProperties"
                type="number"
                min="-1"
                :placeholder="t('plans.unlimitedPlaceholder')"
              />
              <p class="text-xs text-muted-foreground">{{ t('plans.maxPropertiesHint') }}</p>
            </div>

            <div class="space-y-2">
              <Label for="maxUsers">{{ t('plans.maxUsers') }}</Label>
              <Input
                id="maxUsers"
                v-model.number="form.maxUsers"
                type="number"
                min="-1"
                :placeholder="t('plans.unlimitedPlaceholder')"
              />
              <p class="text-xs text-muted-foreground">{{ t('plans.maxUsersHint') }}</p>
            </div>

            <div class="space-y-2">
              <Label for="maxStorageGb">{{ t('plans.maxStorage') }}</Label>
              <Input
                id="maxStorageGb"
                v-model.number="form.maxStorageGb"
                type="number"
                min="-1"
                :placeholder="t('plans.unlimitedPlaceholder')"
              />
              <p class="text-xs text-muted-foreground">{{ t('plans.maxStorageHint') }}</p>
            </div>
          </div>
        </div>

        <!-- Fonctionnalités -->
        <div class="space-y-2">
          <Label>{{ t('plans.features') }}</Label>
          <PlanFeaturesEditor
            v-model="form.features"
            :show-json="false"
          />
        </div>

        <!-- Plan par défaut -->
        <div class="flex items-center space-x-2">
          <Checkbox
            id="isDefault"
            :checked="form.isDefault"
            @update:checked="handleIsDefaultChange"
          />
          <Label for="isDefault" class="text-sm font-normal cursor-pointer">
            {{ t('plans.setAsDefault') }}
          </Label>
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" @click="$emit('update:open', false)">
            {{ t('common.cancel') }}
          </Button>
          <Button type="submit" :disabled="submitting">
            <span v-if="submitting" class="mr-2">
              <div class="inline-block animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
            </span>
            {{ isEdit ? t('common.update') : t('common.create') }}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { planService } from '@viridial/shared'
import type { Plan, PlanCreate, PlanUpdate } from '@viridial/shared'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Checkbox } from '@/components/ui/checkbox'
import { useToast } from '@/components/ui/toast'
import PlanFeaturesEditor from '@/components/plans/PlanFeaturesEditor.vue'

interface Props {
  open: boolean
  plan?: Plan | null
}

const props = withDefaults(defineProps<Props>(), {
  plan: null
})

const emit = defineEmits<{
  'update:open': [value: boolean]
  saved: []
}>()

const { t } = useI18n()
const { toast } = useToast()

const submitting = ref(false)
const isEdit = computed(() => !!props.plan)

const form = ref<PlanCreate | PlanUpdate>({
  name: '',
  description: '',
  price: 0,
  currency: 'EUR',
  billingPeriod: 'MONTHLY',
  maxProperties: undefined,
  maxUsers: undefined,
  maxStorageGb: undefined,
  features: [],
  active: true,
  isDefault: false
})

const activeValue = computed({
  get: () => String(form.value.active ?? true),
  set: (value: string) => {
    form.value.active = value === 'true'
  }
})

watch(() => props.plan, (plan) => {
  if (plan) {
    form.value = {
      name: plan.name || '',
      description: plan.description || '',
      price: plan.price || 0,
      currency: plan.currency || 'EUR',
      billingPeriod: plan.billingPeriod || 'MONTHLY',
      maxProperties: plan.maxProperties,
      maxUsers: plan.maxUsers,
      maxStorageGb: plan.maxStorageGb,
      features: parseFeatures(plan.features),
      active: plan.active !== undefined ? plan.active : true,
      isDefault: plan.isDefault || false
    }
  } else {
    form.value = {
      name: '',
      description: '',
      price: 0,
      currency: 'EUR',
      billingPeriod: 'MONTHLY',
      maxProperties: undefined,
      maxUsers: undefined,
      maxStorageGb: undefined,
      features: [],
      active: true,
      isDefault: false
    }
  }
}, { immediate: true })

const parseFeatures = (features?: string[] | string): string[] => {
  if (!features) return []
  if (Array.isArray(features)) return features
  try {
    return JSON.parse(features)
  } catch {
    return []
  }
}

const handleIsDefaultChange = (checked: boolean) => {
  form.value.isDefault = checked
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (isEdit.value && props.plan) {
      await planService.update(props.plan.id, form.value as PlanUpdate)
      toast({
        title: t('common.success'),
        description: t('plans.updated')
      })
    } else {
      await planService.create(form.value as PlanCreate)
      toast({
        title: t('common.success'),
        description: t('plans.created')
      })
    }
    emit('saved')
    emit('update:open', false)
  } catch (error: any) {
    console.error('Error saving plan:', error)
    toast({
      title: t('common.error'),
      description: error.message || t('plans.errors.saveFailed'),
      variant: 'destructive'
    })
  } finally {
    submitting.value = false
  }
}
</script>

