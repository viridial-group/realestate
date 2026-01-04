<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <!-- Header -->
      <div class="text-center">
        <h2 class="text-3xl font-bold text-gray-900">
          {{ isLogin ? 'Connexion' : 'Créer un compte' }}
        </h2>
        <p class="mt-2 text-sm text-gray-600">
          {{ isLogin 
            ? 'Connectez-vous pour gérer vos annonces' 
            : 'Créez un compte pour publier vos annonces immobilières' 
          }}
        </p>
      </div>

      <!-- Toggle Login/Register -->
      <div class="flex items-center justify-center gap-4">
        <button
          @click="isLogin = true"
          :class="[
            'px-4 py-2 text-sm font-medium rounded-md transition-colors',
            isLogin 
              ? 'bg-blue-600 text-white' 
              : 'text-gray-700 hover:bg-gray-100'
          ]"
        >
          Connexion
        </button>
        <button
          @click="isLogin = false"
          :class="[
            'px-4 py-2 text-sm font-medium rounded-md transition-colors',
            !isLogin 
              ? 'bg-blue-600 text-white' 
              : 'text-gray-700 hover:bg-gray-100'
          ]"
        >
          Inscription
        </button>
      </div>

      <!-- Formulaire -->
      <div class="bg-white rounded-lg shadow-sm p-8">
        <!-- Message d'erreur global -->
        <div
          v-if="globalError"
          class="mb-6 bg-red-50 border border-red-200 rounded-lg p-4"
        >
          <div class="flex items-start gap-3">
            <AlertCircle class="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
            <div class="flex-1">
              <p class="text-sm text-red-800">{{ globalError }}</p>
            </div>
            <button
              @click="globalError = null"
              class="text-red-600 hover:text-red-800"
            >
              <X class="h-4 w-4" />
            </button>
          </div>
        </div>

        <!-- Formulaire de connexion -->
        <form v-if="isLogin" @submit.prevent="handleLogin" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Email <span class="text-red-500">*</span>
            </label>
            <input
              v-model="loginForm.email"
              type="email"
              required
              :class="[
                'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                fieldErrors.email ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
              ]"
              placeholder="votre@email.com"
              @blur="validateField('email', loginForm.email)"
            />
            <p v-if="fieldErrors.email" class="mt-1 text-sm text-red-600">{{ fieldErrors.email }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Mot de passe <span class="text-red-500">*</span>
            </label>
            <input
              v-model="loginForm.password"
              type="password"
              required
              :class="[
                'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                fieldErrors.password ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
              ]"
              placeholder="Votre mot de passe"
              @blur="validateField('password', loginForm.password)"
            />
            <p v-if="fieldErrors.password" class="mt-1 text-sm text-red-600">{{ fieldErrors.password }}</p>
          </div>

          <div class="flex items-center justify-between">
            <label class="flex items-center">
              <input
                v-model="loginForm.rememberMe"
                type="checkbox"
                class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span class="ml-2 text-sm text-gray-600">Se souvenir de moi</span>
            </label>
            <router-link
              to="/forgot-password"
              class="text-sm text-blue-600 hover:text-blue-700"
            >
              Mot de passe oublié ?
            </router-link>
          </div>

          <button
            type="submit"
            :disabled="submitting"
            class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition-colors font-medium"
          >
            {{ submitting ? 'Connexion...' : 'Se connecter' }}
          </button>
        </form>

        <!-- Formulaire d'inscription -->
        <form v-else @submit.prevent="handleRegister" class="space-y-6">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Prénom <span class="text-red-500">*</span>
              </label>
              <input
                v-model="registerForm.firstName"
                type="text"
                required
                :class="[
                  'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                  fieldErrors.firstName ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                ]"
                placeholder="Jean"
                @blur="validateField('firstName', registerForm.firstName)"
              />
              <p v-if="fieldErrors.firstName" class="mt-1 text-sm text-red-600">{{ fieldErrors.firstName }}</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Nom <span class="text-red-500">*</span>
              </label>
              <input
                v-model="registerForm.lastName"
                type="text"
                required
                :class="[
                  'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                  fieldErrors.lastName ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                ]"
                placeholder="Dupont"
                @blur="validateField('lastName', registerForm.lastName)"
              />
              <p v-if="fieldErrors.lastName" class="mt-1 text-sm text-red-600">{{ fieldErrors.lastName }}</p>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Email <span class="text-red-500">*</span>
            </label>
            <input
              v-model="registerForm.email"
              type="email"
              required
              :class="[
                'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                fieldErrors.email ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
              ]"
              placeholder="votre@email.com"
              @blur="validateField('email', registerForm.email)"
            />
            <p v-if="fieldErrors.email" class="mt-1 text-sm text-red-600">{{ fieldErrors.email }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Mot de passe <span class="text-red-500">*</span>
            </label>
            <input
              v-model="registerForm.password"
              type="password"
              required
              minlength="8"
              :class="[
                'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                fieldErrors.password ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
              ]"
              placeholder="Minimum 8 caractères"
              @blur="validateField('password', registerForm.password)"
            />
            <p v-if="fieldErrors.password" class="mt-1 text-sm text-red-600">{{ fieldErrors.password }}</p>
            <p class="mt-1 text-xs text-gray-500">Au moins 8 caractères, une majuscule, une minuscule et un chiffre</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Confirmer le mot de passe <span class="text-red-500">*</span>
            </label>
            <input
              v-model="registerForm.confirmPassword"
              type="password"
              required
              :class="[
                'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                fieldErrors.confirmPassword ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
              ]"
              placeholder="Répétez le mot de passe"
              @blur="validateField('confirmPassword', registerForm.confirmPassword)"
            />
            <p v-if="fieldErrors.confirmPassword" class="mt-1 text-sm text-red-600">{{ fieldErrors.confirmPassword }}</p>
          </div>

          <div>
            <label class="flex items-start">
              <input
                v-model="registerForm.acceptTerms"
                type="checkbox"
                required
                class="mt-1 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span class="ml-2 text-sm text-gray-600">
                J'accepte les 
                <router-link to="/terms" class="text-blue-600 hover:text-blue-700">conditions d'utilisation</router-link>
                et la 
                <router-link to="/privacy" class="text-blue-600 hover:text-blue-700">politique de confidentialité</router-link>
                <span class="text-red-500">*</span>
              </span>
            </label>
          </div>

          <button
            type="submit"
            :disabled="submitting"
            class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition-colors font-medium"
          >
            {{ submitting ? 'Inscription...' : 'Créer mon compte' }}
          </button>
        </form>

        <!-- Lien vers l'abonnement professionnel -->
        <div class="mt-6 pt-6 border-t border-gray-200 text-center">
          <p class="text-sm text-gray-600">
            Vous êtes un professionnel ? 
            <router-link to="/subscribe" class="text-blue-600 hover:text-blue-700 font-medium">
              Découvrez nos offres
            </router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { AlertCircle, X } from 'lucide-vue-next'
import { authService, useAuthStore } from '@viridial/shared'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const route = useRoute()
const { showToast } = useToast()
const authStore = useAuthStore()

const isLogin = ref(true)
const submitting = ref(false)
const globalError = ref<string | null>(null)
const fieldErrors = ref<Record<string, string>>({})

const loginForm = ref({
  email: '',
  password: '',
  rememberMe: false,
})

const registerForm = ref({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  confirmPassword: '',
  acceptTerms: false,
})

// Vérifier si on vient d'une redirection
const redirectTo = computed(() => {
  return (route.query.redirect as string) || '/my-properties'
})

function validateField(fieldName: string, value: any) {
  delete fieldErrors.value[fieldName]

  switch (fieldName) {
    case 'email':
      if (!value || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        fieldErrors.value.email = 'Email invalide'
      }
      break
    case 'password':
      if (!value || value.length < 8) {
        fieldErrors.value.password = 'Le mot de passe doit contenir au moins 8 caractères'
      } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
        fieldErrors.value.password = 'Le mot de passe doit contenir une majuscule, une minuscule et un chiffre'
      }
      break
    case 'confirmPassword':
      if (value !== registerForm.value.password) {
        fieldErrors.value.confirmPassword = 'Les mots de passe ne correspondent pas'
      }
      break
    case 'firstName':
    case 'lastName':
      if (!value || value.trim().length < 2) {
        fieldErrors.value[fieldName] = 'Ce champ doit contenir au moins 2 caractères'
      }
      break
  }
}

async function handleLogin() {
  // Valider tous les champs
  validateField('email', loginForm.value.email)
  validateField('password', loginForm.value.password)

  if (Object.keys(fieldErrors.value).length > 0) {
    globalError.value = 'Veuillez corriger les erreurs dans le formulaire'
    return
  }

  submitting.value = true
  globalError.value = null

  try {
    await authStore.login({
      email: loginForm.value.email,
      password: loginForm.value.password,
    })

    showToast('Connexion réussie', 'success')
    router.push(redirectTo.value)
  } catch (err: any) {
    const errorMessage = err.response?.data?.message || err.message || 'Erreur lors de la connexion'
    globalError.value = errorMessage
    showToast(errorMessage, 'error')
  } finally {
    submitting.value = false
  }
}

async function handleRegister() {
  // Valider tous les champs
  Object.keys(registerForm.value).forEach(key => {
    if (key !== 'acceptTerms') {
      validateField(key, registerForm.value[key as keyof typeof registerForm.value])
    }
  })

  if (!registerForm.value.acceptTerms) {
    globalError.value = 'Vous devez accepter les conditions d\'utilisation'
    return
  }

  if (Object.keys(fieldErrors.value).length > 0) {
    globalError.value = 'Veuillez corriger les erreurs dans le formulaire'
    return
  }

  submitting.value = true
  globalError.value = null

  try {
    await authService.signup({
      firstName: registerForm.value.firstName,
      lastName: registerForm.value.lastName,
      email: registerForm.value.email,
      password: registerForm.value.password,
    })

    showToast('Inscription réussie ! Vous pouvez maintenant vous connecter', 'success')
    
    // Basculer vers le formulaire de connexion
    isLogin.value = true
    loginForm.value.email = registerForm.value.email
    
    // Réinitialiser le formulaire d'inscription
    registerForm.value = {
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      confirmPassword: '',
      acceptTerms: false,
    }
  } catch (err: any) {
    const errorMessage = err.response?.data?.message || err.message || 'Erreur lors de l\'inscription'
    globalError.value = errorMessage
    showToast(errorMessage, 'error')
  } finally {
    submitting.value = false
  }
}
</script>

