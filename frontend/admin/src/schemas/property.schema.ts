import { z } from 'zod'
import { PropertyType, PropertyStatus } from '@viridial/shared'

export const propertySchema = z.object({
  title: z.string().min(1, 'Le titre est requis').max(200, 'Le titre ne peut pas dépasser 200 caractères'),
  description: z.string().min(1, 'La description est requise').max(5000, 'La description ne peut pas dépasser 5000 caractères'),
  price: z.number().min(0, 'Le prix doit être supérieur ou égal à 0').positive('Le prix doit être supérieur à 0'),
  address: z.string().min(1, 'L\'adresse est requise').max(500, 'L\'adresse ne peut pas dépasser 500 caractères'),
  city: z.string().min(1, 'La ville est requise').max(100, 'La ville ne peut pas dépasser 100 caractères'),
  country: z.string().min(1, 'Le pays est requis').max(100, 'Le pays ne peut pas dépasser 100 caractères'),
  propertyType: z.nativeEnum(PropertyType, {
    errorMap: () => ({ message: 'Le type de propriété est requis' })
  }),
  bedrooms: z.number().int().min(0).optional().nullable(),
  bathrooms: z.number().min(0).optional().nullable(),
  area: z.number().min(0).optional().nullable(),
  status: z.nativeEnum(PropertyStatus).optional(),
  fullBathrooms: z.number().int().min(0).optional().nullable(),
  appliancesIncluded: z.string().optional().nullable(),
  laundryLocation: z.string().optional().nullable(),
  totalStructureArea: z.number().min(0).optional().nullable(),
  totalInteriorLivableArea: z.number().min(0).optional().nullable(),
  virtualTourUrl: z.string().url('L\'URL de visite virtuelle doit être valide').optional().nullable().or(z.literal('')),
  parkingFeatures: z.string().optional().nullable(),
  hasGarage: z.boolean().optional(),
  accessibilityFeatures: z.string().optional().nullable(),
  patioPorch: z.string().optional().nullable(),
  exteriorFeatures: z.string().optional().nullable(),
  specialConditions: z.string().optional().nullable(),
  homeType: z.string().optional().nullable(),
  propertySubtype: z.string().optional().nullable(),
  condition: z.string().optional().nullable(),
  yearBuilt: z.number().int().min(1000).max(new Date().getFullYear() + 1).optional().nullable(),
  subdivision: z.string().optional().nullable(),
  hasHOA: z.boolean().optional(),
  hoaAmenities: z.string().optional().nullable(),
  hoaServices: z.string().optional().nullable(),
  hoaFee: z.number().min(0).optional().nullable(),
  hoaFeeFrequency: z.string().optional().nullable(),
  region: z.string().optional().nullable(),
  pricePerSquareFoot: z.number().min(0).optional().nullable(),
  dateOnMarket: z.string().optional().nullable(),
  images: z.array(z.string()).optional()
})

export type PropertyFormData = z.infer<typeof propertySchema>

