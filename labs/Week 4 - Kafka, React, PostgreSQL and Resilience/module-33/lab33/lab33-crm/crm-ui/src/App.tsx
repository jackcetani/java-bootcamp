import { useState } from 'react'
import { AppLayout } from './components/AppLayout'
import { CustomerToolbar } from './components/CustomerToolbar'
import { CustomerList } from './components/CustomerList'
import { CustomerForm } from './components/CustomerForm'
import { LoadingState } from './components/LoadingState'
import { ErrorState } from './components/ErrorState'
import { seedCustomers } from './data/seedCustomers'
import type { CustomerDraft } from './types/customer'

const emptyDraft: CustomerDraft = {
    fullName: '',
    email: '',
    phone: '',
    status: 'PROSPECT',
}

// Local-only view toggle so loading/error shells can be screenshotted (Step 8).
// No real request lifecycle exists yet — Lab 35 replaces this with fetch state.
type ViewState = 'list' | 'loading' | 'error'

export default function App() {
    const [customers] = useState(seedCustomers)
    const [draft, setDraft] = useState<CustomerDraft>(emptyDraft)
    const [viewState] = useState<ViewState>('list')

    if (viewState === 'loading') {
        return (
            <AppLayout>
                <LoadingState />
            </AppLayout>
        )
    }

    if (viewState === 'error') {
        return (
            <AppLayout>
                <ErrorState
                    message="Could not load customers."
                    onRetry={() => console.log('retry', 'lab-request-001')}
                />
            </AppLayout>
        )
    }

    return (
        <AppLayout>
            <CustomerToolbar onAdd={() => console.log('add', 'lab-request-001')} />
            <CustomerList
                customers={customers}
                onEdit={(id) => console.log('edit', id, 'lab-request-001')}
            />
            <CustomerForm
                value={draft}
                errors={{}}
                onChange={setDraft}
                onSubmit={() => console.log('submit', draft, 'lab-request-001')}
                onCancel={() => setDraft(emptyDraft)}
            />
        </AppLayout>
    )
}