<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Sidebar -->
    <UserSidebar ref="sidebarRef" />

    <!-- Contenu principal -->
    <div class="md:ml-64 transition-all duration-300 min-h-screen bg-gray-50">
      <!-- Bouton pour ouvrir/fermer la sidebar sur mobile -->
      <button
        @click="toggleSidebar"
        class="fixed top-16 left-4 z-50 p-2 bg-white rounded-md shadow-md border border-gray-200 md:hidden hover:bg-gray-50 transition-colors"
        aria-label="Toggle sidebar"
      >
        <Menu class="h-5 w-5 text-gray-700" />
      </button>

      <!-- Contenu de la page -->
      <main class="py-8">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Menu } from 'lucide-vue-next'
import UserSidebar from '@/components/UserSidebar.vue'

const sidebarRef = ref<InstanceType<typeof UserSidebar> | null>(null)

function toggleSidebar() {
  sidebarRef.value?.toggle()
}

// Fermer la sidebar sur mobile au chargement
onMounted(() => {
  if (window.innerWidth < 768) {
    sidebarRef.value?.close()
  }
})
</script>

