<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <DocumentationSidebar />
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto">
        <Button variant="ghost" size="sm" @click="$router.push('/documentation')" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        
        <h1 class="text-4xl font-bold mb-4">{{ t('documentation.documents.title') }}</h1>
        <p class="text-lg text-muted-foreground mb-8">
          {{ t('documentation.documents.description') }}
        </p>

        <!-- Overview -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.documents.overview.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <p>{{ t('documentation.documents.overview.description') }}</p>
            <ul class="list-disc list-inside space-y-2 ml-4">
              <li>{{ t('documentation.documents.overview.feature1') }}</li>
              <li>{{ t('documentation.documents.overview.feature2') }}</li>
              <li>{{ t('documentation.documents.overview.feature3') }}</li>
            </ul>
          </CardContent>
        </Card>

        <!-- Uploading Documents -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.documents.uploading.title') }}</CardTitle>
            <CardDescription>{{ t('documentation.documents.uploading.description') }}</CardDescription>
          </CardHeader>
          <CardContent class="space-y-6">
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">1</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.documents.uploading.step1.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.documents.uploading.step1.description') }}</p>
            </div>

            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">2</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.documents.uploading.step2.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.documents.uploading.step2.description') }}</p>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.documents.uploading.step2.field1.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.documents.uploading.step2.field1.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.documents.uploading.step2.field2.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.documents.uploading.step2.field2.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.documents.uploading.step2.field3.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.documents.uploading.step2.field3.description') }}</p>
                </div>
              </div>
            </div>

            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">3</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.documents.uploading.step3.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.documents.uploading.step3.description') }}</p>
              <div class="p-3 bg-yellow-50 dark:bg-yellow-950 rounded-lg">
                <p class="text-sm">
                  <strong>{{ t('documentation.documents.uploading.step3.limit.title') }}</strong>
                  {{ t('documentation.documents.uploading.step3.limit.description') }}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Managing Documents -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('documentation.documents.managing.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.documents.managing.viewing.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.documents.managing.viewing.description') }}</p>
              <ol class="list-decimal list-inside space-y-2 ml-4 text-sm">
                <li>{{ t('documentation.documents.managing.viewing.step1') }}</li>
                <li>{{ t('documentation.documents.managing.viewing.step2') }}</li>
              </ol>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.documents.managing.downloading.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.documents.managing.downloading.description') }}</p>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.documents.managing.deleting.title') }}</h3>
              <p class="text-muted-foreground">{{ t('documentation.documents.managing.deleting.description') }}</p>
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
import { ArrowLeft } from 'lucide-vue-next'
import DocumentationSidebar from '@/components/documentation/DocumentationSidebar.vue'

const { t } = useI18n()
</script>
