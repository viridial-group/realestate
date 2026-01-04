import { PuppeteerScraper } from './PuppeteerScraper';
import { Agency } from '../models/Agency';
import { config } from '../config/config';
import {
  extractDomain,
  cleanText,
} from '../utils/validators';

export class AnnuaireImmoScraper extends PuppeteerScraper {
  // Toutes les catégories à scraper
  private readonly categories = [
    'agence-immobiliere',
    'agent-immobilier',
    'chasseur-immobilier',
    'immobilier-de-prestige',
    'immobilier-d-entreprise',
    'immobilier-commercial',
    'diagnostic-immobilier',
    'expertise-immobiliere',
    'gestion-immobiliere',
    'location-saisonniere',
    'financement-immobilier',
    'investissement-immobilier',
    'immobilier-neuf',
    'annonces-immobilieres',
  ];

  getName(): string {
    return 'AnnuaireImmo';
  }

  async scrape(searchTerm: string, city?: string): Promise<Agency[]> {
    const agencies: Agency[] = [];
    let page;

    try {
      page = await this.createPage();

      // Scraper toutes les catégories
      for (const category of this.categories) {
        try {
          const categoryAgencies = await this.scrapeCategory(page, category);
          agencies.push(...categoryAgencies);
          
          // Délai entre les catégories
          await this.delay(1000);
        } catch (error: any) {
          // Ignore category errors
        }
      }
    } catch (error: any) {
      // Ignore
    } finally {
      if (page) {
        try {
          await page.close();
        } catch (e) {
          // Ignore
        }
      }
    }

    return agencies;
  }

  private async scrapeCategory(page: any, category: string): Promise<Agency[]> {
    const agencies: Agency[] = [];
    let currentPage = 1;
    let hasMorePages = true;

    while (hasMorePages) {
      const categoryUrl = `https://annuaireimmo.fr/${category}/`;
      const pageUrl = currentPage > 1 ? `${categoryUrl}?page=${currentPage}` : categoryUrl;

      try {
        await page.goto(pageUrl, {
          waitUntil: 'networkidle2',
          timeout: config.scraping.timeout,
        });

        await this.delay(2000);

        // Extraire tous les liens "Détails" de la page de liste
        const detailLinks = await page.evaluate(() => {
          const links: Array<{ url: string; name: string }> = [];
          
          // Chercher les liens "Détails" dans la colonne description
          // Plusieurs stratégies pour trouver ces liens
          const selectors = [
            'a:contains("Détails")',
            'a[href*="/"]',
            '.column_in_description_site_category a',
            '[class*="description"] a',
            '[class*="detail"] a',
            'h2 a, h3 a',
            'article a, .entry a',
          ];

          // Méthode 1: Chercher directement les liens contenant "Détails"
          const allLinks = document.querySelectorAll('a');
          allLinks.forEach((link: Element) => {
            const text = link.textContent?.trim().toLowerCase() || '';
            const href = link.getAttribute('href') || '';
            
            if ((text.includes('détails') || text.includes('details')) && href && href.includes('/')) {
              // Trouver le nom de l'agence depuis le conteneur parent
              const container = link.closest('article, .entry, [class*="item"], [class*="card"], tr, div') || link.parentElement;
              const nameEl = container?.querySelector('h2, h3, .title, [class*="title"]') || container?.querySelector('strong, b');
              const name = nameEl?.textContent?.trim() || text.replace(/détails?/i, '').trim() || '';
              
              if (name && name.length > 3) {
                const fullUrl = href.startsWith('http') ? href : `https://annuaireimmo.fr${href.startsWith('/') ? href : '/' + href}`;
                links.push({ url: fullUrl, name });
              }
            }
          });

          // Méthode 2: Chercher dans les colonnes de description
          const descriptionColumns = document.querySelectorAll('[class*="description"], .column_in_description_site_category, [class*="column"]');
          descriptionColumns.forEach((col: Element) => {
            const link = col.querySelector('a[href*="/"]');
            if (link) {
              const href = link.getAttribute('href') || '';
              const text = link.textContent?.trim() || '';
              
              if (href && !links.some(l => l.url.includes(href))) {
                // Trouver le nom depuis le conteneur parent
                const container = col.closest('tr, article, .entry, [class*="item"]') || col.parentElement;
                const nameEl = container?.querySelector('h2, h3, .title, [class*="title"], strong, b, td:first-child');
                const name = nameEl?.textContent?.trim() || text || '';
                
                if (name && name.length > 3) {
                  const fullUrl = href.startsWith('http') ? href : `https://annuaireimmo.fr${href.startsWith('/') ? href : '/' + href}`;
                  links.push({ url: fullUrl, name });
                }
              }
            }
          });

          // Méthode 3: Chercher tous les liens dans les titres h2/h3
          const titleLinks = document.querySelectorAll('h2 a, h3 a');
          titleLinks.forEach((link: Element) => {
            const href = link.getAttribute('href') || '';
            const name = link.textContent?.trim() || '';
            
            if (href && name && name.length > 3 && !links.some(l => l.url.includes(href))) {
              const fullUrl = href.startsWith('http') ? href : `https://annuaireimmo.fr${href.startsWith('/') ? href : '/' + href}`;
              links.push({ url: fullUrl, name });
            }
          });

          return links;
        });

        // Visiter chaque page de détail pour extraire les données complètes
        for (const detailLink of detailLinks) {
          try {
            // Naviguer vers la page de détail
            await page.goto(detailLink.url, {
              waitUntil: 'networkidle2',
              timeout: config.scraping.timeout,
            });

            await this.delay(1500);

            // Extraire les données depuis la page de détail
            const agencyData = await page.evaluate(() => {
              const data: any = {
                name: '',
                description: '',
                email: '',
                phone: '',
                address: '',
                website: '',
              };

              // Extraire le nom depuis .title_h (priorité pour annuaireimmo.fr)
              const titleH = document.querySelector('.title_h');
              if (titleH) {
                const text = titleH.textContent?.trim() || '';
                if (text && text.length > 3 && text.length < 200) {
                  data.name = text;
                }
              }
              
              // Fallback: autres sélecteurs si .title_h n'existe pas
              if (!data.name) {
                const nameSelectors = ['h1', 'h2', '.title', '[class*="title"]', 'title'];
                for (const selector of nameSelectors) {
                  const el = document.querySelector(selector);
                  if (el) {
                    const text = el.textContent?.trim() || el.getAttribute('content') || '';
                    if (text && text.length > 3 && text.length < 200) {
                      data.name = text;
                      break;
                    }
                  }
                }
              }

              // Extraire la description depuis #column_in_details
              const columnDetailsDesc = document.querySelector('#column_in_details');
              if (columnDetailsDesc) {
                // Prendre tout le texte après le lien du site et avant #separation2
                const separation2 = columnDetailsDesc.querySelector('#separation2');
                if (separation2) {
                  // Récupérer le texte entre le lien et separation2
                  const allText = columnDetailsDesc.textContent || '';
                  const linkIndex = allText.indexOf('http://');
                  if (linkIndex >= 0) {
                    // Extraire le texte après le lien jusqu'à separation2
                    let descText = allText.substring(linkIndex);
                    // Trouver la fin du lien (premier saut de ligne ou espace après .net/.com/etc)
                    const linkEndMatch = descText.match(/https?:\/\/[^\s]+/);
                    if (linkEndMatch) {
                      const linkEnd = linkEndMatch[0].length;
                      descText = descText.substring(linkEnd).trim();
                      // Prendre jusqu'à separation2
                      const sepIndexInDesc = descText.indexOf('separation2');
                      if (sepIndexInDesc > 0) {
                        descText = descText.substring(0, sepIndexInDesc).trim();
                      }
                      // Nettoyer les sauts de ligne multiples
                      descText = descText.replace(/\s+/g, ' ').trim();
                      if (descText.length > 20 && descText.length < 1000) {
                        data.description = descText;
                      }
                    }
                  }
                }
              }
              
              // Fallback: chercher dans meta description ou autres sélecteurs
              if (!data.description) {
                const descSelectors = [
                  'meta[name="description"]',
                  '.description',
                  '[class*="description"]',
                  'p',
                  '[class*="desc"]',
                ];
                for (const selector of descSelectors) {
                  const el = document.querySelector(selector);
                  if (el) {
                    const text = el.getAttribute('content') || el.textContent?.trim() || '';
                    if (text && text.length > 20 && text.length < 1000) {
                      data.description = text;
                      break;
                    }
                  }
                }
              }

              // Extraire l'email
              const emailLink = document.querySelector('a[href^="mailto:"]');
              if (emailLink) {
                data.email = emailLink.getAttribute('href')?.replace('mailto:', '').split('?')[0].trim() || '';
              } else {
                // Chercher dans le texte avec regex
                const emailPattern = /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g;
                const bodyText = document.body?.textContent || '';
                const emailMatch = bodyText.match(emailPattern);
                if (emailMatch && emailMatch.length > 0) {
                  data.email = emailMatch[0];
                }
              }

              // Extraire le téléphone
              const phoneLink = document.querySelector('a[href^="tel:"]');
              if (phoneLink) {
                data.phone = phoneLink.getAttribute('href')?.replace('tel:', '').trim() || '';
              } else {
                // Chercher dans le texte avec regex (format français)
                const phonePattern = /(?:(?:\+|00)33|0)\s*[1-9](?:[\s.-]*\d{2}){4}/g;
                const bodyText = document.body?.textContent || '';
                const phoneMatch = bodyText.match(phonePattern);
                if (phoneMatch && phoneMatch.length > 0) {
                  data.phone = phoneMatch[0].replace(/\s+/g, '');
                }
              }

              // Extraire l'adresse
              const addressSelectors = [
                'address',
                '[class*="address"]',
                '[class*="adresse"]',
                '[class*="location"]',
                '[itemprop="address"]',
                '.contact-info',
              ];
              for (const selector of addressSelectors) {
                const el = document.querySelector(selector);
                if (el) {
                  const text = el.textContent?.trim() || '';
                  if (text && text.length > 10 && text.length < 300) {
                    data.address = text;
                    break;
                  }
                }
              }

              // Extraire le site web depuis #column_in_details
              const columnDetails = document.querySelector('#column_in_details');
              if (columnDetails) {
                // Chercher le texte "Lien du site :" et prendre le lien suivant
                const linkLabel = Array.from(columnDetails.querySelectorAll('b, strong')).find(
                  (el: Element) => el.textContent?.includes('Lien du site')
                );
                if (linkLabel) {
                  // Trouver le lien dans le même conteneur ou parent
                  const container = linkLabel.parentElement || linkLabel.closest('div');
                  const websiteLink = container?.querySelector('a[href^="http"]');
                  if (websiteLink) {
                    data.website = websiteLink.getAttribute('href') || '';
                  }
                }
              }
              
              // Fallback: chercher tous les liens externes si pas trouvé dans column_in_details
              if (!data.website) {
                const websiteLinks = document.querySelectorAll('a[href^="http"]:not([href*="annuaireimmo"]):not([href^="mailto"]):not([href^="tel"])');
                if (websiteLinks.length > 0) {
                  // Prendre le premier lien externe qui semble être un site web
                  for (const link of Array.from(websiteLinks)) {
                    const href = link.getAttribute('href') || '';
                    if (href && !href.includes('facebook') && !href.includes('twitter') && !href.includes('linkedin')) {
                      data.website = href;
                      break;
                    }
                  }
                }
              }

              return data;
            });

            // Traiter les données extraites (sans validation)
            const name = cleanText(agencyData.name) || cleanText(detailLink.name);
            if (!name || name.length < 3) {
              continue;
            }

            const email = agencyData.email ? cleanText(agencyData.email) || undefined : undefined;
            const phone = agencyData.phone ? cleanText(agencyData.phone) || undefined : undefined;
            const website = agencyData.website ? cleanText(agencyData.website) || undefined : undefined;
            const address = cleanText(agencyData.address) || undefined;
            const description = cleanText(agencyData.description) || undefined;

            // Extraire ville et code postal de l'adresse
            let cityName: string | undefined;
            let postalCode: string | undefined;
            if (address) {
              const addressParts = address.split(',');
              if (addressParts.length > 0) {
                const lastPart = addressParts[addressParts.length - 1].trim();
                const postalMatch = lastPart.match(/(\d{5})\s+(.+)/);
                if (postalMatch) {
                  postalCode = postalMatch[1];
                  cityName = postalMatch[2];
                } else {
                  cityName = lastPart;
                }
              }
            }

            const agency: Agency = {
              name,
              description,
              email,
              phone,
              address,
              city: cityName || undefined,
              postalCode: postalCode,
              country: 'France',
              website: website,
              domain: website ? (extractDomain(website) ?? undefined) : undefined,
              source: this.getName(),
              sourceUrl: detailLink.url,
            };

            agencies.push(agency);

            // Retourner à la page de liste
            await page.goto(pageUrl, {
              waitUntil: 'networkidle2',
              timeout: config.scraping.timeout,
            });
            await this.delay(1500);

          } catch (error: any) {
            // Essayer de retourner à la page de liste en cas d'erreur
            try {
            await page.goto(pageUrl, {
              waitUntil: 'networkidle2',
              timeout: config.scraping.timeout,
            });
            await this.delay(1500);
            } catch (e) {
              // Ignore
            }
          }
        }

        // Vérifier s'il y a une page suivante
        const hasNextPage = await page.evaluate(() => {
          // Chercher les liens de pagination
          const paginationLinks = document.querySelectorAll('a[href*="page"], .pagination a, [class*="pagination"] a');
          const currentPageText = document.body.textContent || '';
          
          // Vérifier s'il y a un lien "suivant" ou un numéro de page supérieur
          for (const link of Array.from(paginationLinks)) {
            const text = link.textContent?.toLowerCase() || '';
            const href = link.getAttribute('href') || '';
            if (text.includes('suivant') || text.includes('next') || 
                (href.includes('page=') && parseInt(href.match(/page=(\d+)/)?.[1] || '0') > currentPage)) {
              return true;
            }
          }
          
          // Vérifier s'il y a un texte indiquant plus de pages
          const pageMatch = currentPageText.match(/(\d+)\s+sur\s+(\d+)/);
          if (pageMatch) {
            const current = parseInt(pageMatch[1]);
            const total = parseInt(pageMatch[2]);
            return current < total;
          }
          
          return false;
        });

        if (!hasNextPage || detailLinks.length === 0) {
          hasMorePages = false;
        } else {
          currentPage++;
          // Limiter à 50 pages par catégorie pour éviter les boucles infinies
          if (currentPage > 50) {
            hasMorePages = false;
          }
        }

        await this.delay(1500); // Délai entre les pages
      } catch (error: any) {
        hasMorePages = false;
      }
    }

    return agencies;
  }
}

