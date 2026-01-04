<template>
  <div class="bg-white rounded-lg shadow-sm p-6">
    <div class="flex items-center gap-2 mb-4">
      <Calendar class="h-5 w-5 text-blue-600" />
      <h3 class="text-lg font-semibold text-gray-900">Réserver une Visite</h3>
    </div>
    <p class="text-sm text-gray-600 mb-4">
      Choisissez une date et une heure pour visiter cette propriété
    </p>

    <div v-if="loading" class="flex items-center justify-center p-4">
      <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
    </div>

    <div v-else-if="success" class="bg-green-50 border border-green-200 rounded-lg p-4">
      <div class="flex items-center gap-2">
        <CheckCircle class="h-5 w-5 text-green-600" />
        <p class="text-green-800 font-medium">Visite réservée avec succès !</p>
      </div>
      <p class="text-sm text-green-700 mt-2">
        Votre demande de visite a été envoyée. L'agent vous contactera pour confirmer.
      </p>
    </div>

    <form v-else @submit.prevent="bookVisit" class="space-y-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Agent *
        </label>
        <select
          v-model="formData.agentId"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        >
          <option value="">Sélectionner un agent</option>
          <option
            v-for="agent in availableAgents"
            :key="agent.id"
            :value="agent.id"
          >
            {{ agent.name }}
          </option>
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Date et heure *
        </label>
        <input
          v-model="formData.appointmentDate"
          type="datetime-local"
          :min="minDateTime"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Durée (minutes)
        </label>
        <input
          v-model.number="formData.durationMinutes"
          type="number"
          min="30"
          max="180"
          step="30"
          placeholder="60"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Nom *
        </label>
        <input
          v-model="formData.visitorName"
          type="text"
          placeholder="Votre nom"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Email *
        </label>
        <input
          v-model="formData.visitorEmail"
          type="email"
          placeholder="votre@email.com"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Téléphone
        </label>
        <input
          v-model="formData.visitorPhone"
          type="tel"
          placeholder="+33 6 12 34 56 78"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Notes (optionnel)
        </label>
        <textarea
          v-model="formData.notes"
          rows="3"
          placeholder="Informations supplémentaires, questions, etc."
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
      </div>

      <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-3">
        <p class="text-sm text-red-800">{{ error }}</p>
      </div>

      <button
        type="submit"
        :disabled="submitting || !isFormValid"
        class="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <span v-if="submitting">Réservation en cours...</span>
        <span v-else>Réserver la visite</span>
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { visitAppointmentService } from '@viridial/shared'
import type { VisitAppointmentCreate } from '@viridial/shared'
import { Calendar, CheckCircle } from 'lucide-vue-next'

interface Props {
  propertyId: number
  assignedUserId?: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  visitBooked: []
}>()

const loading = ref(false)
const submitting = ref(false)
const success = ref(false)
const error = ref<string | null>(null)

const availableAgents = ref<Array<{ id: number; name: string }>>([])

const formData = reactive<VisitAppointmentCreate>({
  propertyId: props.propertyId,
  agentId: props.assignedUserId || 0,
  appointmentDate: '',
  durationMinutes: 60,
  visitorName: '',
  visitorEmail: '',
  visitorPhone: '',
  notes: ''
})

const minDateTime = computed(() => {
  const now = new Date()
  now.setHours(now.getHours() + 1) // Au moins 1 heure à l'avance
  return now.toISOString().slice(0, 16)
})

const isFormValid = computed(() => {
  return formData.agentId > 0 &&
         formData.appointmentDate &&
         formData.visitorName &&
         formData.visitorEmail
})

const loadAgents = async () => {
  // TODO: Charger la liste des agents depuis l'API
  // Pour l'instant, on utilise l'agent assigné si disponible
  if (props.assignedUserId) {
    availableAgents.value = [
      { id: props.assignedUserId, name: 'Agent assigné' }
    ]
    formData.agentId = props.assignedUserId
  } else {
    // Si pas d'agent assigné, on laisse l'utilisateur choisir
    // TODO: Charger depuis l'API
    availableAgents.value = []
  }
}

const bookVisit = async () => {
  submitting.value = true
  error.value = null

  try {
    // Convertir la date locale en ISO string
    const appointmentDate = new Date(formData.appointmentDate).toISOString()
    
    await visitAppointmentService.create({
      ...formData,
      appointmentDate
    })
    
    success.value = true
    emit('visitBooked')
    
    // Réinitialiser le formulaire après 3 secondes
    setTimeout(() => {
      success.value = false
      formData.appointmentDate = ''
      formData.visitorName = ''
      formData.visitorEmail = ''
      formData.visitorPhone = ''
      formData.notes = ''
    }, 3000)
  } catch (err: any) {
    console.error('Error booking visit:', err)
    error.value = err.message || 'Impossible de réserver la visite'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadAgents()
  // Initialiser la date minimale
  const now = new Date()
  now.setHours(now.getHours() + 1)
  formData.appointmentDate = now.toISOString().slice(0, 16)
})
</script>

