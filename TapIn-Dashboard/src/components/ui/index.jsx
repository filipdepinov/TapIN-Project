// ── Spinner ───────────────────────────────────────────────────
export function Spinner({ size = 'md' }) {
  const s = size === 'sm' ? 'w-4 h-4 border-2' : size === 'lg' ? 'w-10 h-10 border-4' : 'w-7 h-7 border-3'
  return <div className={`${s} border-brand-surface border-t-navy rounded-full animate-spin`} />
}

// ── Loading state ─────────────────────────────────────────────
export function LoadingState({ message = 'Loading…' }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-4">
      <Spinner size="lg" />
      <p className="text-sm text-slate-400">{message}</p>
    </div>
  )
}

// ── Error state ───────────────────────────────────────────────
export function ErrorState({ message, onRetry }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-4 text-center">
      <div className="text-4xl">⚠</div>
      <div>
        <p className="text-sm font-semibold text-slate-700">Something went wrong</p>
        <p className="text-sm text-slate-400 mt-1">{message}</p>
      </div>
      {onRetry && (
        <button onClick={onRetry} className="btn-secondary text-xs">
          Try again
        </button>
      )}
    </div>
  )
}

// ── Empty state ───────────────────────────────────────────────
export function EmptyState({ icon = '◉', title, message }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-3 text-center">
      <div className="text-4xl text-slate-200">{icon}</div>
      <div>
        <p className="text-sm font-semibold text-slate-500">{title}</p>
        {message && <p className="text-xs text-slate-400 mt-1">{message}</p>}
      </div>
    </div>
  )
}

// ── Stat card ─────────────────────────────────────────────────
export function StatCard({ label, value, sub, accent = false, icon }) {
  return (
    <div className={`stat-card ${accent ? 'bg-navy text-white' : ''}`} style={{ animationFillMode: 'both' }}>
      <div className="flex items-start justify-between">
        <span className={`stat-label ${accent ? 'text-blue-200' : ''}`}>{label}</span>
        {icon && <span className="text-xl opacity-40">{icon}</span>}
      </div>
      <span className={`stat-value ${accent ? 'text-white' : ''}`}>{value}</span>
      {sub && <span className={`text-xs ${accent ? 'text-blue-200' : 'text-slate-400'}`}>{sub}</span>}
    </div>
  )
}

// ── Attendance rate badge ─────────────────────────────────────
export function RateBadge({ rate }) {
  const color = rate >= 80 ? 'badge-green' : rate >= 60 ? 'badge-yellow' : 'badge-red'
  return <span className={`badge ${color}`}>{rate.toFixed(1)}%</span>
}

// ── Progress bar ──────────────────────────────────────────────
export function AttendanceBar({ rate }) {
  const color = rate >= 80 ? 'bg-emerald-500' : rate >= 60 ? 'bg-amber-400' : 'bg-red-500'
  return (
    <div className="progress-bar w-full">
      <div className={`progress-fill ${color}`} style={{ width: `${Math.min(rate, 100)}%` }} />
    </div>
  )
}

// ── Modal ─────────────────────────────────────────────────────
export function Modal({ open, onClose, title, children }) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />
      <div className="relative bg-white rounded-2xl shadow-panel w-full max-w-lg animate-slide-up p-6">
        <div className="flex items-center justify-between mb-5">
          <h2 className="text-lg font-display font-bold text-navy">{title}</h2>
          <button onClick={onClose} className="btn-ghost p-1.5 text-lg">✕</button>
        </div>
        {children}
      </div>
    </div>
  )
}

// ── Confirm dialog ────────────────────────────────────────────
export function ConfirmDialog({ open, onClose, onConfirm, title, message, confirmLabel = 'Confirm', danger = false }) {
  if (!open) return null
  return (
    <Modal open={open} onClose={onClose} title={title}>
      <p className="text-sm text-slate-600 mb-6">{message}</p>
      <div className="flex gap-3 justify-end">
        <button onClick={onClose} className="btn-secondary">Cancel</button>
        <button onClick={onConfirm} className={danger ? 'btn-danger' : 'btn-primary'}>{confirmLabel}</button>
      </div>
    </Modal>
  )
}

// ── Pagination ────────────────────────────────────────────────
export function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null
  return (
    <div className="flex items-center justify-center gap-2 pt-4">
      <button
        disabled={page <= 1}
        onClick={() => onChange(page - 1)}
        className="btn-ghost disabled:opacity-30 disabled:cursor-not-allowed"
      >
        ← Prev
      </button>
      <span className="text-sm text-slate-500">
        Page {page} of {totalPages}
      </span>
      <button
        disabled={page >= totalPages}
        onClick={() => onChange(page + 1)}
        className="btn-ghost disabled:opacity-30 disabled:cursor-not-allowed"
      >
        Next →
      </button>
    </div>
  )
}
