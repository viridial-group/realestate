<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <!-- Header -->
      <div class="text-center">
        <h2 class="text-3xl font-bold text-gray-900">Réinitialiser le mot de passe</h2>
        <p class="mt-2 text-sm text-gray-600">
          Entrez votre nouveau mot de passe
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
          class="mb-6 bg-green-50 border border-green-200 rounded-lg p-4 text-center"
        >
          <CheckCircle class="h-12 w-12 text-green-600 mx-auto mb-3" />
          <p class="text-sm text-green-800 font-medium mb-2">Mot de passe réinitialisé avec succès !</p>
          <router-link
            to="/login"
            class="text-sm text-blue-600 hover:text-blue-700 underline"
          >
            Se connecter maintenant
          </router-link>
        </div>

        <form v-if="!success" @submit.prevent="handleSubmit" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Nouveau mot de passe <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.password"
              type="password"
              required
              minlength="8"
              :class="[
                'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                fieldErrors.password ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
              ]"
              placeholder="Minimum 8 caractères"
              @blur="validateField('password')"
            />
            <p v-if="fieldErrors.password" class="mt-1 text-sm text-red-600">{{ fieldErrors.password }}</p>
            <p class="mt-1 text-xs text-gray-500">Au moins 8 caractères, une majuscule, une minuscule et un chiffre</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Confirmer le mot de passe <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.confirmPassword"
              type="password"
              required
              :class="[
                'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                fieldErrors.confirmPassword ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
              ]"
              placeholder="Répétez le mot de passe"
              @blur="validateField('confirmPassword')"
            />
            <p v-if="fieldErrors.confirmPassword" class="mt-1 text-sm text-red-600">{{ fieldErrors.confirmPassword }}</p>
          </div>

          <button
            type="submit"
            :disabled="submitting"
            class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition-colors font-medium"
          >
            {{ submitting ? 'Réinitialisation...' : 'Réinitialiser le mot de passe' }}
          </button>
        </form>

        <!-- Lien de retour -->
        <div v-if="!success" class="mt-6 text-center">
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
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { CheckCircle } from 'lucide-vue-next'
import { authService } from '@viridial/shared'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const route = useRoute()
const { showToast } = useToast()

const form = ref({
  password: '',
  confirmPassword: '',
})

const submitting = ref(false)
const error = ref<string | null>(null)
const fieldErrors = ref<Record<string, string>>({})
const success = ref(false)
const token = ref<string | null>(null)

onMounted(() => {
  // Récupérer le token depuis l'URL
  token.value = (route.query.token as string) || null
  if (!token.value) {
    error.value = 'Token de réinitialisation manquant. Veuillez utiliser le lien reçu par email.'
  }
})

function validateField(fieldName: string) {
  delete fieldErrors.value[fieldName]

  switch (fieldName) {
    case 'password':
      if (!form.value.password || form.value.password.length < 8) {
        fieldErrors.value.password = 'Le mot de passe doit contenir au moins 8 caractères'
      } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(form.value.password)) {
        fieldErrors.value.password = 'Le mot de passe doit contenir une majuscule, une minuscule et un chiffre'
      }
      break
    case 'confirmPassword':
      if (form.value.confirmPassword !== form.value.password) {
        fieldErrors.value.confirmPassword = 'Les mots de passe ne correspondent pas'
      }
      break
  }
}

async function handleSubmit() {
  if (!token.value) {
    error.value = 'Token de réinitialisation manquant'
    return
  }

  // Valider tous les champs
  validateField('password')
  validateField('confirmPassword')

  if (Object.keys(fieldErrors.value).length > 0) {
    error.value = 'Veuillez corriger les erreurs dans le formulaire'
    return
  }

  submitting.value = true
  error.value = null

  try {
    await authService.resetPassword({
      token: token.value,
      newPassword: form.value.password,
    })
    
    success.value = true
    showToast('Mot de passe réinitialisé avec succès', 'success')
    
    // Rediriger vers la connexion après 3 secondes
    setTimeout(() => {
      router.push('/login')
    }, 3000)
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Erreur lors de la réinitialisation'
    showToast(error.value, 'error')
  } finally {
    submitting.value = false
  }
}
</script>

