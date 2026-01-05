<template>
  <div class="space-y-3">
    <div
      v-for="(step, index) in localSteps"
      :key="index"
      class="p-4 border rounded-lg bg-card space-y-3"
    >
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 rounded-full bg-primary text-primary-foreground flex items-center justify-center font-semibold text-sm">
            {{ step.step || index + 1 }}
          </div>
          <span class="font-medium">{{ t('workflows.step') }} {{ step.step || index + 1 }}</span>
        </div>
        <Button
          type="button"
          variant="ghost"
          size="sm"
          @click="removeStep(index)"
          class="text-destructive hover:text-destructive"
        >
          <X class="h-4 w-4" />
        </Button>
      </div>
      <div class="grid grid-cols-1 gap-3">
        <div>
          <Label :for="`step-name-${index}`" class="text-xs">{{ t('workflows.stepName') }}</Label>
          <Input
            :id="`step-name-${index}`"
            v-model="step.name"
            :placeholder="t('workflows.stepNamePlaceholder')"
            @input="updateSteps"
          />
        </div>
        <div>
          <Label :for="`step-role-${index}`" class="text-xs">{{ t('workflows.stepRole') }}</Label>
          <Combobox
            v-model="step.role"
            :search-value="stepSearchValues[index]"
            @update:search-value="(value: string) => handleStepRoleSearchChange(index, value)"
            @update:model-value="(value: any) => handleStepRoleChange(index, typeof value === 'string' ? value : String(value || ''))"
          >
            <ComboboxAnchor as-child>
              <ComboboxInput
                :id="`step-role-${index}`"
                :placeholder="t('workflows.stepRolePlaceholder')"
                class="w-full"
              />
            </ComboboxAnchor>
            <ComboboxList class="w-[var(--radix-combobox-trigger-width)]">
              <ComboboxItem
                v-for="role in filteredStepRoles(index)"
                :key="role"
                :value="role"
              >
                {{ getRoleLabel(role) }}
              </ComboboxItem>
              <ComboboxEmpty>
                {{ t('workflows.noRoleFound') }}
              </ComboboxEmpty>
            </ComboboxList>
          </Combobox>
        </div>
        <div>
          <Label :for="`step-description-${index}`" class="text-xs">{{ t('workflows.descriptionLabel') }}</Label>
          <Input
            :id="`step-description-${index}`"
            v-model="step.description"
            :placeholder="t('workflows.stepDescriptionPlaceholder')"
            @input="updateSteps"
          />
        </div>
      </div>
    </div>
    <Button
      type="button"
      variant="outline"
      size="sm"
      @click="addStep"
      class="w-full"
    >
      <Plus class="mr-2 h-4 w-4" />
      {{ t('workflows.addStep') }}
    </Button>
    <div v-if="showJson" class="mt-3">
      <Label class="text-xs text-muted-foreground">{{ t('workflows.jsonPreview') }}</Label>
      <pre class="text-xs bg-muted p-2 rounded mt-1 overflow-auto">{{ jsonOutput }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Combobox, ComboboxAnchor, ComboboxInput, ComboboxList, ComboboxItem, ComboboxEmpty } from '@/components/ui/combobox'
import { Plus, X } from 'lucide-vue-next'

interface Step {
  step?: number
  name?: string
  role?: string
  description?: string
}

interface Props {
  modelValue?: string
  showJson?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showJson: false
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const { t } = useI18n()

const localSteps = ref<Step[]>([])
const stepSearchValues = ref<Record<number, string>>({})

// Liste des rôles prédéfinis
const availableRoles = ['ADMIN', 'MANAGER', 'DIRECTOR', 'AGENT', 'USER', 'FREELANCE', 'AUTO_ENTREPRENEUR', 'PARTICULAR']

// Parse initial value
watch(() => props.modelValue, (value) => {
  if (value) {
    try {
      const parsed = typeof value === 'string' ? JSON.parse(value) : value
      const steps = Array.isArray(parsed) ? parsed : []
      localSteps.value = steps
      // Initialize search values
      steps.forEach((_, index) => {
        stepSearchValues.value[index] = ''
      })
    } catch {
      localSteps.value = []
    }
  } else {
    localSteps.value = []
    stepSearchValues.value = {}
  }
}, { immediate: true })

const jsonOutput = computed(() => {
  try {
    return JSON.stringify(localSteps.value, null, 2)
  } catch {
    return '[]'
  }
})

const getRoleLabel = (role: string): string => {
  const roleKey = `users.roles.${role.toLowerCase()}`
  const translated = t(roleKey)
  // Si la traduction retourne la clé elle-même, utiliser le rôle en majuscules
  return translated !== roleKey ? translated : role
}

const filteredStepRoles = (index: number) => {
  const searchValue = stepSearchValues.value[index] || ''
  
  if (!searchValue) {
    return availableRoles
  }
  
  const searchLower = searchValue.toLowerCase()
  return availableRoles.filter(role => {
    const roleLabel = getRoleLabel(role).toLowerCase()
    return roleLabel.includes(searchLower)
  })
}

const handleStepRoleSearchChange = (index: number, value: string) => {
  stepSearchValues.value[index] = value
}

const handleStepRoleChange = (index: number, value: string) => {
  if (localSteps.value[index]) {
    localSteps.value[index].role = value || ''
    stepSearchValues.value[index] = ''
    updateSteps()
  }
}

const updateSteps = () => {
  // Update step numbers
  localSteps.value.forEach((step, index) => {
    step.step = index + 1
  })
  emit('update:modelValue', JSON.stringify(localSteps.value))
}

const addStep = () => {
  const newIndex = localSteps.value.length
  localSteps.value.push({
    step: newIndex + 1,
    name: '',
    role: '',
    description: ''
  })
  stepSearchValues.value[newIndex] = ''
  updateSteps()
}

const removeStep = (index: number) => {
  localSteps.value.splice(index, 1)
  // Réindexer les search values
  const newSearchValues: Record<number, string> = {}
  localSteps.value.forEach((_, i) => {
    if (stepSearchValues.value[i] !== undefined) {
      newSearchValues[i] = stepSearchValues.value[i]
    }
  })
  stepSearchValues.value = newSearchValues
  updateSteps()
}
</script>

