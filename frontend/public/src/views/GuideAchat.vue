<template>
  <div class="max-w-6xl mx-auto py-8 px-4">
    <!-- Breadcrumbs -->
    <nav class="mb-6" aria-label="Breadcrumb">
      <ol class="flex items-center space-x-2 text-sm text-gray-600 dark:text-gray-400">
        <li><router-link to="/" class="hover:text-gray-900 dark:hover:text-white">Accueil</router-link></li>
        <li><span aria-hidden="true">/</span></li>
        <li class="text-gray-900 dark:text-white font-medium">Guide d'achat</li>
      </ol>
    </nav>

    <header class="mb-12 text-center">
      <h1 class="text-4xl md:text-5xl font-bold text-gray-900 dark:text-white mb-4">
        Guide complet pour acheter un bien immobilier
      </h1>
      <p class="text-xl text-gray-600 dark:text-gray-400 max-w-3xl mx-auto">
        Toutes les étapes et conseils pour réussir votre achat immobilier en France
      </p>
    </header>

    <!-- Table des matières -->
    <div class="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-6 mb-12">
      <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-4">Table des matières</h2>
      <nav class="space-y-2">
        <a
          v-for="section in sections"
          :key="section.id"
          :href="`#${section.id}`"
          class="block text-blue-600 dark:text-blue-400 hover:underline"
        >
          {{ section.title }}
        </a>
      </nav>
    </div>

    <!-- Contenu du guide -->
    <div class="space-y-12">
      <!-- Étape 1 : Préparer son projet -->
      <section :id="sections[0].id" class="scroll-mt-20">
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-8">
          <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">
            {{ sections[0].title }}
          </h2>
          <div class="prose prose-lg max-w-none dark:prose-invert">
            <h3 class="text-xl font-semibold mb-4">1. Définir son budget</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Avant de commencer vos recherches, il est essentiel de déterminer votre budget réel. 
              Prenez en compte :
            </p>
            <ul class="list-disc list-inside space-y-2 text-gray-700 dark:text-gray-300 mb-6">
              <li>Vos revenus et ceux de votre co-emprunteur</li>
              <li>Vos charges mensuelles existantes</li>
              <li>Votre apport personnel (idéalement 10-20% du prix d'achat)</li>
              <li>Les frais de notaire (7-8% pour l'ancien, 2-3% pour le neuf)</li>
              <li>Les frais d'agence (si applicable)</li>
              <li>Les travaux éventuels</li>
            </ul>

            <h3 class="text-xl font-semibold mb-4">2. Obtenir un accord de principe</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Un accord de principe bancaire valide votre capacité d'emprunt et renforce votre position 
              lors des négociations. Il est généralement valable 2 à 4 mois.
            </p>

            <h3 class="text-xl font-semibold mb-4">3. Choisir la localisation</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              La localisation est un critère essentiel. Considérez :
            </p>
            <ul class="list-disc list-inside space-y-2 text-gray-700 dark:text-gray-300">
              <li>La proximité de votre lieu de travail</li>
              <li>Les transports en commun</li>
              <li>Les écoles et commerces à proximité</li>
              <li>L'évolution du quartier</li>
              <li>Les données de marché (prix au m², évolution)</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- Étape 2 : Rechercher un bien -->
      <section :id="sections[1].id" class="scroll-mt-20">
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-8">
          <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">
            {{ sections[1].title }}
          </h2>
          <div class="prose prose-lg max-w-none dark:prose-invert">
            <h3 class="text-xl font-semibold mb-4">1. Utiliser les outils de recherche</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Sur Viridial, utilisez nos filtres avancés pour affiner votre recherche :
            </p>
            <ul class="list-disc list-inside space-y-2 text-gray-700 dark:text-gray-300 mb-6">
              <li>Filtres par prix, surface, nombre de pièces</li>
              <li>Recherche par localisation (carte interactive)</li>
              <li>Type de bien (appartement, maison, villa, etc.)</li>
              <li>Critères spécifiques (balcon, parking, ascenseur, etc.)</li>
            </ul>

            <h3 class="text-xl font-semibold mb-4">2. Analyser les données de marché</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Consultez les données DVF (Demandes de Valeurs Foncières) pour chaque bien :
            </p>
            <ul class="list-disc list-inside space-y-2 text-gray-700 dark:text-gray-300 mb-6">
              <li>Prix moyen au m² dans la zone</li>
              <li>Évolution des prix sur les 2 dernières années</li>
              <li>Comparaison avec le marché</li>
              <li>Transactions similaires récentes</li>
            </ul>

            <h3 class="text-xl font-semibold mb-4">3. Créer des alertes</h3>
            <p class="text-gray-700 dark:text-gray-300">
              Configurez des alertes personnalisées pour être notifié dès qu'un bien correspondant 
              à vos critères est publié.
            </p>
          </div>
        </div>
      </section>

      <!-- Étape 3 : Visiter et négocier -->
      <section :id="sections[2].id" class="scroll-mt-20">
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-8">
          <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">
            {{ sections[2].title }}
          </h2>
          <div class="prose prose-lg max-w-none dark:prose-invert">
            <h3 class="text-xl font-semibold mb-4">1. Organiser les visites</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Réservez vos visites directement sur Viridial. Préparez une liste de questions :
            </p>
            <ul class="list-disc list-inside space-y-2 text-gray-700 dark:text-gray-300 mb-6">
              <li>État général du bien et travaux nécessaires</li>
              <li>Charges de copropriété (pour appartement)</li>
              <li>Règles de copropriété (animaux, travaux, etc.)</li>
              <li>Historique des prix et durée de mise en vente</li>
              <li>Raisons de la vente</li>
            </ul>

            <h3 class="text-xl font-semibold mb-4">2. Négocier le prix</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              La négociation est possible, surtout si :
            </p>
            <ul class="list-disc list-inside space-y-2 text-gray-700 dark:text-gray-300 mb-6">
              <li>Le bien est sur le marché depuis plusieurs mois</li>
              <li>Des travaux sont nécessaires</li>
              <li>Le prix est au-dessus du marché (vérifiez avec les données DVF)</li>
              <li>Vous avez un financement solide</li>
            </ul>

            <h3 class="text-xl font-semibold mb-4">3. Faire une offre</h3>
            <p class="text-gray-700 dark:text-gray-300">
              Une fois décidé, faites une offre écrite avec conditions suspensives 
              (obtention du prêt, diagnostic technique, etc.).
            </p>
          </div>
        </div>
      </section>

      <!-- Étape 4 : Finaliser l'achat -->
      <section :id="sections[3].id" class="scroll-mt-20">
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-8">
          <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">
            {{ sections[3].title }}
          </h2>
          <div class="prose prose-lg max-w-none dark:prose-invert">
            <h3 class="text-xl font-semibold mb-4">1. Signer le compromis de vente</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Le compromis engage les deux parties. Il doit inclure :
            </p>
            <ul class="list-disc list-inside space-y-2 text-gray-700 dark:text-gray-300 mb-6">
              <li>Le prix de vente</li>
              <li>Les conditions suspensives</li>
              <li>La date de signature définitive</li>
              <li>Les modalités de paiement</li>
            </ul>

            <h3 class="text-xl font-semibold mb-4">2. Réaliser les diagnostics</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Le vendeur doit fournir plusieurs diagnostics obligatoires :
            </p>
            <ul class="list-disc list-inside space-y-2 text-gray-700 dark:text-gray-300 mb-6">
              <li>Diagnostic de performance énergétique (DPE)</li>
              <li>Diagnostic plomb, amiante, termites</li>
              <li>État des risques naturels et technologiques (ERNMT)</li>
              <li>Diagnostic électricité et gaz (si applicable)</li>
            </ul>

            <h3 class="text-xl font-semibold mb-4">3. Finaliser le prêt</h3>
            <p class="text-gray-700 dark:text-gray-300 mb-4">
              Votre banque va étudier votre dossier et valider le prêt. 
              Une fois l'offre de prêt reçue, vous avez 10 jours pour l'accepter.
            </p>

            <h3 class="text-xl font-semibold mb-4">4. Signer l'acte de vente</h3>
            <p class="text-gray-700 dark:text-gray-300">
              L'acte de vente est signé chez le notaire. Vous devenez propriétaire 
              et recevez les clés du bien.
            </p>
          </div>
        </div>
      </section>

      <!-- Conseils supplémentaires -->
      <section class="bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 rounded-lg p-8">
        <h2 class="text-3xl font-semibold text-gray-900 dark:text-white mb-6">
          💡 Conseils supplémentaires
        </h2>
        <div class="grid md:grid-cols-2 gap-6">
          <div class="bg-white dark:bg-gray-800 rounded-lg p-6">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-3">
              ⏱️ Prenez votre temps
            </h3>
            <p class="text-gray-700 dark:text-gray-300 text-sm">
              Ne vous précipitez pas. Visitez plusieurs biens, comparez les prix avec les données de marché, 
              et n'hésitez pas à négocier.
            </p>
          </div>
          <div class="bg-white dark:bg-gray-800 rounded-lg p-6">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-3">
              📋 Faites-vous accompagner
            </h3>
            <p class="text-gray-700 dark:text-gray-300 text-sm">
              Un notaire, un courtier ou un agent immobilier peut vous guider dans les démarches 
              et vous éviter des erreurs coûteuses.
            </p>
          </div>
          <div class="bg-white dark:bg-gray-800 rounded-lg p-6">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-3">
              🔍 Vérifiez tout
            </h3>
            <p class="text-gray-700 dark:text-gray-300 text-sm">
              Vérifiez les diagnostics, l'état du bien, les charges, les règles de copropriété. 
              Un bien qui semble trop beau pour être vrai mérite une attention particulière.
            </p>
          </div>
          <div class="bg-white dark:bg-gray-800 rounded-lg p-6">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-3">
              💰 Prévoyez une marge
            </h3>
            <p class="text-gray-700 dark:text-gray-300 text-sm">
              Prévoyez toujours une marge de sécurité pour les imprévus : travaux, frais supplémentaires, 
              ou baisse de revenus.
            </p>
          </div>
        </div>
      </section>
    </div>

    <!-- CTA -->
    <div class="mt-12 bg-blue-600 rounded-lg shadow-lg p-8 text-center text-white">
      <h2 class="text-2xl font-semibold mb-4">Prêt à commencer votre recherche ?</h2>
      <p class="mb-6 text-blue-100">
        Utilisez nos outils de recherche avancés et les données de marché pour trouver votre bien idéal.
      </p>
      <router-link
        to="/search"
        class="inline-block px-6 py-3 bg-white text-blue-600 font-semibold rounded-lg hover:bg-gray-100 transition-colors"
      >
        Commencer la recherche
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useSEO } from '@/composables/useSEO'

const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'

const sections = [
  { id: 'preparer-projet', title: 'Étape 1 : Préparer son projet' },
  { id: 'rechercher-bien', title: 'Étape 2 : Rechercher un bien' },
  { id: 'visiter-negocier', title: 'Étape 3 : Visiter et négocier' },
  { id: 'finaliser-achat', title: 'Étape 4 : Finaliser l\'achat' }
]

onMounted(() => {
  // SEO optimisé pour la page Guide d'achat
  useSEO({
    title: 'Guide d\'achat immobilier - Comment acheter un bien en France | Viridial',
    description: 'Guide complet pour acheter un bien immobilier en France : préparer son projet, rechercher un bien, négocier, finaliser l\'achat. Conseils pratiques et étapes détaillées.',
    keywords: ['guide achat immobilier', 'acheter appartement', 'acheter maison', 'achat immobilier France', 'conseils achat immobilier', 'étapes achat immobilier'],
    type: 'article',
    canonical: `${siteUrl}/guide-achat`,
    url: `${siteUrl}/guide-achat`,
    article: {
      publishedTime: '2024-01-01T00:00:00Z',
      modifiedTime: new Date().toISOString(),
      author: 'Viridial',
      section: 'Guide',
      tags: ['immobilier', 'achat', 'guide', 'conseils']
    }
  })
})
</script>

