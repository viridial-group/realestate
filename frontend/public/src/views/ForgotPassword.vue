<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-900 dark:to-slate-800 p-4">
    <div class="w-full max-w-md">
      <!-- Logo Section -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-primary rounded-xl mb-4">
          <span class="text-2xl font-bold text-primary-foreground">RE</span>
        </div>
        <h1 class="text-3xl font-bold text-foreground mb-2">Forgot your password?</h1>
        <p class="text-muted-foreground">Enter your Email and instructions will be sent to you!</p>
      </div>

      <!-- Card -->
      <Card class="shadow-lg border-0">
        <CardContent class="p-6 space-y-6">
          <!-- Reset Form -->
          <form @submit.prevent="handleReset" class="space-y-4">
            <div class="space-y-2">
              <Label for="email">Email</Label>
              <Input
                id="email"
                v-model="form.email"
                type="email"
                placeholder="Enter your email"
                required
              />
            </div>

            <Button type="submit" class="w-full" size="lg" :disabled="loading">
              <span v-if="!loading">Email password reset link</span>
              <span v-else>Sending...</span>
            </Button>
          </form>

          <!-- Back to Login Link -->
          <div class="text-center">
            <RouterLink
              to="/login"
              class="text-sm text-primary font-medium hover:underline"
            >
              ← Back to home
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
import { useRouter } from 'vue-router'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { RouterLink } from 'vue-router'

const router = useRouter()
const loading = ref(false)

const form = ref({
  email: ''
})

const handleReset = async () => {
  loading.value = true
  try {
    // TODO: Implement password reset logic with API
    console.log('Password reset:', form.value)
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    alert('Password reset link has been sent to your email!')
    router.push('/login')
  } catch (error) {
    console.error('Reset error:', error)
  } finally {
    loading.value = false
  }
}
</script>

