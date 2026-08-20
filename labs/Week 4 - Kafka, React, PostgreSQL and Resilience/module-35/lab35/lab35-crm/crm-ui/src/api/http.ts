import { ApiError } from './ApiError'

const API_URL = import.meta.env.VITE_CRM_API_URL as string

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  let response: Response
  try {
    response = await fetch(`${API_URL}${path}`, {
      ...init,
      headers: {
        'Content-Type': 'application/json',
        'X-Correlation-Id': 'lab-request-001',
        ...init.headers,
      },
    })
  } catch (err) {
    if (err instanceof DOMException && err.name === 'AbortError') {
      throw err // let native AbortError pass through untouched — see useCustomers below
    }
    throw new ApiError(0, 'NETWORK', 'Cannot reach the CRM service')
  }
  if (!response.ok) throw await ApiError.from(response)
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}