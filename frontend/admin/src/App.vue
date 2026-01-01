<template>
  <RouterView />
  <Toaster />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import { tokenUtils } from '@viridial/shared'
import { authMiddleware } from './middleware/auth.middleware'
import { Toaster } from '@/components/ui/toast'

const router = useRouter()

onMounted(async () => {
  // Vérifier l'authentification au démarrage
  if (tokenUtils.hasToken()) {
    try {
      const isAuthenticated = await authMiddleware()
      if (!isAuthenticated) {
        // Token invalide, rediriger vers login
        router.push({ name: 'login' })
      }
    } catch (error) {
      console.error('Auth check failed on mount:', error)
      router.push({ name: 'login' })
    }
  }
})
</script>
