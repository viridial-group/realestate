import { Page } from 'puppeteer';
import logger from '../utils/logger';
import {
  validateEmail,
  validatePhone,
  validateUrl,
  extractDomain,
  cleanText,
} from '../utils/validators';
import { Agency } from '../models/Agency';

export class WebsiteAnalyzer {
  /**
   * Analyze a website to extract agency information
   */
  async analyzeWebsite(page: Page, url: string, agencyName?: string): Promise<Partial<Agency>> {
    const agency: Partial<Agency> = {
      website: url,
      domain: extractDomain(url) || undefined,
      source: 'Website Analysis',
      sourceUrl: url,
    };

    try {
      logger.info(`Analyzing website: ${url}`);

      // Navigate to the website
      await page.goto(url, {
        waitUntil: 'networkidle2',
        timeout: 30000,
      });

      await new Promise(resolve => setTimeout(resolve, 2000)); // Wait for page to load

      // Extract information from the page
      const extractedData = await page.evaluate((name) => {
        const data: any = {
          name: name || '',
          email: '',
          phone: '',
          address: '',
          description: '',
        };

        // Extract name from various sources
        if (!data.name) {
          const nameSelectors = [
            'h1', '.company-name', '.agency-name', '[class*="name"]',
            'title', 'meta[property="og:title"]', '.site-title'
          ];
          for (const selector of nameSelectors) {
            const el = document.querySelector(selector);
            if (el) {
              data.name = el.textContent?.trim() || el.getAttribute('content') || '';
              if (data.name && data.name.length > 3 && data.name.length < 200) break;
            }
          }
        }

        // Extract email - look for mailto links and email patterns
        const emailPattern = /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g;
        const mailtoLinks = document.querySelectorAll('a[href^="mailto:"]');
        if (mailtoLinks.length > 0) {
          mailtoLinks.forEach((link: Element) => {
            const href = link.getAttribute('href') || '';
            const email = href.replace('mailto:', '').split('?')[0].trim();
            if (email && email.match(emailPattern)) {
              data.email = email;
            }
          });
        }
        
        // Also search in text content
        if (!data.email) {
          const bodyText = document.body?.textContent || '';
          const emailMatches = bodyText.match(emailPattern);
          if (emailMatches && emailMatches.length > 0) {
            // Filter out common non-business emails
            const filtered = emailMatches.filter(e => 
              !e.includes('example.com') && 
              !e.includes('test.com') &&
              !e.includes('placeholder')
            );
            if (filtered.length > 0) {
              data.email = filtered[0];
            }
          }
        }

        // Extract phone - look for tel: links and phone patterns
        const phonePattern = /(?:\+33|0)[1-9](?:[.\s-]?\d{2}){4}/g;
        const telLinks = document.querySelectorAll('a[href^="tel:"]');
        if (telLinks.length > 0) {
          telLinks.forEach((link: Element) => {
            const href = link.getAttribute('href') || '';
            const phone = href.replace('tel:', '').replace(/\s+/g, '').trim();
            if (phone && phone.length >= 10) {
              data.phone = phone;
            }
          });
        }
        
        // Also search in text content
        if (!data.phone) {
          const bodyText = document.body?.textContent || '';
          const phoneMatches = bodyText.match(phonePattern);
          if (phoneMatches && phoneMatches.length > 0) {
            data.phone = phoneMatches[0].replace(/[.\s-]/g, '');
          }
        }

        // Extract address - look for address patterns
        const addressSelectors = [
          '[class*="address"]', '[class*="adresse"]', '[itemprop="address"]',
          '.contact-info', '.location', '[class*="location"]',
          'address', '[class*="contact"]'
        ];
        for (const selector of addressSelectors) {
          const el = document.querySelector(selector);
          if (el) {
            const text = el.textContent?.trim() || '';
            // French address pattern: number, street, postal code, city
            if (text.match(/\d+.*\d{5}/) && text.length > 10 && text.length < 200) {
              data.address = text;
              break;
            }
          }
        }

        // Extract description
        const descSelectors = [
          'meta[name="description"]', 'meta[property="og:description"]',
          '.description', '[class*="description"]', '.about', '[class*="about"]'
        ];
        for (const selector of descSelectors) {
          const el = document.querySelector(selector);
          if (el) {
            const text = el.getAttribute('content') || el.textContent?.trim() || '';
            if (text.length > 20 && text.length < 500) {
              data.description = text;
              break;
            }
          }
        }

        return data;
      }, agencyName);

      // Process extracted data
      if (extractedData.name) {
        agency.name = cleanText(extractedData.name) || agencyName || undefined;
      } else if (agencyName) {
        agency.name = agencyName;
      }

      if (extractedData.email) {
        const email = validateEmail(extractedData.email);
        if (email) {
          agency.email = email;
        }
      }

      if (extractedData.phone) {
        const phone = validatePhone(extractedData.phone);
        if (phone) {
          agency.phone = phone;
        }
      }

      if (extractedData.address) {
        agency.address = cleanText(extractedData.address) || undefined;
        
        // Try to extract city and postal code from address
        const addressParts = extractedData.address.split(',');
        if (addressParts.length > 0) {
          const lastPart = addressParts[addressParts.length - 1].trim();
          const postalMatch = lastPart.match(/(\d{5})\s+(.+)/);
          if (postalMatch) {
            agency.postalCode = postalMatch[1];
            agency.city = postalMatch[2];
          } else {
            agency.city = lastPart;
          }
        }
      }

      if (extractedData.description) {
        agency.description = cleanText(extractedData.description) || undefined;
      }

      logger.info(`Extracted from ${url}: name=${agency.name}, email=${agency.email ? 'yes' : 'no'}, phone=${agency.phone ? 'yes' : 'no'}`);

    } catch (error: any) {
      logger.warn(`Error analyzing website ${url}: ${error.message}`);
    }

    return agency;
  }
}

