import type { Customer, CustomerDraft } from '../types/customer'
import { request } from './http'

export const customersApi = {
  list: (signal?: AbortSignal) => request<Customer[]>('/customers', { signal }),
  create: (draft: CustomerDraft) =>
      request<Customer>('/customers', {
        method: 'POST',
        body: JSON.stringify(draft),
      }),
  update: (id: string, draft: CustomerDraft) =>
      request<Customer>(`/customers/${encodeURIComponent(id)}`, {
        method: 'PUT',
        body: JSON.stringify(draft),
      }),
}