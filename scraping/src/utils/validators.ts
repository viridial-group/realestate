import validator from 'validator';
import phone from 'phone';

export function validateEmail(email: string | undefined | null): string | null {
  if (!email) return null;
  const cleaned = email.trim().toLowerCase();
  return validator.isEmail(cleaned) ? cleaned : null;
}

export function validatePhone(phoneNumber: string | undefined | null, country: string = 'FR'): string | null {
  if (!phoneNumber) return null;
  
  try {
    const cleaned = phoneNumber.replace(/\s+/g, '').trim();
    const result = phone(cleaned, { country });
    return result.isValid ? result.phoneNumber : null;
  } catch (error) {
    return null;
  }
}

export function validateUrl(url: string | undefined | null): string | null {
  if (!url) return null;
  
  try {
    let cleaned = url.trim();
    if (!cleaned.startsWith('http://') && !cleaned.startsWith('https://')) {
      cleaned = `https://${cleaned}`;
    }
    return validator.isURL(cleaned) ? cleaned : null;
  } catch (error) {
    return null;
  }
}

export function extractDomain(url: string | undefined | null): string | null {
  if (!url) return null;
  
  try {
    const validatedUrl = validateUrl(url);
    if (!validatedUrl) return null;
    
    const urlObj = new URL(validatedUrl);
    return urlObj.hostname.replace('www.', '');
  } catch (error) {
    return null;
  }
}

export function cleanText(text: string | undefined | null): string | null {
  if (!text) return null;
  return text.trim().replace(/\s+/g, ' ').substring(0, 500) || null;
}

