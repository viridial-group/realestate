import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import i18n from './locales'
import './style.css'
import { Toaster } from '@/components/ui/toast'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(i18n)
app.component('Toaster', Toaster)

app.mount('#app')

