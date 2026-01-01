import { useI18n as useVueI18n } from 'vue-i18n'

/**
 * Composable pour faciliter l'utilisation d'i18n
 * Fournit des helpers pour les traductions courantes
 */
export function useI18n() {
  const { t, locale } = useVueI18n()

  return {
    t,
    locale,
    // Helpers pour les traductions courantes
    common: {
      save: () => t('common.save'),
      cancel: () => t('common.cancel'),
      delete: () => t('common.delete'),
      edit: () => t('common.edit'),
      create: () => t('common.create'),
      search: () => t('common.search'),
      filter: () => t('common.filter'),
      reset: () => t('common.reset'),
      export: () => t('common.export'),
      actions: () => t('common.actions'),
      status: () => t('common.status'),
      type: () => t('common.type'),
      total: () => t('common.total'),
      view: () => t('common.view'),
      details: () => t('common.details'),
      close: () => t('common.close'),
      confirm: () => t('common.confirm'),
      yes: () => t('common.yes'),
      no: () => t('common.no'),
      back: () => t('common.back'),
      next: () => t('common.next'),
      previous: () => t('common.previous')
    },
    messages: {
      success: {
        created: () => t('messages.success.created'),
        updated: () => t('messages.success.updated'),
        deleted: () => t('messages.success.deleted'),
        saved: () => t('messages.success.saved'),
        activated: () => t('messages.success.activated'),
        suspended: () => t('messages.success.suspended')
      },
      error: {
        generic: () => t('messages.error.generic'),
        notFound: () => t('messages.error.notFound'),
        unauthorized: () => t('messages.error.unauthorized'),
        forbidden: () => t('messages.error.forbidden'),
        network: () => t('messages.error.network'),
        server: () => t('messages.error.server')
      },
      confirm: {
        delete: () => t('messages.confirm.delete'),
        deleteMultiple: (count: number) => t('messages.confirm.deleteMultiple', { count }),
        cancel: () => t('messages.confirm.cancel'),
        logout: () => t('messages.confirm.logout')
      }
    }
  }
}

