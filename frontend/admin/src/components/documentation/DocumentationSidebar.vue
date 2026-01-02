<template>
  <aside class="w-64 border-r bg-muted/30 p-6 overflow-y-auto">
    <div class="space-y-1">
      <h2 class="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-4">
        {{ t('documentation.categoriesLabel') }}
      </h2>
      
      <RouterLink
        :to="'/documentation/getting-started'"
        :class="[
          'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
          isActive('/documentation/getting-started')
            ? 'bg-primary text-primary-foreground'
            : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
        ]"
      >
        <Rocket class="h-4 w-4" />
        <span>{{ t('documentation.categories.gettingStarted') }}</span>
      </RouterLink>

      <RouterLink
        :to="'/documentation/faq'"
        :class="[
          'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
          isActive('/documentation/faq')
            ? 'bg-primary text-primary-foreground'
            : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
        ]"
      >
        <HelpCircle class="h-4 w-4" />
        <span>{{ t('documentation.categories.faq') }}</span>
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
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
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

const modules = computed(() => [
  {
    id: 'properties',
    label: t('documentation.modules.properties'),
    path: '/documentation/properties',
    icon: Home
  },
  {
    id: 'users',
    label: t('documentation.modules.users'),
    path: '/documentation/users',
    icon: Users
  },
  {
    id: 'organizations',
    label: t('documentation.modules.organizations'),
    path: '/documentation/organizations',
    icon: Building2
  },
  {
    id: 'workflows',
    label: t('documentation.modules.workflows'),
    path: '/documentation/workflows',
    icon: Workflow
  },
  {
    id: 'billing',
    label: t('documentation.modules.billing'),
    path: '/documentation/billing',
    icon: CreditCard
  },
  {
    id: 'notifications',
    label: t('documentation.modules.notifications'),
    path: '/documentation/notifications',
    icon: Bell
  },
  {
    id: 'documents',
    label: t('documentation.modules.documents'),
    path: '/documentation/documents',
    icon: FileText
  },
  {
    id: 'map',
    label: t('documentation.modules.map'),
    path: '/documentation/map',
    icon: MapPin
  }
])

const isActive = (path: string) => {
  return route.path === path
}
</script>

