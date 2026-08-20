import type { Customer } from '../types/customer'

export const seedCustomers: Customer[] = [
  {
    customerId: 'CUS-1001',
    fullName: 'Amina Khan',
    email: 'amina@example.com',
    phone: '+1-555-0101',
    status: 'ACTIVE',
  },
  {
    customerId: 'CUS-1002',
    fullName: 'Ravi Singh',
    email: 'ravi@example.com',
    phone: '+1-555-0102',
    status: 'PROSPECT',
  },
]