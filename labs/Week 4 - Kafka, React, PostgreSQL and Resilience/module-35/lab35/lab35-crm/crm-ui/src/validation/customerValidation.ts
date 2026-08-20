import type { CustomerDraft } from '../types/customer'

export type FieldErrors = Partial<Record<keyof CustomerDraft | 'form', string>>

export function validateCustomer(draft: CustomerDraft): FieldErrors {
  const errors: FieldErrors = {}
  if (!draft.fullName.trim()) errors.fullName = 'Full name is required'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(draft.email)) {
    errors.email = 'Enter a valid email'
  }
  return errors
}