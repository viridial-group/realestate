<template>
  <div v-if="roles && roles.length > 0" class="flex flex-wrap gap-2">
    <Badge
      v-for="(role, index) in roles"
      :key="index"
      variant="secondary"
      class="text-sm"
    >
      {{ role }}
    </Badge>
  </div>
  <p v-else class="text-sm text-muted-foreground">{{ t('common.none') }}</p>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Badge } from '@/components/ui/badge'

interface Props {
  rolesJson?: string | null
}

const props = defineProps<Props>()
const { t } = useI18n()

const roles = computed<string[]>(() => {
  if (!props.rolesJson) return []
  try {
    const parsed = typeof props.rolesJson === 'string' ? JSON.parse(props.rolesJson) : props.rolesJson
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
})
</script>

