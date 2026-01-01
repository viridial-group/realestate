<template>
  <div class="space-y-6">
    <!-- Header -->
    <div>
      <h1 class="text-3xl font-bold">{{ t('dashboard.title') }}</h1>
      <p class="text-muted-foreground">{{ t('dashboard.overview') }}</p>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('dashboard.totalUsers') }}</CardTitle>
          <Users class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats?.totalUsers || 0 }}</div>
          <p class="text-xs text-muted-foreground">+{{ stats?.newUsersThisMonth || 0 }} {{ t('dashboard.thisMonth') }}</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('dashboard.activeUsers') }}</CardTitle>
          <UserCheck class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-green-600">{{ stats?.activeUsers || 0 }}</div>
          <p class="text-xs text-muted-foreground">Utilisateurs actifs</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('dashboard.properties') }}</CardTitle>
          <Home class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ totalProperties || 0 }}</div>
          <p class="text-xs text-muted-foreground">Propriétés enregistrées</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('dashboard.organizations') }}</CardTitle>
          <Building2 class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ totalOrganizations || 0 }}</div>
          <p class="text-xs text-muted-foreground">Organisations actives</p>
        </CardContent>
      </Card>
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <Card>
        <CardHeader>
          <CardTitle>{{ t('dashboard.usersByRole') }}</CardTitle>
        </CardHeader>
        <CardContent>
          <div class="space-y-2">
            <div
              v-for="(count, role) in stats?.usersByRole || {}"
              :key="role"
              class="flex items-center justify-between"
            >
              <span class="text-sm">{{ role }}</span>
              <div class="flex items-center space-x-2">
                <div class="w-32 bg-secondary rounded-full h-2">
                  <div
                    class="bg-primary h-2 rounded-full"
                    :style="{ width: `${(count / (stats?.totalUsers || 1)) * 100}%` }"
                  ></div>
                </div>
                <span class="text-sm font-medium w-8 text-right">{{ count }}</span>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>{{ t('dashboard.usersByStatus') }}</CardTitle>
        </CardHeader>
        <CardContent>
          <div class="space-y-2">
            <div
              v-for="(count, status) in stats?.usersByStatus || {}"
              :key="status"
              class="flex items-center justify-between"
            >
              <span class="text-sm">{{ status }}</span>
              <div class="flex items-center space-x-2">
                <div class="w-32 bg-secondary rounded-full h-2">
                  <div
                    class="bg-primary h-2 rounded-full"
                    :style="{ width: `${(count / (stats?.totalUsers || 1)) * 100}%` }"
                  ></div>
                </div>
                <span class="text-sm font-medium w-8 text-right">{{ count }}</span>
              </div>
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
              <span class="text-xs text-muted-foreground">{{ stats?.totalUsers || 0 }} {{ t('common.total') }}</span>
            </Button>
            <Button variant="outline" class="h-24 flex-col justify-start" @click="$router.push('/properties')">
              <Home class="h-6 w-6 mb-2" />
              <span class="text-sm font-medium">{{ t('dashboard.manageProperties') }}</span>
              <span class="text-xs text-muted-foreground">{{ totalProperties || 0 }} {{ t('common.total') }}</span>
            </Button>
            <Button variant="outline" class="h-24 flex-col justify-start" @click="$router.push('/organizations')">
              <Building2 class="h-6 w-6 mb-2" />
              <span class="text-sm font-medium">{{ t('dashboard.manageOrganizations') }}</span>
              <span class="text-xs text-muted-foreground">{{ totalOrganizations || 0 }} {{ t('common.total') }}</span>
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
            <div v-for="activity in recentActivities" :key="activity.id" class="flex items-start gap-3">
              <div class="flex-shrink-0 w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                <component :is="activity.icon" class="h-4 w-4 text-primary" />
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium">{{ activity.title }}</p>
                <p class="text-xs text-muted-foreground">{{ activity.time }}</p>
              </div>
            </div>
            <div v-if="recentActivities.length === 0" class="text-sm text-muted-foreground text-center py-4">
              Aucune activité récente
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUser } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Users, UserCheck, Home, Building2, CreditCard, UserPlus, Plus } from 'lucide-vue-next'

const { t } = useI18n()

const { loadStats, stats } = useUser()
const totalProperties = ref(0)
const totalOrganizations = ref(0)

const recentActivities = ref([
  {
    id: '1',
    title: 'Nouvel utilisateur créé',
    time: 'Il y a 5 minutes',
    icon: UserPlus
  },
  {
    id: '2',
    title: 'Nouvelle propriété ajoutée',
    time: 'Il y a 1 heure',
    icon: Plus
  },
  {
    id: '3',
    title: 'Nouvelle organisation créée',
    time: 'Il y a 2 heures',
    icon: Plus
  }
])

onMounted(async () => {
  await loadStats()
  // TODO: Load properties and organizations stats
  totalProperties.value = 0
  totalOrganizations.value = 0
})
</script>
