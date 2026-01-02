import { z } from 'zod'

export const organizationSchema = z.object({
  name: z.string().min(1, 'Le nom est requis').max(200, 'Le nom ne peut pas dépasser 200 caractères'),
  description: z.string().max(1000, 'La description ne peut pas dépasser 1000 caractères').optional(),
  domain: z.string().regex(/^([a-z0-9]+(-[a-z0-9]+)*\.)+[a-z]{2,}$/i, 'Le domaine n\'est pas valide').optional().or(z.literal('')),
  active: z.boolean().optional(),
  parentId: z.number().int().positive().optional().nullable()
})

export type OrganizationFormData = z.infer<typeof organizationSchema>

