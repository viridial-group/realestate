<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <div class="flex items-center gap-4 mb-4">
          <router-link
            to="/my-properties"
            class="text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft class="h-5 w-5" />
          </router-link>
          <h1 class="text-3xl font-bold text-gray-900">
            {{ isEdit ? 'Modifier l\'annonce' : 'Nouvelle annonce' }}
          </h1>
        </div>
        <p class="text-sm text-gray-600">
          {{ isEdit ? 'Modifiez les informations de votre annonce' : 'Remplissez les informations de votre annonce' }}
        </p>
      </div>

      <!-- Messages d'erreur globaux -->
      <div
        v-if="globalError"
        class="mb-6 bg-red-50 border border-red-200 rounded-lg p-4"
      >
        <div class="flex items-start gap-3">
          <AlertCircle class="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
          <div class="flex-1">
            <h3 class="text-sm font-medium text-red-800 mb-1">Erreur</h3>
            <p class="text-sm text-red-700">{{ globalError }}</p>
          </div>
          <button
            @click="globalError = null"
            class="text-red-600 hover:text-red-800"
          >
            <X class="h-4 w-4" />
          </button>
        </div>
      </div>

      <!-- Formulaire -->
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- Informations de base -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Informations de base</h2>
          
          <div class="space-y-4">
            <div>
              <label class="flex items-center gap-2 text-sm font-medium text-gray-700 mb-1">
                Titre de l'annonce <span class="text-red-500">*</span>
                <HelpTooltip
                  title="Titre de l'annonce"
                  content="Un titre accrocheur augmente les vues. Utilisez des mots-clés importants comme le type de bien, la localisation et les caractéristiques principales (ex: 'Appartement 3 pièces avec balcon à Paris 15ème')."
                />
              </label>
              <input
                v-model="form.title"
                type="text"
                required
                :class="[
                  'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                  fieldErrors.title ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                ]"
                placeholder="Ex: Appartement 3 pièces à Paris"
                @blur="validateField('title')"
              />
              <p v-if="fieldErrors.title" class="mt-1 text-sm text-red-600">{{ fieldErrors.title }}</p>
            </div>

            <div>
              <label class="flex items-center gap-2 text-sm font-medium text-gray-700 mb-1">
                Description <span class="text-red-500">*</span>
                <HelpTooltip
                  title="Description détaillée"
                  content="Décrivez votre bien en détail : état, équipements, environnement, transports, etc. Une description complète et honnête augmente la confiance des visiteurs et les contacts qualifiés."
                />
              </label>
              <textarea
                v-model="form.description"
                rows="5"
                required
                :class="[
                  'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                  fieldErrors.description ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                ]"
                placeholder="Décrivez votre bien immobilier..."
                @blur="validateField('description')"
              />
              <p v-if="fieldErrors.description" class="mt-1 text-sm text-red-600">{{ fieldErrors.description }}</p>
              <p class="mt-1 text-xs text-gray-500">{{ form.description.length }} caractères</p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Type de bien <span class="text-red-500">*</span>
                </label>
                <select
                  v-model="form.propertyType"
                  required
                  :class="[
                    'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                    fieldErrors.propertyType ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                  ]"
                  @blur="validateField('propertyType')"
                >
                  <option value="">Sélectionner...</option>
                  <option value="APARTMENT">Appartement</option>
                  <option value="HOUSE">Maison</option>
                  <option value="VILLA">Villa</option>
                  <option value="LAND">Terrain</option>
                  <option value="COMMERCIAL">Commercial</option>
                  <option value="OTHER">Autre</option>
                </select>
                <p v-if="fieldErrors.propertyType" class="mt-1 text-sm text-red-600">{{ fieldErrors.propertyType }}</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Type de transaction <span class="text-red-500">*</span>
                </label>
                <select
                  v-model="form.transactionType"
                  required
                  :class="[
                    'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                    fieldErrors.transactionType ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                  ]"
                  @blur="validateField('transactionType')"
                >
                  <option value="">Sélectionner...</option>
                  <option value="SALE">Vente</option>
                  <option value="RENT">Location</option>
                </select>
                <p v-if="fieldErrors.transactionType" class="mt-1 text-sm text-red-600">{{ fieldErrors.transactionType }}</p>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Prix <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="form.price"
                type="number"
                required
                min="0"
                step="100"
                :class="[
                  'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                  fieldErrors.price ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                ]"
                placeholder="Ex: 250000"
                @blur="validateField('price')"
              />
              <p v-if="fieldErrors.price" class="mt-1 text-sm text-red-600">{{ fieldErrors.price }}</p>
            </div>
          </div>
        </div>

        <!-- Images -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Images</h2>
          <ImageUpload
            v-model="images"
            :max-images="10"
            ref="imageUploadRef"
          />
        </div>

        <!-- Caractéristiques -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Caractéristiques</h2>
          
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Surface (m²)
              </label>
              <input
                v-model.number="form.area"
                type="number"
                min="0"
                step="1"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Ex: 75"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Chambres
              </label>
              <input
                v-model.number="form.bedrooms"
                type="number"
                min="0"
                step="1"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Ex: 3"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Salles de bain
              </label>
              <input
                v-model.number="form.bathrooms"
                type="number"
                min="0"
                step="0.5"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Ex: 2"
              />
            </div>
          </div>
        </div>

        <!-- Localisation -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Localisation</h2>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Adresse <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.address"
                type="text"
                required
                :class="[
                  'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                  fieldErrors.address ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                ]"
                placeholder="Ex: 123 Rue de la République"
                @blur="validateField('address')"
              />
              <p v-if="fieldErrors.address" class="mt-1 text-sm text-red-600">{{ fieldErrors.address }}</p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Ville <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.city"
                  type="text"
                  required
                  :class="[
                    'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                    fieldErrors.city ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                  ]"
                  placeholder="Ex: Paris"
                  @blur="validateField('city')"
                />
                <p v-if="fieldErrors.city" class="mt-1 text-sm text-red-600">{{ fieldErrors.city }}</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Code postal
                </label>
                <input
                  v-model="form.postalCode"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Ex: 75001"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Pays <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.country"
                  type="text"
                  required
                  :class="[
                    'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2',
                    fieldErrors.country ? 'border-red-300 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                  ]"
                  placeholder="Ex: France"
                  @blur="validateField('country')"
                />
                <p v-if="fieldErrors.country" class="mt-1 text-sm text-red-600">{{ fieldErrors.country }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Statut (uniquement en édition) -->
        <div v-if="isEdit" class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Statut</h2>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Statut de l'annonce
            </label>
            <select
              v-model="form.status"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="DRAFT">Brouillon</option>
              <option value="AVAILABLE">Disponible</option>
              <option value="PENDING">En attente</option>
              <option value="SOLD">Vendu</option>
              <option value="RENTED">Loué</option>
            </select>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex items-center justify-end gap-4">
          <router-link
            to="/my-properties"
            class="px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md transition-colors"
          >
            Annuler
          </router-link>
          <button
            type="button"
            @click="showPreview = true"
            :disabled="submitting || !isFormValid"
            class="px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md transition-colors disabled:opacity-50"
          >
            Aperçu
          </button>
          <button
            type="submit"
            :disabled="submitting || !isFormValid"
            class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition-colors"
          >
            {{ submitting ? 'Enregistrement...' : (isEdit ? 'Enregistrer' : 'Créer l\'annonce') }}
          </button>
        </div>
      </form>
    </div>

    <!-- Modal d'aperçu -->
    <PropertyPreview
      v-if="showPreview"
      :property="previewData"
      :images="images"
      @close="showPreview = false"
      @publish="handlePublishFromPreview"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, AlertCircle, X } from 'lucide-vue-next'
import { userPropertyService } from '@/api/user-property.service'
import { documentService, useAuthStore } from '@viridial/shared'
import type { PropertyCreate, PropertyUpdate } from '@viridial/shared'
import { useToast } from '@/composables/useToast'
import ImageUpload from '@/components/ImageUpload.vue'
import PropertyPreview from '@/components/PropertyPreview.vue'

const router = useRouter()
const route = useRoute()
const { showToast } = useToast()
const authStore = useAuthStore()

const isEdit = computed(() => !!route.params.id)
const submitting = ref(false)
const globalError = ref<string | null>(null)
const fieldErrors = ref<Record<string, string>>({})
const showPreview = ref(false)
const imageUploadRef = ref<InstanceType<typeof ImageUpload>>()
const images = ref<any[]>([])

const form = ref<PropertyCreate & { status?: string; postalCode?: string }>({
  title: '',
  description: '',
  price: 0,
  address: '',
  city: '',
  country: 'France',
  propertyType: 'APARTMENT',
  transactionType: 'SALE',
  bedrooms: undefined,
  bathrooms: undefined,
  area: undefined,
  status: 'DRAFT',
  postalCode: '',
})

const isFormValid = computed(() => {
  return form.value.title &&
    form.value.description &&
    form.value.price > 0 &&
    form.value.address &&
    form.value.city &&
    form.value.country &&
    form.value.propertyType &&
    form.value.transactionType &&
    Object.keys(fieldErrors.value).length === 0
})

const previewData = computed(() => ({
  ...form.value,
  id: isEdit.value ? Number(route.params.id) : undefined,
}))

onMounted(async () => {
  if (isEdit.value) {
    await loadProperty()
  }
})

async function loadProperty() {
  try {
    const propertyId = Number(route.params.id)
    const property = await userPropertyService.getMyPropertyById(propertyId)
    
    form.value = {
      title: property.title || '',
      description: property.description || '',
      price: property.price || 0,
      address: property.address || '',
      city: property.city || '',
      country: property.country || 'France',
      propertyType: (property.propertyType || property.type) as any || 'APARTMENT',
      transactionType: property.transactionType || 'SALE',
      bedrooms: property.bedrooms,
      bathrooms: property.bathrooms,
      area: property.area || property.surface,
      status: property.status || 'DRAFT',
      postalCode: property.postalCode || '',
    }

    // Charger les images existantes
    try {
      const documents = await documentService.getByPropertyId(propertyId)
      const imageDocuments = documents.filter(doc => 
        doc.type === 'IMAGE' || doc.mimeType?.startsWith('image/')
      )
      images.value = imageDocuments.map(doc => ({
        file: null,
        preview: documentService.getViewUrl(doc.id),
        isPrimary: false,
        documentId: doc.id,
        url: documentService.getViewUrl(doc.id),
      }))
    } catch (err) {
      console.warn('Could not load property images:', err)
    }
  } catch (err: any) {
    const errorMessage = err.response?.data?.message || err.message || 'Erreur lors du chargement de l\'annonce'
    globalError.value = errorMessage
    showToast(errorMessage, 'error')
    setTimeout(() => {
      router.push('/my-properties')
    }, 3000)
  }
}

function validateField(fieldName: string) {
  const field = form.value[fieldName as keyof typeof form.value]
  
  switch (fieldName) {
    case 'title':
      if (!field || String(field).trim().length < 10) {
        fieldErrors.value.title = 'Le titre doit contenir au moins 10 caractères'
      } else {
        delete fieldErrors.value.title
      }
      break
    case 'description':
      if (!field || String(field).trim().length < 50) {
        fieldErrors.value.description = 'La description doit contenir au moins 50 caractères'
      } else {
        delete fieldErrors.value.description
      }
      break
    case 'price':
      if (!field || Number(field) <= 0) {
        fieldErrors.value.price = 'Le prix doit être supérieur à 0'
      } else {
        delete fieldErrors.value.price
      }
      break
    case 'address':
    case 'city':
    case 'country':
      if (!field || String(field).trim().length === 0) {
        fieldErrors.value[fieldName] = 'Ce champ est requis'
      } else {
        delete fieldErrors.value[fieldName]
      }
      break
  }
}

async function uploadImages(propertyId: number) {
  if (!authStore.user || !imageUploadRef.value) return

  const imageItems = imageUploadRef.value.getImages()
  const imagesToUpload = imageItems.filter(img => img.file && !img.documentId)
  
  if (imagesToUpload.length === 0) return

  const organizationId = authStore.user.organizationId || 1

  for (let i = 0; i < imagesToUpload.length; i++) {
    const image = imagesToUpload[i]
    if (!image.file) continue

    try {
      imageUploadRef.value.setUploading(i, true)
      
      const document = await documentService.upload({
        file: image.file,
        organizationId,
        createdBy: authStore.user.id!,
        propertyId,
        description: `Image pour la propriété ${propertyId}`
      })

      imageUploadRef.value.setDocumentId(i, document.id, documentService.getViewUrl(document.id))
    } catch (err: any) {
      console.error('Error uploading image:', err)
      showToast(`Erreur lors de l'upload de l'image ${i + 1}`, 'error')
    } finally {
      imageUploadRef.value.setUploading(i, false)
    }
  }
}

async function handleSubmit() {
  // Valider tous les champs
  Object.keys(form.value).forEach(key => {
    if (['title', 'description', 'price', 'address', 'city', 'country', 'propertyType', 'transactionType'].includes(key)) {
      validateField(key)
    }
  })

  if (Object.keys(fieldErrors.value).length > 0) {
    globalError.value = 'Veuillez corriger les erreurs dans le formulaire'
    return
  }

  submitting.value = true
  globalError.value = null

  try {
    let propertyId: number

    if (isEdit.value) {
      const propertyIdParam = Number(route.params.id)
      const updateData: PropertyUpdate = {
        ...form.value,
        status: form.value.status as any,
      }
      const updated = await userPropertyService.updateProperty(propertyIdParam, updateData)
      propertyId = updated.id
      showToast('Annonce modifiée avec succès', 'success')
    } else {
      const createData: PropertyCreate = {
        title: form.value.title,
        description: form.value.description,
        price: form.value.price,
        address: form.value.address,
        city: form.value.city,
        country: form.value.country,
        propertyType: form.value.propertyType,
        transactionType: form.value.transactionType,
        bedrooms: form.value.bedrooms,
        bathrooms: form.value.bathrooms,
        area: form.value.area,
      }
      const created = await userPropertyService.createProperty(createData)
      propertyId = created.id
      showToast('Annonce créée avec succès', 'success')
    }

    // Uploader les images après création/modification
    await uploadImages(propertyId)

    router.push('/my-properties')
  } catch (err: any) {
    const errorMessage = err.response?.data?.message || err.message || 'Erreur lors de l\'enregistrement'
    globalError.value = errorMessage
    showToast(errorMessage, 'error')
  } finally {
    submitting.value = false
  }
}

async function handlePublishFromPreview() {
  showPreview.value = false
  // Changer le statut en AVAILABLE et soumettre
  form.value.status = 'AVAILABLE'
  await handleSubmit()
}
</script>
