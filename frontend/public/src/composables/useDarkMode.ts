import { ref, watch, onMounted } from 'vue'

const STORAGE_KEY = 'realestate_dark_mode'

/**
 * Composable pour gérer le mode sombre
 */
export function useDarkMode() {
  const isDark = ref<boolean>(false)

  // Charger la préférence depuis localStorage ou système
  function loadDarkMode() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored !== null) {
        isDark.value = stored === 'true'
      } else {
        // Utiliser la préférence système
        isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
      }
    } catch (error) {
      console.error('Error loading dark mode preference:', error)
      isDark.value = false
    }
    applyDarkMode()
  }

  // Sauvegarder la préférence
  function saveDarkMode() {
    try {
      localStorage.setItem(STORAGE_KEY, isDark.value.toString())
    } catch (error) {
      console.error('Error saving dark mode preference:', error)
    }
  }

  // Appliquer le mode sombre au document
  function applyDarkMode() {
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  // Toggle le mode sombre
  function toggleDarkMode() {
    isDark.value = !isDark.value
    applyDarkMode()
    saveDarkMode()
  }

  // Écouter les changements de préférence système
  function watchSystemPreference() {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    mediaQuery.addEventListener('change', (e) => {
      // Seulement si l'utilisateur n'a pas défini de préférence manuelle
      if (localStorage.getItem(STORAGE_KEY) === null) {
        isDark.value = e.matches
        applyDarkMode()
      }
    })
  }

  // Initialisation
  onMounted(() => {
    loadDarkMode()
    watchSystemPreference()
  })

  // Sauvegarder automatiquement quand le mode change
  watch(isDark, () => {
    saveDarkMode()
  })

  return {
    isDark,
    toggleDarkMode,
    loadDarkMode,
  }
}

