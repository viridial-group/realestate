<template>
  <div class="min-h-screen bg-gradient-to-b from-blue-50 to-white">
    <!-- Hero Section -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 md:py-24">
      <div class="text-center">
        <!-- Logo/Titre -->
        <div class="mb-8">
          <h1 class="text-5xl md:text-6xl font-light text-gray-900 mb-4">
            Trouvez votre
            <span class="text-blue-600 font-medium">bien immobilier</span>
          </h1>
          <p class="text-xl md:text-2xl text-gray-600 max-w-2xl mx-auto">
            Des milliers de propriétés disponibles. Recherchez, comparez, trouvez.
          </p>
        </div>

        <!-- Barre de recherche hero -->
        <div class="max-w-3xl mx-auto mt-10">
          <div class="bg-white rounded-full shadow-lg p-2 flex items-center">
            <input
              v-model="searchQuery"
              @keyup.enter="handleSearch"
              type="text"
              placeholder="Rechercher une ville, un quartier, un type de bien..."
              class="flex-1 px-6 py-4 text-lg border-none outline-none rounded-full"
            />
            <button
              @click="handleSearch"
              class="bg-blue-600 text-white px-8 py-4 rounded-full hover:bg-blue-700 transition-colors font-medium flex items-center gap-2"
            >
              <span>🔍</span>
              <span>Rechercher</span>
            </button>
          </div>
          
          <!-- Suggestions rapides -->
          <div class="mt-6 flex flex-wrap justify-center gap-3">
            <button
              v-for="suggestion in quickSuggestions"
              :key="suggestion"
              @click="searchQuery = suggestion; handleSearch()"
              class="px-4 py-2 bg-white rounded-full text-sm text-gray-700 hover:bg-gray-50 shadow-sm transition-colors"
            >
              {{ suggestion }}
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Statistiques -->
    <section class="bg-white py-12 border-t border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div>
            <div class="text-4xl font-bold text-blue-600 mb-2">{{ stats.properties }}</div>
            <div class="text-gray-600">Propriétés</div>
          </div>
          <div>
            <div class="text-4xl font-bold text-blue-600 mb-2">{{ stats.cities }}+</div>
            <div class="text-gray-600">Villes</div>
          </div>
          <div>
            <div class="text-4xl font-bold text-blue-600 mb-2">{{ stats.agencies }}+</div>
            <div class="text-gray-600">Agences</div>
          </div>
          <div>
            <div class="text-4xl font-bold text-blue-600 mb-2">{{ stats.satisfaction }}%</div>
            <div class="text-gray-600">Satisfaction</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Types de biens populaires -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <h2 class="text-3xl font-semibold text-gray-900 mb-8 text-center">
        Types de biens populaires
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
        <button
          v-for="type in propertyTypes"
          :key="type.name"
          @click="handleTypeClick(type.name)"
          class="bg-white rounded-xl p-6 shadow-sm hover:shadow-md transition-all text-center group"
        >
          <div class="text-4xl mb-3">{{ type.icon }}</div>
          <div class="font-semibold text-gray-900 group-hover:text-blue-600 transition-colors">
            {{ type.name }}
          </div>
          <div class="text-sm text-gray-500 mt-1">{{ type.count }} disponibles</div>
        </button>
      </div>
    </section>

    <!-- Villes populaires -->
    <section class="bg-gray-50 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 class="text-3xl font-semibold text-gray-900 mb-8 text-center">
          Villes populaires
        </h2>
        <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
          <button
            v-for="city in popularCities"
            :key="city.name"
            @click="searchQuery = city.name; handleSearch()"
            class="bg-white rounded-lg p-4 shadow-sm hover:shadow-md transition-all text-center"
          >
            <div class="font-medium text-gray-900">{{ city.name }}</div>
            <div class="text-sm text-gray-500 mt-1">{{ city.count }} biens</div>
          </button>
        </div>
      </div>
    </section>

    <!-- Témoignages -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <h2 class="text-3xl font-semibold text-gray-900 mb-8 text-center">
        Ce que disent nos utilisateurs
      </h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div
          v-for="testimonial in testimonials"
          :key="testimonial.id"
          class="bg-white rounded-xl p-6 shadow-sm"
        >
          <div class="flex items-center gap-1 mb-4">
            <span
              v-for="i in 5"
              :key="i"
              class="text-yellow-500"
            >
              ★
            </span>
          </div>
          <p class="text-gray-700 mb-4">{{ testimonial.text }}</p>
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
              <span class="text-blue-600 font-semibold">{{ testimonial.initials }}</span>
            </div>
            <div>
              <div class="font-semibold text-gray-900">{{ testimonial.name }}</div>
              <div class="text-sm text-gray-500">{{ testimonial.location }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Final -->
    <section class="bg-blue-600 text-white py-16">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-3xl md:text-4xl font-semibold mb-4">
          Prêt à trouver votre bien idéal ?
        </h2>
        <p class="text-xl text-blue-100 mb-8">
          Commencez votre recherche dès maintenant
        </p>
        <button
          @click="$router.push('/search')"
          class="bg-white text-blue-600 px-8 py-4 rounded-full font-semibold hover:bg-gray-100 transition-colors text-lg"
        >
          Commencer la recherche
        </button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchQuery = ref('')

const stats = ref({
  properties: '10,000+',
  cities: 50,
  agencies: 200,
  satisfaction: 98
})

const propertyTypes = ref([
  { name: 'Appartement', icon: '🏢', count: '3,500+' },
  { name: 'Villa', icon: '🏡', count: '1,200+' },
  { name: 'Studio', icon: '🏘️', count: '2,800+' },
  { name: 'Maison', icon: '🏠', count: '2,500+' }
])

const popularCities = ref([
  { name: 'Paris', count: 2500 },
  { name: 'Lyon', count: 800 },
  { name: 'Marseille', count: 600 },
  { name: 'Bordeaux', count: 500 },
  { name: 'Nice', count: 400 },
  { name: 'Toulouse', count: 350 }
])

const quickSuggestions = ref([
  'Appartement Paris',
  'Villa Côte d\'Azur',
  'Studio étudiant',
  'Maison avec jardin'
])

const testimonials = ref([
  {
    id: 1,
    text: 'J\'ai trouvé mon appartement idéal en moins d\'une semaine. Interface intuitive et résultats pertinents !',
    name: 'Marie D.',
    initials: 'MD',
    location: 'Paris'
  },
  {
    id: 2,
    text: 'Excellent service, beaucoup de choix et des filtres très utiles. Je recommande vivement.',
    name: 'Pierre L.',
    initials: 'PL',
    location: 'Lyon'
  },
  {
    id: 3,
    text: 'La recherche par carte est géniale ! J\'ai pu voir tous les biens disponibles dans mon quartier.',
    name: 'Sophie M.',
    initials: 'SM',
    location: 'Marseille'
  }
])

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({
      path: '/search',
      query: { search: searchQuery.value.trim() }
    })
  } else {
    router.push('/search')
  }
}

function handleTypeClick(type: string) {
  router.push({
    path: '/search',
    query: { type }
  })
}
</script>

