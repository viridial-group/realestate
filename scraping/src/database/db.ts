import { Pool, PoolClient } from 'pg';
import { config } from '../config/config';
import { Agency, AgencyDatabase } from '../models/Agency';
import logger from '../utils/logger';

class Database {
  private pool: Pool;
  private tablePrefix: string;
  private agenciesTableName: string;

  constructor() {
    this.pool = new Pool({
      host: config.database.host,
      port: config.database.port,
      database: config.database.database,
      user: config.database.user,
      password: config.database.password,
      max: 20,
      idleTimeoutMillis: 30000,
      connectionTimeoutMillis: 2000,
    });

    this.pool.on('error', (err) => {
      logger.error('Unexpected error on idle client', err);
    });

    // Set table prefix and names
    this.tablePrefix = config.database.tablePrefix || 'scraping_';
    this.agenciesTableName = `${this.tablePrefix}agencies`;
  }

  async connect(): Promise<void> {
    try {
      await this.pool.query('SELECT NOW()');
      
      // Create tables if they don't exist
      //await this.createTables();
    } catch (error) {
      logger.error('Database connection error:', error);
      throw error;
    }
  }

  private async createTables(): Promise<void> {
    const client = await this.pool.connect();
    
    try {
      // Create agencies table
      const createTableQuery = `
        CREATE TABLE IF NOT EXISTS ${this.agenciesTableName} (
          id SERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          description TEXT,
          email VARCHAR(255),
          phone VARCHAR(50),
          address TEXT,
          city VARCHAR(100),
          postal_code VARCHAR(20),
          country VARCHAR(100) DEFAULT 'France',
          website TEXT,
          domain VARCHAR(255),
          logo_url TEXT,
          source VARCHAR(100),
          source_url TEXT,
          metadata JSONB,
          active BOOLEAN DEFAULT true,
          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
          CONSTRAINT ${this.agenciesTableName}_name_unique UNIQUE (name)
        );
      `;

      await client.query(createTableQuery);

      // Create indexes
      const createIndexesQueries = [
        `CREATE INDEX IF NOT EXISTS idx_${this.tablePrefix}agencies_email ON ${this.agenciesTableName}(email) WHERE email IS NOT NULL;`,
        `CREATE INDEX IF NOT EXISTS idx_${this.tablePrefix}agencies_city ON ${this.agenciesTableName}(city) WHERE city IS NOT NULL;`,
        `CREATE INDEX IF NOT EXISTS idx_${this.tablePrefix}agencies_country ON ${this.agenciesTableName}(country) WHERE country IS NOT NULL;`,
        `CREATE INDEX IF NOT EXISTS idx_${this.tablePrefix}agencies_source ON ${this.agenciesTableName}(source) WHERE source IS NOT NULL;`,
        `CREATE INDEX IF NOT EXISTS idx_${this.tablePrefix}agencies_active ON ${this.agenciesTableName}(active);`,
        `CREATE INDEX IF NOT EXISTS idx_${this.tablePrefix}agencies_created_at ON ${this.agenciesTableName}(created_at);`,
      ];

      for (const indexQuery of createIndexesQueries) {
        try {
          await client.query(indexQuery);
        } catch (error: any) {
          // Index might already exist, ignore error
          if (!error.message.includes('already exists')) {
            // Ignore index creation errors
          }
        }
      }

      // Table created or verified
    } catch (error) {
      logger.error(`Error creating tables:`, error);
      throw error;
    } finally {
      client.release();
    }
  }

  async close(): Promise<void> {
    await this.pool.end();
    // Database connection closed
  }

  async saveAgency(agency: Agency): Promise<number | null> {
    const client: PoolClient = await this.pool.connect();
    
    try {
      // Check if agency already exists
      const checkQuery = `
        SELECT id FROM ${this.agenciesTableName}
        WHERE name = $1 OR (email = $2 AND email IS NOT NULL AND email != '')
      `;
      const checkResult = await client.query(checkQuery, [agency.name, agency.email || '']);

      if (checkResult.rows.length > 0) {
        // Agency already exists
        const existingId = checkResult.rows[0].id;
        logger.error(`Agency already exists - SKIP INSERT: ${agency.name} (ID: ${existingId})`);
        return existingId;
      }

      // Insert new agency
      const insertQuery = `
        INSERT INTO ${this.agenciesTableName} (
          name, description, email, phone, address, city, postal_code, 
          country, website, domain, logo_url, source, source_url, metadata, active, created_at, updated_at
        ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, NOW(), NOW())
        RETURNING id
      `;

      const insertParams = [
        agency.name,
        agency.description || null,
        agency.email || null,
        agency.phone || null,
        agency.address || null,
        agency.city || null,
        agency.postalCode || null,
        agency.country || 'France',
        agency.website || null,
        agency.domain || null,
        agency.logoUrl || null,
        agency.source || null,
        agency.sourceUrl || null,
        agency.metadata ? JSON.stringify(agency.metadata) : null,
        true,
      ];

      // Log the SQL query with parameters (formatted for readability)
      const formattedQuery = insertQuery
        .replace(/\$(\d+)/g, (match, index) => {
          const paramIndex = parseInt(index) - 1;
          const value = insertParams[paramIndex];
          if (value === null) return 'NULL';
          if (typeof value === 'string') {
            const escaped = value.replace(/'/g, "''");
            const truncated = escaped.length > 100 ? escaped.substring(0, 100) + '...' : escaped;
            return `'${truncated}'`;
          }
          return String(value);
        })
        .replace(/\s+/g, ' ')
        .trim();
      
      logger.error(`[SQL INSERT] ${formattedQuery}`);

      const result = await client.query(insertQuery, insertParams);

      const agencyId = result.rows[0].id;
      // Agency saved successfully
      return agencyId;
    } catch (error) {
      logger.error(`Error saving agency ${agency.name}:`, error);
      return null;
    } finally {
      client.release();
    }
  }

  async saveAgencies(agencies: Agency[]): Promise<number> {
    let savedCount = 0;
    
    for (const agency of agencies) {
      const id = await this.saveAgency(agency);
      if (id !== null) {
        savedCount++;
      }
    }

    return savedCount;
  }

  async getAgencyCount(): Promise<number> {
    const result = await this.pool.query(`SELECT COUNT(*) FROM ${this.agenciesTableName}`);
    return parseInt(result.rows[0].count, 10);
  }

  getTableName(): string {
    return this.agenciesTableName;
  }

  getTablePrefix(): string {
    return this.tablePrefix;
  }
}

export default new Database();

