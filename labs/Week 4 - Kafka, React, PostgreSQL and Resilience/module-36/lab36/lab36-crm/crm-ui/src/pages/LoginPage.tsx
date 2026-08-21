import { useState, type FormEvent } from 'react'
import { useLocation, useNavigate, Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

function safeReturnPath(candidate: unknown): string {
    // Only allow internal paths — reject absolute/external URLs (open redirect).
    if (typeof candidate === 'string' && candidate.startsWith('/') && !candidate.startsWith('//')) {
        return candidate
    }
    return '/'
}

export function LoginPage() {
    const { login, status } = useAuth()
    const location = useLocation()
    const navigate = useNavigate()
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [submitting, setSubmitting] = useState(false)

    if (status === 'authenticated') {
        return <Navigate to={safeReturnPath((location.state as { from?: string } | null)?.from)} replace />
    }

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (submitting) return
        setSubmitting(true)
        setError('')
        try {
            await login(username, password)
            navigate(safeReturnPath((location.state as { from?: string } | null)?.from), { replace: true })
        } catch {
            // Generic message — never reveal whether the username exists.
            setError('Invalid username or password')
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h1>Sign in</h1>
            <div>
                <label htmlFor="username">Username</label>
                <input
                    id="username"
                    name="username"
                    autoComplete="username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <div>
                <label htmlFor="password">Password</label>
                <input
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="current-password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            {error && <p role="alert">{error}</p>}
            <button type="submit" disabled={submitting}>
                Sign in
            </button>
        </form>
    )
}