import { PuppeteerScraper } from './PuppeteerScraper';
import { PagesJaunesScraper } from './PagesJaunesScraper';
import { AnnuaireImmoScraper } from './AnnuaireImmoScraper';
import { RankingImmoScraper } from './RankingImmoScraper';
import { NestoriaScraper } from './NestoriaScraper';
import { RealEstateMarketingTalkScraper } from './RealEstateMarketingTalkScraper';

export const scrapers: PuppeteerScraper[] = [
  new PagesJaunesScraper(),
  new AnnuaireImmoScraper(),
  new RankingImmoScraper(),
  new NestoriaScraper(),
  new RealEstateMarketingTalkScraper(),
];

export { 
  PuppeteerScraper, 
  PagesJaunesScraper,
  AnnuaireImmoScraper,
  RankingImmoScraper,
  NestoriaScraper,
  RealEstateMarketingTalkScraper
};

