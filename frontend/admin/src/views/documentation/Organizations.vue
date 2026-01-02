<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <DocumentationSidebar />
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto">
        <Button variant="ghost" size="sm" @click="$router.push('/documentation')" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        
        <h1 class="text-4xl font-bold mb-4">{{ t('documentation.organizations.title') }}</h1>
        <p class="text-lg text-muted-foreground mb-8">
          {{ t('documentation.organizations.description') }}
        </p>

        <!-- Overview -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.organizations.overview.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <p>{{ t('documentation.organizations.overview.description') }}</p>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <Building2 class="h-5 w-5 text-primary" />
                  {{ t('documentation.organizations.overview.benefit1.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.organizations.overview.benefit1.description') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <Users class="h-5 w-5 text-primary" />
                  {{ t('documentation.organizations.overview.benefit2.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.organizations.overview.benefit2.description') }}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Creating Organizations -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.organizations.creating.title') }}</CardTitle>
            <CardDescription>{{ t('documentation.organizations.creating.description') }}</CardDescription>
          </CardHeader>
          <CardContent class="space-y-6">
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">1</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.organizations.creating.step1.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.organizations.creating.step1.description') }}</p>
            </div>

            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">2</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.organizations.creating.step2.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.organizations.creating.step2.description') }}</p>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.organizations.creating.step2.field1.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.organizations.creating.step2.field1.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.organizations.creating.step2.field2.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.organizations.creating.step2.field2.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.organizations.creating.step2.field3.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.organizations.creating.step2.field3.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.organizations.creating.step2.field4.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.organizations.creating.step2.field4.description') }}</p>
                </div>
              </div>
            </div>

            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">3</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.organizations.creating.step3.title') }}</h3>
              </div>
              <p class="text-muted-foreground">{{ t('documentation.organizations.creating.step3.description') }}</p>
            </div>
          </CardContent>
        </Card>

        <!-- Managing Organizations -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('documentation.organizations.managing.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.organizations.managing.users.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.organizations.managing.users.description') }}</p>
              <ol class="list-decimal list-inside space-y-2 ml-4 text-sm">
                <li>{{ t('documentation.organizations.managing.users.step1') }}</li>
                <li>{{ t('documentation.organizations.managing.users.step2') }}</li>
                <li>{{ t('documentation.organizations.managing.users.step3') }}</li>
              </ol>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.organizations.managing.hierarchy.title') }}</h3>
              <p class="text-muted-foreground">{{ t('documentation.organizations.managing.hierarchy.description') }}</p>
            </div>
          </CardContent>
        </Card>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { ArrowLeft, Building2, Users } from 'lucide-vue-next'
import DocumentationSidebar from '@/components/documentation/DocumentationSidebar.vue'

const { t } = useI18n()
</script>
