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
        <Button
          @click="isLogin = true"
          :variant="isLogin ? 'default' : 'outline'"
        >
          Connexion
        </Button>
        <Button
          @click="isLogin = false"
          :variant="!isLogin ? 'default' : 'outline'"
        >
          Inscription
        </Button>
      </div>

      <!-- Formulaire -->
      <Card>
        <CardContent class="p-8">
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
          <div class="space-y-2">
            <Label>
              Email <span class="text-red-500">*</span>
            </Label>
            <Input
              v-model="loginForm.email"
              type="email"
              required
              :class="fieldErrors.email ? 'border-red-300 focus-visible:ring-red-500' : ''"
              placeholder="votre@email.com"
              @blur="validateField('email', loginForm.email)"
            />
            <p v-if="fieldErrors.email" class="text-sm text-red-600">{{ fieldErrors.email }}</p>
          </div>

          <div class="space-y-2">
            <Label>
              Mot de passe <span class="text-red-500">*</span>
            </Label>
            <Input
              v-model="loginForm.password"
              type="password"
              required
              :class="fieldErrors.password ? 'border-red-300 focus-visible:ring-red-500' : ''"
              placeholder="Votre mot de passe"
              @blur="validateField('password', loginForm.password)"
            />
            <p v-if="fieldErrors.password" class="text-sm text-red-600">{{ fieldErrors.password }}</p>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-2">
              <Checkbox
                :checked="loginForm.rememberMe"
                @update:checked="loginForm.rememberMe = $event"
                id="remember"
              />
              <Label for="remember" class="text-sm font-normal cursor-pointer">
                Se souvenir de moi
              </Label>
            </div>
            <router-link
              to="/forgot-password"
              class="text-sm text-primary hover:underline"
            >
              Mot de passe oublié ?
            </router-link>
          </div>

          <Button
            type="submit"
            :disabled="submitting"
            class="w-full"
          >
            {{ submitting ? 'Connexion...' : 'Se connecter' }}
          </Button>
        </form>

        <!-- Formulaire d'inscription -->
        <form v-else @submit.prevent="handleRegister" class="space-y-6">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label>
                Prénom <span class="text-red-500">*</span>
              </Label>
              <Input
                v-model="registerForm.firstName"
                type="text"
                required
                :class="fieldErrors.firstName ? 'border-red-300 focus-visible:ring-red-500' : ''"
                placeholder="Jean"
                @blur="validateField('firstName', registerForm.firstName)"
              />
              <p v-if="fieldErrors.firstName" class="text-sm text-red-600">{{ fieldErrors.firstName }}</p>
            </div>

            <div class="space-y-2">
              <Label>
                Nom <span class="text-red-500">*</span>
              </Label>
              <Input
                v-model="registerForm.lastName"
                type="text"
                required
                :class="fieldErrors.lastName ? 'border-red-300 focus-visible:ring-red-500' : ''"
                placeholder="Dupont"
                @blur="validateField('lastName', registerForm.lastName)"
              />
              <p v-if="fieldErrors.lastName" class="text-sm text-red-600">{{ fieldErrors.lastName }}</p>
            </div>
          </div>

          <div class="space-y-2">
            <Label>
              Email <span class="text-red-500">*</span>
            </Label>
            <Input
              v-model="registerForm.email"
              type="email"
              required
              :class="fieldErrors.email ? 'border-red-300 focus-visible:ring-red-500' : ''"
              placeholder="votre@email.com"
              @blur="validateField('email', registerForm.email)"
            />
            <p v-if="fieldErrors.email" class="text-sm text-red-600">{{ fieldErrors.email }}</p>
          </div>

          <div class="space-y-2">
            <Label>
              Mot de passe <span class="text-red-500">*</span>
            </Label>
            <Input
              v-model="registerForm.password"
              type="password"
              required
              minlength="8"
              :class="fieldErrors.password ? 'border-red-300 focus-visible:ring-red-500' : ''"
              placeholder="Minimum 8 caractères"
              @blur="validateField('password', registerForm.password)"
            />
            <p v-if="fieldErrors.password" class="text-sm text-red-600">{{ fieldErrors.password }}</p>
            <p class="text-xs text-muted-foreground">Au moins 8 caractères, une majuscule, une minuscule et un chiffre</p>
          </div>

          <div class="space-y-2">
            <Label>
              Confirmer le mot de passe <span class="text-red-500">*</span>
            </Label>
            <Input
              v-model="registerForm.confirmPassword"
              type="password"
              required
              :class="fieldErrors.confirmPassword ? 'border-red-300 focus-visible:ring-red-500' : ''"
              placeholder="Répétez le mot de passe"
              @blur="validateField('confirmPassword', registerForm.confirmPassword)"
            />
            <p v-if="fieldErrors.confirmPassword" class="text-sm text-red-600">{{ fieldErrors.confirmPassword }}</p>
          </div>

          <div class="flex items-start space-x-2">
            <Checkbox
              :checked="registerForm.acceptTerms"
              @update:checked="registerForm.acceptTerms = $event"
              id="terms"
              required
            />
            <Label for="terms" class="text-sm font-normal cursor-pointer">
              J'accepte les 
              <router-link to="/terms" class="text-primary hover:underline">conditions d'utilisation</router-link>
              et la 
              <router-link to="/privacy" class="text-primary hover:underline">politique de confidentialité</router-link>
              <span class="text-red-500">*</span>
            </Label>
          </div>

          <Button
            type="submit"
            :disabled="submitting"
            class="w-full"
          >
            {{ submitting ? 'Inscription...' : 'Créer mon compte' }}
          </Button>
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
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { AlertCircle, X } from 'lucide-vue-next'
import { authService, useAuthStore } from '@viridial/shared'
import { useToast } from '@/composables/useToast'
import { Card, CardContent } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'

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
      name: `${registerForm.value.firstName} ${registerForm.value.lastName}`,
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

