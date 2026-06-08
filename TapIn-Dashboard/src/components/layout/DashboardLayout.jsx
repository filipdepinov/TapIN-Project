import { useState } from 'react'
import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const NavItem = ({ to, icon, label, end = false }) => (
  <NavLink
    to={to}
    end={end}
    className={({ isActive }) =>
      `flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-medium transition-all duration-150
      ${isActive
        ? 'bg-brand text-white shadow-sm'
        : 'text-slate-300 hover:bg-white/10 hover:text-white'}`
    }
  >
    <span className="text-lg leading-none">{icon}</span>
    <span>{label}</span>
  </NavLink>
)

export default function DashboardLayout() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const handleLogout = () => { logout(); navigate('/login') }

  const navItems = [
    { to: '/overview',   icon: '⬡', label: 'Overview',   end: true },
    { to: '/sessions',   icon: '◈', label: 'Sessions' },
    { to: '/attendance', icon: '✦', label: 'Attendance' },
    ...(isAdmin ? [
      { to: '/courses', icon: '◉', label: 'Courses' },
      { to: '/users',   icon: '◎', label: 'Users' },
    ] : []),
  ]

  return (
    <div className="flex h-screen overflow-hidden bg-slate-50">
      {/* ── Sidebar ─────────────────────────────────────────── */}
      <aside className={`
        fixed inset-y-0 left-0 z-30 w-60 bg-navy flex flex-col
        transform transition-transform duration-200
        ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}
        lg:relative lg:translate-x-0
      `}>
        {/* Logo */}
        <div className="px-6 py-6 border-b border-white/10">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-brand flex items-center justify-center text-white font-display font-bold text-lg">
              T
            </div>
            <div>
              <div className="font-display font-bold text-white text-lg leading-none">TapIn</div>
              <div className="text-xs text-slate-400 mt-0.5">Attendance System</div>
            </div>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          {navItems.map(item => (
            <NavItem key={item.to} {...item} />
          ))}
        </nav>

        {/* User footer */}
        <div className="px-4 py-4 border-t border-white/10">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-8 h-8 rounded-full bg-brand-light flex items-center justify-center text-white text-xs font-bold">
              {user?.fullName?.[0] ?? '?'}
            </div>
            <div className="flex-1 min-w-0">
              <div className="text-sm font-medium text-white truncate">{user?.fullName}</div>
              <div className="text-xs text-slate-400 capitalize">{user?.role}</div>
            </div>
          </div>
          <button onClick={handleLogout} className="w-full text-left text-xs text-slate-400 hover:text-white transition-colors px-2 py-1.5 rounded-lg hover:bg-white/10">
            Sign out →
          </button>
        </div>
      </aside>

      {/* Sidebar overlay (mobile) */}
      {sidebarOpen && (
        <div className="fixed inset-0 z-20 bg-black/40 lg:hidden" onClick={() => setSidebarOpen(false)} />
      )}

      {/* ── Main content ────────────────────────────────────── */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Topbar */}
        <header className="bg-white border-b border-slate-100 px-6 py-3 flex items-center gap-4 flex-shrink-0">
          <button
            onClick={() => setSidebarOpen(true)}
            className="lg:hidden p-2 rounded-lg hover:bg-slate-100 transition-colors"
          >
            <span className="text-xl">☰</span>
          </button>
          <div className="flex-1" />
          <div className="flex items-center gap-2">
            <span className={`badge ${
              user?.role === 'admin'   ? 'badge-blue' :
              user?.role === 'teacher' ? 'badge-green' : 'badge-gray'
            }`}>
              {user?.role}
            </span>
            <span className="text-sm text-slate-500 font-medium hidden sm:block">
              {user?.fullName}
            </span>
          </div>
        </header>

        {/* Page */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
