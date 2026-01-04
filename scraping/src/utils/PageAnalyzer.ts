import { Page } from 'puppeteer';
import logger from './logger';

export interface PageStructure {
  title: string;
  url: string;
  selectors: {
    containers: string[];
    names: string[];
    emails: string[];
    phones: string[];
    addresses: string[];
    websites: string[];
  };
  sampleData: {
    containerCount: number;
    sampleNames: string[];
    sampleEmails: string[];
    samplePhones: string[];
  };
}

export class PageAnalyzer {
  /**
   * Analyze a page structure to find the best selectors
   */
  async analyzePage(page: Page, url: string): Promise<PageStructure> {
    logger.info(`Analyzing page structure: ${url}`);

    const structure = await page.evaluate(() => {
      const result: PageStructure = {
        title: document.title,
        url: window.location.href,
        selectors: {
          containers: [],
          names: [],
          emails: [],
          phones: [],
          addresses: [],
          websites: [],
        },
        sampleData: {
          containerCount: 0,
          sampleNames: [],
          sampleEmails: [],
          samplePhones: [],
        },
      };

      // Find potential container selectors
      const containerCandidates = [
        '.agency', '.agency-card', '.agency-item', '.agency-listing',
        '.result', '.result-item', '.listing', '.listing-item',
        '.card', '.item', '.company', '.company-card',
        '[class*="agency"]', '[class*="result"]', '[class*="listing"]',
        'article', '.post', '.entry'
      ];

      for (const selector of containerCandidates) {
        const elements = document.querySelectorAll(selector);
        if (elements.length > 0) {
          result.selectors.containers.push(selector);
          result.sampleData.containerCount = elements.length;
          break;
        }
      }

      // Find name selectors
      const nameCandidates = [
        'h1', 'h2', 'h3', '.name', '.agency-name', '.company-name',
        '.title', '[class*="name"]', '[class*="title"]',
        'a[href*="/agence"]', 'a[href*="/agency"]'
      ];

      for (const selector of nameCandidates) {
        const elements = document.querySelectorAll(selector);
        if (elements.length > 0) {
          result.selectors.names.push(selector);
          // Get sample names
          Array.from(elements).slice(0, 3).forEach((el) => {
            const text = el.textContent?.trim() || '';
            if (text.length > 5 && text.length < 200 && !result.sampleData.sampleNames.includes(text)) {
              result.sampleData.sampleNames.push(text);
            }
          });
        }
      }

      // Find email selectors
      const emailElements = document.querySelectorAll('a[href^="mailto:"]');
      if (emailElements.length > 0) {
        result.selectors.emails.push('a[href^="mailto:"]');
        Array.from(emailElements).slice(0, 3).forEach((el) => {
          const email = el.getAttribute('href')?.replace('mailto:', '') || '';
          if (email && !result.sampleData.sampleEmails.includes(email)) {
            result.sampleData.sampleEmails.push(email);
          }
        });
      }

      // Find phone selectors
      const phoneElements = document.querySelectorAll('a[href^="tel:"]');
      if (phoneElements.length > 0) {
        result.selectors.phones.push('a[href^="tel:"]');
        Array.from(phoneElements).slice(0, 3).forEach((el) => {
          const phone = el.getAttribute('href')?.replace('tel:', '') || '';
          if (phone && !result.sampleData.samplePhones.includes(phone)) {
            result.sampleData.samplePhones.push(phone);
          }
        });
      }

      // Find address selectors
      const addressCandidates = [
        '[class*="address"]', '[class*="adresse"]', '[class*="location"]',
        'address', '.location', '.contact-info'
      ];

      for (const selector of addressCandidates) {
        const elements = document.querySelectorAll(selector);
        if (elements.length > 0) {
          result.selectors.addresses.push(selector);
        }
      }

      // Find website selectors
      const websiteElements = document.querySelectorAll('a[href^="http"]:not([href*="' + window.location.hostname + '"])');
      if (websiteElements.length > 0) {
        result.selectors.websites.push('a[href^="http"]');
      }

      return result;
    });

    logger.info(`Page analysis complete: ${structure.sampleData.containerCount} containers found`);
    logger.info(`Sample names: ${structure.sampleData.sampleNames.length}`);
    logger.info(`Sample emails: ${structure.sampleData.sampleEmails.length}`);
    logger.info(`Sample phones: ${structure.sampleData.samplePhones.length}`);

    return structure;
  }

  /**
   * Generate optimized selectors based on page analysis
   */
  generateSelectors(structure: PageStructure): {
    container: string;
    name: string;
    email: string;
    phone: string;
    address: string;
    website: string;
  } {
    return {
      container: structure.selectors.containers[0] || '.result, .item, .card',
      name: structure.selectors.names[0] || 'h2, h3, .name',
      email: structure.selectors.emails[0] || 'a[href^="mailto:"]',
      phone: structure.selectors.phones[0] || 'a[href^="tel:"]',
      address: structure.selectors.addresses[0] || '[class*="address"]',
      website: structure.selectors.websites[0] || 'a[href^="http"]',
    };
  }
}

