<template>
  <div v-if="loading" class="text-center py-16">
    <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    <p class="mt-4 text-gray-600 dark:text-gray-400 text-lg">Chargement de l'agence...</p>
  </div>

  <div v-else-if="error" class="bg-red-50 dark:bg-red-900/20 border-2 border-red-200 dark:border-red-800 rounded-xl p-6 max-w-7xl mx-auto mt-8">
    <div class="flex items-center gap-3">
      <svg class="h-6 w-6 text-red-600 dark:text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <p class="text-red-800 dark:text-red-200 font-medium">{{ error }}</p>
    </div>
  </div>

  <div v-else-if="agency" class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Breadcrumbs -->
    <div class="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
      <div class="max-w-7xl mx-auto px-4 py-3">
        <nav class="flex items-center space-x-2 text-sm text-gray-600 dark:text-gray-400">
          <router-link to="/" class="hover:text-gray-900 dark:hover:text-white">Accueil</router-link>
          <span>/</span>
          <router-link to="/agencies" class="hover:text-gray-900 dark:hover:text-white">Agences</router-link>
          <span>/</span>
          <span class="text-gray-900 dark:text-white font-medium">{{ agency.name }}</span>
        </nav>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 py-8">
      <!-- En-tête principal (style Google Business) -->
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg overflow-hidden mb-8">
        <div class="p-8">
          <div class="flex flex-col lg:flex-row gap-8">
            <!-- Logo et Note -->
            <div class="flex-shrink-0 flex flex-col items-center lg:items-start">
              <div
                v-if="agency.logoUrl"
                class="w-40 h-40 rounded-2xl bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden shadow-lg mb-4"
              >
                <img :src="agency.logoUrl" :alt="agency.name" class="w-full h-full object-cover" />
              </div>
              <div
                v-else
                class="w-40 h-40 rounded-2xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center shadow-lg mb-4"
              >
                <span class="text-5xl text-white font-bold">{{ agency.name.charAt(0).toUpperCase() }}</span>
              </div>
              
              <!-- Note principale -->
              <div v-if="agency.reviewStats" class="text-center lg:text-left">
                <div class="flex items-center justify-center lg:justify-start gap-2 mb-2">
                  <span class="text-4xl font-bold text-gray-900 dark:text-white">
                    {{ formatRating(agency.reviewStats.averageRating) }}
                  </span>
                  <div class="flex text-yellow-500">
                    <svg v-for="i in 5" :key="i" class="h-8 w-8" :class="i <= Math.round(agency.reviewStats.averageRating) ? 'fill-current' : 'text-gray-300'" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  </div>
                </div>
                <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  <span class="font-semibold text-gray-900 dark:text-white">{{ agency.reviewStats.totalReviews }}</span> avis
                </p>
                <p class="text-xs text-gray-500 dark:text-gray-500">
                  Basé sur {{ agency.reviewStats.totalReviews }} avis vérifiés
                </p>
              </div>
            </div>

            <!-- Informations principales -->
            <div class="flex-1">
              <h1 class="text-4xl md:text-5xl font-bold text-gray-900 dark:text-white mb-4">{{ agency.name }}</h1>
              
              <p v-if="agency.description" class="text-lg text-gray-700 dark:text-gray-300 mb-6">
                {{ agency.description }}
              </p>

              <!-- Informations de contact -->
              <div class="space-y-3 mb-6">
                <div v-if="agency.address || agency.city" class="flex items-start gap-3">
                  <svg class="h-5 w-5 text-gray-400 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <div>
                    <p class="text-gray-900 dark:text-white font-medium">
                      {{ agency.address || '' }}{{ agency.address && agency.city ? ', ' : '' }}{{ agency.city || '' }}{{ agency.postalCode ? ` ${agency.postalCode}` : '' }}
                    </p>
                    <button
                      v-if="agency.address || agency.city"
                      @click="openDirections"
                      class="text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 text-sm font-medium mt-1"
                    >
                      Itinéraire
                    </button>
                  </div>
                </div>

                <div v-if="agency.phone" class="flex items-center gap-3">
                  <svg class="h-5 w-5 text-gray-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                  </svg>
                  <a :href="`tel:${agency.phone}`" class="text-gray-900 dark:text-white hover:text-blue-600 dark:hover:text-blue-400 font-medium">
                    {{ agency.phone }}
                  </a>
                </div>

                <div v-if="agency.email" class="flex items-center gap-3">
                  <svg class="h-5 w-5 text-gray-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                  <a :href="`mailto:${agency.email}`" class="text-gray-900 dark:text-white hover:text-blue-600 dark:hover:text-blue-400 font-medium">
                    {{ agency.email }}
                  </a>
                </div>

                <div v-if="agency.domain" class="flex items-center gap-3">
                  <svg class="h-5 w-5 text-gray-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" />
                  </svg>
                  <a :href="`https://${agency.domain}`" target="_blank" rel="noopener noreferrer" class="text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 font-medium">
                    {{ agency.domain }}
                  </a>
                </div>
              </div>

              <!-- Actions rapides -->
              <div class="flex flex-wrap gap-3">
                <button
                  v-if="agency.phone"
                  @click="callAgency(agency.phone)"
                  class="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl transition-all duration-200 shadow-md hover:shadow-lg flex items-center gap-2"
                >
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                  </svg>
                  Appeler
                </button>
                <button
                  v-if="agency.email"
                  @click="emailAgency(agency.email)"
                  class="px-6 py-3 border-2 border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 font-semibold rounded-xl transition-all duration-200 flex items-center gap-2"
                >
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                  Envoyer un email
                </button>
                <button
                  v-if="agency.address || agency.city"
                  @click="openDirections"
                  class="px-6 py-3 border-2 border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 font-semibold rounded-xl transition-all duration-200 flex items-center gap-2"
                >
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                  </svg>
                  Itinéraire
                </button>
                <button
                  v-if="agency.domain"
                  @click="openWebsite(agency.domain)"
                  class="px-6 py-3 border-2 border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 font-semibold rounded-xl transition-all duration-200 flex items-center gap-2"
                >
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" />
                  </svg>
                  Site web
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Grille principale (style Google Business) -->
      <div class="grid lg:grid-cols-3 gap-8">
        <!-- Colonne principale -->
        <div class="lg:col-span-2 space-y-8">
          <!-- Horaires d'ouverture -->
          <section v-if="formattedOfficeHours.length > 0" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-2xl font-bold text-gray-900 dark:text-white">Horaires d'ouverture</h2>
              <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="space-y-3">
              <div
                v-for="day in formattedOfficeHours"
                :key="day.key"
                class="flex items-center justify-between py-2 border-b border-gray-200 dark:border-gray-700 last:border-0"
                :class="{ 'font-semibold text-blue-600 dark:text-blue-400': day.isToday }"
              >
                <span class="text-gray-700 dark:text-gray-300 font-medium">{{ day.label }}</span>
                <span
                  v-if="day.hours === 'Fermé' || day.hours === 'closed'"
                  class="text-gray-500 dark:text-gray-400 italic"
                >
                  Fermé
                </span>
                <span v-else class="text-gray-900 dark:text-white font-semibold">{{ day.hours }}</span>
              </div>
            </div>
            <p class="mt-4 text-xs text-gray-500 dark:text-gray-500">
              Les horaires peuvent varier selon les périodes. Contactez l'agence pour confirmer.
            </p>
          </section>

          <!-- Carte interactive -->
          <section v-if="agency.address || agency.city" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md overflow-hidden">
            <div class="p-6 border-b border-gray-200 dark:border-gray-700">
              <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">Localisation</h2>
              <p class="text-gray-600 dark:text-gray-400">
                {{ agency.address || '' }}{{ agency.address && agency.city ? ', ' : '' }}{{ agency.city || '' }}{{ agency.postalCode ? ` ${agency.postalCode}` : '' }}
              </p>
            </div>
            <div class="h-96 bg-gray-100 dark:bg-gray-700 relative">
              <!-- Carte Google Maps intégrée -->
              <iframe
                :src="googleMapsUrl"
                width="100%"
                height="100%"
                style="border:0;"
                :allowfullscreen="true"
                loading="lazy"
                referrerpolicy="no-referrer-when-downgrade"
                class="absolute inset-0"
              ></iframe>
            </div>
            <div class="p-4 border-t border-gray-200 dark:border-gray-700">
              <button
                @click="openDirections"
                class="w-full px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                </svg>
                Obtenir l'itinéraire
              </button>
            </div>
          </section>

          <!-- Statistiques de performance (style Zillow) -->
          <section v-if="performanceStats" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-6">Performance</h2>
            
            <div class="grid grid-cols-2 md:grid-cols-4 gap-6 mb-8">
              <div class="text-center p-4 bg-gradient-to-br from-blue-50 to-blue-100 dark:from-blue-900/20 dark:to-blue-800/20 rounded-xl">
                <div class="text-3xl font-bold text-blue-600 dark:text-blue-400 mb-1">
                  {{ performanceStats.totalProperties }}
                </div>
                <div class="text-sm text-gray-600 dark:text-gray-400">Propriétés totales</div>
              </div>
              <div class="text-center p-4 bg-gradient-to-br from-green-50 to-green-100 dark:from-green-900/20 dark:to-green-800/20 rounded-xl">
                <div class="text-3xl font-bold text-green-600 dark:text-green-400 mb-1">
                  {{ performanceStats.soldProperties }}
                </div>
                <div class="text-sm text-gray-600 dark:text-gray-400">Vendues</div>
              </div>
              <div class="text-center p-4 bg-gradient-to-br from-purple-50 to-purple-100 dark:from-purple-900/20 dark:to-purple-800/20 rounded-xl">
                <div class="text-3xl font-bold text-purple-600 dark:text-purple-400 mb-1">
                  {{ formatPrice(performanceStats.averagePrice) }}
                </div>
                <div class="text-sm text-gray-600 dark:text-gray-400">Prix moyen</div>
              </div>
              <div class="text-center p-4 bg-gradient-to-br from-orange-50 to-orange-100 dark:from-orange-900/20 dark:to-orange-800/20 rounded-xl">
                <div class="text-3xl font-bold text-orange-600 dark:text-orange-400 mb-1">
                  {{ performanceStats.newThisMonth }}
                </div>
                <div class="text-sm text-gray-600 dark:text-gray-400">Nouveautés ce mois</div>
              </div>
            </div>

            <!-- Détails supplémentaires -->
            <div class="grid md:grid-cols-2 gap-6 pt-6 border-t border-gray-200 dark:border-gray-700">
              <div>
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Statistiques détaillées</h3>
                <div class="space-y-3">
                  <div class="flex justify-between">
                    <span class="text-gray-600 dark:text-gray-400">Propriétés disponibles</span>
                    <span class="font-semibold text-gray-900 dark:text-white">{{ performanceStats.availableProperties }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600 dark:text-gray-400">Propriétés louées</span>
                    <span class="font-semibold text-gray-900 dark:text-white">{{ performanceStats.rentedProperties }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600 dark:text-gray-400">Prix minimum</span>
                    <span class="font-semibold text-gray-900 dark:text-white">{{ formatPrice(performanceStats.minPrice) }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600 dark:text-gray-400">Prix maximum</span>
                    <span class="font-semibold text-gray-900 dark:text-white">{{ formatPrice(performanceStats.maxPrice) }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600 dark:text-gray-400">Surface moyenne</span>
                    <span class="font-semibold text-gray-900 dark:text-white">{{ Math.round(performanceStats.averageSurface) }} m²</span>
                  </div>
                </div>
              </div>
              <div>
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Répartition par type</h3>
                <div class="space-y-2">
                  <div
                    v-for="(count, type) in performanceStats.byType"
                    :key="type"
                    class="flex items-center justify-between"
                  >
                    <span class="text-gray-600 dark:text-gray-400">{{ type }}</span>
                    <div class="flex items-center gap-2">
                      <div class="w-24 bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                        <div
                          class="bg-blue-500 h-2 rounded-full"
                          :style="{ width: `${getPercentage(count, performanceStats.totalProperties)}%` }"
                        ></div>
                      </div>
                      <span class="text-sm font-semibold text-gray-900 dark:text-white w-8 text-right">{{ count }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Graphiques de performance -->
          <section v-if="performanceStats" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-6">Analyse de performance</h2>
            
            <div class="grid md:grid-cols-2 gap-8">
              <!-- Graphique répartition par statut -->
              <div>
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Répartition par statut</h3>
                <div class="space-y-4">
                  <div
                    v-for="(count, status) in performanceStats.byStatus"
                    :key="status"
                    class="flex items-center gap-4"
                  >
                    <div class="w-32">
                      <span class="text-sm text-gray-600 dark:text-gray-400 capitalize">{{ getStatusLabel(status) }}</span>
                    </div>
                    <div class="flex-1 bg-gray-200 dark:bg-gray-700 rounded-full h-4 overflow-hidden">
                      <div
                        class="h-4 rounded-full transition-all duration-500"
                        :class="getStatusColor(status)"
                        :style="{ width: `${getPercentage(count, performanceStats.totalProperties)}%` }"
                      ></div>
                    </div>
                    <span class="text-sm font-semibold text-gray-900 dark:text-white w-12 text-right">{{ count }}</span>
                  </div>
                </div>
              </div>

              <!-- Graphique répartition par ville -->
              <div v-if="performanceStats.byCity && Object.keys(performanceStats.byCity).length > 0">
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Top villes</h3>
                <div class="space-y-3">
                  <div
                    v-for="(count, city) in getTopCities(performanceStats.byCity, 5)"
                    :key="city"
                    class="flex items-center gap-4"
                  >
                    <div class="w-32 truncate">
                      <span class="text-sm text-gray-600 dark:text-gray-400">{{ city }}</span>
                    </div>
                    <div class="flex-1 bg-gray-200 dark:bg-gray-700 rounded-full h-3 overflow-hidden">
                      <div
                        class="bg-gradient-to-r from-blue-500 to-purple-600 h-3 rounded-full transition-all duration-500"
                        :style="{ width: `${getPercentage(count, performanceStats.totalProperties)}%` }"
                      ></div>
                    </div>
                    <span class="text-sm font-semibold text-gray-900 dark:text-white w-8 text-right">{{ count }}</span>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Propriétés récentes -->
          <section v-if="recentProperties.length > 0" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-2xl font-bold text-gray-900 dark:text-white">Propriétés récentes</h2>
              <router-link
                :to="`/search?organizationId=${agencyId}`"
                class="text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 font-medium text-sm"
              >
                Voir toutes les propriétés →
              </router-link>
            </div>
            
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div
                v-for="property in recentProperties"
                :key="property.id"
                class="bg-gray-50 dark:bg-gray-700 rounded-xl overflow-hidden hover:shadow-lg transition-all duration-200 cursor-pointer"
                @click="goToProperty(property.id)"
              >
                <div class="h-48 bg-gradient-to-br from-gray-200 to-gray-300 dark:from-gray-600 dark:to-gray-700 flex items-center justify-center">
                  <svg class="h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                  </svg>
                </div>
                <div class="p-4">
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-2 line-clamp-2">{{ property.title }}</h3>
                  <div class="flex items-center justify-between mb-2">
                    <span class="text-lg font-bold text-blue-600 dark:text-blue-400">
                      {{ formatPrice(property.price) }}
                    </span>
                    <span
                      class="px-2 py-1 text-xs font-semibold rounded"
                      :class="getStatusBadgeClass(property.status)"
                    >
                      {{ getStatusLabel(property.status) }}
                    </span>
                  </div>
                  <div class="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                    <span v-if="property.surface">{{ Math.round(property.surface) }} m²</span>
                    <span v-if="property.bedrooms">{{ property.bedrooms }} ch.</span>
                    <span v-if="property.city">{{ property.city }}</span>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Zones desservies et spécialités -->
          <div class="grid md:grid-cols-2 gap-6">
            <!-- Zones desservies -->
            <section v-if="performanceStats && performanceStats.servedCities && performanceStats.servedCities.length > 0" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
              <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-4">Zones desservies</h2>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="city in performanceStats.servedCities"
                  :key="city"
                  class="px-3 py-1.5 bg-blue-50 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 rounded-lg text-sm font-medium"
                >
                  {{ city }}
                </span>
              </div>
            </section>

            <!-- Spécialités -->
            <section v-if="performanceStats && performanceStats.propertyTypes && performanceStats.propertyTypes.length > 0" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
              <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-4">Types de propriétés</h2>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="type in performanceStats.propertyTypes"
                  :key="type"
                  class="px-3 py-1.5 bg-purple-50 dark:bg-purple-900/20 text-purple-700 dark:text-purple-300 rounded-lg text-sm font-medium"
                >
                  {{ type }}
                </span>
              </div>
            </section>
          </div>

          <!-- Services offerts -->
          <section class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-6">Services offerts</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div class="flex items-start gap-3 p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <svg class="h-6 w-6 text-blue-600 dark:text-blue-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
                <div>
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">Vente immobilière</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Accompagnement complet pour la vente de votre bien</p>
                </div>
              </div>
              <div class="flex items-start gap-3 p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <svg class="h-6 w-6 text-green-600 dark:text-green-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <div>
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">Location</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Gestion locative et recherche de locataires</p>
                </div>
              </div>
              <div class="flex items-start gap-3 p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <svg class="h-6 w-6 text-purple-600 dark:text-purple-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
                <div>
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">Évaluation</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Estimation gratuite de votre bien immobilier</p>
                </div>
              </div>
              <div class="flex items-start gap-3 p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <svg class="h-6 w-6 text-orange-600 dark:text-orange-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <div>
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">Conseil financier</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Accompagnement pour vos projets d'achat</p>
                </div>
              </div>
              <div class="flex items-start gap-3 p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <svg class="h-6 w-6 text-red-600 dark:text-red-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                <div>
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">Visites virtuelles</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Découvrez les biens à distance</p>
                </div>
              </div>
              <div class="flex items-start gap-3 p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <svg class="h-6 w-6 text-indigo-600 dark:text-indigo-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <div>
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">Gestion de dossier</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Suivi complet de votre transaction</p>
                </div>
              </div>
            </div>
          </section>

          <!-- Statistiques d'avis détaillées -->
          <section v-if="agency.reviewStats" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-6">Statistiques des avis</h2>
            
            <div class="grid md:grid-cols-2 gap-8">
              <!-- Distribution des notes -->
              <div v-if="agency.reviewStats.ratingDistribution">
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Répartition des notes</h3>
                <div class="space-y-4">
                  <div
                    v-for="count in [
                      { stars: 5, count: agency.reviewStats.ratingDistribution.fiveStars, label: '5 étoiles' },
                      { stars: 4, count: agency.reviewStats.ratingDistribution.fourStars, label: '4 étoiles' },
                      { stars: 3, count: agency.reviewStats.ratingDistribution.threeStars, label: '3 étoiles' },
                      { stars: 2, count: agency.reviewStats.ratingDistribution.twoStars, label: '2 étoiles' },
                      { stars: 1, count: agency.reviewStats.ratingDistribution.oneStar, label: '1 étoile' }
                    ]"
                    :key="count.stars"
                    class="flex items-center gap-4"
                  >
                    <div class="flex items-center gap-2 w-24">
                      <span class="text-sm text-gray-600 dark:text-gray-400">{{ count.stars }}</span>
                      <svg class="h-4 w-4 text-yellow-500 fill-current" viewBox="0 0 20 20">
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                      </svg>
                    </div>
                    <div class="flex-1 bg-gray-200 dark:bg-gray-700 rounded-full h-3 overflow-hidden">
                      <div
                        class="bg-yellow-500 h-3 rounded-full transition-all duration-500"
                        :style="{ width: `${getPercentage(count.count, agency.reviewStats.totalReviews)}%` }"
                      ></div>
                    </div>
                    <span class="text-sm font-semibold text-gray-900 dark:text-white w-12 text-right">{{ count.count }}</span>
                  </div>
                </div>
              </div>

              <!-- Autres statistiques -->
              <div>
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Informations</h3>
                <div class="space-y-4">
                  <div class="flex justify-between items-center py-2 border-b border-gray-200 dark:border-gray-700">
                    <span class="text-gray-600 dark:text-gray-400">Note moyenne</span>
                    <div class="flex items-center gap-2">
                      <span class="text-2xl font-bold text-gray-900 dark:text-white">
                        {{ formatRating(agency.reviewStats.averageRating) }}
                      </span>
                      <span class="text-gray-500 dark:text-gray-400">/ 5</span>
                    </div>
                  </div>
                  <div class="flex justify-between items-center py-2 border-b border-gray-200 dark:border-gray-700">
                    <span class="text-gray-600 dark:text-gray-400">Total d'avis</span>
                    <span class="text-xl font-bold text-gray-900 dark:text-white">
                      {{ agency.reviewStats.totalReviews }}
                    </span>
                  </div>
                  <div v-if="agency.reviewStats.verifiedClientPercentage" class="flex justify-between items-center py-2 border-b border-gray-200 dark:border-gray-700">
                    <span class="text-gray-600 dark:text-gray-400">Clients vérifiés</span>
                    <span class="text-xl font-bold text-green-600 dark:text-green-400">
                      {{ agency.reviewStats.verifiedClientPercentage.toFixed(1) }}%
                    </span>
                  </div>
                  <div v-if="agency.propertyCount" class="flex justify-between items-center py-2">
                    <span class="text-gray-600 dark:text-gray-400">Propriétés en ligne</span>
                    <span class="text-xl font-bold text-blue-600 dark:text-blue-400">
                      {{ agency.propertyCount }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Liste des avis avec filtres -->
          <section class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-6">
              <div>
                <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-1">
                  Avis clients
                </h2>
                <p class="text-gray-600 dark:text-gray-400">
                  {{ reviews.totalElements }} avis au total
                </p>
              </div>
              <div class="flex gap-3">
                <!-- Filtres d'avis -->
                <select
                  v-model="reviewFilter"
                  @change="applyReviewFilter"
                  class="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white text-sm"
                >
                  <option value="all">Tous les avis</option>
                  <option value="recent">Plus récents</option>
                  <option value="highest">Note la plus élevée</option>
                  <option value="lowest">Note la plus basse</option>
                  <option value="verified">Clients vérifiés</option>
                </select>
                <button
                  @click="showReviewForm = true"
                  class="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors shadow-md hover:shadow-lg"
                >
                  Laisser un avis
                </button>
              </div>
            </div>

            <!-- Avis -->
            <div v-if="reviewsLoading" class="text-center py-12">
              <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>

            <div v-else-if="filteredReviews.length > 0" class="space-y-6">
              <div
                v-for="review in filteredReviews"
                :key="review.id"
                class="border-b border-gray-200 dark:border-gray-700 pb-6 last:border-0 last:pb-0"
              >
                <div class="flex items-start justify-between mb-3">
                  <div class="flex-1">
                    <div class="flex items-center gap-3 mb-2">
                      <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold">
                        {{ (review.authorName || 'A')[0].toUpperCase() }}
                      </div>
                      <div>
                        <div class="flex items-center gap-2">
                          <span class="font-semibold text-gray-900 dark:text-white">
                            {{ review.authorName || 'Anonyme' }}
                          </span>
                          <span
                            v-if="review.verifiedClient"
                            class="px-2 py-0.5 bg-green-100 dark:bg-green-900/20 text-green-800 dark:text-green-200 text-xs rounded-full font-medium"
                          >
                            ✓ Client vérifié
                          </span>
                        </div>
                        <div class="flex items-center gap-1 mt-1">
                          <div class="flex text-yellow-500">
                            <svg v-for="i in 5" :key="i" class="h-4 w-4" :class="i <= review.rating ? 'fill-current' : 'text-gray-300'" viewBox="0 0 20 20">
                              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                            </svg>
                          </div>
                          <span class="text-xs text-gray-500 dark:text-gray-400 ml-2">
                            {{ formatDate(review.createdAt) }}
                          </span>
                          <span v-if="review.transactionType" class="text-xs text-gray-500 dark:text-gray-400">
                            • {{ review.transactionType === 'SALE' ? 'Achat' : 'Location' }}
                          </span>
                        </div>
                      </div>
                    </div>
                    <p class="text-gray-700 dark:text-gray-300 leading-relaxed">{{ review.comment }}</p>
                    <div class="flex items-center gap-4 mt-4">
                      <button
                        @click="markAsHelpful(review.id)"
                        class="text-sm text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400 flex items-center gap-1 font-medium transition-colors"
                      >
                        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v5m7 10h.01M7 20h.01" />
                        </svg>
                        Utile ({{ review.helpfulCount }})
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Pagination améliorée -->
              <div v-if="reviews.totalPages > 1" class="flex items-center justify-center gap-2 pt-6 border-t border-gray-200 dark:border-gray-700">
                <button
                  @click="loadReviews(currentPage - 1)"
                  :disabled="currentPage === 0"
                  class="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                >
                  Précédent
                </button>
                <span class="text-sm text-gray-600 dark:text-gray-400 px-4">
                  Page {{ currentPage + 1 }} sur {{ reviews.totalPages }}
                </span>
                <button
                  @click="loadReviews(currentPage + 1)"
                  :disabled="currentPage >= reviews.totalPages - 1"
                  class="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                >
                  Suivant
                </button>
              </div>
            </div>

            <div v-else class="text-center py-12">
              <svg class="mx-auto h-16 w-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              <p class="text-gray-600 dark:text-gray-400 text-lg font-medium mb-2">Aucun avis pour le moment</p>
              <button
                @click="showReviewForm = true"
                class="mt-4 px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors shadow-md hover:shadow-lg"
              >
                Soyez le premier à laisser un avis
              </button>
            </div>
          </section>
        </div>

        <!-- Colonne latérale -->
        <div class="space-y-6">
          <!-- À propos -->
          <section class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <h2 class="text-xl font-bold text-gray-900 dark:text-white mb-4">À propos</h2>
            <div class="space-y-4">
              <div v-if="agency.description" class="text-gray-700 dark:text-gray-300">
                {{ agency.description }}
              </div>
              <div v-if="agency.propertyCount" class="pt-4 border-t border-gray-200 dark:border-gray-700">
                <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">Propriétés en ligne</p>
                <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ agency.propertyCount }}</p>
              </div>
              <div v-if="agency.reviewStats" class="pt-4 border-t border-gray-200 dark:border-gray-700">
                <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">Note moyenne</p>
                <div class="flex items-center gap-2">
                  <span class="text-2xl font-bold text-gray-900 dark:text-white">
                    {{ formatRating(agency.reviewStats.averageRating) }}
                  </span>
                  <div class="flex text-yellow-500">
                    <svg v-for="i in 5" :key="i" class="h-5 w-5" :class="i <= Math.round(agency.reviewStats.averageRating) ? 'fill-current' : 'text-gray-300'" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  </div>
                </div>
                <p class="text-sm text-gray-500 dark:text-gray-500 mt-1">
                  Basé sur {{ agency.reviewStats.totalReviews }} avis
                </p>
              </div>
            </div>
          </section>

          <!-- Statistiques rapides -->
          <section v-if="agency.reviewStats && agency.reviewStats.ratingDistribution" class="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-6">
            <h2 class="text-xl font-bold text-gray-900 dark:text-white mb-4">Résumé</h2>
            <div class="space-y-3">
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600 dark:text-gray-400">5 étoiles</span>
                <span class="font-semibold text-gray-900 dark:text-white">
                  {{ agency.reviewStats.ratingDistribution.fiveStars }}
                </span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600 dark:text-gray-400">4 étoiles</span>
                <span class="font-semibold text-gray-900 dark:text-white">
                  {{ agency.reviewStats.ratingDistribution.fourStars }}
                </span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600 dark:text-gray-400">3 étoiles</span>
                <span class="font-semibold text-gray-900 dark:text-white">
                  {{ agency.reviewStats.ratingDistribution.threeStars }}
                </span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600 dark:text-gray-400">2 étoiles</span>
                <span class="font-semibold text-gray-900 dark:text-white">
                  {{ agency.reviewStats.ratingDistribution.twoStars }}
                </span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600 dark:text-gray-400">1 étoile</span>
                <span class="font-semibold text-gray-900 dark:text-white">
                  {{ agency.reviewStats.ratingDistribution.oneStar }}
                </span>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>

    <!-- Formulaire d'avis -->
    <OrganizationReviewForm
      v-if="showReviewForm"
      :organization-id="agency.id"
      @close="showReviewForm = false"
      @submitted="handleReviewSubmitted"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { organizationReviewService, type OrganizationWithReviews, type OrganizationReview, type OrganizationPerformanceStats } from '@viridial/shared'
import { useSEO } from '@/composables/useSEO'
import OrganizationReviewForm from '@/components/OrganizationReviewForm.vue'
import ContactAgencyForm from '@/components/ContactAgencyForm.vue'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'

const route = useRoute()
const router = useRouter()
const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'

const agency = ref<OrganizationWithReviews | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const reviews = ref<{ content: OrganizationReview[]; totalElements: number; totalPages: number }>({
  content: [],
  totalElements: 0,
  totalPages: 0
})
const reviewsLoading = ref(false)
const currentPage = ref(0)
const showReviewForm = ref(false)
const reviewFilter = ref('all')
const performanceStats = ref<OrganizationPerformanceStats | null>(null)
const performanceLoading = ref(false)
const recentProperties = ref<PublicProperty[]>([])
const propertiesLoading = ref(false)

const agencyId = computed(() => Number(route.params.id))

// Horaires d'ouverture formatés
const daysOfWeek = [
  { key: 'monday', label: 'Lundi' },
  { key: 'tuesday', label: 'Mardi' },
  { key: 'wednesday', label: 'Mercredi' },
  { key: 'thursday', label: 'Jeudi' },
  { key: 'friday', label: 'Vendredi' },
  { key: 'saturday', label: 'Samedi' },
  { key: 'sunday', label: 'Dimanche' }
]

const officeHoursData = computed(() => {
  if (!agency.value?.defaultOfficeHours) return null
  try {
    return JSON.parse(agency.value.defaultOfficeHours)
  } catch {
    return null
  }
})

const formattedOfficeHours = computed(() => {
  if (!officeHoursData.value) return []
  
  const today = new Date().getDay()
  const dayIndex = today === 0 ? 6 : today - 1 // Convertir dimanche (0) à 6
  
  return daysOfWeek.map((day, index) => {
    const hours = officeHoursData.value[day.key]
    return {
      key: day.key,
      label: day.label,
      hours: hours === 'closed' ? 'Fermé' : (hours || 'Fermé'),
      isToday: index === dayIndex
    }
  })
})

// URL Google Maps
const googleMapsUrl = computed(() => {
  if (!agency.value) return ''
  const address = `${agency.value.address || ''} ${agency.value.city || ''} ${agency.value.postalCode || ''}`.trim()
  if (!address) return ''
  return `https://www.google.com/maps/embed/v1/place?key=AIzaSyBFw0Qbyq9zTFTd-tUY6d-s6U4Uc3YhFk&q=${encodeURIComponent(address)}`
})

// Avis filtrés
const filteredReviews = computed(() => {
  if (!reviews.value.content) return []
  
  let filtered = [...reviews.value.content]
  
  switch (reviewFilter.value) {
    case 'recent':
      return filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    case 'highest':
      return filtered.sort((a, b) => b.rating - a.rating)
    case 'lowest':
      return filtered.sort((a, b) => a.rating - b.rating)
    case 'verified':
      return filtered.filter(r => r.verifiedClient)
    default:
      return filtered
  }
})

async function loadPerformanceStats() {
  if (!agencyId.value) return
  performanceLoading.value = true
  try {
    const stats = await organizationReviewService.getPerformanceStats(agencyId.value)
    performanceStats.value = stats
  } catch (err: any) {
    console.error('Error loading performance stats:', err)
  } finally {
    performanceLoading.value = false
  }
}

function formatPrice(price: number): string {
  if (!price || price === 0) return 'N/A'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price)
}

onMounted(async () => {
  await loadAgency()
  await loadReviews(0)
  await loadPerformanceStats()
  await loadRecentProperties()
})

async function loadAgency() {
  loading.value = true
  error.value = null

  try {
    const found = await organizationReviewService.getOrganizationWithReviewsById(agencyId.value)
    
    if (!found) {
      error.value = 'Agence non trouvée'
      return
    }

    agency.value = found

    // SEO
    useSEO({
      title: `${found.name} - Agence Immobilière | Viridial`,
      description: `${found.description || found.name}. Note: ${formatRating(found.reviewStats?.averageRating)}/5 - ${found.reviewStats?.totalReviews || 0} avis. ${found.city ? `Agence immobilière à ${found.city}` : ''}`,
      keywords: ['agence immobilière', found.name, found.city || '', 'avis agence'],
      type: 'website',
      canonical: `${siteUrl}/agencies/${found.id}`,
      url: `${siteUrl}/agencies/${found.id}`
    })
  } catch (err: any) {
    console.error('Error loading agency:', err)
    error.value = err.message || 'Une erreur est survenue'
  } finally {
    loading.value = false
  }
}

async function loadReviews(page: number) {
  reviewsLoading.value = true
  currentPage.value = page

  try {
    const result = await organizationReviewService.getApprovedReviews(agencyId.value, page, 10)
    reviews.value = result
  } catch (err: any) {
    console.error('Error loading reviews:', err)
  } finally {
    reviewsLoading.value = false
  }
}

function applyReviewFilter() {
  // Le filtrage est géré par computed property filteredReviews
}

async function markAsHelpful(reviewId: number) {
  try {
    await organizationReviewService.markAsHelpful(reviewId)
    await loadReviews(currentPage.value)
  } catch (err: any) {
    console.error('Error marking review as helpful:', err)
  }
}

function handleReviewSubmitted() {
  showReviewForm.value = false
  loadReviews(0)
  loadAgency()
}

function handleContactSuccess() {
  // Optionnel : afficher une notification ou recharger les données
  console.log('Contact message sent successfully')
}

function formatRating(rating?: number): string {
  if (rating === undefined || rating === null) return 'N/A'
  return rating.toFixed(1)
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now.getTime() - date.getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) return "Aujourd'hui"
  if (diffDays === 1) return "Hier"
  if (diffDays < 7) return `Il y a ${diffDays} jours`
  if (diffDays < 30) return `Il y a ${Math.floor(diffDays / 7)} semaine${Math.floor(diffDays / 7) > 1 ? 's' : ''}`
  if (diffDays < 365) return `Il y a ${Math.floor(diffDays / 30)} mois`
  
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

function getPercentage(count: number, total: number): number {
  if (total === 0) return 0
  return Math.round((count / total) * 100)
}

function callAgency(phone: string) {
  window.location.href = `tel:${phone}`
}

function emailAgency(email: string) {
  window.location.href = `mailto:${email}`
}

function openWebsite(domain: string) {
  window.open(`https://${domain}`, '_blank', 'noopener,noreferrer')
}

function openDirections() {
  if (!agency.value) return
  const address = `${agency.value.address || ''} ${agency.value.city || ''} ${agency.value.postalCode || ''}`.trim()
  if (!address) return
  window.open(`https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(address)}`, '_blank', 'noopener,noreferrer')
}

async function loadRecentProperties() {
  if (!agencyId.value) return
  propertiesLoading.value = true
  try {
    const response = await publicPropertyService.getPublishedProperties({
      organizationId: agencyId.value,
      page: 0,
      size: 6
    })
    recentProperties.value = response.content || []
  } catch (err: any) {
    console.error('Error loading recent properties:', err)
  } finally {
    propertiesLoading.value = false
  }
}

function goToProperty(propertyId: number) {
  router.push(`/property/${propertyId}`)
}

function getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    'AVAILABLE': 'Disponible',
    'PUBLISHED': 'Publié',
    'SOLD': 'Vendu',
    'RENTED': 'Loué',
    'PENDING': 'En attente',
    'DRAFT': 'Brouillon'
  }
  return labels[status] || status
}

function getStatusColor(status: string): string {
  const colors: Record<string, string> = {
    'AVAILABLE': 'bg-green-500',
    'PUBLISHED': 'bg-blue-500',
    'SOLD': 'bg-purple-500',
    'RENTED': 'bg-orange-500',
    'PENDING': 'bg-yellow-500',
    'DRAFT': 'bg-gray-500'
  }
  return colors[status] || 'bg-gray-500'
}

function getStatusBadgeClass(status: string): string {
  const classes: Record<string, string> = {
    'AVAILABLE': 'bg-green-100 dark:bg-green-900/20 text-green-800 dark:text-green-200',
    'PUBLISHED': 'bg-blue-100 dark:bg-blue-900/20 text-blue-800 dark:text-blue-200',
    'SOLD': 'bg-purple-100 dark:bg-purple-900/20 text-purple-800 dark:text-purple-200',
    'RENTED': 'bg-orange-100 dark:bg-orange-900/20 text-orange-800 dark:text-orange-200',
    'PENDING': 'bg-yellow-100 dark:bg-yellow-900/20 text-yellow-800 dark:text-yellow-200',
    'DRAFT': 'bg-gray-100 dark:bg-gray-900/20 text-gray-800 dark:text-gray-200'
  }
  return classes[status] || 'bg-gray-100 dark:bg-gray-900/20 text-gray-800 dark:text-gray-200'
}

function getTopCities(cities: Record<string, number>, limit: number): Record<string, number> {
  const entries = Object.entries(cities)
    .sort((a, b) => b[1] - a[1])
    .slice(0, limit)
  const result: Record<string, number> = {}
  entries.forEach(([key, value]) => {
    result[key] = value
  })
  return result
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
