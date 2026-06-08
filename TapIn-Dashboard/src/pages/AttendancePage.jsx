import { useState } from 'react'
import { getAttendance } from '../api'
import { useFetch } from '../hooks/useFetch'
import { LoadingState, ErrorState, EmptyState, Pagination, RateBadge, AttendanceBar } from '../components/ui'
import { getStudentRates } from '../api'
import DoughnutChart from '../components/charts/DoughnutChart'

function formatDate(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('en-GB', {
    day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'
  })
}

export default function AttendancePage() {
  const [page, setPage]     = useState(1)
  const [search, setSearch] = useState('')
  const [applied, setApplied] = useState('')

  const params = { page, limit: 25, ...(applied ? { studentId: applied } : {}) }
  const { data, loading, error, refetch } = useFetch(() => getAttendance(params), [page, applied])
  const { data: rates } = useFetch(getStudentRates)

  const records    = data?.data       ?? []
  const pagination = data?.pagination ?? {}

  const handleSearch = (e) => {
    e.preventDefault()
    setApplied(search.trim())
    setPage(1)
  }

  if (loading && !data) return <LoadingState message="Loading attendance records…" />
  if (error) return <ErrorState message={error} onRetry={refetch} />

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="page-header">
        <div>
          <h1 className="page-title">Attendance</h1>
          <p className="page-subtitle">{pagination.total ?? 0} records total</p>
        </div>
      </div>

      {/* Summary + chart */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 card">
          <h3 className="font-display font-bold text-navy text-base mb-4">Student Attendance Rates</h3>
          <div className="space-y-3 max-h-72 overflow-y-auto pr-1">
            {(rates?.data ?? []).map(s => (
              <div key={s.studentId} className="flex items-center gap-3">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-xs font-medium text-slate-700 truncate">{s.fullName}</span>
                    <div className="flex items-center gap-2 flex-shrink-0 ml-2">
                      <span className="text-xs text-slate-400 font-mono">
                        {s.sessionsAttended}/{s.totalSessions}
                      </span>
                      <RateBadge rate={s.attendanceRate} />
                    </div>
                  </div>
                  <AttendanceBar rate={s.attendanceRate} />
                </div>
              </div>
            ))}
            {(rates?.data ?? []).length === 0 && (
              <p className="text-sm text-slate-400 text-center py-6">No data yet</p>
            )}
          </div>
        </div>

        <div className="card">
          <h3 className="font-display font-bold text-navy text-base mb-4">Rate Distribution</h3>
          <DoughnutChart data={rates?.data ?? []} />
        </div>
      </div>

      {/* Records table */}
      <div className="card">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-5">
          <h3 className="font-display font-bold text-navy text-base">All Records</h3>
          <form onSubmit={handleSearch} className="flex gap-2">
            <input
              className="input py-2 w-48 text-xs"
              placeholder="Filter by student ID…"
              value={search}
              onChange={e => setSearch(e.target.value)}
            />
            <button type="submit" className="btn-primary py-2 px-4 text-xs">Search</button>
            {applied && (
              <button
                type="button"
                onClick={() => { setApplied(''); setSearch(''); setPage(1) }}
                className="btn-secondary py-2 px-3 text-xs"
              >
                Clear
              </button>
            )}
          </form>
        </div>

        {loading && <div className="text-center py-4 text-sm text-slate-400">Refreshing…</div>}

        {records.length === 0 && !loading
          ? <EmptyState icon="✦" title="No records found" message="Try adjusting your filters." />
          : (
            <div className="table-wrapper">
              <table className="table">
                <thead>
                  <tr>
                    <th>Student</th>
                    <th>Course</th>
                    <th>Session Date</th>
                    <th>Tapped At</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {records.map(r => (
                    <tr key={r.id}>
                      <td>
                        <div className="font-medium text-navy">{r.student?.fullName ?? '—'}</div>
                        <div className="text-xs text-slate-400 font-mono">{r.student?.studentId}</div>
                      </td>
                      <td>
                        <div className="text-sm text-slate-700">{r.session?.course?.name ?? '—'}</div>
                        <div className="text-xs text-slate-400 font-mono">{r.session?.course?.code}</div>
                      </td>
                      <td className="text-slate-500 text-xs">{formatDate(r.session?.startedAt)}</td>
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
          )
        }

        <Pagination page={page} totalPages={pagination.totalPages ?? 1} onChange={setPage} />
      </div>
    </div>
  )
}
