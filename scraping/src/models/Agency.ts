export interface Agency {
  name: string;
  description?: string;
  email?: string;
  phone?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  country?: string;
  website?: string;
  domain?: string;
  logoUrl?: string;
  source?: string;
  sourceUrl?: string;
  metadata?: Record<string, any>;
}

export interface AgencyDatabase {
  id?: number;
  name: string;
  description?: string;
  email?: string;
  phone?: string;
  address?: string;
  city?: string;
  postal_code?: string;
  country?: string;
  domain?: string;
  logo_url?: string;
  active?: boolean;
  created_at?: Date;
  updated_at?: Date;
}

