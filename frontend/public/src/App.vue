<template>
  <div id="app">
    <Header />
    <main class="min-h-screen bg-[#f8f9fd]">
      <RouterView />
    </main>
    <Footer />
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import ToastContainer from './components/ToastContainer.vue'
</script>
