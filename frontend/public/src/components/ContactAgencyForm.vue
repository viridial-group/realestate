<template>
  <div>
    <!-- Bouton pour ouvrir le modal -->
    <button
      v-if="!inline"
      @click="isOpen = true"
      :class="buttonClass"
      class="flex items-center gap-2"
    >
      <Mail class="h-4 w-4" />
      <span>{{ buttonText }}</span>
    </button>

    <!-- Formulaire inline -->
    <div v-if="inline" class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
      <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Contacter l'agence</h3>
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="name" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Nom complet <span class="text-red-500">*</span>
            </label>
            <input
              id="name"
              v-model="form.name"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
              placeholder="Votre nom"
            />
            <p v-if="errors.name" class="mt-1 text-sm text-red-600">{{ errors.name }}</p>
          </div>

          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Email <span class="text-red-500">*</span>
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
              placeholder="votre@email.com"
            />
            <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
          </div>
        </div>

        <div>
          <label for="phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Téléphone (optionnel)
          </label>
          <input
            id="phone"
            v-model="form.phone"
            type="tel"
            class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
            placeholder="+33 6 12 34 56 78"
          />
        </div>

        <div>
          <label for="subject" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Sujet <span class="text-red-500">*</span>
          </label>
          <input
            id="subject"
            v-model="form.subject"
            type="text"
            required
            maxlength="500"
            class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
            :placeholder="subjectPlaceholder"
          />
          <p v-if="errors.subject" class="mt-1 text-sm text-red-600">{{ errors.subject }}</p>
        </div>

        <div>
          <label for="message" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Message <span class="text-red-500">*</span>
          </label>
          <textarea
            id="message"
            v-model="form.message"
            required
            rows="5"
            class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none dark:bg-gray-700 dark:text-white"
            :placeholder="messagePlaceholder"
          ></textarea>
          <p v-if="errors.message" class="mt-1 text-sm text-red-600">{{ errors.message }}</p>
        </div>

        <div v-if="organizationName" class="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-3">
          <p class="text-sm text-blue-800 dark:text-blue-200">
            📍 Ce message concerne l'agence : {{ organizationName }}
          </p>
        </div>

        <div v-if="success" class="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-4">
          <p class="text-sm text-green-800 dark:text-green-200">
            ✅ Votre message a été envoyé avec succès ! Nous vous répondrons dans les plus brefs délais.
          </p>
        </div>

        <div v-if="error" class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
          <p class="text-sm text-red-800 dark:text-red-200">{{ errorMessage }}</p>
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2"
        >
          <Loader2 v-if="loading" class="h-4 w-4 animate-spin" />
          <span>{{ loading ? 'Envoi en cours...' : 'Envoyer le message' }}</span>
        </button>
      </form>
    </div>

    <!-- Modal -->
    <div
      v-if="!inline && isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4"
      @click.self="closeModal"
    >
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div class="sticky top-0 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 px-6 py-4 flex items-center justify-between">
          <h3 class="text-xl font-semibold text-gray-900 dark:text-white">Contacter l'agence</h3>
          <button
            @click="closeModal"
            class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
          >
            <X class="h-5 w-5" />
          </button>
        </div>

        <div class="p-6">
          <form @submit.prevent="handleSubmit" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="modal-name" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Nom complet <span class="text-red-500">*</span>
                </label>
                <input
                  id="modal-name"
                  v-model="form.name"
                  type="text"
                  required
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                  placeholder="Votre nom"
                />
                <p v-if="errors.name" class="mt-1 text-sm text-red-600">{{ errors.name }}</p>
              </div>

              <div>
                <label for="modal-email" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Email <span class="text-red-500">*</span>
                </label>
                <input
                  id="modal-email"
                  v-model="form.email"
                  type="email"
                  required
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                  placeholder="votre@email.com"
                />
                <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
              </div>
            </div>

            <div>
              <label for="modal-phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Téléphone (optionnel)
              </label>
              <input
                id="modal-phone"
                v-model="form.phone"
                type="tel"
                class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                placeholder="+33 6 12 34 56 78"
              />
            </div>

            <div>
              <label for="modal-subject" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Sujet <span class="text-red-500">*</span>
              </label>
              <input
                id="modal-subject"
                v-model="form.subject"
                type="text"
                required
                maxlength="500"
                class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                :placeholder="subjectPlaceholder"
              />
              <p v-if="errors.subject" class="mt-1 text-sm text-red-600">{{ errors.subject }}</p>
            </div>

            <div>
              <label for="modal-message" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Message <span class="text-red-500">*</span>
              </label>
              <textarea
                id="modal-message"
                v-model="form.message"
                required
                rows="5"
                class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none dark:bg-gray-700 dark:text-white"
                :placeholder="messagePlaceholder"
              ></textarea>
              <p v-if="errors.message" class="mt-1 text-sm text-red-600">{{ errors.message }}</p>
            </div>

            <div v-if="organizationName" class="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-3">
              <p class="text-sm text-blue-800 dark:text-blue-200">
                📍 Ce message concerne l'agence : {{ organizationName }}
              </p>
            </div>

            <div v-if="success" class="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-4">
              <p class="text-sm text-green-800 dark:text-green-200">
                ✅ Votre message a été envoyé avec succès ! Nous vous répondrons dans les plus brefs délais.
              </p>
            </div>

            <div v-if="error" class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
              <p class="text-sm text-red-800 dark:text-red-200">{{ errorMessage }}</p>
            </div>

            <button
              type="submit"
              :disabled="loading"
              class="w-full px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2"
            >
              <Loader2 v-if="loading" class="h-4 w-4 animate-spin" />
              <span>{{ loading ? 'Envoi en cours...' : 'Envoyer le message' }}</span>
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { organizationContactService, useAuthStore } from '@viridial/shared'
import type { OrganizationContactMessageCreate } from '@viridial/shared'
import { Mail, X, Loader2 } from 'lucide-vue-next'

interface Props {
  organizationId: number
  organizationName?: string
  inline?: boolean
  buttonText?: string
  buttonClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  inline: false,
  buttonText: 'Contacter l\'agence',
  buttonClass: 'px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors'
})

const emit = defineEmits<{
  submitted: []
  success: []
}>()

// Obtenir le store d'authentification (peut être null si Pinia n'est pas initialisé)
let authStore: any = null
try {
  authStore = useAuthStore()
} catch (error) {
  // Pinia n'est pas disponible, on continue sans
  console.debug('Auth store not available')
}

const isOpen = ref(false)
const loading = ref(false)
const success = ref(false)
const error = ref(false)
const errorMessage = ref('')
const errors = reactive<Record<string, string>>({})

const form = reactive<OrganizationContactMessageCreate>({
  name: '',
  email: '',
  phone: '',
  subject: '',
  message: '',
  organizationId: props.organizationId
})

const subjectPlaceholder = computed(() => {
  if (props.organizationName) {
    return `Demande d'informations sur : ${props.organizationName}`
  }
  return "Sujet de votre message"
})

const messagePlaceholder = computed(() => {
  if (props.organizationName) {
    return `Bonjour,\n\nJe souhaite obtenir des informations sur votre agence "${props.organizationName}".\n\nPourriez-vous me contacter ?\n\nMerci.`
  }
  return 'Votre message...'
})

// Initialiser le sujet et le message si organizationName est fourni
watch(() => props.organizationName, (newName) => {
  if (newName && !form.subject) {
    form.subject = `Demande d'informations sur : ${newName}`
  }
  if (newName && !form.message) {
    form.message = `Bonjour,\n\nJe souhaite obtenir des informations sur votre agence "${newName}".\n\nPourriez-vous me contacter ?\n\nMerci.`
  }
}, { immediate: true })

// Pré-remplir avec les infos de l'utilisateur connecté
watch(() => authStore?.user, (user) => {
  if (user?.firstName && user?.lastName) {
    form.name = `${user.firstName} ${user.lastName}`
  }
  if (user?.email && !form.email) {
    form.email = user.email
  }
}, { immediate: true })

const validateForm = (): boolean => {
  errors.name = ''
  errors.email = ''
  errors.subject = ''
  errors.message = ''

  if (!form.name || form.name.trim().length === 0) {
    errors.name = 'Le nom est requis'
    return false
  }

  if (!form.email || form.email.trim().length === 0) {
    errors.email = 'L\'email est requis'
    return false
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'L\'email n\'est pas valide'
    return false
  }

  if (!form.subject || form.subject.trim().length === 0) {
    errors.subject = 'Le sujet est requis'
    return false
  }

  if (!form.message || form.message.trim().length === 0) {
    errors.message = 'Le message est requis'
    return false
  }

  if (form.message.trim().length < 10) {
    errors.message = 'Le message doit contenir au moins 10 caractères'
    return false
  }

  return true
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true
  success.value = false
  error.value = false
  errorMessage.value = ''

  try {
    await organizationContactService.create(props.organizationId, {
      ...form,
      organizationId: props.organizationId
    })
    
    success.value = true
    emit('success')
    emit('submitted')
    
    // Fermer le modal après 2 secondes si ce n'est pas inline
    if (!props.inline) {
      setTimeout(() => {
        closeModal()
        resetForm()
      }, 2000)
    } else {
      // Réinitialiser le formulaire après 3 secondes si inline
      setTimeout(() => {
        resetForm()
      }, 3000)
    }
  } catch (err: any) {
    console.error('Error sending contact message:', err)
    error.value = true
    if (err.response?.data?.message) {
      errorMessage.value = err.response.data.message
    } else {
      errorMessage.value = 'Une erreur est survenue lors de l\'envoi de votre message.'
    }
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.name = authStore?.user?.firstName && authStore?.user?.lastName
    ? `${authStore.user.firstName} ${authStore.user.lastName}`
    : ''
  form.email = authStore?.user?.email || ''
  form.phone = ''
  form.subject = props.organizationName
    ? `Demande d'informations sur : ${props.organizationName}`
    : ''
  form.message = props.organizationName
    ? `Bonjour,\n\nJe souhaite obtenir des informations sur votre agence "${props.organizationName}".\n\nPourriez-vous me contacter ?\n\nMerci.`
    : ''
  form.organizationId = props.organizationId
  Object.keys(errors).forEach(key => delete errors[key])
  success.value = false
  error.value = false
  errorMessage.value = ''
}

const closeModal = () => {
  isOpen.value = false
  resetForm()
}
</script>

