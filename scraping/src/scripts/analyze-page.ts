#!/usr/bin/env ts-node

/**
 * Script to analyze a webpage structure and suggest optimal selectors
 * Usage: npm run analyze-page -- <url>
 */

import puppeteer from 'puppeteer';
import { PageAnalyzer } from '../utils/PageAnalyzer';
import logger from '../utils/logger';

async function analyzePage(url: string) {
  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox'],
  });

  try {
    const page = await browser.newPage();
    await page.setViewport({ width: 1920, height: 1080 });
    await page.setUserAgent(
      'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
    );

    logger.info(`Navigating to: ${url}`);
    await page.goto(url, {
      waitUntil: 'networkidle2',
      timeout: 30000,
    });

    await new Promise(resolve => setTimeout(resolve, 3000));

    const analyzer = new PageAnalyzer();
    const structure = await analyzer.analyzePage(page, url);
    const selectors = analyzer.generateSelectors(structure);

    console.log('\n=== PAGE ANALYSIS RESULTS ===\n');
    console.log(`Title: ${structure.title}`);
    console.log(`URL: ${structure.url}`);
    console.log(`\nContainer Count: ${structure.sampleData.containerCount}`);
    console.log(`\n=== SUGGESTED SELECTORS ===\n`);
    console.log(`Container: ${selectors.container}`);
    console.log(`Name: ${selectors.name}`);
    console.log(`Email: ${selectors.email}`);
    console.log(`Phone: ${selectors.phone}`);
    console.log(`Address: ${selectors.address}`);
    console.log(`Website: ${selectors.website}`);

    console.log(`\n=== SAMPLE DATA ===\n`);
    console.log(`Sample Names (${structure.sampleData.sampleNames.length}):`);
    structure.sampleData.sampleNames.forEach((name, i) => {
      console.log(`  ${i + 1}. ${name}`);
    });

    console.log(`\nSample Emails (${structure.sampleData.sampleEmails.length}):`);
    structure.sampleData.sampleEmails.forEach((email, i) => {
      console.log(`  ${i + 1}. ${email}`);
    });

    console.log(`\nSample Phones (${structure.sampleData.samplePhones.length}):`);
    structure.sampleData.samplePhones.forEach((phone, i) => {
      console.log(`  ${i + 1}. ${phone}`);
    });

    console.log(`\n=== ALL FOUND SELECTORS ===\n`);
    console.log('Containers:', structure.selectors.containers.join(', ') || 'None found');
    console.log('Names:', structure.selectors.names.join(', ') || 'None found');
    console.log('Emails:', structure.selectors.emails.join(', ') || 'None found');
    console.log('Phones:', structure.selectors.phones.join(', ') || 'None found');
    console.log('Addresses:', structure.selectors.addresses.join(', ') || 'None found');
    console.log('Websites:', structure.selectors.websites.join(', ') || 'None found');

    // Save screenshot
    const screenshotPath = `debug-screenshots/analysis-${Date.now()}.png`;
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`\nScreenshot saved: ${screenshotPath}`);

    await browser.close();
  } catch (error: any) {
    logger.error(`Error analyzing page: ${error.message}`);
    await browser.close();
    process.exit(1);
  }
}

// Get URL from command line arguments
const url = process.argv[2];

if (!url) {
  console.error('Usage: npm run analyze-page -- <url>');
  console.error('Example: npm run analyze-page -- https://www.annuaireimmo.fr/recherche?q=agence+immobiliere');
  process.exit(1);
}

analyzePage(url).catch((error) => {
  logger.error('Fatal error:', error);
  process.exit(1);
});

