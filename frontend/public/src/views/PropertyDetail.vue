<template>
  <div class="min-h-screen bg-background p-8">
    <h1 class="text-3xl font-bold mb-8">Détails de la Propriété</h1>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
const propertyId = route.params.id
</script>

