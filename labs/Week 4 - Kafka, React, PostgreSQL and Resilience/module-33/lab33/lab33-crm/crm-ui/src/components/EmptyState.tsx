export function EmptyState({ title = 'No customers yet' }: { title?: string }) {
  return <p role="status">{title}</p>
}