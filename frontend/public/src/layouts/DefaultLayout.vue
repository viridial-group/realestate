<template>
    <div class="min-h-screen bg-[#f8f9fd] dark:bg-gray-900 flex flex-col">
      <Header />
  
      <main class="flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-6">
        <slot />
      </main>
  
      <Footer />
      <ToastContainer />
    </div>
  </template>

<script setup lang="ts">
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import ToastContainer from '@/components/ToastContainer.vue'
</script>
  