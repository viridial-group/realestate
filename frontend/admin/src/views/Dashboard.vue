<template>
  <div class="space-y-6">
    <!-- Header -->
    <div>
      <h1 class="text-2.5xl font-bold">{{ t('dashboard.title') }}</h1>
      <p class="text-muted-foreground">{{ t('dashboard.overview') }}</p>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <!-- Total Users Card -->
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('dashboard.totalUsers') }}</CardTitle>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <Users class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ userStats.total || 0 }}</div>
          <p class="text-xs text-muted-foreground">+{{ userStats.newThisMonth || 0 }} {{ t('dashboard.thisMonth') }}</p>
        </CardContent>
      </Card>

      <!-- Active Users Card -->
      <Card class="relative overflow-hidden border-l-4" style="border-left-color: #33d484;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('dashboard.activeUsers') }}</CardTitle>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(51, 212, 132, 0.1);">
            <UserCheck class="h-5 w-5" style="color: #33d484;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #33d484;">{{ userStats.active || 0 }}</div>
          <p class="text-xs text-muted-foreground">Utilisateurs actifs</p>
        </CardContent>
      </Card>

      <!-- Properties Card -->
      <Card class="relative overflow-hidden border-l-4" style="border-left-color: #fdb022;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('dashboard.properties') }}</CardTitle>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(253, 176, 34, 0.1);">
            <Home class="h-5 w-5" style="color: #fdb022;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ propertiesStats.total || 0 }}</div>
          <p class="text-xs text-muted-foreground">
            +{{ propertiesStats.newThisMonth || 0 }} {{ t('dashboard.thisMonth') }}
          </p>
        </CardContent>
      </Card>

      <!-- Organizations Card -->
      <Card class="relative overflow-hidden border-l-4" style="border-left-color: #04c9ff;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('dashboard.organizations') }}</CardTitle>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(4, 201, 255, 0.1);">
            <Building2 class="h-5 w-5" style="color: #04c9ff;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ organizationStats.total || 0 }}</div>
          <p class="text-xs text-muted-foreground">{{ organizationStats.active || 0 }} actives</p>
        </CardContent>
      </Card>
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <!-- Graphique: Propriétés par type -->
      <Card v-if="properties.length > 0">
        <CardHeader>
          <CardTitle>Propriétés par type</CardTitle>
          <CardDescription>Répartition des propriétés selon leur type</CardDescription>
        </CardHeader>
        <CardContent>
          <PropertiesByTypeChart :properties="properties" />
        </CardContent>
      </Card>

      <!-- Graphique: Propriétés par statut -->
      <Card v-if="properties.length > 0">
        <CardHeader>
          <CardTitle>Répartition par statut</CardTitle>
          <CardDescription>Statut des propriétés</CardDescription>
        </CardHeader>
        <CardContent>
          <PropertiesByStatusChart :properties="properties" />
        </CardContent>
      </Card>

      <!-- Graphique: Propriétés par ville (shadcn-vue/Unovis) -->
      <Card v-if="dashboardStats && propertiesStats.byCity && Object.keys(propertiesStats.byCity).length > 0">
        <CardHeader>
          <CardTitle>Propriétés par ville</CardTitle>
          <CardDescription>Top 10 des villes avec le plus de propriétés</CardDescription>
        </CardHeader>
        <CardContent>
          <PropertiesByCityChart :stats="dashboardStats" />
        </CardContent>
      </Card>

      <!-- Graphique: Évolution temporelle -->
      <Card v-if="properties.length > 0" class="lg:col-span-2">
        <CardHeader>
          <CardTitle>Évolution des propriétés</CardTitle>
          <CardDescription>Nombre de propriétés créées par mois (12 derniers mois)</CardDescription>
        </CardHeader>
        <CardContent>
          <PropertiesTimelineChart :properties="properties" />
        </CardContent>
      </Card>

      <!-- Graphiques utilisateurs -->
      <Card>
        <CardHeader>
          <CardTitle>{{ t('dashboard.usersByRole') }}</CardTitle>
          <CardDescription>{{ t('dashboard.usersByRoleDescription', 'Répartition des utilisateurs par rôle') }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="space-y-2">
            <div
              v-for="(count, role) in usersByRole"
              :key="role"
              class="flex items-center justify-between"
            >
              <span class="text-sm">{{ getRoleLabel(role) }}</span>
              <div class="flex items-center space-x-2">
                <div class="w-32 bg-secondary rounded-full h-2">
                  <div
                    class="bg-primary h-2 rounded-full transition-all"
                    :style="{ width: `${(count / (userStats.total || 1)) * 100}%` }"
                  ></div>
                </div>
                <span class="text-sm font-medium w-8 text-right">{{ count }}</span>
              </div>
            </div>
            <div v-if="Object.keys(usersByRole).length === 0" class="text-sm text-muted-foreground text-center py-4">
              {{ t('dashboard.noDataAvailable', 'Aucune donnée disponible') }}
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>{{ t('dashboard.usersByStatus') }}</CardTitle>
          <CardDescription>{{ t('dashboard.usersByStatusDescription', 'Répartition des utilisateurs par statut') }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="space-y-2">
            <div
              v-for="(count, status) in usersByStatus"
              :key="status"
              class="flex items-center justify-between"
            >
              <span class="text-sm">{{ getStatusLabel(status) }}</span>
              <div class="flex items-center space-x-2">
                <div class="w-32 bg-secondary rounded-full h-2">
                  <div
                    class="h-2 rounded-full transition-all"
                    :class="getStatusColorClass(status)"
                    :style="{ width: `${(count / (userStats.total || 1)) * 100}%` }"
                  ></div>
                </div>
                <span class="text-sm font-medium w-8 text-right">{{ count }}</span>
              </div>
            </div>
            <div v-if="Object.keys(usersByStatus).length === 0" class="text-sm text-muted-foreground text-center py-4">
              {{ t('dashboard.noDataAvailable', 'Aucune donnée disponible') }}
            </div>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Quick Actions avec Accès Direct -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <Card>
        <CardHeader>
          <CardTitle>{{ t('dashboard.quickActions') }}</CardTitle>
          <CardDescription>{{ t('dashboard.overview') }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="grid grid-cols-2 gap-3">
            <Button variant="outline" class="h-24 flex-col justify-start" @click="$router.push('/users')">
              <Users class="h-6 w-6 mb-2" />
              <span class="text-sm font-medium">{{ t('dashboard.manageUsers') }}</span>
              <span class="text-xs text-muted-foreground">{{ userStats.total || 0 }} {{ t('common.total') }}</span>
            </Button>
            <Button variant="outline" class="h-24 flex-col justify-start" @click="$router.push('/properties')">
              <Home class="h-6 w-6 mb-2" />
              <span class="text-sm font-medium">{{ t('dashboard.manageProperties') }}</span>
              <span class="text-xs text-muted-foreground">{{ propertiesStats.total || 0 }} {{ t('common.total') }}</span>
            </Button>
            <Button variant="outline" class="h-24 flex-col justify-start" @click="$router.push('/organizations')">
              <Building2 class="h-6 w-6 mb-2" />
              <span class="text-sm font-medium">{{ t('dashboard.manageOrganizations') }}</span>
              <span class="text-xs text-muted-foreground">{{ organizationStats.total || 0 }} {{ t('common.total') }}</span>
            </Button>
            <Button variant="outline" class="h-24 flex-col justify-start" @click="$router.push('/billing')">
              <CreditCard class="h-6 w-6 mb-2" />
              <span class="text-sm font-medium">{{ t('billing.title') }}</span>
              <span class="text-xs text-muted-foreground">{{ t('common.actions') }}</span>
            </Button>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>{{ t('dashboard.recentActivity') }}</CardTitle>
          <CardDescription>{{ t('dashboard.overview') }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="space-y-3">
            <div v-for="activity in formattedActivities" :key="activity.id" class="flex items-start gap-3">
              <div class="flex-shrink-0 w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                <component :is="activity.icon" class="h-4 w-4 text-primary" />
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium">{{ activity.title }}</p>
                <p class="text-xs text-muted-foreground">{{ activity.time }}</p>
              </div>
            </div>
            <div v-if="formattedActivities.length === 0" class="text-sm text-muted-foreground text-center py-4">
              {{ t('dashboard.noRecentActivity', 'Aucune activité récente') }}
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { statsService, propertyService, userService, type Property, type DashboardStats, type User, UserStatus } from '@viridial/shared'
import { auditService, type AuditLog } from '../../../shared/api/audit.service'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Users, UserCheck, Home, Building2, CreditCard, UserPlus, Plus, FileText, Settings } from 'lucide-vue-next'
import PropertiesByTypeChart from '@/components/dashboard/PropertiesByTypeChart.vue'
import PropertiesByStatusChart from '@/components/dashboard/PropertiesByStatusChart.vue'
import PropertiesTimelineChart from '@/components/dashboard/PropertiesTimelineChart.vue'
import PropertiesByCityChart from '@/components/dashboard/PropertiesByCityChart.vue'
import { useToast } from '@/components/ui/toast'
import { useAuthStore } from '@viridial/shared'

const { t } = useI18n()
const { toast } = useToast()

// Statistiques complètes depuis l'API optimisée
const dashboardStats = ref<DashboardStats | null>(null)
const loading = ref(false)
const properties = ref<Property[]>([]) // Pour les graphiques qui nécessitent les données complètes
const allUsers = ref<User[]>([]) // Pour calculer les statistiques par rôle et statut
const recentActivities = ref<AuditLog[]>([]) // Activités récentes depuis l'API audit
const authStore = useAuthStore()

// Statistiques calculées depuis l'API optimisée
const propertiesStats = computed(() => dashboardStats.value?.propertyStats || {
  total: 0,
  available: 0,
  sold: 0,
  rented: 0,
  published: 0,
  draft: 0,
  pending: 0,
  averagePrice: 0,
  averageSurface: 0,
  newThisMonth: 0,
  newThisWeek: 0,
  byType: {},
  byStatus: {},
  byCity: {}
})

const userStats = computed(() => dashboardStats.value?.userStats || {
  total: 0,
  active: 0,
  inactive: 0,
  newThisMonth: 0,
  newThisWeek: 0
})

const organizationStats = computed(() => dashboardStats.value?.organizationStats || {
  total: 0,
  active: 0,
  newThisMonth: 0,
  newThisWeek: 0
})


// Statistiques utilisateurs par rôle et par statut (calculées depuis allUsers)
const usersByRole = computed(() => {
  const roleCounts: Record<string, number> = {}
  allUsers.value.forEach(user => {
    user.roles?.forEach(role => {
      roleCounts[role] = (roleCounts[role] || 0) + 1
    })
  })
  return roleCounts
})

const usersByStatus = computed(() => {
  const statusCounts: Record<string, number> = {}
  allUsers.value.forEach(user => {
    const status = user.status || UserStatus.ACTIVE
    statusCounts[status] = (statusCounts[status] || 0) + 1
  })
  return statusCounts
})

// Formater les activités récentes pour l'affichage
const formattedActivities = computed(() => {
  return recentActivities.value.map((activity: AuditLog) => {
    let icon = Plus
    let title = activity.description || activity.action
    
    // Déterminer l'icône selon l'action
    if (activity.action?.toLowerCase().includes('user') || activity.action?.toLowerCase().includes('utilisateur')) {
      icon = UserPlus
    } else if (activity.action?.toLowerCase().includes('property') || activity.action?.toLowerCase().includes('propriété')) {
      icon = Home
    } else if (activity.action?.toLowerCase().includes('organization') || activity.action?.toLowerCase().includes('organisation')) {
      icon = Building2
    } else if (activity.action?.toLowerCase().includes('document')) {
      icon = FileText
    } else if (activity.action?.toLowerCase().includes('config') || activity.action?.toLowerCase().includes('setting')) {
      icon = Settings
    }
    
    // Formater le temps
    const createdAt = new Date(activity.createdAt)
    const now = new Date()
    const diffMs = now.getTime() - createdAt.getTime()
    const diffMins = Math.floor(diffMs / 60000)
    const diffHours = Math.floor(diffMs / 3600000)
    const diffDays = Math.floor(diffMs / 86400000)
    
    let time = ''
    if (diffMins < 1) {
      time = t('dashboard.justNow', 'À l\'instant')
    } else if (diffMins < 60) {
      time = t('dashboard.minutesAgo', { count: diffMins }, `Il y a ${diffMins} minute${diffMins > 1 ? 's' : ''}`)
    } else if (diffHours < 24) {
      time = t('dashboard.hoursAgo', { count: diffHours }, `Il y a ${diffHours} heure${diffHours > 1 ? 's' : ''}`)
    } else if (diffDays < 7) {
      time = t('dashboard.daysAgo', { count: diffDays }, `Il y a ${diffDays} jour${diffDays > 1 ? 's' : ''}`)
    } else {
      time = createdAt.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
    }
    
    return {
      id: activity.id.toString(),
      title,
      time,
      icon
    }
  })
})

// Charger toutes les statistiques depuis l'API optimisée
const loadDashboardStats = async () => {
  try {
    const stats = await statsService.getDashboardStats()
    dashboardStats.value = stats
    
    // Charger aussi les propriétés complètes pour les graphiques détaillés
    try {
      const allProperties = await propertyService.getAll()
      properties.value = allProperties
    } catch (error) {
      console.warn('Could not load full properties list for charts:', error)
    }
    
    // Charger tous les utilisateurs pour calculer les statistiques par rôle et statut
    try {
      const usersData = await userService.getAll({ size: 1000 }) // Charger jusqu'à 1000 utilisateurs
      allUsers.value = usersData.users
    } catch (error) {
      console.warn('Could not load users for statistics:', error)
    }
    
    // Charger les activités récentes depuis l'API audit
    try {
      const currentOrgId = authStore.user?.organizationId
      if (currentOrgId) {
        const activities = await auditService.getRecentActivities(currentOrgId, 10)
        recentActivities.value = activities
      }
    } catch (error) {
      console.warn('Could not load recent activities:', error)
      // Continuer sans les activités récentes
    }
  } catch (error: any) {
    console.error('Error loading dashboard stats:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les statistiques du dashboard',
      variant: 'destructive'
    })
  }
}

// Fonctions utilitaires pour les labels
const getRoleLabel = (role: string): string => {
  const roleLabels: Record<string, string> = {
    'ADMIN': t('users.roles.admin', 'Admin'),
    'AGENT': t('users.roles.agent', 'Agent'),
    'FREELANCE': t('users.roles.freelance', 'Freelance'),
    'AUTO_ENTREPRENEUR': t('users.roles.autoEntrepreneur', 'Auto-entrepreneur'),
    'PARTICULAR': t('users.roles.particular', 'Particulier'),
    'USER': t('users.roles.user', 'Utilisateur'),
    'SUPER_ADMIN': t('users.roles.superAdmin', 'Super Admin')
  }
  return roleLabels[role] || role
}

const getStatusLabel = (status: string): string => {
  const statusLabels: Record<string, string> = {
    'ACTIVE': t('users.active', 'Actif'),
    'INACTIVE': t('users.inactive', 'Inactif'),
    'SUSPENDED': t('users.suspended', 'Suspendu'),
    'PENDING': t('users.pending', 'En attente'),
    'DELETED': t('users.deleted', 'Supprimé')
  }
  return statusLabels[status] || status
}

const getStatusColorClass = (status: string): string => {
  const colorClasses: Record<string, string> = {
    'ACTIVE': 'bg-green-500',
    'INACTIVE': 'bg-gray-500',
    'SUSPENDED': 'bg-red-500',
    'PENDING': 'bg-yellow-500',
    'DELETED': 'bg-gray-400'
  }
  return colorClasses[status] || 'bg-primary'
}

onMounted(async () => {
  loading.value = true
  try {
    await loadDashboardStats()
  } finally {
    loading.value = false
  }
})
</script>
