import { useParams, useNavigate } from 'react-router-dom'
import { getSession } from '../api'
import { useFetch } from '../hooks/useFetch'
import { LoadingState, ErrorState, RateBadge } from '../components/ui'

function formatDate(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('en-GB', {
    day: 'numeric', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  })
}

export default function SessionDetailPage() {
  const { id }   = useParams()
  const navigate = useNavigate()

  const { data, loading, error, refetch } = useFetch(() => getSession(id), [id])
  const session = data?.data

  if (loading) return <LoadingState message="Loading session…" />
  if (error)   return <ErrorState message={error} onRetry={refetch} />
  if (!session) return null

  const enrolled    = session._count?.records ?? session.records?.length ?? 0
  const records     = session.records ?? []

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Back */}
      <button onClick={() => navigate(-1)} className="btn-ghost text-sm">
        ← Back to Sessions
      </button>

      {/* Header card */}
      <div className="card bg-navy text-white">
        <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="badge bg-white/20 text-white text-xs font-mono">
                {session.course?.code}
              </span>
              {session.status === 'open'
                ? <span className="badge bg-emerald-400/20 text-emerald-300">● Live</span>
                : <span className="badge bg-white/10 text-slate-300">Closed</span>
              }
            </div>
            <h1 className="font-display font-bold text-2xl mt-2">{session.course?.name}</h1>
            <p className="text-slate-300 text-sm mt-1">Teacher: {session.teacher?.fullName}</p>
          </div>
          <div className="text-right flex-shrink-0">
            <div className="text-4xl font-display font-bold">{enrolled}</div>
            <div className="text-slate-300 text-xs mt-1">students present</div>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4 mt-6 pt-6 border-t border-white/10">
          <div>
            <div className="text-xs text-slate-400 mb-1">Started</div>
            <div className="text-sm font-medium">{formatDate(session.startedAt)}</div>
          </div>
          <div>
            <div className="text-xs text-slate-400 mb-1">Ended</div>
            <div className="text-sm font-medium">{formatDate(session.endedAt)}</div>
          </div>
          {session.notes && (
            <div className="col-span-2">
              <div className="text-xs text-slate-400 mb-1">Notes</div>
              <div className="text-sm">{session.notes}</div>
            </div>
          )}
        </div>
      </div>

      {/* Attendance records table */}
      <div className="card">
        <div className="flex items-center justify-between mb-5">
          <h2 className="font-display font-bold text-navy text-base">
            Attendance Records
            <span className="ml-2 text-sm font-normal text-slate-400">({records.length})</span>
          </h2>
        </div>

        {records.length === 0 ? (
          <p className="text-sm text-slate-400 text-center py-12">
            No students have tapped in yet.
          </p>
        ) : (
          <div className="table-wrapper">
            <table className="table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Student</th>
                  <th>Student ID</th>
                  <th>Tapped At</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {records.map((r, i) => (
                  <tr key={r.id}>
                    <td className="text-slate-400 font-mono text-xs">{i + 1}</td>
                    <td className="font-medium text-navy">{r.student?.fullName ?? '—'}</td>
                    <td className="font-mono text-xs text-slate-500">{r.student?.studentId ?? '—'}</td>
                    <td className="text-slate-500 text-xs">{formatDate(r.tappedAt)}</td>
                    <td>
                      <span className={`badge ${
                        r.status === 'present' ? 'badge-green' :
                        r.status === 'late'    ? 'badge-yellow' : 'badge-gray'
                      }`}>
                        {r.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
