import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate   = useNavigate()

  const [email,    setEmail]    = useState('')
  const [password, setPassword] = useState('')
  const [error,    setError]    = useState('')
  const [loading,  setLoading]  = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await login(email, password)
      if (user.role === 'student') navigate('/my-attendance')
      else navigate('/overview')
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed. Check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex bg-slate-50">
      {/* Left panel */}
      <div className="hidden lg:flex w-1/2 bg-navy flex-col justify-between p-12">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-brand flex items-center justify-center text-white font-display font-bold text-xl">T</div>
          <span className="font-display font-bold text-white text-xl">TapIn</span>
        </div>

        <div>
          <h1 className="font-display font-bold text-white text-5xl leading-tight mb-6">
            Attendance<br />made<br />effortless.
          </h1>
          <p className="text-slate-300 text-base leading-relaxed max-w-sm">
            NFC-powered classroom attendance tracking. Students tap in,
            teachers track instantly, administrators see everything.
          </p>
        </div>

        <div className="grid grid-cols-3 gap-4">
          {[
            { label: 'Students', value: 'NFC Tap' },
            { label: 'Teachers', value: 'Live View' },
            { label: 'Admins',   value: 'Full Control' },
          ].map(item => (
            <div key={item.label} className="bg-white/10 rounded-xl p-4">
              <div className="text-white font-semibold text-sm">{item.value}</div>
              <div className="text-slate-400 text-xs mt-1">{item.label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Right panel */}
      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-sm animate-fade-in">
          {/* Mobile logo */}
          <div className="flex items-center gap-2 mb-10 lg:hidden">
            <div className="w-9 h-9 rounded-xl bg-navy flex items-center justify-center text-white font-display font-bold">T</div>
            <span className="font-display font-bold text-navy text-xl">TapIn</span>
          </div>

          <h2 className="font-display font-bold text-navy text-3xl mb-1">Sign in</h2>
          <p className="text-sm text-slate-400 mb-8">Enter your credentials to continue</p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="label">Email address</label>
              <input
                type="email"
                className="input"
                placeholder="you@university.edu"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
                autoFocus
              />
            </div>

            <div>
              <label className="label">Password</label>
              <input
                type="password"
                className="input"
                placeholder="••••••••"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
              />
            </div>

            {error && (
              <div className="bg-red-50 border border-red-100 text-red-700 text-sm px-4 py-3 rounded-xl">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full justify-center py-3 text-base disabled:opacity-60"
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin" />
                  Signing in…
                </span>
              ) : 'Sign in →'}
            </button>
          </form>

          <p className="text-xs text-slate-400 text-center mt-8">
            TapIn NFC Attendance System · v1.0
          </p>
        </div>
      </div>
    </div>
  )
}
