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
    <Card v-if="inline">
      <CardHeader>
        <CardTitle>Contacter l'agent</CardTitle>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <slot name="form-content" :form="form" :errors="errors">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="name">
                  Nom complet <span class="text-red-500">*</span>
                </Label>
                <Input
                  id="name"
                  v-model="form.name"
                  type="text"
                  required
                  :class="errors.name ? 'border-red-300 focus-visible:ring-red-500' : ''"
                  placeholder="Votre nom"
                />
                <p v-if="errors.name" class="text-sm text-red-600">{{ errors.name }}</p>
              </div>

              <div class="space-y-2">
                <Label for="email">
                  Email <span class="text-red-500">*</span>
                </Label>
                <Input
                  id="email"
                  v-model="form.email"
                  type="email"
                  required
                  :class="errors.email ? 'border-red-300 focus-visible:ring-red-500' : ''"
                  placeholder="votre@email.com"
                />
                <p v-if="errors.email" class="text-sm text-red-600">{{ errors.email }}</p>
              </div>
            </div>

            <div class="space-y-2">
              <Label for="phone">Téléphone (optionnel)</Label>
              <Input
                id="phone"
                v-model="form.phone"
                type="tel"
                placeholder="+33 6 12 34 56 78"
              />
            </div>

            <div class="space-y-2">
              <Label for="subject">
                Sujet <span class="text-red-500">*</span>
              </Label>
              <Input
                id="subject"
                v-model="form.subject"
                type="text"
                required
                maxlength="500"
                :class="errors.subject ? 'border-red-300 focus-visible:ring-red-500' : ''"
                :placeholder="subjectPlaceholder"
              />
              <p v-if="errors.subject" class="text-sm text-red-600">{{ errors.subject }}</p>
            </div>

            <div class="space-y-2">
              <Label for="message">
                Message <span class="text-red-500">*</span>
              </Label>
              <textarea
                id="message"
                v-model="form.message"
                required
                rows="5"
                :class="[
                  'flex min-h-[120px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 resize-none',
                  errors.message ? 'border-red-300 focus-visible:ring-red-500' : ''
                ]"
                :placeholder="messagePlaceholder"
              ></textarea>
              <p v-if="errors.message" class="text-sm text-red-600">{{ errors.message }}</p>
            </div>
          </slot>

          <div v-if="propertyId" class="bg-primary/10 border border-primary/20 rounded-lg p-3">
            <p class="text-sm text-primary">
              📍 Ce message concerne la propriété #{{ propertyId }}
            </p>
          </div>

          <div class="flex justify-end gap-3">
            <Button
              v-if="inline"
              type="button"
              variant="outline"
              @click="resetForm"
            >
              Annuler
            </Button>
            <Button
              type="submit"
              :disabled="loading"
            >
              <Loader2 v-if="loading" class="h-4 w-4 animate-spin" />
              <Mail v-else class="h-4 w-4" />
              {{ loading ? 'Envoi...' : 'Envoyer' }}
            </Button>
          </div>
        </form>

        <!-- Message de succès -->
        <div v-if="success" class="mt-4 p-4 bg-green-50 border border-green-200 rounded-lg">
          <p class="text-green-800">
            ✅ Votre message a été envoyé avec succès ! Nous vous répondrons dans les plus brefs délais.
          </p>
        </div>

        <!-- Message d'erreur -->
        <div v-if="error" class="mt-4 p-4 bg-destructive/10 border border-destructive/20 rounded-lg">
          <p class="text-destructive">
            ❌ {{ errorMessage || 'Une erreur est survenue lors de l\'envoi de votre message.' }}
          </p>
        </div>
      </CardContent>
    </Card>

    <!-- Modal -->
    <div
      v-if="!inline && isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      @click.self="closeModal"
    >
      <Card class="max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <CardHeader class="sticky top-0 bg-background border-b">
          <div class="flex items-center justify-between">
            <CardTitle>Contacter l'agent</CardTitle>
            <button
              @click="closeModal"
              class="text-muted-foreground hover:text-foreground transition-colors"
            >
              <X class="h-5 w-5" />
            </button>
          </div>
        </CardHeader>
        <CardContent class="p-6">
          <form @submit.prevent="handleSubmit" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="modal-name">
                  Nom complet <span class="text-red-500">*</span>
                </Label>
                <Input
                  id="modal-name"
                  v-model="form.name"
                  type="text"
                  required
                  :class="errors.name ? 'border-red-300 focus-visible:ring-red-500' : ''"
                  placeholder="Votre nom"
                />
                <p v-if="errors.name" class="text-sm text-red-600">{{ errors.name }}</p>
              </div>

              <div class="space-y-2">
                <Label for="modal-email">
                  Email <span class="text-red-500">*</span>
                </Label>
                <Input
                  id="modal-email"
                  v-model="form.email"
                  type="email"
                  required
                  :class="errors.email ? 'border-red-300 focus-visible:ring-red-500' : ''"
                  placeholder="votre@email.com"
                />
                <p v-if="errors.email" class="text-sm text-red-600">{{ errors.email }}</p>
              </div>
            </div>

            <div class="space-y-2">
              <Label for="modal-phone">Téléphone (optionnel)</Label>
              <Input
                id="modal-phone"
                v-model="form.phone"
                type="tel"
                placeholder="+33 6 12 34 56 78"
              />
            </div>

            <div class="space-y-2">
              <Label for="modal-subject">
                Sujet <span class="text-red-500">*</span>
              </Label>
              <Input
                id="modal-subject"
                v-model="form.subject"
                type="text"
                required
                maxlength="500"
                :class="errors.subject ? 'border-red-300 focus-visible:ring-red-500' : ''"
                :placeholder="subjectPlaceholder"
              />
              <p v-if="errors.subject" class="text-sm text-red-600">{{ errors.subject }}</p>
            </div>

            <div class="space-y-2">
              <Label for="modal-message">
                Message <span class="text-red-500">*</span>
              </Label>
              <textarea
                id="modal-message"
                v-model="form.message"
                required
                rows="5"
                :class="[
                  'flex min-h-[120px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 resize-none',
                  errors.message ? 'border-red-300 focus-visible:ring-red-500' : ''
                ]"
                :placeholder="messagePlaceholder"
              ></textarea>
              <p v-if="errors.message" class="text-sm text-red-600">{{ errors.message }}</p>
            </div>

            <div v-if="propertyId" class="bg-primary/10 border border-primary/20 rounded-lg p-3">
              <p class="text-sm text-primary">
                📍 Ce message concerne la propriété #{{ propertyId }}
              </p>
            </div>

            <div class="flex justify-end gap-3 pt-4">
              <Button
                type="button"
                variant="outline"
                @click="closeModal"
              >
                Annuler
              </Button>
              <Button
                type="submit"
                :disabled="loading"
              >
                <Loader2 v-if="loading" class="h-4 w-4 animate-spin" />
                <Mail v-else class="h-4 w-4" />
                {{ loading ? 'Envoi...' : 'Envoyer' }}
              </Button>
            </div>
          </form>

          <!-- Message de succès -->
          <div v-if="success" class="mt-4 p-4 bg-green-50 border border-green-200 rounded-lg">
            <p class="text-green-800">
              ✅ Votre message a été envoyé avec succès ! Nous vous répondrons dans les plus brefs délais.
            </p>
          </div>

          <!-- Message d'erreur -->
          <div v-if="error" class="mt-4 p-4 bg-destructive/10 border border-destructive/20 rounded-lg">
            <p class="text-destructive">
              ❌ {{ errorMessage || 'Une erreur est survenue lors de l\'envoi de votre message.' }}
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { contactService, useAuthStore } from '@viridial/shared'
import type { ContactMessageCreate } from '@viridial/shared'
import { Mail, X, Loader2 } from 'lucide-vue-next'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'

interface Props {
  propertyId?: number
  propertyTitle?: string
  inline?: boolean
  buttonText?: string
  buttonClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  inline: false,
  buttonText: 'Contacter',
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

const form = reactive<ContactMessageCreate>({
  name: '',
  email: '',
  phone: '',
  subject: '',
  message: '',
  propertyId: props.propertyId
})

const subjectPlaceholder = computed(() => {
  if (props.propertyTitle) {
    return `Demande d'informations sur : ${props.propertyTitle}`
  }
  return "Sujet de votre message"
})

const messagePlaceholder = computed(() => {
  if (props.propertyTitle) {
    return `Bonjour,\n\nJe suis intéressé(e) par la propriété "${props.propertyTitle}".\n\nPourriez-vous me fournir plus d'informations ?\n\nMerci.`
  }
  return 'Votre message...'
})

// Initialiser le sujet et le message si propertyTitle est fourni
watch(() => props.propertyTitle, (newTitle) => {
  if (newTitle && !form.subject) {
    form.subject = `Demande d'informations sur : ${newTitle}`
  }
  if (newTitle && !form.message) {
    form.message = `Bonjour,\n\nJe suis intéressé(e) par la propriété "${newTitle}".\n\nPourriez-vous me fournir plus d'informations ?\n\nMerci.`
  }
}, { immediate: true })

// Pré-remplir avec les infos de l'utilisateur connecté
watch(() => authStore?.user, (user) => {
  if (user && !form.name) {
    if (user.firstName && user.lastName) {
      form.name = `${user.firstName} ${user.lastName}`
    }
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
    await contactService.create({
      ...form,
      propertyId: props.propertyId
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
  form.subject = props.propertyTitle
    ? `Demande d'informations sur : ${props.propertyTitle}`
    : ''
  form.message = props.propertyTitle
    ? `Bonjour,\n\nJe suis intéressé(e) par la propriété "${props.propertyTitle}".\n\nPourriez-vous me fournir plus d'informations ?\n\nMerci.`
    : ''
  form.propertyId = props.propertyId
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

