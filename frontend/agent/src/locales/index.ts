import { createI18n } from 'vue-i18n'
import en from './en'
import fr from './fr'

export type SupportedLocale = 'en' | 'fr'

export const supportedLocales: { code: SupportedLocale; name: string; flag: string }[] = [
  { code: 'en', name: 'English', flag: '🇬🇧' },
  { code: 'fr', name: 'Français', flag: '🇫🇷' }
]

const getSavedLocale = (): SupportedLocale => {
  if (typeof window !== 'undefined') {
    const saved = localStorage.getItem('locale') as SupportedLocale
    if (saved && supportedLocales.find((l) => l.code === saved)) {
      return saved
    }
  }
  return 'fr'
}

const savedLocale = getSavedLocale()

if (typeof document !== 'undefined') {
  document.documentElement.lang = savedLocale
  document.documentElement.dir = 'ltr'
}

const i18n = createI18n({
  legacy: false,
  locale: savedLocale,
  fallbackLocale: 'en',
  messages: {
    en,
    fr
  }
})

export default i18n


