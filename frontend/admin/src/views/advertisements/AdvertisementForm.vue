<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
      <div class="p-6">
        <!-- En-tête -->
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-bold text-gray-900 dark:text-white">
            {{ advertisement ? 'Modifier l\'annonce' : 'Créer une annonce' }}
          </h2>
          <button
            @click="$emit('close')"
            class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
          >
            ✕
          </button>
        </div>

        <!-- Formulaire -->
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Informations de base -->
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Informations de base</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Titre <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.title"
                  type="text"
                  required
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                  placeholder="Titre de l'annonce"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Type <span class="text-red-500">*</span>
                </label>
                <select
                  v-model="form.adType"
                  required
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                >
                  <option value="BANNER">Bannière</option>
                  <option value="SIDEBAR">Barre latérale</option>
                  <option value="INLINE">Inline</option>
                  <option value="POPUP">Popup</option>
                  <option value="SPONSORED_PROPERTY">Propriété sponsorisée</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Position <span class="text-red-500">*</span>
                </label>
                <select
                  v-model="form.position"
                  required
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                >
                  <option value="TOP">Haut</option>
                  <option value="BOTTOM">Bas</option>
                  <option value="SIDEBAR_LEFT">Barre latérale gauche</option>
                  <option value="SIDEBAR_RIGHT">Barre latérale droite</option>
                  <option value="INLINE">Inline</option>
                  <option value="HEADER">En-tête</option>
                  <option value="FOOTER">Pied de page</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Statut
                </label>
                <select
                  v-model="form.status"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                >
                  <option value="DRAFT">Brouillon</option>
                  <option value="ACTIVE">Actif</option>
                  <option value="PAUSED">En pause</option>
                </select>
              </div>
            </div>
            <div class="mt-4">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Description
              </label>
              <textarea
                v-model="form.description"
                rows="3"
                class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                placeholder="Description de l'annonce"
              />
            </div>
          </div>

          <!-- Images et liens -->
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Contenu</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  URL de l'image
                </label>
                <input
                  v-model="form.imageUrl"
                  type="url"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                  placeholder="https://example.com/image.jpg"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  URL de destination
                </label>
                <input
                  v-model="form.linkUrl"
                  type="url"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                  placeholder="https://example.com"
                />
              </div>
            </div>
          </div>

          <!-- Dates -->
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Période</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Date de début <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.startDate"
                  type="datetime-local"
                  required
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Date de fin
                </label>
                <input
                  v-model="form.endDate"
                  type="datetime-local"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                />
              </div>
            </div>
          </div>

          <!-- Budget -->
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Budget</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Budget total (€)
                </label>
                <input
                  v-model.number="form.budget"
                  type="number"
                  step="0.01"
                  min="0"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Budget quotidien (€)
                </label>
                <input
                  v-model.number="form.dailyBudget"
                  type="number"
                  step="0.01"
                  min="0"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Coût par clic (€)
                </label>
                <input
                  v-model.number="form.costPerClick"
                  type="number"
                  step="0.01"
                  min="0"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                />
              </div>
            </div>
          </div>

          <!-- Ciblage -->
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Ciblage</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Localisations (JSON)
                </label>
                <textarea
                  v-model="form.targetLocations"
                  rows="3"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white font-mono text-sm"
                  placeholder='{"cities": ["Paris"], "postalCodes": ["75001"]}'
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Types de propriétés (JSON)
                </label>
                <textarea
                  v-model="form.targetPropertyTypes"
                  rows="3"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white font-mono text-sm"
                  placeholder='["APARTMENT", "HOUSE"]'
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Types de transaction (JSON)
                </label>
                <textarea
                  v-model="form.targetTransactionTypes"
                  rows="3"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white font-mono text-sm"
                  placeholder='["RENT", "SALE"]'
                />
              </div>
            </div>
          </div>

          <!-- Paramètres avancés -->
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Paramètres avancés</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Impressions max/jour
                </label>
                <input
                  v-model.number="form.maxImpressionsPerDay"
                  type="number"
                  min="0"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Priorité
                </label>
                <input
                  v-model.number="form.priority"
                  type="number"
                  min="0"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div class="flex items-end">
                <label class="flex items-center">
                  <input
                    v-model="form.active"
                    type="checkbox"
                    class="mr-2"
                  />
                  <span class="text-sm text-gray-700 dark:text-gray-300">Actif</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex justify-end gap-4 pt-4 border-t border-gray-200 dark:border-gray-700">
            <button
              type="button"
              @click="$emit('close')"
              class="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              Annuler
            </button>
            <button
              type="submit"
              :disabled="submitting"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-colors"
            >
              {{ submitting ? 'Enregistrement...' : 'Enregistrer' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { advertisementService, type Advertisement, type AdvertisementCreate, type AdvertisementUpdate } from '@viridial/shared'
import { useAuthStore } from '@viridial/shared'

const props = defineProps<{
  advertisement?: Advertisement | null
}>()

const emit = defineEmits<{
  close: []
  saved: []
}>()

const authStore = useAuthStore()
const submitting = ref(false)

const form = ref<AdvertisementCreate | AdvertisementUpdate>({
  title: '',
  description: '',
  imageUrl: '',
  linkUrl: '',
  adType: 'BANNER',
  position: 'TOP',
  status: 'DRAFT',
  startDate: new Date().toISOString().slice(0, 16),
  endDate: '',
  budget: undefined,
  dailyBudget: undefined,
  costPerClick: undefined,
  costPerImpression: undefined,
  targetLocations: '',
  targetPropertyTypes: '',
  targetTransactionTypes: '',
  maxImpressionsPerDay: undefined,
  priority: 0,
  active: true,
  organizationId: authStore.user?.organizationId || 0
})

watch(() => props.advertisement, (ad) => {
  if (ad) {
    form.value = {
      title: ad.title,
      description: ad.description || '',
      imageUrl: ad.imageUrl || '',
      linkUrl: ad.linkUrl || '',
      adType: ad.adType,
      position: ad.position,
      status: ad.status,
      startDate: ad.startDate ? new Date(ad.startDate).toISOString().slice(0, 16) : '',
      endDate: ad.endDate ? new Date(ad.endDate).toISOString().slice(0, 16) : '',
      budget: ad.budget,
      dailyBudget: ad.dailyBudget,
      costPerClick: ad.costPerClick,
      costPerImpression: ad.costPerImpression,
      targetLocations: ad.targetLocations || '',
      targetPropertyTypes: ad.targetPropertyTypes || '',
      targetTransactionTypes: ad.targetTransactionTypes || '',
      maxImpressionsPerDay: ad.maxImpressionsPerDay,
      priority: ad.priority,
      active: ad.active
    }
  }
}, { immediate: true })

async function handleSubmit() {
  submitting.value = true
  try {
    if (props.advertisement) {
      await advertisementService.update(props.advertisement.id, form.value as AdvertisementUpdate)
    } else {
      await advertisementService.create({
        ...form.value,
        organizationId: authStore.user?.organizationId || 0
      } as AdvertisementCreate)
    }
    emit('saved')
  } catch (error: any) {
    console.error('Error saving advertisement:', error)
    alert('Erreur lors de l\'enregistrement')
  } finally {
    submitting.value = false
  }
}
</script>

