import { PuppeteerScraper } from './PuppeteerScraper';
import { Agency } from '../models/Agency';
import logger from '../utils/logger';
import { config } from '../config/config';
import {
  validatePhone,
  validateUrl,
  extractDomain,
  cleanText,
} from '../utils/validators';

export class PagesJaunesScraper extends PuppeteerScraper {
  getName(): string {
    return 'PagesJaunes';
  }

  async scrape(searchTerm: string, city?: string): Promise<Agency[]> {
    const agencies: Agency[] = [];
    const query = city ? `${searchTerm} ${city}` : searchTerm;
    const searchUrl = `https://www.pagesjaunes.fr/recherche/${encodeURIComponent(query)}`;


    let page;
    try {
      page = await this.createPage();
      
      await page.goto(searchUrl, {
        waitUntil: 'networkidle2',
        timeout: config.scraping.timeout,
      });

      await this.delay(4000); // Wait for dynamic content

      // Debug page
      await this.debugPage(page, this.getName(), searchUrl);

      // Extract results using Puppeteer with multiple strategies
      const results = await page.evaluate(() => {
        const agencies: any[] = [];
        
        // Strategy 1: Try multiple container selectors
        const containerSelectors = [
          '.bi-container', '.bi-content', '[data-role="result"]',
          '.result-item', '.listing-item', '.card', '.result',
          '[class*="result"]', '[class*="listing"]', '[class*="card"]'
        ];
        
        let resultElements: NodeListOf<Element> | null = null;
        for (const selector of containerSelectors) {
          resultElements = document.querySelectorAll(selector);
          if (resultElements.length > 0) {
            console.log(`Found ${resultElements.length} elements with selector: ${selector}`);
            break;
          }
        }

        if (!resultElements || resultElements.length === 0) {
          console.log('No results found, trying to find any elements with agency-related text...');
          // Fallback: find any elements containing agency keywords
          const allElements = document.querySelectorAll('*');
          allElements.forEach((el: Element) => {
            const text = el.textContent?.toLowerCase() || '';
            if (text.includes('agence') && text.includes('immobilier') && text.length < 200) {
              const name = el.textContent?.trim() || '';
              if (name.length > 5 && name.length < 100) {
                agencies.push({
                  name,
                  address: '',
                  phone: '',
                  website: '',
                  description: '',
                });
              }
            }
          });
          return agencies;
        }
        
        resultElements.forEach((element: Element) => {
          try {
            // Try multiple name selectors
            const nameSelectors = [
              '.denomination-links', 'h2 a', '.denomination', 
              '[data-role="title"]', 'h2', 'h3', '.name', '.title',
              'a[href*="/pro/"]', '.company-name'
            ];
            let nameElement: Element | null = null;
            for (const sel of nameSelectors) {
              nameElement = element.querySelector(sel);
              if (nameElement) break;
            }

            // Try multiple address selectors
            const addressSelectors = [
              '.adresse', '.address', '[data-role="address"]',
              '.location', '[class*="address"]', '[class*="adresse"]'
            ];
            let addressElement: Element | null = null;
            for (const sel of addressSelectors) {
              addressElement = element.querySelector(sel);
              if (addressElement) break;
            }

            // Try multiple phone selectors
            const phoneSelectors = [
              '.num', '.phone', '[data-role="phone"]',
              'a[href^="tel:"]', '[class*="phone"]', '[class*="tel"]'
            ];
            let phoneElement: Element | null = null;
            for (const sel of phoneSelectors) {
              phoneElement = element.querySelector(sel);
              if (phoneElement) break;
            }

            // Try multiple website selectors
            const websiteSelectors = [
              '.site-web a', '.website a', '[data-role="website"]',
              'a[href^="http"]', 'a[target="_blank"]'
            ];
            let websiteElement: Element | null = null;
            for (const sel of websiteSelectors) {
              websiteElement = element.querySelector(sel);
              if (websiteElement && websiteElement.getAttribute('href')?.startsWith('http')) break;
            }

            const descriptionElement = element.querySelector('.activite, .activity, .description');

            const name = nameElement?.textContent?.trim() || '';
            const address = addressElement?.textContent?.trim() || '';
            const phone = phoneElement?.textContent?.trim() || phoneElement?.getAttribute('href')?.replace('tel:', '') || '';
            const website = websiteElement?.getAttribute('href') || websiteElement?.textContent?.trim() || '';
            const description = descriptionElement?.textContent?.trim() || '';

            if (name && name.length > 3) {
              agencies.push({
                name,
                address,
                phone,
                website,
                description,
              });
            }
          } catch (error) {
            console.error('Error parsing result:', error);
          }
        });

        return agencies;
      });


      // Process results
      for (const result of results) {
        try {
          const name = cleanText(result.name);
          if (!name) continue;

          const address = cleanText(result.address);
          const phoneText = cleanText(result.phone);
          const phone = phoneText ? validatePhone(phoneText) : null;
          const websiteText = result.website;
          const website = websiteText ? validateUrl(websiteText) : null;
          const description = cleanText(result.description);

          // Extract city and postal code from address
          const addressParts = address?.split(',') || [];
          let cityName = city;
          let postalCode: string | undefined;

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

          const agency: Agency = {
            name: name,
            description: description || undefined,
            email: undefined, // PagesJaunes usually doesn't show emails
            phone: phone || undefined,
            address: address || undefined,
            city: cityName || city || undefined,
            postalCode: postalCode,
            country: 'France',
            website: website ?? undefined,
            domain: website ? (extractDomain(website) ?? undefined) : undefined,
            source: this.getName(),
            sourceUrl: searchUrl,
          };

          agencies.push(agency);
        } catch (error: any) {
        }
      }

      if (results.length > 0) {
      } else {
      }
      
      await page.close();
      await this.delay();
    } catch (error: any) {
      logger.error(`Error scraping PagesJaunes: ${error.message}`);
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
}

