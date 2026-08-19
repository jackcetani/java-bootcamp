import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import App from './App'

describe('App flows', () => {
  it('renders seed customers', () => {
    render(<App />)
    expect(screen.getByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Ravi Singh' })).toBeInTheDocument()
  })

  it('filters to Amina when searching', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.type(screen.getByRole('searchbox', { name: /search customers/i }), 'amina')
    expect(screen.getByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
    expect(screen.queryByRole('heading', { name: 'Ravi Singh' })).not.toBeInTheDocument()
  })

  it('shows the empty state when search matches nothing', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.type(screen.getByRole('searchbox', { name: /search customers/i }), 'zzz-no-match')
    expect(screen.getByRole('status')).toHaveTextContent('No customers yet')
  })

  it('creates a valid customer exactly once', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('button', { name: 'Add customer' }))
    await user.type(screen.getByLabelText('Full name'), 'New Customer')
    await user.type(screen.getByLabelText('Email'), 'new@example.com')
    await user.click(screen.getByRole('button', { name: 'Save' }))
    expect(screen.getAllByRole('heading', { name: 'New Customer' })).toHaveLength(1)
  })

  it('blocks an invalid create and leaves the list unchanged', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('button', { name: 'Add customer' }))
    await user.click(screen.getByRole('button', { name: 'Save' }))
    expect(await screen.findByText('Full name is required')).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Ravi Singh' })).toBeInTheDocument()
  })

  it('edits Ravi without affecting Amina', async () => {
    const user = userEvent.setup()
    render(<App />)
    const raviHeading = screen.getByRole('heading', { name: 'Ravi Singh' })
    const raviCard = raviHeading.closest('article')!
    await user.click(within(raviCard).getByRole('button', { name: 'Edit' }))
    const nameInput = screen.getByLabelText('Full name')
    await user.clear(nameInput)
    await user.type(nameInput, 'Ravi Updated')
    await user.click(screen.getByRole('button', { name: 'Save' }))
    expect(screen.getByRole('heading', { name: 'Ravi Updated' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
  })

  it('cancel discards the draft and adds no card', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('button', { name: 'Add customer' }))
    await user.type(screen.getByLabelText('Full name'), 'Should Not Save')
    await user.click(screen.getByRole('button', { name: 'Cancel' }))
    expect(screen.queryByText('Should Not Save')).not.toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Ravi Singh' })).toBeInTheDocument()
  })

  it('updates the document title to match the visible count', async () => {
    const user = userEvent.setup()
    render(<App />)
    expect(document.title).toBe('CRM (2)')
    await user.type(screen.getByRole('searchbox', { name: /search customers/i }), 'amina')
    expect(document.title).toBe('CRM (1)')
  })
})