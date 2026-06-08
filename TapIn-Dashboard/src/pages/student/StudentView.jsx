import { useNavigate } from 'react-router-dom'
import { getMyAttendance } from '../../api'
import { useFetch } from '../../hooks/useFetch'
import { useAuth } from '../../context/AuthContext'
import { LoadingState, ErrorState, RateBadge, AttendanceBar } from '../../components/ui'

function formatDate(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleDateString('en-GB', {
    weekday: 'short', day: 'numeric', month: 'short', year: 'numeric'
  })
}

export default function StudentView() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const { data, loading, error, refetch } = useFetch(getMyAttendance)

  const courses = data?.data ?? []

  const overallRate = courses.length === 0 ? 0 : (() => {
    const total    = courses.reduce((s, c) => s + c.totalSessions, 0)
    const attended = courses.reduce((s, c) => s + c.attended, 0)
    return total === 0 ? 0 : Math.round((attended / total) * 10000) / 100
  })()

  const handleLogout = () => { logout(); navigate('/login') }

  if (loading) return <LoadingState message="Loading your attendance…" />
  if (error)   return <ErrorState message={error} onRetry={refetch} />

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Top bar */}
      <header className="bg-navy text-white px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-xl bg-brand flex items-center justify-center font-display font-bold">T</div>
          <span className="font-display font-bold text-lg">TapIn</span>
        </div>
        <div className="flex items-center gap-4">
          <span className="text-sm text-slate-300 hidden sm:block">{user?.fullName}</span>
          <button onClick={handleLogout} className="text-xs text-slate-400 hover:text-white transition-colors">
            Sign out →
          </button>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-4 py-8 space-y-6 animate-fade-in">
        {/* Summary card */}
        <div className="card bg-navy text-white">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-slate-300 text-sm">Hello,</p>
              <h1 className="font-display font-bold text-2xl mt-0.5">{user?.fullName}</h1>
              <p className="text-slate-300 text-sm mt-1 font-mono">ID: {user?.studentId ?? '—'}</p>
            </div>
            <div className="text-right">
              <div className="text-4xl font-display font-bold">{overallRate}%</div>
              <div className="text-slate-300 text-xs mt-1">overall attendance</div>
            </div>
          </div>

          {/* Overall bar */}
          <div className="mt-6">
            <div className="h-2 bg-white/20 rounded-full overflow-hidden">
              <div
                className="h-full rounded-full bg-white transition-all duration-700"
                style={{ width: `${Math.min(overallRate, 100)}%` }}
              />
            </div>
            <div className="flex justify-between text-xs text-slate-400 mt-1.5">
              <span>0%</span>
              <span className={overallRate >= 80 ? 'text-emerald-300' : overallRate >= 60 ? 'text-amber-300' : 'text-red-300'}>
                {overallRate >= 80 ? 'Good standing' : overallRate >= 60 ? 'Fair — needs improvement' : 'At risk'}
              </span>
              <span>100%</span>
            </div>
          </div>
        </div>

        {/* Per-course cards */}
        {courses.length === 0 ? (
          <div className="card text-center py-12">
            <p className="text-slate-400">No courses enrolled yet.</p>
          </div>
        ) : (
          courses.map(course => (
            <div key={course.courseId} className="card space-y-4">
              {/* Course header */}
              <div className="flex items-start justify-between">
                <div>
                  <span className="badge badge-blue font-mono mr-2">{course.courseCode}</span>
                  <h2 className="font-display font-bold text-navy text-base mt-2">{course.courseName}</h2>
                </div>
                <RateBadge rate={course.attendanceRate} />
              </div>

              {/* Rate bar */}
              <div>
                <AttendanceBar rate={course.attendanceRate} />
                <div className="flex justify-between text-xs text-slate-400 mt-1.5">
                  <span>{course.attended} of {course.totalSessions} sessions attended</span>
                  <span>{course.attendanceRate}%</span>
                </div>
              </div>

              {/* Session list */}
              {course.sessions.length > 0 && (
                <div className="border-t border-slate-100 pt-4">
                  <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">
                    Session History
                  </p>
                  <div className="space-y-2">
                    {course.sessions.map(s => (
                      <div key={s.sessionId} className="flex items-center justify-between py-2 px-3 rounded-xl bg-slate-50">
                        <div className="flex items-center gap-3">
                          <div className={`w-2 h-2 rounded-full flex-shrink-0 ${s.present ? 'bg-emerald-500' : 'bg-red-400'}`} />
                          <span className="text-xs text-slate-600">{formatDate(s.startedAt)}</span>
                        </div>
                        <span className={`badge text-xs ${s.present ? 'badge-green' : 'badge-red'}`}>
                          {s.present ? 'Present' : 'Absent'}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          ))
        )}
      </main>
    </div>
  )
}
