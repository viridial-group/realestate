<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-900 dark:to-slate-800 p-4">
    <div class="w-full max-w-md">
      <!-- Logo Section -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-primary rounded-xl mb-4">
          <span class="text-2xl font-bold text-primary-foreground">RE</span>
        </div>
        <h1 class="text-3xl font-bold text-foreground mb-2">{{ t('auth.signup') }}</h1>
        <p class="text-muted-foreground">{{ t('auth.email') }} & {{ t('auth.password') }}</p>
      </div>

      <!-- Card -->
      <Card class="shadow-lg border-0">
        <CardContent class="p-6 space-y-6">
          <!-- Social Login Buttons -->
          <div class="flex gap-3">
            <Button variant="outline" class="flex-1" size="lg" @click="handleSocialSignup('google')" :disabled="loading">
              <svg class="w-5 h-5 mr-2" viewBox="0 0 24 24" fill="currentColor">
                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
              </svg>
              <span class="hidden sm:inline">{{ t('auth.signInWithGoogle') }}</span>
              <span class="sm:hidden">Google</span>
            </Button>
            <Button variant="outline" class="flex-1" size="lg" @click="handleSocialSignup('apple')" :disabled="loading">
              <svg class="w-5 h-5 mr-2" viewBox="0 0 24 24" fill="currentColor">
                <path d="M17.05 20.28c-.98.95-2.05.88-3.08.78-1.17-.12-2.23-.48-3.5-.48-1.27 0-2.4.36-3.5.48-1.03.1-2.1.17-3.08-.78-1.9-1.85-1.9-4.9 0-6.75 1.9-1.85 4.95-1.85 6.85 0 .35.34.73.5 1.23.5s.88-.16 1.23-.5c1.9-1.85 4.95-1.85 6.85 0 1.9 1.85 1.9 4.9 0 6.75zM12.03 3.75c-1.95-2.03-5.11-2.03-7.06 0-1.95 2.03-1.95 5.32 0 7.35 1.95 2.03 5.11 2.03 7.06 0 1.95-2.03 1.95-5.32 0-7.35z"/>
              </svg>
              <span class="hidden sm:inline">{{ t('auth.signInWithApple') }}</span>
              <span class="sm:hidden">Apple</span>
            </Button>
          </div>

          <!-- Divider -->
          <div class="relative">
            <div class="absolute inset-0 flex items-center">
              <Separator />
            </div>
            <div class="relative flex justify-center text-xs uppercase">
              <span class="bg-card px-2 text-muted-foreground">{{ t('auth.email') }}</span>
            </div>
          </div>

          <!-- Signup Form -->
          <Form @submit="handleSignup" :validation-schema="validationSchema">
            <div class="space-y-4">
              <Field
                v-slot="{ componentField, errors }"
                name="name"
              >
                <div class="space-y-2">
                  <Label for="name">{{ t('users.name') }}</Label>
                  <Input
                    id="name"
                    type="text"
                    :placeholder="t('users.name')"
                    v-bind="componentField"
                    :class="{ 'border-destructive': errors.length > 0 }"
                  />
                  <ErrorMessage name="name" class="text-sm text-destructive" />
                </div>
              </Field>

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

              <Field
                v-slot="{ componentField, errors }"
                name="password"
              >
                <div class="space-y-2">
                  <Label for="password">{{ t('auth.password') }}</Label>
                  <Input
                    id="password"
                    type="password"
                    :placeholder="t('auth.password')"
                    v-bind="componentField"
                    :class="{ 'border-destructive': errors.length > 0 }"
                  />
                  <ErrorMessage name="password" class="text-sm text-destructive" />
                </div>
              </Field>

              <Field
                v-slot="{ componentField, errors }"
                name="confirmPassword"
              >
                <div class="space-y-2">
                  <Label for="confirmPassword">Confirm Password</Label>
                  <Input
                    id="confirmPassword"
                    type="password"
                    placeholder="Confirm your password"
                    v-bind="componentField"
                    :class="{ 'border-destructive': errors.length > 0 }"
                  />
                  <ErrorMessage name="confirmPassword" class="text-sm text-destructive" />
                </div>
              </Field>

              <Field
                v-slot="{ value, handleChange }"
                name="acceptTerms"
                type="checkbox"
                :rules="{ required: 'Vous devez accepter les conditions' }"
                :value="true"
              >
                <div class="flex items-start space-x-2">
                  <input
                    id="terms"
                    type="checkbox"
                    :checked="value"
                    @change="handleChange"
                    class="mt-1 h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary"
                  />
                  <Label for="terms" class="text-sm font-normal cursor-pointer">
                    I Accept the
                    <a href="#" class="text-primary hover:underline">Terms and Conditions</a>.
                  </Label>
                </div>
                <ErrorMessage name="acceptTerms" class="text-sm text-destructive" />
              </Field>

              <Button type="submit" class="w-full" size="lg" :disabled="loading">
                <Loader2 v-if="loading" class="mr-2 h-4 w-4 animate-spin" />
                <span v-if="!loading">{{ t('auth.createAccount') }}</span>
                <span v-else>{{ t('common.loading') }}</span>
              </Button>
            </div>
          </Form>

          <!-- Sign In Link -->
          <div class="text-center text-sm text-muted-foreground">
            {{ t('auth.notMember') }}
            <RouterLink to="/login" class="text-primary font-medium hover:underline ml-1">
              {{ t('auth.login') }}
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
import { signupSchema } from '@/schemas/auth.schema'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { RouterLink } from 'vue-router'
import { Loader2 } from 'lucide-vue-next'

const { t } = useI18n()

const router = useRouter()
const { toast } = useToast()
const loading = ref(false)
const validationSchema = toTypedSchema(signupSchema)

const handleSignup = async (values: any) => {
  loading.value = true
  try {
    await authService.signup({
      name: values.name,
      email: values.email,
      password: values.password,
      confirmPassword: values.confirmPassword
    })

    toast({
      title: t('messages.success.created'),
      description: t('messages.success.created')
    })

    // Rediriger vers login après 2 secondes
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (error: any) {
    console.error('Signup error:', error)
    
    toast({
      title: t('messages.error.generic'),
      description: error.response?.data?.message || error.message || t('messages.error.generic'),
      variant: 'destructive' as const
    })
  } finally {
    loading.value = false
  }
}

const handleSocialSignup = async (provider: 'google' | 'apple') => {
  loading.value = true
  try {
    // TODO: Implémenter OAuth
    toast({
      title: 'Bientôt disponible',
      description: `Inscription avec ${provider} sera disponible prochainement`
    })
  } catch (error) {
    console.error('Social signup error:', error)
  } finally {
    loading.value = false
  }
}
</script>
