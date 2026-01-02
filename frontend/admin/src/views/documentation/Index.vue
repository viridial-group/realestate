<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <!-- Sidebar Navigation -->
    <aside class="w-64 border-r bg-muted/30 p-6 overflow-y-auto">
      <div class="space-y-1">
        <h2 class="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-4">
          {{ t('documentation.categoriesLabel') }}
        </h2>
        
        <RouterLink
          v-for="category in categories"
          :key="category.id"
          :to="category.path"
          :class="[
            'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
            isActive(category.path)
              ? 'bg-primary text-primary-foreground'
              : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
          ]"
        >
          <component :is="category.icon" class="h-4 w-4" />
          <span>{{ category.label }}</span>
        </RouterLink>
      </div>

      <div class="mt-8 space-y-1">
        <h2 class="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-4">
          {{ t('documentation.modulesLabel') }}
        </h2>
        
        <RouterLink
          v-for="module in modules"
          :key="module.id"
          :to="module.path"
          :class="[
            'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
            isActive(module.path)
              ? 'bg-primary text-primary-foreground'
              : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
          ]"
        >
          <component :is="module.icon" class="h-4 w-4" />
          <span>{{ module.label }}</span>
        </RouterLink>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto">
        <!-- Header -->
        <div class="mb-8">
          <h1 class="text-4xl font-bold mb-4">{{ t('documentation.title') }}</h1>
          <p class="text-lg text-muted-foreground">
            {{ t('documentation.description') }}
          </p>
        </div>

        <!-- Quick Start Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-12">
          <Card class="cursor-pointer hover:shadow-lg transition-shadow" @click="$router.push('/documentation/getting-started')">
            <CardHeader>
              <div class="flex items-center gap-3 mb-2">
                <div class="p-2 rounded-lg bg-primary/10">
                  <Rocket class="h-5 w-5 text-primary" />
                </div>
                <CardTitle>{{ t('documentation.quickStart.title') }}</CardTitle>
              </div>
              <CardDescription>
                {{ t('documentation.quickStart.description') }}
              </CardDescription>
            </CardHeader>
          </Card>

          <Card class="cursor-pointer hover:shadow-lg transition-shadow" @click="$router.push('/documentation/faq')">
            <CardHeader>
              <div class="flex items-center gap-3 mb-2">
                <div class="p-2 rounded-lg bg-primary/10">
                  <HelpCircle class="h-5 w-5 text-primary" />
                </div>
                <CardTitle>{{ t('documentation.faq.title') }}</CardTitle>
              </div>
              <CardDescription>
                {{ t('documentation.faq.description') }}
              </CardDescription>
            </CardHeader>
          </Card>
        </div>

        <!-- Modules Section -->
        <div class="mb-12">
          <h2 class="text-2xl font-bold mb-6">{{ t('documentation.modules') }}</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <Card
              v-for="module in modules"
              :key="module.id"
              class="cursor-pointer hover:shadow-lg transition-shadow"
              @click="$router.push(module.path)"
            >
              <CardHeader>
                <div class="flex items-center gap-3 mb-2">
                  <component :is="module.icon" class="h-5 w-5 text-primary" />
                  <CardTitle class="text-lg">{{ module.label }}</CardTitle>
                </div>
                <CardDescription>{{ module.description }}</CardDescription>
              </CardHeader>
            </Card>
          </div>
        </div>

        <!-- Features Section -->
        <div>
          <h2 class="text-2xl font-bold mb-6">{{ t('documentation.features.title') }}</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="flex gap-4">
              <div class="flex-shrink-0">
                <div class="p-3 rounded-lg bg-primary/10">
                  <Home class="h-6 w-6 text-primary" />
                </div>
              </div>
              <div>
                <h3 class="font-semibold mb-2">{{ t('documentation.features.properties') }}</h3>
                <p class="text-sm text-muted-foreground">
                  {{ t('documentation.features.propertiesDescription') }}
                </p>
              </div>
            </div>

            <div class="flex gap-4">
              <div class="flex-shrink-0">
                <div class="p-3 rounded-lg bg-primary/10">
                  <Users class="h-6 w-6 text-primary" />
                </div>
              </div>
              <div>
                <h3 class="font-semibold mb-2">{{ t('documentation.features.users') }}</h3>
                <p class="text-sm text-muted-foreground">
                  {{ t('documentation.features.usersDescription') }}
                </p>
              </div>
            </div>

            <div class="flex gap-4">
              <div class="flex-shrink-0">
                <div class="p-3 rounded-lg bg-primary/10">
                  <Workflow class="h-6 w-6 text-primary" />
                </div>
              </div>
              <div>
                <h3 class="font-semibold mb-2">{{ t('documentation.features.workflows') }}</h3>
                <p class="text-sm text-muted-foreground">
                  {{ t('documentation.features.workflowsDescription') }}
                </p>
              </div>
            </div>

            <div class="flex gap-4">
              <div class="flex-shrink-0">
                <div class="p-3 rounded-lg bg-primary/10">
                  <MapPin class="h-6 w-6 text-primary" />
                </div>
              </div>
              <div>
                <h3 class="font-semibold mb-2">{{ t('documentation.features.map') }}</h3>
                <p class="text-sm text-muted-foreground">
                  {{ t('documentation.features.mapDescription') }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Card, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Rocket,
  HelpCircle,
  Home,
  Users,
  Building2,
  Workflow,
  CreditCard,
  Bell,
  FileText,
  MapPin
} from 'lucide-vue-next'

const { t } = useI18n()
const route = useRoute()

const categories = computed(() => [
  {
    id: 'getting-started',
    label: t('documentation.categories.gettingStarted'),
    path: '/documentation/getting-started',
    icon: Rocket
  },
  {
    id: 'faq',
    label: t('documentation.categories.faq'),
    path: '/documentation/faq',
    icon: HelpCircle
  }
])

const modules = computed(() => [
  {
    id: 'properties',
    label: t('documentation.modules.properties'),
    path: '/documentation/properties',
    icon: Home,
    description: t('documentation.modules.propertiesDescription')
  },
  {
    id: 'users',
    label: t('documentation.modules.users'),
    path: '/documentation/users',
    icon: Users,
    description: t('documentation.modules.usersDescription')
  },
  {
    id: 'organizations',
    label: t('documentation.modules.organizations'),
    path: '/documentation/organizations',
    icon: Building2,
    description: t('documentation.modules.organizationsDescription')
  },
  {
    id: 'workflows',
    label: t('documentation.modules.workflows'),
    path: '/documentation/workflows',
    icon: Workflow,
    description: t('documentation.modules.workflowsDescription')
  },
  {
    id: 'billing',
    label: t('documentation.modules.billing'),
    path: '/documentation/billing',
    icon: CreditCard,
    description: t('documentation.modules.billingDescription')
  },
  {
    id: 'notifications',
    label: t('documentation.modules.notifications'),
    path: '/documentation/notifications',
    icon: Bell,
    description: t('documentation.modules.notificationsDescription')
  },
  {
    id: 'documents',
    label: t('documentation.modules.documents'),
    path: '/documentation/documents',
    icon: FileText,
    description: t('documentation.modules.documentsDescription')
  },
  {
    id: 'map',
    label: t('documentation.modules.map'),
    path: '/documentation/map',
    icon: MapPin,
    description: t('documentation.modules.mapDescription')
  }
])

const isActive = (path: string) => {
  return route.path === path
}
</script>

