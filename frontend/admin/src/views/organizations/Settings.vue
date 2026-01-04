<template>
  <div class="space-y-6" v-if="organization">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center space-x-4">
        <Button variant="ghost" size="icon" @click="$router.push(`/organizations/${organizationId}`)">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">Paramètres de l'organisation</h1>
          <p class="text-muted-foreground mt-1">{{ organization.name }}</p>
        </div>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>

    <div v-else class="space-y-6">
      <!-- Logo -->
      <Card>
        <CardHeader>
          <CardTitle>Logo de l'organisation</CardTitle>
          <CardDescription>Ajoutez un logo pour personnaliser votre organisation</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="flex items-center space-x-4">
            <div class="h-24 w-24 rounded-lg border-2 border-dashed border-muted-foreground/25 flex items-center justify-center overflow-hidden bg-muted/50">
              <img
                v-if="form.logoUrl"
                :src="form.logoUrl"
                alt="Logo"
                class="h-full w-full object-cover"
              />
              <Image v-else class="h-8 w-8 text-muted-foreground" />
            </div>
            <div class="space-y-2">
              <Button variant="outline" @click="openLogoDialog">
                <Upload class="mr-2 h-4 w-4" />
                {{ form.logoUrl ? 'Changer le logo' : 'Ajouter un logo' }}
              </Button>
              <p class="text-sm text-muted-foreground">
                Formats acceptés: JPG, PNG, SVG (max 2MB)
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Adresse -->
      <Card>
        <CardHeader>
          <CardTitle>Adresse</CardTitle>
          <CardDescription>Informations d'adresse de l'organisation</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label for="address">Adresse</Label>
              <Input
                id="address"
                v-model="form.address"
                placeholder="123 Rue de la République"
              />
            </div>
            <div class="space-y-2">
              <Label for="city">Ville</Label>
              <Input
                id="city"
                v-model="form.city"
                placeholder="Paris"
              />
            </div>
            <div class="space-y-2">
              <Label for="postalCode">Code postal</Label>
              <Input
                id="postalCode"
                v-model="form.postalCode"
                placeholder="75001"
              />
            </div>
            <div class="space-y-2">
              <Label for="country">Pays</Label>
              <Input
                id="country"
                v-model="form.country"
                placeholder="France"
              />
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Contact -->
      <Card>
        <CardHeader>
          <CardTitle>Contact</CardTitle>
          <CardDescription>Informations de contact de l'organisation</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label for="phone">Téléphone</Label>
              <Input
                id="phone"
                v-model="form.phone"
                placeholder="+33 1 23 45 67 89"
                type="tel"
              />
            </div>
            <div class="space-y-2">
              <Label for="email">Email</Label>
              <Input
                id="email"
                v-model="form.email"
                placeholder="contact@example.com"
                type="email"
              />
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Domaines personnalisés -->
      <Card>
        <CardHeader>
          <CardTitle>Domaines personnalisés</CardTitle>
          <CardDescription>Ajoutez des domaines personnalisés pour votre organisation</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="space-y-2">
            <Label for="customDomains">Domaines (JSON array)</Label>
            <Textarea
              id="customDomains"
              v-model="form.customDomains"
              placeholder='["example.com", "www.example.com"]'
              rows="3"
            />
            <p class="text-sm text-muted-foreground">
              Entrez un tableau JSON de domaines, par exemple: ["example.com", "www.example.com"]
            </p>
          </div>
        </CardContent>
      </Card>

      <!-- Horaires du bureau par défaut -->
      <Card>
        <CardHeader>
          <CardTitle>Horaires du bureau par défaut</CardTitle>
          <CardDescription>
            Définissez les horaires par défaut qui seront automatiquement appliqués aux nouvelles propriétés.
            Ces horaires peuvent être personnalisés pour chaque propriété si nécessaire.
          </CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="space-y-4">
            <div v-for="day in daysOfWeek" :key="day.key" class="flex items-center justify-between gap-4 p-3 border rounded-lg">
              <div class="flex-1">
                <Label :for="`hours-${day.key}`" class="font-medium">{{ day.label }}</Label>
              </div>
              <div class="flex-1 flex items-center gap-2">
                <Input
                  :id="`hours-${day.key}`"
                  v-model="officeHoursData[day.key]"
                  placeholder="9:00-18:00 ou closed"
                  class="flex-1"
                />
                <Button
                  v-if="officeHoursData[day.key] && officeHoursData[day.key] !== 'closed'"
                  variant="ghost"
                  size="icon"
                  @click="officeHoursData[day.key] = 'closed'"
                  title="Fermé"
                >
                  <X class="h-4 w-4" />
                </Button>
                <Button
                  v-else-if="officeHoursData[day.key] === 'closed'"
                  variant="ghost"
                  size="icon"
                  @click="officeHoursData[day.key] = '9:00-18:00'"
                  title="Ouvrir"
                >
                  <Check class="h-4 w-4" />
                </Button>
              </div>
            </div>
          </div>
          <div class="flex items-center gap-2 pt-2">
            <Button variant="outline" size="sm" @click="setDefaultHours">
              <Clock class="mr-2 h-4 w-4" />
              Horaires par défaut
            </Button>
            <Button variant="outline" size="sm" @click="copyToAllDays">
              <Copy class="mr-2 h-4 w-4" />
              Copier sur tous les jours
            </Button>
          </div>
          <p class="text-sm text-muted-foreground">
            Format: "HH:mm-HH:mm" (ex: 9:00-18:00) ou "closed" pour fermé. 
            Les nouvelles propriétés hériteront automatiquement de ces horaires.
          </p>
        </CardContent>
      </Card>

      <!-- Quotas -->
      <Card>
        <CardHeader>
          <CardTitle>Quotas</CardTitle>
          <CardDescription>Configurez les limites de ressources pour cette organisation</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="space-y-2">
              <Label for="maxProperties">Propriétés max</Label>
              <Input
                id="maxProperties"
                v-model.number="quotasData.max_properties"
                type="number"
                min="0"
                placeholder="100"
              />
            </div>
            <div class="space-y-2">
              <Label for="maxUsers">Utilisateurs max</Label>
              <Input
                id="maxUsers"
                v-model.number="quotasData.max_users"
                type="number"
                min="0"
                placeholder="10"
              />
            </div>
            <div class="space-y-2">
              <Label for="maxStorage">Stockage max (GB)</Label>
              <Input
                id="maxStorage"
                v-model.number="quotasData.max_storage_gb"
                type="number"
                min="0"
                placeholder="50"
              />
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Actions -->
      <div class="flex justify-end space-x-2">
        <Button variant="outline" @click="resetForm">
          Annuler
        </Button>
        <Button @click="saveSettings" :disabled="saving">
          <Loader2 v-if="saving" class="mr-2 h-4 w-4 animate-spin" />
          <Save v-else class="mr-2 h-4 w-4" />
          Enregistrer
        </Button>
      </div>
    </div>

    <!-- Dialog pour upload logo -->
    <Dialog :open="logoDialogOpen" @update:open="logoDialogOpen = $event">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Changer le logo</DialogTitle>
          <DialogDescription>
            Entrez l'URL du logo ou uploadez un fichier
          </DialogDescription>
        </DialogHeader>
        <div class="space-y-4">
          <div class="space-y-2">
            <Label for="logoUrl">URL du logo</Label>
            <Input
              id="logoUrl"
              v-model="logoUrlInput"
              placeholder="https://example.com/logo.png"
            />
          </div>
          <div class="space-y-2">
            <Label for="logoFile">Ou uploader un fichier</Label>
            <Input
              id="logoFile"
              type="file"
              accept="image/*"
              @change="handleLogoFileChange"
              :disabled="uploadingLogo"
            />
            <p class="text-sm text-muted-foreground">
              Formats acceptés: JPG, PNG, SVG (max 2MB)
            </p>
            <div v-if="selectedLogoFile" class="mt-2 p-2 bg-muted rounded-lg">
              <p class="text-sm font-medium">{{ selectedLogoFile.name }}</p>
              <p class="text-xs text-muted-foreground">
                {{ (selectedLogoFile.size / 1024).toFixed(2) }} KB
              </p>
            </div>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="logoDialogOpen = false" :disabled="uploadingLogo">
            Annuler
          </Button>
          <Button @click="saveLogo" :disabled="uploadingLogo || (!logoUrlInput && !selectedLogoFile)">
            <Loader2 v-if="uploadingLogo" class="mr-2 h-4 w-4 animate-spin" />
            {{ uploadingLogo ? 'Upload en cours...' : 'Enregistrer' }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { organizationService, documentService, useAuthStore, type Organization, type OrganizationSettings } from '@viridial/shared'
import { env } from '@/config/env'
import { ArrowLeft, Loader2, Upload, Image, Save, Clock, Copy, X, Check } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

const route = useRoute()
const { toast } = useToast()
const authStore = useAuthStore()

const organizationId = computed(() => Number(route.params.id))
const organization = ref<Organization | null>(null)
const loading = ref(true)
const saving = ref(false)
const logoDialogOpen = ref(false)
const logoUrlInput = ref('')
const uploadingLogo = ref(false)
const selectedLogoFile = ref<File | null>(null)

const form = ref<OrganizationSettings>({
  logoUrl: '',
  address: '',
  city: '',
  postalCode: '',
  country: '',
  phone: '',
  email: '',
  customDomains: '',
  quotas: '',
  defaultOfficeHours: ''
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

const officeHoursData = ref<Record<string, string>>({
  monday: '9:00-18:00',
  tuesday: '9:00-18:00',
  wednesday: '9:00-18:00',
  thursday: '9:00-18:00',
  friday: '9:00-18:00',
  saturday: '10:00-16:00',
  sunday: 'closed'
})

// Charger les horaires depuis les settings
watch(() => form.value.defaultOfficeHours, (newValue) => {
  if (newValue) {
    try {
      const parsed = JSON.parse(newValue)
      officeHoursData.value = { ...officeHoursData.value, ...parsed }
    } catch (e) {
      console.warn('Error parsing office hours:', e)
    }
  }
}, { immediate: true })

const quotasData = computed({
  get: () => {
    try {
      return form.value.quotas ? JSON.parse(form.value.quotas) : { max_properties: null, max_users: null, max_storage_gb: null }
    } catch {
      return { max_properties: null, max_users: null, max_storage_gb: null }
    }
  },
  set: (value) => {
    form.value.quotas = JSON.stringify(value)
  }
})

const loadOrganization = async () => {
  try {
    loading.value = true
    organization.value = await organizationService.getById(organizationId.value)
    const settings = await organizationService.getSettings(organizationId.value)
    
    form.value = {
      logoUrl: settings.logoUrl || '',
      address: settings.address || '',
      city: settings.city || '',
      postalCode: settings.postalCode || '',
      country: settings.country || '',
      phone: settings.phone || '',
      email: settings.email || '',
      customDomains: settings.customDomains || '',
      quotas: settings.quotas || '{"max_properties": null, "max_users": null, "max_storage_gb": null}',
      defaultOfficeHours: settings.defaultOfficeHours || ''
    }
    
    // Charger les horaires dans officeHoursData
    if (form.value.defaultOfficeHours) {
      try {
        const parsed = JSON.parse(form.value.defaultOfficeHours)
        officeHoursData.value = { ...officeHoursData.value, ...parsed }
      } catch (e) {
        console.warn('Error parsing office hours:', e)
      }
    }
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.response?.data?.message || 'Impossible de charger les paramètres',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const setDefaultHours = () => {
  officeHoursData.value = {
    monday: '9:00-18:00',
    tuesday: '9:00-18:00',
    wednesday: '9:00-18:00',
    thursday: '9:00-18:00',
    friday: '9:00-18:00',
    saturday: '10:00-16:00',
    sunday: 'closed'
  }
}

const copyToAllDays = () => {
  const firstDayValue = officeHoursData.value.monday
  daysOfWeek.forEach(day => {
    officeHoursData.value[day.key] = firstDayValue
  })
}

const saveSettings = async () => {
  try {
    saving.value = true
    
    // Valider le JSON des domaines personnalisés
    if (form.value.customDomains) {
      try {
        JSON.parse(form.value.customDomains)
      } catch {
        toast({
          title: 'Erreur de validation',
          description: 'Le format des domaines personnalisés est invalide. Utilisez un tableau JSON.',
          variant: 'destructive'
        })
        return
      }
    }

    // Valider et sauvegarder les horaires
    form.value.defaultOfficeHours = JSON.stringify(officeHoursData.value)

    await organizationService.updateSettings(organizationId.value, form.value)
    
    toast({
      title: 'Succès',
      description: 'Les paramètres ont été enregistrés avec succès'
    })
    
    await loadOrganization()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.response?.data?.message || 'Impossible d\'enregistrer les paramètres',
      variant: 'destructive'
    })
  } finally {
    saving.value = false
  }
}

const resetForm = async () => {
  await loadOrganization()
}

const openLogoDialog = () => {
  logoUrlInput.value = form.value.logoUrl || ''
  logoDialogOpen.value = true
}

const handleLogoFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    // Valider le type de fichier
    if (!file.type.startsWith('image/')) {
      toast({
        title: 'Type de fichier invalide',
        description: 'Veuillez sélectionner un fichier image (JPG, PNG, SVG, etc.)',
        variant: 'destructive'
      })
      return
    }
    
    // Valider la taille (max 2MB pour les logos)
    if (file.size > 2 * 1024 * 1024) {
      toast({
        title: 'Fichier trop volumineux',
        description: 'La taille maximale autorisée est de 2MB',
        variant: 'destructive'
      })
      return
    }
    
    selectedLogoFile.value = file
    
    // Afficher un aperçu
    const reader = new FileReader()
    reader.onload = (e) => {
      logoUrlInput.value = e.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

const uploadLogo = async () => {
  if (!selectedLogoFile.value || !authStore.user?.id) {
    toast({
      title: 'Erreur',
      description: 'Fichier ou utilisateur non disponible',
      variant: 'destructive'
    })
    return
  }

  try {
    uploadingLogo.value = true
    
    // Upload vers document-service
    const document = await documentService.upload({
      file: selectedLogoFile.value,
      organizationId: organizationId.value,
      createdBy: authStore.user.id,
      description: `Logo de l'organisation ${organization.value?.name || ''}`
    })
    
    // Construire l'URL du document
    // Si le document a déjà une URL, l'utiliser, sinon construire l'URL de download
    let logoUrl = document.url
    if (!logoUrl) {
      // Construire l'URL à partir de l'endpoint de download
      const documentServiceUrl = env.documentServiceUrl || 'http://localhost:8085'
      logoUrl = `${documentServiceUrl}/api/documents/${document.id}/download`
    }
    
    // Mettre à jour le formulaire avec l'URL
    form.value.logoUrl = logoUrl
    
    toast({
      title: 'Logo uploadé',
      description: 'Le logo a été uploadé avec succès'
    })
    
    // Réinitialiser
    selectedLogoFile.value = null
    logoDialogOpen.value = false
  } catch (error: any) {
    console.error('Error uploading logo:', error)
    toast({
      title: 'Erreur lors de l\'upload',
      description: error.response?.data?.message || 'Impossible d\'uploader le logo',
      variant: 'destructive'
    })
  } finally {
    uploadingLogo.value = false
  }
}

const saveLogo = async () => {
  // Si un fichier est sélectionné, uploader d'abord
  if (selectedLogoFile.value) {
    await uploadLogo()
  } else if (logoUrlInput.value) {
    // Sinon, utiliser l'URL directement
    form.value.logoUrl = logoUrlInput.value
    logoDialogOpen.value = false
  }
}

onMounted(() => {
  loadOrganization()
})
</script>

