export interface CustomerToolbarProps {
    onAdd: () => void
}

export function CustomerToolbar({ onAdd }: CustomerToolbarProps) {
    return (
        <div className="toolbar">
            <button type="button" onClick={onAdd}>
                Add customer
            </button>
        </div>
    )
}