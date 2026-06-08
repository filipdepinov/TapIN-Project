import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'

import LoginPage        from './pages/LoginPage'
import DashboardLayout  from './components/layout/DashboardLayout'
import OverviewPage     from './pages/OverviewPage'
import AttendancePage   from './pages/AttendancePage'
import SessionsPage     from './pages/SessionsPage'
import SessionDetailPage from './pages/SessionDetailPage'
import CoursesPage      from './pages/admin/CoursesPage'
import UsersPage        from './pages/admin/UsersPage'
import StudentView      from './pages/student/StudentView'
import NotFoundPage     from './pages/NotFoundPage'

function RequireAuth({ children, roles }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="min-h-screen flex items-center justify-center"><Spinner /></div>
  if (!user) return <Navigate to="/login" replace />
  if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />
  return children
}

function Spinner() {
  return (
    <div className="w-8 h-8 border-4 border-brand-surface border-t-navy rounded-full animate-spin" />
  )
}

function RootRedirect() {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  if (user.role === 'student') return <Navigate to="/my-attendance" replace />
  return <Navigate to="/overview" replace />
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          {/* Student — own view only */}
          <Route path="/my-attendance" element={
            <RequireAuth roles={['student']}>
              <StudentView />
            </RequireAuth>
          } />

          {/* Teacher + Admin shared layout */}
          <Route path="/" element={
            <RequireAuth roles={['admin', 'teacher']}>
              <DashboardLayout />
            </RequireAuth>
          }>
            <Route index element={<RootRedirect />} />
            <Route path="overview"  element={<OverviewPage />} />
            <Route path="attendance" element={<AttendancePage />} />
            <Route path="sessions"   element={<SessionsPage />} />
            <Route path="sessions/:id" element={<SessionDetailPage />} />

            {/* Admin-only routes */}
            <Route path="courses" element={
              <RequireAuth roles={['admin']}>
                <CoursesPage />
              </RequireAuth>
            } />
            <Route path="users" element={
              <RequireAuth roles={['admin']}>
                <UsersPage />
              </RequireAuth>
            } />
          </Route>

          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
