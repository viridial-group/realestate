<template>
  <div class="relative">
    <button
      @click="showMenu = !showMenu"
      class="p-2 rounded-full bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
      title="Partager cette recherche"
      aria-label="Partager cette recherche"
      aria-expanded="false"
      :aria-haspopup="true"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"
        />
      </svg>
    </button>

    <!-- Menu déroulant -->
    <Transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <div
        v-if="showMenu"
        v-click-outside="() => showMenu = false"
        class="absolute right-0 mt-2 w-56 bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 z-50"
        role="menu"
        aria-label="Options de partage"
      >
        <div class="py-1">
          <!-- Copier le lien -->
          <button
            @click="handleCopyLink"
            class="w-full px-4 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
            role="menuitem"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
            </svg>
            Copier le lien
          </button>

          <!-- Partage natif (si disponible) -->
          <button
            v-if="canUseNativeShare"
            @click="handleNativeShare"
            class="w-full px-4 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
            role="menuitem"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            Partager...
          </button>

          <div class="border-t border-gray-200 dark:border-gray-700 my-1"></div>

          <!-- Email -->
          <button
            @click="handleEmailShare"
            class="w-full px-4 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
            role="menuitem"
          >
            <span class="text-lg">📧</span>
            Email
          </button>

          <!-- WhatsApp -->
          <button
            @click="handleWhatsAppShare"
            class="w-full px-4 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
            role="menuitem"
          >
            <span class="text-lg">💬</span>
            WhatsApp
          </button>

          <!-- Facebook -->
          <button
            @click="handleFacebookShare"
            class="w-full px-4 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
            role="menuitem"
          >
            <span class="text-lg">📘</span>
            Facebook
          </button>

          <!-- Twitter -->
          <button
            @click="handleTwitterShare"
            class="w-full px-4 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
            role="menuitem"
          >
            <span class="text-lg">🐦</span>
            Twitter
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useShareSearch, type SearchParams } from '@/composables/useShareSearch'

const props = defineProps<{
  searchParams: SearchParams
}>()

const showMenu = ref(false)
const { copyShareLink, shareViaWebAPI, shareViaEmail, shareViaWhatsApp, shareViaFacebook, shareViaTwitter } = useShareSearch()

const canUseNativeShare = computed(() => {
  return typeof navigator !== 'undefined' && !!navigator.share
})

// Directive pour fermer le menu en cliquant à l'extérieur
const vClickOutside = {
  mounted(el: any, binding: any) {
    el.clickOutsideEvent = (event: Event) => {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value()
      }
    }
    document.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el: any) {
    document.removeEventListener('click', el.clickOutsideEvent)
  },
}

async function handleCopyLink() {
  await copyShareLink(props.searchParams)
  showMenu.value = false
}

async function handleNativeShare() {
  await shareViaWebAPI(props.searchParams)
  showMenu.value = false
}

function handleEmailShare() {
  shareViaEmail(props.searchParams)
  showMenu.value = false
}

function handleWhatsAppShare() {
  shareViaWhatsApp(props.searchParams)
  showMenu.value = false
}

function handleFacebookShare() {
  shareViaFacebook(props.searchParams)
  showMenu.value = false
}

function handleTwitterShare() {
  shareViaTwitter(props.searchParams)
  showMenu.value = false
}
</script>

