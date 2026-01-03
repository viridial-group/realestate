/**
 * Composable pour améliorer la navigation au clavier
 */
export function useKeyboardNavigation() {
  /**
   * Gère la navigation au clavier dans une liste
   */
  function handleListNavigation(
    event: KeyboardEvent,
    currentIndex: number,
    totalItems: number,
    onSelect: (index: number) => void
  ) {
    let newIndex = currentIndex

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault()
        newIndex = currentIndex < totalItems - 1 ? currentIndex + 1 : 0
        onSelect(newIndex)
        break
      case 'ArrowUp':
        event.preventDefault()
        newIndex = currentIndex > 0 ? currentIndex - 1 : totalItems - 1
        onSelect(newIndex)
        break
      case 'Home':
        event.preventDefault()
        onSelect(0)
        break
      case 'End':
        event.preventDefault()
        onSelect(totalItems - 1)
        break
      case 'Enter':
      case ' ':
        event.preventDefault()
        onSelect(currentIndex)
        break
    }
  }

  /**
   * Gère la navigation par onglets
   */
  function handleTabNavigation(
    event: KeyboardEvent,
    items: HTMLElement[],
    currentIndex: number,
    onSelect: (index: number) => void
  ) {
    if (event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
      event.preventDefault()
      const direction = event.key === 'ArrowLeft' ? -1 : 1
      const newIndex = (currentIndex + direction + items.length) % items.length
      items[newIndex]?.focus()
      onSelect(newIndex)
    }
  }

  /**
   * Gère l'échappement pour fermer les modales/menus
   */
  function handleEscape(callback: () => void) {
    const handler = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        callback()
      }
    }
    document.addEventListener('keydown', handler)
    return () => document.removeEventListener('keydown', handler)
  }

  return {
    handleListNavigation,
    handleTabNavigation,
    handleEscape,
  }
}

