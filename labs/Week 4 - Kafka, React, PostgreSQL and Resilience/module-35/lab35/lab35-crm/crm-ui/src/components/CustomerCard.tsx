import type { Customer } from '../types/customer'
import { StatusBadge } from './StatusBadge'

export interface CustomerCardProps {
  customer: Customer
  onEdit: (customerId: string) => void
}

export function CustomerCard({ customer, onEdit }: CustomerCardProps) {
  const headingId = `customer-${customer.customerId}-heading`
  return (
      <article className="card" aria-labelledby={headingId}>
        <h3 id={headingId}>{customer.fullName}</h3>
        <StatusBadge status={customer.status} />
        <p>
          <a href={`mailto:${customer.email}`}>{customer.email}</a>
        </p>
        <button type="button" onClick={() => onEdit(customer.customerId)}>
          Edit
        </button>
      </article>
  )
}