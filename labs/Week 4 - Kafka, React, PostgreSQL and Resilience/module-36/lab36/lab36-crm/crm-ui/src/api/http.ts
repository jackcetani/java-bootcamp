import { ApiError } from './ApiError'
import { tokenStore } from '../auth/tokenStore'
import { forceLogout } from '../auth/AuthContext'

const API_BASE = import.meta.env.VITE_API_BASE_URL as string
const apiOrigin = new URL(API_BASE).origin

export class ForbiddenError extends Error {
  constructor() {
    super('You do not have permission to perform this action.')
    this.name = 'ForbiddenError'
  }
}

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const url = new URL(path.startsWith('http') ? path : `${API_BASE}${path}`)
  const headers = new Headers(init.headers)
  headers.set('Content-Type', 'application/json')
  headers.set('X-Correlation-Id', 'lab-request-001')

  const token = tokenStore.get()
  // Bearer only ever goes to the CRM API's own origin — never a third party,
  // even if a caller passes an absolute URL pointing somewhere else.
  if (token && url.origin === apiOrigin) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  let response: Response
  try {
    response = await fetch(url.toString(), { ...init, headers })
  } catch (err) {
    if (err instanceof DOMException && err.name === 'AbortError') throw err
    throw new ApiError(0, 'NETWORK', 'Cannot reach the CRM service')
  }

  if (response.status === 401) {
    forceLogout()
    throw new ApiError(401, 'UNAUTHORIZED', 'Session expired — please sign in again')
  }
  if (response.status === 403) {
    throw new ForbiddenError()
  }
  if (!response.ok) throw await ApiError.from(response)
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}