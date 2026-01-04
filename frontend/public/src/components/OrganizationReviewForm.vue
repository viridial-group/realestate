<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click.self="$emit('close')">
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
      <div class="p-6">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-semibold text-gray-900 dark:text-white">Laisser un avis</h2>
          <button
            @click="$emit('close')"
            class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
          >
            ✕
          </button>
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Note -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Note <span class="text-red-500">*</span>
            </label>
            <div class="flex items-center gap-2">
              <button
                v-for="i in 5"
                :key="i"
                type="button"
                @click="form.rating = i"
                class="text-3xl transition-colors"
                :class="i <= form.rating ? 'text-yellow-500' : 'text-gray-300 dark:text-gray-600'"
              >
                ★
              </button>
              <span v-if="form.rating" class="ml-2 text-sm text-gray-600 dark:text-gray-400">
                {{ form.rating }}/5
              </span>
            </div>
          </div>

          <!-- Nom -->
          <div>
            <label for="authorName" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Nom <span class="text-red-500">*</span>
            </label>
            <input
              id="authorName"
              v-model="form.authorName"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
              placeholder="Votre nom"
            />
          </div>

          <!-- Email -->
          <div>
            <label for="authorEmail" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Email (optionnel)
            </label>
            <input
              id="authorEmail"
              v-model="form.authorEmail"
              type="email"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
              placeholder="votre@email.com"
            />
          </div>

          <!-- Type de transaction -->
          <div>
            <label for="transactionType" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Type de transaction (optionnel)
            </label>
            <select
              id="transactionType"
              v-model="form.transactionType"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
            >
              <option :value="undefined">Non spécifié</option>
              <option value="SALE">Achat</option>
              <option value="RENT">Location</option>
            </select>
          </div>

          <!-- Commentaire -->
          <div>
            <label for="comment" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Votre avis <span class="text-red-500">*</span>
            </label>
            <textarea
              id="comment"
              v-model="form.comment"
              required
              rows="6"
              maxlength="2000"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white resize-none"
              placeholder="Partagez votre expérience avec cette agence..."
            ></textarea>
            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
              {{ form.comment.length }}/2000 caractères
            </p>
          </div>

          <!-- Client vérifié -->
          <div class="flex items-center gap-2">
            <input
              id="verifiedClient"
              v-model="form.verifiedClient"
              type="checkbox"
              class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <label for="verifiedClient" class="text-sm text-gray-700 dark:text-gray-300">
              J'ai effectué une transaction avec cette agence
            </label>
          </div>

          <!-- Erreur -->
          <div v-if="submitError" class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
            <p class="text-red-800 dark:text-red-200 text-sm">{{ submitError }}</p>
          </div>

          <!-- Actions -->
          <div class="flex justify-end gap-3">
            <button
              type="button"
              @click="$emit('close')"
              class="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 font-medium rounded-lg transition-colors"
            >
              Annuler
            </button>
            <button
              type="submit"
              :disabled="loading || !form.rating || !form.comment || !form.authorName"
              class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="loading">Envoi en cours...</span>
              <span v-else>Publier l'avis</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { organizationReviewService, type OrganizationReviewCreate } from '@viridial/shared'
import { useToast } from '@/composables/useToast'

const props = defineProps<{
  organizationId: number
}>()

const emit = defineEmits<{
  close: []
  submitted: []
}>()

const { success } = useToast()

const form = ref<OrganizationReviewCreate>({
  organizationId: props.organizationId,
  rating: 0,
  comment: '',
  authorName: '',
  authorEmail: '',
  transactionType: undefined,
  verifiedClient: false
})

const loading = ref(false)
const submitError = ref<string | null>(null)

async function handleSubmit() {
  if (!form.value.rating || !form.value.comment || !form.value.authorName) {
    submitError.value = 'Veuillez remplir tous les champs obligatoires'
    return
  }

  loading.value = true
  submitError.value = null

  try {
    await organizationReviewService.createReview(props.organizationId, form.value)
    
    success('Votre avis a été soumis et sera modéré avant publication.')

    // Réinitialiser le formulaire
    form.value = {
      organizationId: props.organizationId,
      rating: 0,
      comment: '',
      authorName: '',
      authorEmail: '',
      transactionType: undefined,
      verifiedClient: false
    }

    emit('submitted')
    emit('close')
  } catch (err: any) {
    console.error('Error submitting review:', err)
    submitError.value = err.message || 'Une erreur est survenue lors de l\'envoi de l\'avis'
  } finally {
    loading.value = false
  }
}
</script>

