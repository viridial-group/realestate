<template>
  <div class="space-y-4">
    <Label>Images de la propriété</Label>
    
    <!-- Zone de drop -->
    <div
      @drop.prevent="handleDrop"
      @dragover.prevent="isDragging = true"
      @dragleave.prevent="isDragging = false"
      :class="[
        'border-2 border-dashed rounded-lg p-8 text-center transition-colors',
        isDragging ? 'border-primary bg-primary/5' : 'border-muted-foreground/25',
        uploading ? 'opacity-50 pointer-events-none' : 'cursor-pointer hover:border-primary'
      ]"
      @click="triggerFileInput"
    >
      <input
        ref="fileInputRef"
        type="file"
        multiple
        accept="image/*"
        class="hidden"
        @change="handleFileSelect"
      />
      
      <div class="flex flex-col items-center gap-2">
        <Upload class="h-12 w-12 text-muted-foreground" />
        <div>
          <p class="text-sm font-medium">
            Glissez-déposez des images ici ou cliquez pour sélectionner
          </p>
          <p class="text-xs text-muted-foreground mt-1">
            Formats acceptés: JPG, PNG, GIF, WebP (max 50MB par image)
          </p>
        </div>
      </div>
    </div>

    <!-- Liste des images uploadées -->
    <div v-if="uploadedImages.length > 0" class="grid grid-cols-2 md:grid-cols-4 gap-4">
      <div
        v-for="(image, index) in uploadedImages"
        :key="index"
        class="relative group"
      >
        <div class="aspect-square rounded-lg overflow-hidden border border-border bg-muted">
          <img
            :src="image.url || image.preview"
            :alt="image.name || `Image ${index + 1}`"
            class="w-full h-full object-cover"
          />
          <div class="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-colors flex items-center justify-center gap-2 opacity-0 group-hover:opacity-100">
            <Button
              variant="destructive"
              size="icon"
              @click="removeImage(index)"
              :disabled="uploading"
            >
              <Trash2 class="h-4 w-4" />
            </Button>
            <Button
              v-if="index > 0"
              variant="secondary"
              size="icon"
              @click="moveImage(index, index - 1)"
              :disabled="uploading"
            >
              <ArrowUp class="h-4 w-4" />
            </Button>
            <Button
              v-if="index < uploadedImages.length - 1"
              variant="secondary"
              size="icon"
              @click="moveImage(index, index + 1)"
              :disabled="uploading"
            >
              <ArrowDown class="h-4 w-4" />
            </Button>
          </div>
          <Badge
            v-if="index === 0"
            class="absolute top-2 left-2"
            variant="default"
          >
            Principale
          </Badge>
        </div>
        <div v-if="image.uploading" class="absolute inset-0 bg-black/50 flex items-center justify-center">
          <Loader2 class="h-6 w-6 animate-spin text-white" />
        </div>
      </div>
    </div>

    <!-- Messages d'erreur -->
    <div v-if="errors.length > 0" class="space-y-1">
      <p
        v-for="(error, index) in errors"
        :key="index"
        class="text-sm text-destructive"
      >
        {{ error }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Upload, Trash2, ArrowUp, ArrowDown, Loader2 } from 'lucide-vue-next'
import { documentService } from '@viridial/shared'
import { useAuthStore } from '@viridial/shared'
import { useToast } from '@/components/ui/toast'

interface ImageItem {
  id?: number
  url?: string
  preview?: string
  name?: string
  file?: File
  uploading?: boolean
  documentId?: number
}

interface Props {
  modelValue?: string[]
  propertyId?: number | undefined
}

interface Emits {
  (e: 'update:modelValue', value: string[]): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  propertyId: undefined
})

const emit = defineEmits<Emits>()

const authStore = useAuthStore()
const { toast } = useToast()

const fileInputRef = ref<HTMLInputElement>()
const isDragging = ref(false)
const uploading = ref(false)
const errors = ref<string[]>([])
const uploadedImages = ref<ImageItem[]>([])

// Initialiser les images depuis modelValue
watch(() => props.modelValue, (newValue) => {
  if (newValue && newValue.length > 0) {
    // Ne mettre à jour que si les URLs ont changé
    const currentUrls = uploadedImages.value
      .filter(img => img.url || img.preview)
      .map(img => img.url || img.preview || '')
    
    if (JSON.stringify(currentUrls) !== JSON.stringify(newValue)) {
      uploadedImages.value = newValue.map((url, index) => ({
        url,
        preview: url,
        name: `Image ${index + 1}`
      }))
    }
  } else if (!newValue || newValue.length === 0) {
    // Si modelValue est vide, vider aussi uploadedImages (sauf si on est en train d'uploader)
    if (!uploading.value) {
      uploadedImages.value = []
    }
  }
}, { immediate: true })

// Émettre les URLs des images
watch(uploadedImages, (images) => {
  const urls = images
    .filter(img => img.url || img.preview)
    .map(img => img.url || img.preview || '')
  emit('update:modelValue', urls)
}, { deep: true })

const triggerFileInput = () => {
  fileInputRef.value?.click()
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files) {
    handleFiles(Array.from(target.files))
  }
}

const handleDrop = (event: DragEvent) => {
  isDragging.value = false
  if (event.dataTransfer?.files) {
    handleFiles(Array.from(event.dataTransfer.files))
  }
}

const handleFiles = async (files: File[]) => {
  errors.value = []
  const imageFiles = files.filter(file => file.type.startsWith('image/'))
  
  if (imageFiles.length === 0) {
    errors.value.push('Veuillez sélectionner uniquement des fichiers image')
    return
  }

  // Vérifier la taille des fichiers
  const maxSize = 50 * 1024 * 1024 // 50MB
  const oversizedFiles = imageFiles.filter(file => file.size > maxSize)
  if (oversizedFiles.length > 0) {
    errors.value.push(`${oversizedFiles.length} fichier(s) dépassent la taille maximale de 50MB`)
    return
  }

  // Créer des previews pour les nouvelles images
  const newImages: ImageItem[] = []
  for (const file of imageFiles) {
    const preview = URL.createObjectURL(file)
    newImages.push({
      file,
      preview,
      name: file.name,
      uploading: true
    })
  }

  uploadedImages.value.push(...newImages)

  // Uploader les images
  if (props.propertyId && authStore.user) {
    await uploadImages(newImages)
  } else {
    // Si pas de propertyId, on garde juste les previews
    // Les images seront uploadées lors de la sauvegarde de la propriété
    // Mettre à jour les objets dans uploadedImages.value directement
    newImages.forEach(newImg => {
      const index = uploadedImages.value.findIndex(img => img === newImg)
      if (index !== -1) {
        uploadedImages.value[index].uploading = false
      }
    })
  }
}

const uploadImages = async (images: ImageItem[]) => {
  if (!props.propertyId || !authStore.user) return

  uploading.value = true
  const organizationId = authStore.user.organizationId || 1

  for (const image of images) {
    if (!image.file) {
      // Si pas de fichier, désactiver le loading pour cette image
      const imageIndex = uploadedImages.value.findIndex(img => img === image)
      if (imageIndex !== -1) {
        uploadedImages.value[imageIndex].uploading = false
      }
      continue
    }

    try {
      const document = await documentService.upload({
        file: image.file,
        organizationId,
        createdBy: authStore.user.id!,
        propertyId: props.propertyId,
        description: `Image pour la propriété ${props.propertyId}`
      })

      // Remplacer la preview par l'URL du document
      const imageIndex = uploadedImages.value.findIndex(img => img === image)
      if (imageIndex !== -1) {
        uploadedImages.value[imageIndex] = {
          id: document.id,
          url: documentService.getViewUrl(document.id),
          preview: documentService.getViewUrl(document.id),
          name: document.name,
          documentId: document.id,
          uploading: false
        }
      }
    } catch (error: any) {
      console.error('Error uploading image:', error)
      toast({
        title: 'Erreur',
        description: `Impossible d'uploader ${image.name}: ${error.message || 'Erreur inconnue'}`,
        variant: 'destructive'
      })
      // Retirer l'image en erreur et s'assurer que uploading est false
      const imageIndex = uploadedImages.value.findIndex(img => img === image)
      if (imageIndex !== -1) {
        // Libérer l'URL de la preview si c'est un blob
        const imgToRemove = uploadedImages.value[imageIndex]
        if (imgToRemove.preview && imgToRemove.preview.startsWith('blob:')) {
          URL.revokeObjectURL(imgToRemove.preview)
        }
        uploadedImages.value.splice(imageIndex, 1)
      }
    }
  }

  uploading.value = false
}

const removeImage = async (index: number) => {
  const image = uploadedImages.value[index]
  
  // Si l'image a un documentId, supprimer le document
  if (image.documentId) {
    try {
      await documentService.delete(image.documentId)
    } catch (error: any) {
      console.error('Error deleting document:', error)
      toast({
        title: 'Erreur',
        description: 'Impossible de supprimer le document',
        variant: 'destructive'
      })
      return
    }
  }

  // Libérer l'URL de la preview si c'est un blob
  if (image.preview && image.preview.startsWith('blob:')) {
    URL.revokeObjectURL(image.preview)
  }

  uploadedImages.value.splice(index, 1)
}

const moveImage = (fromIndex: number, toIndex: number) => {
  const image = uploadedImages.value[fromIndex]
  uploadedImages.value.splice(fromIndex, 1)
  uploadedImages.value.splice(toIndex, 0, image)
}

// Exposer une méthode pour uploader les images après création de la propriété
defineExpose({
  uploadPendingImages: async (propertyId: number) => {
    const pendingImages = uploadedImages.value.filter(img => img.file && !img.documentId)
    if (pendingImages.length > 0 && authStore.user) {
      uploading.value = true
      const organizationId = authStore.user.organizationId || 1

      for (const image of pendingImages) {
        if (!image.file) {
          // Si pas de fichier, désactiver le loading pour cette image
          const imageIndex = uploadedImages.value.findIndex(img => img === image)
          if (imageIndex !== -1) {
            uploadedImages.value[imageIndex].uploading = false
          }
          continue
        }

        try {
          const document = await documentService.upload({
            file: image.file,
            organizationId,
            createdBy: authStore.user.id!,
            propertyId: propertyId,
            description: `Image pour la propriété ${propertyId}`
          })

          // Remplacer la preview par l'URL du document
          const imageIndex = uploadedImages.value.findIndex(img => img === image)
          if (imageIndex !== -1) {
            uploadedImages.value[imageIndex] = {
              id: document.id,
              url: documentService.getViewUrl(document.id),
              preview: documentService.getViewUrl(document.id),
              name: document.name,
              documentId: document.id,
              uploading: false
            }
          }
        } catch (error: any) {
          console.error('Error uploading image:', error)
          toast({
            title: 'Erreur',
            description: `Impossible d'uploader ${image.name}: ${error.message || 'Erreur inconnue'}`,
            variant: 'destructive'
          })
          // Retirer l'image en erreur et s'assurer que uploading est false
          const imageIndex = uploadedImages.value.findIndex(img => img === image)
          if (imageIndex !== -1) {
            // Libérer l'URL de la preview si c'est un blob
            const imgToRemove = uploadedImages.value[imageIndex]
            if (imgToRemove.preview && imgToRemove.preview.startsWith('blob:')) {
              URL.revokeObjectURL(imgToRemove.preview)
            }
            uploadedImages.value[imageIndex].uploading = false
            uploadedImages.value.splice(imageIndex, 1)
          }
        }
      }

      uploading.value = false
    }
  }
})
</script>

