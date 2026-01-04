<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <div class="flex items-center gap-4 mb-4">
          <router-link
            to="/my-properties"
            class="text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft class="h-5 w-5" />
          </router-link>
          <h1 class="text-3xl font-bold text-gray-900">
            {{ isEdit ? 'Modifier l\'annonce' : 'Nouvelle annonce' }}
          </h1>
        </div>
        <p class="text-sm text-gray-600">
          {{ isEdit ? 'Modifiez les informations de votre annonce' : 'Remplissez les informations de votre annonce' }}
        </p>
      </div>

      <!-- Messages d'erreur globaux -->
      <div
        v-if="globalError"
        class="mb-6 bg-red-50 border border-red-200 rounded-lg p-4"
      >
        <div class="flex items-start gap-3">
          <AlertCircle class="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
          <div class="flex-1">
            <h3 class="text-sm font-medium text-red-800 mb-1">Erreur</h3>
            <p class="text-sm text-red-700">{{ globalError }}</p>
          </div>
          <button
            @click="globalError = null"
            class="text-red-600 hover:text-red-800"
          >
            <X class="h-4 w-4" />
          </button>
        </div>
      </div>

      <!-- Formulaire -->
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- Informations de base -->
        <Card>
          <CardHeader>
            <CardTitle>Informations de base</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div class="space-y-2">
              <Label class="flex items-center gap-2">
                Titre de l'annonce <span class="text-red-500">*</span>
                <HelpTooltip
                  title="Titre de l'annonce"
                  content="Un titre accrocheur augmente les vues. Utilisez des mots-clés importants comme le type de bien, la localisation et les caractéristiques principales (ex: 'Appartement 3 pièces avec balcon à Paris 15ème')."
                />
              </Label>
              <Input
                v-model="form.title"
                type="text"
                required
                :class="fieldErrors.title ? 'border-red-300 focus-visible:ring-red-500' : ''"
                placeholder="Ex: Appartement 3 pièces à Paris"
                @blur="validateField('title')"
              />
              <p v-if="fieldErrors.title" class="text-sm text-red-600">{{ fieldErrors.title }}</p>
            </div>

            <div class="space-y-2">
              <Label class="flex items-center gap-2">
                Description <span class="text-red-500">*</span>
                <HelpTooltip
                  title="Description détaillée"
                  content="Décrivez votre bien en détail : état, équipements, environnement, transports, etc. Une description complète et honnête augmente la confiance des visiteurs et les contacts qualifiés."
                />
              </Label>
              <textarea
                v-model="form.description"
                rows="5"
                required
                :class="[
                  'flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50',
                  fieldErrors.description ? 'border-red-300 focus-visible:ring-red-500' : ''
                ]"
                placeholder="Décrivez votre bien immobilier..."
                @blur="validateField('description')"
              />
              <p v-if="fieldErrors.description" class="text-sm text-red-600">{{ fieldErrors.description }}</p>
              <p class="text-xs text-muted-foreground">{{ form.description.length }} caractères</p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label>
                  Type de bien <span class="text-red-500">*</span>
                </Label>
                <Select v-model="form.propertyType" required @update:model-value="validateField('propertyType')">
                  <SelectTrigger :class="fieldErrors.propertyType ? 'border-red-300' : ''">
                    <SelectValue placeholder="Sélectionner..." />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="APARTMENT">Appartement</SelectItem>
                    <SelectItem value="HOUSE">Maison</SelectItem>
                    <SelectItem value="VILLA">Villa</SelectItem>
                    <SelectItem value="LAND">Terrain</SelectItem>
                    <SelectItem value="COMMERCIAL">Commercial</SelectItem>
                    <SelectItem value="OTHER">Autre</SelectItem>
                  </SelectContent>
                </Select>
                <p v-if="fieldErrors.propertyType" class="text-sm text-red-600">{{ fieldErrors.propertyType }}</p>
              </div>

              <div class="space-y-2">
                <Label>
                  Type de transaction <span class="text-red-500">*</span>
                </Label>
                <Select v-model="form.transactionType" required @update:model-value="validateField('transactionType')">
                  <SelectTrigger :class="fieldErrors.transactionType ? 'border-red-300' : ''">
                    <SelectValue placeholder="Sélectionner..." />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="SALE">Vente</SelectItem>
                    <SelectItem value="RENT">Location</SelectItem>
                  </SelectContent>
                </Select>
                <p v-if="fieldErrors.transactionType" class="text-sm text-red-600">{{ fieldErrors.transactionType }}</p>
              </div>
            </div>

            <div class="space-y-2">
              <Label>
                Prix <span class="text-red-500">*</span>
              </Label>
              <Input
                v-model.number="form.price"
                type="number"
                required
                min="0"
                step="100"
                :class="fieldErrors.price ? 'border-red-300 focus-visible:ring-red-500' : ''"
                placeholder="Ex: 250000"
                @blur="validateField('price')"
              />
              <p v-if="fieldErrors.price" class="text-sm text-red-600">{{ fieldErrors.price }}</p>
            </div>
          </CardContent>
        </Card>

        <!-- Images -->
        <Card>
          <CardHeader>
            <CardTitle>Images</CardTitle>
          </CardHeader>
          <CardContent>
            <ImageUpload
              v-model="images"
              :max-images="10"
              ref="imageUploadRef"
            />
          </CardContent>
        </Card>

        <!-- Caractéristiques principales -->
        <Card>
          <CardHeader>
            <CardTitle>Caractéristiques principales</CardTitle>
            <CardDescription>Informations sur la superficie et les pièces</CardDescription>
          </CardHeader>
          <CardContent>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div class="space-y-2">
                <Label>Surface (m²)</Label>
                <Input
                  v-model.number="form.area"
                  type="number"
                  min="0"
                  step="1"
                  placeholder="Ex: 75"
                />
              </div>

              <div class="space-y-2">
                <Label>Pièces totales</Label>
                <Input
                  v-model.number="form.rooms"
                  type="number"
                  min="0"
                  step="1"
                  placeholder="Ex: 4"
                />
              </div>

              <div class="space-y-2">
                <Label>Chambres</Label>
                <Input
                  v-model.number="form.bedrooms"
                  type="number"
                  min="0"
                  step="1"
                  placeholder="Ex: 3"
                />
              </div>

              <div class="space-y-2">
                <Label>Salles de bain</Label>
                <Input
                  v-model.number="form.bathrooms"
                  type="number"
                  min="0"
                  step="0.5"
                  placeholder="Ex: 2"
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              <div class="space-y-2">
                <Label>Surface habitable (m²)</Label>
                <Input
                  v-model.number="form.totalInteriorLivableArea"
                  type="number"
                  min="0"
                  step="1"
                  placeholder="Ex: 70"
                />
              </div>

              <div class="space-y-2">
                <Label>Surface totale du bâtiment (m²)</Label>
                <Input
                  v-model.number="form.totalStructureArea"
                  type="number"
                  min="0"
                  step="1"
                  placeholder="Ex: 100"
                />
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Informations de construction -->
        <Card>
          <CardHeader>
            <CardTitle>Informations de construction</CardTitle>
            <CardDescription>Détails sur la construction et l'état du bien</CardDescription>
          </CardHeader>
          <CardContent>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div class="space-y-2">
                <Label>Année de construction</Label>
                <Input
                  v-model.number="form.yearBuilt"
                  type="number"
                  min="1800"
                  :max="new Date().getFullYear()"
                  step="1"
                  placeholder="Ex: 2020"
                />
              </div>

              <div class="space-y-2">
                <Label>Type de bien</Label>
                <Select v-model="form.homeType">
                  <SelectTrigger>
                    <SelectValue placeholder="Sélectionner..." />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="CONDO">Appartement</SelectItem>
                    <SelectItem value="HOUSE">Maison</SelectItem>
                    <SelectItem value="TOWNHOUSE">Maison de ville</SelectItem>
                    <SelectItem value="MULTI_FAMILY">Multifamilial</SelectItem>
                    <SelectItem value="LAND">Terrain</SelectItem>
                    <SelectItem value="OTHER">Autre</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div class="space-y-2">
                <Label>État du bien</Label>
                <Select v-model="form.condition">
                  <SelectTrigger>
                    <SelectValue placeholder="Sélectionner..." />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="EXCELLENT">Excellent</SelectItem>
                    <SelectItem value="GOOD">Bon</SelectItem>
                    <SelectItem value="FAIR">Correct</SelectItem>
                    <SelectItem value="POOR">À rénover</SelectItem>
                    <SelectItem value="NEW_CONSTRUCTION">Neuf</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              <div class="space-y-2">
                <Label>Quartier</Label>
                <Input
                  v-model="form.neighborhood"
                  type="text"
                  placeholder="Ex: Centre-ville"
                />
              </div>

              <div class="space-y-2">
                <Label>Nom du bâtiment</Label>
                <Input
                  v-model="form.buildingName"
                  type="text"
                  placeholder="Ex: Résidence Les Jardins"
                />
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Équipements et caractéristiques -->
        <Card>
          <CardHeader>
            <CardTitle>Équipements et caractéristiques</CardTitle>
            <CardDescription>Sélectionnez les équipements et caractéristiques disponibles</CardDescription>
          </CardHeader>
          <CardContent class="space-y-6">
            <!-- Équipements intérieurs -->
            <div>
              <Label class="text-base font-semibold mb-3 block">Équipements intérieurs</Label>
              <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="dishwasher"
                    :checked="selectedAppliances.includes('Dishwasher')"
                    @update:checked="toggleAppliance('Dishwasher')"
                  />
                  <Label for="dishwasher" class="font-normal cursor-pointer">Lave-vaisselle</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="refrigerator"
                    :checked="selectedAppliances.includes('Refrigerator')"
                    @update:checked="toggleAppliance('Refrigerator')"
                  />
                  <Label for="refrigerator" class="font-normal cursor-pointer">Réfrigérateur</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="washer-dryer"
                    :checked="selectedAppliances.includes('Washer/Dryer')"
                    @update:checked="toggleAppliance('Washer/Dryer')"
                  />
                  <Label for="washer-dryer" class="font-normal cursor-pointer">Lave-linge/Sèche-linge</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="microwave"
                    :checked="selectedAppliances.includes('Microwave')"
                    @update:checked="toggleAppliance('Microwave')"
                  />
                  <Label for="microwave" class="font-normal cursor-pointer">Micro-ondes</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="oven"
                    :checked="selectedAppliances.includes('Oven')"
                    @update:checked="toggleAppliance('Oven')"
                  />
                  <Label for="oven" class="font-normal cursor-pointer">Four</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="air-conditioning"
                    :checked="selectedFeatures.includes('air_conditioning')"
                    @update:checked="toggleFeature('air_conditioning')"
                  />
                  <Label for="air-conditioning" class="font-normal cursor-pointer">Climatisation</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="heating"
                    :checked="selectedFeatures.includes('heating')"
                    @update:checked="toggleFeature('heating')"
                  />
                  <Label for="heating" class="font-normal cursor-pointer">Chauffage</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="fireplace"
                    :checked="selectedFeatures.includes('fireplace')"
                    @update:checked="toggleFeature('fireplace')"
                  />
                  <Label for="fireplace" class="font-normal cursor-pointer">Cheminée</Label>
                </div>
              </div>
            </div>

            <!-- Caractéristiques extérieures -->
            <div>
              <Label class="text-base font-semibold mb-3 block">Caractéristiques extérieures</Label>
              <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="balcony"
                    :checked="selectedExteriorFeatures.includes('Balcony')"
                    @update:checked="toggleExteriorFeature('Balcony')"
                  />
                  <Label for="balcony" class="font-normal cursor-pointer">Balcon</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="terrace"
                    :checked="selectedExteriorFeatures.includes('Terrace')"
                    @update:checked="toggleExteriorFeature('Terrace')"
                  />
                  <Label for="terrace" class="font-normal cursor-pointer">Terrasse</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="garden"
                    :checked="selectedExteriorFeatures.includes('Garden')"
                    @update:checked="toggleExteriorFeature('Garden')"
                  />
                  <Label for="garden" class="font-normal cursor-pointer">Jardin</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="patio"
                    :checked="selectedExteriorFeatures.includes('Patio')"
                    @update:checked="toggleExteriorFeature('Patio')"
                  />
                  <Label for="patio" class="font-normal cursor-pointer">Patio</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="courtyard"
                    :checked="selectedExteriorFeatures.includes('Courtyard')"
                    @update:checked="toggleExteriorFeature('Courtyard')"
                  />
                  <Label for="courtyard" class="font-normal cursor-pointer">Cour</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="pool"
                    :checked="selectedFeatures.includes('pool')"
                    @update:checked="toggleFeature('pool')"
                  />
                  <Label for="pool" class="font-normal cursor-pointer">Piscine</Label>
                </div>
              </div>
            </div>

            <!-- Parking -->
            <div>
              <Label class="text-base font-semibold mb-3 block">Parking</Label>
              <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="garage"
                    :checked="form.hasGarage"
                    @update:checked="form.hasGarage = $event"
                  />
                  <Label for="garage" class="font-normal cursor-pointer">Garage</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="parking-street"
                    :checked="selectedParkingFeatures.includes('Street')"
                    @update:checked="toggleParkingFeature('Street')"
                  />
                  <Label for="parking-street" class="font-normal cursor-pointer">Parking rue</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="parking-covered"
                    :checked="selectedParkingFeatures.includes('Covered')"
                    @update:checked="toggleParkingFeature('Covered')"
                  />
                  <Label for="parking-covered" class="font-normal cursor-pointer">Parking couvert</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="parking-private"
                    :checked="selectedParkingFeatures.includes('Private')"
                    @update:checked="toggleParkingFeature('Private')"
                  />
                  <Label for="parking-private" class="font-normal cursor-pointer">Parking privé</Label>
                </div>
              </div>
            </div>

            <!-- Accessibilité -->
            <div>
              <Label class="text-base font-semibold mb-3 block">Accessibilité</Label>
              <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="elevator"
                    :checked="selectedFeatures.includes('elevator')"
                    @update:checked="toggleFeature('elevator')"
                  />
                  <Label for="elevator" class="font-normal cursor-pointer">Ascenseur</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="wheelchair"
                    :checked="selectedAccessibilityFeatures.includes('Wheelchair Access')"
                    @update:checked="toggleAccessibilityFeature('Wheelchair Access')"
                  />
                  <Label for="wheelchair" class="font-normal cursor-pointer">Accès fauteuil roulant</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="ground-floor"
                    :checked="selectedFeatures.includes('ground_floor')"
                    @update:checked="toggleFeature('ground_floor')"
                  />
                  <Label for="ground-floor" class="font-normal cursor-pointer">Rez-de-chaussée</Label>
                </div>
              </div>
            </div>

            <!-- Autres caractéristiques -->
            <div>
              <Label class="text-base font-semibold mb-3 block">Autres caractéristiques</Label>
              <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="pet-friendly"
                    :checked="form.petFriendly"
                    @update:checked="form.petFriendly = $event"
                  />
                  <Label for="pet-friendly" class="font-normal cursor-pointer">Animaux acceptés</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="furnished"
                    :checked="selectedFeatures.includes('furnished')"
                    @update:checked="toggleFeature('furnished')"
                  />
                  <Label for="furnished" class="font-normal cursor-pointer">Meublé</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="storage"
                    :checked="selectedFeatures.includes('storage')"
                    @update:checked="toggleFeature('storage')"
                  />
                  <Label for="storage" class="font-normal cursor-pointer">Cave/Stockage</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <Checkbox
                    id="alarm"
                    :checked="selectedFeatures.includes('alarm')"
                    @update:checked="toggleFeature('alarm')"
                  />
                  <Label for="alarm" class="font-normal cursor-pointer">Alarme</Label>
                </div>
              </div>
            </div>

            <!-- Localisation du lave-linge -->
            <div class="space-y-2">
              <Label>Localisation du lave-linge</Label>
              <Select v-model="form.laundryLocation">
                <SelectTrigger>
                  <SelectValue placeholder="Sélectionner..." />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="INSIDE">À l'intérieur</SelectItem>
                  <SelectItem value="OUTSIDE">À l'extérieur</SelectItem>
                  <SelectItem value="NONE">Aucun</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardContent>
        </Card>

        <!-- Localisation -->
        <Card>
          <CardHeader>
            <CardTitle>Localisation</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div class="space-y-2">
              <Label>
                Adresse <span class="text-red-500">*</span>
              </Label>
              <Input
                v-model="form.address"
                type="text"
                required
                :class="fieldErrors.address ? 'border-red-300 focus-visible:ring-red-500' : ''"
                placeholder="Ex: 123 Rue de la République"
                @blur="validateField('address')"
              />
              <p v-if="fieldErrors.address" class="text-sm text-red-600">{{ fieldErrors.address }}</p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div class="space-y-2">
                <Label>
                  Ville <span class="text-red-500">*</span>
                </Label>
                <Input
                  v-model="form.city"
                  type="text"
                  required
                  :class="fieldErrors.city ? 'border-red-300 focus-visible:ring-red-500' : ''"
                  placeholder="Ex: Paris"
                  @blur="validateField('city')"
                />
                <p v-if="fieldErrors.city" class="text-sm text-red-600">{{ fieldErrors.city }}</p>
              </div>

              <div class="space-y-2">
                <Label>Code postal</Label>
                <Input
                  v-model="form.postalCode"
                  type="text"
                  placeholder="Ex: 75001"
                />
              </div>

              <div class="space-y-2">
                <Label>
                  Pays <span class="text-red-500">*</span>
                </Label>
                <Input
                  v-model="form.country"
                  type="text"
                  required
                  :class="fieldErrors.country ? 'border-red-300 focus-visible:ring-red-500' : ''"
                  placeholder="Ex: France"
                  @blur="validateField('country')"
                />
                <p v-if="fieldErrors.country" class="text-sm text-red-600">{{ fieldErrors.country }}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Statut (uniquement en édition) -->
        <Card v-if="isEdit">
          <CardHeader>
            <CardTitle>Statut</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="space-y-2">
              <Label>Statut de l'annonce</Label>
              <Select v-model="form.status">
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="DRAFT">Brouillon</SelectItem>
                  <SelectItem value="AVAILABLE">Disponible</SelectItem>
                  <SelectItem value="PENDING">En attente</SelectItem>
                  <SelectItem value="SOLD">Vendu</SelectItem>
                  <SelectItem value="RENTED">Loué</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardContent>
        </Card>

        <!-- Actions -->
        <div class="flex items-center justify-end gap-4">
          <Button
            type="button"
            variant="outline"
            as-child
          >
            <router-link to="/my-properties">
              Annuler
            </router-link>
          </Button>
          <Button
            type="button"
            variant="outline"
            @click="showPreview = true"
            :disabled="submitting || !isFormValid"
          >
            Aperçu
          </Button>
          <Button
            type="submit"
            :disabled="submitting || !isFormValid"
          >
            {{ submitting ? 'Enregistrement...' : (isEdit ? 'Enregistrer' : 'Créer l\'annonce') }}
          </Button>
        </div>
      </form>
    </div>

    <!-- Modal d'aperçu -->
    <PropertyPreview
      v-if="showPreview"
      :property="previewData"
      :images="images"
      @close="showPreview = false"
      @publish="handlePublishFromPreview"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, AlertCircle, X } from 'lucide-vue-next'
import { userPropertyService } from '@/api/user-property.service'
import { documentService, useAuthStore } from '@viridial/shared'
import type { PropertyCreate, PropertyUpdate } from '@viridial/shared'
import { useToast } from '@/composables/useToast'
import ImageUpload from '@/components/ImageUpload.vue'
import PropertyPreview from '@/components/PropertyPreview.vue'
import HelpTooltip from '@/components/HelpTooltip.vue'
import { Card, CardHeader, CardTitle, CardContent, CardDescription } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Checkbox } from '@/components/ui/checkbox'

const router = useRouter()
const route = useRoute()
const { showToast } = useToast()
const authStore = useAuthStore()

const isEdit = computed(() => !!route.params.id)
const submitting = ref(false)
const globalError = ref<string | null>(null)
const fieldErrors = ref<Record<string, string>>({})
const showPreview = ref(false)
const imageUploadRef = ref<InstanceType<typeof ImageUpload>>()
const images = ref<any[]>([])

const form = ref<PropertyCreate & { 
  status?: string
  postalCode?: string
  rooms?: number
  totalInteriorLivableArea?: number
  totalStructureArea?: number
  yearBuilt?: number
  homeType?: string
  condition?: string
  neighborhood?: string
  buildingName?: string
  laundryLocation?: string
  hasGarage?: boolean
  petFriendly?: boolean
  virtualTourUrl?: string
  specialOffer?: string
}>({
  title: '',
  description: '',
  price: 0,
  address: '',
  city: '',
  country: 'France',
  propertyType: 'APARTMENT',
  transactionType: 'SALE',
  bedrooms: undefined,
  bathrooms: undefined,
  area: undefined,
  status: 'DRAFT',
  postalCode: '',
  rooms: undefined,
  totalInteriorLivableArea: undefined,
  totalStructureArea: undefined,
  yearBuilt: undefined,
  homeType: undefined,
  condition: undefined,
  neighborhood: undefined,
  buildingName: undefined,
  laundryLocation: undefined,
  hasGarage: false,
  petFriendly: false,
  virtualTourUrl: undefined,
  specialOffer: undefined,
})

// Features arrays
const selectedAppliances = ref<string[]>([])
const selectedFeatures = ref<string[]>([])
const selectedExteriorFeatures = ref<string[]>([])
const selectedParkingFeatures = ref<string[]>([])
const selectedAccessibilityFeatures = ref<string[]>([])

const isFormValid = computed(() => {
  return form.value.title &&
    form.value.description &&
    form.value.price > 0 &&
    form.value.address &&
    form.value.city &&
    form.value.country &&
    form.value.propertyType &&
    form.value.transactionType &&
    Object.keys(fieldErrors.value).length === 0
})

const previewData = computed(() => ({
  ...form.value,
  id: isEdit.value ? Number(route.params.id) : undefined,
}))

// Functions to toggle features
function toggleAppliance(appliance: string) {
  const index = selectedAppliances.value.indexOf(appliance)
  if (index > -1) {
    selectedAppliances.value.splice(index, 1)
  } else {
    selectedAppliances.value.push(appliance)
  }
}

function toggleFeature(feature: string) {
  const index = selectedFeatures.value.indexOf(feature)
  if (index > -1) {
    selectedFeatures.value.splice(index, 1)
  } else {
    selectedFeatures.value.push(feature)
  }
}

function toggleExteriorFeature(feature: string) {
  const index = selectedExteriorFeatures.value.indexOf(feature)
  if (index > -1) {
    selectedExteriorFeatures.value.splice(index, 1)
  } else {
    selectedExteriorFeatures.value.push(feature)
  }
}

function toggleParkingFeature(feature: string) {
  const index = selectedParkingFeatures.value.indexOf(feature)
  if (index > -1) {
    selectedParkingFeatures.value.splice(index, 1)
  } else {
    selectedParkingFeatures.value.push(feature)
  }
}

function toggleAccessibilityFeature(feature: string) {
  const index = selectedAccessibilityFeatures.value.indexOf(feature)
  if (index > -1) {
    selectedAccessibilityFeatures.value.splice(index, 1)
  } else {
    selectedAccessibilityFeatures.value.push(feature)
  }
}

onMounted(async () => {
  if (isEdit.value) {
    await loadProperty()
  }
})

async function loadProperty() {
  try {
    const propertyId = Number(route.params.id)
    const property = await userPropertyService.getMyPropertyById(propertyId)
    
    form.value = {
      title: property.title || '',
      description: property.description || '',
      price: property.price || 0,
      address: property.address || '',
      city: property.city || '',
      country: property.country || 'France',
      propertyType: (property.propertyType || property.type) as any || 'APARTMENT',
      transactionType: property.transactionType || 'SALE',
      bedrooms: property.bedrooms,
      bathrooms: property.bathrooms,
      area: property.area || property.surface,
      status: property.status || 'DRAFT',
      postalCode: property.postalCode || '',
      rooms: property.rooms,
      totalInteriorLivableArea: property.totalInteriorLivableArea,
      totalStructureArea: property.totalStructureArea,
      yearBuilt: property.yearBuilt,
      homeType: property.homeType,
      condition: property.condition,
      neighborhood: property.neighborhood,
      buildingName: property.buildingName,
      laundryLocation: property.laundryLocation,
      hasGarage: property.hasGarage || false,
      petFriendly: property.petFriendly || false,
      virtualTourUrl: property.virtualTourUrl,
      specialOffer: property.specialOffer,
    }

    // Load features from JSON strings
    try {
      if (property.appliancesIncluded) {
        selectedAppliances.value = JSON.parse(property.appliancesIncluded)
      }
      if (property.exteriorFeatures) {
        selectedExteriorFeatures.value = JSON.parse(property.exteriorFeatures)
      }
      if (property.parkingFeatures) {
        selectedParkingFeatures.value = JSON.parse(property.parkingFeatures)
      }
      if (property.accessibilityFeatures) {
        selectedAccessibilityFeatures.value = JSON.parse(property.accessibilityFeatures)
      }
      if ((property as any).features) {
        const features = JSON.parse((property as any).features)
        selectedFeatures.value = Object.keys(features).filter(key => features[key] === true)
      }
    } catch (err) {
      console.warn('Error parsing features:', err)
    }

    // Charger les images existantes
    try {
      const documents = await documentService.getByPropertyId(propertyId)
      const imageDocuments = documents.filter(doc => 
        doc.type === 'IMAGE' || doc.mimeType?.startsWith('image/')
      )
      images.value = imageDocuments.map(doc => ({
        file: null,
        preview: documentService.getViewUrl(doc.id),
        isPrimary: false,
        documentId: doc.id,
        url: documentService.getViewUrl(doc.id),
      }))
    } catch (err) {
      console.warn('Could not load property images:', err)
    }
  } catch (err: any) {
    const errorMessage = err.response?.data?.message || err.message || 'Erreur lors du chargement de l\'annonce'
    globalError.value = errorMessage
    showToast(errorMessage, 'error')
    setTimeout(() => {
      router.push('/my-properties')
    }, 3000)
  }
}

function validateField(fieldName: string) {
  const field = form.value[fieldName as keyof typeof form.value]
  
  switch (fieldName) {
    case 'title':
      if (!field || String(field).trim().length < 10) {
        fieldErrors.value.title = 'Le titre doit contenir au moins 10 caractères'
      } else {
        delete fieldErrors.value.title
      }
      break
    case 'description':
      if (!field || String(field).trim().length < 50) {
        fieldErrors.value.description = 'La description doit contenir au moins 50 caractères'
      } else {
        delete fieldErrors.value.description
      }
      break
    case 'price':
      if (!field || Number(field) <= 0) {
        fieldErrors.value.price = 'Le prix doit être supérieur à 0'
      } else {
        delete fieldErrors.value.price
      }
      break
    case 'address':
    case 'city':
    case 'country':
      if (!field || String(field).trim().length === 0) {
        fieldErrors.value[fieldName] = 'Ce champ est requis'
      } else {
        delete fieldErrors.value[fieldName]
      }
      break
  }
}

async function uploadImages(propertyId: number) {
  if (!authStore.user || !imageUploadRef.value) return

  const imageItems = imageUploadRef.value.getImages()
  const imagesToUpload = imageItems.filter(img => img.file && !img.documentId)
  
  if (imagesToUpload.length === 0) return

  const organizationId = authStore.user.organizationId || 1

  for (let i = 0; i < imagesToUpload.length; i++) {
    const image = imagesToUpload[i]
    if (!image.file) continue

    try {
      imageUploadRef.value.setUploading(i, true)
      
      const document = await documentService.upload({
        file: image.file,
        organizationId,
        createdBy: authStore.user.id!,
        propertyId,
        description: `Image pour la propriété ${propertyId}`
      })

      imageUploadRef.value.setDocumentId(i, document.id, documentService.getViewUrl(document.id))
    } catch (err: any) {
      console.error('Error uploading image:', err)
      showToast(`Erreur lors de l'upload de l'image ${i + 1}`, 'error')
    } finally {
      imageUploadRef.value.setUploading(i, false)
    }
  }
}

async function handleSubmit() {
  // Valider tous les champs
  Object.keys(form.value).forEach(key => {
    if (['title', 'description', 'price', 'address', 'city', 'country', 'propertyType', 'transactionType'].includes(key)) {
      validateField(key)
    }
  })

  if (Object.keys(fieldErrors.value).length > 0) {
    globalError.value = 'Veuillez corriger les erreurs dans le formulaire'
    return
  }

  submitting.value = true
  globalError.value = null

  try {
    let propertyId: number

    // Prepare features as JSON strings
    const features: Record<string, boolean> = {}
    selectedFeatures.value.forEach(feature => {
      features[feature] = true
    })

    const commonData: any = {
      rooms: form.value.rooms,
      totalInteriorLivableArea: form.value.totalInteriorLivableArea,
      totalStructureArea: form.value.totalStructureArea,
      yearBuilt: form.value.yearBuilt,
      homeType: form.value.homeType,
      condition: form.value.condition,
      neighborhood: form.value.neighborhood,
      buildingName: form.value.buildingName,
      postalCode: form.value.postalCode,
      laundryLocation: form.value.laundryLocation,
      hasGarage: form.value.hasGarage,
      petFriendly: form.value.petFriendly,
      virtualTourUrl: form.value.virtualTourUrl,
      specialOffer: form.value.specialOffer,
      appliancesIncluded: selectedAppliances.value.length > 0 ? JSON.stringify(selectedAppliances.value) : undefined,
      exteriorFeatures: selectedExteriorFeatures.value.length > 0 ? JSON.stringify(selectedExteriorFeatures.value) : undefined,
      parkingFeatures: selectedParkingFeatures.value.length > 0 ? JSON.stringify(selectedParkingFeatures.value) : undefined,
      accessibilityFeatures: selectedAccessibilityFeatures.value.length > 0 ? JSON.stringify(selectedAccessibilityFeatures.value) : undefined,
      features: Object.keys(features).length > 0 ? JSON.stringify(features) : undefined,
    }

    if (isEdit.value) {
      const propertyIdParam = Number(route.params.id)
      const updateData: PropertyUpdate & any = {
        ...form.value,
        type: form.value.propertyType, // Le backend attend 'type' pas 'propertyType'
        surface: form.value.area, // Le backend peut attendre 'surface' aussi
        ...commonData,
        status: form.value.status as any,
      }
      const updated = await userPropertyService.updateProperty(propertyIdParam, updateData)
      propertyId = updated.id
      showToast('Annonce modifiée avec succès', 'success')
    } else {
      const createData: PropertyCreate & any = {
        // reference: générée automatiquement côté backend
        title: form.value.title,
        description: form.value.description,
        price: form.value.price,
        address: form.value.address,
        city: form.value.city,
        country: form.value.country,
        type: form.value.propertyType, // Le backend attend 'type' (obligatoire @NotBlank), pas 'propertyType'
        propertyType: form.value.propertyType, // Garder aussi pour compatibilité avec le frontend
        transactionType: form.value.transactionType,
        bedrooms: form.value.bedrooms,
        bathrooms: form.value.bathrooms,
        area: form.value.area,
        surface: form.value.area, // Le backend utilise 'surface' dans l'entité Property
        status: form.value.status || 'DRAFT', // Statut obligatoire (@NotBlank)
        ...commonData,
      }
      const created = await userPropertyService.createProperty(createData)
      propertyId = created.id
      showToast('Annonce créée avec succès', 'success')
    }

    // Uploader les images après création/modification
    await uploadImages(propertyId)

    router.push('/my-properties')
  } catch (err: any) {
    const errorMessage = err.response?.data?.message || err.message || 'Erreur lors de l\'enregistrement'
    globalError.value = errorMessage
    showToast(errorMessage, 'error')
  } finally {
    submitting.value = false
  }
}

async function handlePublishFromPreview() {
  showPreview.value = false
  // Changer le statut en AVAILABLE et soumettre
  form.value.status = 'AVAILABLE'
  await handleSubmit()
}
</script>
