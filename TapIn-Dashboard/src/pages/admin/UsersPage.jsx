import { useState } from 'react'
import { getUsers, createUser, updateUser, deleteUser } from '../../api'
import { useFetch } from '../../hooks/useFetch'
import { LoadingState, ErrorState, EmptyState, Modal, ConfirmDialog, Pagination } from '../../components/ui'

const ROLES = ['student', 'teacher', 'admin']

function UserForm({ initial = {}, onSubmit, loading, error }) {
  const [form, setForm] = useState({
    fullName: initial.fullName ?? '',
    email:    initial.email    ?? '',
    password: '',
    role:     initial.role     ?? 'student',
    studentId: initial.studentId ?? '',
  })

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }))

  const handleSubmit = (e) => {
    e.preventDefault()
    const payload = { ...form }
    if (!payload.password) delete payload.password
    if (payload.role !== 'student') delete payload.studentId
    onSubmit(payload)
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="label">Full Name</label>
        <input className="input" value={form.fullName} onChange={e => set('fullName', e.target.value)} required />
      </div>
      <div>
        <label className="label">Email</label>
        <input className="input" type="email" value={form.email} onChange={e => set('email', e.target.value)} required />
      </div>
      <div>
        <label className="label">Password {initial.id && <span className="normal-case font-normal text-slate-400">(leave blank to keep)</span>}</label>
        <input className="input" type="password" value={form.password} onChange={e => set('password', e.target.value)} minLength={6} required={!initial.id} />
      </div>
      <div>
        <label className="label">Role</label>
        <select className="input" value={form.role} onChange={e => set('role', e.target.value)}>
          {ROLES.map(r => <option key={r} value={r}>{r.charAt(0).toUpperCase() + r.slice(1)}</option>)}
        </select>
      </div>
      {form.role === 'student' && (
        <div>
          <label className="label">Student ID</label>
          <input className="input" placeholder="e.g. STU001" value={form.studentId} onChange={e => set('studentId', e.target.value)} required />
        </div>
      )}
      {error && <div className="text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg">{error}</div>}
      <div className="flex justify-end gap-3 pt-2">
        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? 'Saving…' : initial.id ? 'Save Changes' : 'Create User'}
        </button>
      </div>
    </form>
  )
}

export default function UsersPage() {
  const [page, setPage]         = useState(1)
  const [roleFilter, setRole]   = useState('')
  const [search, setSearch]     = useState('')
  const [searchApplied, setApplied] = useState('')

  const [modalOpen, setModalOpen]   = useState(false)
  const [editing, setEditing]       = useState(null)
  const [deactivating, setDeact]    = useState(null)
  const [formLoading, setFormLoading] = useState(false)
  const [formError, setFormError]   = useState('')

  const params = {
    page, limit: 20,
    ...(roleFilter ? { role: roleFilter } : {}),
    ...(searchApplied ? { search: searchApplied } : {}),
  }

  const { data, loading, error, refetch } = useFetch(() => getUsers(params), [page, roleFilter, searchApplied])
  const users      = data?.data       ?? []
  const pagination = data?.pagination ?? {}

  const openCreate = () => { setEditing(null); setFormError(''); setModalOpen(true) }
  const openEdit   = (u) => { setEditing(u);    setFormError(''); setModalOpen(true) }

  const handleSubmit = async (payload) => {
    setFormLoading(true)
    setFormError('')
    try {
      if (editing) await updateUser(editing.id, payload)
      else         await createUser(payload)
      setModalOpen(false)
      refetch()
    } catch (err) {
      setFormError(err.response?.data?.error ?? 'Failed to save user')
    } finally {
      setFormLoading(false)
    }
  }

  const handleDeactivate = async () => {
    if (!deactivating) return
    try {
      await deleteUser(deactivating.id)
      refetch()
    } finally {
      setDeact(null)
    }
  }

  const roleBadge = (role) => {
    if (role === 'admin')   return <span className="badge badge-blue">Admin</span>
    if (role === 'teacher') return <span className="badge badge-green">Teacher</span>
    return <span className="badge badge-gray">Student</span>
  }

  if (loading && !data) return <LoadingState message="Loading users…" />
  if (error) return <ErrorState message={error} onRetry={refetch} />

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="page-header">
        <div>
          <h1 className="page-title">User Management</h1>
          <p className="page-subtitle">{pagination.total ?? 0} users in the system</p>
        </div>
        <button onClick={openCreate} className="btn-primary">+ New User</button>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3">
        <select
          className="input py-2 w-36"
          value={roleFilter}
          onChange={e => { setRole(e.target.value); setPage(1) }}
        >
          <option value="">All roles</option>
          {ROLES.map(r => <option key={r} value={r}>{r.charAt(0).toUpperCase() + r.slice(1)}</option>)}
        </select>

        <form className="flex gap-2" onSubmit={e => { e.preventDefault(); setApplied(search); setPage(1) }}>
          <input
            className="input py-2 w-52"
            placeholder="Search name or email…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <button type="submit" className="btn-primary py-2 px-4 text-xs">Search</button>
          {searchApplied && (
            <button type="button" onClick={() => { setApplied(''); setSearch('') }} className="btn-secondary py-2 px-3 text-xs">Clear</button>
          )}
        </form>
      </div>

      {/* Table */}
      {users.length === 0 && !loading
        ? <EmptyState icon="◎" title="No users found" message="Try changing your filters or create a new user." />
        : (
          <div className="table-wrapper">
            <table className="table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Student ID</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.id}>
                    <td className="font-medium text-navy">{u.fullName}</td>
                    <td className="text-slate-500 text-xs font-mono">{u.email}</td>
                    <td>{roleBadge(u.role)}</td>
                    <td className="text-slate-500 text-xs font-mono">{u.studentId ?? '—'}</td>
                    <td>
                      {u.isActive
                        ? <span className="badge badge-green">Active</span>
                        : <span className="badge badge-red">Inactive</span>
                      }
                    </td>
                    <td>
                      <div className="flex items-center gap-2">
                        <button onClick={() => openEdit(u)} className="btn-ghost text-xs py-1 px-3">Edit</button>
                        {u.isActive && (
                          <button
                            onClick={() => setDeact(u)}
                            className="text-xs px-3 py-1 rounded-lg bg-red-50 text-red-600 hover:bg-red-100 font-medium transition-colors"
                          >
                            Deactivate
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

      {/* Create / Edit modal */}
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit User' : 'Create New User'}>
        <UserForm
          initial={editing ?? {}}
          onSubmit={handleSubmit}
          loading={formLoading}
          error={formError}
        />
      </Modal>

      {/* Deactivate confirm */}
      <ConfirmDialog
        open={!!deactivating}
        onClose={() => setDeact(null)}
        onConfirm={handleDeactivate}
        title="Deactivate User"
        message={`Deactivate ${deactivating?.fullName}? They will no longer be able to log in.`}
        confirmLabel="Deactivate"
        danger
      />
    </div>
  )
}
