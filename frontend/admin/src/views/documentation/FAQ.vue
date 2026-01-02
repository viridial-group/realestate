<template>
  <div class="flex h-[calc(100vh-8rem)]">
    <DocumentationSidebar />
    <main class="flex-1 overflow-y-auto p-8">
      <div class="max-w-4xl mx-auto">
        <Button variant="ghost" size="sm" @click="$router.push('/documentation')" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        
        <h1 class="text-4xl font-bold mb-4">{{ t('documentation.faq.title') }}</h1>
        <p class="text-lg text-muted-foreground mb-8">
          {{ t('documentation.faq.description') }}
        </p>

        <div class="space-y-4">
          <Card v-for="(faq, index) in faqs" :key="index" class="mb-4">
            <CardHeader>
              <CardTitle class="text-lg">{{ faq.question }}</CardTitle>
            </CardHeader>
            <CardContent>
              <p class="text-muted-foreground">{{ faq.answer }}</p>
            </CardContent>
          </Card>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { ArrowLeft } from 'lucide-vue-next'
import DocumentationSidebar from '@/components/documentation/DocumentationSidebar.vue'

const { t } = useI18n()

const faqs = computed(() => [
  {
    question: t('documentation.faq.items.q1'),
    answer: t('documentation.faq.items.a1')
  },
  {
    question: t('documentation.faq.items.q2'),
    answer: t('documentation.faq.items.a2')
  },
  {
    question: t('documentation.faq.items.q3'),
    answer: t('documentation.faq.items.a3')
  },
  {
    question: t('documentation.faq.items.q4'),
    answer: t('documentation.faq.items.a4')
  },
  {
    question: t('documentation.faq.items.q5'),
    answer: t('documentation.faq.items.a5')
  }
])
</script>

