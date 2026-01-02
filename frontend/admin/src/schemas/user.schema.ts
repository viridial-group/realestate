import { z } from 'zod'
import { UserStatus, UserRole } from '@viridial/shared'

export const userCreateSchema = z.object({
  name: z.string().min(1, 'Le nom complet est requis').max(200, 'Le nom ne peut pas dépasser 200 caractères'),
  email: z.string().min(1, 'L\'email est requis').email('L\'email n\'est pas valide'),
  firstName: z.string().max(100, 'Le prénom ne peut pas dépasser 100 caractères').optional(),
  lastName: z.string().max(100, 'Le nom ne peut pas dépasser 100 caractères').optional(),
  phone: z.string().regex(/^[\+]?[(]?[0-9]{1,4}[)]?[-\s\.]?[(]?[0-9]{1,4}[)]?[-\s\.]?[0-9]{1,9}$/, 'Le numéro de téléphone n\'est pas valide').optional().or(z.literal('')),
  password: z.string().min(8, 'Le mot de passe doit contenir au moins 8 caractères').max(100, 'Le mot de passe ne peut pas dépasser 100 caractères'),
  status: z.nativeEnum(UserStatus).optional(),
  roles: z.array(z.nativeEnum(UserRole)).min(1, 'Au moins un rôle doit être sélectionné')
})

export const userUpdateSchema = z.object({
  name: z.string().min(1, 'Le nom complet est requis').max(200, 'Le nom ne peut pas dépasser 200 caractères').optional(),
  email: z.string().min(1, 'L\'email est requis').email('L\'email n\'est pas valide').optional(),
  firstName: z.string().max(100, 'Le prénom ne peut pas dépasser 100 caractères').optional(),
  lastName: z.string().max(100, 'Le nom ne peut pas dépasser 100 caractères').optional(),
  phone: z.string().regex(/^[\+]?[(]?[0-9]{1,4}[)]?[-\s\.]?[(]?[0-9]{1,4}[)]?[-\s\.]?[0-9]{1,9}$/, 'Le numéro de téléphone n\'est pas valide').optional().or(z.literal('')),
  status: z.nativeEnum(UserStatus).optional(),
  roles: z.array(z.nativeEnum(UserRole)).min(1, 'Au moins un rôle doit être sélectionné').optional()
})

export type UserCreateFormData = z.infer<typeof userCreateSchema>
export type UserUpdateFormData = z.infer<typeof userUpdateSchema>

