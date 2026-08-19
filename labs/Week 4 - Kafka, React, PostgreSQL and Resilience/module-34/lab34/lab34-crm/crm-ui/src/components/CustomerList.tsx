import type { Customer } from '../types/customer'
import { CustomerCard } from './CustomerCard'
import { EmptyState } from './EmptyState'

export interface CustomerListProps {
  customers: Customer[]
  onEdit: (customerId: string) => void
}

export function CustomerList({ customers, onEdit }: CustomerListProps) {
  if (customers.length === 0) {
    return <EmptyState title="No customers yet" />
  }
  return (
      <section aria-labelledby="customer-list-title">
        <h2 id="customer-list-title">Customers</h2>
        <div className="customer-grid">
          {customers.map((customer) => (
              <CustomerCard
                  key={customer.customerId}
                  customer={customer}
                  onEdit={onEdit}
              />
          ))}
        </div>
      </section>
  )
}