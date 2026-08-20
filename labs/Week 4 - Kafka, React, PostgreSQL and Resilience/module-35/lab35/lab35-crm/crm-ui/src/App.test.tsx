import { render, screen, within, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import App from './App'
import type { Customer } from './types/customer'

const seedResponse: Customer[] = [
  { customerId: 'CUS-1001', fullName: 'Amina Khan', email: 'amina@example.com', phone: '+1-555-0101', status: 'ACTIVE' },
  { customerId: 'CUS-1002', fullName: 'Ravi Singh', email: 'ravi@example.com', phone: '+1-555-0102', status: 'PROSPECT' },
]

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}

describe('App flows', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  it('renders customers loaded from the API', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse(seedResponse)))
    render(<App />)
    expect(await screen.findByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Ravi Singh' })).toBeInTheDocument()
  })

  it('shows an error state when the API is unreachable', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')))
    render(<App />)
    expect(await screen.findByRole('alert')).toBeInTheDocument()
  })

  it('filters to Amina when searching', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse(seedResponse)))
    const user = userEvent.setup()
    render(<App />)
    await screen.findByRole('heading', { name: 'Amina Khan' })
    await user.type(screen.getByRole('searchbox', { name: /search customers/i }), 'amina')
    expect(screen.queryByRole('heading', { name: 'Ravi Singh' })).not.toBeInTheDocument()
  })

  it('creates a customer via POST and appends the server response', async () => {
    const created: Customer = { customerId: 'CUS-9001', fullName: 'New Customer', email: 'new@example.com', phone: '', status: 'PROSPECT' }
    const fetchMock = vi
        .fn()
        .mockResolvedValueOnce(jsonResponse(seedResponse))
        .mockResolvedValueOnce(jsonResponse(created, 201))
    vi.stubGlobal('fetch', fetchMock)
    const user = userEvent.setup()
    render(<App />)
    await screen.findByRole('heading', { name: 'Amina Khan' })
    await user.click(screen.getByRole('button', { name: 'Add customer' }))
    await user.type(screen.getByLabelText('Full name'), 'New Customer')
    await user.type(screen.getByLabelText('Email'), 'new@example.com')
    await user.click(screen.getByRole('button', { name: 'Save' }))
    expect(await screen.findByRole('heading', { name: 'New Customer' })).toBeInTheDocument()
  })

  it('maps a 400 field error onto the Email field', async () => {
    const badRequestBody = { message: 'Validation failed', violations: [{ field: 'email', message: 'Email already in use' }] }
    const fetchMock = vi
        .fn()
        .mockResolvedValueOnce(jsonResponse(seedResponse))
        .mockResolvedValueOnce(jsonResponse(badRequestBody, 400))
    vi.stubGlobal('fetch', fetchMock)
    const user = userEvent.setup()
    render(<App />)
    await screen.findByRole('heading', { name: 'Amina Khan' })
    await user.click(screen.getByRole('button', { name: 'Add customer' }))
    await user.type(screen.getByLabelText('Full name'), 'Dup Email')
    await user.type(screen.getByLabelText('Email'), 'amina@example.com')
    await user.click(screen.getByRole('button', { name: 'Save' }))
    expect(await screen.findByText('Email already in use')).toBeInTheDocument()
  })

  it('shows a 500 as a form-level error, not a crash', async () => {
    const fetchMock = vi
        .fn()
        .mockResolvedValueOnce(jsonResponse(seedResponse))
        .mockResolvedValueOnce(jsonResponse({ message: 'Internal error' }, 500))
    vi.stubGlobal('fetch', fetchMock)
    const user = userEvent.setup()
    render(<App />)
    await screen.findByRole('heading', { name: 'Amina Khan' })
    await user.click(screen.getByRole('button', { name: 'Add customer' }))
    await user.type(screen.getByLabelText('Full name'), 'Server Fail')
    await user.type(screen.getByLabelText('Email'), 'fail@example.com')
    await user.click(screen.getByRole('button', { name: 'Save' }))
    expect(await screen.findByText('Internal error')).toBeInTheDocument()
  })

  it('edits Ravi via PUT without affecting Amina', async () => {
    const updated: Customer = { ...seedResponse[1], fullName: 'Ravi Updated' }
    const fetchMock = vi
        .fn()
        .mockResolvedValueOnce(jsonResponse(seedResponse))
        .mockResolvedValueOnce(jsonResponse(updated))
    vi.stubGlobal('fetch', fetchMock)
    const user = userEvent.setup()
    render(<App />)
    const raviHeading = await screen.findByRole('heading', { name: 'Ravi Singh' })
    const raviCard = raviHeading.closest('article')!
    await user.click(within(raviCard).getByRole('button', { name: 'Edit' }))
    const nameInput = screen.getByLabelText('Full name')
    await user.clear(nameInput)
    await user.type(nameInput, 'Ravi Updated')
    await user.click(screen.getByRole('button', { name: 'Save' }))
    expect(await screen.findByRole('heading', { name: 'Ravi Updated' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
  })

  it('does not send a second POST on a rapid double click', async () => {
    let createCalls = 0
    const created: Customer = { customerId: 'CUS-9002', fullName: 'Once Only', email: 'once@example.com', phone: '', status: 'PROSPECT' }
    const fetchMock = vi.fn().mockImplementation((_url: string, init?: RequestInit) => {
      if (init?.method === 'POST') {
        createCalls += 1
        return Promise.resolve(jsonResponse(created, 201))
      }
      return Promise.resolve(jsonResponse(seedResponse))
    })
    vi.stubGlobal('fetch', fetchMock)
    const user = userEvent.setup()
    render(<App />)
    await screen.findByRole('heading', { name: 'Amina Khan' })
    await user.click(screen.getByRole('button', { name: 'Add customer' }))
    await user.type(screen.getByLabelText('Full name'), 'Once Only')
    await user.type(screen.getByLabelText('Email'), 'once@example.com')
    const saveButton = screen.getByRole('button', { name: 'Save' })
    await user.click(saveButton)
    await user.click(saveButton) // second click after mode already closed / button disabled
    await waitFor(() => expect(createCalls).toBe(1))
  })
})