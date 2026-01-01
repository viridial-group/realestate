import { createI18n } from 'vue-i18n'
import en from './en'
import fr from './fr'
import es from './es'
import it from './it'
import de from './de'
import vi from './vi'
import zh from './zh'
import ar from './ar'

export type SupportedLocale = 'en' | 'fr' | 'es' | 'it' | 'de' | 'vi' | 'zh' | 'ar'

export const supportedLocales: { code: SupportedLocale; name: string; flag: string }[] = [
  { code: 'en', name: 'English', flag: '🇬🇧' },
  { code: 'fr', name: 'Français', flag: '🇫🇷' },
  { code: 'es', name: 'Español', flag: '🇪🇸' },
  { code: 'it', name: 'Italiano', flag: '🇮🇹' },
  { code: 'de', name: 'Deutsch', flag: '🇩🇪' },
  { code: 'vi', name: 'Tiếng Việt', flag: '🇻🇳' },
  { code: 'zh', name: '中文', flag: '🇨🇳' },
  { code: 'ar', name: 'العربية', flag: '🇸🇦' }
]

// Get saved locale or default to French
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

// Set initial HTML attributes
if (typeof document !== 'undefined') {
  document.documentElement.lang = savedLocale
  if (savedLocale === 'ar') {
    document.documentElement.dir = 'rtl'
  } else {
    document.documentElement.dir = 'ltr'
  }
}

const i18n = createI18n({
  legacy: false,
  locale: savedLocale,
  fallbackLocale: 'en',
  messages: {
    en,
    fr,
    es,
    it,
    de,
    vi,
    zh,
    ar
  }
})

export default i18n

