import { describe, expect, it, vi } from 'vitest'
import { customersApi } from './customers'
import { ApiError } from './ApiError'

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}

describe('customersApi', () => {
  it('200: lists customers', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
        jsonResponse([{ customerId: 'CUS-1001', fullName: 'Amina Khan', email: 'amina@example.com', phone: '+1-555-0101', status: 'ACTIVE' }])
    ))
    const result = await customersApi.list()
    expect(result).toHaveLength(1)
    expect(result[0].customerId).toBe('CUS-1001')
  })

  it('201: create returns the server-assigned customer', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
        jsonResponse({ customerId: 'CUS-9001', fullName: 'New', email: 'new@example.com', phone: '', status: 'PROSPECT' }, 201)
    ))
    const result = await customersApi.create({ fullName: 'New', email: 'new@example.com', phone: '', status: 'PROSPECT' })
    expect(result.customerId).toBe('CUS-9001')
  })

  it('400: throws ApiError with fieldErrors', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
        jsonResponse({ message: 'Validation failed', violations: [{ field: 'email', message: 'Enter a valid email' }] }, 400)
    ))
    await expect(customersApi.create({ fullName: 'X', email: 'bad', phone: '', status: 'PROSPECT' }))
        .rejects.toSatisfy((err: unknown) => {
          expect(err).toBeInstanceOf(ApiError)
          expect((err as ApiError).status).toBe(400)
          expect((err as ApiError).fieldErrors).toEqual({ email: 'Enter a valid email' })
          return true
        })
  })

  it('500: throws ApiError with server message', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse({ message: 'Internal error' }, 500)))
    await expect(customersApi.list()).rejects.toSatisfy((err: unknown) => {
      expect(err).toBeInstanceOf(ApiError)
      expect((err as ApiError).status).toBe(500)
      return true
    })
  })

  it('network failure: throws ApiError with status 0', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')))
    await expect(customersApi.list()).rejects.toSatisfy((err: unknown) => {
      expect(err).toBeInstanceOf(ApiError)
      expect((err as ApiError).status).toBe(0)
      return true
    })
  })

  it('abort: rejects with a native AbortError, not ApiError', async () => {
    const controller = new AbortController()
    vi.stubGlobal('fetch', vi.fn().mockImplementation(() => {
      const err = new DOMException('The operation was aborted.', 'AbortError')
      return Promise.reject(err)
    }))
    controller.abort()
    await expect(customersApi.list(controller.signal)).rejects.toMatchObject({ name: 'AbortError' })
  })
})