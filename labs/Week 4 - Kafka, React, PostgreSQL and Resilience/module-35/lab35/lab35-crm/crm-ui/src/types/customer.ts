export type CustomerStatus = 'PROSPECT' | 'ACTIVE' | 'SUSPENDED' | 'CLOSED'

export interface Customer {
  customerId: string
  fullName: string
  email: string
  phone: string
  status: CustomerStatus
}

export type CustomerDraft = Omit<Customer, 'customerId'>

export type Mode =
    | { kind: 'closed' }
    | { kind: 'create' }
    | { kind: 'edit'; id: string }