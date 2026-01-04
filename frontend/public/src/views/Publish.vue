<template>
  <div class="min-h-screen bg-gray-50 py-12">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">
          Publiez votre annonce immobilière
        </h1>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          Mettez en ligne votre bien immobilier et touchez des milliers de prospects intéressés
        </p>
      </div>

      <!-- Contenu selon l'état d'authentification -->
      <div v-if="!isAuthenticated" class="bg-white rounded-lg shadow-sm p-8 text-center">
        <Home class="h-16 w-16 text-gray-400 mx-auto mb-4" />
        <h2 class="text-2xl font-semibold text-gray-900 mb-4">
          Connectez-vous pour publier une annonce
        </h2>
        <p class="text-gray-600 mb-6">
          Créez un compte gratuitement ou connectez-vous pour commencer à publier vos annonces immobilières.
        </p>
        <div class="flex items-center justify-center gap-4">
          <router-link
            :to="{ name: 'Login', query: { redirect: '/my-properties/new' } }"
            class="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors font-medium"
          >
            Se connecter
          </router-link>
          <router-link
            :to="{ name: 'Login', query: { redirect: '/my-properties/new' } }"
            class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors font-medium"
          >
            Créer un compte
          </router-link>
        </div>
      </div>

      <div v-else class="space-y-6">
        <!-- CTA pour créer une annonce -->
        <div class="bg-gradient-to-r from-blue-600 to-blue-700 rounded-lg shadow-lg p-8 text-white">
          <h2 class="text-2xl font-bold mb-4">
            Prêt à publier votre première annonce ?
          </h2>
          <p class="text-blue-100 mb-6">
            Créez votre annonce en quelques minutes et commencez à recevoir des contacts qualifiés.
          </p>
          <router-link
            to="/my-properties/new"
            class="inline-flex items-center gap-2 px-6 py-3 bg-white text-blue-600 rounded-md hover:bg-blue-50 transition-colors font-medium shadow-sm"
          >
            <Plus class="h-5 w-5" />
            Créer une nouvelle annonce
          </router-link>
        </div>

        <!-- Mes annonces existantes -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-xl font-semibold text-gray-900">Mes annonces</h2>
            <router-link
              to="/my-properties"
              class="text-sm text-blue-600 hover:text-blue-700 font-medium"
            >
              Voir toutes mes annonces →
            </router-link>
          </div>

          <div v-if="loadingProperties" class="text-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p class="mt-2 text-sm text-gray-600">Chargement...</p>
          </div>

          <div v-else-if="recentProperties.length === 0" class="text-center py-8">
            <p class="text-gray-600 mb-4">Vous n'avez pas encore d'annonces</p>
            <router-link
              to="/my-properties/new"
              class="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              <Plus class="h-5 w-5" />
              Créer ma première annonce
            </router-link>
          </div>

          <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div
              v-for="property in recentProperties"
              :key="property.id"
              class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h3 class="font-semibold text-gray-900 mb-1">{{ property.title }}</h3>
                  <p class="text-sm text-gray-600 mb-2 line-clamp-2">{{ property.description }}</p>
                  <div class="flex items-center gap-4 text-sm text-gray-600">
                    <span class="font-semibold text-gray-900">{{ formatPrice(property.price) }}</span>
                    <span v-if="property.city">{{ property.city }}</span>
                  </div>
                </div>
                <router-link
                  :to="`/my-properties/${property.id}/edit`"
                  class="ml-4 text-blue-600 hover:text-blue-700"
                >
                  <Edit class="h-5 w-5" />
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- Guide rapide -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-xl font-semibold text-gray-900 mb-4">Comment publier une annonce ?</h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="text-center">
              <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
                <span class="text-blue-600 font-bold text-lg">1</span>
              </div>
              <h3 class="font-semibold text-gray-900 mb-2">Remplissez les informations</h3>
              <p class="text-sm text-gray-600">
                Décrivez votre bien : type, prix, localisation, caractéristiques
              </p>
            </div>
            <div class="text-center">
              <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
                <span class="text-blue-600 font-bold text-lg">2</span>
              </div>
              <h3 class="font-semibold text-gray-900 mb-2">Ajoutez des photos</h3>
              <p class="text-sm text-gray-600">
                Téléchargez jusqu'à 10 photos pour mettre en valeur votre bien
              </p>
            </div>
            <div class="text-center">
              <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
                <span class="text-blue-600 font-bold text-lg">3</span>
              </div>
              <h3 class="font-semibold text-gray-900 mb-2">Publiez</h3>
              <p class="text-sm text-gray-600">
                Vérifiez votre annonce et publiez-la pour qu'elle soit visible
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Home, Plus, Edit } from 'lucide-vue-next'
import { useAuthStore } from '@viridial/shared'
import { userPropertyService } from '@/api/user-property.service'
import type { Property } from '@viridial/shared'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated)
const loadingProperties = ref(false)
const recentProperties = ref<Property[]>([])

onMounted(async () => {
  if (isAuthenticated.value) {
    await loadRecentProperties()
  }
})

async function loadRecentProperties() {
  loadingProperties.value = true
  try {
    const response = await userPropertyService.getMyProperties({ page: 0, size: 4 })
    recentProperties.value = response.content || []
  } catch (err) {
    console.error('Error loading recent properties:', err)
  } finally {
    loadingProperties.value = false
  }
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
  }).format(price)
}
</script>
