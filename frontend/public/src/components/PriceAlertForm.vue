<template>
  <div class="bg-white rounded-lg shadow-sm p-6">
    <div class="flex items-center gap-2 mb-4">
      <Bell class="h-5 w-5 text-blue-600" />
      <h3 class="text-lg font-semibold text-gray-900">Alerte de Prix</h3>
    </div>
    <p class="text-sm text-gray-600 mb-4">
      Soyez notifié lorsque le prix de cette propriété change
    </p>

    <div v-if="loading" class="flex items-center justify-center p-4">
      <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
    </div>

    <div v-else-if="success" class="bg-green-50 border border-green-200 rounded-lg p-4">
      <div class="flex items-center gap-2">
        <CheckCircle class="h-5 w-5 text-green-600" />
        <p class="text-green-800 font-medium">Alerte créée avec succès !</p>
      </div>
      <p class="text-sm text-green-700 mt-2">
        Vous recevrez une notification lorsque le prix changera selon vos critères.
      </p>
    </div>

    <form v-else @submit.prevent="createAlert" class="space-y-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Type d'alerte
        </label>
        <select
          v-model="formData.alertType"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        >
          <option value="">Sélectionner un type</option>
          <option value="PRICE_DROP">Alerte si le prix baisse</option>
          <option value="PERCENTAGE_DROP">Alerte si le prix baisse de X%</option>
          <option value="PRICE_INCREASE">Alerte si le prix augmente</option>
          <option value="PERCENTAGE_INCREASE">Alerte si le prix augmente de X%</option>
        </select>
      </div>

      <div v-if="formData.alertType === 'PRICE_DROP' || formData.alertType === 'PRICE_INCREASE'">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Prix cible (€)
        </label>
        <input
          v-model.number="formData.targetPrice"
          type="number"
          step="0.01"
          :placeholder="formData.alertType === 'PRICE_DROP' ? 'Prix minimum' : 'Prix maximum'"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        />
      </div>

      <div v-if="formData.alertType === 'PERCENTAGE_DROP' || formData.alertType === 'PERCENTAGE_INCREASE'">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Seuil de pourcentage (%)
        </label>
        <input
          v-model.number="formData.percentageThreshold"
          type="number"
          step="0.1"
          min="0.1"
          placeholder="Ex: 5 pour 5%"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        />
      </div>

      <div class="flex items-center gap-4">
        <label class="flex items-center gap-2 cursor-pointer">
          <input
            v-model="formData.emailNotification"
            type="checkbox"
            class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <span class="text-sm text-gray-700">Notification par email</span>
        </label>
        <label class="flex items-center gap-2 cursor-pointer">
          <input
            v-model="formData.inAppNotification"
            type="checkbox"
            class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <span class="text-sm text-gray-700">Notification in-app</span>
        </label>
      </div>

      <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-3">
        <p class="text-sm text-red-800">{{ error }}</p>
      </div>

      <button
        type="submit"
        :disabled="submitting"
        class="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <span v-if="submitting">Création en cours...</span>
        <span v-else>Créer l'alerte</span>
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { priceAlertService } from '@viridial/shared'
import type { PriceAlertCreate } from '@viridial/shared'
import { Bell, CheckCircle } from 'lucide-vue-next'

interface Props {
  propertyId: number
  currentPrice: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  alertCreated: []
}>()

const loading = ref(false)
const submitting = ref(false)
const success = ref(false)
const error = ref<string | null>(null)

const formData = reactive<PriceAlertCreate>({
  propertyId: props.propertyId,
  alertType: 'PERCENTAGE_DROP' as any,
  emailNotification: true,
  inAppNotification: true
})

const createAlert = async () => {
  submitting.value = true
  error.value = null

  try {
    await priceAlertService.create(formData)
    success.value = true
    emit('alertCreated')
    
    // Réinitialiser le formulaire après 3 secondes
    setTimeout(() => {
      success.value = false
      formData.alertType = 'PERCENTAGE_DROP' as any
      formData.targetPrice = undefined
      formData.percentageThreshold = undefined
    }, 3000)
  } catch (err: any) {
    console.error('Error creating price alert:', err)
    error.value = err.message || 'Impossible de créer l\'alerte'
  } finally {
    submitting.value = false
  }
}
</script>

