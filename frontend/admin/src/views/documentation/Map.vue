<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <DocumentationSidebar />
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto">
        <Button variant="ghost" size="sm" @click="$router.push('/documentation')" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        
        <h1 class="text-4xl font-bold mb-4">{{ t('documentation.map.title') }}</h1>
        <p class="text-lg text-muted-foreground mb-8">
          {{ t('documentation.map.description') }}
        </p>

        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.map.overview.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <p>{{ t('documentation.map.overview.description') }}</p>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.map.features.markers.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.map.features.markers.description') }}</p>
              <div class="grid grid-cols-2 gap-2">
                <div class="flex items-center gap-2 p-2 bg-muted rounded">
                  <div class="w-4 h-4 rounded-full" style="background-color: #33d484;"></div>
                  <span class="text-sm">{{ t('properties.status.available') }}</span>
                </div>
                <div class="flex items-center gap-2 p-2 bg-muted rounded">
                  <div class="w-4 h-4 rounded-full" style="background-color: #fdb022;"></div>
                  <span class="text-sm">{{ t('properties.status.sold') }}</span>
                </div>
                <div class="flex items-center gap-2 p-2 bg-muted rounded">
                  <div class="w-4 h-4 rounded-full" style="background-color: #04c9ff;"></div>
                  <span class="text-sm">{{ t('properties.status.rented') }}</span>
                </div>
                <div class="flex items-center gap-2 p-2 bg-muted rounded">
                  <div class="w-4 h-4 rounded-full" style="background-color: #8b5cf6;"></div>
                  <span class="text-sm">{{ t('properties.status.pending') }}</span>
                </div>
              </div>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.map.features.clusters.title') }}</h3>
              <p class="text-muted-foreground">{{ t('documentation.map.features.clusters.description') }}</p>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.map.features.popups.title') }}</h3>
              <p class="text-muted-foreground">{{ t('documentation.map.features.popups.description') }}</p>
            </div>
          </CardContent>
        </Card>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { ArrowLeft } from 'lucide-vue-next'
import DocumentationSidebar from '@/components/documentation/DocumentationSidebar.vue'

const { t } = useI18n()
</script>

