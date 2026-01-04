/**
 * Utilitaires pour l'export PDF des annonces
 */

export interface PropertyForExport {
  id: number
  title: string
  description?: string
  price: number
  surface?: number
  bedrooms?: number
  bathrooms?: number
  city?: string
  address?: string
  postalCode?: string
  type?: string
  transactionType?: string
  images?: string[]
}

/**
 * Export une propriété en PDF (simplifié - ouvre une nouvelle fenêtre avec le contenu)
 */
export function exportPropertyToPDF(property: PropertyForExport) {
  const content = generatePropertyHTML(property)
  const printWindow = window.open('', '_blank')
  if (!printWindow) {
    alert('Veuillez autoriser les popups pour exporter en PDF')
    return
  }

  printWindow.document.write(`
    <!DOCTYPE html>
    <html>
      <head>
        <title>${property.title}</title>
        <style>
          body {
            font-family: Arial, sans-serif;
            padding: 40px;
            max-width: 800px;
            margin: 0 auto;
          }
          h1 {
            color: #1f2937;
            border-bottom: 3px solid #3b82f6;
            padding-bottom: 10px;
            margin-bottom: 20px;
          }
          .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
          }
          .price {
            font-size: 28px;
            font-weight: bold;
            color: #3b82f6;
          }
          .details {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
            margin: 20px 0;
          }
          .detail-item {
            padding: 10px;
            background: #f3f4f6;
            border-radius: 5px;
          }
          .detail-label {
            font-size: 12px;
            color: #6b7280;
            text-transform: uppercase;
          }
          .detail-value {
            font-size: 18px;
            font-weight: bold;
            color: #1f2937;
            margin-top: 5px;
          }
          .description {
            margin: 30px 0;
            line-height: 1.6;
            color: #374151;
          }
          .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
            text-align: center;
            color: #6b7280;
            font-size: 12px;
          }
          @media print {
            body {
              padding: 20px;
            }
          }
        </style>
      </head>
      <body>
        ${content}
      </body>
    </html>
  `)

  printWindow.document.close()
  printWindow.focus()
  
  // Attendre que le contenu soit chargé avant d'imprimer
  setTimeout(() => {
    printWindow.print()
  }, 250)
}

/**
 * Génère le HTML pour une propriété
 */
function generatePropertyHTML(property: PropertyForExport): string {
  const transactionLabel = property.transactionType === 'RENT' ? 'Location' : 'Vente'
  const priceLabel = property.transactionType === 'RENT' ? '/mois' : ''

  return `
    <div class="header">
      <h1>${escapeHtml(property.title)}</h1>
      <div class="price">${formatPrice(property.price)} €${priceLabel}</div>
    </div>

    <div class="details">
      ${property.surface ? `
        <div class="detail-item">
          <div class="detail-label">Surface</div>
          <div class="detail-value">${property.surface} m²</div>
        </div>
      ` : ''}
      ${property.bedrooms ? `
        <div class="detail-item">
          <div class="detail-label">Chambres</div>
          <div class="detail-value">${property.bedrooms}</div>
        </div>
      ` : ''}
      ${property.bathrooms ? `
        <div class="detail-item">
          <div class="detail-label">Salles de bain</div>
          <div class="detail-value">${property.bathrooms}</div>
        </div>
      ` : ''}
      <div class="detail-item">
        <div class="detail-label">Type de transaction</div>
        <div class="detail-value">${transactionLabel}</div>
      </div>
      ${property.type ? `
        <div class="detail-item">
          <div class="detail-label">Type</div>
          <div class="detail-value">${escapeHtml(property.type)}</div>
        </div>
      ` : ''}
      ${property.city ? `
        <div class="detail-item">
          <div class="detail-label">Localisation</div>
          <div class="detail-value">${escapeHtml(property.city)}${property.postalCode ? ` ${property.postalCode}` : ''}</div>
        </div>
      ` : ''}
    </div>

    ${property.address ? `
      <div style="margin: 20px 0; padding: 15px; background: #eff6ff; border-radius: 5px;">
        <strong>Adresse:</strong> ${escapeHtml(property.address)}
      </div>
    ` : ''}

    ${property.description ? `
      <div class="description">
        <h2 style="color: #1f2937; margin-bottom: 15px;">Description</h2>
        <p>${escapeHtml(property.description).replace(/\n/g, '<br>')}</p>
      </div>
    ` : ''}

    <div class="footer">
      <p>Référence: ${property.id}</p>
      <p>Document généré le ${new Date().toLocaleDateString('fr-FR')}</p>
    </div>
  `
}

/**
 * Export plusieurs propriétés en PDF
 */
export function exportPropertiesToPDF(properties: PropertyForExport[]) {
  if (properties.length === 0) {
    alert('Aucune propriété à exporter')
    return
  }

  if (properties.length === 1) {
    exportPropertyToPDF(properties[0])
    return
  }

  // Pour plusieurs propriétés, créer un document avec toutes
  const content = properties.map((property, index) => `
    <div style="page-break-after: ${index < properties.length - 1 ? 'always' : 'auto'}; margin-bottom: 40px;">
      ${generatePropertyHTML(property)}
    </div>
  `).join('')

  const printWindow = window.open('', '_blank')
  if (!printWindow) {
    alert('Veuillez autoriser les popups pour exporter en PDF')
    return
  }

  printWindow.document.write(`
    <!DOCTYPE html>
    <html>
      <head>
        <title>Mes annonces - ${properties.length} propriété(s)</title>
        <style>
          body {
            font-family: Arial, sans-serif;
            padding: 40px;
            max-width: 800px;
            margin: 0 auto;
          }
          h1 {
            color: #1f2937;
            border-bottom: 3px solid #3b82f6;
            padding-bottom: 10px;
            margin-bottom: 20px;
          }
          .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
          }
          .price {
            font-size: 28px;
            font-weight: bold;
            color: #3b82f6;
          }
          .details {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
            margin: 20px 0;
          }
          .detail-item {
            padding: 10px;
            background: #f3f4f6;
            border-radius: 5px;
          }
          .detail-label {
            font-size: 12px;
            color: #6b7280;
            text-transform: uppercase;
          }
          .detail-value {
            font-size: 18px;
            font-weight: bold;
            color: #1f2937;
            margin-top: 5px;
          }
          .description {
            margin: 30px 0;
            line-height: 1.6;
            color: #374151;
          }
          .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
            text-align: center;
            color: #6b7280;
            font-size: 12px;
          }
          @media print {
            body {
              padding: 20px;
            }
          }
        </style>
      </head>
      <body>
        <h1 style="text-align: center; margin-bottom: 40px;">Mes annonces (${properties.length})</h1>
        ${content}
      </body>
    </html>
  `)

  printWindow.document.close()
  printWindow.focus()
  
  setTimeout(() => {
    printWindow.print()
  }, 250)
}

function escapeHtml(text: string): string {
  const div = document.createElement('div')
  div.textContent = text
  return div.innerHTML
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(price)
}

/**
 * Export les favoris en PDF
 * Convertit les PublicProperty en PropertyForExport et exporte
 */
export function exportFavoritesToPDF(properties: Array<{
  id: number
  title: string
  description?: string
  price: number
  surface?: number
  bedrooms?: number
  bathrooms?: number
  city?: string
  address?: string
  postalCode?: string
  type?: string
  transactionType?: string
  images?: string[]
}>) {
  if (properties.length === 0) {
    alert('Aucune propriété à exporter')
    return
  }

  // Convertir les propriétés au format PropertyForExport
  const propertiesForExport: PropertyForExport[] = properties.map(property => ({
    id: property.id,
    title: property.title || 'Sans titre',
    description: property.description,
    price: property.price || 0,
    surface: property.surface,
    bedrooms: property.bedrooms,
    bathrooms: property.bathrooms,
    city: property.city,
    address: property.address,
    postalCode: property.postalCode,
    type: property.type,
    transactionType: property.transactionType,
    images: property.images || []
  }))

  // Utiliser la fonction existante pour exporter
  exportPropertiesToPDF(propertiesForExport)
}
