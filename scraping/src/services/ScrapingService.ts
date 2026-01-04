import { scrapers } from '../scrapers';
import { config } from '../config/config';
import { Agency } from '../models/Agency';
import database from '../database/db';
import logger from '../utils/logger';
import pLimit from 'p-limit';

export class ScrapingService {
  private limit = pLimit(config.scraping.concurrentRequests);

  async scrapeAll(): Promise<void> {
    try {
      await database.connect();
      let totalSaved = 0;

      // Scrape for each search term and city combination
      for (const term of config.search.terms) {
        for (const city of config.search.cities) {
          // Run scrapers sequentially to avoid overwhelming sites
          for (const scraper of scrapers) {
            try {
              const agencies = await scraper.scrape(term, city);
              
              // Save each agency immediately to database
              for (const agency of agencies) {
                try {
                  const savedId = await database.saveAgency(agency);
                  if (savedId !== null) {
                    totalSaved++;
                    logger.info(`✓ Saved agency: ${agency.name} (ID: ${savedId})`);
                  }
                } catch (error: any) {
                  logger.error(`✗ Failed to save agency: ${agency.name} - ${error.message}`);
                }
              }
              
              // Delay between scrapers
              await new Promise(resolve => setTimeout(resolve, config.scraping.delay * 2));
            } catch (error: any) {
              logger.error(`Error in scraper ${scraper.getName()}:`, error.message);
            }
          }
        }
      }

      // Close all browser instances
      for (const scraper of scrapers) {
        try {
          await (scraper as any).closeBrowser?.();
        } catch (e) {
          // Ignore
        }
      }

      await database.getAgencyCount();
    } catch (error: any) {
      logger.error('Error in scraping process:', error);
      throw error;
    } finally {
      // Ensure all browsers are closed
      for (const scraper of scrapers) {
        try {
          await (scraper as any).closeBrowser?.();
        } catch (e) {
          // Ignore
        }
      }
      await database.close();
    }
  }

}

export default new ScrapingService();

