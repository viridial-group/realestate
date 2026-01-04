<template>
  <div class="max-w-6xl mx-auto py-8 px-4">
    <!-- Breadcrumbs -->
    <nav class="mb-6" aria-label="Breadcrumb">
      <ol class="flex items-center space-x-2 text-sm text-gray-600 dark:text-gray-400">
        <li><router-link to="/" class="hover:text-gray-900 dark:hover:text-white">Accueil</router-link></li>
        <li><span aria-hidden="true">/</span></li>
        <li class="text-gray-900 dark:text-white font-medium">FAQ</li>
      </ol>
    </nav>

    <header class="mb-12 text-center">
      <h1 class="text-4xl md:text-5xl font-bold text-gray-900 dark:text-white mb-4">
        Questions fréquentes
      </h1>
      <p class="text-xl text-gray-600 dark:text-gray-400 max-w-3xl mx-auto">
        Trouvez rapidement des réponses aux questions les plus courantes sur nos services immobiliers
      </p>
    </header>

    <!-- Recherche dans la FAQ -->
    <div class="mb-8 max-w-2xl mx-auto">
      <div class="relative">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Rechercher dans la FAQ..."
          class="w-full px-4 py-3 pl-12 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-800 dark:text-white"
        />
        <span class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400">🔍</span>
      </div>
    </div>

    <!-- Catégories de FAQ -->
    <div class="space-y-12">
      <!-- Recherche de biens -->
      <section>
        <h2 class="text-2xl font-semibold text-gray-900 dark:text-white mb-6">
          🔍 Recherche de biens
        </h2>
        <FAQ
          :items="filteredFAQItems(searchQuery, 'search')"
          id="search-faq"
        />
      </section>

      <!-- Achat immobilier -->
      <section>
        <h2 class="text-2xl font-semibold text-gray-900 dark:text-white mb-6">
          🏠 Achat immobilier
        </h2>
        <FAQ
          :items="filteredFAQItems(searchQuery, 'purchase')"
          id="purchase-faq"
        />
      </section>

      <!-- Location -->
      <section>
        <h2 class="text-2xl font-semibold text-gray-900 dark:text-white mb-6">
          🏡 Location
        </h2>
        <FAQ
          :items="filteredFAQItems(searchQuery, 'rental')"
          id="rental-faq"
        />
      </section>

      <!-- Compte et profil -->
      <section>
        <h2 class="text-2xl font-semibold text-gray-900 dark:text-white mb-6">
          👤 Compte et profil
        </h2>
        <FAQ
          :items="filteredFAQItems(searchQuery, 'account')"
          id="account-faq"
        />
      </section>

      <!-- Données de marché -->
      <section>
        <h2 class="text-2xl font-semibold text-gray-900 dark:text-white mb-6">
          📊 Données de marché (DVF)
        </h2>
        <FAQ
          :items="filteredFAQItems(searchQuery, 'market')"
          id="market-faq"
        />
      </section>

      <!-- Support technique -->
      <section>
        <h2 class="text-2xl font-semibold text-gray-900 dark:text-white mb-6">
          🛠️ Support technique
        </h2>
        <FAQ
          :items="filteredFAQItems(searchQuery, 'technical')"
          id="technical-faq"
        />
      </section>
    </div>

    <!-- Section contact si pas de réponse -->
    <div class="mt-12 bg-blue-50 dark:bg-blue-900/20 rounded-lg p-8 text-center">
      <h2 class="text-2xl font-semibold text-gray-900 dark:text-white mb-4">
        Vous ne trouvez pas la réponse ?
      </h2>
      <p class="text-gray-600 dark:text-gray-400 mb-6">
        Notre équipe est disponible pour répondre à toutes vos questions.
      </p>
      <router-link
        to="/contact"
        class="inline-block px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-colors"
      >
        Nous contacter
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import FAQ from '@/components/FAQ.vue'
import { useSEO } from '@/composables/useSEO'

const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'
const searchQuery = ref('')

// Toutes les questions FAQ organisées par catégorie
const faqItems = {
  search: [
    {
      question: 'Comment rechercher un bien immobilier sur Viridial ?',
      answer: 'Utilisez notre barre de recherche en haut de la page pour saisir une ville, un code postal ou un type de bien. Vous pouvez ensuite affiner votre recherche avec les filtres avancés : prix, surface, nombre de pièces, etc. La carte interactive vous permet également de visualiser les biens par localisation.'
    },
    {
      question: 'Puis-je sauvegarder mes recherches favorites ?',
      answer: 'Oui ! Cliquez sur l\'icône cœur sur une annonce pour l\'ajouter à vos favoris. Vous pouvez accéder à tous vos favoris depuis la page "Mes favoris" dans le menu. Vous pouvez également créer des alertes pour être notifié quand de nouveaux biens correspondent à vos critères.'
    },
    {
      question: 'Comment fonctionne la comparaison de biens ?',
      answer: 'Sélectionnez plusieurs biens en cliquant sur le bouton "Comparer" sur chaque carte. Vous pouvez ensuite accéder à la page de comparaison pour voir côte à côte les caractéristiques, prix, localisation et photos de chaque bien.'
    },
    {
      question: 'Les prix affichés sont-ils à jour ?',
      answer: 'Oui, tous les prix sont mis à jour en temps réel par les propriétaires et agences. Nous vérifions régulièrement la cohérence des annonces pour garantir la fiabilité des informations.'
    }
  ],
  purchase: [
    {
      question: 'Quels sont les frais d\'achat d\'un bien immobilier ?',
      answer: 'Les frais d\'achat incluent généralement : les frais de notaire (environ 7-8% pour l\'ancien, 2-3% pour le neuf), les frais d\'agence (si applicable), les frais de garantie (PTZ, etc.), et les frais de dossier bancaire. Nous recommandons de consulter un notaire pour un calcul précis selon votre situation.'
    },
    {
      question: 'Comment obtenir un prêt immobilier ?',
      answer: 'Viridial ne gère pas directement les prêts, mais nous vous mettons en relation avec des partenaires bancaires. Vous pouvez également utiliser notre simulateur de prêt pour estimer votre capacité d\'emprunt. Nous recommandons de comparer plusieurs offres bancaires.'
    },
    {
      question: 'Puis-je visiter un bien avant d\'acheter ?',
      answer: 'Absolument ! Chaque annonce propose un système de réservation de visite. Cliquez sur "Réserver une visite" et choisissez un créneau disponible. Vous recevrez une confirmation par email avec les détails de la visite.'
    },
    {
      question: 'Que faire si je trouve un bien qui m\'intéresse ?',
      answer: 'Vous pouvez : 1) Contacter directement le propriétaire ou l\'agence via le formulaire de contact, 2) Réserver une visite, 3) Ajouter le bien à vos favoris, 4) Créer une alerte de prix si le bien est au-dessus de votre budget.'
    }
  ],
  rental: [
    {
      question: 'Quels documents sont nécessaires pour louer ?',
      answer: 'Généralement, vous aurez besoin de : une pièce d\'identité, 3 dernières fiches de paie ou justificatifs de revenus, un justificatif de domicile, un RIB, et parfois une garantie (caution solidaire, garantie visale, etc.). Les exigences peuvent varier selon le propriétaire.'
    },
    {
      question: 'Quel est le montant de la caution ?',
      answer: 'En France, la caution (dépôt de garantie) est généralement équivalente à 1 mois de loyer hors charges. Elle ne peut pas dépasser 2 mois de loyer pour un logement meublé ou 1 mois pour un logement non meublé. La caution est restituée à la fin du bail, déduction faite des éventuels dégâts.'
    },
    {
      question: 'Puis-je négocier le loyer ?',
      answer: 'Oui, la négociation est possible, surtout si le bien est sur le marché depuis un certain temps ou si vous avez un profil solide. N\'hésitez pas à contacter le propriétaire pour discuter des conditions.'
    },
    {
      question: 'Comment calculer les charges locatives ?',
      answer: 'Les charges locatives incluent généralement : l\'eau, le chauffage collectif, l\'entretien des parties communes, l\'ascenseur, etc. Le montant est souvent proportionnel à la surface ou au nombre de pièces. Demandez un détail des charges au propriétaire avant de signer.'
    }
  ],
  account: [
    {
      question: 'Dois-je créer un compte pour utiliser Viridial ?',
      answer: 'Non, vous pouvez rechercher et consulter les annonces sans compte. Cependant, créer un compte gratuit vous permet de : sauvegarder vos favoris, créer des alertes personnalisées, comparer des biens, et contacter directement les propriétaires.'
    },
    {
      question: 'Comment créer un compte ?',
      answer: 'Cliquez sur "Connexion" en haut à droite, puis sur "Créer un compte". Remplissez le formulaire avec votre email et un mot de passe sécurisé. Vous recevrez un email de confirmation pour activer votre compte.'
    },
    {
      question: 'Mes données personnelles sont-elles sécurisées ?',
      answer: 'Absolument. Nous respectons strictement le RGPD et utilisons des protocoles de sécurité avancés pour protéger vos données. Nous ne partageons jamais vos informations avec des tiers sans votre consentement.'
    },
    {
      question: 'Comment supprimer mon compte ?',
      answer: 'Connectez-vous à votre compte, allez dans "Paramètres" puis "Supprimer mon compte". Vous recevrez un email de confirmation. Attention, cette action est irréversible.'
    }
  ],
  market: [
    {
      question: 'Que sont les données DVF (Demandes de Valeurs Foncières) ?',
      answer: 'Les données DVF sont des informations publiques sur les transactions immobilières en France, publiées par la Direction Générale des Finances Publiques. Elles incluent les prix de vente, dates de transaction, types de biens, et permettent d\'analyser les tendances du marché immobilier.'
    },
    {
      question: 'Comment utiliser les données de marché sur Viridial ?',
      answer: 'Sur chaque page de détail d\'un bien en France, vous trouverez une section "Données de marché" qui affiche : le prix moyen au m² dans la zone, l\'évolution des prix, une comparaison avec le marché, et des transactions similaires. Ces données vous aident à évaluer si le prix est juste.'
    },
    {
      question: 'Les données DVF sont-elles disponibles partout en France ?',
      answer: 'Les données DVF couvrent la France métropolitaine et les DOM-TOM, sauf l\'Alsace, la Moselle et Mayotte. Les données sont mises à jour semestriellement (avril et octobre) par la DGFiP.'
    },
    {
      question: 'Comment interpréter les graphiques d\'évolution des prix ?',
      answer: 'Les graphiques montrent l\'évolution trimestrielle du prix au m² dans votre zone. Une courbe ascendante indique une hausse des prix, une courbe descendante une baisse. Comparez le prix du bien avec la moyenne du marché pour évaluer son attractivité.'
    }
  ],
  technical: [
    {
      question: 'Le site ne charge pas correctement, que faire ?',
      answer: 'Essayez de : 1) Vider le cache de votre navigateur, 2) Désactiver temporairement les extensions, 3) Utiliser un autre navigateur (Chrome, Firefox, Safari), 4) Vérifier votre connexion internet. Si le problème persiste, contactez notre support technique.'
    },
    {
      question: 'La carte ne s\'affiche pas, comment résoudre ?',
      answer: 'Vérifiez que JavaScript est activé dans votre navigateur. Si vous utilisez un bloqueur de publicités, autorisez les scripts pour viridial.com. La carte nécessite une connexion internet active pour charger les données cartographiques.'
    },
    {
      question: 'Puis-je utiliser Viridial sur mobile ?',
      answer: 'Oui ! Viridial est entièrement responsive et optimisé pour mobile, tablette et desktop. Vous pouvez rechercher, consulter les annonces, et contacter les propriétaires depuis n\'importe quel appareil.'
    },
    {
      question: 'Comment signaler une annonce incorrecte ou frauduleuse ?',
      answer: 'Cliquez sur "Signaler" sur l\'annonce concernée ou contactez-nous directement à contact@viridial.com avec les détails. Notre équipe modère toutes les annonces et prendra les mesures nécessaires rapidement.'
    }
  ]
}

// Filtrer les FAQ selon la recherche
function filteredFAQItems(query: string, category: keyof typeof faqItems) {
  if (!query.trim()) {
    return faqItems[category]
  }
  
  const lowerQuery = query.toLowerCase()
  return faqItems[category].filter(item => 
    item.question.toLowerCase().includes(lowerQuery) ||
    item.answer.toLowerCase().includes(lowerQuery)
  )
}

onMounted(() => {
  // SEO optimisé pour la page FAQ
  useSEO({
    title: 'FAQ - Questions fréquentes | Viridial Immobilier',
    description: 'Trouvez rapidement des réponses à vos questions sur la recherche, l\'achat, la location immobilière, les données de marché DVF, et l\'utilisation de la plateforme Viridial.',
    keywords: ['FAQ immobilier', 'questions fréquentes', 'aide immobilière', 'guide achat immobilier', 'location appartement', 'données DVF'],
    type: 'website',
    canonical: `${siteUrl}/faq`,
    url: `${siteUrl}/faq`
  })
})
</script>

