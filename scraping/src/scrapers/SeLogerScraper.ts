import { PuppeteerScraper } from './PuppeteerScraper';
import { Agency } from '../models/Agency';
import logger from '../utils/logger';
import {
  validatePhone,
  validateUrl,
  extractDomain,
  cleanText,
} from '../utils/validators';
import { config } from '../config/config';

export class SeLogerScraper extends PuppeteerScraper {
  getName(): string {
    return 'SeLoger';
  }

  async scrape(searchTerm: string, city?: string): Promise<Agency[]> {
    const agencies: Agency[] = [];
    const query = city ? `${searchTerm} ${city}` : searchTerm;
    const searchUrl = `https://www.seloger.com/list.htm?types=1,2&projects=1,2&enterprise=0&rooms=1,2,3,4,5&surface=25&price=NaN/NaN&mandatorycommodities=0&enterprise=0&qsVersion=1.0&LISTING-LISTpg=1`;

    logger.info(`Scraping SeLoger for: ${query}`);

    let page;
    try {
      page = await this.createPage();
      
      await page.goto(searchUrl, {
        waitUntil: 'networkidle2',
        timeout: config.scraping.timeout,
      });

      await this.delay(3000);

      // Extract agency information from listings
      const results = await page.evaluate(() => {
        const agencies: any[] = [];
        const agencyElements = document.querySelectorAll('.c-pa-link, .agency-card, [data-agency]');
        
        agencyElements.forEach((element: Element) => {
          try {
            const nameElement = element.querySelector('.agency-name, h3, .name');
            const addressElement = element.querySelector('.agency-address, .address');
            const phoneElement = element.querySelector('.agency-phone, .phone');
            const websiteElement = element.querySelector('.agency-website a, .website a');

            const name = nameElement?.textContent?.trim() || '';
            const address = addressElement?.textContent?.trim() || '';
            const phone = phoneElement?.textContent?.trim() || '';
            const website = websiteElement?.getAttribute('href') || websiteElement?.textContent?.trim() || '';

            if (name) {
              agencies.push({
                name,
                address,
                phone,
                website,
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
          const website = result.website ? validateUrl(result.website) : null;

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
            phone: phone || undefined,
            address: address || undefined,
            city: cityName || city || undefined,
            postalCode: postalCode,
            country: 'France',
            website: website || undefined,
            domain: website ? (extractDomain(website) ?? undefined) : undefined,
            source: this.getName(),
            sourceUrl: searchUrl,
          };

          agencies.push(agency);
        } catch (error: any) {
          logger.warn(`Error processing SeLoger result: ${error.message}`);
        }
      }

      logger.info(`Found ${agencies.length} agencies from SeLoger`);
      await page.close();
      await this.delay();
    } catch (error: any) {
      logger.error(`Error scraping SeLoger: ${error.message}`);
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

