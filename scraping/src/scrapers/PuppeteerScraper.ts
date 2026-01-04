import puppeteer, { Browser, Page } from 'puppeteer';
import { config } from '../config/config';
import { Agency } from '../models/Agency';
import logger from '../utils/logger';
import {
  validateEmail,
  validatePhone,
  validateUrl,
  extractDomain,
  cleanText,
} from '../utils/validators';

export abstract class PuppeteerScraper {
  protected browser: Browser | null = null;

  protected async initBrowser(): Promise<Browser> {
    if (!this.browser) {
      this.browser = await puppeteer.launch({
        headless: 'new', // Use new headless mode to avoid deprecation warning
        args: [
          '--no-sandbox',
          '--disable-setuid-sandbox',
          '--disable-dev-shm-usage',
          '--disable-accelerated-2d-canvas',
          '--disable-gpu',
          '--window-size=1920x1080',
        ],
      });
    }
    return this.browser;
  }

  protected async closeBrowser(): Promise<void> {
    if (this.browser) {
      await this.browser.close();
      this.browser = null;
    }
  }

  protected async createPage(): Promise<Page> {
    const browser = await this.initBrowser();
    const page = await browser.newPage();
    
    // Set viewport
    await page.setViewport({ width: 1920, height: 1080 });
    
    // Set user agent
    await page.setUserAgent(
      'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
    );

    // Set extra headers
    await page.setExtraHTTPHeaders({
      'Accept-Language': 'fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7',
      'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
    });

    // Enable console logging from page
    page.on('console', (msg) => {
      const type = msg.type();
      const text = msg.text();
      if (type === 'error' || type === 'warning') {
        logger.debug(`Page console [${type}]: ${text}`);
      }
    });

    return page;
  }

  protected async debugPage(page: Page, scraperName: string, url: string): Promise<void> {
    try {
      const title = await page.title();
      const urlAfter = page.url();
      logger.debug(`${scraperName} - Page title: ${title}`);
      logger.debug(`${scraperName} - Final URL: ${urlAfter}`);
      
      // Check for common blocking indicators
      const blockingIndicators = await page.evaluate(() => {
        const bodyText = document.body?.textContent?.toLowerCase() || '';
        return {
          hasCaptcha: bodyText.includes('captcha') || bodyText.includes('robot'),
          hasBlocked: bodyText.includes('blocked') || bodyText.includes('access denied'),
          hasSorry: bodyText.includes('sorry') || bodyText.includes('désolé'),
          bodyLength: bodyText.length,
        };
      });

      if (blockingIndicators.hasCaptcha || blockingIndicators.hasBlocked || blockingIndicators.hasSorry) {
        logger.warn(`${scraperName} - Page might be blocked. Indicators: ${JSON.stringify(blockingIndicators)}`);
      }

      // Save screenshot in debug mode
      if (process.env.DEBUG_SCREENSHOTS === 'true') {
        const fs = require('fs');
        const path = require('path');
        const screenshotsDir = path.join(process.cwd(), 'debug-screenshots');
        if (!fs.existsSync(screenshotsDir)) {
          fs.mkdirSync(screenshotsDir, { recursive: true });
        }
        const filename = `${scraperName}-${Date.now()}.png`;
        await page.screenshot({ path: path.join(screenshotsDir, filename), fullPage: true });
        logger.debug(`${scraperName} - Screenshot saved: ${filename}`);
      }
    } catch (error: any) {
      logger.warn(`Error in debugPage: ${error.message}`);
    }
  }

  protected async delay(ms: number = config.scraping.delay): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  abstract scrape(searchTerm: string, city?: string): Promise<Agency[]>;
  abstract getName(): string;
}

