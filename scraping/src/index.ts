import scrapingService from './services/ScrapingService';
import logger from './utils/logger';

async function main() {
  try {
    logger.info('=== Real Estate Agency Scraping System ===');
    await scrapingService.scrapeAll();
    logger.info('Scraping completed successfully');
    process.exit(0);
  } catch (error: any) {
    logger.error('Fatal error:', error);
    process.exit(1);
  }
}

// Handle unhandled promise rejections
process.on('unhandledRejection', (reason, promise) => {
  logger.error('Unhandled Rejection at:', promise, 'reason:', reason);
  process.exit(1);
});

// Handle uncaught exceptions
process.on('uncaughtException', (error) => {
  logger.error('Uncaught Exception:', error);
  process.exit(1);
});

main();

