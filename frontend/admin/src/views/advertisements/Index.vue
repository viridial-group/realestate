<template>
  <div class="p-6 space-y-6">
    <!-- En-tête -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Annonces publicitaires</h1>
        <p class="text-gray-600 dark:text-gray-400 mt-1">Gérez vos campagnes publicitaires</p>
      </div>
      <div class="flex items-center gap-3">
        <router-link
          to="/advertisements/analytics"
          class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors font-medium flex items-center gap-2"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          Analytics
        </router-link>
        <button
          @click="showCreateModal = true"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
        >
          + Créer une annonce
        </button>
      </div>
    </div>

    <!-- Filtres -->
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Statut</label>
          <select
            v-model="filters.status"
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
          >
            <option value="">Tous</option>
            <option value="DRAFT">Brouillon</option>
            <option value="ACTIVE">Actif</option>
            <option value="PAUSED">En pause</option>
            <option value="EXPIRED">Expiré</option>
            <option value="ARCHIVED">Archivé</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Type</label>
          <select
            v-model="filters.adType"
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
          >
            <option value="">Tous</option>
            <option value="BANNER">Bannière</option>
            <option value="SIDEBAR">Barre latérale</option>
            <option value="INLINE">Inline</option>
            <option value="POPUP">Popup</option>
            <option value="SPONSORED_PROPERTY">Propriété sponsorisée</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Position</label>
          <select
            v-model="filters.position"
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
          >
            <option value="">Toutes</option>
            <option value="TOP">Haut</option>
            <option value="BOTTOM">Bas</option>
            <option value="SIDEBAR_LEFT">Barre latérale gauche</option>
            <option value="SIDEBAR_RIGHT">Barre latérale droite</option>
            <option value="INLINE">Inline</option>
            <option value="HEADER">En-tête</option>
            <option value="FOOTER">Pied de page</option>
          </select>
        </div>
        <div class="flex items-end">
          <button
            @click="loadAdvertisements"
            class="w-full px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
          >
            Filtrer
          </button>
        </div>
      </div>
    </div>

    <!-- Statistiques -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4">
        <div class="text-sm text-gray-600 dark:text-gray-400">Total annonces</div>
        <div class="text-2xl font-bold text-gray-900 dark:text-white">{{ advertisements.length }}</div>
      </div>
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4">
        <div class="text-sm text-gray-600 dark:text-gray-400">Actives</div>
        <div class="text-2xl font-bold text-green-600">{{ activeCount }}</div>
      </div>
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4">
        <div class="text-sm text-gray-600 dark:text-gray-400">Total impressions</div>
        <div class="text-2xl font-bold text-blue-600">{{ totalImpressions.toLocaleString() }}</div>
      </div>
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4">
        <div class="text-sm text-gray-600 dark:text-gray-400">Total clics</div>
        <div class="text-2xl font-bold text-purple-600">{{ totalClicks.toLocaleString() }}</div>
      </div>
    </div>

    <!-- Liste des annonces -->
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm overflow-hidden">
      <div v-if="loading" class="p-8 text-center">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
      <div v-else-if="advertisements.length === 0" class="p-8 text-center text-gray-500">
        Aucune annonce trouvée
      </div>
      <table v-else class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
        <thead class="bg-gray-50 dark:bg-gray-700">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Titre</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Type / Position</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Statut</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Performance</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Budget</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
          <tr v-for="ad in advertisements" :key="ad.id" class="hover:bg-gray-50 dark:hover:bg-gray-700">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <img
                  v-if="ad.imageUrl"
                  :src="ad.imageUrl"
                  :alt="ad.title"
                  class="h-12 w-12 rounded-lg object-cover mr-3"
                />
                <div>
                  <div class="text-sm font-medium text-gray-900 dark:text-white">{{ ad.title }}</div>
                  <div class="text-xs text-gray-500 dark:text-gray-400">{{ formatDate(ad.createdAt) }}</div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900 dark:text-white">{{ ad.adType }}</div>
              <div class="text-xs text-gray-500 dark:text-gray-400">{{ ad.position }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="getStatusClass(ad.status)"
                class="px-2 py-1 text-xs font-semibold rounded-full"
              >
                {{ getStatusLabel(ad.status) }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900 dark:text-white">
                <div>Impressions: {{ ad.impressions.toLocaleString() }}</div>
                <div>Clics: {{ ad.clicks.toLocaleString() }}</div>
                <div v-if="ad.clickThroughRate" class="text-xs text-gray-500 dark:text-gray-400">
                  CTR: {{ ad.clickThroughRate.toFixed(2) }}%
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900 dark:text-white">
                <div v-if="ad.budget">Budget: €{{ ad.budget.toLocaleString() }}</div>
                <div class="text-xs text-gray-500 dark:text-gray-400">
                  Dépensé: €{{ ad.totalSpent.toLocaleString() }}
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
              <div class="flex items-center gap-2">
                <button
                  @click="viewStats(ad.id)"
                  class="text-blue-600 hover:text-blue-900 dark:text-blue-400"
                  title="Statistiques"
                >
                  📊
                </button>
                <button
                  @click="editAdvertisement(ad)"
                  class="text-indigo-600 hover:text-indigo-900 dark:text-indigo-400"
                  title="Modifier"
                >
                  ✏️
                </button>
                <button
                  v-if="ad.status === 'ACTIVE'"
                  @click="updateAdStatus(ad.id, 'PAUSED')"
                  class="text-yellow-600 hover:text-yellow-900 dark:text-yellow-400"
                  title="Mettre en pause"
                >
                  ⏸️
                </button>
                <button
                  v-else-if="ad.status === 'PAUSED' || ad.status === 'DRAFT'"
                  @click="updateAdStatus(ad.id, 'ACTIVE')"
                  class="text-green-600 hover:text-green-900 dark:text-green-400"
                  title="Activer"
                >
                  ▶️
                </button>
                <button
                  @click="deleteAdvertisement(ad.id)"
                  class="text-red-600 hover:text-red-900 dark:text-red-400"
                  title="Supprimer"
                >
                  🗑️
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal de création/édition -->
    <AdvertisementForm
      v-if="showCreateModal || editingAd"
      :advertisement="editingAd"
      @close="closeModal"
      @saved="handleSaved"
    />

    <!-- Modal de statistiques -->
    <AdvertisementStatsModal
      v-if="selectedAdId"
      :advertisement-id="selectedAdId"
      @close="selectedAdId = null"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { advertisementService, type Advertisement } from '@viridial/shared'
import AdvertisementForm from './AdvertisementForm.vue'
import AdvertisementStatsModal from './AdvertisementStatsModal.vue'

const advertisements = ref<Advertisement[]>([])
const loading = ref(false)
const showCreateModal = ref(false)
const editingAd = ref<Advertisement | null>(null)
const selectedAdId = ref<number | null>(null)

const filters = ref({
  status: '',
  adType: '',
  position: ''
})

const activeCount = computed(() => {
  return advertisements.value.filter(ad => ad.status === 'ACTIVE').length
})

const totalImpressions = computed(() => {
  return advertisements.value.reduce((sum, ad) => sum + ad.impressions, 0)
})

const totalClicks = computed(() => {
  return advertisements.value.reduce((sum, ad) => sum + ad.clicks, 0)
})

onMounted(() => {
  loadAdvertisements()
})

async function loadAdvertisements() {
  loading.value = true
  try {
    const params: any = {}
    if (filters.value.status) params.status = filters.value.status
    if (filters.value.adType) params.adType = filters.value.adType
    if (filters.value.position) params.position = filters.value.position
    
    advertisements.value = await advertisementService.getAll(params)
  } catch (error: any) {
    console.error('Error loading advertisements:', error)
    alert('Erreur lors du chargement des annonces')
  } finally {
    loading.value = false
  }
}

function editAdvertisement(ad: Advertisement) {
  editingAd.value = ad
  showCreateModal.value = true
}

function closeModal() {
  showCreateModal.value = false
  editingAd.value = null
}

async function handleSaved() {
  closeModal()
  await loadAdvertisements()
}

async function updateAdStatus(id: number, status: string) {
  try {
    await advertisementService.updateStatus(id, status)
    await loadAdvertisements()
  } catch (error: any) {
    console.error('Error updating status:', error)
    alert('Erreur lors de la mise à jour du statut')
  }
}

async function deleteAdvertisement(id: number) {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette annonce ?')) {
    return
  }
  try {
    await advertisementService.delete(id)
    await loadAdvertisements()
  } catch (error: any) {
    console.error('Error deleting advertisement:', error)
    alert('Erreur lors de la suppression')
  }
}

function viewStats(id: number) {
  selectedAdId.value = id
}

function getStatusClass(status: string): string {
  const classes: Record<string, string> = {
    DRAFT: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
    ACTIVE: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
    PAUSED: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
    EXPIRED: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
    ARCHIVED: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
  return classes[status] || classes.DRAFT
}

function getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    DRAFT: 'Brouillon',
    ACTIVE: 'Actif',
    PAUSED: 'En pause',
    EXPIRED: 'Expiré',
    ARCHIVED: 'Archivé'
  }
  return labels[status] || status
}

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>

