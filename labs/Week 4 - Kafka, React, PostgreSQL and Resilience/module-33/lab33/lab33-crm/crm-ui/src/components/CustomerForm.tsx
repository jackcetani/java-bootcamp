import type { CustomerDraft } from '../types/customer'

export interface CustomerFormProps {
    value: CustomerDraft
    errors: Partial<Record<keyof CustomerDraft, string>>
    onChange: (next: CustomerDraft) => void
    onSubmit: () => void
    onCancel: () => void
}

export function CustomerForm({ value, errors, onChange, onSubmit, onCancel }: CustomerFormProps) {
    return (
        <form
            onSubmit={(e) => {
                e.preventDefault()
                onSubmit()
            }}
        >
            <div>
                <label htmlFor="fullName">Full name</label>
                <input
                    id="fullName"
                    name="fullName"
                    value={value.fullName}
                    onChange={(e) => onChange({ ...value, fullName: e.target.value })}
                    aria-describedby="fullName-error"
                />
                <p id="fullName-error" role="alert">
                    {errors.fullName}
                </p>
            </div>

            <div>
                <label htmlFor="email">Email</label>
                <input
                    id="email"
                    name="email"
                    type="email"
                    value={value.email}
                    onChange={(e) => onChange({ ...value, email: e.target.value })}
                    aria-describedby="email-error"
                />
                <p id="email-error" role="alert">
                    {errors.email}
                </p>
            </div>

            <div>
                <label htmlFor="phone">Phone</label>
                <input
                    id="phone"
                    name="phone"
                    value={value.phone}
                    onChange={(e) => onChange({ ...value, phone: e.target.value })}
                    aria-describedby="phone-error"
                />
                <p id="phone-error" role="alert">
                    {errors.phone}
                </p>
            </div>

            <div>
                <label htmlFor="status">Status</label>
                <select
                    id="status"
                    name="status"
                    value={value.status}
                    onChange={(e) =>
                        onChange({ ...value, status: e.target.value as CustomerDraft['status'] })
                    }
                >
                    <option value="PROSPECT">Prospect</option>
                    <option value="ACTIVE">Active</option>
                    <option value="SUSPENDED">Suspended</option>
                    <option value="CLOSED">Closed</option>
                </select>
            </div>

            <button type="submit">Save</button>
            <button type="button" onClick={onCancel}>
                Cancel
            </button>
        </form>
    )
}