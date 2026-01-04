<template>
  <footer class="bg-white border-t border-gray-200 mt-auto">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <!-- Logo & Description -->
        <div class="col-span-1 md:col-span-2">
          <div class="flex items-center gap-2 mb-4">
            <div class="h-8 w-8 rounded bg-blue-600 flex items-center justify-center">
              <Home class="h-5 w-5 text-white" />
            </div>
            <span class="text-lg font-medium text-gray-900">Viridial</span>
          </div>
          <p class="text-sm text-gray-600 max-w-md leading-relaxed">
            Votre plateforme immobilière moderne pour trouver votre bien idéal en France. 
            Recherche avancée, données de marché DVF, plus de 10 000 annonces vérifiées.
          </p>
        </div>

        <!-- Liens rapides -->
        <div>
          <h3 class="text-sm font-medium text-gray-900 mb-4">Liens rapides</h3>
          <ul class="space-y-3">
            <li>
              <router-link to="/search" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <Search class="h-3 w-3" />
                Rechercher un bien
              </router-link>
            </li>
            <li>
              <router-link to="/publish" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <Plus class="h-3 w-3" />
                Publier une annonce
              </router-link>
            </li>
            <li>
              <router-link to="/guide-achat" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <BookOpen class="h-3 w-3" />
                Guide d'achat
              </router-link>
            </li>
            <li>
              <router-link to="/about" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <Info class="h-3 w-3" />
                À propos
              </router-link>
            </li>
            <li>
              <router-link to="/agencies" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <Building2 class="h-3 w-3" />
                Recherche d'agences
              </router-link>
            </li>
          </ul>
        </div>

        <!-- Support -->
        <div>
          <h3 class="text-sm font-medium text-gray-900 mb-4">Support</h3>
          <ul class="space-y-3">
            <li>
              <router-link to="/faq" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <HelpCircle class="h-3 w-3" />
                FAQ
              </router-link>
            </li>
            <li>
              <router-link to="/contact" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <Mail class="h-3 w-3" />
                Contact
              </router-link>
            </li>
            <li>
              <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <Shield class="h-3 w-3" />
                Confidentialité
              </a>
            </li>
            <li>
              <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2">
                <FileText class="h-3 w-3" />
                Mentions légales
              </a>
            </li>
          </ul>
        </div>
      </div>

      <!-- Annonces publicitaires -->
      <div class="mt-8 pt-8 border-t border-gray-200">
        <AdvertisementSlot
          ad-type="BANNER"
          position="FOOTER"
          page-type="FOOTER"
        />
      </div>

      <!-- Copyright style Google -->
      <div class="mt-8 pt-8 border-t border-gray-200">
        <div class="flex flex-col sm:flex-row justify-between items-center gap-4">
          <p class="text-sm text-gray-600">
            © {{ currentYear }} Viridial. Tous droits réservés.
          </p>
          <div class="flex items-center gap-4">
            <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors">
              Mentions légales
            </a>
            <span class="text-gray-300">·</span>
            <a href="#" class="text-sm text-gray-600 hover:text-gray-900 transition-colors">
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
import { Home, Search, Plus, BookOpen, Info, Building2, HelpCircle, Mail, Shield, FileText } from 'lucide-vue-next'
import AdvertisementSlot from './AdvertisementSlot.vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

