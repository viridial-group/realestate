<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="icon" @click="goBack">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">Uploader un document</h1>
          <p class="text-muted-foreground mt-1">Ajoutez un nouveau document ou fichier</p>
        </div>
      </div>
    </div>

    <Card>
      <CardHeader>
        <CardTitle>Informations du document</CardTitle>
        <CardDescription>Remplissez les informations et sélectionnez le fichier à uploader</CardDescription>
      </CardHeader>
      <CardContent class="space-y-6">
        <!-- Formulaire d'upload -->
        <div class="space-y-4">
          <div class="space-y-2">
            <Label for="file">Fichier *</Label>
            <div
              class="border-2 border-dashed rounded-lg p-8 text-center cursor-pointer hover:border-primary transition-colors"
              :class="file ? 'border-primary bg-primary/5' : 'border-muted'"
              @click="fileInput?.click()"
              @dragover.prevent
              @drop.prevent="handleDrop"
            >
              <input
                ref="fileInput"
                type="file"
                class="hidden"
                @change="handleFileSelect"
                accept="*/*"
              />
              <Upload class="mx-auto h-12 w-12 text-muted-foreground mb-4" />
              <p class="text-sm font-medium mb-2">
                {{ file ? file.name : 'Cliquez pour sélectionner ou glissez-déposez un fichier' }}
              </p>
              <p class="text-xs text-muted-foreground">
                {{ file ? formatFileSize(file.size) : 'Tous les types de fichiers sont acceptés' }}
              </p>
            </div>
          </div>

          <div class="space-y-2">
            <Label for="description">Description</Label>
            <textarea
              id="description"
              v-model="description"
              class="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
              placeholder="Description optionnelle du document..."
            />
          </div>

          <div class="space-y-2">
            <Label for="organization">Organisation *</Label>
            <Select v-model="selectedOrganizationId" required>
              <SelectTrigger>
                <SelectValue placeholder="Sélectionner une organisation" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem
                  v-for="org in organizations"
                  :key="org.id"
                  :value="String(org.id)"
                >
                  {{ org.name }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="property">Propriété (optionnel)</Label>
            <Select v-model="selectedPropertyId">
              <SelectTrigger>
                <SelectValue placeholder="Associer à une propriété (optionnel)" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="none">Aucune</SelectItem>
                <SelectItem
                  v-for="property in properties"
                  :key="property.id"
                  :value="String(property.id)"
                >
                  {{ property.title || property.reference }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>

          <!-- Aperçu du fichier -->
          <div v-if="file && isImageFile" class="space-y-2">
            <Label>Aperçu</Label>
            <div class="border rounded-lg p-4">
              <img
                :src="previewUrl"
                :alt="file.name"
                class="max-w-full max-h-64 mx-auto rounded"
              />
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex justify-end gap-2 pt-4 border-t">
          <Button variant="outline" @click="goBack" :disabled="uploading">
            Annuler
          </Button>
          <Button @click="handleUpload" :disabled="!canUpload || uploading">
            <Upload v-if="!uploading" class="mr-2 h-4 w-4" />
            <Loader2 v-else class="mr-2 h-4 w-4 animate-spin" />
            {{ uploading ? 'Upload en cours...' : 'Uploader' }}
          </Button>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import {
  documentService,
  organizationService,
  propertyService,
  useAuthStore,
  type Organization,
  type Property
} from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { ArrowLeft, Upload, Loader2 } from 'lucide-vue-next'

const { toast } = useToast()
const router = useRouter()
const authStore = useAuthStore()

const fileInput = ref<HTMLInputElement | null>(null)
const file = ref<File | null>(null)
const description = ref('')
const selectedOrganizationId = ref('')
const selectedPropertyId = ref('none')
const uploading = ref(false)
const previewUrl = ref<string | null>(null)

const organizations = ref<Organization[]>([])
const properties = ref<Property[]>([])

const isImageFile = computed(() => {
  if (!file.value) return false
  return file.value.type.startsWith('image/')
})

const canUpload = computed(() => {
  return file.value !== null && selectedOrganizationId.value !== ''
})

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    setFile(target.files[0])
  }
}

const handleDrop = (event: DragEvent) => {
  if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
    setFile(event.dataTransfer.files[0])
  }
}

const setFile = (selectedFile: File) => {
  file.value = selectedFile
  
  // Create preview for images
  if (selectedFile.type.startsWith('image/')) {
    const reader = new FileReader()
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string
    }
    reader.readAsDataURL(selectedFile)
  } else {
    previewUrl.value = null
  }
}

const handleUpload = async () => {
  if (!file.value || !selectedOrganizationId.value || !authStore.user?.id) {
    toast({
      title: 'Erreur',
      description: 'Veuillez remplir tous les champs requis',
      variant: 'destructive'
    })
    return
  }

  uploading.value = true
  try {
    await documentService.upload({
      file: file.value,
      organizationId: Number(selectedOrganizationId.value),
      createdBy: authStore.user.id,
      propertyId: selectedPropertyId.value && selectedPropertyId.value !== 'none' ? Number(selectedPropertyId.value) : undefined,
      description: description.value || undefined
    })

    toast({
      title: 'Document uploadé',
      description: 'Le document a été uploadé avec succès'
    })

    // Redirect to documents list
    router.push('/documents')
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'uploader le document',
      variant: 'destructive'
    })
  } finally {
    uploading.value = false
  }
}

const goBack = () => {
  router.push('/documents')
}

const loadOrganizations = async () => {
  try {
    const result = await organizationService.getAll()
    if (result.organizations && Array.isArray(result.organizations)) {
      organizations.value = result.organizations
      // Set default organization if user has one
      if (authStore.user?.organizationId && !selectedOrganizationId.value) {
        selectedOrganizationId.value = String(authStore.user.organizationId)
      }
    }
  } catch (error) {
    console.error('Error loading organizations:', error)
  }
}

const loadProperties = async () => {
  try {
    const result = await propertyService.getAll()
    if (result.properties && Array.isArray(result.properties)) {
      properties.value = result.properties
    }
  } catch (error) {
    console.error('Error loading properties:', error)
  }
}

onMounted(async () => {
  await loadOrganizations()
  await loadProperties()
})
</script>

