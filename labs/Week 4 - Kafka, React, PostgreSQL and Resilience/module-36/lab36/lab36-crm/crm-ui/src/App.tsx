import { useEffect, useState } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider, useAuth } from './auth/AuthContext'
import { ProtectedRoute } from './auth/ProtectedRoute'
import { LoginPage } from './pages/LoginPage'
import { AppLayout } from './components/AppLayout'
import { CustomerToolbar } from './components/CustomerToolbar'
import { CustomerList } from './components/CustomerList'
import { CustomerForm } from './components/CustomerForm'
import { LoadingState } from './components/LoadingState'
import { ErrorState } from './components/ErrorState'
import { customersApi } from './api/customers'
import { ApiError } from './api/ApiError'
import { ForbiddenError } from './api/http'
import type { Customer, CustomerDraft, Mode } from './types/customer'
import { validateCustomer, type FieldErrors } from './validation/customerValidation'

const emptyDraft: CustomerDraft = { fullName: '', email: '', phone: '', status: 'PROSPECT' }

type LoadState =
    | { kind: 'loading' }
    | { kind: 'data'; data: Customer[] }
    | { kind: 'error'; error: Error }

function Dashboard() {
    const { logout } = useAuth()
    const [state, setState] = useState<LoadState>({ kind: 'loading' })
    const [reloadToken, setReloadToken] = useState(0)
    const [query, setQuery] = useState('')
    const [mode, setMode] = useState<Mode>({ kind: 'closed' })
    const [draft, setDraft] = useState<CustomerDraft>(emptyDraft)
    const [errors, setErrors] = useState<FieldErrors>({})
    const [saving, setSaving] = useState(false)

    useEffect(() => {
        const controller = new AbortController()
        let cancelled = false
        setState({ kind: 'loading' })
        customersApi
            .list(controller.signal)
            .then((data) => { if (!cancelled) setState({ kind: 'data', data }) })
            .catch((err) => {
                if (err instanceof ForbiddenError) { if (!cancelled) setState({ kind: 'error', error: err }); return }
                if (err.name === 'AbortError' || controller.signal.aborted) return
                if (!cancelled) setState({ kind: 'error', error: err })
            })
        return () => { cancelled = true; controller.abort() }
    }, [reloadToken])

    const customers = state.kind === 'data' ? state.data : []
    const visible = customers.filter((c) =>
        [c.customerId, c.fullName, c.email].some((v) => v.toLowerCase().includes(query.trim().toLowerCase()))
    )

    useEffect(() => {
        const original = document.title
        document.title = `CRM (${visible.length})`
        return () => { document.title = original }
    }, [visible.length])

    function openCreate() { setDraft(emptyDraft); setErrors({}); setMode({ kind: 'create' }) }
    function openEdit(id: string) {
        const row = customers.find((c) => c.customerId === id)
        if (!row) return
        const { customerId: _id, ...draftFields } = row
        setDraft(draftFields); setErrors({}); setMode({ kind: 'edit', id })
    }
    function handleDraftChange(next: CustomerDraft) {
        const changedKey = (Object.keys(next) as (keyof CustomerDraft)[]).find((k) => next[k] !== draft[k])
        setDraft(next)
        if (changedKey) setErrors((prev) => { const c = { ...prev }; delete c[changedKey]; return c })
    }
    async function handleSubmit() {
        const fieldErrors = validateCustomer(draft)
        if (Object.keys(fieldErrors).length) { setErrors(fieldErrors); return }
        setSaving(true)
        try {
            if (mode.kind === 'create') {
                const created = await customersApi.create(draft)
                setState((p) => (p.kind === 'data' ? { kind: 'data', data: [...p.data, created] } : p))
            } else if (mode.kind === 'edit') {
                const editId = mode.id
                const updated = await customersApi.update(editId, draft)
                setState((p) => (p.kind === 'data' ? { kind: 'data', data: p.data.map((c) => (c.customerId === editId ? updated : c)) } : p))
            }
            setMode({ kind: 'closed' }); setDraft(emptyDraft); setErrors({})
        } catch (err) {
            if (err instanceof ApiError && err.status === 400) setErrors(err.fieldErrors ?? { form: err.message })
            else if (err instanceof ForbiddenError) setErrors({ form: err.message })
            else if (err instanceof ApiError) setErrors({ form: err.message })
            else setErrors({ form: 'Save failed' })
        } finally {
            setSaving(false)
        }
    }
    function handleCancel() { setMode({ kind: 'closed' }); setDraft(emptyDraft); setErrors({}) }

    if (state.kind === 'loading') return <AppLayout><LoadingState /></AppLayout>
    if (state.kind === 'error') {
        return (
            <AppLayout>
                <ErrorState message={state.error.message} onRetry={() => setReloadToken((t) => t + 1)} />
            </AppLayout>
        )
    }

    return (
        <AppLayout>
            <button type="button" onClick={logout}>Sign out</button>
            <CustomerToolbar query={query} onQueryChange={setQuery} onAdd={openCreate} />
            <CustomerList customers={visible} onEdit={openEdit} />
            {mode.kind !== 'closed' && (
                <CustomerForm
                    value={draft} errors={errors} saving={saving}
                    onChange={handleDraftChange} onSubmit={handleSubmit} onCancel={handleCancel}
                />
            )}
        </AppLayout>
    )
}

export default function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route element={<ProtectedRoute />}>
                        <Route path="/" element={<Dashboard />} />
                    </Route>
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    )
}