import dotenv from 'dotenv';

dotenv.config();

export interface Config {
  database: {
    host: string;
    port: number;
    database: string;
    user: string;
    password: string;
    tablePrefix?: string;
  };
  scraping: {
    delay: number;
    timeout: number;
    maxRetries: number;
    concurrentRequests: number;
    userAgentRotation: boolean;
  };
  proxy: {
    enabled: boolean;
    list: string[];
  };
  logging: {
    level: string;
    file: string;
  };
  search: {
    terms: string[];
    cities: string[];
  };
}

export const config: Config = {
  database: {
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT || '5432', 10),
    database: process.env.DB_NAME || 'realestate_db',
    user: process.env.DB_USER || 'postgres',
    password: process.env.DB_PASSWORD || '',
    tablePrefix: process.env.TABLE_PREFIX || 'scraping_',
  },
  scraping: {
    delay: parseInt(process.env.SCRAPING_DELAY || '2000', 10),
    timeout: parseInt(process.env.SCRAPING_TIMEOUT || '30000', 10),
    maxRetries: parseInt(process.env.MAX_RETRIES || '3', 10),
    concurrentRequests: parseInt(process.env.CONCURRENT_REQUESTS || '5', 10),
    userAgentRotation: process.env.USER_AGENT_ROTATION === 'true',
  },
  proxy: {
    enabled: process.env.USE_PROXY === 'true',
    list: process.env.PROXY_LIST ? process.env.PROXY_LIST.split(',') : [],
  },
  logging: {
    level: process.env.LOG_LEVEL || 'info',
    file: process.env.LOG_FILE || 'scraping.log',
  },
  search: {
    terms: process.env.SEARCH_TERMS
      ? process.env.SEARCH_TERMS.split(',')
      : [
          'agence immobilière',
          'immobilier',
          'agence immo',
          'conseiller immobilier',
          'mandataire immobilier',
          'expert immobilier',
        ],
    cities: process.env.CITIES
      ? process.env.CITIES.split(',')
      : [
          'Paris',
          'Lyon',
          'Marseille',
          'Toulouse',
          'Nice',
          'Nantes',
          'Strasbourg',
          'Montpellier',
          'Bordeaux',
          'Lille',
        ],
  },
};

