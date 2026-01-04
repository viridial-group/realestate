<template>
  <div class="max-w-6xl mx-auto py-8 px-4">
    <!-- Breadcrumbs -->
    <nav class="mb-6" aria-label="Breadcrumb">
      <ol class="flex items-center space-x-2 text-sm text-gray-600">
        <li><router-link to="/" class="hover:text-gray-900">Accueil</router-link></li>
        <li><span aria-hidden="true">/</span></li>
        <li class="text-gray-900 font-medium">Aide & Support</li>
      </ol>
    </nav>

    <header class="mb-12 text-center">
      <h1 class="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
        Aide & Support
      </h1>
      <p class="text-xl text-gray-600 max-w-3xl mx-auto">
        Nous sommes là pour vous aider. Trouvez les réponses à vos questions ou contactez notre équipe.
      </p>
    </header>

    <!-- Sections d'aide -->
    <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
      <!-- FAQ -->
      <router-link
        to="/faq"
        class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow border border-gray-200 hover:border-blue-500"
      >
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-blue-100 rounded-lg">
            <HelpCircle class="h-6 w-6 text-blue-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900">Questions fréquentes</h3>
        </div>
        <p class="text-gray-600 text-sm">
          Consultez les réponses aux questions les plus courantes sur nos services.
        </p>
      </router-link>

      <!-- Contact -->
      <router-link
        to="/contact"
        class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow border border-gray-200 hover:border-blue-500"
      >
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-green-100 rounded-lg">
            <Mail class="h-6 w-6 text-green-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900">Nous contacter</h3>
        </div>
        <p class="text-gray-600 text-sm">
          Envoyez-nous un message et notre équipe vous répondra dans les plus brefs délais.
        </p>
      </router-link>

      <!-- Guide d'achat -->
      <router-link
        to="/guide-achat"
        class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow border border-gray-200 hover:border-blue-500"
      >
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-purple-100 rounded-lg">
            <BookOpen class="h-6 w-6 text-purple-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900">Guide d'achat</h3>
        </div>
        <p class="text-gray-600 text-sm">
          Découvrez nos conseils et guides pour réussir votre projet immobilier.
        </p>
      </router-link>

      <!-- Publier une annonce -->
      <router-link
        to="/publish"
        class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow border border-gray-200 hover:border-blue-500"
      >
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-orange-100 rounded-lg">
            <Plus class="h-6 w-6 text-orange-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900">Publier une annonce</h3>
        </div>
        <p class="text-gray-600 text-sm">
          Apprenez comment publier et gérer vos annonces immobilières.
        </p>
      </router-link>

      <!-- À propos -->
      <router-link
        to="/about"
        class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow border border-gray-200 hover:border-blue-500"
      >
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-indigo-100 rounded-lg">
            <Info class="h-6 w-6 text-indigo-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900">À propos</h3>
        </div>
        <p class="text-gray-600 text-sm">
          Découvrez notre plateforme et notre mission.
        </p>
      </router-link>

      <!-- Blog -->
      <router-link
        to="/blog"
        class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow border border-gray-200 hover:border-blue-500"
      >
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-pink-100 rounded-lg">
            <FileText class="h-6 w-6 text-pink-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900">Blog</h3>
        </div>
        <p class="text-gray-600 text-sm">
          Consultez nos articles et actualités sur l'immobilier.
        </p>
      </router-link>
    </div>

    <!-- Section contact rapide -->
    <div class="bg-blue-50 rounded-lg p-8 mb-12">
      <h2 class="text-2xl font-semibold text-gray-900 mb-4">
        Besoin d'aide supplémentaire ?
      </h2>
      <p class="text-gray-600 mb-6">
        Notre équipe de support est disponible pour répondre à toutes vos questions.
      </p>
      <div class="flex flex-wrap gap-4">
        <router-link
          to="/contact"
          class="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors font-medium"
        >
          Contacter le support
        </router-link>
        <a
          href="mailto:support@example.com"
          class="px-6 py-3 bg-white text-blue-600 border border-blue-600 rounded-md hover:bg-blue-50 transition-colors font-medium"
        >
          Envoyer un email
        </a>
      </div>
    </div>

    <!-- Ressources utiles -->
    <div class="mb-12">
      <h2 class="text-2xl font-semibold text-gray-900 mb-6">
        Ressources utiles
      </h2>
      <div class="grid md:grid-cols-2 gap-6">
        <div class="bg-white rounded-lg shadow-sm p-6 border border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">
            Pour les particuliers
          </h3>
          <ul class="space-y-2 text-gray-600">
            <li>• <router-link to="/my-properties" class="hover:text-blue-600">Gérer mes annonces</router-link></li>
            <li>• <router-link to="/dashboard" class="hover:text-blue-600">Tableau de bord</router-link></li>
            <li>• <router-link to="/my-messages" class="hover:text-blue-600">Mes messages</router-link></li>
            <li>• <router-link to="/profile" class="hover:text-blue-600">Mon profil</router-link></li>
          </ul>
        </div>
        <div class="bg-white rounded-lg shadow-sm p-6 border border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">
            Informations légales
          </h3>
          <ul class="space-y-2 text-gray-600">
            <li>• <a href="#" class="hover:text-blue-600">Mentions légales</a></li>
            <li>• <a href="#" class="hover:text-blue-600">Politique de confidentialité</a></li>
            <li>• <a href="#" class="hover:text-blue-600">Conditions d'utilisation</a></li>
            <li>• <a href="#" class="hover:text-blue-600">CGV</a></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { HelpCircle, Mail, BookOpen, Plus, Info, FileText } from 'lucide-vue-next'
</script>

