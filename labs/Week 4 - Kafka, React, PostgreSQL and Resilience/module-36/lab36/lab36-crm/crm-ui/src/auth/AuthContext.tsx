import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { tokenStore } from './tokenStore'
import { login as loginApi } from '../api/auth'

export interface User {
    username: string
}

type AuthState =
    | { status: 'checking' }
    | { status: 'anonymous' }
    | { status: 'authenticated'; user: User }

interface AuthContextValue {
    status: AuthState['status']
    user: User | null
    login: (username: string, password: string) => Promise<void>
    logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [state, setState] = useState<AuthState>({ status: 'checking' })

    useEffect(() => {
        // Token is in-memory only, so it never actually survives a real page
        // reload — this effect exists to represent "checking" explicitly rather
        // than defaulting straight to authenticated, per the guide's warning.
        const existing = tokenStore.get()
        setState(existing ? { status: 'authenticated', user: { username: 'unknown' } } : { status: 'anonymous' })
    }, [])

    const value = useMemo<AuthContextValue>(
        () => ({
            status: state.status,
            user: state.status === 'authenticated' ? state.user : null,
            login: async (username: string, password: string) => {
                const result = await loginApi(username, password)
                tokenStore.set(result.accessToken)
                setState({ status: 'authenticated', user: { username: result.username } })
            },
            logout: () => {
                tokenStore.clear()
                setState({ status: 'anonymous' })
            },
        }),
        [state],
    )

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
    const ctx = useContext(AuthContext)
    if (!ctx) throw new Error('useAuth requires AuthProvider')
    return ctx
}

// Called by http.ts on a 401 — logs the session out from outside a component,
// without a circular import back into a hook.
export function forceLogout() {
    tokenStore.clear()
    window.dispatchEvent(new CustomEvent('auth:expired'))
}