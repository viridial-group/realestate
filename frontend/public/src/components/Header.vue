<template>
  <header class="bg-white border-b border-gray-200 sticky top-0 z-50 shadow-sm">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-14">
        <!-- Logo -->
        <div class="flex items-center gap-3">
          <router-link to="/" class="flex items-center gap-2 hover:opacity-80 transition-opacity">
            <div class="h-8 w-8 rounded bg-blue-600 flex items-center justify-center">
              <Home class="h-5 w-5 text-white" />
            </div>
            <span class="text-lg font-medium text-gray-900">RealEstate</span>
          </router-link>
        </div>

        <!-- Navigation -->
        <nav class="hidden md:flex items-center gap-1">
          <router-link
            to="/search"
            class="px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
            active-class="text-blue-600 bg-blue-50"
          >
            Recherche
          </router-link>
          <router-link
            to="/publish"
            class="px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
            active-class="text-blue-600 bg-blue-50"
          >
            Publier
          </router-link>
          <router-link
            to="/about"
            class="px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
            active-class="text-blue-600 bg-blue-50"
          >
            À propos
          </router-link>
          <router-link
            to="/agencies"
            class="px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
            active-class="text-blue-600 bg-blue-50"
          >
            Agences
          </router-link>
          <router-link
            to="/subscribe"
            class="px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
            active-class="text-blue-600 bg-blue-50"
          >
            S'abonner
          </router-link>
        </nav>

        <!-- Actions -->
        <div class="flex items-center gap-2">
          <DarkModeToggle />
          <router-link
            to="/compare"
            class="hidden sm:flex items-center gap-1.5 px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors relative"
            active-class="text-blue-600 bg-blue-50"
          >
            <Scale class="h-4 w-4" />
            <span>Comparer</span>
            <ComparisonBadge />
          </router-link>
          <router-link
            to="/favorites"
            class="hidden sm:flex items-center gap-1.5 px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
            active-class="text-blue-600 bg-blue-50"
          >
            <Star class="h-4 w-4" />
            <span>Favoris</span>
          </router-link>
          <button
            class="hidden sm:flex items-center gap-1.5 px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
          >
            <HelpCircle class="h-4 w-4" />
            <span>Aide</span>
          </button>
          <button
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm font-medium shadow-sm"
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
import { Home, Scale, Star, HelpCircle } from 'lucide-vue-next'
import ComparisonBadge from './ComparisonBadge.vue'
import DarkModeToggle from './DarkModeToggle.vue'
</script>

