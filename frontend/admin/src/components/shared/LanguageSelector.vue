<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button variant="outline" size="sm" class="gap-2">
        <span>{{ currentLocale.flag }}</span>
        <span class="hidden sm:inline">{{ currentLocale.name }}</span>
        <ChevronDown class="h-4 w-4" />
      </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent align="end" class="w-48">
      <DropdownMenuItem
        v-for="locale in supportedLocales"
        :key="locale.code"
        @click="changeLocale(locale.code)"
        :class="{ 'bg-accent': currentLocale.code === locale.code }"
      >
        <span class="mr-2">{{ locale.flag }}</span>
        <span>{{ locale.name }}</span>
        <Check v-if="currentLocale.code === locale.code" class="ml-auto h-4 w-4" />
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { supportedLocales, type SupportedLocale } from '@/locales/index'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { Check, ChevronDown } from 'lucide-vue-next'

const { locale } = useI18n()

const currentLocale = computed(() => {
  return supportedLocales.find((l) => l.code === locale.value) || supportedLocales[0]
})

const changeLocale = (newLocale: SupportedLocale) => {
  locale.value = newLocale
  if (typeof window !== 'undefined') {
    localStorage.setItem('locale', newLocale)
  }
  // Update HTML lang attribute
  document.documentElement.lang = newLocale
  // For RTL languages
  if (newLocale === 'ar') {
    document.documentElement.dir = 'rtl'
  } else {
    document.documentElement.dir = 'ltr'
  }
}
</script>

