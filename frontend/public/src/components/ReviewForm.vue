<template>
  <div class="bg-white rounded-lg shadow-sm p-6">
    <h3 class="text-lg font-semibold text-gray-900 mb-4">Laisser un avis</h3>
    
    <form @submit.prevent="handleSubmit" class="space-y-4">
      <!-- Note -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Votre note <span class="text-red-500">*</span>
        </label>
        <div class="flex items-center gap-2">
          <button
            v-for="rating in 5"
            :key="rating"
            type="button"
            @click="formData.rating = rating"
            @mouseenter="hoveredRating = rating"
            @mouseleave="hoveredRating = 0"
            class="focus:outline-none transition-transform hover:scale-110"
          >
            <Star
              :class="[
                'h-8 w-8',
                rating <= (hoveredRating || formData.rating)
                  ? 'text-yellow-400 fill-current'
                  : 'text-gray-300'
              ]"
            />
          </button>
          <span v-if="formData.rating" class="ml-2 text-sm text-gray-600">
            {{ getRatingLabel(formData.rating) }}
          </span>
        </div>
        <p v-if="errors.rating" class="mt-1 text-sm text-red-600">{{ errors.rating }}</p>
      </div>

      <!-- Nom -->
      <div>
        <label for="authorName" class="block text-sm font-medium text-gray-700 mb-2">
          Votre nom <span class="text-red-500">*</span>
        </label>
        <input
          id="authorName"
          v-model="formData.authorName"
          type="text"
          required
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          placeholder="Votre nom"
        />
        <p v-if="errors.authorName" class="mt-1 text-sm text-red-600">{{ errors.authorName }}</p>
      </div>

      <!-- Email (optionnel) -->
      <div>
        <label for="authorEmail" class="block text-sm font-medium text-gray-700 mb-2">
          Votre email (optionnel)
        </label>
        <input
          id="authorEmail"
          v-model="formData.authorEmail"
          type="email"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          placeholder="votre@email.com"
        />
      </div>

      <!-- Commentaire -->
      <div>
        <label for="comment" class="block text-sm font-medium text-gray-700 mb-2">
          Votre avis <span class="text-red-500">*</span>
        </label>
        <textarea
          id="comment"
          v-model="formData.comment"
          required
          rows="5"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          placeholder="Partagez votre expérience..."
        ></textarea>
        <p v-if="errors.comment" class="mt-1 text-sm text-red-600">{{ errors.comment }}</p>
      </div>

      <!-- Boutons -->
      <div class="flex justify-end gap-3">
        <button
          type="button"
          @click="resetForm"
          class="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
        >
          Annuler
        </button>
        <button
          type="submit"
          :disabled="submitting"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          <Loader2 v-if="submitting" class="h-4 w-4 animate-spin" />
          {{ submitting ? 'Envoi...' : 'Publier l\'avis' }}
        </button>
      </div>
    </form>

    <!-- Message de succès -->
    <div v-if="success" class="mt-4 p-4 bg-green-50 border border-green-200 rounded-lg">
      <p class="text-green-800">
        Merci pour votre avis ! Il sera publié après modération.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { reviewService, useAuthStore } from '@viridial/shared'
import type { ReviewCreate } from '@viridial/shared'
import { Star, Loader2 } from 'lucide-vue-next'

interface Props {
  propertyId: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  reviewCreated: []
}>()

// Obtenir le store d'authentification (peut être null si Pinia n'est pas initialisé)
let authStore: any = null
try {
  authStore = useAuthStore()
} catch (error) {
  // Pinia n'est pas disponible, on continue sans
  console.debug('Auth store not available')
}

const submitting = ref(false)
const success = ref(false)
const hoveredRating = ref(0)
const errors = reactive<Record<string, string>>({})

const formData = reactive<ReviewCreate>({
  propertyId: props.propertyId,
  userId: undefined,
  authorName: '',
  authorEmail: '',
  rating: 0,
  comment: '',
  verifiedPurchase: false
})

const getRatingLabel = (rating: number): string => {
  const labels: Record<number, string> = {
    1: 'Très mauvais',
    2: 'Mauvais',
    3: 'Moyen',
    4: 'Bon',
    5: 'Excellent'
  }
  return labels[rating] || ''
}

const validateForm = (): boolean => {
  errors.rating = ''
  errors.authorName = ''
  errors.comment = ''

  if (!formData.rating || formData.rating < 1 || formData.rating > 5) {
    errors.rating = 'Veuillez sélectionner une note'
    return false
  }

  if (!formData.authorName || formData.authorName.trim().length === 0) {
    errors.authorName = 'Le nom est requis'
    return false
  }

  if (!formData.comment || formData.comment.trim().length === 0) {
    errors.comment = 'Le commentaire est requis'
    return false
  }

  if (formData.comment.trim().length < 10) {
    errors.comment = 'Le commentaire doit contenir au moins 10 caractères'
    return false
  }

  return true
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  submitting.value = true
  try {
    await reviewService.create({
      ...formData,
      propertyId: props.propertyId
    })
    
    success.value = true
    resetForm()
    
    // Émettre l'événement pour rafraîchir la liste
    emit('reviewCreated')
    
    // Masquer le message de succès après 5 secondes
    setTimeout(() => {
      success.value = false
    }, 5000)
  } catch (error: any) {
    console.error('Error creating review:', error)
    if (error.response?.data?.message) {
      errors.submit = error.response.data.message
    } else {
      errors.submit = 'Une erreur est survenue lors de l\'envoi de votre avis'
    }
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  formData.rating = 0
  formData.authorName = authStore?.user?.firstName && authStore?.user?.lastName
    ? `${authStore.user.firstName} ${authStore.user.lastName}`
    : ''
  formData.authorEmail = authStore?.user?.email || ''
  formData.comment = ''
  formData.userId = authStore?.user?.id
  hoveredRating.value = 0
  Object.keys(errors).forEach(key => delete errors[key])
}

// Initialiser le formulaire au montage
onMounted(() => {
  // Pré-remplir avec les infos de l'utilisateur si disponible
  if (authStore && authStore.user) {
    if (authStore.user.firstName && authStore.user.lastName) {
      formData.authorName = `${authStore.user.firstName} ${authStore.user.lastName}`
    }
    if (authStore.user.email) {
      formData.authorEmail = authStore.user.email
    }
    if (authStore.user.id) {
      formData.userId = authStore.user.id
    }
  }
})
</script>

