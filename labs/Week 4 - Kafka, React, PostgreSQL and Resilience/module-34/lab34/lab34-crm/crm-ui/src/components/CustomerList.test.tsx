import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { CustomerList } from './CustomerList'
import { seedCustomers } from '../data/seedCustomers'

describe('CustomerList', () => {
  it('renders a card for each fixture customer', () => {
    render(<CustomerList customers={seedCustomers} onEdit={() => {}} />)
    expect(screen.getByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Ravi Singh' })).toBeInTheDocument()
  })

  it('shows the empty state when there are no customers', () => {
    render(<CustomerList customers={[]} onEdit={() => {}} />)
    expect(screen.getByRole('status')).toHaveTextContent('No customers yet')
  })

  it('reports the selected customer', async () => {
    const user = userEvent.setup()
    const onEdit = vi.fn()
    render(<CustomerList customers={seedCustomers} onEdit={onEdit} />)
    const aminaHeading = screen.getByRole('heading', { name: 'Amina Khan' })
    const aminaCard = aminaHeading.closest('article')!
    await user.click(within(aminaCard).getByRole('button', { name: 'Edit' }))
    expect(onEdit).toHaveBeenCalledWith('CUS-1001')
  })
})