<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Paramètres</h1>
      <p class="mt-2 text-sm text-gray-600">
        Gérez vos paramètres et préférences
      </p>
    </div>

    <div class="space-y-6">
      <!-- Informations personnelles -->
      <Card>
        <CardHeader>
          <CardTitle>Informations personnelles</CardTitle>
        </CardHeader>
        <CardContent>
          <div v-if="loading" class="text-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          </div>

          <form v-else @submit.prevent="handleUpdateProfile" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <Label>
                  Prénom <span class="text-red-500">*</span>
                </Label>
                <Input
                  v-model="profileForm.firstName"
                  type="text"
                  required
                />
              </div>

              <div class="space-y-2">
                <Label>
                  Nom <span class="text-red-500">*</span>
                </Label>
                <Input
                  v-model="profileForm.lastName"
                  type="text"
                  required
                />
              </div>
            </div>

            <div class="space-y-2">
              <Label>
                Email <span class="text-red-500">*</span>
              </Label>
              <Input
                v-model="profileForm.email"
                type="email"
                required
                disabled
                class="bg-muted"
              />
              <p class="text-xs text-muted-foreground">L'email ne peut pas être modifié</p>
            </div>

            <div class="flex items-center justify-end gap-4 pt-4">
              <Button
                type="button"
                variant="outline"
                @click="loadProfile"
              >
                Annuler
              </Button>
              <Button
                type="submit"
                :disabled="submitting"
              >
                {{ submitting ? 'Enregistrement...' : 'Enregistrer' }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      <!-- Changer le mot de passe -->
      <Card>
        <CardHeader>
          <CardTitle>Changer le mot de passe</CardTitle>
        </CardHeader>
        <CardContent>
          <form @submit.prevent="handleChangePassword" class="space-y-4">
            <div class="space-y-2">
              <Label>
                Mot de passe actuel <span class="text-red-500">*</span>
              </Label>
              <Input
                v-model="passwordForm.currentPassword"
                type="password"
                required
              />
            </div>

            <div class="space-y-2">
              <Label>
                Nouveau mot de passe <span class="text-red-500">*</span>
              </Label>
              <Input
                v-model="passwordForm.newPassword"
                type="password"
                required
                minlength="8"
              />
              <p class="text-xs text-muted-foreground">Minimum 8 caractères</p>
            </div>

            <div class="space-y-2">
              <Label>
                Confirmer le nouveau mot de passe <span class="text-red-500">*</span>
              </Label>
              <Input
                v-model="passwordForm.confirmPassword"
                type="password"
                required
              />
            </div>

            <div v-if="passwordError" class="bg-destructive/10 border border-destructive/20 rounded-lg p-4">
              <p class="text-sm text-destructive">{{ passwordError }}</p>
            </div>

            <div class="flex items-center justify-end">
              <Button
                type="submit"
                :disabled="submittingPassword"
              >
                {{ submittingPassword ? 'Changement...' : 'Changer le mot de passe' }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      <!-- Préférences -->
      <Card>
        <CardHeader>
          <CardTitle>Préférences</CardTitle>
        </CardHeader>
        <CardContent>
        
        <div class="space-y-4">
          <!-- Notifications -->
          <div class="flex items-center justify-between py-3 border-b">
            <div class="space-y-0.5">
              <Label class="font-medium">Notifications par email</Label>
              <p class="text-sm text-muted-foreground">Recevoir des emails pour les nouveaux messages</p>
            </div>
            <Checkbox
              :checked="preferences.emailNotifications"
              @update:checked="handleEmailNotificationsChange"
            />
          </div>

          <!-- Mode sombre -->
          <div class="flex items-center justify-between py-3 border-b">
            <div class="space-y-0.5">
              <Label class="font-medium">Mode sombre</Label>
              <p class="text-sm text-muted-foreground">Activer le thème sombre</p>
            </div>
            <Checkbox
              :checked="preferences.darkMode"
              @update:checked="handleDarkModeChange"
            />
          </div>

          <!-- Confidentialité -->
          <div class="flex items-center justify-between py-3">
            <div class="space-y-0.5">
              <Label class="font-medium">Profil public</Label>
              <p class="text-sm text-muted-foreground">Permettre aux autres de voir votre profil</p>
            </div>
            <Checkbox
              :checked="preferences.publicProfile"
              @update:checked="handlePublicProfileChange"
            />
          </div>
        </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore, userService } from '@viridial/shared'
import { useToast } from '@/composables/useToast'
import { useDarkMode } from '@/composables/useDarkMode'

const authStore = useAuthStore()
const { showToast } = useToast()
const { isDark, toggleDarkMode } = useDarkMode()

const loading = ref(false)
const submitting = ref(false)
const submittingPassword = ref(false)
const passwordError = ref<string | null>(null)

const profileForm = ref({
  firstName: '',
  lastName: '',
  email: '',
})

const preferences = ref({
  emailNotifications: true,
  darkMode: false,
  publicProfile: false,
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

onMounted(async () => {
  await loadProfile()
  loadPreferences()
})

async function loadProfile() {
  loading.value = true
  try {
    const profile = await userService.getProfile()
    profileForm.value = {
      firstName: profile.firstName || '',
      lastName: profile.lastName || '',
      email: profile.email || '',
    }
  } catch (err: any) {
    showToast('Erreur lors du chargement du profil', 'error')
  } finally {
    loading.value = false
  }
}

function loadPreferences() {
  try {
    const stored = localStorage.getItem('user_preferences')
    if (stored) {
      preferences.value = { ...preferences.value, ...JSON.parse(stored) }
    }
    
    // Charger le mode sombre depuis le composable
    preferences.value.darkMode = isDark.value
  } catch (err) {
    console.error('Error loading preferences:', err)
  }
}

function savePreferences() {
  try {
    localStorage.setItem('user_preferences', JSON.stringify(preferences.value))
    
    // Appliquer le mode sombre si changé
    if (preferences.value.darkMode !== isDark.value) {
      toggleDarkMode()
    }
    
    showToast('Préférences sauvegardées', 'success')
  } catch (err) {
    console.error('Error saving preferences:', err)
    showToast('Erreur lors de la sauvegarde', 'error')
  }
}

function handleEmailNotificationsChange(checked: boolean) {
  preferences.value.emailNotifications = checked
  savePreferences()
}

function handleDarkModeChange(checked: boolean) {
  preferences.value.darkMode = checked
  savePreferences()
}

function handlePublicProfileChange(checked: boolean) {
  preferences.value.publicProfile = checked
  savePreferences()
}

async function handleUpdateProfile() {
  submitting.value = true
  try {
    await userService.updateProfile({
      firstName: profileForm.value.firstName,
      lastName: profileForm.value.lastName,
    })
    showToast('Profil mis à jour avec succès', 'success')
    await authStore.checkAuth() // Rafraîchir les infos utilisateur
  } catch (err: any) {
    showToast(
      err.response?.data?.message || err.message || 'Erreur lors de la mise à jour',
      'error'
    )
  } finally {
    submitting.value = false
  }
}

async function handleChangePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordError.value = 'Les mots de passe ne correspondent pas'
    return
  }

  if (passwordForm.value.newPassword.length < 8) {
    passwordError.value = 'Le mot de passe doit contenir au moins 8 caractères'
    return
  }

  submittingPassword.value = true
  passwordError.value = null

  try {
    await userService.changePassword(
      passwordForm.value.currentPassword,
      passwordForm.value.newPassword
    )
    
    showToast('Mot de passe changé avec succès', 'success')
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    }
  } catch (err: any) {
    passwordError.value = err.response?.data?.message || err.message || 'Erreur lors du changement de mot de passe'
  } finally {
    submittingPassword.value = false
  }
}
</script>

