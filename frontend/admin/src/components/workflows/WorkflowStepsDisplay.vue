<template>
  <div v-if="steps && steps.length > 0" class="space-y-3">
    <div
      v-for="(step, index) in steps"
      :key="index"
      class="flex items-start gap-4 p-4 border rounded-lg bg-card"
    >
      <div class="flex-shrink-0 w-8 h-8 rounded-full bg-primary text-primary-foreground flex items-center justify-center font-semibold">
        {{ step.step || index + 1 }}
      </div>
      <div class="flex-1 min-w-0">
        <div class="flex items-center gap-2 mb-1">
          <h4 class="font-medium">{{ step.name || t('workflows.step') }} {{ step.step || index + 1 }}</h4>
          <Badge v-if="step.role" variant="outline">
            {{ step.role }}
          </Badge>
        </div>
        <p v-if="step.description" class="text-sm text-muted-foreground">
          {{ step.description }}
        </p>
      </div>
    </div>
  </div>
  <p v-else class="text-sm text-muted-foreground">{{ t('common.none') }}</p>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Badge } from '@/components/ui/badge'

interface Step {
  step?: number
  name?: string
  role?: string
  description?: string
}

interface Props {
  stepsJson?: string | null
}

const props = defineProps<Props>()
const { t } = useI18n()

const steps = computed<Step[]>(() => {
  if (!props.stepsJson) return []
  try {
    const parsed = typeof props.stepsJson === 'string' ? JSON.parse(props.stepsJson) : props.stepsJson
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
})
</script>

