import { render, screen } from '@testing-library/react'
import { CustomerCard } from '../components/CustomerCard'

const amina = { customerId: 'CUS-1001', fullName: 'Amina Khan', email: 'amina@example.com', phone: '+1-555-0101', status: 'ACTIVE' as const }

describe('XSS posture', () => {
  it('renders a malicious fullName as text, not HTML', () => {
    render(<CustomerCard customer={{ ...amina, fullName: '<img onerror=alert(1)>' }} onEdit={() => {}} />)
    expect(document.querySelector('img')).toBeNull()
    expect(screen.getByText('<img onerror=alert(1)>')).toBeInTheDocument()
  })
})