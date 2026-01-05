<template>
  <div class="space-y-6">
    <!-- Header -->
    <div>
      <h1 class="text-2.5xl font-bold">{{ t('profile.title', 'Mon Profil') }}</h1>
      <p class="text-muted-foreground mt-1">{{ t('profile.description', 'Gérez vos informations personnelles et préférences') }}</p>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>

    <div v-else-if="profile" class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Left Column: Profile Information -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Personal Information -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('profile.personalInfo', 'Informations personnelles') }}</CardTitle>
            <CardDescription>{{ t('profile.personalInfoDesc', 'Mettez à jour vos informations personnelles') }}</CardDescription>
          </CardHeader>
          <CardContent class="space-y-4">
            <div class="flex items-center space-x-4">
              <Avatar class="h-20 w-20">
                <AvatarImage :src="(profile.avatarUrl || profile.avatar) || ''" v-if="profile.avatarUrl || profile.avatar" />
                <AvatarFallback class="text-2xl">
                  {{ (profile.name || profile.email || 'U').charAt(0).toUpperCase() }}
                </AvatarFallback>
              </Avatar>
              <div class="flex-1">
                <div class="text-lg font-semibold">{{ profile.name }}</div>
                <div class="text-sm text-muted-foreground">{{ profile.email }}</div>
                <Button variant="outline" size="sm" class="mt-2" @click="showAvatarDialog = true">
                  <Upload class="mr-2 h-4 w-4" />
                  {{ t('profile.changeAvatar', 'Changer la photo') }}
                </Button>
              </div>
            </div>

            <Separator />

            <form @submit.prevent="updateProfile" class="space-y-4">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                  <Label for="firstName">{{ t('profile.firstName', 'Prénom') }}</Label>
                  <Input
                    id="firstName"
                    v-model="profileForm.firstName"
                    :placeholder="t('profile.firstNamePlaceholder', 'Votre prénom')"
                  />
                </div>
                <div class="space-y-2">
                  <Label for="lastName">{{ t('profile.lastName', 'Nom') }}</Label>
                  <Input
                    id="lastName"
                    v-model="profileForm.lastName"
                    :placeholder="t('profile.lastNamePlaceholder', 'Votre nom')"
                  />
                </div>
              </div>

              <div class="space-y-2">
                <Label for="email">{{ t('profile.email', 'Email') }}</Label>
                <Input
                  id="email"
                  v-model="profileForm.email"
                  type="email"
                  :placeholder="t('profile.emailPlaceholder', 'votre@email.com')"
                />
              </div>

              <div class="flex justify-end gap-2">
                <Button type="button" variant="outline" @click="resetProfileForm">
                  {{ t('common.cancel', 'Annuler') }}
                </Button>
                <Button type="submit" :disabled="saving">
                  <Loader2 v-if="saving" class="mr-2 h-4 w-4 animate-spin" />
                  {{ t('common.save', 'Enregistrer') }}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>

        <!-- Preferences -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('profile.preferences', 'Préférences') }}</CardTitle>
            <CardDescription>{{ t('profile.preferencesDesc', 'Configurez vos préférences d\'utilisation') }}</CardDescription>
          </CardHeader>
          <CardContent class="space-y-4">
            <form @submit.prevent="updatePreferences" class="space-y-4">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                  <Label for="language">{{ t('profile.language', 'Langue') }}</Label>
                  <Select v-model="preferencesForm.language">
                    <SelectTrigger>
                      <SelectValue :placeholder="t('profile.selectLanguage', 'Sélectionner une langue')" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="fr">Français</SelectItem>
                      <SelectItem value="en">English</SelectItem>
                      <SelectItem value="es">Español</SelectItem>
                      <SelectItem value="de">Deutsch</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div class="space-y-2">
                  <Label for="timezone">{{ t('profile.timezone', 'Fuseau horaire') }}</Label>
                  <Select v-model="preferencesForm.timezone">
                    <SelectTrigger>
                      <SelectValue :placeholder="t('profile.selectTimezone', 'Sélectionner un fuseau horaire')" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="Europe/Paris">Europe/Paris (UTC+1)</SelectItem>
                      <SelectItem value="Europe/London">Europe/London (UTC+0)</SelectItem>
                      <SelectItem value="America/New_York">America/New_York (UTC-5)</SelectItem>
                      <SelectItem value="America/Los_Angeles">America/Los_Angeles (UTC-8)</SelectItem>
                      <SelectItem value="Asia/Tokyo">Asia/Tokyo (UTC+9)</SelectItem>
                      <SelectItem value="Australia/Sydney">Australia/Sydney (UTC+10)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <Separator />

              <div class="space-y-4">
                <Label class="text-base font-semibold">{{ t('profile.notifications', 'Notifications') }}</Label>
                <div class="space-y-3">
                  <div class="flex items-center justify-between">
                    <div class="space-y-0.5">
                      <Label for="notifEmail">{{ t('profile.emailNotifications', 'Notifications par email') }}</Label>
                      <p class="text-sm text-muted-foreground">{{ t('profile.emailNotificationsDesc', 'Recevoir des notifications par email') }}</p>
                    </div>
                    <Checkbox
                      id="notifEmail"
                      :checked="notificationPrefs.email"
                      @update:checked="(val: boolean) => notificationPrefs.email = val"
                    />
                  </div>
                  <div class="flex items-center justify-between">
                    <div class="space-y-0.5">
                      <Label for="notifPush">{{ t('profile.pushNotifications', 'Notifications push') }}</Label>
                      <p class="text-sm text-muted-foreground">{{ t('profile.pushNotificationsDesc', 'Recevoir des notifications dans le navigateur') }}</p>
                    </div>
                    <Checkbox
                      id="notifPush"
                      :checked="notificationPrefs.push"
                      @update:checked="(val: boolean) => notificationPrefs.push = val"
                    />
                  </div>
                  <div class="flex items-center justify-between">
                    <div class="space-y-0.5">
                      <Label for="notifSms">{{ t('profile.smsNotifications', 'Notifications SMS') }}</Label>
                      <p class="text-sm text-muted-foreground">{{ t('profile.smsNotificationsDesc', 'Recevoir des notifications par SMS') }}</p>
                    </div>
                    <Checkbox
                      id="notifSms"
                      :checked="notificationPrefs.sms"
                      @update:checked="(val: boolean) => notificationPrefs.sms = val"
                    />
                  </div>
                  <div class="flex items-center justify-between">
                    <div class="space-y-0.5">
                      <Label for="notifMarketing">{{ t('profile.marketingNotifications', 'Notifications marketing') }}</Label>
                      <p class="text-sm text-muted-foreground">{{ t('profile.marketingNotificationsDesc', 'Recevoir des communications marketing') }}</p>
                    </div>
                    <Checkbox
                      id="notifMarketing"
                      :checked="notificationPrefs.marketing"
                      @update:checked="(val: boolean) => notificationPrefs.marketing = val"
                    />
                  </div>
                </div>
              </div>

              <div class="flex justify-end gap-2">
                <Button type="button" variant="outline" @click="resetPreferencesForm">
                  {{ t('common.cancel', 'Annuler') }}
                </Button>
                <Button type="submit" :disabled="savingPreferences">
                  <Loader2 v-if="savingPreferences" class="mr-2 h-4 w-4 animate-spin" />
                  {{ t('common.save', 'Enregistrer') }}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>

      <!-- Right Column: Account Info -->
      <div class="space-y-6">
        <!-- Account Information -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('profile.accountInfo', 'Informations du compte') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div class="space-y-3">
              <div>
                <Label class="text-muted-foreground">{{ t('profile.memberSince', 'Membre depuis') }}</Label>
                <div class="font-medium">{{ formatDate(profile.createdAt) }}</div>
              </div>
              <div>
                <Label class="text-muted-foreground">{{ t('profile.lastLogin', 'Dernière connexion') }}</Label>
                <div class="font-medium">{{ profile.lastLoginAt ? formatDate(profile.lastLoginAt) : t('profile.never', 'Jamais') }}</div>
              </div>
              <div>
                <Label class="text-muted-foreground">{{ t('profile.status', 'Statut') }}</Label>
                <div>
                  <Badge :variant="getStatusVariant(profile.status)">
                    {{ getStatusLabel(profile.status) }}
                  </Badge>
                </div>
              </div>
              <div v-if="profile.organizationName">
                <Label class="text-muted-foreground">{{ t('profile.organization', 'Organisation') }}</Label>
                <div class="font-medium">{{ profile.organizationName }}</div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Roles -->
        <Card v-if="profile.roles && profile.roles.length > 0">
          <CardHeader>
            <CardTitle>{{ t('profile.roles', 'Rôles') }}</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="flex flex-wrap gap-2">
              <Badge v-for="role in profile.roles" :key="role" variant="secondary">
                {{ role }}
              </Badge>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>

    <!-- Avatar Upload Dialog -->
    <Dialog v-model:open="showAvatarDialog">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{{ t('profile.changeAvatar', 'Changer la photo de profil') }}</DialogTitle>
          <DialogDescription>{{ t('profile.changeAvatarDesc', 'Téléchargez une nouvelle photo de profil') }}</DialogDescription>
        </DialogHeader>
        <div class="space-y-4">
          <div class="flex justify-center">
            <Avatar class="h-32 w-32">
              <AvatarImage :src="(avatarPreview || profile?.avatarUrl || profile?.avatar) || ''" />
              <AvatarFallback class="text-4xl">
                {{ (profile?.name || profile?.email || 'U').charAt(0).toUpperCase() }}
              </AvatarFallback>
            </Avatar>
          </div>
          <div class="space-y-2">
            <Label for="avatar">{{ t('profile.selectImage', 'Sélectionner une image') }}</Label>
            <Input
              id="avatar"
              type="file"
              accept="image/*"
              @change="handleAvatarChange"
            />
            <p class="text-sm text-muted-foreground">{{ t('profile.imageHint', 'Formats acceptés: JPG, PNG, GIF. Taille max: 5MB') }}</p>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="showAvatarDialog = false">
            {{ t('common.cancel', 'Annuler') }}
          </Button>
          <Button @click="uploadAvatar" :disabled="!avatarFile || uploadingAvatar">
            <Loader2 v-if="uploadingAvatar" class="mr-2 h-4 w-4 animate-spin" />
            {{ t('common.upload', 'Télécharger') }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from '@/components/ui/toast'
import { userService, type UserProfile, UserStatus } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Checkbox } from '@/components/ui/checkbox'
import { Separator } from '@/components/ui/separator'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Loader2, Upload } from 'lucide-vue-next'

const { t } = useI18n()
const { toast } = useToast()

const profile = ref<UserProfile | null>(null)
const loading = ref(false)
const saving = ref(false)
const savingPreferences = ref(false)
const showAvatarDialog = ref(false)
const avatarFile = ref<File | null>(null)
const avatarPreview = ref<string | null>(null)
const uploadingAvatar = ref(false)

const profileForm = ref({
  firstName: '',
  lastName: '',
  email: ''
})

const preferencesForm = ref({
  language: 'fr',
  timezone: 'Europe/Paris'
})

const notificationPrefs = ref({
  email: true,
  push: true,
  sms: false,
  marketing: false
})

const loadProfile = async () => {
  loading.value = true
  try {
    profile.value = await userService.getProfile()
    
    // Initialiser les formulaires
    profileForm.value = {
      firstName: profile.value.firstName || '',
      lastName: profile.value.lastName || '',
      email: profile.value.email || ''
    }
    
    preferencesForm.value = {
      language: profile.value.language || 'fr',
      timezone: profile.value.timezone || 'Europe/Paris'
    }
    
    // Initialiser les préférences de notifications
    if (profile.value.notificationPreferences) {
      if (typeof profile.value.notificationPreferences === 'object') {
        notificationPrefs.value = {
          email: profile.value.notificationPreferences.email ?? true,
          push: profile.value.notificationPreferences.push ?? true,
          sms: profile.value.notificationPreferences.sms ?? false,
          marketing: profile.value.notificationPreferences.marketing ?? false
        }
      }
    }
  } catch (error: any) {
    toast({
      title: t('common.error', 'Erreur'),
      description: error.message || t('profile.loadError', 'Impossible de charger le profil'),
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const updateProfile = async () => {
  saving.value = true
  try {
    profile.value = await userService.updateProfile({
      firstName: profileForm.value.firstName,
      lastName: profileForm.value.lastName,
      email: profileForm.value.email
    })
    
    toast({
      title: t('common.success', 'Succès'),
      description: t('profile.updateSuccess', 'Profil mis à jour avec succès')
    })
  } catch (error: any) {
    toast({
      title: t('common.error', 'Erreur'),
      description: error.message || t('profile.updateError', 'Impossible de mettre à jour le profil'),
      variant: 'destructive'
    })
  } finally {
    saving.value = false
  }
}

const updatePreferences = async () => {
  savingPreferences.value = true
  try {
    profile.value = await userService.updatePreferences({
      language: preferencesForm.value.language,
      timezone: preferencesForm.value.timezone,
      notificationPreferences: notificationPrefs.value
    })
    
    toast({
      title: t('common.success', 'Succès'),
      description: t('profile.preferencesUpdateSuccess', 'Préférences mises à jour avec succès')
    })
  } catch (error: any) {
    toast({
      title: t('common.error', 'Erreur'),
      description: error.message || t('profile.preferencesUpdateError', 'Impossible de mettre à jour les préférences'),
      variant: 'destructive'
    })
  } finally {
    savingPreferences.value = false
  }
}

const handleAvatarChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    avatarFile.value = target.files[0]
    const reader = new FileReader()
    reader.onload = (e) => {
      avatarPreview.value = e.target?.result as string
    }
    reader.readAsDataURL(avatarFile.value)
  }
}

const uploadAvatar = async () => {
  if (!avatarFile.value) return
  
  uploadingAvatar.value = true
  try {
    // TODO: Implémenter l'upload d'avatar via document-service
    // Pour l'instant, on simule avec une URL
    const avatarUrl = URL.createObjectURL(avatarFile.value)
    
    profile.value = await userService.updateProfile({
      avatarUrl
    })
    
    toast({
      title: t('common.success', 'Succès'),
      description: t('profile.avatarUpdateSuccess', 'Photo de profil mise à jour')
    })
    
    showAvatarDialog.value = false
    avatarFile.value = null
    avatarPreview.value = null
  } catch (error: any) {
    toast({
      title: t('common.error', 'Erreur'),
      description: error.message || t('profile.avatarUpdateError', 'Impossible de mettre à jour la photo'),
      variant: 'destructive'
    })
  } finally {
    uploadingAvatar.value = false
  }
}

const resetProfileForm = () => {
  if (profile.value) {
    profileForm.value = {
      firstName: profile.value.firstName || '',
      lastName: profile.value.lastName || '',
      email: profile.value.email || ''
    }
  }
}

const resetPreferencesForm = () => {
  if (profile.value) {
    preferencesForm.value = {
      language: profile.value.language || 'fr',
      timezone: profile.value.timezone || 'Europe/Paris'
    }
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getStatusVariant = (status: UserStatus) => {
  switch (status) {
    case UserStatus.ACTIVE:
      return 'default'
    case UserStatus.INACTIVE:
      return 'secondary'
    case UserStatus.SUSPENDED:
      return 'destructive'
    default:
      return 'secondary'
  }
}

const getStatusLabel = (status: UserStatus) => {
  switch (status) {
    case UserStatus.ACTIVE:
      return t('users.active', 'Actif')
    case UserStatus.INACTIVE:
      return t('users.inactive', 'Inactif')
    case UserStatus.SUSPENDED:
      return t('users.suspended', 'Suspendu')
    default:
      return status
  }
}

onMounted(() => {
  loadProfile()
})
</script>

