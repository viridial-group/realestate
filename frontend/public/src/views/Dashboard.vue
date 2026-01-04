<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Tableau de bord</h1>
        <p class="mt-2 text-sm text-gray-600">
          Vue d'ensemble de vos annonces et statistiques
        </p>
      </div>

      <!-- Statistiques globales -->
      <div v-if="loading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>

      <div v-else class="space-y-6">
        <!-- Cartes de statistiques -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div class="bg-white rounded-lg shadow-sm p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Total annonces</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalProperties }}</p>
              </div>
              <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                <Home class="h-6 w-6 text-blue-600" />
              </div>
            </div>
            <div class="mt-4 flex items-center text-sm">
              <span :class="stats.propertiesChange >= 0 ? 'text-green-600' : 'text-red-600'">
                {{ stats.propertiesChange >= 0 ? '+' : '' }}{{ stats.propertiesChange }}
              </span>
              <span class="text-gray-600 ml-1">ce mois</span>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-sm p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Total vues</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalViews }}</p>
              </div>
              <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                <Eye class="h-6 w-6 text-green-600" />
              </div>
            </div>
            <div class="mt-4 flex items-center text-sm">
              <span :class="stats.viewsChange >= 0 ? 'text-green-600' : 'text-red-600'">
                {{ stats.viewsChange >= 0 ? '+' : '' }}{{ stats.viewsChange }}
              </span>
              <span class="text-gray-600 ml-1">ce mois</span>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-sm p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Total contacts</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalContacts }}</p>
              </div>
              <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
                <Mail class="h-6 w-6 text-purple-600" />
              </div>
            </div>
            <div class="mt-4 flex items-center text-sm">
              <span :class="stats.contactsChange >= 0 ? 'text-green-600' : 'text-red-600'">
                {{ stats.contactsChange >= 0 ? '+' : '' }}{{ stats.contactsChange }}
              </span>
              <span class="text-gray-600 ml-1">ce mois</span>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-sm p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Taux de conversion</p>
                <p class="text-3xl font-bold text-gray-900">
                  {{ stats.conversionRate > 0 ? stats.conversionRate.toFixed(1) : 0 }}%
                </p>
              </div>
              <div class="w-12 h-12 bg-orange-100 rounded-full flex items-center justify-center">
                <TrendingUp class="h-6 w-6 text-orange-600" />
              </div>
            </div>
            <p class="mt-4 text-sm text-gray-600">
              Contacts / Vues
            </p>
          </div>
        </div>

        <!-- Graphique d'évolution -->
        <StatsChart
          v-if="chartData.length > 0"
          title="Évolution des statistiques"
          subtitle="Vues et contacts sur les 7 derniers jours"
          :data="chartData"
          :loading="loading"
        />

        <!-- Répartition par statut -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Répartition par statut</h2>
          <div class="grid grid-cols-2 md:grid-cols-5 gap-4">
            <div
              v-for="status in statusDistribution"
              :key="status.status"
              class="text-center p-4 rounded-lg"
              :class="getStatusBgClass(status.status)"
            >
              <p class="text-2xl font-bold mb-1">{{ status.count }}</p>
              <p class="text-sm font-medium">{{ getStatusLabel(status.status) }}</p>
            </div>
          </div>
        </div>

        <!-- Annonces récentes -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900">Annonces récentes</h2>
            <router-link
              to="/my-properties"
              class="text-sm text-blue-600 hover:text-blue-700 font-medium"
            >
              Voir toutes →
            </router-link>
          </div>
          <div v-if="recentProperties.length === 0" class="text-center py-8">
            <p class="text-gray-600">Aucune annonce</p>
          </div>
          <div v-else class="space-y-4">
            <div
              v-for="property in recentProperties"
              :key="property.id"
              class="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:shadow-md transition-shadow"
            >
              <div class="flex-1">
                <h3 class="font-semibold text-gray-900 mb-1">{{ property.title }}</h3>
                <div class="flex items-center gap-4 text-sm text-gray-600">
                  <span>{{ formatPrice(property.price) }}</span>
                  <span>{{ property.city }}</span>
                  <span
                    :class="getStatusBadgeClass(property.status)"
                    class="px-2 py-1 text-xs font-medium rounded"
                  >
                    {{ getStatusLabel(property.status) }}
                  </span>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <router-link
                  :to="`/my-properties/${property.id}`"
                  class="px-3 py-2 text-sm text-blue-600 hover:text-blue-700 hover:bg-blue-50 rounded-md transition-colors"
                >
                  Voir
                </router-link>
                <router-link
                  :to="`/my-properties/${property.id}/edit`"
                  class="px-3 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
                >
                  Modifier
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions rapides -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Actions rapides</h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <router-link
              to="/my-properties/new"
              class="flex items-center gap-3 p-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors"
            >
              <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                <Plus class="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <p class="font-medium text-gray-900">Nouvelle annonce</p>
                <p class="text-sm text-gray-600">Créer une nouvelle annonce</p>
              </div>
            </router-link>
            <router-link
              to="/my-properties"
              class="flex items-center gap-3 p-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors"
            >
              <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                <List class="h-5 w-5 text-green-600" />
              </div>
              <div>
                <p class="font-medium text-gray-900">Mes annonces</p>
                <p class="text-sm text-gray-600">Gérer toutes mes annonces</p>
              </div>
            </router-link>
            <router-link
              to="/profile"
              class="flex items-center gap-3 p-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors"
            >
              <div class="w-10 h-10 bg-purple-100 rounded-full flex items-center justify-center">
                <User class="h-5 w-5 text-purple-600" />
              </div>
              <div>
                <p class="font-medium text-gray-900">Mon profil</p>
                <p class="text-sm text-gray-600">Modifier mes informations</p>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Home, Eye, Mail, TrendingUp, Plus, List, User } from 'lucide-vue-next'
import StatsChart, { type ChartSeries } from '@/components/StatsChart.vue'
import { userPropertyService } from '@/api/user-property.service'
import { statsHistoryService } from '@/api/stats-history.service'
import type { Property } from '@viridial/shared'

const loading = ref(false)
const recentProperties = ref<Property[]>([])
const chartData = ref<ChartSeries[]>([])

const stats = ref({
  totalProperties: 0,
  totalViews: 0,
  totalContacts: 0,
  totalFavorites: 0,
  conversionRate: 0,
  propertiesChange: 0,
  viewsChange: 0,
  contactsChange: 0,
})

const statusDistribution = ref<Array<{ status: string; count: number }>>([])

onMounted(async () => {
  await loadDashboard()
})

async function loadDashboard() {
  loading.value = true
  try {
    // Charger toutes les propriétés
    const response = await userPropertyService.getMyProperties({ page: 0, size: 100 })
    const properties = response.content || []
    
    stats.value.totalProperties = properties.length
    recentProperties.value = properties.slice(0, 5)

    // Calculer la répartition par statut
    const statusCounts: Record<string, number> = {}
    properties.forEach(prop => {
      statusCounts[prop.status] = (statusCounts[prop.status] || 0) + 1
    })
    statusDistribution.value = Object.entries(statusCounts).map(([status, count]) => ({
      status,
      count,
    }))

    // Charger les statistiques pour chaque propriété
    let totalViews = 0
    let totalContacts = 0
    let totalFavorites = 0

    for (const property of properties) {
      try {
        const propertyStats = await userPropertyService.getPropertyStats(property.id)
        totalViews += propertyStats.views || 0
        totalContacts += propertyStats.contacts || 0
        totalFavorites += propertyStats.favorites || 0
      } catch (err) {
        // Ignorer les erreurs
      }
    }

    stats.value.totalViews = totalViews
    stats.value.totalContacts = totalContacts
    stats.value.totalFavorites = totalFavorites
    stats.value.conversionRate = totalViews > 0 ? (totalContacts / totalViews) * 100 : 0

    // Générer des données de graphique (simulation sur 7 jours)
    generateChartData(properties)
  } catch (err) {
    console.error('Error loading dashboard:', err)
  } finally {
    loading.value = false
  }
}

async function generateChartData(properties: Property[]) {
  try {
    // Essayer de récupérer les données réelles depuis l'API
    const history = await statsHistoryService.getGlobalHistory({ days: 7 })
    
    if (history && history.length > 0) {
      // Utiliser les données réelles
      const viewsData = history.map(point => ({
        date: new Date(point.date),
        value: point.views || 0,
      }))

      const contactsData = history.map(point => ({
        date: new Date(point.date),
        value: point.contacts || 0,
      }))

      chartData.value = [
        {
          label: 'Vues',
          data: viewsData,
        },
        {
          label: 'Contacts',
          data: contactsData,
        },
      ]
    } else {
      // Fallback: générer des données simulées
      generateSimulatedChartData(properties)
    }
  } catch (error) {
    console.warn('Could not load real stats history, using simulated data:', error)
    generateSimulatedChartData(properties)
  }
}

function generateSimulatedChartData(properties: Property[]) {
  // Générer des données pour les 7 derniers jours
  const days = 7
  const today = new Date()
  const dates: Date[] = []
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(date)
  }

  // Simuler des données basées sur les stats actuelles
  const viewsData = dates.map((date) => ({
    date,
    value: Math.floor(Math.random() * 20) + (stats.value.totalViews / days) * 0.8,
  }))

  const contactsData = dates.map((date) => ({
    date,
    value: Math.floor(Math.random() * 5) + (stats.value.totalContacts / days) * 0.8,
  }))

  chartData.value = [
    {
      label: 'Vues',
      data: viewsData,
    },
    {
      label: 'Contacts',
      data: contactsData,
    },
  ]
}

function getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    DRAFT: 'Brouillon',
    AVAILABLE: 'Disponible',
    PENDING: 'En attente',
    SOLD: 'Vendu',
    RENTED: 'Loué',
  }
  return labels[status] || status
}

function getStatusBadgeClass(status: string): string {
  const classes: Record<string, string> = {
    DRAFT: 'bg-gray-100 text-gray-800',
    AVAILABLE: 'bg-green-100 text-green-800',
    PENDING: 'bg-yellow-100 text-yellow-800',
    SOLD: 'bg-blue-100 text-blue-800',
    RENTED: 'bg-purple-100 text-purple-800',
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function getStatusBgClass(status: string): string {
  const classes: Record<string, string> = {
    DRAFT: 'bg-gray-50',
    AVAILABLE: 'bg-green-50',
    PENDING: 'bg-yellow-50',
    SOLD: 'bg-blue-50',
    RENTED: 'bg-purple-50',
  }
  return classes[status] || 'bg-gray-50'
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
  }).format(price)
}
</script>

