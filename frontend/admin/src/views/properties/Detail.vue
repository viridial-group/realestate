<template>
  <div class="space-y-6">
    <!-- Header avec Actions -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="icon" @click="goBack">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">{{ property?.title || 'Chargement...' }}</h1>
          <p class="text-muted-foreground mt-1">{{ property?.address }}, {{ property?.city }}</p>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="exportToCSV" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Export CSV
        </Button>
        <Button variant="outline" @click="exportToExcel" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Export Excel
        </Button>
        <Button variant="outline" @click="editProperty" :disabled="loading">
          <Edit class="mr-2 h-4 w-4" />
          Modifier
        </Button>
        <Button variant="destructive" @click="deleteProperty" :disabled="loading">
          <Trash2 class="mr-2 h-4 w-4" />
          Supprimer
        </Button>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>

    <div v-else-if="property" class="space-y-6">
      <!-- Galerie d'images -->
      <Card v-if="property.images && property.images.length > 0">
        <CardHeader>
          <CardTitle>Galerie d'images</CardTitle>
        </CardHeader>
        <CardContent>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div
              v-for="(image, index) in property.images"
              :key="index"
              class="relative group cursor-pointer"
              @click="openImageModal(image, index)"
            >
              <img
                :src="image"
                :alt="`Image ${index + 1}`"
                class="w-full h-48 object-cover rounded-lg transition-transform group-hover:scale-105"
              />
              <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity rounded-lg flex items-center justify-center">
                <Eye class="h-6 w-6 text-white" />
              </div>
              <Badge
                v-if="index === 0"
                class="absolute top-2 left-2"
                variant="default"
              >
                Principale
              </Badge>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Informations principales -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Colonne principale -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Description -->
          <Card>
            <CardHeader>
              <CardTitle>Description</CardTitle>
            </CardHeader>
            <CardContent>
              <p class="text-muted-foreground whitespace-pre-wrap">{{ property.description || 'Aucune description' }}</p>
            </CardContent>
          </Card>

          <!-- Informations organisation et assignation -->
          <Card v-if="getOrganizationName || getAssignedUserName">
            <CardHeader>
              <CardTitle>Organisation et assignation</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-1" v-if="getOrganizationName">
                  <p class="text-sm text-muted-foreground">Organisation</p>
                  <p class="font-semibold">{{ getOrganizationName }}</p>
                </div>
                <div class="space-y-1" v-if="getAssignedUserName">
                  <p class="text-sm text-muted-foreground">Assigné à</p>
                  <p class="font-semibold">{{ getAssignedUserName }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Caractéristiques -->
          <Card>
            <CardHeader>
              <CardTitle>Caractéristiques</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div class="space-y-1">
                  <p class="text-sm text-muted-foreground">Type</p>
                  <p class="font-semibold">{{ getTypeLabel((property as any).type || property.propertyType) }}</p>
                </div>
                <div class="space-y-1" v-if="property.bedrooms">
                  <p class="text-sm text-muted-foreground">Chambres</p>
                  <p class="font-semibold">{{ property.bedrooms }}</p>
                </div>
                <div class="space-y-1" v-if="property.bathrooms">
                  <p class="text-sm text-muted-foreground">Salles de bain</p>
                  <p class="font-semibold">{{ property.bathrooms }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).fullBathrooms">
                  <p class="text-sm text-muted-foreground">Salles de bain complètes</p>
                  <p class="font-semibold">{{ (property as any).fullBathrooms }}</p>
                </div>
                <div class="space-y-1" v-if="property.area">
                  <p class="text-sm text-muted-foreground">Surface</p>
                  <p class="font-semibold">{{ property.area }} m²</p>
                </div>
                <div class="space-y-1" v-if="(property as any).totalStructureArea">
                  <p class="text-sm text-muted-foreground">Surface totale structure</p>
                  <p class="font-semibold">{{ (property as any).totalStructureArea }} m²</p>
                </div>
                <div class="space-y-1" v-if="(property as any).totalInteriorLivableArea">
                  <p class="text-sm text-muted-foreground">Surface habitable intérieure</p>
                  <p class="font-semibold">{{ (property as any).totalInteriorLivableArea }} m²</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Interior Details -->
          <Card v-if="(property as any).appliancesIncluded || (property as any).laundryLocation">
            <CardHeader>
              <CardTitle>Détails intérieurs</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-1" v-if="(property as any).appliancesIncluded">
                  <p class="text-sm text-muted-foreground">Équipements inclus</p>
                  <p class="font-semibold">{{ parseJsonArray((property as any).appliancesIncluded)?.join(', ') || (property as any).appliancesIncluded }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).laundryLocation">
                  <p class="text-sm text-muted-foreground">Localisation laverie</p>
                  <p class="font-semibold">{{ getLaundryLabel((property as any).laundryLocation) }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Video & Virtual Tour -->
          <Card v-if="(property as any).virtualTourUrl">
            <CardHeader>
              <CardTitle>Visite virtuelle</CardTitle>
            </CardHeader>
            <CardContent>
              <a 
                :href="(property as any).virtualTourUrl" 
                target="_blank" 
                rel="noopener noreferrer"
                class="text-primary hover:underline flex items-center gap-2"
              >
                <Eye class="h-4 w-4" />
                Voir la visite virtuelle
              </a>
            </CardContent>
          </Card>

          <!-- Parking & Accessibility -->
          <Card v-if="(property as any).parkingFeatures || (property as any).hasGarage || (property as any).accessibilityFeatures">
            <CardHeader>
              <CardTitle>Parking et accessibilité</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-1" v-if="(property as any).parkingFeatures">
                  <p class="text-sm text-muted-foreground">Caractéristiques parking</p>
                  <p class="font-semibold">{{ parseJsonArray((property as any).parkingFeatures)?.join(', ') || (property as any).parkingFeatures }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).hasGarage !== undefined">
                  <p class="text-sm text-muted-foreground">Garage</p>
                  <Badge :variant="(property as any).hasGarage ? 'default' : 'secondary'">
                    {{ (property as any).hasGarage ? 'Oui' : 'Non' }}
                  </Badge>
                </div>
                <div class="space-y-1" v-if="(property as any).accessibilityFeatures">
                  <p class="text-sm text-muted-foreground">Caractéristiques accessibilité</p>
                  <p class="font-semibold">{{ parseJsonArray((property as any).accessibilityFeatures)?.join(', ') || (property as any).accessibilityFeatures }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Exterior Features -->
          <Card v-if="(property as any).patioPorch || (property as any).exteriorFeatures">
            <CardHeader>
              <CardTitle>Caractéristiques extérieures</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-1" v-if="(property as any).patioPorch">
                  <p class="text-sm text-muted-foreground">Patio & Porche</p>
                  <p class="font-semibold">{{ (property as any).patioPorch }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).exteriorFeatures">
                  <p class="text-sm text-muted-foreground">Caractéristiques extérieures</p>
                  <p class="font-semibold">{{ parseJsonArray((property as any).exteriorFeatures)?.join(', ') || (property as any).exteriorFeatures }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Construction Details -->
          <Card v-if="(property as any).homeType || (property as any).propertySubtype || (property as any).condition || (property as any).yearBuilt || (property as any).specialConditions">
            <CardHeader>
              <CardTitle>Détails de construction</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
                <div class="space-y-1" v-if="(property as any).homeType">
                  <p class="text-sm text-muted-foreground">Type de maison</p>
                  <p class="font-semibold">{{ (property as any).homeType }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).propertySubtype">
                  <p class="text-sm text-muted-foreground">Sous-type</p>
                  <p class="font-semibold">{{ (property as any).propertySubtype }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).condition">
                  <p class="text-sm text-muted-foreground">État</p>
                  <p class="font-semibold">{{ (property as any).condition }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).yearBuilt">
                  <p class="text-sm text-muted-foreground">Année de construction</p>
                  <p class="font-semibold">{{ (property as any).yearBuilt }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).specialConditions">
                  <p class="text-sm text-muted-foreground">Conditions spéciales</p>
                  <p class="font-semibold">{{ (property as any).specialConditions }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Community & HOA -->
          <Card v-if="(property as any).subdivision || (property as any).hasHOA !== undefined">
            <CardHeader>
              <CardTitle>Communauté et HOA</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="space-y-4">
                <div class="space-y-1" v-if="(property as any).subdivision">
                  <p class="text-sm text-muted-foreground">Subdivision</p>
                  <p class="font-semibold">{{ (property as any).subdivision }}</p>
                </div>
                <div v-if="(property as any).hasHOA !== undefined">
                  <p class="text-sm text-muted-foreground mb-2">HOA</p>
                  <Badge :variant="(property as any).hasHOA ? 'default' : 'secondary'">
                    {{ (property as any).hasHOA ? 'Oui' : 'Non' }}
                  </Badge>
                  <div v-if="(property as any).hasHOA" class="mt-4 space-y-2">
                    <div v-if="(property as any).hoaAmenities">
                      <p class="text-sm text-muted-foreground">Équipements HOA</p>
                      <p class="font-semibold">{{ parseJsonArray((property as any).hoaAmenities)?.join(', ') || (property as any).hoaAmenities }}</p>
                    </div>
                    <div v-if="(property as any).hoaServices">
                      <p class="text-sm text-muted-foreground">Services HOA</p>
                      <p class="font-semibold">{{ parseJsonArray((property as any).hoaServices)?.join(', ') || (property as any).hoaServices }}</p>
                    </div>
                    <div v-if="(property as any).hoaFee" class="grid grid-cols-2 gap-4">
                      <div>
                        <p class="text-sm text-muted-foreground">Frais HOA</p>
                        <p class="font-semibold">{{ formatPrice((property as any).hoaFee) }}</p>
                      </div>
                      <div v-if="(property as any).hoaFeeFrequency">
                        <p class="text-sm text-muted-foreground">Fréquence</p>
                        <p class="font-semibold">{{ getHoaFrequencyLabel((property as any).hoaFeeFrequency) }}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Historique des Prix -->
          <PriceHistoryChart :property-id="propertyId" />
          
          <!-- Données de Marché (DVF) -->
          <MarketDataCard 
            :property-id="propertyId"
            :postal-code="property.postalCode"
            :property-type="property.type || property.propertyType"
          />
          
          <!-- Alertes de Prix -->
          <PriceAlertsList :property-id="propertyId" />
          
          <!-- Rendez-vous de Visite -->
          <VisitsList :property-id="propertyId" />

          <!-- Statistiques des Avis -->
          <Card v-if="reviewStats">
            <CardHeader>
              <CardTitle class="flex items-center gap-2">
                <Star class="h-5 w-5 text-yellow-500" />
                Statistiques des Avis
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="space-y-2">
                  <p class="text-sm text-muted-foreground">Note moyenne</p>
                  <div class="flex items-center gap-2">
                    <div class="flex items-center gap-1">
                      <Star
                        v-for="i in 5"
                        :key="i"
                        :class="[
                          'h-5 w-5',
                          i <= Math.round(reviewStats.averageRating) ? 'text-yellow-400 fill-current' : 'text-gray-300'
                        ]"
                      />
                    </div>
                    <span class="text-lg font-bold">{{ reviewStats.averageRating.toFixed(1) }}/5</span>
                  </div>
                </div>
                <div class="space-y-2">
                  <p class="text-sm text-muted-foreground">Nombre total d'avis</p>
                  <p class="text-2xl font-bold">{{ reviewStats.totalReviews }}</p>
                </div>
                <div class="space-y-2">
                  <p class="text-sm text-muted-foreground">Distribution des notes</p>
                  <div class="space-y-1">
                    <div
                      v-for="rating in [5, 4, 3, 2, 1]"
                      :key="rating"
                      class="flex items-center gap-2"
                    >
                      <span class="text-xs w-8">{{ rating }}★</span>
                      <div class="flex-1 bg-gray-200 rounded-full h-2">
                        <div
                          class="bg-yellow-400 h-2 rounded-full"
                          :style="{ width: `${(reviewStats.ratingPercentages?.[rating] || 0)}%` }"
                        />
                      </div>
                      <span class="text-xs text-muted-foreground w-8 text-right">
                        {{ reviewStats.ratingDistribution?.[rating] || 0 }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="mt-4 pt-4 border-t">
                <Button variant="outline" @click="router.push(`/reviews?propertyId=${property.id}`)">
                  Voir tous les avis
                </Button>
              </div>
            </CardContent>
          </Card>

          <!-- Financial & Listing -->
          <Card v-if="(property as any).region || (property as any).pricePerSquareFoot || (property as any).dateOnMarket">
            <CardHeader>
              <CardTitle>Financier et annonce</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="space-y-1" v-if="(property as any).region">
                  <p class="text-sm text-muted-foreground">Région</p>
                  <p class="font-semibold">{{ (property as any).region }}</p>
                </div>
                <div class="space-y-1" v-if="(property as any).pricePerSquareFoot">
                  <p class="text-sm text-muted-foreground">Prix au m²</p>
                  <p class="font-semibold">{{ formatPrice((property as any).pricePerSquareFoot) }} / m²</p>
                </div>
                <div class="space-y-1" v-if="(property as any).dateOnMarket">
                  <p class="text-sm text-muted-foreground">Date de mise sur le marché</p>
                  <p class="font-semibold">{{ formatDate((property as any).dateOnMarket) }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Property Features -->
          <Card v-if="propertyFeatures && propertyFeatures.length > 0">
            <CardHeader>
              <CardTitle>Caractéristiques détaillées</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="space-y-6">
                <!-- Appliances -->
                <div v-if="getFeaturesByKey('appliance').length > 0">
                  <p class="text-sm font-semibold text-muted-foreground mb-2">Équipements</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge v-for="feature in getFeaturesByKey('appliance')" :key="feature.id" variant="outline">
                      {{ feature.value }}
                    </Badge>
                  </div>
                </div>

                <!-- Parking Features -->
                <div v-if="getFeaturesByKey('parking_feature').length > 0">
                  <p class="text-sm font-semibold text-muted-foreground mb-2">Parking</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge v-for="feature in getFeaturesByKey('parking_feature')" :key="feature.id" variant="outline">
                      {{ feature.value }}
                    </Badge>
                  </div>
                </div>

                <!-- Accessibility Features -->
                <div v-if="getFeaturesByKey('accessibility_feature').length > 0">
                  <p class="text-sm font-semibold text-muted-foreground mb-2">Accessibilité</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge v-for="feature in getFeaturesByKey('accessibility_feature')" :key="feature.id" variant="outline">
                      {{ feature.value }}
                    </Badge>
                  </div>
                </div>

                <!-- Exterior Features -->
                <div v-if="getFeaturesByKey('exterior_feature').length > 0">
                  <p class="text-sm font-semibold text-muted-foreground mb-2">Caractéristiques extérieures</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge v-for="feature in getFeaturesByKey('exterior_feature')" :key="feature.id" variant="outline">
                      {{ feature.value }}
                    </Badge>
                  </div>
                </div>

                <!-- HOA Amenities -->
                <div v-if="getFeaturesByKey('hoa_amenity').length > 0">
                  <p class="text-sm font-semibold text-muted-foreground mb-2">Équipements HOA</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge v-for="feature in getFeaturesByKey('hoa_amenity')" :key="feature.id" variant="outline">
                      {{ feature.value }}
                    </Badge>
                  </div>
                </div>

                <!-- HOA Services -->
                <div v-if="getFeaturesByKey('hoa_service').length > 0">
                  <p class="text-sm font-semibold text-muted-foreground mb-2">Services HOA</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge v-for="feature in getFeaturesByKey('hoa_service')" :key="feature.id" variant="outline">
                      {{ feature.value }}
                    </Badge>
                  </div>
                </div>

                <!-- Patio/Porch -->
                <div v-if="getFeaturesByKey('patio_porch').length > 0">
                  <p class="text-sm font-semibold text-muted-foreground mb-2">Patio & Porche</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge v-for="feature in getFeaturesByKey('patio_porch')" :key="feature.id" variant="outline">
                      {{ feature.value }}
                    </Badge>
                  </div>
                </div>

                <!-- Special Conditions -->
                <div v-if="getFeaturesByKey('special_condition').length > 0">
                  <p class="text-sm font-semibold text-muted-foreground mb-2">Conditions spéciales</p>
                  <div class="flex flex-wrap gap-2">
                    <Badge v-for="feature in getFeaturesByKey('special_condition')" :key="feature.id" variant="outline">
                      {{ feature.value }}
                    </Badge>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

        </div>

        <!-- Colonne latérale -->
        <div class="space-y-6">
          <!-- Statut et Prix -->
          <Card>
            <CardHeader>
              <CardTitle>Informations</CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <p class="text-sm text-muted-foreground mb-1">Statut</p>
                <Badge :variant="getStatusVariant(property.status)">
                  {{ getStatusLabel(property.status) }}
                </Badge>
              </div>
              <div>
                <p class="text-sm text-muted-foreground mb-1">Prix</p>
                <p class="text-2xl font-bold text-primary">{{ formatPrice(property.price) }}</p>
              </div>
            </CardContent>
          </Card>

          <!-- Dates -->
          <Card>
            <CardHeader>
              <CardTitle>Dates</CardTitle>
            </CardHeader>
            <CardContent class="space-y-2">
              <div>
                <p class="text-sm text-muted-foreground">Créée le</p>
                <p class="font-semibold">{{ formatDate(property.createdAt) }}</p>
              </div>
              <div v-if="property.updatedAt">
                <p class="text-sm text-muted-foreground">Modifiée le</p>
                <p class="font-semibold">{{ formatDate(property.updatedAt) }}</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

      <!-- Localisation - Pleine largeur -->
      <Card>
        <CardHeader>
          <CardTitle>Localisation</CardTitle>
        </CardHeader>
        <CardContent class="p-0">
          <!-- Carte -->
          <div v-if="hasLocation" class="w-full">
            <PropertyMap
              :properties="[property]"
              :selected-property-id="property.id"
              :hide-action-button="true"
              :hide-sidebar="true"
            />
          </div>
          <div v-else class="p-12 bg-muted/50 rounded-lg text-center text-sm text-muted-foreground">
            <MapPin class="h-12 w-12 mx-auto mb-4 opacity-50" />
            <p class="text-base">Coordonnées GPS non disponibles pour cette propriété</p>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Modal d'image (Lightbox) -->
    <Dialog v-model:open="imageModalOpen">
      <DialogContent class="max-w-6xl max-h-[90vh] p-0">
        <div class="relative">
          <!-- Navigation précédent -->
          <Button
            v-if="property?.images && property.images.length > 1"
            variant="ghost"
            size="icon"
            class="absolute left-4 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 text-white"
            @click="previousImage"
          >
            <ArrowLeft class="h-6 w-6" />
          </Button>
          
          <!-- Image -->
          <div class="flex items-center justify-center min-h-[60vh] bg-black/90">
            <img
              v-if="selectedImage"
              :src="selectedImage"
              :alt="`Image ${selectedImageIndex + 1}`"
              class="max-w-full max-h-[80vh] object-contain"
            />
          </div>
          
          <!-- Navigation suivant -->
          <Button
            v-if="property?.images && property.images.length > 1"
            variant="ghost"
            size="icon"
            class="absolute right-4 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 text-white"
            @click="nextImage"
          >
            <ArrowRight class="h-6 w-6" />
          </Button>
          
          <!-- Compteur d'images -->
          <div
            v-if="property?.images && property.images.length > 1"
            class="absolute bottom-4 left-1/2 -translate-x-1/2 bg-black/70 text-white px-4 py-2 rounded-full text-sm"
          >
            {{ selectedImageIndex + 1 }} / {{ property.images.length }}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import {
  ArrowLeft,
  ArrowRight,
  Edit,
  Trash2,
  Download,
  MapPin,
  Eye,
  Loader2
} from 'lucide-vue-next'
import { propertyService, documentService, organizationService, reviewService, type Property, PropertyType, PropertyStatus, type PropertyFeature, type ReviewStats } from '@viridial/shared'
import PropertyMap from '@/components/properties/PropertyMap.vue'
import PriceHistoryChart from '@/components/properties/PriceHistoryChart.vue'
import PriceAlertsList from '@/components/properties/PriceAlertsList.vue'
import VisitsList from '@/components/properties/VisitsList.vue'
import MarketDataCard from '@/components/properties/MarketDataCard.vue'
import { Star } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()

const property = ref<Property | null>(null)
const propertyFeatures = ref<PropertyFeature[]>([])
const loading = ref(false)
const imageModalOpen = ref(false)
const selectedImage = ref<string | null>(null)
const selectedImageIndex = ref(0)
const organizationName = ref<string | null>(null)
const assignedUserName = ref<string | null>(null)
const reviewStats = ref<ReviewStats | null>(null)

const propertyId = computed(() => Number(route.params.id))

const hasLocation = computed(() => {
  return property.value?.latitude != null && 
         property.value?.longitude != null && 
         !isNaN(property.value.latitude) && 
         !isNaN(property.value.longitude)
})

onMounted(() => {
  loadProperty()
  loadPropertyFeatures()
  loadReviewStats()
})

const loadProperty = async () => {
  loading.value = true
  try {
    property.value = await propertyService.getById(propertyId.value)
    
    // Charger les images depuis Document Service
    if (property.value) {
      try {
        const documents = await documentService.getByPropertyId(propertyId.value)
        const imageUrls = documents
          .filter((doc: any) => doc.mimeType?.startsWith('image/'))
          .map((doc: any) => documentService.getViewUrl(doc.id))
        
        if (imageUrls.length > 0) {
          property.value.images = imageUrls
        }
      } catch (error) {
        console.warn('Could not load property documents:', error)
        // Utiliser les images du property si disponibles
      }
      
      // Enrichir avec le nom de l'organisation si non présent
      if ((property.value as any).organizationId && !(property.value as any).organizationName) {
        try {
          const org = await organizationService.getById((property.value as any).organizationId)
          organizationName.value = org.name
        } catch (error) {
          console.warn('Could not load organization name:', error)
        }
      } else if ((property.value as any).organizationName) {
        organizationName.value = (property.value as any).organizationName
      }
      
      // Enrichir avec le nom de l'utilisateur assigné si non présent
      if ((property.value as any).assignedUserId && !(property.value as any).assignedUserName) {
        // Note: On pourrait charger depuis userService si disponible
        // Pour l'instant, on utilise ce qui est retourné par le backend
      } else if ((property.value as any).assignedUserName) {
        assignedUserName.value = (property.value as any).assignedUserName
      }
    }
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger la propriété',
      variant: 'destructive'
    })
    router.push('/properties')
  } finally {
    loading.value = false
  }
}

const loadPropertyFeatures = async () => {
  try {
    const features = await propertyService.getPropertyFeatures(propertyId.value)
    // S'assurer que features est toujours un tableau
    propertyFeatures.value = Array.isArray(features) ? features : []
  } catch (error: any) {
    console.warn('Could not load property features:', error)
    // S'assurer que propertyFeatures reste un tableau vide en cas d'erreur
    propertyFeatures.value = []
    // Ne pas afficher d'erreur si c'est une 401 (l'utilisateur sera redirigé)
    if (error.response?.status !== 401) {
      toast({
        title: 'Avertissement',
        description: 'Impossible de charger les caractéristiques détaillées',
        variant: 'default'
      })
    }
  }
}

const loadReviewStats = async () => {
  try {
    reviewStats.value = await reviewService.getStatsByProperty(propertyId.value)
  } catch (error: any) {
    console.warn('Could not load review stats:', error)
    // Ne pas afficher d'erreur si c'est une 401 (l'utilisateur sera redirigé)
    if (error.response?.status !== 401) {
      // Stats optionnelles, on ne montre pas d'erreur
    }
  }
}

const getFeaturesByKey = (key: string) => {
  if (!propertyFeatures.value || !Array.isArray(propertyFeatures.value)) {
    return []
  }
  return propertyFeatures.value.filter((feature: PropertyFeature) => feature.key === key)
}

const getOrganizationName = computed(() => {
  return organizationName.value || (property.value as any)?.organizationName || null
})

const getAssignedUserName = computed(() => {
  return assignedUserName.value || (property.value as any)?.assignedUserName || null
})

const goBack = () => {
  router.push('/properties')
}

const editProperty = () => {
  router.push(`/properties/${propertyId.value}/edit`)
}

const deleteProperty = async () => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette propriété ?')) return
  loading.value = true
  try {
    await propertyService.delete(propertyId.value)
    toast({
      title: 'Propriété supprimée',
      description: 'La propriété a été supprimée avec succès'
    })
    router.push('/properties')
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de supprimer la propriété',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const openImageModal = (image: string, index?: number) => {
  selectedImage.value = image
  selectedImageIndex.value = index ?? (property.value?.images?.indexOf(image) ?? 0)
  imageModalOpen.value = true
}

const nextImage = () => {
  if (!property.value?.images) return
  selectedImageIndex.value = (selectedImageIndex.value + 1) % property.value.images.length
  selectedImage.value = property.value.images[selectedImageIndex.value]
}

const previousImage = () => {
  if (!property.value?.images) return
  selectedImageIndex.value = selectedImageIndex.value === 0 
    ? property.value.images.length - 1 
    : selectedImageIndex.value - 1
  selectedImage.value = property.value.images[selectedImageIndex.value]
}

// Export functions
const exportToCSV = () => {
  if (!property.value) return
  const headers = ['ID', 'Titre', 'Type', 'Prix', 'Adresse', 'Ville', 'Statut', 'Chambres', 'Salles de bain', 'Surface', 'Créée le']
  const row = [
    property.value.id,
    property.value.title,
    getTypeLabel((property.value as any).type || property.value.propertyType),
    formatPrice(property.value.price),
    property.value.address,
    property.value.city,
    getStatusLabel(property.value.status),
    property.value.bedrooms || '',
    property.value.bathrooms || '',
    property.value.area || '',
    formatDate(property.value.createdAt)
  ]
  const csvContent = [
    headers.join(','),
    row.map((cell: any) => `"${String(cell).replace(/"/g, '""')}"`).join(',')
  ].join('\n')
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', `property_${property.value.id}_export_${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  toast({
    title: 'Export réussi',
    description: 'Propriété exportée en CSV'
  })
}

const exportToExcel = () => {
  if (!property.value) return
  const headers = ['ID', 'Titre', 'Type', 'Prix', 'Adresse', 'Ville', 'Statut', 'Chambres', 'Salles de bain', 'Surface', 'Créée le']
  const row = [
    property.value.id,
    property.value.title,
    getTypeLabel((property.value as any).type || property.value.propertyType),
    formatPrice(property.value.price),
    property.value.address,
    property.value.city,
    getStatusLabel(property.value.status),
    property.value.bedrooms || '',
    property.value.bathrooms || '',
    property.value.area || '',
    formatDate(property.value.createdAt)
  ]
  const excelContent = [
    headers.join('\t'),
    row.map((cell: any) => String(cell).replace(/\t/g, ' ')).join('\t')
  ].join('\n')
  const blob = new Blob(['\ufeff' + excelContent], { type: 'application/vnd.ms-excel;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', `property_${property.value.id}_export_${new Date().toISOString().split('T')[0]}.xls`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  toast({
    title: 'Export réussi',
    description: 'Propriété exportée en Excel'
  })
}

// Utility functions
const getTypeLabel = (type: PropertyType | string) => {
  const typeStr = typeof type === 'string' ? type : String(type)
  const labels: Record<string, string> = {
    [PropertyType.APARTMENT]: 'Appartement',
    [PropertyType.HOUSE]: 'Maison',
    [PropertyType.VILLA]: 'Villa',
    [PropertyType.LAND]: 'Terrain',
    [PropertyType.COMMERCIAL]: 'Commercial',
    [PropertyType.OTHER]: 'Autre'
  }
  return labels[typeStr] || typeStr
}

const getStatusLabel = (status: PropertyStatus) => {
  const labels: Record<PropertyStatus, string> = {
    [PropertyStatus.AVAILABLE]: 'Disponible',
    [PropertyStatus.SOLD]: 'Vendu',
    [PropertyStatus.RENTED]: 'Loué',
    [PropertyStatus.PENDING]: 'En attente',
    [PropertyStatus.DRAFT]: 'Brouillon'
  }
  return labels[status] || status
}

const getStatusVariant = (status: PropertyStatus): 'default' | 'secondary' | 'destructive' | 'outline' => {
  const variants: Record<PropertyStatus, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    [PropertyStatus.AVAILABLE]: 'default',
    [PropertyStatus.SOLD]: 'secondary',
    [PropertyStatus.RENTED]: 'outline',
    [PropertyStatus.PENDING]: 'destructive',
    [PropertyStatus.DRAFT]: 'outline'
  }
  return variants[status] || 'default'
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

const formatDate = (date: string | undefined) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const parseJsonArray = (jsonString: string | undefined): string[] | null => {
  if (!jsonString) return null
  try {
    const parsed = JSON.parse(jsonString)
    return Array.isArray(parsed) ? parsed : null
  } catch {
    return null
  }
}

const getLaundryLabel = (location: string | undefined): string => {
  if (!location) return 'N/A'
  const labels: Record<string, string> = {
    'Inside': 'Intérieur',
    'Outside': 'Extérieur',
    'None': 'Aucune'
  }
  return labels[location] || location
}

const getHoaFrequencyLabel = (frequency: string | undefined): string => {
  if (!frequency) return 'N/A'
  const labels: Record<string, string> = {
    'monthly': 'Mensuel',
    'quarterly': 'Trimestriel',
    'annually': 'Annuel'
  }
  return labels[frequency] || frequency
}
</script>

