import { render, screen } from '@testing-library/react'
import { CustomerForm } from './CustomerForm'

describe('CustomerForm', () => {
    it('associates the full name label with its input', () => {
        render(
            <CustomerForm
                value={{ fullName: '', email: '', phone: '', status: 'PROSPECT' }}
                errors={{}}
                saving={false}
                onChange={() => {}}
                onSubmit={() => {}}
                onCancel={() => {}}
            />
        )
        expect(screen.getByLabelText('Full name')).toBeInTheDocument()
    })
})