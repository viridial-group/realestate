<template>
  <div id="app">
    <Header />
    <main class="min-h-screen bg-[#f8f9fd]">
      <RouterView v-slot="{ Component, route }">
        <UserLayout v-if="route.meta?.layout === 'user'">
          <component :is="Component" />
        </UserLayout>
        <component v-else :is="Component" />
      </RouterView>
    </main>
    <Footer />
    <ComparisonPanel />
    <FavoritesPanel />
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import ToastContainer from './components/ToastContainer.vue'
import ComparisonPanel from './components/ComparisonPanel.vue'
import FavoritesPanel from './components/FavoritesPanel.vue'
import UserLayout from './layouts/UserLayout.vue'
</script>
