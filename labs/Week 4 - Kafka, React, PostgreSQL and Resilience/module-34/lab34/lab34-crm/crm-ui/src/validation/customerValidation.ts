import type { CustomerDraft } from '../types/customer'

export function validateCustomer(draft: CustomerDraft): Record<string, string> {
  const errors: Record<string, string> = {}
  if (!draft.fullName.trim()) errors.fullName = 'Full name is required'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(draft.email)) {
    errors.email = 'Enter a valid email'
  }
  return errors
}