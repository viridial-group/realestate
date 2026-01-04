<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <!-- Header -->
      <div class="text-center">
        <h2 class="text-3xl font-bold text-gray-900">Mot de passe oublié ?</h2>
        <p class="mt-2 text-sm text-gray-600">
          Entrez votre adresse email et nous vous enverrons un lien pour réinitialiser votre mot de passe
        </p>
      </div>

      <!-- Formulaire -->
      <div class="bg-white rounded-lg shadow-sm p-8">
        <!-- Message d'erreur -->
        <div
          v-if="error"
          class="mb-6 bg-red-50 border border-red-200 rounded-lg p-4"
        >
          <p class="text-sm text-red-800">{{ error }}</p>
        </div>

        <!-- Message de succès -->
        <div
          v-if="success"
          class="mb-6 bg-green-50 border border-green-200 rounded-lg p-4"
        >
          <div class="flex items-start gap-3">
            <CheckCircle class="h-5 w-5 text-green-600 flex-shrink-0 mt-0.5" />
            <div>
              <p class="text-sm text-green-800 font-medium mb-1">Email envoyé !</p>
              <p class="text-sm text-green-700">
                Vérifiez votre boîte de réception et suivez les instructions pour réinitialiser votre mot de passe.
              </p>
            </div>
          </div>
        </div>

        <form v-if="!success" @submit.prevent="handleSubmit" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Adresse email <span class="text-red-500">*</span>
            </label>
            <input
              v-model="email"
              type="email"
              required
              :class="[
                'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                fieldError ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
              ]"
              placeholder="votre@email.com"
              @blur="validateEmail"
            />
            <p v-if="fieldError" class="mt-1 text-sm text-red-600">{{ fieldError }}</p>
          </div>

          <button
            type="submit"
            :disabled="submitting"
            class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition-colors font-medium"
          >
            {{ submitting ? 'Envoi...' : 'Envoyer le lien de réinitialisation' }}
          </button>
        </form>

        <!-- Lien de retour -->
        <div class="mt-6 text-center">
          <router-link
            to="/login"
            class="text-sm text-blue-600 hover:text-blue-700"
          >
            ← Retour à la connexion
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { CheckCircle } from 'lucide-vue-next'
import { authService } from '@viridial/shared'
import { useToast } from '@/composables/useToast'

const { showToast } = useToast()

const email = ref('')
const submitting = ref(false)
const error = ref<string | null>(null)
const fieldError = ref<string | null>(null)
const success = ref(false)

function validateEmail() {
  fieldError.value = null
  if (!email.value) {
    fieldError.value = 'L\'email est requis'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    fieldError.value = 'Email invalide'
  }
}

async function handleSubmit() {
  validateEmail()
  if (fieldError.value) return

  submitting.value = true
  error.value = null

  try {
    await authService.forgotPassword({ email: email.value })
    success.value = true
    showToast('Email de réinitialisation envoyé', 'success')
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Erreur lors de l\'envoi de l\'email'
    showToast(error.value, 'error')
  } finally {
    submitting.value = false
  }
}
</script>

