<template>
  <footer class="bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-800 mt-auto">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <!-- Logo & Description -->
        <div class="col-span-1 md:col-span-2">
          <div class="flex items-center gap-2 mb-4">
            <div class="h-8 w-8 rounded-lg bg-gradient-to-br from-blue-600 to-purple-600 flex items-center justify-center">
              <span class="text-lg">🏠</span>
            </div>
            <span class="text-lg font-semibold text-gray-900 dark:text-white">RealEstate</span>
          </div>
          <p class="text-sm text-gray-600 dark:text-gray-400 max-w-md">
            La plateforme immobilière moderne pour trouver votre propriété idéale. 
            Des milliers d'annonces vérifiées à votre disposition.
          </p>
        </div>

        <!-- Liens rapides -->
        <div>
          <h3 class="text-sm font-semibold text-gray-900 dark:text-white mb-4">Liens rapides</h3>
          <ul class="space-y-2">
            <li>
              <a href="#" class="text-sm text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors">
                Rechercher
              </a>
            </li>
            <li>
              <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors">
                Publier une annonce
              </a>
            </li>
            <li>
              <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors">
                Comment ça marche
              </a>
            </li>
            <li>
              <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors">
                Tarifs
              </a>
            </li>
          </ul>
        </div>

        <!-- Support -->
        <div>
          <h3 class="text-sm font-semibold text-gray-900 dark:text-white mb-4">Support</h3>
          <ul class="space-y-2">
            <li>
              <a href="#" class="text-sm text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors">
                Aide & FAQ
              </a>
            </li>
            <li>
              <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors">
                Contact
              </a>
            </li>
            <li>
              <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors">
                Confidentialité
              </a>
            </li>
            <li>
              <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors">
                Conditions d'utilisation
              </a>
            </li>
          </ul>
        </div>
      </div>

      <!-- Copyright -->
      <div class="mt-8 pt-8 border-t border-gray-200 dark:border-gray-800">
        <div class="flex flex-col sm:flex-row justify-between items-center gap-4">
          <p class="text-sm text-gray-600 dark:text-gray-400">
            © {{ currentYear }} RealEstate. Tous droits réservés.
          </p>
          <div class="flex items-center gap-6">
            <a href="#" class="text-sm text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors">
              Mentions légales
            </a>
            <a href="#" class="text-sm text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors">
              Cookies
            </a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

