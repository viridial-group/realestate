<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">Gestion DVF</h1>
        <p class="text-muted-foreground mt-1">
          Import et gestion des données de valeurs foncières (data.gouv.fr)
        </p>
      </div>
      <Button @click="showImportDialog = true" :disabled="importing">
        <Download class="mr-2 h-4 w-4" />
        Importer des données
      </Button>
    </div>

    <!-- Informations -->
    <Card>
      <CardHeader>
        <CardTitle>À propos des données DVF</CardTitle>
      </CardHeader>
      <CardContent class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <h4 class="font-semibold mb-2">Source</h4>
            <p class="text-sm text-muted-foreground">
              Les données proviennent de la Direction Générale des Finances Publiques (DGFiP)
              et sont disponibles sur <a href="https://www.data.gouv.fr/datasets/demandes-de-valeurs-foncieres/" 
              target="_blank" class="text-primary hover:underline">data.gouv.fr</a>.
            </p>
          </div>
          <div>
            <h4 class="font-semibold mb-2">Mise à jour</h4>
            <p class="text-sm text-muted-foreground">
              Les fichiers DVF sont mis à jour semestriellement (avril et octobre).
              La synchronisation automatique est configurée pour s'exécuter le 15 de ces mois.
            </p>
          </div>
          <div>
            <h4 class="font-semibold mb-2">Couverture</h4>
            <p class="text-sm text-muted-foreground">
              France métropolitaine et DOM-TOM (sauf Alsace, Moselle et Mayotte).
            </p>
          </div>
          <div>
            <h4 class="font-semibold mb-2">Licence</h4>
            <p class="text-sm text-muted-foreground">
              Licence Ouverte / Open Licence version 2.0
            </p>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Statistiques globales -->
    <Card>
      <CardHeader>
        <CardTitle>Statistiques Globales</CardTitle>
      </CardHeader>
      <CardContent>
        <div v-if="statsLoading" class="flex items-center justify-center py-8">
          <Loader2 class="h-6 w-6 animate-spin text-muted-foreground" />
        </div>
        <div v-else-if="stats" class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Total transactions</p>
            <p class="text-2xl font-bold">{{ stats.totalTransactions?.toLocaleString() || 0 }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Départements couverts</p>
            <p class="text-2xl font-bold">{{ stats.coveredDepartments || 0 }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Imports terminés</p>
            <p class="text-2xl font-bold">{{ stats.completedImports || 0 }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs text-muted-foreground">Imports en cours</p>
            <p class="text-2xl font-bold">{{ stats.inProgressImports || 0 }}</p>
          </div>
          <div v-if="stats.averagePricePerSquareMeter" class="space-y-1 md:col-span-2">
            <p class="text-xs text-muted-foreground">Prix moyen au m² (toutes zones)</p>
            <p class="text-xl font-bold">{{ formatPrice(stats.averagePricePerSquareMeter) }}</p>
          </div>
          <div v-if="stats.availableYears && stats.availableYears.length > 0" class="space-y-1 md:col-span-2">
            <p class="text-xs text-muted-foreground">Années disponibles</p>
            <div class="flex flex-wrap gap-2">
              <Badge v-for="year in stats.availableYears" :key="year" variant="secondary">
                {{ year }}
              </Badge>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Historique des imports -->
    <Card>
      <CardHeader>
        <CardTitle>Historique des imports</CardTitle>
      </CardHeader>
      <CardContent>
        <div v-if="loading" class="flex items-center justify-center py-12">
          <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
        <div v-else-if="importHistory.length === 0" class="text-center py-12 text-muted-foreground">
          <Database class="h-12 w-12 mx-auto mb-4 opacity-50" />
          <p>Aucun import effectué</p>
        </div>
        <div v-else class="space-y-4">
          <div
            v-for="(importItem, index) in importHistory"
            :key="importItem.id || index"
            class="flex items-center justify-between p-4 border rounded-lg"
          >
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-1">
                <Badge :variant="getStatusVariant(importItem.status)">
                  {{ importItem.status }}
                </Badge>
                <span class="font-semibold">
                  Année {{ importItem.year }} - Département {{ importItem.department }}
                </span>
              </div>
              <p class="text-sm text-muted-foreground">
                {{ importItem.transactionCount || 0 }} transactions importées
              </p>
              <p v-if="importItem.errorMessage" class="text-xs text-destructive mt-1">
                Erreur: {{ importItem.errorMessage }}
              </p>
              <p class="text-xs text-muted-foreground mt-1">
                {{ formatDate(importItem.createdAt) }}
              </p>
            </div>
            <div class="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                @click="cleanData(importItem.year, importItem.department)"
                :disabled="cleaning"
              >
                <Trash2 class="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Dialog d'import -->
    <Dialog :open="showImportDialog" @update:open="showImportDialog = $event">
      <DialogContent class="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Importer des données DVF</DialogTitle>
          <DialogDescription>
            Téléchargez et importez les données DVF pour une année et un département spécifiques.
          </DialogDescription>
        </DialogHeader>
        <div class="space-y-4 py-4">
          <div class="space-y-2">
            <Label for="year">Année</Label>
            <Input
              id="year"
              v-model="importForm.year"
              type="number"
              :min="2019"
              :max="new Date().getFullYear()"
              placeholder="2024"
            />
          </div>
          <div class="space-y-2">
            <Label for="department">Code département</Label>
            <Input
              id="department"
              v-model="importForm.department"
              placeholder="75 (Paris), 13 (Bouches-du-Rhône), etc."
              maxlength="3"
            />
            <p class="text-xs text-muted-foreground">
              Code INSEE du département (2 ou 3 chiffres)
            </p>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="showImportDialog = false" :disabled="importing">
            Annuler
          </Button>
          <Button @click="startImport" :disabled="importing || !isImportFormValid">
            <Loader2 v-if="importing" class="mr-2 h-4 w-4 animate-spin" />
            <Download v-else class="mr-2 h-4 w-4" />
            Importer
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { httpClient } from '@viridial/shared'
import type { DVFImportHistory, DVFStats } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from '@/components/ui/dialog'
import {
  Download,
  Loader2,
  Database,
  Trash2
} from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast/use-toast'

const { toast } = useToast()

const loading = ref(false)
const statsLoading = ref(false)
const importing = ref(false)
const cleaning = ref(false)
const showImportDialog = ref(false)
const importHistory = ref<DVFImportHistory[]>([])
const stats = ref<DVFStats | null>(null)

const importForm = ref({
  year: new Date().getFullYear().toString(),
  department: ''
})

const isImportFormValid = computed(() => {
  return importForm.value.year && 
         importForm.value.department && 
         importForm.value.department.length >= 2
})

const startImport = async () => {
  if (!isImportFormValid.value) return

  importing.value = true
  try {
    const response = await httpClient.post(
      `/api/admin/dvf/import/${importForm.value.year}/${importForm.value.department}`
    )
    
    toast({
      title: 'Import démarré',
      description: 'L\'import des données DVF a été démarré en arrière-plan.',
    })

    // Ajouter à l'historique
    importHistory.value.unshift({
      year: importForm.value.year,
      department: importForm.value.department,
      status: 'EN_COURS',
      date: new Date().toISOString()
    })

    showImportDialog.value = false
    importForm.value.department = ''
  } catch (error: any) {
    console.error('Error starting import:', error)
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de démarrer l\'import',
      variant: 'destructive'
    })
  } finally {
    importing.value = false
  }
}

const cleanData = async (year: string, department: string) => {
  if (!confirm(`Êtes-vous sûr de vouloir supprimer les données DVF pour l'année ${year} et le département ${department} ?`)) {
    return
  }

  cleaning.value = true
  try {
    await httpClient.delete(`/api/admin/dvf/clean/${year}`)
    
    toast({
      title: 'Succès',
      description: 'Les données ont été supprimées.',
    })

    // Retirer de l'historique
    importHistory.value = importHistory.value.filter(
      (item: DVFImportHistory) => !(item.year === year && item.department === department)
    )
  } catch (error: any) {
    console.error('Error cleaning data:', error)
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de supprimer les données',
      variant: 'destructive'
    })
  } finally {
    cleaning.value = false
  }
}

const getStatusVariant = (status: string): 'default' | 'secondary' | 'destructive' | 'outline' => {
  switch (status) {
    case 'TERMINÉ':
      return 'default'
    case 'EN_COURS':
      return 'secondary'
    case 'ERREUR':
      return 'destructive'
    default:
      return 'secondary'
  }
}

const formatDate = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Recharger après un import
watch(showImportDialog, (newVal: boolean) => {
  if (!newVal) {
    loadHistory()
    loadStats()
  }
})

// Charger l'historique
const loadHistory = async () => {
  loading.value = true
  try {
    const response = await httpClient.get('/api/admin/dvf/history?page=0&size=50')
    // La réponse est une Page avec content
    const data = response.data as any
    if (data && data.content) {
      importHistory.value = data.content as DVFImportHistory[]
    } else if (Array.isArray(data)) {
      importHistory.value = data as DVFImportHistory[]
    } else {
      importHistory.value = []
    }
  } catch (error: any) {
    console.error('Error loading history:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger l\'historique des imports',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

// Charger les statistiques
const loadStats = async () => {
  statsLoading.value = true
  try {
    const response = await httpClient.get('/api/admin/dvf/stats')
    stats.value = response.data as DVFStats
  } catch (error: any) {
    console.error('Error loading stats:', error)
  } finally {
    statsLoading.value = false
  }
}

const formatPrice = (price?: number): string => {
  if (price === undefined || price === null) return 'N/A'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price)
}

onMounted(() => {
  loadHistory()
  loadStats()
})
</script>

