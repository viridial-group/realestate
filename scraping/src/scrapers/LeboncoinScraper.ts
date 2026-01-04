import { PuppeteerScraper } from './PuppeteerScraper';
import { Agency } from '../models/Agency';
import logger from '../utils/logger';
import { config } from '../config/config';
import {
  validateUrl,
  extractDomain,
  cleanText,
} from '../utils/validators';

export class LeboncoinScraper extends PuppeteerScraper {
  getName(): string {
    return 'Leboncoin';
  }

  async scrape(searchTerm: string, city?: string): Promise<Agency[]> {
    const agencies: Agency[] = [];
    const location = city || 'toute-la-france';
    const searchUrl = `https://www.leboncoin.fr/recherche?text=${encodeURIComponent(searchTerm)}&locations=${location}`;

    logger.info(`Scraping Leboncoin for: ${searchTerm} in ${city || 'France'}`);

    let page;
    try {
      page = await this.createPage();
      
      await page.goto(searchUrl, {
        waitUntil: 'networkidle2',
        timeout: config.scraping.timeout,
      });

      await this.delay(3000); // Wait for dynamic content

      // Extract results using Puppeteer
      const results = await page.evaluate(() => {
        const agencies: any[] = [];
        const resultElements = document.querySelectorAll('[data-qa-id="aditem_container"], .aditem, .ad-listitem');
        
        resultElements.forEach((element: Element) => {
          try {
            const titleElement = element.querySelector('.aditem_title, h2, [data-qa-id="aditem_title"]');
            const locationElement = element.querySelector('.item_supp, .location, [data-qa-id="aditem_location"]');
            const linkElement = element.querySelector('a');

            const title = titleElement?.textContent?.trim() || '';
            const location = locationElement?.textContent?.trim() || '';
            const link = linkElement?.getAttribute('href') || '';

            // Try to extract agency name from title or link
            if (title && (title.toLowerCase().includes('agence') || title.toLowerCase().includes('immobilier'))) {
              agencies.push({
                name: title.substring(0, 100),
                location,
                link: link.startsWith('http') ? link : `https://www.leboncoin.fr${link}`,
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

          const agency: Agency = {
            name: name,
            city: city || cleanText(result.location) || undefined,
            country: 'France',
            website: validateUrl(result.link) || undefined,
            domain: extractDomain(result.link) || undefined,
            source: this.getName(),
            sourceUrl: searchUrl,
          };

          agencies.push(agency);
        } catch (error: any) {
          logger.warn(`Error processing Leboncoin result: ${error.message}`);
        }
      }

      logger.info(`Found ${agencies.length} agencies from Leboncoin`);
      await page.close();
      await this.delay();
    } catch (error: any) {
      logger.error(`Error scraping Leboncoin: ${error.message}`);
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

