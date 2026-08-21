//import { render, screen, waitFor } from '@testing-library/react'
//import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import { tokenStore } from '../auth/tokenStore'
import { request } from '../api/http'
import { ApiError } from '../api/ApiError'

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } })
}

describe('token storage', () => {
  it('keeps token out of Web Storage', () => {
    tokenStore.set('secret-token')
    expect(localStorage.getItem('token')).toBeNull()
    expect(sessionStorage.getItem('token')).toBeNull()
    tokenStore.clear()
  })
})

describe('bearer origin scoping', () => {
  it('attaches Authorization to the CRM API origin only', async () => {
    tokenStore.set('secret-token')
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([]))
    vi.stubGlobal('fetch', fetchMock)
    await request('/api/customers')
    const [, init] = fetchMock.mock.calls[0]
    expect((init.headers as Headers).get('Authorization')).toBe('Bearer secret-token')
    tokenStore.clear()
    vi.unstubAllGlobals()
  })
})

describe('401 vs 403', () => {
  it('401 clears the session', async () => {
    tokenStore.set('secret-token')
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse({ message: 'expired' }, 401)))
    await expect(request('/api/customers')).rejects.toBeInstanceOf(ApiError)
    expect(tokenStore.get()).toBeNull()
    vi.unstubAllGlobals()
  })

  it('403 does not clear the session', async () => {
    tokenStore.set('secret-token')
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse({ message: 'forbidden' }, 403)))
    await expect(request('/api/customers')).rejects.toThrow('You do not have permission')
    expect(tokenStore.get()).toBe('secret-token')
    tokenStore.clear()
    vi.unstubAllGlobals()
  })
})