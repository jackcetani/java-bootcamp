import { useEffect, useState } from 'react'
import { AppLayout } from './components/AppLayout'
import { CustomerToolbar } from './components/CustomerToolbar'
import { CustomerList } from './components/CustomerList'
import { CustomerForm } from './components/CustomerForm'
import { LoadingState } from './components/LoadingState'
import { ErrorState } from './components/ErrorState'
import { customersApi } from './api/customers'
import { ApiError } from './api/ApiError'
import type { Customer, CustomerDraft, Mode } from './types/customer'
import { validateCustomer, type FieldErrors } from './validation/customerValidation'

const emptyDraft: CustomerDraft = {
    fullName: '',
    email: '',
    phone: '',
    status: 'PROSPECT',
}

type LoadState =
    | { kind: 'loading' }
    | { kind: 'data'; data: Customer[] }
    | { kind: 'error'; error: ApiError }

export default function App() {
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
            .then((data) => {
                if (!cancelled) setState({ kind: 'data', data })
            })
            .catch((err) => {
                if (err.name === 'AbortError' || controller.signal.aborted) return
                if (!cancelled) setState({ kind: 'error', error: err })
            })
        return () => {
            cancelled = true
            controller.abort()
        }
    }, [reloadToken])

    const customers = state.kind === 'data' ? state.data : []

    const visible = customers.filter((c) =>
        [c.customerId, c.fullName, c.email].some((v) =>
            v.toLowerCase().includes(query.trim().toLowerCase())
        )
    )

    useEffect(() => {
        const original = document.title
        document.title = `CRM (${visible.length})`
        return () => {
            document.title = original
        }
    }, [visible.length])

    function openCreate() {
        setDraft(emptyDraft)
        setErrors({})
        setMode({ kind: 'create' })
    }

    function openEdit(id: string) {
        const row = customers.find((c) => c.customerId === id)
        if (!row) return
        const { customerId: _id, ...draftFields } = row
        setDraft(draftFields)
        setErrors({})
        setMode({ kind: 'edit', id })
    }

    function handleDraftChange(next: CustomerDraft) {
        const changedKey = (Object.keys(next) as (keyof CustomerDraft)[]).find(
            (key) => next[key] !== draft[key]
        )
        setDraft(next)
        if (changedKey) {
            setErrors((prev) => {
                const copy = { ...prev }
                delete copy[changedKey]
                return copy
            })
        }
    }

    async function handleSubmit() {
        const fieldErrors = validateCustomer(draft)
        if (Object.keys(fieldErrors).length) {
            setErrors(fieldErrors)
            return
        }
        setSaving(true)
        try {
            if (mode.kind === 'create') {
                const created = await customersApi.create(draft)
                setState((prev) =>
                    prev.kind === 'data' ? { kind: 'data', data: [...prev.data, created] } : prev
                )
                console.log('create', 'lab-request-001')
            } else if (mode.kind === 'edit') {
                const editId = mode.id
                const updated = await customersApi.update(editId, draft)
                setState((prev) =>
                    prev.kind === 'data'
                        ? { kind: 'data', data: prev.data.map((c) => (c.customerId === editId ? updated : c)) }
                        : prev
                )
                console.log('update', editId, 'lab-request-001')
            }
            setMode({ kind: 'closed' })
            setDraft(emptyDraft)
            setErrors({})
        } catch (err) {
            if (err instanceof ApiError && err.status === 400) {
                setErrors(err.fieldErrors ?? { form: err.message })
            } else if (err instanceof ApiError) {
                setErrors({ form: err.message })
            } else {
                setErrors({ form: 'Save failed' })
            }
        } finally {
            setSaving(false)
        }
    }

    function handleCancel() {
        setMode({ kind: 'closed' })
        setDraft(emptyDraft)
        setErrors({})
        console.log('cancel', 'lab-request-001')
    }

    if (state.kind === 'loading') {
        return (
            <AppLayout>
                <LoadingState />
            </AppLayout>
        )
    }

    if (state.kind === 'error') {
        return (
            <AppLayout>
                <ErrorState message={state.error.message} onRetry={() => setReloadToken((t) => t + 1)} />
            </AppLayout>
        )
    }

    return (
        <AppLayout>
            <CustomerToolbar query={query} onQueryChange={setQuery} onAdd={openCreate} />
            <CustomerList customers={visible} onEdit={openEdit} />
            {mode.kind !== 'closed' && (
                <CustomerForm
                    value={draft}
                    errors={errors}
                    saving={saving}
                    onChange={handleDraftChange}
                    onSubmit={handleSubmit}
                    onCancel={handleCancel}
                />
            )}
        </AppLayout>
    )
}