/**
 * Utilitaires pour l'export PDF des favoris
 * Note: Cette implémentation utilise une approche simple avec window.print()
 * Pour une solution plus avancée, on pourrait utiliser jsPDF ou pdfmake
 */

/**
 * Exporte les favoris en PDF (via impression)
 */
export function exportFavoritesToPDF(properties: any[]): void {
  // Créer une fenêtre d'impression
  const printWindow = window.open('', '_blank')
  
  if (!printWindow) {
    alert('Veuillez autoriser les popups pour exporter en PDF')
    return
  }

  // Générer le HTML pour l'impression
  const html = generatePrintHTML(properties)
  
  printWindow.document.write(html)
  printWindow.document.close()
  
  // Attendre que le contenu soit chargé puis imprimer
  printWindow.onload = () => {
    setTimeout(() => {
      printWindow.print()
    }, 250)
  }
}

/**
 * Génère le HTML pour l'impression
 */
function generatePrintHTML(properties: any[]): string {
  const date = new Date().toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })

  const propertiesHTML = properties.map((property, index) => `
    <div style="page-break-inside: avoid; margin-bottom: 30px; padding: 20px; border: 1px solid #e5e7eb; border-radius: 8px;">
      <h3 style="color: #1e40af; font-size: 18px; margin-bottom: 10px;">
        ${index + 1}. ${property.title}
      </h3>
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-top: 15px;">
        <div>
          <p style="margin: 5px 0;"><strong>Type:</strong> ${property.type}</p>
          <p style="margin: 5px 0;"><strong>Statut:</strong> ${property.status}</p>
          <p style="margin: 5px 0;"><strong>Prix:</strong> €${property.price.toLocaleString('fr-FR')}</p>
          <p style="margin: 5px 0;"><strong>Surface:</strong> ${property.surface} m²</p>
        </div>
        <div>
          <p style="margin: 5px 0;"><strong>Ville:</strong> ${property.city}</p>
          <p style="margin: 5px 0;"><strong>Chambres:</strong> ${property.bedrooms || 'N/A'}</p>
          <p style="margin: 5px 0;"><strong>Salles de bain:</strong> ${property.bathrooms || 'N/A'}</p>
          ${property.rating ? `<p style="margin: 5px 0;"><strong>Note:</strong> ${property.rating.toFixed(1)} ⭐</p>` : ''}
        </div>
      </div>
      ${property.description ? `<p style="margin-top: 15px; color: #4b5563;">${property.description}</p>` : ''}
    </div>
  `).join('')

  return `
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="UTF-8">
        <title>Mes Favoris - RealEstate</title>
        <style>
          @media print {
            @page {
              margin: 2cm;
            }
            body {
              margin: 0;
              padding: 0;
            }
          }
          body {
            font-family: 'Roboto', Arial, sans-serif;
            color: #1f2937;
            line-height: 1.6;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
          }
          h1 {
            color: #1e40af;
            border-bottom: 3px solid #1e40af;
            padding-bottom: 10px;
            margin-bottom: 30px;
          }
          .header-info {
            margin-bottom: 30px;
            padding: 15px;
            background-color: #f3f4f6;
            border-radius: 8px;
          }
        </style>
      </head>
      <body>
        <h1>Mes Propriétés Favorites</h1>
        <div class="header-info">
          <p><strong>Date d'export:</strong> ${date}</p>
          <p><strong>Nombre de propriétés:</strong> ${properties.length}</p>
        </div>
        ${propertiesHTML}
        <div style="margin-top: 40px; padding-top: 20px; border-top: 2px solid #e5e7eb; text-align: center; color: #6b7280; font-size: 12px;">
          <p>Généré par RealEstate - ${date}</p>
        </div>
      </body>
    </html>
  `
}

