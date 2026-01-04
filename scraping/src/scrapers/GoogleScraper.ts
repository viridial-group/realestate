import { PuppeteerScraper } from './PuppeteerScraper';
import { Agency } from '../models/Agency';
import logger from '../utils/logger';
import { config } from '../config/config';
import { WebsiteAnalyzer } from './WebsiteAnalyzer';
import {
  validateUrl,
  extractDomain,
  cleanText,
} from '../utils/validators';

export class GoogleScraper extends PuppeteerScraper {
  private websiteAnalyzer: WebsiteAnalyzer;

  constructor() {
    super();
    this.websiteAnalyzer = new WebsiteAnalyzer();
  }

  getName(): string {
    return 'Google';
  }

  async scrape(searchTerm: string, city?: string): Promise<Agency[]> {
    const agencies: Agency[] = [];
    const query = city ? `${searchTerm} ${city}` : searchTerm;
    const searchUrl = `https://www.google.com/search?q=${encodeURIComponent(query)}&num=20`;

    logger.info(`Scraping Google for: ${query}`);

    let page;
    try {
      page = await this.createPage();
      
      // Navigate to Google search
      await page.goto(searchUrl, {
        waitUntil: 'networkidle2',
        timeout: config.scraping.timeout,
      });

      await this.delay(3000); // Wait for page to load

      // Debug page
      await this.debugPage(page, this.getName(), searchUrl);

      // Check if we're blocked or redirected
      const currentUrl = page.url();
      if (currentUrl.includes('sorry') || currentUrl.includes('captcha')) {
        logger.warn('Google blocked the request or showed captcha');
        await page.close();
        return agencies;
      }

      // Extract results from Google search with multiple selector strategies
      const results = await page.evaluate(() => {
        const agencies: any[] = [];
        
        // Strategy 1: Standard Google results
        const selectors = [
          '.g', '.tF2Cxc', '.MjjYud', '.yuRUbf', 
          '[data-ved]', '.Gx5Zad', '.xpdopen'
        ];
        
        let resultElements: NodeListOf<Element> | null = null;
        for (const selector of selectors) {
          resultElements = document.querySelectorAll(selector);
          if (resultElements.length > 0) {
            console.log(`Found ${resultElements.length} elements with selector: ${selector}`);
            break;
          }
        }

        if (!resultElements || resultElements.length === 0) {
          console.log('No results found with standard selectors, trying alternative...');
          // Try to find any links with text containing keywords
          const allLinks = document.querySelectorAll('a[href*="http"]');
          allLinks.forEach((link: Element) => {
            const text = link.textContent?.toLowerCase() || '';
            const href = link.getAttribute('href') || '';
            if ((text.includes('agence') || text.includes('immobilier')) && href.startsWith('http')) {
              agencies.push({
                name: link.textContent?.trim() || '',
                url: href,
                description: '',
              });
            }
          });
          return agencies;
        }
        
        resultElements.forEach((element: Element) => {
          try {
            // Try multiple title selectors
            const titleSelectors = ['h3', '.LC20lb', '.DKV0Md', '.r', 'a h3'];
            let titleElement: Element | null = null;
            for (const sel of titleSelectors) {
              titleElement = element.querySelector(sel);
              if (titleElement) break;
            }

            // Try multiple link selectors
            const linkElement = element.querySelector('a[href*="http"]') || element.querySelector('a');
            
            // Try multiple snippet selectors
            const snippetSelectors = ['.VwiC3b', '.s', '.IsZvec', '.st', '.aCOpRe'];
            let snippetElement: Element | null = null;
            for (const sel of snippetSelectors) {
              snippetElement = element.querySelector(sel);
              if (snippetElement) break;
            }

            const name = titleElement?.textContent?.trim() || '';
            const url = linkElement?.getAttribute('href') || '';
            const description = snippetElement?.textContent?.trim() || '';

            // More flexible matching
            const nameLower = name.toLowerCase();
            if (name && name.length > 3 && (
              nameLower.includes('agence') || 
              nameLower.includes('immobilier') ||
              nameLower.includes('immo') ||
              url.includes('agence') ||
              url.includes('immobilier')
            )) {
              agencies.push({
                name,
                url,
                description,
              });
            }
          } catch (error) {
            console.error('Error parsing result:', error);
          }
        });

        return agencies;
      });

      logger.info(`Extracted ${results.length} potential results from Google page`);

      // Process results - Step 1: Extract domains from Google
      const domainsToAnalyze: Array<{ url: string; name: string }> = [];
      
      for (const result of results) {
        try {
          const name = cleanText(result.name);
          if (!name) continue;

          let url = result.url.startsWith('/url?q=')
            ? decodeURIComponent(result.url.split('&')[0].replace('/url?q=', ''))
            : result.url;

          // Clean and validate URL
          if (!url.startsWith('http')) {
            continue;
          }

          const validatedUrl = validateUrl(url);
          if (!validatedUrl) continue;

          domainsToAnalyze.push({
            url: validatedUrl,
            name: name,
          });
        } catch (error: any) {
          logger.warn(`Error processing Google result: ${error.message}`);
        }
      }

      logger.info(`Found ${domainsToAnalyze.length} domains to analyze`);

      // Step 2: Analyze each website to extract detailed information
      for (const domainInfo of domainsToAnalyze.slice(0, 10)) { // Limit to 10 to avoid timeout
        try {
          logger.info(`Analyzing website: ${domainInfo.url}`);
          
          const analyzedAgency = await this.websiteAnalyzer.analyzeWebsite(
            page,
            domainInfo.url,
            domainInfo.name
          );

          // Merge with basic info from Google
          const agency: Agency = {
            name: analyzedAgency.name || domainInfo.name,
            description: analyzedAgency.description || undefined,
            email: analyzedAgency.email,
            phone: analyzedAgency.phone,
            address: analyzedAgency.address,
            city: analyzedAgency.city || city || undefined,
            postalCode: analyzedAgency.postalCode,
            country: analyzedAgency.country || 'France',
            website: analyzedAgency.website || domainInfo.url,
            domain: analyzedAgency.domain || extractDomain(domainInfo.url) || undefined,
            source: this.getName(),
            sourceUrl: searchUrl,
          };

          agencies.push(agency);
          
          // Delay between website analyses
          await this.delay(3000);
        } catch (error: any) {
          logger.warn(`Error analyzing website ${domainInfo.url}: ${error.message}`);
        }
      }

      if (results.length > 0) {
        logger.info(`Found ${results.length} potential results, processing...`);
      } else {
        logger.warn('No results found on Google page. Page might be blocked or structure changed.');
        // Save page HTML for debugging (optional)
        // const html = await page.content();
        // logger.debug(`Page HTML length: ${html.length}`);
      }
      
      logger.info(`Found ${agencies.length} agencies from Google after processing`);
      await page.close();
      await this.delay();
    } catch (error: any) {
      logger.error(`Error scraping Google: ${error.message}`);
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

