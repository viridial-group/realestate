<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent class="max-w-2xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{{ isEdit ? t('workflows.editWorkflow') : t('workflows.createWorkflow') }}</DialogTitle>
        <DialogDescription>
          {{ isEdit ? t('workflows.editWorkflowDescription') : t('workflows.createWorkflowDescription') }}
        </DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label for="name">{{ t('workflows.name') }} *</Label>
          <Input
            id="name"
            v-model="form.name"
            :placeholder="t('workflows.namePlaceholder')"
            required
          />
        </div>

        <div class="space-y-2">
          <Label for="description">{{ t('workflows.descriptionLabel') }}</Label>
          <Textarea
            id="description"
            v-model="form.description"
            :placeholder="t('workflows.descriptionPlaceholder')"
            rows="3"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="action">{{ t('workflows.action') }} *</Label>
            <Select v-model="form.action" required>
              <SelectTrigger>
                <SelectValue :placeholder="t('workflows.selectAction')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="PROPERTY_CREATE">{{ t('workflows.actions.propertyCreate') }}</SelectItem>
                <SelectItem value="PROPERTY_UPDATE">{{ t('workflows.actions.propertyUpdate') }}</SelectItem>
                <SelectItem value="PROPERTY_DELETE">{{ t('workflows.actions.propertyDelete') }}</SelectItem>
                <SelectItem value="DOCUMENT_UPLOAD">{{ t('workflows.actions.documentUpload') }}</SelectItem>
                <SelectItem value="USER_CREATE">{{ t('workflows.actions.userCreate') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="targetType">{{ t('workflows.targetType') }}</Label>
            <Input
              id="targetType"
              v-model="form.targetType"
              :placeholder="t('workflows.targetTypePlaceholder')"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="active">{{ t('workflows.status') }}</Label>
            <Select v-model="activeValue">
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="true">{{ t('workflows.active') }}</SelectItem>
                <SelectItem value="false">{{ t('workflows.inactive') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="isDefault">{{ t('workflows.isDefault') }}</Label>
            <div class="flex items-center space-x-2 pt-2">
              <Checkbox
                id="isDefault"
                :checked="form.isDefault"
                @update:checked="handleIsDefaultChange"
              />
              <Label for="isDefault" class="text-sm font-normal cursor-pointer">
                {{ t('workflows.setAsDefault') }}
              </Label>
            </div>
          </div>
        </div>

        <div class="space-y-2">
          <Label>{{ t('workflows.steps') }}</Label>
          <WorkflowStepsEditor
            v-model="form.steps"
            :show-json="false"
          />
        </div>

        <div class="space-y-2">
          <Label>{{ t('workflows.requiredRoles') }}</Label>
          <RequiredRolesEditor
            v-model="form.requiredRoles"
            :show-json="false"
          />
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
import { workflowService, type Workflow, type WorkflowCreate, type WorkflowUpdate, useAuthStore } from '@viridial/shared'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Checkbox } from '@/components/ui/checkbox'
import { useToast } from '@/components/ui/toast'
import WorkflowStepsEditor from '@/components/workflows/WorkflowStepsEditor.vue'
import RequiredRolesEditor from '@/components/workflows/RequiredRolesEditor.vue'

interface Props {
  open: boolean
  workflow?: Workflow | null
}

const props = withDefaults(defineProps<Props>(), {
  workflow: null
})

const emit = defineEmits<{
  'update:open': [value: boolean]
  saved: []
}>()

const { t } = useI18n()
const { toast } = useToast()
const authStore = useAuthStore()

const submitting = ref(false)

const isEdit = computed(() => !!props.workflow)

const form = ref<{
  name: string
  description: string
  action: string
  targetType: string
  steps: string
  requiredRoles: string
  active: boolean
  isDefault: boolean
}>({
  name: '',
  description: '',
  action: '',
  targetType: '',
  steps: '',
  requiredRoles: '',
  active: true,
  isDefault: false
})

const activeValue = computed({
  get: () => form.value.active ? 'true' : 'false',
  set: (value: string) => {
    form.value.active = value === 'true'
  }
})

watch(() => props.workflow, (workflow) => {
  if (workflow) {
    form.value = {
      name: workflow.name || '',
      description: workflow.description || '',
      action: workflow.action || '',
      targetType: workflow.targetType || '',
      steps: workflow.steps || '',
      requiredRoles: workflow.requiredRoles || '',
      active: workflow.active ?? true,
      isDefault: workflow.isDefault ?? false
    }
  } else {
    form.value = {
      name: '',
      description: '',
      action: '',
      targetType: '',
      steps: '',
      requiredRoles: '',
      active: true,
      isDefault: false
    }
  }
}, { immediate: true })

const handleIsDefaultChange = (checked: boolean) => {
  form.value.isDefault = checked
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    const currentOrgId = authStore.user?.organizationId
    if (!currentOrgId) {
      throw new Error('No organization ID')
    }

    if (isEdit.value && props.workflow) {
      const update: WorkflowUpdate = {
        name: form.value.name,
        description: form.value.description || undefined,
        action: form.value.action,
        targetType: form.value.targetType || undefined,
        steps: form.value.steps || undefined,
        requiredRoles: form.value.requiredRoles || undefined,
        active: form.value.active,
        isDefault: form.value.isDefault
      }
      await workflowService.update(props.workflow.id, update)
      toast({
        title: t('common.success'),
        description: t('workflows.updated')
      })
    } else {
      const create: WorkflowCreate = {
        name: form.value.name,
        description: form.value.description || undefined,
        action: form.value.action,
        organizationId: currentOrgId,
        targetType: form.value.targetType || undefined,
        steps: form.value.steps || undefined,
        requiredRoles: form.value.requiredRoles || undefined,
        active: form.value.active,
        isDefault: form.value.isDefault
      }
      await workflowService.create(create)
      toast({
        title: t('common.success'),
        description: t('workflows.created')
      })
    }

    emit('saved')
    emit('update:open', false)
  } catch (error: any) {
    console.error('Error saving workflow:', error)
    toast({
      title: t('common.error'),
      description: error.message || t('workflows.errors.saveFailed'),
      variant: 'destructive'
    })
  } finally {
    submitting.value = false
  }
}
</script>

