<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <DocumentationSidebar />
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto">
        <Button variant="ghost" size="sm" @click="$router.push('/documentation')" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        
        <h1 class="text-4xl font-bold mb-4">{{ t('documentation.workflows.title') }}</h1>
        <p class="text-lg text-muted-foreground mb-8">
          {{ t('documentation.workflows.description') }}
        </p>

        <!-- Overview -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.workflows.overview.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <p>{{ t('documentation.workflows.overview.description') }}</p>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <CheckCircle class="h-5 w-5 text-green-500" />
                  {{ t('documentation.workflows.overview.benefit1.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.workflows.overview.benefit1.description') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <CheckCircle class="h-5 w-5 text-green-500" />
                  {{ t('documentation.workflows.overview.benefit2.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.workflows.overview.benefit2.description') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <CheckCircle class="h-5 w-5 text-green-500" />
                  {{ t('documentation.workflows.overview.benefit3.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.workflows.overview.benefit3.description') }}</p>
              </div>
              <div class="p-4 border rounded-lg">
                <div class="font-semibold mb-2 flex items-center gap-2">
                  <CheckCircle class="h-5 w-5 text-green-500" />
                  {{ t('documentation.workflows.overview.benefit4.title') }}
                </div>
                <p class="text-sm text-muted-foreground">{{ t('documentation.workflows.overview.benefit4.description') }}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Creating a Workflow - Detailed Steps -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.workflows.creating.title') }}</CardTitle>
            <CardDescription>{{ t('documentation.workflows.creating.description') }}</CardDescription>
          </CardHeader>
          <CardContent class="space-y-6">
            <!-- Step 1 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">1</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.workflows.creating.step1.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.workflows.creating.step1.description') }}</p>
              <div class="bg-muted p-4 rounded-lg">
                <ol class="list-decimal list-inside space-y-2 text-sm">
                  <li>{{ t('documentation.workflows.creating.step1.item1') }}</li>
                  <li>{{ t('documentation.workflows.creating.step1.item2') }}</li>
                  <li>{{ t('documentation.workflows.creating.step1.item3') }}</li>
                </ol>
              </div>
            </div>

            <!-- Step 2 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">2</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.workflows.creating.step2.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.workflows.creating.step2.description') }}</p>
              <div class="space-y-3">
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.workflows.creating.step2.field1.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.creating.step2.field1.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.workflows.creating.step2.field2.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.creating.step2.field2.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.workflows.creating.step2.field3.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.creating.step2.field3.description') }}</p>
                </div>
                <div class="p-3 bg-muted rounded-lg">
                  <div class="font-semibold text-sm mb-1">{{ t('documentation.workflows.creating.step2.field4.label') }}</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.creating.step2.field4.description') }}</p>
                </div>
              </div>
            </div>

            <!-- Step 3 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">3</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.workflows.creating.step3.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.workflows.creating.step3.description') }}</p>
              <div class="space-y-3">
                <div class="p-4 border rounded-lg">
                  <div class="font-semibold mb-2">{{ t('documentation.workflows.creating.step3.example.title') }}</div>
                  <div class="text-sm space-y-2">
                    <div class="flex items-center gap-2">
                      <span class="w-6 h-6 rounded-full bg-primary text-primary-foreground flex items-center justify-center text-xs font-bold">1</span>
                      <div>
                        <div class="font-medium">{{ t('documentation.workflows.creating.step3.example.step1.name') }}</div>
                        <div class="text-xs text-muted-foreground">{{ t('documentation.workflows.creating.step3.example.step1.role') }}</div>
                      </div>
                    </div>
                    <div class="flex items-center gap-2">
                      <span class="w-6 h-6 rounded-full bg-primary text-primary-foreground flex items-center justify-center text-xs font-bold">2</span>
                      <div>
                        <div class="font-medium">{{ t('documentation.workflows.creating.step3.example.step2.name') }}</div>
                        <div class="text-xs text-muted-foreground">{{ t('documentation.workflows.creating.step3.example.step2.role') }}</div>
                      </div>
                    </div>
                    <div class="flex items-center gap-2">
                      <span class="w-6 h-6 rounded-full bg-primary text-primary-foreground flex items-center justify-center text-xs font-bold">3</span>
                      <div>
                        <div class="font-medium">{{ t('documentation.workflows.creating.step3.example.step3.name') }}</div>
                        <div class="text-xs text-muted-foreground">{{ t('documentation.workflows.creating.step3.example.step3.role') }}</div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="p-3 bg-blue-50 dark:bg-blue-950 rounded-lg">
                  <p class="text-sm">
                    <strong>{{ t('documentation.workflows.creating.step3.tip.title') }}</strong>
                    {{ t('documentation.workflows.creating.step3.tip.description') }}
                  </p>
                </div>
              </div>
            </div>

            <!-- Step 4 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">4</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.workflows.creating.step4.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.workflows.creating.step4.description') }}</p>
              <div class="p-4 border rounded-lg bg-muted">
                <div class="text-sm space-y-2">
                  <div class="flex items-center gap-2">
                    <CheckCircle class="h-4 w-4 text-green-500" />
                    <span>{{ t('documentation.workflows.creating.step4.item1') }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <CheckCircle class="h-4 w-4 text-green-500" />
                    <span>{{ t('documentation.workflows.creating.step4.item2') }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <CheckCircle class="h-4 w-4 text-green-500" />
                    <span>{{ t('documentation.workflows.creating.step4.item3') }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Step 5 -->
            <div class="border-l-4 border-primary pl-6">
              <div class="flex items-center gap-3 mb-3">
                <span class="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">5</span>
                <h3 class="text-xl font-semibold">{{ t('documentation.workflows.creating.step5.title') }}</h3>
              </div>
              <p class="text-muted-foreground mb-4">{{ t('documentation.workflows.creating.step5.description') }}</p>
            </div>
          </CardContent>
        </Card>

        <!-- Using Workflows -->
        <Card class="mb-8">
          <CardHeader>
            <CardTitle>{{ t('documentation.workflows.using.title') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.workflows.using.starting.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.workflows.using.starting.description') }}</p>
              <ol class="list-decimal list-inside space-y-2 ml-4 text-sm">
                <li>{{ t('documentation.workflows.using.starting.step1') }}</li>
                <li>{{ t('documentation.workflows.using.starting.step2') }}</li>
                <li>{{ t('documentation.workflows.using.starting.step3') }}</li>
              </ol>
            </div>
            <div>
              <h3 class="text-lg font-semibold mb-2">{{ t('documentation.workflows.using.tasks.title') }}</h3>
              <p class="text-muted-foreground mb-3">{{ t('documentation.workflows.using.tasks.description') }}</p>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
                <div class="p-3 border rounded-lg">
                  <div class="font-semibold text-sm mb-1">PENDING</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.using.tasks.status.pending') }}</p>
                </div>
                <div class="p-3 border rounded-lg">
                  <div class="font-semibold text-sm mb-1">IN_PROGRESS</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.using.tasks.status.inProgress') }}</p>
                </div>
                <div class="p-3 border rounded-lg">
                  <div class="font-semibold text-sm mb-1">APPROVED</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.using.tasks.status.approved') }}</p>
                </div>
                <div class="p-3 border rounded-lg">
                  <div class="font-semibold text-sm mb-1">REJECTED</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.using.tasks.status.rejected') }}</p>
                </div>
                <div class="p-3 border rounded-lg">
                  <div class="font-semibold text-sm mb-1">CANCELLED</div>
                  <p class="text-xs text-muted-foreground">{{ t('documentation.workflows.using.tasks.status.cancelled') }}</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Best Practices -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('documentation.workflows.bestPractices.title') }}</CardTitle>
          </CardHeader>
          <CardContent>
            <ul class="space-y-3">
              <li class="flex items-start gap-3">
                <CheckCircle class="h-5 w-5 text-green-500 mt-0.5 flex-shrink-0" />
                <div>
                  <div class="font-semibold">{{ t('documentation.workflows.bestPractices.item1.title') }}</div>
                  <p class="text-sm text-muted-foreground">{{ t('documentation.workflows.bestPractices.item1.description') }}</p>
                </div>
              </li>
              <li class="flex items-start gap-3">
                <CheckCircle class="h-5 w-5 text-green-500 mt-0.5 flex-shrink-0" />
                <div>
                  <div class="font-semibold">{{ t('documentation.workflows.bestPractices.item2.title') }}</div>
                  <p class="text-sm text-muted-foreground">{{ t('documentation.workflows.bestPractices.item2.description') }}</p>
                </div>
              </li>
              <li class="flex items-start gap-3">
                <CheckCircle class="h-5 w-5 text-green-500 mt-0.5 flex-shrink-0" />
                <div>
                  <div class="font-semibold">{{ t('documentation.workflows.bestPractices.item3.title') }}</div>
                  <p class="text-sm text-muted-foreground">{{ t('documentation.workflows.bestPractices.item3.description') }}</p>
                </div>
              </li>
            </ul>
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
import { ArrowLeft, CheckCircle } from 'lucide-vue-next'
import DocumentationSidebar from '@/components/documentation/DocumentationSidebar.vue'

const { t } = useI18n()
</script>
