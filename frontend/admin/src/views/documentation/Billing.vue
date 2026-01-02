<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <DocumentationSidebar />
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto">
        <Button variant="ghost" size="sm" @click="$router.push('/documentation')" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        
        <h1 class="text-4xl font-bold mb-4">{{ t('documentation.billing.title') }}</h1>
        <p class="text-lg text-muted-foreground mb-8">
          {{ t('documentation.billing.description') }}
        </p>

        <!-- Overview -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.billing.overview.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <p>{{ t('documentation.billing.overview.description') }}</p>
            <ul class="list-disc list-inside space-y-2 ml-4">
              <li>{{ t('documentation.billing.overview.feature1') }}</li>
              <li>{{ t('documentation.billing.overview.feature2') }}</li>
              <li>{{ t('documentation.billing.overview.feature3') }}</li>
            </ul>
          </CardContent>
        </Card>

        <!-- Subscriptions -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.billing.subscriptions.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-6">
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.billing.subscriptions.viewing.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.billing.subscriptions.viewing.description') }}</p>
              <ol class="list-decimal list-inside space-y-2 ml-4 text-sm">
                <li>{{ t('documentation.billing.subscriptions.viewing.step1') }}</li>
                <li>{{ t('documentation.billing.subscriptions.viewing.step2') }}</li>
                <li>{{ t('documentation.billing.subscriptions.viewing.step3') }}</li>
              </ol>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.billing.subscriptions.details.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.billing.subscriptions.details.description') }}</p>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.billing.subscriptions.details.info1') }}</div>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.billing.subscriptions.details.info2') }}</div>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.billing.subscriptions.details.info3') }}</div>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.billing.subscriptions.details.info4') }}</div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Invoices -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('documentation.billing.invoices.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.billing.invoices.viewing.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.billing.invoices.viewing.description') }}</p>
              <ol class="list-decimal list-inside space-y-2 ml-4 text-sm">
                <li>{{ t('documentation.billing.invoices.viewing.step1') }}</li>
                <li>{{ t('documentation.billing.invoices.viewing.step2') }}</li>
                <li>{{ t('documentation.billing.invoices.viewing.step3') }}</li>
              </ol>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.billing.invoices.downloading.title') }}</h3>
              <p class="text-muted-foreground">{{ t('documentation.billing.invoices.downloading.description') }}</p>
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
