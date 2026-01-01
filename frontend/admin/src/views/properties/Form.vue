<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="icon" @click="goBack">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-3xl font-bold">{{ isEdit ? 'Modifier la propriété' : 'Nouvelle propriété' }}</h1>
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
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Informations principales -->
          <div class="space-y-4">
            <h2 class="text-xl font-semibold">Informations principales</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label for="title">Titre *</Label>
                <Input
                  id="title"
                  v-model="form.title"
                  placeholder="Ex: Appartement 3 pièces centre-ville"
                  required
                />
                <p v-if="errors.title" class="text-sm text-destructive">{{ errors.title }}</p>
              </div>

              <div class="space-y-2">
                <Label for="propertyType">Type de propriété *</Label>
                <Select v-model="form.propertyType" required>
                  <SelectTrigger>
                    <SelectValue :placeholder="form.propertyType ? undefined : 'Sélectionnez un type'" />
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
            </div>

            <div class="space-y-2">
              <Label for="description">Description *</Label>
              <textarea
                id="description"
                v-model="form.description"
                placeholder="Description détaillée de la propriété..."
                rows="4"
                class="flex min-h-[100px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                required
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
                  v-model.number="form.price"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                  required
                />
                <p v-if="errors.price" class="text-sm text-destructive">{{ errors.price }}</p>
              </div>

              <div class="space-y-2">
                <Label for="bedrooms">Chambres</Label>
                <Input
                  id="bedrooms"
                  v-model.number="form.bedrooms"
                  type="number"
                  min="0"
                  placeholder="0"
                />
              </div>

              <div class="space-y-2">
                <Label for="bathrooms">Salles de bain</Label>
                <Input
                  id="bathrooms"
                  v-model.number="form.bathrooms"
                  type="number"
                  min="0"
                  step="0.5"
                  placeholder="0"
                />
              </div>

              <div class="space-y-2">
                <Label for="area">Surface (m²)</Label>
                <Input
                  id="area"
                  v-model.number="form.area"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0"
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
                  v-model="form.address"
                  placeholder="Ex: 123 Rue de la République"
                  required
                />
                <p v-if="errors.address" class="text-sm text-destructive">{{ errors.address }}</p>
              </div>

              <div class="space-y-2">
                <Label for="city">Ville *</Label>
                <Input
                  id="city"
                  v-model="form.city"
                  placeholder="Ex: Paris"
                  required
                />
                <p v-if="errors.city" class="text-sm text-destructive">{{ errors.city }}</p>
              </div>

              <div class="space-y-2">
                <Label for="country">Pays *</Label>
                <Input
                  id="country"
                  v-model="form.country"
                  placeholder="Ex: France"
                  required
                />
                <p v-if="errors.country" class="text-sm text-destructive">{{ errors.country }}</p>
              </div>
            </div>
          </div>

          <!-- Statut (uniquement en édition) -->
          <div v-if="isEdit" class="space-y-4">
            <h2 class="text-xl font-semibold">Statut</h2>
            
            <div class="space-y-2">
              <Label for="status">Statut</Label>
              <Select v-model="form.status">
                <SelectTrigger>
                  <SelectValue :placeholder="form.status ? undefined : 'Sélectionnez un statut'" />
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
import { useToast } from '@/components/ui/toast'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { ArrowLeft, Loader2 } from 'lucide-vue-next'
import { propertyService, type Property, type PropertyCreate, type PropertyUpdate, PropertyType, PropertyStatus } from '@viridial/shared'

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

const form = ref<PropertyCreate & { status?: PropertyStatus }>({
  title: '',
  description: '',
  price: 0,
  address: '',
  city: '',
  country: '',
  propertyType: PropertyType.APARTMENT,
  bedrooms: undefined,
  bathrooms: undefined,
  area: undefined,
  images: [],
  status: PropertyStatus.DRAFT
})

const errors = ref<Record<string, string>>({})

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
    form.value = {
      title: property.title || '',
      description: property.description || '',
      price: property.price || 0,
      address: property.address || '',
      city: property.city || '',
      country: property.country || '',
      propertyType: property.propertyType || PropertyType.APARTMENT,
      bedrooms: property.bedrooms,
      bathrooms: property.bathrooms,
      area: property.area,
      images: property.images || [],
      status: property.status || PropertyStatus.DRAFT
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

const validate = (): boolean => {
  errors.value = {}
  
  if (!form.value.title || form.value.title.trim().length === 0) {
    errors.value.title = 'Le titre est requis'
  }
  
  if (!form.value.description || form.value.description.trim().length === 0) {
    errors.value.description = 'La description est requise'
  }
  
  if (!form.value.price || form.value.price <= 0) {
    errors.value.price = 'Le prix doit être supérieur à 0'
  }
  
  if (!form.value.address || form.value.address.trim().length === 0) {
    errors.value.address = 'L\'adresse est requise'
  }
  
  if (!form.value.city || form.value.city.trim().length === 0) {
    errors.value.city = 'La ville est requise'
  }
  
  if (!form.value.country || form.value.country.trim().length === 0) {
    errors.value.country = 'Le pays est requis'
  }
  
  return Object.keys(errors.value).length === 0
}

const handleSubmit = async () => {
  if (!validate()) {
    toast({
      title: 'Erreur de validation',
      description: 'Veuillez corriger les erreurs dans le formulaire',
      variant: 'destructive'
    })
    return
  }

  submitting.value = true
  try {
    if (isEdit.value && propertyId.value) {
      const updateData: PropertyUpdate = {
        title: form.value.title,
        description: form.value.description,
        price: form.value.price,
        address: form.value.address,
        city: form.value.city,
        country: form.value.country,
        propertyType: form.value.propertyType,
        bedrooms: form.value.bedrooms,
        bathrooms: form.value.bathrooms,
        area: form.value.area,
        images: form.value.images,
        status: form.value.status
      }
      await propertyService.update(propertyId.value, updateData)
      toast({
        title: 'Propriété modifiée',
        description: 'Les modifications ont été enregistrées avec succès'
      })
      router.push(`/properties/${propertyId.value}`)
    } else {
      const createData: PropertyCreate = {
        title: form.value.title,
        description: form.value.description,
        price: form.value.price,
        address: form.value.address,
        city: form.value.city,
        country: form.value.country,
        propertyType: form.value.propertyType,
        bedrooms: form.value.bedrooms,
        bathrooms: form.value.bathrooms,
        area: form.value.area,
        images: form.value.images
      }
      const newProperty = await propertyService.create(createData)
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
}

const goBack = () => {
  router.push('/properties')
}
</script>

