<template>
  <div class="space-y-3">
    <div
      v-for="(_feature, index) in localFeatures"
      :key="index"
      class="flex items-center gap-2"
    >
      <Input
        :id="`feature-${index}`"
        v-model="localFeatures[index]"
        :placeholder="t('plans.featurePlaceholder')"
        class="flex-1"
        @input="updateFeatures"
      />
      <Button
        type="button"
        variant="ghost"
        size="sm"
        @click="removeFeature(index)"
        class="text-destructive hover:text-destructive flex-shrink-0"
      >
        <X class="h-4 w-4" />
      </Button>
    </div>
    <Button
      type="button"
      variant="outline"
      size="sm"
      @click="addFeature"
      class="w-full"
    >
      <Plus class="mr-2 h-4 w-4" />
      {{ t('plans.addFeature') }}
    </Button>
    <div v-if="showJson" class="mt-3">
      <Label class="text-xs text-muted-foreground">{{ t('plans.jsonPreview') }}</Label>
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
import { Plus, X } from 'lucide-vue-next'

interface Props {
  modelValue?: string[] | string
  showJson?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showJson: false
})

const emit = defineEmits<{
  'update:modelValue': [value: string[]]
}>()

const { t } = useI18n()

const localFeatures = ref<string[]>([])

const parseModelValue = (value: string[] | string | undefined): string[] => {
  if (!value) return []
  if (Array.isArray(value)) return value.filter(f => f)
  try {
    const parsed = JSON.parse(value)
    return Array.isArray(parsed) ? parsed.filter((f: string) => f) : []
  } catch {
    return []
  }
}

watch(() => props.modelValue, (newValue) => {
  localFeatures.value = parseModelValue(newValue)
}, { immediate: true })

const jsonOutput = computed(() => {
  try {
    return JSON.stringify(localFeatures.value.filter(f => f), null, 2)
  } catch {
    return '[]'
  }
})

const updateFeatures = () => {
  emit('update:modelValue', localFeatures.value.filter(f => f))
}

const addFeature = () => {
  localFeatures.value.push('')
  updateFeatures()
}

const removeFeature = (index: number) => {
  localFeatures.value.splice(index, 1)
  updateFeatures()
}
</script>

