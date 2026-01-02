<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2.5xl font-bold">Documents</h1>
        <p class="text-muted-foreground mt-1">Gérez tous les documents et fichiers</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          Actualiser
        </Button>
        <Button @click="router.push('/documents/upload')" size="lg">
          <Upload class="mr-2 h-4 w-4" />
          Uploader un document
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Total</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ documents.length }}</div>
          <p class="text-xs text-muted-foreground mt-1">Documents</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Images</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-primary">{{ imageCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">Fichiers</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>PDFs</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-destructive">{{ pdfCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">Fichiers</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Taille totale</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ formatFileSize(totalSize) }}</div>
          <p class="text-xs text-muted-foreground mt-1">Stockage</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Nom, description..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Type</Label>
            <Select v-model="selectedType" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous</SelectItem>
                <SelectItem value="IMAGE">Image</SelectItem>
                <SelectItem value="PDF">PDF</SelectItem>
                <SelectItem value="DOCUMENT">Document</SelectItem>
                <SelectItem value="VIDEO">Vidéo</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Organisation</Label>
            <Select v-model="selectedOrganizationId" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Toutes" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Toutes</SelectItem>
                <SelectItem v-for="org in organizations" :key="org.id" :value="String(org.id)">
                  {{ org.name }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex items-end">
            <Button variant="outline" @click="resetFilters" class="w-full">
              <X class="mr-2 h-4 w-4" />
              Réinitialiser
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des Documents -->
    <Card>
      <CardHeader>
        <CardTitle>Documents</CardTitle>
        <CardDescription>Liste de tous les documents uploadés</CardDescription>
      </CardHeader>
      <CardContent class="p-0">
        <div v-if="loading" class="flex items-center justify-center py-12">
          <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Document</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Type</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Taille</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Organisation</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Date</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-muted-foreground uppercase">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-border">
              <tr
                v-for="document in filteredDocuments"
                :key="document.id"
                class="hover:bg-muted/50 transition-colors"
              >
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div
                      v-if="isImage(document)"
                      class="w-10 h-10 rounded overflow-hidden bg-muted flex items-center justify-center"
                    >
                      <img
                        :src="documentService.getViewUrl(document.id)"
                        :alt="document.name"
                        class="w-full h-full object-cover"
                        @error="handleImageError"
                      />
                    </div>
                    <FileText v-else class="h-10 w-10 text-muted-foreground" />
                    <div>
                      <div class="text-sm font-medium">{{ document.name }}</div>
                      <div v-if="document.description" class="text-sm text-muted-foreground">
                        {{ document.description }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge>{{ document.type }}</Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatFileSize(document.fileSize) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ getOrganizationName(document.organizationId) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatDate(document.createdAt) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right" @click.stop>
                  <DropdownMenu>
                    <DropdownMenuTrigger as-child>
                      <Button variant="ghost" size="sm">
                        <MoreVertical class="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem @click.stop="previewDocument(document)">
                        <Eye class="mr-2 h-4 w-4" />
                        Prévisualiser
                      </DropdownMenuItem>
                      <DropdownMenuItem @click.stop="downloadDocument(document.id)">
                        <Download class="mr-2 h-4 w-4" />
                        Télécharger
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        @click.stop="deleteDocument(document.id)"
                        class="text-destructive"
                      >
                        <Trash2 class="mr-2 h-4 w-4" />
                        Supprimer
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="filteredDocuments.length === 0" class="p-8 text-center text-muted-foreground">
            Aucun document trouvé
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { documentService, organizationService, useAuthStore, type Document, type Organization } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { RefreshCw, Upload, X, MoreVertical, Eye, Download, Trash2, FileText, Loader2 } from 'lucide-vue-next'

const { toast } = useToast()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const searchQuery = ref('')
const selectedType = ref('all')
const selectedOrganizationId = ref('all')

const documents = ref<Document[]>([])
const organizations = ref<Organization[]>([])
const organizationsMap = ref<Record<number, string>>({})

const filteredDocuments = computed(() => {
  let filtered = [...documents.value]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (d) =>
        d.name?.toLowerCase().includes(query) ||
        d.description?.toLowerCase().includes(query)
    )
  }

  if (selectedType.value && selectedType.value !== 'all') {
    filtered = filtered.filter((d) => d.type === selectedType.value)
  }

  if (selectedOrganizationId.value && selectedOrganizationId.value !== 'all') {
    filtered = filtered.filter(
      (d) => d.organizationId === Number(selectedOrganizationId.value)
    )
  }

  // Sort by date (newest first)
  filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())

  return filtered
})

const imageCount = computed(() => 
  documents.value.filter((d) => d.type === 'IMAGE' || d.mimeType?.startsWith('image/')).length
)

const pdfCount = computed(() => 
  documents.value.filter((d) => d.type === 'PDF' || d.mimeType === 'application/pdf').length
)

const totalSize = computed(() => 
  documents.value.reduce((sum, d) => sum + (d.fileSize || 0), 0)
)

const isImage = (document: Document): boolean => {
  return document.type === 'IMAGE' || document.mimeType?.startsWith('image/') || false
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

const getOrganizationName = (organizationId: number): string => {
  return organizationsMap.value[organizationId] || `Organisation #${organizationId}`
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const handleSearch = () => {
  // Filtering is handled by computed property
}

const handleFilter = () => {
  // Filtering is handled by computed property
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = 'all'
  selectedOrganizationId.value = 'all'
}

const refreshData = () => {
  loadDocuments()
  loadOrganizations()
}

const previewDocument = async (document: Document) => {
  if (isImage(document)) {
    // Open image in new window
    window.open(documentService.getViewUrl(document.id), '_blank')
  } else {
    // For other types, download or show info
    toast({
      title: 'Prévisualisation',
      description: `Prévisualisation non disponible pour les fichiers ${document.type}. Téléchargez le fichier pour l'ouvrir.`
    })
  }
}

const downloadDocument = async (id: number) => {
  try {
    const blob = await documentService.download(id)
    const doc = documents.value.find(d => d.id === id)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = doc?.name || `document-${id}`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    toast({
      title: 'Téléchargement réussi',
      description: 'Le document a été téléchargé'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de télécharger le document',
      variant: 'destructive'
    })
  }
}

const deleteDocument = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer ce document ?')) {
    return
  }

  loading.value = true
  try {
    await documentService.delete(id)
    documents.value = documents.value.filter((d) => d.id !== id)
    toast({
      title: 'Document supprimé',
      description: 'Le document a été supprimé avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadOrganizations = async () => {
  try {
    const result = await organizationService.getAll()
    if (result.organizations && Array.isArray(result.organizations)) {
      organizations.value = result.organizations
      const map: Record<number, string> = {}
      result.organizations.forEach(org => {
        map[org.id] = org.name
      })
      organizationsMap.value = map
    }
  } catch (error) {
    console.error('Error loading organizations:', error)
  }
}

const loadDocuments = async () => {
  loading.value = true
  try {
    // Load documents for all organizations the user has access to
    // For now, we'll load documents for the user's organization
    if (authStore.user?.organizationId) {
      const docs = await documentService.getByOrganizationId(authStore.user.organizationId)
      documents.value = docs
    } else if (organizations.value.length > 0) {
      // Load documents for all organizations
      const allDocs: Document[] = []
      for (const org of organizations.value) {
        try {
          const docs = await documentService.getByOrganizationId(org.id)
          allDocs.push(...docs)
        } catch (error) {
          console.error(`Error loading documents for organization ${org.id}:`, error)
        }
      }
      documents.value = allDocs
    } else {
      documents.value = []
    }
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les documents',
      variant: 'destructive'
    })
    documents.value = []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadOrganizations()
  await loadDocuments()
})
</script>

