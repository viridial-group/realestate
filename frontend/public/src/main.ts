import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css' // Tailwind + shadcn-vue variables
import './assets/main.css' // Styles personnalisés
import '@fortawesome/fontawesome-free/css/all.css' // FontAwesome icons

createApp(App).use(router).mount('#app')
