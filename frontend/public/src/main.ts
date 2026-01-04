import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './style.css' // Tailwind + shadcn-vue variables
import './assets/main.css' // Styles personnalisés
import '@fortawesome/fontawesome-free/css/all.css' // FontAwesome icons
import { registerServiceWorker } from './utils/serviceWorker'

// Enregistrer le Service Worker pour améliorer les performances et le SEO
if (import.meta.env.PROD) {
  registerServiceWorker()
}

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.mount('#app')
