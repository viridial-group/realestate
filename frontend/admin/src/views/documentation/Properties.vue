<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <DocumentationSidebar />
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto">
        <Button variant="ghost" size="sm" @click="$router.push('/documentation')" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        
        <h1 class="text-4xl font-bold mb-4">{{ t('documentation.properties.title') }}</h1>
        <p class="text-lg text-muted-foreground mb-8">
          {{ t('documentation.properties.description') }}
        </p>

        <!-- Overview -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.properties.overview.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <p>{{ t('documentation.properties.overview.description') }}</p>
            <ul class="list-disc list-inside space-y-2 ml-4">
              <li>{{ t('documentation.properties.overview.feature1') }}</li>
              <li>{{ t('documentation.properties.overview.feature2') }}</li>
              <li>{{ t('documentation.properties.overview.feature3') }}</li>
              <li>{{ t('documentation.properties.overview.feature4') }}</li>
            </ul>
          </CardContent>
        </Card>

        <!-- Creating Properties - Detailed Steps -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.properties.creating.title') }}</CardTitle>
            <CardDescription>{{ t('documentation.properties.creating.description') }}</CardDescription>
          </CardHeader>
          <CardContent class="space-y-6">
            <!-- Step 1 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">1</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.properties.creating.step1.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.properties.creating.step1.description') }}</p>
              <div class="bg-muted p-4 rounded-lg">
                <ol class="list-decimal list-inside space-y-2 text-sm">
                  <li>{{ t('documentation.properties.creating.step1.item1') }}</li>
                  <li>{{ t('documentation.properties.creating.step1.item2') }}</li>
                  <li>{{ t('documentation.properties.creating.step1.item3') }}</li>
                  <li>{{ t('documentation.properties.creating.step1.item4') }}</li>
                </ol>
              </div>
            </div>

            <!-- Step 2 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">2</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.properties.creating.step2.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.properties.creating.step2.description') }}</p>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.properties.creating.step2.field1.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.properties.creating.step2.field1.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.properties.creating.step2.field2.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.properties.creating.step2.field2.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.properties.creating.step2.field3.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.properties.creating.step2.field3.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.properties.creating.step2.field4.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.properties.creating.step2.field4.description') }}</p>
                </div>
              </div>
            </div>

            <!-- Step 3 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">3</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.properties.creating.step3.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.properties.creating.step3.description') }}</p>
              <div class="p-3 bg-blue-50 dark:bg-blue-950 rounded-lg">
                <p class="text-sm">
                  <strong>{{ t('documentation.properties.creating.step3.tip.title') }}</strong>
                  {{ t('documentation.properties.creating.step3.tip.description') }}
                </p>
              </div>
            </div>

            <!-- Step 4 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">4</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.properties.creating.step4.title') }}</h3>
              </div>
              <p class="text-muted-foreground">{{ t('documentation.properties.creating.step4.description') }}</p>
            </div>
          </CardContent>
        </Card>

        <!-- Views -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.properties.views.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-6">
            <div>
              <h3 class="text-lg font-semibold mb-3 flex items-center gap-2">
                <Grid3x3 class="h-5 w-5" />
                {{ t('documentation.properties.views.grid.title') }}
              </h3>
              <p class="text-muted-foreground mb-2">{{ t('documentation.properties.views.grid.description') }}</p>
              <p class="text-sm text-muted-foreground">{{ t('documentation.properties.views.grid.usage') }}</p>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-3 flex items-center gap-2">
                <List class="h-5 w-5" />
                {{ t('documentation.properties.views.list.title') }}
              </h3>
              <p class="text-muted-foreground mb-2">{{ t('documentation.properties.views.list.description') }}</p>
              <p class="text-sm text-muted-foreground">{{ t('documentation.properties.views.list.usage') }}</p>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-3 flex items-center gap-2">
                <MapPin class="h-5 w-5" />
                {{ t('documentation.properties.views.map.title') }}
              </h3>
              <p class="text-muted-foreground mb-2">{{ t('documentation.properties.views.map.description') }}</p>
              <p class="text-sm text-muted-foreground">{{ t('documentation.properties.views.map.usage') }}</p>
            </div>
          </CardContent>
        </Card>

        <!-- Filters -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.properties.filters.title') }}</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="mb-4">{{ t('documentation.properties.filters.description') }}</p>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2">{{ t('documentation.properties.filters.organization') }}</div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.properties.filters.organizationDescription') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2">{{ t('documentation.properties.filters.type') }}</div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.properties.filters.typeDescription') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2">{{ t('documentation.properties.filters.price') }}</div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.properties.filters.priceDescription') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2">{{ t('documentation.properties.filters.status') }}</div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.properties.filters.statusDescription') }}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Editing Properties -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('documentation.properties.editing.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <p>{{ t('documentation.properties.editing.description') }}</p>
            <ol class="list-decimal list-inside space-y-2 ml-4">
              <li>{{ t('documentation.properties.editing.step1') }}</li>
              <li>{{ t('documentation.properties.editing.step2') }}</li>
              <li>{{ t('documentation.properties.editing.step3') }}</li>
            </ol>
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
import { ArrowLeft, Grid3x3, List, MapPin } from 'lucide-vue-next'
import DocumentationSidebar from '@/components/documentation/DocumentationSidebar.vue'

const { t } = useI18n()
</script>
