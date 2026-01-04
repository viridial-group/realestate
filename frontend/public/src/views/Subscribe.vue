<template>
  <div class="min-h-screen bg-gradient-to-b from-blue-50 to-white dark:from-gray-900 dark:to-gray-800 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-7xl mx-auto">
      <!-- En-tête -->
      <div class="text-center mb-12">
        <h1 class="text-4xl md:text-5xl font-bold text-gray-900 dark:text-white mb-4">
          Choisissez votre abonnement
        </h1>
        <p class="text-xl text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
          Commencez votre essai gratuit et gérez vos propriétés immobilières en toute simplicité
        </p>
      </div>

      <!-- Plans disponibles -->
      <div v-if="!showForm" class="mb-12">
        <div v-if="loadingPlans" class="text-center py-12">
          <SkeletonLoader type="generic" :lines="3" />
        </div>
        
        <div v-else-if="plans.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-8">
          <PlanCard
            v-for="plan in plans"
            :key="plan.id"
            :plan="plan"
            :selected="selectedPlan?.id === plan.id"
            @select="handlePlanSelect"
          />
        </div>
        
        <div v-else-if="error" class="mb-8">
          <EmptyState
            title="Erreur de chargement"
            :description="error"
            icon="error"
            action-label="Réessayer"
            :show-action="true"
            @action="loadPlans"
          />
        </div>
        
        <EmptyState
          v-else
          title="Aucun plan disponible"
          description="Les plans d'abonnement ne sont pas disponibles pour le moment. Veuillez réessayer plus tard ou contacter le support."
          icon="error"
          action-label="Réessayer"
          :show-action="true"
          @action="loadPlans"
        />
      </div>

      <!-- Formulaire d'inscription -->
      <div v-if="showForm && selectedPlan" class="max-w-2xl mx-auto">
        <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-xl p-8">
          <!-- Plan sélectionné -->
          <div class="mb-6 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
            <div class="flex items-center justify-between">
              <div>
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
                  Plan sélectionné : {{ selectedPlan.name }}
                </h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">
                  {{ formatPrice(selectedPlan.price) }} / {{ selectedPlan.billingPeriod === 'MONTHLY' ? 'mois' : 'an' }}
                </p>
              </div>
              <button
                @click="showForm = false"
                class="text-sm text-blue-600 dark:text-blue-400 hover:underline"
              >
                Changer
              </button>
            </div>
          </div>

          <!-- Formulaire -->
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <!-- Informations personnelles -->
            <div>
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                Informations personnelles
              </h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Prénom <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="form.firstName"
                    type="text"
                    required
                    class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                    placeholder="Votre prénom"
                  />
                  <p v-if="errors.firstName" class="mt-1 text-sm text-red-600">{{ errors.firstName }}</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Nom <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="form.lastName"
                    type="text"
                    required
                    class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                    placeholder="Votre nom"
                  />
                  <p v-if="errors.lastName" class="mt-1 text-sm text-red-600">{{ errors.lastName }}</p>
                </div>
              </div>
            </div>

            <!-- Email et mot de passe -->
            <div>
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                Connexion
              </h3>
              <div class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Email <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="form.email"
                    type="email"
                    required
                    class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                    placeholder="votre@email.com"
                  />
                  <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Mot de passe <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="form.password"
                    type="password"
                    required
                    minlength="8"
                    class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                    placeholder="Minimum 8 caractères"
                  />
                  <p v-if="errors.password" class="mt-1 text-sm text-red-600">{{ errors.password }}</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Confirmer le mot de passe <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="form.confirmPassword"
                    type="password"
                    required
                    class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                    placeholder="Confirmez votre mot de passe"
                  />
                  <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-600">{{ errors.confirmPassword }}</p>
                </div>
              </div>
            </div>

            <!-- Informations de l'organisation -->
            <div>
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                Informations de l'organisation
              </h3>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Nom de l'organisation <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.organizationName"
                  type="text"
                  required
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                  placeholder="Nom de votre agence immobilière"
                />
                <p v-if="errors.organizationName" class="mt-1 text-sm text-red-600">{{ errors.organizationName }}</p>
                <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                  Vous pourrez compléter les autres informations plus tard dans les paramètres
                </p>
              </div>
            </div>

            <!-- Conditions -->
            <div class="flex items-start">
              <input
                v-model="form.acceptTerms"
                type="checkbox"
                required
                class="mt-1 mr-3"
              />
              <label class="text-sm text-gray-600 dark:text-gray-400">
                J'accepte les <a href="/terms" target="_blank" class="text-blue-600 dark:text-blue-400 hover:underline">conditions générales</a>
                et la <a href="/privacy" target="_blank" class="text-blue-600 dark:text-blue-400 hover:underline">politique de confidentialité</a>
                <span class="text-red-500">*</span>
              </label>
            </div>

            <!-- Messages d'erreur/succès -->
            <div v-if="error" class="mb-4">
              <ErrorMessage
                :title="'Erreur d\'inscription'"
                :message="error"
                :show-retry="false"
              />
            </div>
            
            <div v-if="success" class="mb-4 p-4 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg">
              <div class="flex items-center gap-2">
                <svg class="w-5 h-5 text-green-600 dark:text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                <p class="text-green-800 dark:text-green-200 font-medium">
                  Inscription réussie ! Redirection vers l'interface d'administration...
                </p>
              </div>
            </div>

            <!-- Bouton de soumission -->
            <button
              type="submit"
              :disabled="submitting"
              class="w-full py-3 px-6 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-semibold text-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            >
              <span v-if="submitting">Inscription en cours...</span>
              <span v-else>Créer mon compte et m'abonner</span>
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { planService, authService } from '@viridial/shared'
import type { Plan } from '@viridial/shared'
import PlanCard from '@/components/PlanCard.vue'
import SkeletonLoader from '@/components/SkeletonLoader.vue'
import EmptyState from '@/components/EmptyState.vue'
import ErrorMessage from '@/components/ErrorMessage.vue'

const plans = ref<Plan[]>([])
const selectedPlan = ref<Plan | null>(null)
const showForm = ref(false)
const loadingPlans = ref(false)
const submitting = ref(false)
const error = ref<string | null>(null)
const success = ref(false)

const form = ref({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  confirmPassword: '',
  organizationName: '',
  acceptTerms: false
})

const errors = ref<Record<string, string>>({})

onMounted(async () => {
  await loadPlans()
})

async function loadPlans() {
  loadingPlans.value = true
  error.value = null
  try {
    // Récupérer tous les plans actifs
    const allPlans = await planService.getAll(true)
    plans.value = allPlans || []
    
    if (plans.value.length === 0) {
      console.warn('No active plans found')
      // Ne pas afficher d'erreur si c'est juste qu'il n'y a pas de plans
      // L'EmptyState s'affichera automatiquement
    }
  } catch (err: any) {
    console.error('Error loading plans:', err)
    const errorMessage = err.response?.data?.message || err.message || 'Erreur inconnue'
    error.value = `Impossible de charger les plans d'abonnement: ${errorMessage}`
    plans.value = []
  } finally {
    loadingPlans.value = false
  }
}

function handlePlanSelect(plan: Plan) {
  selectedPlan.value = plan
  showForm.value = true
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function validateForm(): boolean {
  errors.value = {}

  // Validation du prénom
  if (!form.value.firstName.trim()) {
    errors.value.firstName = 'Le prénom est requis'
  } else if (form.value.firstName.trim().length > 100) {
    errors.value.firstName = 'Le prénom ne peut pas dépasser 100 caractères'
  }

  // Validation du nom
  if (!form.value.lastName.trim()) {
    errors.value.lastName = 'Le nom est requis'
  } else if (form.value.lastName.trim().length > 100) {
    errors.value.lastName = 'Le nom ne peut pas dépasser 100 caractères'
  }

  // Validation de l'email
  if (!form.value.email.trim()) {
    errors.value.email = 'L\'email est requis'
  } else {
    const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
    if (!emailRegex.test(form.value.email.trim())) {
      errors.value.email = 'Le format de l\'email est invalide'
    } else if (form.value.email.trim().length > 255) {
      errors.value.email = 'L\'email ne peut pas dépasser 255 caractères'
    }
  }

  // Validation du mot de passe
  if (!form.value.password) {
    errors.value.password = 'Le mot de passe est requis'
  } else if (form.value.password.length < 8) {
    errors.value.password = 'Le mot de passe doit contenir au moins 8 caractères'
  } else if (form.value.password.length > 255) {
    errors.value.password = 'Le mot de passe ne peut pas dépasser 255 caractères'
  } else {
    // Validation de la force du mot de passe (au moins une majuscule, une minuscule et un chiffre)
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/
    if (!passwordRegex.test(form.value.password)) {
      errors.value.password = 'Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre'
    }
  }

  // Validation de la confirmation du mot de passe
  if (!form.value.confirmPassword) {
    errors.value.confirmPassword = 'Veuillez confirmer votre mot de passe'
  } else if (form.value.password !== form.value.confirmPassword) {
    errors.value.confirmPassword = 'Les mots de passe ne correspondent pas'
  }

  // Validation du nom d'organisation
  if (!form.value.organizationName.trim()) {
    errors.value.organizationName = 'Le nom de l\'organisation est requis'
  } else if (form.value.organizationName.trim().length < 2) {
    errors.value.organizationName = 'Le nom de l\'organisation doit contenir au moins 2 caractères'
  } else if (form.value.organizationName.trim().length > 255) {
    errors.value.organizationName = 'Le nom de l\'organisation ne peut pas dépasser 255 caractères'
  } else {
    // Validation des caractères autorisés
    const orgNameRegex = /^[A-Za-z0-9À-ÿ\s\-_.]{2,255}$/
    if (!orgNameRegex.test(form.value.organizationName.trim())) {
      errors.value.organizationName = 'Le nom de l\'organisation contient des caractères invalides. Utilisez uniquement des lettres, chiffres, espaces, tirets, points et underscores'
    }
  }

  if (!form.value.acceptTerms) {
    errors.value.acceptTerms = 'Vous devez accepter les conditions générales'
  }

  return Object.keys(errors.value).length === 0
}

async function handleSubmit() {
  if (!validateForm() || !selectedPlan.value) {
    return
  }

  submitting.value = true
  error.value = null

  try {
    // Appel unique au service d'inscription avec abonnement
    // Ce service crée automatiquement :
    // 1. L'utilisateur
    // 2. L'organisation
    // 3. L'association utilisateur-organisation (avec rôle ORGANIZATION_ADMIN)
    // 4. L'abonnement
    const subscribeData = {
      firstName: form.value.firstName,
      lastName: form.value.lastName,
      email: form.value.email,
      password: form.value.password,
      organizationName: form.value.organizationName,
      planId: selectedPlan.value.id
    }

    await authService.subscribe(subscribeData)

    // Afficher un message de succès
    error.value = null
    success.value = true

    // Rediriger vers l'admin après un court délai
    setTimeout(() => {
      // Rediriger vers l'admin (par défaut sur le port 3001)
      // Cette URL peut être configurée via une variable d'environnement VITE_ADMIN_URL
      const adminUrl = import.meta.env.VITE_ADMIN_URL || 'http://localhost:3001'
      window.location.href = `${adminUrl}/login?email=${encodeURIComponent(form.value.email)}`
    }, 2000)

  } catch (err: any) {
    console.error('Error during subscription:', err)
    
    // Gestion détaillée des erreurs
    let errorMessage = 'Une erreur est survenue lors de l\'inscription. Veuillez réessayer.'
    
    if (err.response?.data) {
      // Erreur structurée de l'API
      const errorData = err.response.data
      if (errorData.message) {
        errorMessage = errorData.message
      } else if (errorData.error && errorData.details) {
        // Erreur de validation avec détails
        errorMessage = `${errorData.error}: ${errorData.details.join(', ')}`
      } else if (errorData.error) {
        errorMessage = errorData.error
      }
    } else if (err.message) {
      errorMessage = err.message
    }
    
    error.value = errorMessage
  } finally {
    submitting.value = false
  }
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0
  }).format(price)
}
</script>

