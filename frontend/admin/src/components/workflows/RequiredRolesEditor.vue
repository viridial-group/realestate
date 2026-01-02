<template>
  <div class="space-y-3">
    <div
      v-for="(_, index) in localRoles"
      :key="index"
      class="flex items-center gap-2"
    >
      <div class="flex-1">
        <Combobox
          v-model="localRoles[index]"
          :search-value="roleSearchValues[index]"
          @update:search-value="(value) => handleSearchValueChange(index, value)"
          @update:model-value="(value) => handleRoleChange(index, value)"
        >
          <ComboboxAnchor as-child>
            <ComboboxInput
              :id="`role-${index}`"
              :placeholder="t('workflows.rolePlaceholder')"
              class="w-full"
            />
          </ComboboxAnchor>
          <ComboboxList class="w-[var(--radix-combobox-trigger-width)]">
            <ComboboxItem
              v-for="role in filteredRoles(index)"
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
      <Button
        type="button"
        variant="ghost"
        size="sm"
        @click="removeRole(index)"
        class="text-destructive hover:text-destructive flex-shrink-0"
      >
        <X class="h-4 w-4" />
      </Button>
    </div>
    <Button
      type="button"
      variant="outline"
      size="sm"
      @click="addRole"
      class="w-full"
    >
      <Plus class="mr-2 h-4 w-4" />
      {{ t('workflows.addRole') }}
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
import { Label } from '@/components/ui/label'
import { Combobox, ComboboxAnchor, ComboboxInput, ComboboxList, ComboboxItem, ComboboxEmpty } from '@/components/ui/combobox'
import { Plus, X } from 'lucide-vue-next'

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

const localRoles = ref<string[]>([])
const roleSearchValues = ref<Record<number, string>>({})

// Liste des rôles prédéfinis
const availableRoles = ['ADMIN', 'MANAGER', 'DIRECTOR', 'AGENT', 'USER', 'FREELANCE', 'AUTO_ENTREPRENEUR', 'PARTICULAR']

// Parse initial value
watch(() => props.modelValue, (value) => {
  if (value) {
    try {
      const parsed = typeof value === 'string' ? JSON.parse(value) : value
      const roles = Array.isArray(parsed) ? parsed.filter(r => r) : []
      localRoles.value = roles
      // Initialize search values
      roles.forEach((_, index) => {
        roleSearchValues.value[index] = ''
      })
    } catch {
      localRoles.value = []
    }
  } else {
    localRoles.value = []
    roleSearchValues.value = {}
  }
}, { immediate: true })

const jsonOutput = computed(() => {
  try {
    return JSON.stringify(localRoles.value.filter(r => r), null, 2)
  } catch {
    return '[]'
  }
})

const filteredRoles = (index: number) => {
  const searchValue = roleSearchValues.value[index] || ''
  const currentRole = localRoles.value[index] || ''
  
  if (!searchValue) {
    return availableRoles.filter(role => 
      !localRoles.value.includes(role) || role === currentRole
    )
  }
  
  const searchLower = searchValue.toLowerCase()
  return availableRoles.filter(role => {
    const roleLabel = getRoleLabel(role).toLowerCase()
    const matches = roleLabel.includes(searchLower)
    const notUsed = !localRoles.value.includes(role) || role === currentRole
    return matches && notUsed
  })
}

const getRoleLabel = (role: string): string => {
  const roleKey = `users.roles.${role.toLowerCase()}`
  const translated = t(roleKey)
  // Si la traduction retourne la clé elle-même, utiliser le rôle en majuscules
  return translated !== roleKey ? translated : role
}

const handleSearchValueChange = (index: number, value: string) => {
  roleSearchValues.value[index] = value
}

const handleRoleChange = (index: number, value: string) => {
  if (value) {
    localRoles.value[index] = value
    roleSearchValues.value[index] = ''
    updateRoles()
  }
}

const updateRoles = () => {
  emit('update:modelValue', JSON.stringify(localRoles.value.filter(r => r)))
}

const addRole = () => {
  const newIndex = localRoles.value.length
  localRoles.value.push('')
  roleSearchValues.value[newIndex] = ''
}

const removeRole = (index: number) => {
  localRoles.value.splice(index, 1)
  // Réindexer les search values
  const newSearchValues: Record<number, string> = {}
  localRoles.value.forEach((_, i) => {
    if (roleSearchValues.value[i] !== undefined) {
      newSearchValues[i] = roleSearchValues.value[i]
    }
  })
  roleSearchValues.value = newSearchValues
  updateRoles()
}
</script>

