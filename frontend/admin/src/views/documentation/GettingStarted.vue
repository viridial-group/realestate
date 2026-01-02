<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <!-- Sidebar Navigation -->
    <DocumentationSidebar />

    <!-- Main Content -->
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto prose prose-slate dark:prose-invert max-w-none">
        <div class="mb-8">
          <Button variant="ghost" size="sm" @click="$router.push('/documentation')" class="mb-4">
            <ArrowLeft class="mr-2 h-4 w-4" />
            {{ t('common.back') }}
          </Button>
          <h1 class="text-4xl font-bold mb-4">{{ t('documentation.gettingStarted.title') }}</h1>
          <p class="text-lg text-muted-foreground">
            {{ t('documentation.gettingStarted.description') }}
          </p>
        </div>

        <!-- Introduction -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.gettingStarted.welcome') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <p>{{ t('documentation.gettingStarted.intro') }}</p>
            <ul class="list-disc list-inside space-y-2 ml-4">
              <li>{{ t('documentation.gettingStarted.feature1') }}</li>
              <li>{{ t('documentation.gettingStarted.feature2') }}</li>
              <li>{{ t('documentation.gettingStarted.feature3') }}</li>
              <li>{{ t('documentation.gettingStarted.feature4') }}</li>
            </ul>
          </CardContent>
        </Card>

        <!-- First Steps -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.gettingStarted.firstSteps.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-6">
            <div>
              <h3 class="text-xl font-semibold mb-3 flex items-center gap-2">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">1</span>
                {{ t('documentation.gettingStarted.firstSteps.step1.title') }}
              </h3>
              <p class="text-muted-foreground mb-4">{{ t('documentation.gettingStarted.firstSteps.step1.description') }}</p>
              <div class="bg-muted p-4 rounded-lg">
                <code class="text-sm">admin@viridial.com / admin123</code>
              </div>
            </div>

            <div>
              <h3 class="text-xl font-semibold mb-3 flex items-center gap-2">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">2</span>
                {{ t('documentation.gettingStarted.firstSteps.step2.title') }}
              </h3>
              <p class="text-muted-foreground mb-4">{{ t('documentation.gettingStarted.firstSteps.step2.description') }}</p>
              <div class="space-y-2">
                <div class="flex items-center gap-2">
                  <CheckCircle class="h-5 w-5 text-green-500" />
                  <span>{{ t('documentation.gettingStarted.firstSteps.step2.item1') }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <CheckCircle class="h-5 w-5 text-green-500" />
                  <span>{{ t('documentation.gettingStarted.firstSteps.step2.item2') }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <CheckCircle class="h-5 w-5 text-green-500" />
                  <span>{{ t('documentation.gettingStarted.firstSteps.step2.item3') }}</span>
                </div>
              </div>
            </div>

            <div>
              <h3 class="text-xl font-semibold mb-3 flex items-center gap-2">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">3</span>
                {{ t('documentation.gettingStarted.firstSteps.step3.title') }}
              </h3>
              <p class="text-muted-foreground">{{ t('documentation.gettingStarted.firstSteps.step3.description') }}</p>
            </div>
          </CardContent>
        </Card>

        <!-- Navigation Guide -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.gettingStarted.navigation.title') }}</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="mb-4">{{ t('documentation.gettingStarted.navigation.description') }}</p>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <LayoutDashboard class="h-4 w-4" />
                  {{ t('dashboard.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.gettingStarted.navigation.dashboard') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <Home class="h-4 w-4" />
                  {{ t('properties.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.gettingStarted.navigation.properties') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <Users class="h-4 w-4" />
                  {{ t('users.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.gettingStarted.navigation.users') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <Building2 class="h-4 w-4" />
                  {{ t('organizations.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.gettingStarted.navigation.organizations') }}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Next Steps -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('documentation.gettingStarted.nextSteps.title') }}</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="mb-4">{{ t('documentation.gettingStarted.nextSteps.description') }}</p>
            <div class="space-y-3">
              <RouterLink
                to="/documentation/properties"
                class="flex items-center gap-3 p-3 border rounded-lg hover:bg-accent transition-colors"
              >
                <Home class="h-5 w-5 text-primary" />
                <div>
                  <div class="font-semibold">{{ t('documentation.modules.properties') }}</div>
                  <div class="text-sm text-muted-foreground">{{ t('documentation.modules.propertiesDescription') }}</div>
                </div>
                <ChevronRight class="h-4 w-4 ml-auto" />
              </RouterLink>
              <RouterLink
                to="/documentation/users"
                class="flex items-center gap-3 p-3 border rounded-lg hover:bg-accent transition-colors"
              >
                <Users class="h-5 w-5 text-primary" />
                <div>
                  <div class="font-semibold">{{ t('documentation.modules.users') }}</div>
                  <div class="text-sm text-muted-foreground">{{ t('documentation.modules.usersDescription') }}</div>
                </div>
                <ChevronRight class="h-4 w-4 ml-auto" />
              </RouterLink>
            </div>
          </CardContent>
        </Card>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  ArrowLeft,
  CheckCircle,
  LayoutDashboard,
  Home,
  Users,
  Building2,
  ChevronRight
} from 'lucide-vue-next'
import DocumentationSidebar from '@/components/documentation/DocumentationSidebar.vue'

const { t } = useI18n()
</script>

