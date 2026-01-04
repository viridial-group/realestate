<template>
  <div v-if="loading" class="max-w-7xl mx-auto px-4 py-8">
    <SkeletonLoader type="property-detail" />
  </div>

  <ErrorMessage
    v-else-if="error"
    title="Erreur de chargement"
    :message="error"
    :show-retry="true"
    :show-home="true"
    @retry="loadProperty"
    @go-home="router.push('/search')"
  />

  <div v-else-if="property" class="max-w-7xl mx-auto px-4 py-8">
    <!-- Header avec bouton retour -->
    <nav class="mb-6" aria-label="Navigation">
      <Button variant="ghost" size="sm" as-child>
        <router-link
          to="/search"
          class="flex items-center gap-2 no-underline"
          aria-label="Retour à la recherche"
        >
          <ArrowLeft class="h-4 w-4" />
          <span>Retour à la recherche</span>
        </router-link>
      </Button>
    </nav>

    <!-- En-tête avec titre et partage -->
    <Card class="mb-6">
      <CardHeader>
        <div class="flex items-start justify-between gap-4">
          <div class="flex-1">
            <div class="flex items-center gap-3 mb-4 flex-wrap">
              <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100" itemprop="name">{{ property.title }}</h1>
              <!-- Badge type de transaction proéminent -->
              <Badge
                :class="getTransactionType() === 'Location' ? 'bg-blue-600 hover:bg-blue-700' : 'bg-red-600 hover:bg-red-700'"
                class="text-white text-sm sm:text-base font-semibold shadow-md"
              >
                {{ getTransactionType() }}
              </Badge>
            </div>
            <div class="flex flex-col sm:flex-row sm:items-center gap-4 mb-4">
              <div class="flex items-center gap-2">
                <Euro class="h-5 w-5 text-primary" />
                <p class="text-2xl sm:text-3xl font-bold text-primary">
                  {{ property.price?.toLocaleString('fr-FR') }} €
                </p>
                <span v-if="getTransactionType() === 'Location'" class="text-sm text-gray-600 dark:text-gray-400 font-medium">
                  /mois
                </span>
              </div>
              <Separator orientation="vertical" class="h-6 hidden sm:block" />
              <div class="flex items-center gap-4 flex-wrap">
                <div class="flex items-center gap-1">
                  <Square class="h-4 w-4 text-gray-500" />
                  <span class="text-sm font-medium text-gray-700 dark:text-gray-300">{{ property.surface }} m²</span>
                </div>
                <div v-if="property.bedrooms" class="flex items-center gap-1">
                  <Bed class="h-4 w-4 text-gray-500" />
                  <span class="text-sm font-medium text-gray-700 dark:text-gray-300">{{ property.bedrooms }} chambre{{ property.bedrooms > 1 ? 's' : '' }}</span>
                </div>
                <div v-if="property.bathrooms" class="flex items-center gap-1">
                  <Bath class="h-4 w-4 text-gray-500" />
                  <span class="text-sm font-medium text-gray-700 dark:text-gray-300">{{ property.bathrooms }} salle{{ property.bathrooms > 1 ? 's' : '' }} de bain</span>
                </div>
              </div>
            </div>
            <div class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400" itemprop="address" itemscope itemtype="https://schema.org/PostalAddress">
              <MapPin class="h-4 w-4" />
              <span>
                <span itemprop="streetAddress">{{ property.address || '' }}</span>
                <span v-if="property.city" itemprop="addressLocality">, {{ property.city }}</span>
                <span v-if="property.postalCode" itemprop="postalCode"> {{ property.postalCode }}</span>
                <span v-if="!property.address && !property.city">Non spécifié</span>
                <span itemprop="addressCountry" class="hidden">FR</span>
              </span>
            </div>
          </div>
          <div v-if="property" class="sm:ml-4 w-full sm:w-auto">
            <ShareButtons
              :url="shareUrl"
              :title="property.title"
              :description="property.description"
            />
          </div>
        </div>
      </CardHeader>
    </Card>

    <!-- Offre spéciale -->
    <Card v-if="property.specialOffer" class="mb-6 border-blue-200 bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-950 dark:to-indigo-950">
      <CardContent class="p-4">
        <div class="flex items-center gap-2 mb-2">
          <Gift class="h-5 w-5 text-blue-600" />
          <h3 class="text-lg font-semibold text-blue-900 dark:text-blue-100">Offre spéciale</h3>
        </div>
        <p class="text-blue-800 dark:text-blue-200">{{ property.specialOffer }}</p>
      </CardContent>
    </Card>

    <!-- Galerie d'images améliorée -->
    <div class="mb-8">
      <ImageGallery v-if="images.length > 0" :images="images" />
      <EmptyState
        v-else
        title="Aucune image disponible"
        description="Cette propriété n'a pas encore d'images. Contactez l'agent pour plus d'informations."
        icon="empty"
      />
    </div>

    <!-- Contenu principal -->
    <main class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Colonne principale -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Description -->
        <Card>
          <CardHeader>
            <CardTitle>Description</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-gray-700 dark:text-gray-300 whitespace-pre-line leading-relaxed" itemprop="description">{{ property.description || 'Aucune description disponible.' }}</p>
          </CardContent>
        </Card>

        <!-- Caractéristiques -->
        <Card>
          <CardHeader>
            <CardTitle>Caractéristiques</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="space-y-4">
              <!-- Type de transaction en premier -->
              <div class="pb-4 border-b">
                <span class="text-sm text-gray-600 dark:text-gray-400 block mb-2">Type de transaction</span>
                <Badge
                  :class="getTransactionType() === 'Location' ? 'bg-blue-600 hover:bg-blue-700' : 'bg-red-600 hover:bg-red-700'"
                  class="text-white text-base font-semibold"
                >
                  {{ getTransactionType() }}
                  <span v-if="getTransactionType() === 'Location'" class="text-sm font-normal opacity-90 ml-1">
                    (Loyer mensuel)
                  </span>
                </Badge>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div v-if="property.surface" class="space-y-1">
                  <span class="text-sm text-gray-600 dark:text-gray-400">Surface</span>
                  <p class="font-semibold text-gray-900 dark:text-white">{{ property.surface }} m²</p>
                </div>
                <div v-if="property.rooms" class="space-y-1">
                  <span class="text-sm text-gray-600 dark:text-gray-400">Pièces</span>
                  <p class="font-semibold text-gray-900 dark:text-white">{{ property.rooms }}</p>
                </div>
                <div v-if="property.bedrooms" class="space-y-1">
                  <span class="text-sm text-gray-600 dark:text-gray-400">Chambres</span>
                  <p class="font-semibold text-gray-900 dark:text-white">{{ property.bedrooms }}</p>
                </div>
                <div v-if="property.bathrooms" class="space-y-1">
                  <span class="text-sm text-gray-600 dark:text-gray-400">Salles de bain</span>
                  <p class="font-semibold text-gray-900 dark:text-white">{{ property.bathrooms }}</p>
                </div>
                <div v-if="property.yearBuilt" class="space-y-1">
                  <span class="text-sm text-gray-600 dark:text-gray-400">Année de construction</span>
                  <p class="font-semibold text-gray-900 dark:text-white">{{ property.yearBuilt }}</p>
                </div>
                <div v-if="property.condition" class="space-y-1">
                  <span class="text-sm text-gray-600 dark:text-gray-400">État</span>
                  <p class="font-semibold text-gray-900 dark:text-white">{{ property.condition }}</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Équipements du bâtiment -->
        <Card v-if="hasBuildingAmenities">
          <CardHeader>
            <CardTitle>Équipements du bâtiment</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div v-for="(category, categoryName) in buildingAmenitiesByCategory" :key="categoryName" class="space-y-2">
                <h3 class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">{{ categoryName }}</h3>
                <ul class="space-y-2">
                  <li v-for="(amenity, idx) in category" :key="idx" class="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-2">
                    <CheckCircle2 class="h-4 w-4 text-green-500 flex-shrink-0" />
                    <span>{{ amenity }}</span>
                  </li>
                </ul>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Caractéristiques des unités -->
        <Card v-if="hasUnitFeatures">
          <CardHeader>
            <CardTitle>Caractéristiques des unités</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div v-for="(category, categoryName) in unitFeaturesByCategory" :key="categoryName" class="space-y-2">
                <h3 class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">{{ categoryName }}</h3>
                <ul class="space-y-2">
                  <li v-for="(feature, idx) in category" :key="idx" class="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-2">
                    <CheckCircle2 class="h-4 w-4 text-green-500 flex-shrink-0" />
                    <span>{{ feature }}</span>
                  </li>
                </ul>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Politiques -->
        <Card v-if="hasPolicies">
          <CardHeader>
            <CardTitle>Politiques</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="space-y-6">
              <!-- Parking -->
              <div v-if="property.parkingPolicy" class="space-y-2">
                <h3 class="text-sm font-semibold text-gray-700 dark:text-gray-300">Parking</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">{{ property.parkingPolicy }}</p>
              </div>
              <!-- Animaux -->
              <div v-if="petPolicyData" class="space-y-2">
                <h3 class="text-sm font-semibold text-gray-700 dark:text-gray-300">Animaux de compagnie</h3>
                <div class="flex items-center gap-4 flex-wrap">
                  <Badge v-if="petPolicyData.dogsAllowed" variant="outline" class="bg-green-50 text-green-700 border-green-300">
                    <CheckCircle2 class="h-3 w-3 mr-1" />
                    Chiens autorisés
                  </Badge>
                  <Badge v-if="petPolicyData.catsAllowed" variant="outline" class="bg-green-50 text-green-700 border-green-300">
                    <CheckCircle2 class="h-3 w-3 mr-1" />
                    Chats autorisés
                  </Badge>
                  <Badge v-if="!petPolicyData.dogsAllowed && !petPolicyData.catsAllowed" variant="outline" class="bg-gray-50 text-gray-700">
                    Animaux non autorisés
                  </Badge>
                </div>
                <p v-if="petPolicyData.restrictions" class="text-xs text-gray-500 dark:text-gray-400 mt-2">{{ petPolicyData.restrictions }}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Quartier -->
        <Card v-if="hasNeighborhoodInfo">
          <CardHeader>
            <CardTitle>Quartier: {{ property.neighborhood || property.city }}</CardTitle>
          </CardHeader>
          <CardContent>
            <div v-if="hasScores" class="grid grid-cols-3 gap-4">
              <Card v-if="property.walkScore" class="text-center">
                <CardContent class="p-4">
                  <div class="text-2xl font-bold text-blue-600">{{ property.walkScore }}</div>
                  <div class="text-xs text-gray-600 dark:text-gray-400 mt-1">Walk Score</div>
                  <Badge variant="outline" class="mt-2 text-xs">{{ getScoreLabel(property.walkScore) }}</Badge>
                </CardContent>
              </Card>
              <Card v-if="property.transitScore" class="text-center">
                <CardContent class="p-4">
                  <div class="text-2xl font-bold text-green-600">{{ property.transitScore }}</div>
                  <div class="text-xs text-gray-600 dark:text-gray-400 mt-1">Transit Score</div>
                  <Badge variant="outline" class="mt-2 text-xs">{{ getScoreLabel(property.transitScore) }}</Badge>
                </CardContent>
              </Card>
              <Card v-if="property.bikeScore" class="text-center">
                <CardContent class="p-4">
                  <div class="text-2xl font-bold text-purple-600">{{ property.bikeScore }}</div>
                  <div class="text-xs text-gray-600 dark:text-gray-400 mt-1">Bike Score</div>
                  <Badge variant="outline" class="mt-2 text-xs">{{ getScoreLabel(property.bikeScore) }}</Badge>
                </CardContent>
              </Card>
            </div>
          </CardContent>
        </Card>

        <!-- Horaires du bureau -->
        <Card v-if="officeHoursData">
          <CardHeader>
            <div class="flex items-center justify-between">
              <CardTitle>Horaires du bureau</CardTitle>
              <Clock class="h-5 w-5 text-gray-400 dark:text-gray-500" />
            </div>
          </CardHeader>
          <CardContent>
            <div class="overflow-x-auto">
              <table class="min-w-full text-sm">
                <thead class="bg-gray-50 dark:bg-gray-800">
                  <tr>
                    <th class="px-4 py-3 text-left text-gray-700 dark:text-gray-300 font-semibold">Jour</th>
                    <th class="px-4 py-3 text-left text-gray-700 dark:text-gray-300 font-semibold">Horaires</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
                  <tr v-for="day in formattedOfficeHours" :key="day.key" class="hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                    <td class="px-4 py-3 text-gray-700 dark:text-gray-300 font-medium">{{ day.label }}</td>
                    <td class="px-4 py-3">
                      <Badge v-if="day.hours === 'closed' || day.hours === 'Fermé'" variant="outline" class="text-gray-500">
                        Fermé
                      </Badge>
                      <span v-else class="text-gray-900 dark:text-gray-100 font-medium">{{ day.hours }}</span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <p class="mt-4 text-xs text-gray-500 dark:text-gray-400">
              Les horaires peuvent varier selon les périodes. Contactez-nous pour confirmer.
            </p>
          </CardContent>
        </Card>

        <!-- Unités disponibles -->
        <Card v-if="availableUnitsList && availableUnitsList.length > 0">
          <CardHeader>
            <CardTitle>Unités disponibles</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="overflow-x-auto">
              <table class="min-w-full text-sm">
                <thead class="bg-gray-50 dark:bg-gray-800">
                  <tr>
                    <th class="px-4 py-2 text-left text-gray-700 dark:text-gray-300 font-semibold">Unité</th>
                    <th class="px-4 py-2 text-left text-gray-700 dark:text-gray-300 font-semibold">Surface</th>
                    <th class="px-4 py-2 text-left text-gray-700 dark:text-gray-300 font-semibold">Disponible</th>
                    <th class="px-4 py-2 text-left text-gray-700 dark:text-gray-300 font-semibold">Loyer de base</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
                  <tr v-for="(unit, idx) in availableUnitsList" :key="idx" class="hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                    <td class="px-4 py-2 text-gray-700 dark:text-gray-300 font-medium">{{ unit.unit || `Unité ${Number(idx) + 1}` }}</td>
                    <td class="px-4 py-2 text-gray-600 dark:text-gray-400">
                      <template v-if="unit.sqft">{{ unit.sqft }} sqft</template>
                      <template v-else-if="unit.surface">{{ unit.surface }} m²</template>
                      <template v-else><Badge variant="outline">N/A</Badge></template>
                    </td>
                    <td class="px-4 py-2 text-gray-600 dark:text-gray-400">{{ formatAvailableDate(unit.available) }}</td>
                    <td class="px-4 py-2 text-gray-900 dark:text-gray-100 font-semibold">€{{ formatPrice(unit.baseRent || unit.price) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>

        <!-- Localisation -->
        <Card v-if="property.latitude && property.longitude" class="overflow-hidden">
          <CardHeader>
            <CardTitle>Localisation</CardTitle>
          </CardHeader>
          <CardContent class="p-0">
            <div class="w-full h-[500px]">
              <PropertyMap
                :property="{
                  id: property.id,
                  title: property.title,
                  latitude: property.latitude,
                  longitude: property.longitude,
                  address: property.address,
                  city: property.city
                }"
                :hide-action-button="true"
                :hide-sidebar="true"
              />
            </div>
          </CardContent>
        </Card>

        <!-- Historique des Prix -->
        <section class="space-y-6">
          <PriceHistoryChart :property-id="property.id" />
        </section>

        <!-- Avis clients -->
        <section class="space-y-6">
          <ReviewList 
            ref="reviewListRef"
            :property-id="property.id" 
          />
          <ReviewForm 
            :property-id="property.id"
            @review-created="handleReviewCreated"
          />
        </section>
      </div>

          <!-- Sidebar -->
          <aside class="lg:col-span-1 space-y-6">
            <!-- Annonces publicitaires -->
            <AdvertisementSlot
              ad-type="SIDEBAR"
              position="SIDEBAR_RIGHT"
              :city="property.city"
              :postal-code="property.postalCode"
              :property-type="property.type"
              :property-id="property.id"
              page-type="PROPERTY_DETAIL"
            />
        <!-- Actions -->
        <Card class="sticky top-4">
          <CardHeader>
            <CardTitle>Actions</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="space-y-3">
              <ContactForm
                :property-id="property.id"
                :property-title="property.title"
                inline
                button-text="Contacter l'agent"
              />
              
              <!-- Réserver une visite -->
              <VisitBookingForm
                :property-id="property.id"
                @visit-booked="handleVisitBooked"
              />
              
              <!-- Alerte de prix -->
              <PriceAlertForm
                :property-id="property.id"
                :current-price="property.price"
                @alert-created="handleAlertCreated"
              />
              <div class="space-y-3">
                <div class="flex items-center justify-center gap-3">
                  <FavoriteButton
                    v-if="property"
                    :property-id="property.id"
                    @favorite-toggled="handleFavoriteToggled"
                  />
                  <span class="text-sm text-gray-600">Ajouter aux favoris</span>
                </div>
                <div class="flex items-center justify-center gap-3">
                  <CompareButton
                    v-if="property"
                    :property-id="property.id"
                  />
                  <span class="text-sm text-gray-600">Comparer</span>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Informations -->
        <Card>
          <CardHeader>
            <CardTitle>Informations</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="space-y-4 text-sm">
              <div class="space-y-1">
                <span class="text-gray-600 dark:text-gray-400">Référence</span>
                <p class="font-semibold text-gray-900 dark:text-white">{{ property.reference }}</p>
              </div>
              <Separator />
              <div class="space-y-1">
                <span class="text-gray-600 dark:text-gray-400">Type</span>
                <p class="font-semibold text-gray-900 dark:text-white">{{ property.type }}</p>
              </div>
              <Separator />
              <div class="space-y-1">
                <span class="text-gray-600 dark:text-gray-400">Statut</span>
                <p class="font-semibold text-gray-900 dark:text-white">{{ property.status }}</p>
              </div>
              <Separator />
              <div class="space-y-2">
                <span class="text-sm text-gray-600 dark:text-gray-400 block">Type de transaction</span>
                <Badge
                  :class="getTransactionType() === 'Location' ? 'bg-blue-600 hover:bg-blue-700' : 'bg-red-600 hover:bg-red-700'"
                  class="text-white"
                >
                  {{ getTransactionType() }}
                  <span v-if="getTransactionType() === 'Location'" class="text-xs font-normal opacity-90 ml-1">
                    (Loyer mensuel)
                  </span>
                </Badge>
              </div>
              <div v-if="property.dateOnMarket">
                <Separator />
                <div class="space-y-2 mt-2">
                  <div class="flex items-center gap-2">
                    <Calendar class="h-4 w-4 text-gray-500" />
                    <span class="text-sm text-gray-600 dark:text-gray-400">Sur le marché depuis</span>
                  </div>
                  <p class="font-semibold text-gray-900 dark:text-white">{{ formatDate(property.dateOnMarket) }}</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </aside>
    </main>
    
    <!-- Propriétés similaires -->
    <RelatedProperties
      v-if="property"
      :current-property-id="property.id"
      :city="property.city"
      :type="property.type"
      :max-price="property.price"
      @property-click="handleRelatedPropertyClick"
    />
    
    <!-- Bouton retour en haut -->
    <ScrollToTop />
  </div>
</template>

<script setup lang="ts">
    import { ref, computed, onMounted, watch } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'
import { documentService, type Document } from '@/api/document.service'
    import PropertyMap from '@/components/PropertyMap.vue'
    import ImageGallery from '@/components/ImageGallery.vue'
    import ShareButtons from '@/components/ShareButtons.vue'
    import RelatedProperties from '@/components/RelatedProperties.vue'
    import FavoriteButton from '@/components/FavoriteButton.vue'
    import { useSEO } from '@/composables/useSEO'
    import CompareButton from '@/components/CompareButton.vue'
    import { getPlaceholderImage } from '@/utils/imageOptimization'
    import { Clock, ArrowLeft, MapPin, Euro, Square, Home, Bed, Bath, Calendar, CheckCircle2, Gift } from 'lucide-vue-next'
    import ReviewList from '@/components/ReviewList.vue'
    import ReviewForm from '@/components/ReviewForm.vue'
    import ContactForm from '@/components/ContactForm.vue'
    import PriceHistoryChart from '@/components/PriceHistoryChart.vue'
    import PriceAlertForm from '@/components/PriceAlertForm.vue'
    import VisitBookingForm from '@/components/VisitBookingForm.vue'
    import SkeletonLoader from '@/components/SkeletonLoader.vue'
    import ErrorMessage from '@/components/ErrorMessage.vue'
    import EmptyState from '@/components/EmptyState.vue'
    import ScrollToTop from '@/components/ScrollToTop.vue'
    import AdvertisementSlot from '@/components/AdvertisementSlot.vue'
    import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
    import { Button } from '@/components/ui/button'
    import { Badge } from '@/components/ui/badge'
    import { Separator } from '@/components/ui/separator'

const route = useRoute()
const router = useRouter()

const property = ref<PublicProperty | null>(null)
const propertyId = ref<number | null>(null)
    const images = ref<Document[]>([])
    const loading = ref(false)
    const error = ref<string | null>(null)
    const selectedImage = ref<Document | null>(null)
    const reviewListRef = ref<InstanceType<typeof ReviewList> | null>(null)
    const shareUrl = computed(() => {
      if (typeof window !== 'undefined') {
        return window.location.href
      }
      return ''
    })

watch(
  () => route.params.id || route.params.slug,
  async (newIdOrSlug) => {
    if (newIdOrSlug) {
      // Si c'est un slug (contient des tirets et lettres), utiliser getPublishedPropertyBySlug
      if (typeof newIdOrSlug === 'string' && /^[a-z0-9-]+$/.test(newIdOrSlug) && newIdOrSlug.includes('-')) {
        await loadPropertyBySlug(newIdOrSlug)
      } else {
        propertyId.value = Number(newIdOrSlug)
        await loadProperty()
      }
    }
  },
  { immediate: true }
)

async function loadPropertyBySlug(slug: string) {
  loading.value = true
  error.value = null
  
  try {
    property.value = await publicPropertyService.getPublishedPropertyBySlug(slug)
    if (property.value) {
      propertyId.value = property.value.id
      await loadPropertyImages()
      updateSEOForProperty()
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Propriété non trouvée'
    console.error('Error loading property by slug:', err)
  } finally {
    loading.value = false
  }
}

async function loadProperty() {
  if (!propertyId.value) {
    propertyId.value = Number(route.params.id)
  }
  
  if (!propertyId.value) {
    error.value = 'ID de propriété invalide'
    return
  }

  loading.value = true
  error.value = null

  try {
    // Charger la propriété
    property.value = await publicPropertyService.getPublishedPropertyById(propertyId.value)
    
    // Charger les images
    const allDocuments = await documentService.getByPropertyId(propertyId.value)
    images.value = documentService.filterImages(allDocuments)
    
    // Mettre à jour le SEO pour cette propriété
    if (property.value) {
      updateSEOForProperty()
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Erreur lors du chargement de la propriété'
    console.error('Error loading property:', err)
  } finally {
    loading.value = false
  }
}

async function loadPropertyImages() {
  if (!propertyId.value) return
  const allDocuments = await documentService.getByPropertyId(propertyId.value)
  images.value = documentService.filterImages(allDocuments)
}

// Initialiser le SEO au setup (synchrone) avec une config réactive
const seoConfig = ref({
  title: 'Viridial - Annonces Immobilières',
  description: 'Découvrez des milliers d\'annonces immobilières',
  type: 'website' as const
})

// Initialiser useSEO au setup avec la ref (pour que les changements soient détectés)
const { updateSEO } = useSEO(seoConfig)

// Fonction pour mettre à jour le SEO après le chargement de la propriété
function updateSEOForProperty() {
  if (!property.value) return
  
  const transactionType = property.value.transactionType === 'RENT' ? 'Location' : 'Vente'
  const priceText = transactionType === 'Location' 
    ? `${property.value.price.toLocaleString('fr-FR')}€/mois`
    : `${property.value.price.toLocaleString('fr-FR')}€`
  
  // Générer l'URL canonique avec slug si disponible
  const canonicalUrl = property.value.slug 
    ? `${typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'}/property-slug/${property.value.slug}`
    : (typeof window !== 'undefined' ? window.location.href : undefined)
  
  // Utiliser meta_title et meta_description si disponibles, sinon générer automatiquement
  const seoTitle = property.value.metaTitle || `${property.value.title} - ${property.value.city} | ${transactionType}`
  const seoDescription = property.value.metaDescription || property.value.description || `${transactionType} ${property.value.type.toLowerCase()} ${property.value.city} - ${priceText}. ${property.value.surface} m²${property.value.bedrooms ? `, ${property.value.bedrooms} chambre${property.value.bedrooms > 1 ? 's' : ''}` : ''}.`
  
  // Utiliser meta_keywords si disponible, sinon générer automatiquement
  const seoKeywords = property.value.metaKeywords 
    ? property.value.metaKeywords.split(',').map(k => k.trim())
    : [
        transactionType.toLowerCase(),
        property.value.type.toLowerCase(),
        property.value.city || '',
        'immobilier',
        property.value.country || 'France',
        property.value.address?.toLowerCase()
      ].filter((k): k is string => Boolean(k))
  
  // Utiliser og_image si disponible, sinon première image
  const seoImage = property.value.ogImage || (property.value.images && property.value.images.length > 0 ? property.value.images[0] : undefined)
  
  // Mettre à jour la config réactive
  seoConfig.value = {
    title: seoTitle,
    description: seoDescription,
    keywords: seoKeywords,
    image: seoImage,
    property: property.value,
    type: 'product',
    canonical: canonicalUrl
  }
  
  // Appeler updateSEO pour appliquer les changements
  updateSEO()
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long', day: 'numeric' })
}

function handleRelatedPropertyClick(id: number) {
      propertyId.value = id
      router.push(`/property/${id}`)
      loadProperty()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }

function handleReviewCreated() {
  if (reviewListRef.value) {
    reviewListRef.value.refresh()
  }
}

function handleAlertCreated() {
  // Optionnel: Afficher une notification ou rafraîchir les données
  console.log('Price alert created')
}

function handleVisitBooked() {
  // Optionnel: Afficher une notification ou rafraîchir les données
  console.log('Visit booked')
}
    
    function handleFavoriteToggled(isFavorite: boolean) {
      // Optionnel : afficher une notification
      if (isFavorite) {
        console.log(`Propriété ${property.value?.id} ajoutée aux favoris`)
      } else {
        console.log(`Propriété ${property.value?.id} retirée des favoris`)
      }
    }

function getTransactionType(): string {
  if (!property.value) return 'Vente'
  
  // Utiliser transactionType de l'API si disponible
  if (property.value.transactionType) {
    const apiTransactionType = property.value.transactionType.toUpperCase()
    if (apiTransactionType === 'RENT') {
      return 'Location'
    } else if (apiTransactionType === 'SALE') {
      return 'Vente'
    }
  }
  
  // Fallback : déduire du status si transactionType n'est pas défini
  const apiStatus = property.value.status?.toUpperCase()
  if (apiStatus === 'RENTED' || apiStatus === 'LOUÉ') {
    return 'Location'
  } else if (apiStatus === 'SOLD' || apiStatus === 'VENDU') {
    return 'Vente'
  }
  
  // Par défaut pour les propriétés disponibles
  return 'Vente'
}

function getTransactionTypeColor(): string {
  const transactionType = getTransactionType()
  if (transactionType === 'Location') {
    return '#1a73e8' // Bleu pour location
  } else if (transactionType === 'Vente') {
    return '#ea4335' // Rouge pour vente
  }
  return '#1a73e8' // Par défaut bleu (location)
}

// Parsing JSON fields
const buildingAmenitiesByCategory = computed(() => {
  if (!property.value?.buildingAmenities) return {}
  try {
    const amenities = JSON.parse(property.value.buildingAmenities)
    if (!Array.isArray(amenities)) return {}
    
    // Organiser par catégories (simplifié - peut être amélioré)
    const categories: Record<string, string[]> = {
      'Équipements communautaires': [],
      'Extérieur': [],
      'Sécurité': [],
      'Services et installations': []
    }
    
    amenities.forEach((amenity: string) => {
      const lower = amenity.toLowerCase()
      if (lower.includes('fitness') || lower.includes('gym') || lower.includes('salle')) {
        categories['Équipements communautaires'].push(amenity)
      } else if (lower.includes('deck') || lower.includes('roof') || lower.includes('garden') || lower.includes('balcony')) {
        categories['Extérieur'].push(amenity)
      } else if (lower.includes('security') || lower.includes('doorman') || lower.includes('concierge')) {
        categories['Sécurité'].push(amenity)
      } else {
        categories['Services et installations'].push(amenity)
      }
    })
    
    // Retirer les catégories vides
    Object.keys(categories).forEach(key => {
      if (categories[key].length === 0) delete categories[key]
    })
    
    return categories
  } catch {
    return {}
  }
})

const unitFeaturesByCategory = computed(() => {
  if (!property.value?.unitFeatures) return {}
  try {
    const features = JSON.parse(property.value.unitFeatures)
    if (!Array.isArray(features)) return {}
    
    const categories: Record<string, string[]> = {
      'Électroménagers': [],
      'Revêtements': []
    }
    
    features.forEach((feature: string) => {
      const lower = feature.toLowerCase()
      if (lower.includes('dishwasher') || lower.includes('washer') || lower.includes('dryer') || lower.includes('refrigerator')) {
        categories['Électroménagers'].push(feature)
      } else if (lower.includes('hardwood') || lower.includes('carpet') || lower.includes('floor')) {
        categories['Revêtements'].push(feature)
      } else {
        if (!categories['Autres']) categories['Autres'] = []
        categories['Autres'].push(feature)
      }
    })
    
    Object.keys(categories).forEach(key => {
      if (categories[key].length === 0) delete categories[key]
    })
    
    return categories
  } catch {
    return {}
  }
})

const hasBuildingAmenities = computed(() => {
  return Object.keys(buildingAmenitiesByCategory.value).length > 0
})

const hasUnitFeatures = computed(() => {
  return Object.keys(unitFeaturesByCategory.value).length > 0
})

const petPolicyData = computed(() => {
  if (!property.value?.petPolicy) return null
  try {
    return JSON.parse(property.value.petPolicy)
  } catch {
    return null
  }
})

const hasPolicies = computed(() => {
  return property.value?.parkingPolicy || petPolicyData.value
})

const hasNeighborhoodInfo = computed(() => {
  return property.value?.neighborhood || property.value?.city
})

const hasScores = computed(() => {
  return property.value?.walkScore || property.value?.transitScore || property.value?.bikeScore
})

const officeHoursData = computed(() => {
  if (!property.value?.officeHours) return null
  try {
    return JSON.parse(property.value.officeHours)
  } catch {
    return null
  }
})

const daysOfWeek = [
  { key: 'monday', label: 'Lundi' },
  { key: 'tuesday', label: 'Mardi' },
  { key: 'wednesday', label: 'Mercredi' },
  { key: 'thursday', label: 'Jeudi' },
  { key: 'friday', label: 'Vendredi' },
  { key: 'saturday', label: 'Samedi' },
  { key: 'sunday', label: 'Dimanche' }
]

const formattedOfficeHours = computed(() => {
  if (!officeHoursData.value) return []
  
  return daysOfWeek.map(day => {
    const hours = officeHoursData.value[day.key]
    return {
      key: day.key,
      label: day.label,
      hours: hours === 'closed' ? 'Fermé' : (hours || 'Fermé')
    }
  })
})

const availableUnitsList = computed(() => {
  if (!property.value?.availableUnits) return []
  try {
    return JSON.parse(property.value.availableUnits)
  } catch {
    return []
  }
})

function getScoreLabel(score: number): string {
  if (score >= 90) return "Parfait"
  if (score >= 70) return "Excellent"
  if (score >= 50) return "Bon"
  if (score >= 25) return "Moyen"
  return "Faible"
}

function formatAvailableDate(dateStr: string | undefined): string {
  if (!dateStr) return 'Maintenant'
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long', day: 'numeric' })
  } catch {
    return dateStr
  }
}

function formatPrice(price: number | string | undefined): string {
  if (!price) return '0'
  const numPrice = typeof price === 'string' ? parseFloat(price) : price
  return numPrice.toLocaleString('fr-FR')
}
</script>

