<template>
  <div
    class="bg-white dark:bg-gray-800 rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 p-8 border-2 flex flex-col"
    :class="selected ? 'border-blue-600 ring-2 ring-blue-300' : 'border-gray-200 dark:border-gray-700'"
  >
    <!-- Badge populaire -->
    <div v-if="plan.isDefault" class="mb-4">
      <span class="inline-block px-3 py-1 bg-blue-600 text-white text-xs font-semibold rounded-full">
        Le plus populaire
      </span>
    </div>

    <!-- Nom et description -->
    <div class="mb-6">
      <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">
        {{ plan.name }}
      </h3>
      <p v-if="plan.description" class="text-gray-600 dark:text-gray-400 text-sm">
        {{ plan.description }}
      </p>
    </div>

    <!-- Prix -->
    <div class="mb-6">
      <div class="flex items-baseline">
        <span class="text-4xl font-bold text-gray-900 dark:text-white">
          {{ formatPrice(plan.price) }}
        </span>
        <span class="text-gray-600 dark:text-gray-400 ml-2">
          / {{ plan.billingPeriod === 'MONTHLY' ? 'mois' : 'an' }}
        </span>
      </div>
      <p v-if="plan.billingPeriod === 'YEARLY'" class="text-sm text-gray-500 dark:text-gray-500 mt-1">
        Économisez {{ formatPrice(plan.price / 12) }} par mois
      </p>
    </div>

    <!-- Features -->
    <div class="flex-1 mb-6">
      <ul class="space-y-3">
        <li
          v-for="(feature, index) in features"
          :key="index"
          class="flex items-start gap-2"
        >
          <svg class="w-5 h-5 text-green-500 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          <span class="text-gray-700 dark:text-gray-300 text-sm">{{ feature }}</span>
        </li>
      </ul>
    </div>

    <!-- Limites -->
    <div v-if="hasLimits" class="mb-6 p-4 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
      <div class="space-y-2 text-sm">
        <div v-if="plan.maxProperties" class="flex justify-between">
          <span class="text-gray-600 dark:text-gray-400">Propriétés max :</span>
          <span class="font-semibold text-gray-900 dark:text-white">
            {{ plan.maxProperties === -1 ? 'Illimité' : plan.maxProperties }}
          </span>
        </div>
        <div v-if="plan.maxUsers" class="flex justify-between">
          <span class="text-gray-600 dark:text-gray-400">Utilisateurs max :</span>
          <span class="font-semibold text-gray-900 dark:text-white">
            {{ plan.maxUsers === -1 ? 'Illimité' : plan.maxUsers }}
          </span>
        </div>
        <div v-if="plan.maxStorageGb" class="flex justify-between">
          <span class="text-gray-600 dark:text-gray-400">Stockage :</span>
          <span class="font-semibold text-gray-900 dark:text-white">
            {{ plan.maxStorageGb === -1 ? 'Illimité' : `${plan.maxStorageGb} GB` }}
          </span>
        </div>
      </div>
    </div>

    <!-- Bouton -->
    <button
      @click="$emit('select', plan)"
      class="w-full py-3 px-6 rounded-lg font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
      :class="selected
        ? 'bg-blue-600 text-white hover:bg-blue-700'
        : 'bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white hover:bg-gray-200 dark:hover:bg-gray-600'"
    >
      {{ selected ? 'Sélectionné' : 'Choisir ce plan' }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Plan } from '@viridial/shared'

const props = defineProps<{
  plan: Plan
  selected?: boolean
}>()

defineEmits<{
  select: [plan: Plan]
}>()

const features = computed(() => {
  if (!props.plan.features) return []
  
  if (typeof props.plan.features === 'string') {
    try {
      return JSON.parse(props.plan.features)
    } catch {
      return props.plan.features.split(',').map(f => f.trim())
    }
  }
  
  return props.plan.features
})

const hasLimits = computed(() => {
  return props.plan.maxProperties !== undefined || 
         props.plan.maxUsers !== undefined || 
         props.plan.maxStorageGb !== undefined
})

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0
  }).format(price)
}
</script>

