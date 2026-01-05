<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="icon" @click="goBack">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">{{ isEdit ? 'Modifier la propriété' : 'Nouvelle propriété' }}</h1>
          <p class="text-muted-foreground mt-1">
            {{ isEdit ? 'Modifiez les informations de la propriété' : 'Créez une nouvelle propriété immobilière' }}
          </p>
        </div>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>

    <Card v-else>
      <CardContent class="p-6">
        <form @submit="onSubmit" class="space-y-6">
          <!-- Informations principales -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Informations principales</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="title">Titre *</Label>
                <Input
                  id="title"
                  v-model="title"
                  v-bind="titleAttrs"
                  placeholder="Ex: Appartement 3 pièces centre-ville"
                />
                <p v-if="errors.title" class="text-sm text-destructive">{{ errors.title }}</p>
              </div>

              <div class="space-y-2">
                <Label for="propertyType">Type de propriété *</Label>
                <Select v-model="propertyType" v-bind="propertyTypeAttrs">
                  <SelectTrigger>
                    <SelectValue :placeholder="propertyType ? undefined : 'Sélectionnez un type'" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem :value="PropertyType.APARTMENT">Appartement</SelectItem>
                    <SelectItem :value="PropertyType.HOUSE">Maison</SelectItem>
                    <SelectItem :value="PropertyType.VILLA">Villa</SelectItem>
                    <SelectItem :value="PropertyType.LAND">Terrain</SelectItem>
                    <SelectItem :value="PropertyType.COMMERCIAL">Commercial</SelectItem>
                    <SelectItem :value="PropertyType.OTHER">Autre</SelectItem>
                  </SelectContent>
                </Select>
                <p v-if="errors.propertyType" class="text-sm text-destructive">{{ errors.propertyType }}</p>
              </div>

              <div class="space-y-2">
                <Label for="transactionType">Type de transaction</Label>
                <Select v-model="transactionType" v-bind="transactionTypeAttrs">
                  <SelectTrigger>
                    <SelectValue :placeholder="transactionType ? undefined : 'Sélectionnez un type'" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">Non spécifié</SelectItem>
                    <SelectItem value="RENT">📍 Location</SelectItem>
                    <SelectItem value="SALE">💰 Vente</SelectItem>
                  </SelectContent>
                </Select>
                <p v-if="errors.transactionType" class="text-sm text-destructive">{{ errors.transactionType }}</p>
              </div>
            </div>

            <div class="space-y-2">
              <Label for="description">Description *</Label>
              <textarea
                id="description"
                v-model="description"
                v-bind="descriptionAttrs"
                placeholder="Description détaillée de la propriété..."
                rows="4"
                class="flex min-h-[100px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
              />
              <p v-if="errors.description" class="text-sm text-destructive">{{ errors.description }}</p>
            </div>
          </div>

          <!-- Prix et caractéristiques -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Prix et caractéristiques</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div class="space-y-2">
                <Label for="price">Prix (€) *</Label>
                <Input
                  id="price"
                  v-model.number="price"
                  v-bind="priceAttrs"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                />
                <p v-if="errors.price" class="text-sm text-destructive">{{ errors.price }}</p>
              </div>

              <div class="space-y-2">
                <Label for="bedrooms">Chambres</Label>
                <Input
                  id="bedrooms"
                  v-model.number="bedrooms"
                  v-bind="bedroomsAttrs"
                  type="number"
                  min="0"
                  placeholder="0"
                />
              </div>

              <div class="space-y-2">
                <Label for="bathrooms">Salles de bain</Label>
                <Input
                  id="bathrooms"
                  v-model.number="bathrooms"
                  v-bind="bathroomsAttrs"
                  type="number"
                  min="0"
                  step="0.5"
                  placeholder="0"
                />
              </div>

              <div class="space-y-2">
                <Label for="fullBathrooms">Salles de bain complètes</Label>
                <Input
                  id="fullBathrooms"
                  v-model.number="fullBathrooms"
                  v-bind="fullBathroomsAttrs"
                  type="number"
                  min="0"
                  placeholder="0"
                />
              </div>

              <div class="space-y-2">
                <Label for="area">Surface (m²)</Label>
                <Input
                  id="area"
                  v-model.number="area"
                  v-bind="areaAttrs"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0"
                />
              </div>
            </div>
          </div>

          <!-- Interior Details -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Détails intérieurs</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="totalStructureArea">Surface totale structure (m²)</Label>
                <Input
                  id="totalStructureArea"
                  v-model.number="totalStructureArea"
                  v-bind="totalStructureAreaAttrs"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0"
                />
              </div>

              <div class="space-y-2">
                <Label for="totalInteriorLivableArea">Surface habitable intérieure (m²)</Label>
                <Input
                  id="totalInteriorLivableArea"
                  v-model.number="totalInteriorLivableArea"
                  v-bind="totalInteriorLivableAreaAttrs"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0"
                />
              </div>

              <div class="space-y-2">
                <Label for="appliancesIncluded">Équipements inclus (JSON array)</Label>
                <Input
                  id="appliancesIncluded"
                  v-model="form.appliancesIncluded"
                  placeholder='["Dishwasher", "Refrigerator"]'
                />
                <p class="text-xs text-muted-foreground">Format: ["Item1", "Item2"]</p>
              </div>

              <div class="space-y-2">
                <Label for="laundryLocation">Localisation laverie</Label>
                <Select v-model="form.laundryLocation">
                  <SelectTrigger>
                    <SelectValue placeholder="Sélectionnez" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Inside">Intérieur</SelectItem>
                    <SelectItem value="Outside">Extérieur</SelectItem>
                    <SelectItem value="None">Aucune</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </div>

          <!-- Video & Virtual Tour -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Visite virtuelle</h2>
            
            <div class="space-y-2">
              <Label for="virtualTourUrl">URL visite virtuelle</Label>
              <Input
                id="virtualTourUrl"
                v-model="form.virtualTourUrl"
                type="url"
                placeholder="https://..."
              />
            </div>
          </div>

          <!-- Parking & Accessibility -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Parking et accessibilité</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="parkingFeatures">Caractéristiques parking (JSON array)</Label>
                <Input
                  id="parkingFeatures"
                  v-model="form.parkingFeatures"
                  placeholder='["Garage", "Street"]'
                />
                <p class="text-xs text-muted-foreground">Format: ["Feature1", "Feature2"]</p>
              </div>

              <div class="space-y-2">
                <Label for="hasGarage">A un garage</Label>
                <div class="flex items-center space-x-2 pt-2">
                  <input
                    id="hasGarage"
                    v-model="form.hasGarage"
                    type="checkbox"
                    class="h-4 w-4 rounded border-gray-300"
                  />
                  <Label for="hasGarage" class="cursor-pointer">Oui</Label>
                </div>
              </div>

              <div class="space-y-2">
                <Label for="accessibilityFeatures">Caractéristiques accessibilité (JSON array)</Label>
                <Input
                  id="accessibilityFeatures"
                  v-model="form.accessibilityFeatures"
                  placeholder='["Wheelchair Access"]'
                />
                <p class="text-xs text-muted-foreground">Format: ["Feature1", "Feature2"]</p>
              </div>
            </div>
          </div>

          <!-- Exterior Features -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Caractéristiques extérieures</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="patioPorch">Patio & Porche</Label>
                <Input
                  id="patioPorch"
                  v-model="form.patioPorch"
                  placeholder="Other, Patio, Porch, etc."
                />
              </div>

              <div class="space-y-2">
                <Label for="exteriorFeatures">Caractéristiques extérieures (JSON array)</Label>
                <Input
                  id="exteriorFeatures"
                  v-model="form.exteriorFeatures"
                  placeholder='["Courtyard", "Balcony"]'
                />
                <p class="text-xs text-muted-foreground">Format: ["Feature1", "Feature2"]</p>
              </div>
            </div>
          </div>

          <!-- Construction Details -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Détails de construction</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="homeType">Type de maison</Label>
                <Select v-model="form.homeType">
                  <SelectTrigger>
                    <SelectValue placeholder="Sélectionnez" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Condo">Condominium</SelectItem>
                    <SelectItem value="House">Maison</SelectItem>
                    <SelectItem value="Townhouse">Maison de ville</SelectItem>
                    <SelectItem value="Apartment">Appartement</SelectItem>
                    <SelectItem value="Villa">Villa</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div class="space-y-2">
                <Label for="propertySubtype">Sous-type de propriété</Label>
                <Input
                  id="propertySubtype"
                  v-model="form.propertySubtype"
                  placeholder="Condominium, Single Family, etc."
                />
              </div>

              <div class="space-y-2">
                <Label for="condition">État</Label>
                <Select v-model="form.condition">
                  <SelectTrigger>
                    <SelectValue placeholder="Sélectionnez" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="New">Neuf</SelectItem>
                    <SelectItem value="Good">Bon</SelectItem>
                    <SelectItem value="Fair">Correct</SelectItem>
                    <SelectItem value="Needs Renovation">Nécessite rénovation</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div class="space-y-2">
                <Label for="yearBuilt">Année de construction</Label>
                <Input
                  id="yearBuilt"
                  v-model.number="yearBuilt"
                  v-bind="yearBuiltAttrs"
                  type="number"
                  min="1800"
                  :max="new Date().getFullYear()"
                  placeholder="1990"
                />
              </div>

              <div class="space-y-2">
                <Label for="specialConditions">Conditions spéciales</Label>
                <Input
                  id="specialConditions"
                  v-model="form.specialConditions"
                  placeholder="Resale, New Construction, Foreclosure, etc."
                />
              </div>
            </div>
          </div>

          <!-- Community & HOA -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Communauté et HOA</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="subdivision">Subdivision</Label>
                <Input
                  id="subdivision"
                  v-model="form.subdivision"
                  placeholder="Nom de la subdivision"
                />
              </div>

              <div class="space-y-2">
                <Label for="hasHOA">A une HOA</Label>
                <div class="flex items-center space-x-2 pt-2">
                  <input
                    id="hasHOA"
                    v-model="form.hasHOA"
                    type="checkbox"
                    class="h-4 w-4 rounded border-gray-300"
                  />
                  <Label for="hasHOA" class="cursor-pointer">Oui</Label>
                </div>
              </div>

              <div class="space-y-2">
                <Label for="hoaAmenities">Équipements HOA (JSON array)</Label>
                <Input
                  id="hoaAmenities"
                  v-model="form.hoaAmenities"
                  placeholder='["Laundry", "Elevator(s)", "Pool"]'
                />
                <p class="text-xs text-muted-foreground">Format: ["Item1", "Item2"]</p>
              </div>

              <div class="space-y-2">
                <Label for="hoaServices">Services HOA (JSON array)</Label>
                <Input
                  id="hoaServices"
                  v-model="form.hoaServices"
                  placeholder='["Maintenance", "Security"]'
                />
                <p class="text-xs text-muted-foreground">Format: ["Service1", "Service2"]</p>
              </div>

              <div class="space-y-2">
                <Label for="hoaFee">Frais HOA</Label>
                <Input
                  id="hoaFee"
                  v-model.number="hoaFee"
                  v-bind="hoaFeeAttrs"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                />
              </div>

              <div class="space-y-2">
                <Label for="hoaFeeFrequency">Fréquence frais HOA</Label>
                <Select v-model="form.hoaFeeFrequency">
                  <SelectTrigger>
                    <SelectValue placeholder="Sélectionnez" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="monthly">Mensuel</SelectItem>
                    <SelectItem value="quarterly">Trimestriel</SelectItem>
                    <SelectItem value="annually">Annuel</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </div>

          <!-- Financial & Listing -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Financier et annonce</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="region">Région</Label>
                <Input
                  id="region"
                  v-model="form.region"
                  placeholder="Brooklyn, Manhattan, etc."
                />
              </div>

              <div class="space-y-2">
                <Label for="pricePerSquareFoot">Prix au m²</Label>
                <Input
                  id="pricePerSquareFoot"
                  v-model.number="form.pricePerSquareFoot"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                />
              </div>

              <div class="space-y-2">
                <Label for="dateOnMarket">Date de mise sur le marché</Label>
                <Input
                  id="dateOnMarket"
                  v-model="form.dateOnMarket"
                  type="date"
                />
              </div>
            </div>
          </div>

          <!-- Localisation -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Localisation</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div class="space-y-2">
                <Label for="address">Adresse *</Label>
                <Input
                  id="address"
                  v-model="address"
                  v-bind="addressAttrs"
                  placeholder="Ex: 123 Rue de la République"
                />
                <p v-if="errors.address" class="text-sm text-destructive">{{ errors.address }}</p>
              </div>

              <div class="space-y-2">
                <Label for="city">Ville *</Label>
                <Input
                  id="city"
                  v-model="city"
                  v-bind="cityAttrs"
                  placeholder="Ex: Paris"
                />
                <p v-if="errors.city" class="text-sm text-destructive">{{ errors.city }}</p>
              </div>

              <div class="space-y-2">
                <Label for="country">Pays *</Label>
                <Input
                  id="country"
                  v-model="country"
                  v-bind="countryAttrs"
                  placeholder="Ex: France"
                />
                <p v-if="errors.country" class="text-sm text-destructive">{{ errors.country }}</p>
              </div>
            </div>
          </div>

          <!-- Images -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Images</h2>
            <ImageUpload
              ref="imageUploadRef"
              v-model="form.images"
              :property-id="propertyId || undefined"
            />
          </div>

          <!-- Statut (uniquement en édition) -->
          <div v-if="isEdit" class="space-y-4">
            <h2 class="text-xl font-semibold">Statut</h2>
            
            <div class="space-y-2">
              <Label for="status">Statut</Label>
              <Select v-model="status" v-bind="statusAttrs">
                <SelectTrigger>
                  <SelectValue :placeholder="status ? undefined : 'Sélectionnez un statut'" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem :value="PropertyStatus.AVAILABLE">Disponible</SelectItem>
                  <SelectItem :value="PropertyStatus.SOLD">Vendu</SelectItem>
                  <SelectItem :value="PropertyStatus.RENTED">Loué</SelectItem>
                  <SelectItem :value="PropertyStatus.PENDING">En attente</SelectItem>
                  <SelectItem :value="PropertyStatus.DRAFT">Brouillon</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <!-- Horaires du bureau -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Horaires du bureau</h2>
            <div class="space-y-4">
              <div class="flex items-center gap-2 p-3 bg-muted/50 rounded-lg">
                <Info class="h-4 w-4 text-muted-foreground" />
                <p class="text-sm text-muted-foreground">
                  <span v-if="!officeHours || officeHours.trim() === ''">
                    Les horaires seront automatiquement hérités de l'organisation lors de la création.
                  </span>
                  <span v-else>
                    Horaires personnalisés pour cette propriété. Vous pouvez les modifier ci-dessous.
                  </span>
                </p>
              </div>
              <div class="space-y-2">
                <Label for="officeHours">Horaires (JSON)</Label>
                <Textarea
                  id="officeHours"
                  v-model="officeHours"
                  v-bind="officeHoursAttrs"
                  placeholder='{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}'
                  rows="4"
                  class="font-mono text-sm"
                />
                <p class="text-xs text-muted-foreground">
                  Format JSON: {"jour": "HH:mm-HH:mm" ou "closed"}. Laissez vide pour utiliser les horaires par défaut de l'organisation.
                </p>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex justify-end gap-4 pt-4 border-t">
            <Button type="button" variant="outline" @click="goBack">
              Annuler
            </Button>
            <Button type="submit" :disabled="submitting">
              <Loader2 v-if="submitting" class="mr-2 h-4 w-4 animate-spin" />
              {{ isEdit ? 'Enregistrer' : 'Créer' }}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { ArrowLeft, Loader2, Info } from 'lucide-vue-next'
import { propertyService, documentService, type PropertyCreate, type PropertyUpdate, PropertyType, PropertyStatus } from '@viridial/shared'
import ImageUpload from '@/components/properties/ImageUpload.vue'
import { propertySchema, type PropertyFormData } from '@/schemas/property.schema'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()

const propertyId = computed(() => {
  const id = route.params.id
  return id && id !== 'new' ? Number(id) : null
})

const isEdit = computed(() => !!propertyId.value)
const loading = ref(false)
const submitting = ref(false)

const { handleSubmit, defineField, errors, setValues, values } = useForm<PropertyFormData>({
  validationSchema: toTypedSchema(propertySchema),
  initialValues: {
    title: '',
    description: '',
    price: 0,
    address: '',
    city: '',
    country: '',
    propertyType: PropertyType.APARTMENT,
    transactionType: undefined,
    bedrooms: undefined,
    bathrooms: undefined,
    area: undefined,
    images: [],
    status: PropertyStatus.DRAFT,
    fullBathrooms: undefined,
    appliancesIncluded: undefined,
    laundryLocation: undefined,
    totalStructureArea: undefined,
    totalInteriorLivableArea: undefined,
    virtualTourUrl: undefined,
    parkingFeatures: undefined,
    hasGarage: false,
    accessibilityFeatures: undefined,
    patioPorch: undefined,
    exteriorFeatures: undefined,
    specialConditions: undefined,
    homeType: undefined,
    propertySubtype: undefined,
    condition: undefined,
    yearBuilt: undefined,
    subdivision: undefined,
    hasHOA: false,
    hoaAmenities: undefined,
    hoaServices: undefined,
    hoaFee: undefined,
    hoaFeeFrequency: undefined,
    region: undefined,
    pricePerSquareFoot: undefined,
    dateOnMarket: undefined,
    // Zillow-inspired fields
    petFriendly: false,
    specialOffer: undefined,
    officeHours: undefined,
    neighborhood: undefined,
    walkScore: undefined,
    transitScore: undefined,
    bikeScore: undefined,
    buildingName: undefined,
    flooring: undefined,
    unitFeatures: undefined,
    buildingAmenities: undefined,
    availableUnits: undefined,
    petPolicy: undefined,
    parkingPolicy: undefined
  }
})

// Define fields with v-model support
const [title, titleAttrs] = defineField('title')
const [description, descriptionAttrs] = defineField('description')
const [price, priceAttrs] = defineField('price')
const [address, addressAttrs] = defineField('address')
const [city, cityAttrs] = defineField('city')
const [country, countryAttrs] = defineField('country')
const [propertyType, propertyTypeAttrs] = defineField('propertyType')
const [bedrooms, bedroomsAttrs] = defineField('bedrooms')
const [bathrooms, bathroomsAttrs] = defineField('bathrooms')
const [area, areaAttrs] = defineField('area')
const [status, statusAttrs] = defineField('status')
const [fullBathrooms, fullBathroomsAttrs] = defineField('fullBathrooms')
const [totalStructureArea, totalStructureAreaAttrs] = defineField('totalStructureArea')
const [totalInteriorLivableArea, totalInteriorLivableAreaAttrs] = defineField('totalInteriorLivableArea')
// Ces champs sont utilisés via form.fieldName dans le template
const [_parkingFeatures] = defineField('parkingFeatures')
const [_hasGarage] = defineField('hasGarage')
const [_accessibilityFeatures] = defineField('accessibilityFeatures')
const [_patioPorch] = defineField('patioPorch')
const [_exteriorFeatures] = defineField('exteriorFeatures')
const [_specialConditions] = defineField('specialConditions')
const [_homeType] = defineField('homeType')
const [_propertySubtype] = defineField('propertySubtype')
const [_condition] = defineField('condition')
const [yearBuilt, yearBuiltAttrs] = defineField('yearBuilt')
const [_subdivision] = defineField('subdivision')
const [_hasHOA] = defineField('hasHOA')
const [_hoaAmenities] = defineField('hoaAmenities')
const [_hoaServices] = defineField('hoaServices')
const [hoaFee, hoaFeeAttrs] = defineField('hoaFee')
const [_hoaFeeFrequency] = defineField('hoaFeeFrequency')
const [_region] = defineField('region')
const [_pricePerSquareFoot] = defineField('pricePerSquareFoot')
const [_dateOnMarket] = defineField('dateOnMarket')
const [transactionType, transactionTypeAttrs] = defineField('transactionType')
// Zillow-inspired fields
const [_petFriendly] = defineField('petFriendly')
const [_specialOffer] = defineField('specialOffer')
const [officeHours, officeHoursAttrs] = defineField('officeHours')
const [_neighborhood] = defineField('neighborhood')
const [_walkScore] = defineField('walkScore')
const [_transitScore] = defineField('transitScore')
const [_bikeScore] = defineField('bikeScore')
const [_buildingName] = defineField('buildingName')
const [_flooring] = defineField('flooring')
const [_unitFeatures] = defineField('unitFeatures')
const [_buildingAmenities] = defineField('buildingAmenities')
const [_availableUnits] = defineField('availableUnits')
const [_petPolicy] = defineField('petPolicy')
const [_parkingPolicy] = defineField('parkingPolicy')

// Keep form as computed for backward compatibility with template
const form = computed(() => values)

const imageUploadRef = ref<InstanceType<typeof ImageUpload>>()

onMounted(async () => {
  if (isEdit.value && propertyId.value) {
    await loadProperty()
  }
})

const loadProperty = async () => {
  if (!propertyId.value) return
  loading.value = true
  try {
    const property = await propertyService.getById(propertyId.value)
    
    // Charger les documents (images) associés à la propriété
    let imageUrls: string[] = []
    try {
      const documents = await documentService.getByPropertyId(propertyId.value)
      imageUrls = documents
        .filter((doc: any) => doc.mimeType?.startsWith('image/'))
        .map((doc: any) => documentService.getViewUrl(doc.id))
    } catch (error) {
      console.warn('Could not load property documents:', error)
      // Utiliser les images du property si disponibles
      imageUrls = property.images || []
    }
    
    setValues({
      title: property.title || '',
      description: property.description || '',
      price: property.price || 0,
      address: property.address || '',
      city: property.city || '',
      country: property.country || '',
      propertyType: property.propertyType || PropertyType.APARTMENT,
      transactionType: (property as any).transactionType || undefined,
      bedrooms: property.bedrooms,
      bathrooms: property.bathrooms,
      area: property.area,
      images: imageUrls.length > 0 ? imageUrls : (property.images || []),
      status: property.status || PropertyStatus.DRAFT,
      fullBathrooms: (property as any).fullBathrooms,
      appliancesIncluded: (property as any).appliancesIncluded,
      laundryLocation: (property as any).laundryLocation,
      totalStructureArea: (property as any).totalStructureArea,
      totalInteriorLivableArea: (property as any).totalInteriorLivableArea,
      virtualTourUrl: (property as any).virtualTourUrl,
      parkingFeatures: (property as any).parkingFeatures,
      hasGarage: (property as any).hasGarage || false,
      accessibilityFeatures: (property as any).accessibilityFeatures,
      patioPorch: (property as any).patioPorch,
      exteriorFeatures: (property as any).exteriorFeatures,
      specialConditions: (property as any).specialConditions,
      homeType: (property as any).homeType,
      propertySubtype: (property as any).propertySubtype,
      condition: (property as any).condition,
      yearBuilt: (property as any).yearBuilt,
      subdivision: (property as any).subdivision,
      hasHOA: (property as any).hasHOA || false,
      hoaAmenities: (property as any).hoaAmenities,
      hoaServices: (property as any).hoaServices,
      hoaFee: (property as any).hoaFee,
      hoaFeeFrequency: (property as any).hoaFeeFrequency,
      region: (property as any).region,
      pricePerSquareFoot: (property as any).pricePerSquareFoot,
      dateOnMarket: (property as any).dateOnMarket ? new Date((property as any).dateOnMarket).toISOString().split('T')[0] : undefined,
      // Zillow-inspired fields
      petFriendly: (property as any).petFriendly || false,
      specialOffer: (property as any).specialOffer,
      officeHours: (property as any).officeHours,
      neighborhood: (property as any).neighborhood,
      walkScore: (property as any).walkScore,
      transitScore: (property as any).transitScore,
      bikeScore: (property as any).bikeScore,
      buildingName: (property as any).buildingName,
      flooring: (property as any).flooring,
      unitFeatures: (property as any).unitFeatures,
      buildingAmenities: (property as any).buildingAmenities,
      availableUnits: (property as any).availableUnits,
      petPolicy: (property as any).petPolicy,
      parkingPolicy: (property as any).parkingPolicy
    })
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

const onSubmit = handleSubmit(async (formData) => {

  submitting.value = true
  try {
    if (isEdit.value && propertyId.value) {
      const updateData: PropertyUpdate & any = {
        title: formData.title,
        description: formData.description,
        price: formData.price,
        address: formData.address,
        city: formData.city,
        country: formData.country,
        propertyType: formData.propertyType,
        transactionType: formData.transactionType,
        bedrooms: formData.bedrooms,
        bathrooms: formData.bathrooms,
        area: formData.area,
        images: formData.images || [],
        status: formData.status,
        fullBathrooms: formData.fullBathrooms,
        appliancesIncluded: formData.appliancesIncluded,
        laundryLocation: formData.laundryLocation,
        totalStructureArea: formData.totalStructureArea,
        totalInteriorLivableArea: formData.totalInteriorLivableArea,
        virtualTourUrl: formData.virtualTourUrl,
        parkingFeatures: formData.parkingFeatures,
        hasGarage: formData.hasGarage,
        accessibilityFeatures: formData.accessibilityFeatures,
        patioPorch: formData.patioPorch,
        exteriorFeatures: formData.exteriorFeatures,
        specialConditions: formData.specialConditions,
        homeType: formData.homeType,
        propertySubtype: formData.propertySubtype,
        condition: formData.condition,
        yearBuilt: formData.yearBuilt,
        subdivision: formData.subdivision,
        hasHOA: formData.hasHOA,
        hoaAmenities: formData.hoaAmenities,
        hoaServices: formData.hoaServices,
        hoaFee: formData.hoaFee,
        hoaFeeFrequency: formData.hoaFeeFrequency,
        region: formData.region,
        pricePerSquareFoot: formData.pricePerSquareFoot,
        dateOnMarket: formData.dateOnMarket,
        // Zillow-inspired fields
        petFriendly: formData.petFriendly,
        specialOffer: formData.specialOffer,
        officeHours: formData.officeHours,
        neighborhood: formData.neighborhood,
        walkScore: formData.walkScore,
        transitScore: formData.transitScore,
        bikeScore: formData.bikeScore,
        buildingName: formData.buildingName,
        flooring: formData.flooring,
        unitFeatures: formData.unitFeatures,
        buildingAmenities: formData.buildingAmenities,
        availableUnits: formData.availableUnits,
        petPolicy: formData.petPolicy,
        parkingPolicy: formData.parkingPolicy
      }
      await propertyService.update(propertyId.value, updateData)
      toast({
        title: 'Propriété modifiée',
        description: 'Les modifications ont été enregistrées avec succès'
      })
      router.push(`/properties/${propertyId.value}`)
    } else {
      const createData: PropertyCreate & any = {
        title: formData.title,
        description: formData.description,
        price: formData.price,
        address: formData.address,
        city: formData.city,
        country: formData.country,
        propertyType: formData.propertyType,
        transactionType: formData.transactionType,
        bedrooms: formData.bedrooms,
        bathrooms: formData.bathrooms,
        area: formData.area,
        images: formData.images || [],
        fullBathrooms: formData.fullBathrooms,
        appliancesIncluded: formData.appliancesIncluded,
        laundryLocation: formData.laundryLocation,
        totalStructureArea: formData.totalStructureArea,
        totalInteriorLivableArea: formData.totalInteriorLivableArea,
        virtualTourUrl: formData.virtualTourUrl,
        parkingFeatures: formData.parkingFeatures,
        hasGarage: formData.hasGarage,
        accessibilityFeatures: formData.accessibilityFeatures,
        patioPorch: formData.patioPorch,
        exteriorFeatures: formData.exteriorFeatures,
        specialConditions: formData.specialConditions,
        homeType: formData.homeType,
        propertySubtype: formData.propertySubtype,
        condition: formData.condition,
        yearBuilt: formData.yearBuilt,
        subdivision: formData.subdivision,
        hasHOA: formData.hasHOA,
        hoaAmenities: formData.hoaAmenities,
        hoaServices: formData.hoaServices,
        hoaFee: formData.hoaFee,
        hoaFeeFrequency: formData.hoaFeeFrequency,
        region: formData.region,
        pricePerSquareFoot: formData.pricePerSquareFoot,
        dateOnMarket: formData.dateOnMarket,
        // Zillow-inspired fields
        petFriendly: formData.petFriendly,
        specialOffer: formData.specialOffer,
        officeHours: formData.officeHours,
        neighborhood: formData.neighborhood,
        walkScore: formData.walkScore,
        transitScore: formData.transitScore,
        bikeScore: formData.bikeScore,
        buildingName: formData.buildingName,
        flooring: formData.flooring,
        unitFeatures: formData.unitFeatures,
        buildingAmenities: formData.buildingAmenities,
        availableUnits: formData.availableUnits,
        petPolicy: formData.petPolicy,
        parkingPolicy: formData.parkingPolicy
      }
      const newProperty = await propertyService.create(createData)
      
      // Uploader les images en attente si le composant ImageUpload a des images non uploadées
      if (imageUploadRef.value && newProperty.id) {
        try {
          await imageUploadRef.value.uploadPendingImages(newProperty.id)
        } catch (error: any) {
          console.error('Error uploading pending images:', error)
          toast({
            title: 'Avertissement',
            description: 'La propriété a été créée mais certaines images n\'ont pas pu être uploadées',
            variant: 'default'
          })
        }
      }
      
      toast({
        title: 'Propriété créée',
        description: 'La propriété a été créée avec succès'
      })
      router.push(`/properties/${newProperty.id}`)
    }
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue lors de l\'enregistrement',
      variant: 'destructive'
    })
  } finally {
    submitting.value = false
  }
})

const goBack = () => {
  router.push('/properties')
}
</script>

