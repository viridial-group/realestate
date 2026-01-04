<template>
  <header class="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 shadow-sm sticky top-0 z-50 backdrop-blur-sm bg-white/95 dark:bg-gray-900/95">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <div class="flex items-center gap-3">
          <router-link to="/" class="flex items-center gap-2 hover:opacity-80 transition-opacity">
            <div class="h-10 w-10 rounded-lg bg-gradient-to-br from-blue-600 to-purple-600 flex items-center justify-center">
              <span class="text-xl">🏠</span>
            </div>
            <span class="text-xl font-semibold text-gray-900 dark:text-white">RealEstate</span>
          </router-link>
        </div>

        <!-- Navigation -->
        <nav class="hidden md:flex items-center gap-6">
              <router-link
                to="/search"
                class="text-sm font-medium text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
                active-class="text-blue-600 dark:text-blue-400"
              >
                Recherche
              </router-link>
          <router-link
            to="/publish"
            class="text-sm font-medium text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
            active-class="text-blue-600 dark:text-blue-400"
          >
            Publier
          </router-link>
          <router-link
            to="/about"
            class="text-sm font-medium text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
            active-class="text-blue-600 dark:text-blue-400"
          >
            À propos
          </router-link>
          <router-link
            to="/agencies"
            class="text-sm font-medium text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
            active-class="text-blue-600 dark:text-blue-400"
          >
            Agences
          </router-link>
          <router-link
            to="/subscribe"
            class="text-sm font-medium text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
            active-class="text-blue-600 dark:text-blue-400"
          >
            S'abonner
          </router-link>
        </nav>

        <!-- Actions -->
        <div class="flex items-center gap-3">
          <DarkModeToggle />
          <router-link
            to="/compare"
            class="hidden sm:flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors relative"
            active-class="text-blue-600 dark:text-blue-400"
          >
            <span>⚖️</span>
            <span>Comparer</span>
            <ComparisonBadge />
          </router-link>
          <router-link
            to="/favorites"
            class="hidden sm:flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
            active-class="text-blue-600 dark:text-blue-400"
          >
            <span>⭐</span>
            <span>Favoris</span>
          </router-link>
          <button
            class="hidden sm:flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors"
          >
            <span>🔍</span>
            <span>Aide</span>
          </button>
          <button
            class="px-4 py-2 bg-blue-600 dark:bg-blue-500 text-white rounded-lg hover:bg-blue-700 dark:hover:bg-blue-600 transition-colors text-sm font-medium"
          >
            Connexion
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import ComparisonBadge from './ComparisonBadge.vue'
import DarkModeToggle from './DarkModeToggle.vue'
</script>

