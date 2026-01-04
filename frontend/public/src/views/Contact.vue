<template>
  <div class="max-w-4xl mx-auto py-8 px-4">
    <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-8">Contactez-nous</h1>
    
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6 space-y-6">
      <div v-if="success" class="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-4 mb-6">
        <p class="text-green-800 dark:text-green-200">
          ✅ Votre message a été envoyé avec succès ! Nous vous répondrons dans les plus brefs délais.
        </p>
      </div>

      <div v-if="error" class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4 mb-6">
        <p class="text-red-800 dark:text-red-200">
          ❌ Une erreur est survenue lors de l'envoi de votre message. Veuillez réessayer.
        </p>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label for="name" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
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
          </div>

          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
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
          </div>
        </div>

        <div>
          <label for="phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
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
          <label for="subject" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Sujet <span class="text-red-500">*</span>
          </label>
          <input
            id="subject"
            v-model="form.subject"
            type="text"
            required
            maxlength="500"
            class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
            placeholder="Sujet de votre message"
          />
        </div>

        <div>
          <label for="message" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Message <span class="text-red-500">*</span>
          </label>
          <textarea
            id="message"
            v-model="form.message"
            required
            rows="6"
            class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white resize-none"
            placeholder="Votre message..."
          ></textarea>
        </div>

        <div v-if="propertyId" class="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
          <p class="text-sm text-blue-800 dark:text-blue-200">
            📍 Ce message concerne la propriété #{{ propertyId }}
          </p>
        </div>

        <div class="flex justify-end">
          <button
            type="submit"
            :disabled="loading"
            class="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span v-if="loading">Envoi en cours...</span>
            <span v-else>Envoyer le message</span>
          </button>
        </div>
      </form>

      <div class="mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
        <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-4">Autres moyens de contact</h2>
        <div class="grid md:grid-cols-2 gap-6">
          <div class="space-y-3">
            <div class="flex items-center gap-3">
              <span class="text-2xl">📧</span>
              <div>
                <p class="text-sm text-gray-500 dark:text-gray-400">Email</p>
                <a href="mailto:contact@viridial.com" class="text-blue-600 dark:text-blue-400 hover:underline font-medium">
                  contact@viridial.com
                </a>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <span class="text-2xl">📞</span>
              <div>
                <p class="text-sm text-gray-500 dark:text-gray-400">Téléphone</p>
                <a href="tel:+33123456789" class="text-blue-600 dark:text-blue-400 hover:underline font-medium">
                  +33 1 23 45 67 89
                </a>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <span class="text-2xl">🕒</span>
              <div>
                <p class="text-sm text-gray-500 dark:text-gray-400">Horaires</p>
                <p class="text-gray-700 dark:text-gray-300 font-medium">
                  Lundi - Vendredi : 9h00 - 18h00<br>
                  Samedi : 10h00 - 16h00
                </p>
              </div>
            </div>
          </div>
          <div class="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-4">
            <h3 class="font-semibold text-gray-900 dark:text-white mb-2">💡 Besoin d'aide ?</h3>
            <p class="text-sm text-gray-600 dark:text-gray-400 mb-3">
              Consultez notre FAQ pour trouver rapidement des réponses aux questions fréquentes.
            </p>
            <router-link
              to="/faq"
              class="inline-block text-sm text-blue-600 dark:text-blue-400 hover:underline font-medium"
            >
              Voir la FAQ →
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { contactService, type ContactMessageCreate } from '../../../shared'
import { useSEO } from '@/composables/useSEO'

const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'

onMounted(() => {
  // SEO optimisé pour la page Contact
  useSEO({
    title: 'Contactez-nous - Viridial Immobilier',
    description: 'Contactez l\'équipe Viridial pour toute question sur nos services immobiliers. Email, téléphone, horaires d\'ouverture. Nous sommes là pour vous aider à trouver votre bien idéal.',
    keywords: ['contact Viridial', 'support immobilier', 'aide immobilière', 'contact agence immobilière'],
    type: 'website',
    canonical: `${siteUrl}/contact`,
    url: `${siteUrl}/contact`
  })
})

const route = useRoute()

const form = ref<ContactMessageCreate>({
  name: '',
  email: '',
  phone: '',
  subject: '',
  message: '',
  propertyId: undefined
})

const loading = ref(false)
const success = ref(false)
const error = ref(false)
const propertyId = ref<number | undefined>(undefined)

onMounted(() => {
  // Si un propertyId est passé en paramètre, l'ajouter au formulaire
  if (route.query.propertyId) {
    const id = Number(route.query.propertyId)
    if (!isNaN(id)) {
      propertyId.value = id
      form.value.propertyId = id
    }
  }
})

async function handleSubmit() {
  loading.value = true
  success.value = false
  error.value = false

  try {
    await contactService.create(form.value)
    success.value = true
    // Réinitialiser le formulaire
    form.value = {
      name: '',
      email: '',
      phone: '',
      subject: '',
      message: '',
      propertyId: propertyId.value
    }
  } catch (err) {
    console.error('Error sending contact message:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}
</script>

