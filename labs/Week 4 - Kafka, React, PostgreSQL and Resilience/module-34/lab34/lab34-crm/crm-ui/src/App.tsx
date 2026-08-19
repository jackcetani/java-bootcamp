import { useEffect, useState } from 'react'
import { AppLayout } from './components/AppLayout'
import { CustomerToolbar } from './components/CustomerToolbar'
import { CustomerList } from './components/CustomerList'
import { CustomerForm } from './components/CustomerForm'
import { seedCustomers } from './data/seedCustomers'
import type { Customer, CustomerDraft, Mode } from './types/customer'
import { validateCustomer } from './validation/customerValidation'

const emptyDraft: CustomerDraft = {
  fullName: '',
  email: '',
  phone: '',
  status: 'PROSPECT',
}

export default function App() {
  const [customers, setCustomers] = useState<Customer[]>(seedCustomers)
  const [query, setQuery] = useState('')
  const [mode, setMode] = useState<Mode>({ kind: 'closed' })
  const [draft, setDraft] = useState<CustomerDraft>(emptyDraft)
  const [errors, setErrors] = useState<Partial<Record<keyof CustomerDraft, string>>>({})
  const [saving, setSaving] = useState(false)

  // Derived during render — never a second useState (Step 3 / Step 11 ban).
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

  function handleSubmit() {
    const fieldErrors = validateCustomer(draft)
    if (Object.keys(fieldErrors).length) {
      setErrors(fieldErrors)
      return
    }
    setSaving(true)
    if (mode.kind === 'create') {
      setCustomers((prev) => [...prev, { ...draft, customerId: crypto.randomUUID() }])
      console.log('create', 'lab-request-001')
    } else if (mode.kind === 'edit') {
      const editId = mode.id
      setCustomers((prev) =>
          prev.map((c) =>
              c.customerId === editId ? { ...c, ...draft, customerId: c.customerId } : c
          )
      )
      console.log('update', editId, 'lab-request-001')
    }
    setMode({ kind: 'closed' })
    setDraft(emptyDraft)
    setErrors({})
    setSaving(false)
  }

  function handleCancel() {
    setMode({ kind: 'closed' })
    setDraft(emptyDraft)
    setErrors({})
    console.log('cancel', 'lab-request-001')
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