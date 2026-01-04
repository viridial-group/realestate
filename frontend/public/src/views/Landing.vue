<template>
  <div class="min-h-screen bg-gradient-to-b from-blue-50 to-white">
    <!-- Hero Section -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 md:py-24">
      <div class="text-center">
        <!-- Logo/Titre -->
        <div class="mb-8">
          <h1 class="text-5xl md:text-7xl font-bold text-gray-900 mb-6 leading-tight">
            Trouvez votre
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600">bien immobilier</span>
            <br />
            <span class="text-4xl md:text-5xl font-normal">de rêve en France</span>
          </h1>
          <p class="text-xl md:text-2xl text-gray-600 max-w-3xl mx-auto leading-relaxed">
            Plus de <strong class="text-blue-600">10 000 propriétés</strong> vérifiées. 
            <strong class="text-purple-600">Recherche intelligente</strong>, 
            <strong class="text-indigo-600">données de marché DVF</strong> et 
            <strong class="text-blue-600">agences certifiées</strong>.
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
    <section class="bg-gradient-to-b from-white to-gray-50 py-16 border-t border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div class="p-6 bg-white rounded-xl shadow-sm hover:shadow-md transition-all">
            <div class="text-5xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent mb-2">{{ stats.properties }}</div>
            <div class="text-gray-600 font-medium">Propriétés disponibles</div>
            <div class="text-xs text-gray-500 mt-1">Mises à jour quotidiennes</div>
          </div>
          <div class="p-6 bg-white rounded-xl shadow-sm hover:shadow-md transition-all">
            <div class="text-5xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent mb-2">{{ stats.cities }}+</div>
            <div class="text-gray-600 font-medium">Villes couvertes</div>
            <div class="text-xs text-gray-500 mt-1">Toute la France</div>
          </div>
          <div class="p-6 bg-white rounded-xl shadow-sm hover:shadow-md transition-all">
            <div class="text-5xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent mb-2">{{ stats.agencies }}+</div>
            <div class="text-gray-600 font-medium">Agences partenaires</div>
            <div class="text-xs text-gray-500 mt-1">Certifiées et vérifiées</div>
          </div>
          <div class="p-6 bg-white rounded-xl shadow-sm hover:shadow-md transition-all">
            <div class="text-5xl font-bold bg-gradient-to-r from-pink-600 to-red-600 bg-clip-text text-transparent mb-2">{{ stats.satisfaction }}%</div>
            <div class="text-gray-600 font-medium">Satisfaction client</div>
            <div class="text-xs text-gray-500 mt-1">Note moyenne 4.8/5</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Types de biens populaires -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
      <div class="text-center mb-12">
        <h2 class="text-4xl font-bold text-gray-900 mb-4">
          Explorez tous les types de biens
        </h2>
        <p class="text-xl text-gray-600">
          De l'appartement au studio, de la villa au local commercial
        </p>
      </div>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
        <button
          v-for="type in propertyTypes"
          :key="type.name"
          @click="handleTypeClick(type.name)"
          class="bg-white rounded-xl p-8 shadow-sm hover:shadow-xl transition-all text-center group border-2 border-transparent hover:border-blue-200"
        >
          <div class="text-5xl mb-4 transform group-hover:scale-110 transition-transform">{{ type.icon }}</div>
          <div class="font-bold text-lg text-gray-900 group-hover:text-blue-600 transition-colors mb-2">
            {{ type.name }}
          </div>
          <div class="text-sm text-gray-500 font-medium">{{ type.count }} disponibles</div>
          <div class="mt-3 text-xs text-blue-600 opacity-0 group-hover:opacity-100 transition-opacity font-semibold">
            Voir les biens →
          </div>
        </button>
      </div>
    </section>

    <!-- Annonces publicitaires inline -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <AdvertisementSlot
        ad-type="BANNER"
        position="INLINE"
        page-type="LANDING"
      />
    </section>

    <!-- Villes populaires -->
    <section class="bg-gradient-to-b from-gray-50 to-white py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2 class="text-4xl font-bold text-gray-900 mb-4">
            Découvrez les villes les plus recherchées
          </h2>
          <p class="text-xl text-gray-600">
            Des milliers de biens disponibles dans les plus belles villes de France
          </p>
        </div>
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          <button
            v-for="city in popularCities"
            :key="city.name"
            @click="searchQuery = city.name; handleSearch()"
            class="bg-white rounded-xl p-6 shadow-sm hover:shadow-lg transition-all text-center group border-2 border-transparent hover:border-blue-200"
          >
            <div class="text-3xl mb-2">📍</div>
            <div class="font-bold text-lg text-gray-900 group-hover:text-blue-600 transition-colors">{{ city.name }}</div>
            <div class="text-sm text-gray-500 mt-2 font-medium">{{ city.count }} biens</div>
            <div class="mt-3 text-xs text-blue-600 opacity-0 group-hover:opacity-100 transition-opacity font-semibold">
              Explorer →
            </div>
          </button>
        </div>
      </div>
    </section>

    <!-- Pourquoi nous choisir -->
    <section class="bg-gradient-to-b from-gray-50 to-white py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2 class="text-4xl font-bold text-gray-900 mb-4">
            Pourquoi choisir Viridial ?
          </h2>
          <p class="text-xl text-gray-600 max-w-2xl mx-auto">
            La plateforme immobilière la plus complète et innovante de France
          </p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          <div
            v-for="feature in features"
            :key="feature.id"
            class="bg-white rounded-xl p-6 shadow-sm hover:shadow-lg transition-all group"
          >
            <div class="w-14 h-14 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
              <span class="text-2xl">{{ feature.icon }}</span>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-2">{{ feature.title }}</h3>
            <p class="text-gray-600 leading-relaxed">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Témoignages -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div class="text-center mb-12">
        <h2 class="text-4xl font-bold text-gray-900 mb-4">
          Ce que disent nos utilisateurs
        </h2>
        <p class="text-xl text-gray-600">
          Plus de 50 000 utilisateurs satisfaits nous font confiance
        </p>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div
          v-for="testimonial in testimonials"
          :key="testimonial.id"
          class="bg-white rounded-xl p-6 shadow-sm hover:shadow-md transition-all border border-gray-100"
        >
          <div class="flex items-center gap-1 mb-4">
            <span
              v-for="i in 5"
              :key="i"
              class="text-yellow-500 text-lg"
            >
              ★
            </span>
          </div>
          <p class="text-gray-700 mb-6 leading-relaxed italic">"{{ testimonial.text }}"</p>
          <div class="flex items-center gap-3 pt-4 border-t border-gray-100">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
              <span class="text-white font-semibold text-sm">{{ testimonial.initials }}</span>
            </div>
            <div>
              <div class="font-semibold text-gray-900">{{ testimonial.name }}</div>
              <div class="text-sm text-gray-500">{{ testimonial.location }}</div>
              <div class="text-xs text-gray-400 mt-1">{{ testimonial.type }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Garanties et certifications -->
    <section class="bg-white border-t border-gray-200 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">
            Vos garanties de confiance
          </h2>
          <p class="text-lg text-gray-600">
            Nous nous engageons à vous offrir la meilleure expérience immobilière
          </p>
        </div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div
            v-for="guarantee in guarantees"
            :key="guarantee.id"
            class="text-center p-6 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <div class="text-4xl mb-3">{{ guarantee.icon }}</div>
            <h3 class="font-semibold text-gray-900 mb-2">{{ guarantee.title }}</h3>
            <p class="text-sm text-gray-600">{{ guarantee.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Processus simple -->
    <section class="bg-gradient-to-b from-blue-50 to-white py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2 class="text-4xl font-bold text-gray-900 mb-4">
            Trouvez votre bien en 3 étapes simples
          </h2>
          <p class="text-xl text-gray-600">
            Un processus rapide et intuitif pour vous faire gagner du temps
          </p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8 relative">
          <!-- Ligne de connexion -->
          <div class="hidden md:block absolute top-20 left-1/3 right-1/3 h-0.5 bg-blue-200 -z-10"></div>
          <div
            v-for="(step, index) in processSteps"
            :key="step.id"
            class="relative bg-white rounded-xl p-8 shadow-lg hover:shadow-xl transition-all"
          >
            <div class="absolute -top-6 left-1/2 transform -translate-x-1/2 w-12 h-12 bg-blue-600 text-white rounded-full flex items-center justify-center font-bold text-xl shadow-lg">
              {{ index + 1 }}
            </div>
            <div class="text-5xl mb-4 text-center">{{ step.icon }}</div>
            <h3 class="text-xl font-semibold text-gray-900 mb-3 text-center">{{ step.title }}</h3>
            <p class="text-gray-600 text-center leading-relaxed">{{ step.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Final -->
    <section class="bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 text-white py-20 relative overflow-hidden">
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-0 left-0 w-96 h-96 bg-white rounded-full -translate-x-1/2 -translate-y-1/2"></div>
        <div class="absolute bottom-0 right-0 w-96 h-96 bg-white rounded-full translate-x-1/2 translate-y-1/2"></div>
      </div>
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center relative z-10">
        <h2 class="text-4xl md:text-5xl font-bold mb-6">
          Prêt à trouver votre bien idéal ?
        </h2>
        <p class="text-xl text-blue-100 mb-10 max-w-2xl mx-auto">
          Rejoignez plus de 50 000 utilisateurs qui ont déjà trouvé leur bien immobilier de rêve grâce à Viridial
        </p>
        <div class="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <button
            @click="router.push('/search')"
            class="bg-white text-blue-600 px-10 py-5 rounded-full font-bold hover:bg-gray-100 transition-all text-lg shadow-xl hover:shadow-2xl transform hover:scale-105"
          >
            🏠 Commencer la recherche
          </button>
          <router-link
            to="/subscribe"
            class="bg-transparent border-2 border-white text-white px-10 py-5 rounded-full font-bold hover:bg-white hover:text-blue-600 transition-all text-lg shadow-xl hover:shadow-2xl transform hover:scale-105"
          >
            ⭐ S'abonner maintenant
          </router-link>
        </div>
        <p class="text-blue-100 text-sm mt-6">
          ✓ Gratuit pour les acheteurs • ✓ Plus de 10 000 biens disponibles • ✓ Support 7j/7
        </p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AdvertisementSlot from '@/components/AdvertisementSlot.vue'

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

const features = ref([
  {
    id: 1,
    icon: '🔍',
    title: 'Recherche intelligente',
    description: 'Filtres avancés, recherche par carte interactive, suggestions personnalisées et alertes en temps réel pour ne manquer aucun bien.'
  },
  {
    id: 2,
    icon: '📊',
    title: 'Données de marché DVF',
    description: 'Accès exclusif aux données DVF pour connaître les prix réels du marché, l\'historique des transactions et les tendances immobilières.'
  },
  {
    id: 3,
    icon: '🏆',
    title: 'Agences certifiées',
    description: 'Plus de 200 agences immobilières partenaires vérifiées et certifiées pour vous garantir des biens authentiques et des transactions sécurisées.'
  },
  {
    id: 4,
    icon: '⚡',
    title: 'Rapidité et efficacité',
    description: 'Interface ultra-rapide, résultats instantanés, comparaison de biens en un clic et prise de rendez-vous en ligne simplifiée.'
  }
])

const testimonials = ref([
  {
    id: 1,
    text: 'J\'ai trouvé mon appartement idéal en moins d\'une semaine grâce aux filtres ultra-précis. L\'interface est intuitive et les résultats sont vraiment pertinents. Je recommande à 100% !',
    name: 'Marie Dubois',
    initials: 'MD',
    location: 'Paris 15ème',
    type: 'Acheteuse'
  },
  {
    id: 2,
    text: 'Excellent service ! Beaucoup de choix, des filtres très utiles et surtout les données DVF m\'ont permis de négocier le prix. J\'ai économisé 15 000€ grâce à Viridial.',
    name: 'Pierre Laurent',
    initials: 'PL',
    location: 'Lyon',
    type: 'Acheteur'
  },
  {
    id: 3,
    text: 'La recherche par carte est géniale ! J\'ai pu voir tous les biens disponibles dans mon quartier et comparer les prix facilement. Le système d\'alertes m\'a permis de ne pas rater le bien parfait.',
    name: 'Sophie Martin',
    initials: 'SM',
    location: 'Marseille',
    type: 'Locataire'
  }
])

const guarantees = ref([
  {
    id: 1,
    icon: '✅',
    title: 'Biens vérifiés',
    description: 'Tous nos biens sont vérifiés et authentifiés'
  },
  {
    id: 2,
    icon: '🔒',
    title: 'Données sécurisées',
    description: 'Protection RGPD et sécurité maximale'
  },
  {
    id: 3,
    icon: '💬',
    title: 'Support 7j/7',
    description: 'Assistance disponible tous les jours'
  },
  {
    id: 4,
    icon: '💰',
    title: 'Gratuit',
    description: 'Recherche 100% gratuite pour les acheteurs'
  }
])

const processSteps = ref([
  {
    id: 1,
    icon: '🔎',
    title: 'Recherchez',
    description: 'Utilisez nos filtres avancés ou explorez la carte interactive pour trouver les biens qui correspondent à vos critères.'
  },
  {
    id: 2,
    icon: '📋',
    title: 'Comparez',
    description: 'Ajoutez vos favoris, comparez les prix, consultez les données de marché DVF et analysez l\'historique des prix.'
  },
  {
    id: 3,
    icon: '🤝',
    title: 'Contactez',
    description: 'Prenez rendez-vous directement en ligne avec les agences partenaires ou contactez-les via notre plateforme sécurisée.'
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

