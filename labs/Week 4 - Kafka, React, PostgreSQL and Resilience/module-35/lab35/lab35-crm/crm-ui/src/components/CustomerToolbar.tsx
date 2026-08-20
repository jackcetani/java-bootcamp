export interface CustomerToolbarProps {
    query: string
    onQueryChange: (value: string) => void
    onAdd: () => void
}

export function CustomerToolbar({ query, onQueryChange, onAdd }: CustomerToolbarProps) {
    return (
        <div className="toolbar">
            <input
                type="search"
                aria-label="Search customers"
                value={query}
                onChange={(e) => onQueryChange(e.target.value)}
            />
            <button type="button" onClick={onAdd}>
                Add customer
            </button>
        </div>
    )
}