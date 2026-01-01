<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-900 dark:to-slate-800 p-4">
    <div class="w-full max-w-md">
      <!-- Logo Section -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-primary rounded-xl mb-4">
          <span class="text-2xl font-bold text-primary-foreground">RE</span>
        </div>
        <h1 class="text-3xl font-bold text-foreground mb-2">{{ t('auth.forgotPassword') }}</h1>
        <p class="text-muted-foreground">{{ t('auth.email') }}</p>
      </div>

      <!-- Card -->
      <Card class="shadow-lg border-0">
        <CardContent class="p-6 space-y-6">
          <!-- Reset Form -->
          <Form @submit="handleReset" :validation-schema="validationSchema">
            <div class="space-y-4">
              <Field
                v-slot="{ componentField, errors }"
                name="email"
              >
                <div class="space-y-2">
                  <Label for="email">{{ t('auth.email') }}</Label>
                  <Input
                    id="email"
                    type="email"
                    :placeholder="t('auth.email')"
                    v-bind="componentField"
                    :class="{ 'border-destructive': errors.length > 0 }"
                  />
                  <ErrorMessage name="email" class="text-sm text-destructive" />
                </div>
              </Field>

              <Button type="submit" class="w-full" size="lg" :disabled="loading">
                <Loader2 v-if="loading" class="mr-2 h-4 w-4 animate-spin" />
                <span v-if="!loading">{{ t('auth.sendResetLink') }}</span>
                <span v-else>{{ t('common.loading') }}</span>
              </Button>
            </div>
          </Form>

          <!-- Back to Login Link -->
          <div class="text-center">
            <RouterLink
              to="/login"
              class="text-sm text-primary font-medium hover:underline"
            >
              ← {{ t('auth.backToLogin') }}
            </RouterLink>
          </div>
        </CardContent>
      </Card>

      <!-- Footer -->
      <div class="text-center mt-8 text-sm text-muted-foreground">
        © Real Estate Platform. Crafted with ❤️
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { Form, Field, ErrorMessage } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { authService } from '@viridial/shared'
import { forgotPasswordSchema } from '@/schemas/auth.schema'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { RouterLink } from 'vue-router'
import { Loader2 } from 'lucide-vue-next'

const { t } = useI18n()

const router = useRouter()
const { toast } = useToast()
const loading = ref(false)
const validationSchema = toTypedSchema(forgotPasswordSchema)

const handleReset = async (values: any) => {
  loading.value = true
  try {
    await authService.forgotPassword({
      email: values.email
    })

    toast({
      title: t('messages.success.created'),
      description: t('auth.sendResetLink')
    })

    // Rediriger vers login après 2 secondes
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (error: any) {
    console.error('Reset password error:', error)
    
    toast({
      title: t('messages.error.generic'),
      description: error.response?.data?.message || error.message || t('messages.error.generic'),
      variant: 'destructive' as const
    })
  } finally {
    loading.value = false
  }
}
</script>
