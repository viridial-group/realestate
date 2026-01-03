/**
 * Utilitaires pour mettre en évidence les termes de recherche dans les résultats
 */
export interface HighlightMatch {
  text: string
  highlighted: boolean
}

/**
 * Parse une requête de recherche pour extraire les termes à mettre en évidence
 */
export function parseSearchTerms(query: string): {
  exactPhrases: string[]
  includeTerms: string[]
  excludeTerms: string[]
} {
  if (!query || query.trim().length === 0) {
    return { exactPhrases: [], includeTerms: [], excludeTerms: [] }
  }

  const exactPhrases: string[] = []
  const includeTerms: string[] = []
  const excludeTerms: string[] = []

  // Extraire les phrases exactes (entre guillemets)
  const exactPhraseRegex = /"([^"]+)"/g
  let match
  while ((match = exactPhraseRegex.exec(query)) !== null) {
    exactPhrases.push(match[1].toLowerCase())
  }

  // Extraire les termes exclus (préfixés par -)
  const excludeRegex = /-([^\s"]+)/g
  while ((match = excludeRegex.exec(query)) !== null) {
    excludeTerms.push(match[1].toLowerCase())
  }

  // Extraire les termes normaux (enlever les phrases exactes et exclusions)
  let normalizedQuery = query
    .replace(/"[^"]+"/g, '') // Enlever les phrases exactes
    .replace(/-[^\s"]+/g, '') // Enlever les exclusions
    .trim()

  // Diviser en mots
  const words = normalizedQuery.split(/\s+/).filter(w => w.length > 0)
  words.forEach(word => {
    const lowerWord = word.toLowerCase()
    if (lowerWord.length > 2) {
      includeTerms.push(lowerWord)
    }
  })

  return { exactPhrases, includeTerms, excludeTerms }
}

/**
 * Met en évidence les termes de recherche dans un texte
 */
export function highlightText(
  text: string,
  searchTerms: { exactPhrases: string[]; includeTerms: string[]; excludeTerms: string[] }
): HighlightMatch[] {
  if (!text || text.length === 0) {
    return [{ text: '', highlighted: false }]
  }

  const matches: HighlightMatch[] = []
  const allTerms = [
    ...searchTerms.exactPhrases,
    ...searchTerms.includeTerms,
  ].sort((a, b) => b.length - a.length) // Trier par longueur décroissante pour prioriser les phrases longues

  // Créer une regex pour trouver tous les termes (insensible à la casse)
  const termsPattern = allTerms
    .map(term => term.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')) // Échapper les caractères spéciaux
    .join('|')

  if (termsPattern.length === 0) {
    return [{ text, highlighted: false }]
  }

  const regex = new RegExp(`(${termsPattern})`, 'gi')
  let lastIndex = 0
  let match

  while ((match = regex.exec(text)) !== null) {
    // Ajouter le texte avant le match
    if (match.index > lastIndex) {
      matches.push({
        text: text.substring(lastIndex, match.index),
        highlighted: false,
      })
    }

    // Ajouter le match mis en évidence
    matches.push({
      text: match[0],
      highlighted: true,
    })

    lastIndex = regex.lastIndex
  }

  // Ajouter le texte restant
  if (lastIndex < text.length) {
    matches.push({
      text: text.substring(lastIndex),
      highlighted: false,
    })
  }

  return matches.length > 0 ? matches : [{ text, highlighted: false }]
}

/**
 * Génère du HTML avec highlighting
 */
export function highlightTextHTML(
  text: string,
  searchTerms: { exactPhrases: string[]; includeTerms: string[]; excludeTerms: string[] }
): string {
  const matches = highlightText(text, searchTerms)
  return matches
    .map(match =>
      match.highlighted
        ? `<mark class="bg-yellow-200 dark:bg-yellow-900 px-1 rounded">${escapeHtml(match.text)}</mark>`
        : escapeHtml(match.text)
    )
    .join('')
}

/**
 * Échappe le HTML pour éviter les injections XSS
 */
function escapeHtml(text: string): string {
  const div = document.createElement('div')
  div.textContent = text
  return div.innerHTML
}

