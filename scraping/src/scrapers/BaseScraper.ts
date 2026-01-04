import axios, { AxiosInstance } from 'axios';
import { CheerioAPI, load } from 'cheerio';
import { config } from '../config/config';
import { Agency } from '../models/Agency';
import logger from '../utils/logger';
import UserAgent from 'user-agents';
import pRetry from 'p-retry';

export abstract class BaseScraper {
  protected httpClient: AxiosInstance;
  protected userAgent: UserAgent;

  constructor() {
    this.userAgent = new UserAgent();
    this.httpClient = axios.create({
      timeout: config.scraping.timeout,
      headers: {
        'User-Agent': config.scraping.userAgentRotation
          ? this.userAgent.toString()
          : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7',
        'Accept-Encoding': 'gzip, deflate, br',
        'Connection': 'keep-alive',
        'Upgrade-Insecure-Requests': '1',
      },
    });
  }

  protected async fetchPage(url: string): Promise<CheerioAPI | null> {
    try {
      const response = await pRetry(
        async () => {
          if (config.scraping.userAgentRotation) {
            this.httpClient.defaults.headers['User-Agent'] = this.userAgent.toString();
          }
          return await this.httpClient.get(url);
        },
        {
          retries: config.scraping.maxRetries,
          onFailedAttempt: (error) => {
            logger.warn(`Attempt ${error.attemptNumber} failed for ${url}: ${error.message}`);
          },
        }
      );

      return load(response.data);
    } catch (error: any) {
      logger.error(`Error fetching ${url}:`, error.message);
      return null;
    }
  }

  protected async delay(ms: number = config.scraping.delay): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  abstract scrape(searchTerm: string, city?: string): Promise<Agency[]>;
  abstract getName(): string;
}

