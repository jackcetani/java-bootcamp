import { ApiError } from './ApiError'

const API_BASE = import.meta.env.VITE_API_BASE_URL as string

export interface LoginResult {
    accessToken: string
    username: string
}

export async function login(username: string, password: string): Promise<LoginResult> {
    const response = await fetch(`${API_BASE}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
    })
    if (!response.ok) {
        throw new ApiError(response.status, 'AUTH_FAILED', 'Invalid username or password')
    }
    return response.json() as Promise<LoginResult>
}