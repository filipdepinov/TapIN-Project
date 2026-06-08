import { useAuth } from '../context/AuthContext'
import { getOverview, getAttendanceTrend, getPerCourse, getStudentRates } from '../api'
import { useFetch } from '../hooks/useFetch'
import { StatCard, LoadingState, ErrorState, RateBadge, AttendanceBar } from '../components/ui'
import TrendChart   from '../components/charts/TrendChart'
import BarChart     from '../components/charts/BarChart'
import DoughnutChart from '../components/charts/DoughnutChart'

export default function OverviewPage() {
  const { user } = useAuth()

  const { data: overview, loading: l1, error: e1 } = useFetch(getOverview)
  const { data: trend,    loading: l2 }             = useFetch(() => getAttendanceTrend(30))
  const { data: perCourse, loading: l3 }            = useFetch(getPerCourse)
  const { data: rates,    loading: l4 }             = useFetch(getStudentRates)

  if (l1) return <LoadingState message="Loading dashboard…" />
  if (e1) return <ErrorState message={e1} />

  const kpi = overview?.data
  const topStudents = (rates?.data ?? []).slice(0, 8)

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Page header */}
      <div className="page-header">
        <div>
          <h1 className="page-title">Overview</h1>
          <p className="page-subtitle">
            Welcome back, {user?.fullName?.split(' ')[0]} — here's what's happening today.
          </p>
        </div>
        <div className="text-xs text-slate-400 font-mono">
          {new Date().toLocaleDateString('en-GB', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
        </div>
      </div>

      {/* KPI grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          label="Total Students"
          value={kpi?.totalStudents ?? '—'}
          icon="◎"
          accent
        />
        <StatCard
          label="Total Sessions"
          value={kpi?.totalSessions ?? '—'}
          icon="◈"
        />
        <StatCard
          label="Today's Taps"
          value={kpi?.recordsToday ?? '—'}
          sub={`${kpi?.sessionsToday ?? 0} sessions today`}
          icon="✦"
        />
        <StatCard
          label="Open Sessions"
          value={kpi?.openSessions ?? '—'}
          sub={kpi?.openSessions > 0 ? 'Live now' : 'None active'}
          icon="⬡"
        />
      </div>

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Trend — spans 2 cols */}
        <div className="lg:col-span-2 card">
          <div className="flex items-center justify-between mb-5">
            <div>
              <h3 className="font-display font-bold text-navy text-base">Attendance Trend</h3>
              <p className="text-xs text-slate-400">Daily taps over the last 30 days</p>
            </div>
          </div>
          {l2 ? <div className="h-[220px] flex items-center justify-center"><span className="text-sm text-slate-400">Loading…</span></div>
              : <TrendChart data={trend?.data ?? []} />}
        </div>

        {/* Doughnut */}
        <div className="card">
          <div className="mb-5">
            <h3 className="font-display font-bold text-navy text-base">Student Distribution</h3>
            <p className="text-xs text-slate-400">By attendance rate</p>
          </div>
          {l4 ? <div className="h-[220px] flex items-center justify-center"><span className="text-sm text-slate-400">Loading…</span></div>
              : <DoughnutChart data={rates?.data ?? []} />}
        </div>
      </div>

      {/* Per-course bar + Top students */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Bar chart */}
        <div className="card">
          <div className="mb-5">
            <h3 className="font-display font-bold text-navy text-base">Attendance by Course</h3>
            <p className="text-xs text-slate-400">Total taps vs max possible</p>
          </div>
          {l3 ? <div className="h-[220px] flex items-center justify-center"><span className="text-sm text-slate-400">Loading…</span></div>
              : <BarChart data={perCourse?.data ?? []} />}
        </div>

        {/* Top students table */}
        <div className="card">
          <div className="mb-5">
            <h3 className="font-display font-bold text-navy text-base">Top Students</h3>
            <p className="text-xs text-slate-400">Highest attendance rates</p>
          </div>
          <div className="space-y-2.5">
            {topStudents.length === 0 && <p className="text-sm text-slate-400 text-center py-8">No data yet</p>}
            {topStudents.map((s, i) => (
              <div key={s.studentId} className="flex items-center gap-3">
                <span className="text-xs font-mono text-slate-300 w-4">{i + 1}</span>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-xs font-medium text-slate-700 truncate">{s.fullName}</span>
                    <RateBadge rate={s.attendanceRate} />
                  </div>
                  <AttendanceBar rate={s.attendanceRate} />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
