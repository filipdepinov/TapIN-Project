import { useNavigate } from 'react-router-dom'

export default function NotFoundPage() {
  const navigate = useNavigate()
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 text-center px-6">
      <div className="text-8xl font-display font-bold text-brand-surface select-none">404</div>
      <h1 className="text-2xl font-display font-bold text-navy mt-4">Page not found</h1>
      <p className="text-slate-400 text-sm mt-2 mb-8">The page you're looking for doesn't exist.</p>
      <button onClick={() => navigate('/')} className="btn-primary">Go to Dashboard</button>
    </div>
  )
}
