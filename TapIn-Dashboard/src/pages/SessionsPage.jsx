import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getSessions, closeSession } from '../api'
import { useFetch } from '../hooks/useFetch'
import { LoadingState, ErrorState, EmptyState, ConfirmDialog, Pagination } from '../components/ui'

function formatDate(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('en-GB', {
    day: 'numeric', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}

function StatusBadge({ status }) {
  return status === 'open'
    ? <span className="badge badge-green">● Live</span>
    : <span className="badge badge-gray">Closed</span>
}

export default function SessionsPage() {
  const navigate = useNavigate()
  const [page, setPage]           = useState(1)
  const [statusFilter, setStatus] = useState('')
  const [closing, setClosing]     = useState(null)   // session to confirm-close
  const [closing2, setClosing2]   = useState(false)  // loading state

  const { data, loading, error, refetch } = useFetch(
    () => getSessions({ page, limit: 20, ...(statusFilter ? { status: statusFilter } : {}) }),
    [page, statusFilter]
  )

  const sessions    = data?.data          ?? []
  const pagination  = data?.pagination    ?? {}

  const handleClose = async () => {
    if (!closing) return
    setClosing2(true)
    try {
      await closeSession(closing.id)
      refetch()
    } finally {
      setClosing2(false)
      setClosing(null)
    }
  }

  if (loading) return <LoadingState message="Loading sessions…" />
  if (error)   return <ErrorState message={error} onRetry={refetch} />

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="page-header">
        <div>
          <h1 className="page-title">Sessions</h1>
          <p className="page-subtitle">{pagination.total ?? 0} attendance sessions recorded</p>
        </div>
        <div className="flex items-center gap-3">
          <select
            className="input w-36 py-2"
            value={statusFilter}
            onChange={e => { setStatus(e.target.value); setPage(1) }}
          >
            <option value="">All statuses</option>
            <option value="open">Live</option>
            <option value="closed">Closed</option>
          </select>
        </div>
      </div>

      {/* Table */}
      {sessions.length === 0
        ? <EmptyState icon="◈" title="No sessions found" message="Sessions started from the Teacher App appear here." />
        : (
          <div className="table-wrapper">
            <table className="table">
              <thead>
                <tr>
                  <th>Course</th>
                  <th>Teacher</th>
                  <th>Started</th>
                  <th>Ended</th>
                  <th>Students</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {sessions.map(s => (
                  <tr key={s.id}>
                    <td>
                      <div className="font-medium text-navy">{s.course?.name ?? '—'}</div>
                      <div className="text-xs text-slate-400 font-mono">{s.course?.code}</div>
                    </td>
                    <td className="text-slate-600">{s.teacher?.fullName ?? '—'}</td>
                    <td className="text-slate-500 text-xs">{formatDate(s.startedAt)}</td>
                    <td className="text-slate-500 text-xs">{s.endedAt ? formatDate(s.endedAt) : '—'}</td>
                    <td>
                      <span className="font-semibold text-navy">{s._count?.records ?? 0}</span>
                    </td>
                    <td><StatusBadge status={s.status} /></td>
                    <td>
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => navigate(`/sessions/${s.id}`)}
                          className="btn-ghost text-xs py-1 px-3"
                        >
                          View
                        </button>
                        {s.status === 'open' && (
                          <button
                            onClick={() => setClosing(s)}
                            className="text-xs px-3 py-1 rounded-lg bg-red-50 text-red-600 hover:bg-red-100 font-medium transition-colors"
                          >
                            Close
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      }

      <Pagination page={page} totalPages={pagination.totalPages ?? 1} onChange={setPage} />

      {/* Confirm close dialog */}
      <ConfirmDialog
        open={!!closing}
        onClose={() => setClosing(null)}
        onConfirm={handleClose}
        title="Close Session"
        message={`Close the session for ${closing?.course?.name ?? 'this course'}? No more taps will be recorded after closing.`}
        confirmLabel={closing2 ? 'Closing…' : 'Close Session'}
        danger
      />
    </div>
  )
}
