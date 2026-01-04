<template>
  <div class="max-w-6xl mx-auto py-8 px-4">
    <!-- En-tête avec breadcrumbs -->
    <nav class="mb-6" aria-label="Breadcrumb">
      <ol class="flex items-center space-x-2 text-sm text-gray-600">
        <li><router-link to="/" class="hover:text-gray-900">Accueil</router-link></li>
        <li><span aria-hidden="true">/</span></li>
        <li class="text-gray-900 font-medium">À propos</li>
      </ol>
    </nav>

    <header class="mb-12 text-center">
      <h1 class="text-4xl md:text-5xl font-bold text-gray-900 dark:text-white mb-4">
        À propos de Viridial
      </h1>
      <p class="text-xl text-gray-600 dark:text-gray-400 max-w-3xl mx-auto">
        Votre partenaire de confiance pour trouver le bien immobilier de vos rêves en France
      </p>
    </header>
    
    <div class="space-y-12">
      <!-- Notre mission -->
      <section class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-8">
        <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">Notre mission</h2>
        <div class="prose prose-lg max-w-none dark:prose-invert">
          <p class="text-gray-700 dark:text-gray-300 leading-relaxed mb-4">
            <strong>Viridial</strong> est une plateforme immobilière moderne et innovante conçue pour révolutionner 
            la façon dont vous recherchez, comparez et trouvez votre bien immobilier idéal en France. 
            Notre objectif est de rendre l'immobilier accessible, transparent et agréable pour tous.
          </p>
          <p class="text-gray-700 dark:text-gray-300 leading-relaxed">
            Que vous cherchiez un appartement à Paris, une maison à Lyon, une villa sur la Côte d'Azur 
            ou un local commercial, nous mettons à votre disposition des outils puissants et une interface 
            intuitive pour faciliter votre recherche.
          </p>
        </div>
      </section>
      
      <!-- Nos valeurs -->
      <section class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-8">
        <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">Nos valeurs</h2>
        <div class="grid md:grid-cols-2 gap-6">
          <div class="flex items-start gap-4">
            <div class="flex-shrink-0 w-12 h-12 bg-blue-100 dark:bg-blue-900 rounded-lg flex items-center justify-center">
              <span class="text-2xl">🔍</span>
            </div>
            <div>
              <h3 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">Transparence</h3>
              <p class="text-gray-600 dark:text-gray-400">
                Nous croyons en la transparence totale dans toutes nos transactions. 
                Toutes les informations sur les biens sont vérifiées et mises à jour régulièrement.
              </p>
            </div>
          </div>
          
          <div class="flex items-start gap-4">
            <div class="flex-shrink-0 w-12 h-12 bg-green-100 dark:bg-green-900 rounded-lg flex items-center justify-center">
              <span class="text-2xl">💡</span>
            </div>
            <div>
              <h3 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">Innovation</h3>
              <p class="text-gray-600 dark:text-gray-400">
                Nous utilisons les dernières technologies pour offrir une expérience utilisateur 
                exceptionnelle : recherche intelligente, cartes interactives, comparaison de biens.
              </p>
            </div>
          </div>
          
          <div class="flex items-start gap-4">
            <div class="flex-shrink-0 w-12 h-12 bg-purple-100 dark:bg-purple-900 rounded-lg flex items-center justify-center">
              <span class="text-2xl">🤝</span>
            </div>
            <div>
              <h3 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">Accessibilité</h3>
              <p class="text-gray-600 dark:text-gray-400">
                Notre plateforme est accessible à tous, quel que soit votre budget. 
                Nous proposons des biens dans toutes les gammes de prix, de l'économique au luxe.
              </p>
            </div>
          </div>
          
          <div class="flex items-start gap-4">
            <div class="flex-shrink-0 w-12 h-12 bg-red-100 dark:bg-red-900 rounded-lg flex items-center justify-center">
              <span class="text-2xl">🔒</span>
            </div>
            <div>
              <h3 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">Fiabilité</h3>
              <p class="text-gray-600 dark:text-gray-400">
                La sécurité et la fiabilité de vos données sont notre priorité. 
                Nous respectons strictement le RGPD et les réglementations en vigueur.
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- Nos services -->
      <section class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-8">
        <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">Nos services</h2>
        <div class="grid md:grid-cols-3 gap-6">
          <div class="p-6 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-3">🔎 Recherche avancée</h3>
            <p class="text-gray-600 dark:text-gray-400 text-sm">
              Filtres intelligents, recherche par localisation, carte interactive, 
              comparaison de biens et alertes personnalisées.
            </p>
          </div>
          
          <div class="p-6 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-3">📊 Données de marché</h3>
            <p class="text-gray-600 dark:text-gray-400 text-sm">
              Accès aux données DVF (Demandes de Valeurs Foncières) pour connaître 
              les prix du marché et l'évolution des prix dans votre zone.
            </p>
          </div>
          
          <div class="p-6 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-3">💬 Support client</h3>
            <p class="text-gray-600 dark:text-gray-400 text-sm">
              Équipe dédiée disponible pour répondre à vos questions et vous accompagner 
              dans votre recherche immobilière.
            </p>
          </div>
        </div>
      </section>

      <!-- Statistiques -->
      <section class="bg-gradient-to-r from-blue-600 to-indigo-600 rounded-lg shadow-lg p-8 text-white">
        <h2 class="text-3xl font-semibold mb-8 text-center">Viridial en chiffres</h2>
        <div class="grid md:grid-cols-4 gap-6 text-center">
          <div>
            <div class="text-4xl font-bold mb-2">10K+</div>
            <div class="text-blue-100">Biens immobiliers</div>
          </div>
          <div>
            <div class="text-4xl font-bold mb-2">500+</div>
            <div class="text-blue-100">Villes couvertes</div>
          </div>
          <div>
            <div class="text-4xl font-bold mb-2">50K+</div>
            <div class="text-blue-100">Visiteurs mensuels</div>
          </div>
          <div>
            <div class="text-4xl font-bold mb-2">4.8/5</div>
            <div class="text-blue-100">Note moyenne</div>
          </div>
        </div>
      </section>

      <!-- Contact -->
      <section class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-8">
        <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">Contactez-nous</h2>
        <div class="grid md:grid-cols-2 gap-6">
          <div>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Pour toute question, suggestion ou demande d'information, notre équipe est à votre disposition.
            </p>
            <div class="space-y-3">
              <div class="flex items-center gap-3">
                <span class="text-2xl">📧</span>
                <a href="mailto:contact@viridial.com" class="text-blue-600 dark:text-blue-400 hover:underline">
                  contact@viridial.com
                </a>
              </div>
              <div class="flex items-center gap-3">
                <span class="text-2xl">📞</span>
                <a href="tel:+33123456789" class="text-blue-600 dark:text-blue-400 hover:underline">
                  +33 1 23 45 67 89
                </a>
              </div>
              <div class="flex items-center gap-3">
                <span class="text-2xl">📍</span>
                <span class="text-gray-700 dark:text-gray-300">France - Service disponible partout</span>
              </div>
            </div>
          </div>
          <div>
            <router-link
              to="/contact"
              class="inline-block px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors"
            >
              Envoyer un message
            </router-link>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useSEO, generateSiteStructuredData } from '@/composables/useSEO'

const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'

onMounted(() => {
  // SEO optimisé pour la page About
  useSEO({
    title: 'À propos de Viridial - Plateforme Immobilière en France',
    description: 'Découvrez Viridial, votre plateforme immobilière de confiance en France. Recherche avancée, données de marché DVF, support client dédié. Plus de 10 000 biens immobiliers disponibles.',
    keywords: ['Viridial', 'à propos', 'plateforme immobilière', 'immobilier France', 'recherche immobilière', 'achat immobilier', 'location immobilier'],
    type: 'website',
    canonical: `${siteUrl}/about`,
    url: `${siteUrl}/about`
  })

  // Ajouter les structured data Organization
  if (typeof document !== 'undefined') {
    const existingScript = document.querySelector('script[data-about-structured-data]')
    if (!existingScript) {
      const siteStructuredData = generateSiteStructuredData(siteUrl)
      const script = document.createElement('script')
      script.type = 'application/ld+json'
      script.setAttribute('data-about-structured-data', 'true')
      script.textContent = JSON.stringify(siteStructuredData, null, 2)
      document.head.appendChild(script)
    }
  }
})
</script>

