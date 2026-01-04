<template>
  <Transition
    enter-active-class="transition ease-out duration-300"
    enter-from-class="opacity-0"
    enter-to-class="opacity-100"
    leave-active-class="transition ease-in duration-200"
    leave-from-class="opacity-100"
    leave-to-class="opacity-0"
  >
    <div
      v-if="isVisible"
      class="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4"
      @click.self="close"
    >
      <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <!-- Header -->
        <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">{{ currentStep.title }}</h2>
            <p class="text-sm text-gray-600 mt-1">Étape {{ currentStepIndex + 1 }} sur {{ steps.length }}</p>
          </div>
          <button
            @click="close"
            class="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X class="h-6 w-6" />
          </button>
        </div>

        <!-- Content -->
        <div class="p-6">
          <div class="mb-6">
            <div class="flex items-center gap-2 mb-4">
              <component :is="currentStep.icon" class="h-8 w-8 text-blue-600" />
              <h3 class="text-xl font-semibold text-gray-900">{{ currentStep.subtitle }}</h3>
            </div>
            <div class="prose max-w-none">
              <p class="text-gray-700 mb-4">{{ currentStep.description }}</p>
              <div v-if="currentStep.content" class="bg-gray-50 rounded-lg p-4">
                <div v-html="currentStep.content" />
              </div>
            </div>
          </div>

          <!-- Progress bar -->
          <div class="mb-6">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm text-gray-600">Progression</span>
              <span class="text-sm font-medium text-gray-900">{{ Math.round(((currentStepIndex + 1) / steps.length) * 100) }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div
                class="bg-blue-600 h-2 rounded-full transition-all duration-300"
                :style="{ width: `${((currentStepIndex + 1) / steps.length) * 100}%` }"
              />
            </div>
          </div>

          <!-- Steps indicators -->
          <div class="flex items-center justify-center gap-2 mb-6">
            <div
              v-for="(step, index) in steps"
              :key="index"
              :class="[
                'w-2 h-2 rounded-full transition-all',
                index === currentStepIndex ? 'bg-blue-600 w-8' : index < currentStepIndex ? 'bg-green-500' : 'bg-gray-300'
              ]"
            />
          </div>
        </div>

        <!-- Footer -->
        <div class="sticky bottom-0 bg-gray-50 border-t border-gray-200 px-6 py-4 flex items-center justify-between">
          <button
            v-if="currentStepIndex > 0"
            @click="previousStep"
            class="px-4 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-200 rounded-md transition-colors"
          >
            Précédent
          </button>
          <div v-else />
          
          <div class="flex items-center gap-3">
            <button
              v-if="!currentStep.skipable"
              @click="skipAll"
              class="px-4 py-2 text-sm text-gray-600 hover:text-gray-800 transition-colors"
            >
              Passer le guide
            </button>
            <button
              v-if="currentStepIndex < steps.length - 1"
              @click="nextStep"
              class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors font-medium"
            >
              Suivant
            </button>
            <button
              v-else
              @click="complete"
              class="px-6 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors font-medium"
            >
              Terminer
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { X, Home, FileText, Image, Eye, BarChart3, MessageSquare } from 'lucide-vue-next'

export interface OnboardingStep {
  title: string
  subtitle: string
  description: string
  content?: string
  icon: any
  skipable?: boolean
  action?: () => void
}

const props = withDefaults(defineProps<{
  steps: OnboardingStep[]
  storageKey?: string
}>(), {
  storageKey: 'onboarding_completed'
})

const emit = defineEmits<{
  completed: []
  skipped: []
}>()

const isVisible = ref(false)
const currentStepIndex = ref(0)

const currentStep = computed(() => props.steps[currentStepIndex.value])

onMounted(() => {
  // Vérifier si le guide a déjà été complété
  const completed = localStorage.getItem(props.storageKey)
  if (!completed) {
    // Attendre un peu avant d'afficher le guide
    setTimeout(() => {
      isVisible.value = true
    }, 1000)
  }
})

function nextStep() {
  if (currentStep.value.action) {
    currentStep.value.action()
  }
  
  if (currentStepIndex.value < props.steps.length - 1) {
    currentStepIndex.value++
  } else {
    complete()
  }
}

function previousStep() {
  if (currentStepIndex.value > 0) {
    currentStepIndex.value--
  }
}

function skipAll() {
  localStorage.setItem(props.storageKey, 'skipped')
  isVisible.value = false
  emit('skipped')
}

function complete() {
  localStorage.setItem(props.storageKey, 'completed')
  isVisible.value = false
  emit('completed')
}

function close() {
  skipAll()
}
</script>

