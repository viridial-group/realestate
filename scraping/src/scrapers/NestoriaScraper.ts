import { PuppeteerScraper } from './PuppeteerScraper';
import { Agency } from '../models/Agency';
import logger from '../utils/logger';
import { config } from '../config/config';
import { PageAnalyzer } from '../utils/PageAnalyzer';
import {
  validateEmail,
  validatePhone,
  validateUrl,
  extractDomain,
  cleanText,
} from '../utils/validators';

export class NestoriaScraper extends PuppeteerScraper {
  getName(): string {
    return 'Nestoria';
  }

  async scrape(searchTerm: string, city?: string): Promise<Agency[]> {
    const agencies: Agency[] = [];
    const location = city || 'france';
    const searchUrl = `https://www.nestoria.fr/${location}/agence-immobiliere`;


    let page;
    try {
      page = await this.createPage();
      
      await page.goto(searchUrl, {
        waitUntil: 'networkidle2',
        timeout: config.scraping.timeout,
      });

      await this.delay(3000);

      await this.debugPage(page, this.getName(), searchUrl);

      // Analyze page structure
      const pageAnalyzer = new PageAnalyzer();
      const pageStructure = await pageAnalyzer.analyzePage(page, searchUrl);
      const optimalSelectors = pageAnalyzer.generateSelectors(pageStructure);


      // Extract results using analyzed selectors
      const results = await page.evaluate((selectors) => {
        const agencies: any[] = [];
        
        const containerSelectors = [
          selectors.container,
          '.agency', '.agent', '.listing', '.result',
          '[class*="agency"]', '[class*="agent"]', '.card'
        ];
        
        let resultElements: NodeListOf<Element> | null = null;
        for (const selector of containerSelectors) {
          if (!selector) continue;
          resultElements = document.querySelectorAll(selector);
          if (resultElements.length > 0) {
            console.log(`Found ${resultElements.length} elements with selector: ${selector}`);
            break;
          }
        }

        if (!resultElements || resultElements.length === 0) {
          const allLinks = document.querySelectorAll('a[href*="/agence"], a[href*="/agent"]');
          allLinks.forEach((link: Element) => {
            const text = link.textContent?.trim() || '';
            if (text.length > 5) {
              agencies.push({
                name: text,
                url: link.getAttribute('href') || '',
              });
            }
          });
          return agencies;
        }
        
        resultElements.forEach((element: Element) => {
          try {
            const nameElement = element.querySelector(selectors.name || 'h2, h3, .name, .agency-name, .title');
            const emailElement = element.querySelector(selectors.email || 'a[href^="mailto:"]');
            const phoneElement = element.querySelector(selectors.phone || 'a[href^="tel:"]') || 
                                element.querySelector('[class*="phone"]');
            const addressElement = element.querySelector(selectors.address || '[class*="address"], [class*="location"]');
            const websiteElement = element.querySelector(selectors.website || 'a[href^="http"]:not([href*="nestoria"])');

            const name = nameElement?.textContent?.trim() || '';
            const email = emailElement?.getAttribute('href')?.replace('mailto:', '') || '';
            const phone = phoneElement?.getAttribute('href')?.replace('tel:', '') || 
                         phoneElement?.textContent?.trim() || '';
            const address = addressElement?.textContent?.trim() || '';
            const website = websiteElement?.getAttribute('href') || '';

            if (name) {
              agencies.push({
                name,
                email,
                phone,
                address,
                website,
              });
            }
          } catch (error) {
            console.error('Error parsing result:', error);
          }
        });

        return agencies;
      }, optimalSelectors);

      // Process results
      for (const result of results) {
        try {
          const name = cleanText(result.name);
          if (!name) continue;

          const email = result.email ? (validateEmail(result.email) || undefined) : undefined;
          const phone = result.phone ? (validatePhone(result.phone) || undefined) : undefined;
          const website = result.website ? (validateUrl(result.website) || undefined) : undefined;
          const address = cleanText(result.address);

          let cityName = city;
          let postalCode: string | undefined;
          if (address) {
            const addressParts = address.split(',');
            if (addressParts.length > 0) {
              const lastPart = addressParts[addressParts.length - 1].trim();
              const postalMatch = lastPart.match(/(\d{5})\s+(.+)/);
              if (postalMatch) {
                postalCode = postalMatch[1];
                cityName = postalMatch[2];
              }
            }
          }

          const agency: Agency = {
            name: name,
            email: email,
            phone: phone,
            address: address || undefined,
            city: cityName || city || undefined,
            postalCode: postalCode,
            country: 'France',
            website: website,
            domain: website ? (extractDomain(website) ?? undefined) : undefined,
            source: this.getName(),
            sourceUrl: searchUrl,
          };

          agencies.push(agency);
        } catch (error: any) {
        }
      }

      await page.close();
      await this.delay();
    } catch (error: any) {
      logger.error(`Error scraping Nestoria: ${error.message}`);
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

