<template>
  <div class="space-y-4">
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-2">
        Images <span class="text-red-500">*</span>
        <span class="text-xs text-gray-500 font-normal ml-2">
          (Maximum {{ maxImages }} images, formats acceptés: JPG, PNG, WebP)
        </span>
      </label>
      
      <!-- Zone de drop -->
      <div
        @drop.prevent="handleDrop"
        @dragover.prevent="isDragging = true"
        @dragleave.prevent="isDragging = false"
        :class="[
          'border-2 border-dashed rounded-lg p-6 text-center transition-colors',
          isDragging ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-gray-400',
          error ? 'border-red-300 bg-red-50' : ''
        ]"
      >
        <input
          ref="fileInput"
          type="file"
          multiple
          accept="image/jpeg,image/jpg,image/png,image/webp"
          @change="handleFileSelect"
          class="hidden"
        />
        
        <div v-if="images.length === 0" class="space-y-2">
          <Upload class="h-12 w-12 text-gray-400 mx-auto" />
          <p class="text-sm text-gray-600">
            Glissez-déposez vos images ici ou
            <button
              type="button"
              @click="$refs.fileInput.click()"
              class="text-blue-600 hover:text-blue-700 underline"
            >
              cliquez pour sélectionner
            </button>
          </p>
        </div>
        
        <div v-else class="text-sm text-gray-600">
          {{ images.length }} image(s) sélectionnée(s)
          <button
            type="button"
            @click="$refs.fileInput.click()"
            class="text-blue-600 hover:text-blue-700 underline ml-2"
          >
            Ajouter plus
          </button>
        </div>
      </div>

      <!-- Message d'erreur -->
      <p v-if="error" class="mt-2 text-sm text-red-600">{{ error }}</p>
    </div>

    <!-- Grille d'images -->
    <div v-if="images.length > 0" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <div
        v-for="(image, index) in images"
        :key="index"
        class="relative group aspect-square rounded-lg overflow-hidden border border-gray-200 bg-gray-100"
      >
        <img
          :src="image.preview"
          :alt="`Image ${index + 1}`"
          class="w-full h-full object-cover"
        />
        
        <!-- Overlay avec actions -->
        <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-50 transition-opacity flex items-center justify-center gap-2">
          <button
            type="button"
            @click="setPrimary(index)"
            :class="[
              'px-3 py-1 text-xs rounded text-white transition-opacity',
              image.isPrimary ? 'bg-blue-600' : 'bg-gray-700 opacity-0 group-hover:opacity-100'
            ]"
          >
            {{ image.isPrimary ? 'Principale' : 'Définir principale' }}
          </button>
          <button
            type="button"
            @click="removeImage(index)"
            class="px-3 py-1 text-xs bg-red-600 text-white rounded opacity-0 group-hover:opacity-100 transition-opacity"
          >
            Supprimer
          </button>
        </div>

        <!-- Badge principale -->
        <div
          v-if="image.isPrimary"
          class="absolute top-2 left-2 px-2 py-1 bg-blue-600 text-white text-xs rounded"
        >
          Principale
        </div>

        <!-- Indicateur de chargement -->
        <div
          v-if="image.uploading"
          class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center"
        >
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Upload } from 'lucide-vue-next'

interface ImageItem {
  file: File
  preview: string
  isPrimary: boolean
  uploading?: boolean
  documentId?: number
  url?: string
}

const props = defineProps<{
  maxImages?: number
  modelValue?: ImageItem[]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: ImageItem[]]
}>()

const fileInput = ref<HTMLInputElement>()
const isDragging = ref(false)
const error = ref<string | null>(null)
const images = ref<ImageItem[]>(props.modelValue || [])

const maxImages = props.maxImages || 10

watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    images.value = newValue
  }
}, { deep: true })

watch(images, (newImages) => {
  emit('update:modelValue', newImages)
}, { deep: true })

function handleFileSelect(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files) {
    addFiles(Array.from(target.files))
  }
}

function handleDrop(event: DragEvent) {
  isDragging.value = false
  if (event.dataTransfer?.files) {
    addFiles(Array.from(event.dataTransfer.files))
  }
}

function addFiles(files: File[]) {
  error.value = null

  // Filtrer seulement les images
  const imageFiles = files.filter(file => file.type.startsWith('image/'))

  if (imageFiles.length === 0) {
    error.value = 'Veuillez sélectionner uniquement des fichiers image'
    return
  }

  // Vérifier le nombre maximum
  if (images.value.length + imageFiles.length > maxImages) {
    error.value = `Vous ne pouvez pas ajouter plus de ${maxImages} images`
    return
  }

  // Vérifier la taille des fichiers (max 10MB par image)
  const maxSize = 10 * 1024 * 1024 // 10MB
  const oversizedFiles = imageFiles.filter(file => file.size > maxSize)
  if (oversizedFiles.length > 0) {
    error.value = `Certains fichiers dépassent 10MB: ${oversizedFiles.map(f => f.name).join(', ')}`
    return
  }

  // Ajouter les images
  imageFiles.forEach(file => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const preview = e.target?.result as string
      const isPrimary = images.value.length === 0 // Première image = principale par défaut
      
      images.value.push({
        file,
        preview,
        isPrimary,
        uploading: false
      })
    }
    reader.readAsDataURL(file)
  })
}

function removeImage(index: number) {
  const removed = images.value.splice(index, 1)[0]
  
  // Si l'image supprimée était principale, définir la première comme principale
  if (removed.isPrimary && images.value.length > 0) {
    images.value[0].isPrimary = true
  }
}

function setPrimary(index: number) {
  // Retirer le statut principal de toutes les images
  images.value.forEach(img => img.isPrimary = false)
  // Définir la nouvelle principale
  images.value[index].isPrimary = true
}

// Exposer les méthodes pour l'upload
defineExpose({
  getImages: () => images.value,
  clearImages: () => {
    images.value = []
  },
  setUploading: (index: number, uploading: boolean) => {
    if (images.value[index]) {
      images.value[index].uploading = uploading
    }
  },
  setDocumentId: (index: number, documentId: number, url: string) => {
    if (images.value[index]) {
      images.value[index].documentId = documentId
      images.value[index].url = url
    }
  }
})
</script>

