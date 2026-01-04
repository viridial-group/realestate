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

export class RealEstateMarketingTalkScraper extends PuppeteerScraper {
  getName(): string {
    return 'RealEstateMarketingTalk';
  }

  async scrape(searchTerm: string, city?: string): Promise<Agency[]> {
    const agencies: Agency[] = [];
    const query = city ? `${searchTerm} ${city}` : searchTerm;
    const searchUrl = `https://www.real-estate-marketing-talk.com/search?q=${encodeURIComponent(query)}`;


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


      // Extract results - this site might be a blog/forum, so we'll look for agency mentions
      const results = await page.evaluate((selectors) => {
        const agencies: any[] = [];
        
        // Look for agency listings or mentions
        const containerSelectors = [
          selectors.container,
          '.agency', '.company', '.listing', '.result',
          '[class*="agency"]', '[class*="company"]', 'article', '.post'
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
          // Fallback: search for links and text containing agency keywords
          const allLinks = document.querySelectorAll('a[href*="agence"], a[href*="agency"], a[href*="immobilier"]');
          allLinks.forEach((link: Element) => {
            const text = link.textContent?.trim() || '';
            const href = link.getAttribute('href') || '';
            if (text.length > 5 && (href.startsWith('http') || href.startsWith('/'))) {
              agencies.push({
                name: text,
                url: href.startsWith('http') ? href : `https://www.real-estate-marketing-talk.com${href}`,
              });
            }
          });
          return agencies;
        }
        
        resultElements.forEach((element: Element) => {
          try {
            const nameElement = element.querySelector(selectors.name || 'h1, h2, h3, .name, .title, .company-name');
            const emailElement = element.querySelector(selectors.email || 'a[href^="mailto:"]');
            const phoneElement = element.querySelector(selectors.phone || 'a[href^="tel:"]') || 
                                element.querySelector('[class*="phone"]');
            const addressElement = element.querySelector(selectors.address || '[class*="address"], [class*="location"]');
            const websiteElement = element.querySelector(selectors.website || 'a[href^="http"]:not([href*="real-estate-marketing-talk"])');

            let name = nameElement?.textContent?.trim() || '';
            let email = emailElement?.getAttribute('href')?.replace('mailto:', '') || '';
            let phone = phoneElement?.getAttribute('href')?.replace('tel:', '') || 
                         phoneElement?.textContent?.trim() || '';
            const address = addressElement?.textContent?.trim() || '';
            const website = websiteElement?.getAttribute('href') || '';

            // Also extract from text content if structured data not found
            if (!name || !email || !phone) {
              const text = element.textContent || '';
              const emailPattern = /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g;
              const phonePattern = /(?:\+33|0)[1-9](?:[.\s-]?\d{2}){4}/g;
              
              if (!email) {
                const emailMatch = text.match(emailPattern);
                if (emailMatch) email = emailMatch[0];
              }
              
              if (!phone) {
                const phoneMatch = text.match(phonePattern);
                if (phoneMatch) phone = phoneMatch[0].replace(/[.\s-]/g, '');
              }
            }

            if (name || website) {
              agencies.push({
                name: name || 'Agency',
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
      logger.error(`Error scraping RealEstateMarketingTalk: ${error.message}`);
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

