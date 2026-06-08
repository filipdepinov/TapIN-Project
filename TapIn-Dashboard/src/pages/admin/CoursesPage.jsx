import { useState } from 'react'
import { getCourses, createCourse, getUsers, enrollStudent, unenrollStudent, getCourse } from '../../api'
import { useFetch } from '../../hooks/useFetch'
import { LoadingState, ErrorState, EmptyState, Modal, ConfirmDialog } from '../../components/ui'

function CourseForm({ teachers = [], onSubmit, loading, error }) {
  const [form, setForm] = useState({ name: '', code: '', teacherId: teachers[0]?.id ?? '', description: '' })
  const set = (k, v) => setForm(f => ({ ...f, [k]: v }))

  return (
    <form onSubmit={e => { e.preventDefault(); onSubmit(form) }} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <div className="col-span-2">
          <label className="label">Course Name</label>
          <input className="input" value={form.name} onChange={e => set('name', e.target.value)} required placeholder="Data Structures & Algorithms" />
        </div>
        <div>
          <label className="label">Course Code</label>
          <input className="input" value={form.code} onChange={e => set('code', e.target.value.toUpperCase())} required placeholder="CS301" />
        </div>
        <div>
          <label className="label">Teacher</label>
          <select className="input" value={form.teacherId} onChange={e => set('teacherId', e.target.value)} required>
            <option value="">Select teacher…</option>
            {teachers.map(t => <option key={t.id} value={t.id}>{t.fullName}</option>)}
          </select>
        </div>
        <div className="col-span-2">
          <label className="label">Description <span className="normal-case font-normal text-slate-400">(optional)</span></label>
          <input className="input" value={form.description} onChange={e => set('description', e.target.value)} placeholder="Short course description" />
        </div>
      </div>
      {error && <div className="text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg">{error}</div>}
      <div className="flex justify-end">
        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? 'Creating…' : 'Create Course'}
        </button>
      </div>
    </form>
  )
}

function EnrollModal({ course, students, open, onClose, onRefetch }) {
  const [enrolling, setEnrolling]   = useState(false)
  const [unenrolling, setUnenrolling] = useState(null)
  const [selectedId, setSelectedId] = useState('')
  const [err, setErr]               = useState('')

  const { data: detail, refetch: refetchDetail } = useFetch(
    () => getCourse(course?.id),
    [course?.id, open]
  )

  const enrolled   = detail?.data?.enrollments ?? []
  const enrolledIds = new Set(enrolled.map(e => e.student.id))
  const available  = students.filter(s => !enrolledIds.has(s.id))

  const handleEnroll = async () => {
    if (!selectedId) return
    setEnrolling(true); setErr('')
    try {
      await enrollStudent(course.id, selectedId)
      setSelectedId('')
      refetchDetail()
      onRefetch()
    } catch (e) {
      setErr(e.response?.data?.error ?? 'Failed to enroll')
    } finally { setEnrolling(false) }
  }

  const handleUnenroll = async (studentId) => {
    setUnenrolling(studentId)
    try {
      await unenrollStudent(course.id, studentId)
      refetchDetail()
      onRefetch()
    } finally { setUnenrolling(null) }
  }

  if (!open || !course) return null

  return (
    <Modal open={open} onClose={onClose} title={`Enroll Students — ${course.code}`}>
      <div className="space-y-4">
        {/* Enroll new */}
        <div className="flex gap-2">
          <select
            className="input flex-1 py-2"
            value={selectedId}
            onChange={e => setSelectedId(e.target.value)}
          >
            <option value="">Select student to enroll…</option>
            {available.map(s => (
              <option key={s.id} value={s.id}>{s.fullName} ({s.studentId})</option>
            ))}
          </select>
          <button onClick={handleEnroll} disabled={!selectedId || enrolling} className="btn-primary py-2 px-4">
            {enrolling ? '…' : 'Enroll'}
          </button>
        </div>
        {err && <p className="text-xs text-red-600">{err}</p>}

        {/* Enrolled list */}
        <div className="border-t border-slate-100 pt-4">
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">
            Enrolled ({enrolled.length})
          </p>
          {enrolled.length === 0
            ? <p className="text-sm text-slate-400 text-center py-4">No students enrolled yet</p>
            : (
              <div className="space-y-2 max-h-56 overflow-y-auto">
                {enrolled.map(e => (
                  <div key={e.student.id} className="flex items-center justify-between py-2 px-3 bg-slate-50 rounded-xl">
                    <div>
                      <span className="text-sm font-medium text-slate-700">{e.student.fullName}</span>
                      <span className="text-xs text-slate-400 ml-2 font-mono">{e.student.studentId}</span>
                    </div>
                    <button
                      onClick={() => handleUnenroll(e.student.id)}
                      disabled={unenrolling === e.student.id}
                      className="text-xs text-red-500 hover:text-red-700 font-medium transition-colors"
                    >
                      {unenrolling === e.student.id ? '…' : 'Remove'}
                    </button>
                  </div>
                ))}
              </div>
            )
          }
        </div>
      </div>
    </Modal>
  )
}

export default function CoursesPage() {
  const [createOpen, setCreateOpen] = useState(false)
  const [enrollCourse, setEnrollCourse] = useState(null)
  const [formLoading, setFormLoading]   = useState(false)
  const [formError, setFormError]       = useState('')

  const { data: coursesData, loading, error, refetch } = useFetch(getCourses)
  const { data: usersData }  = useFetch(() => getUsers({ role: 'teacher', limit: 100 }))
  const { data: studentsData } = useFetch(() => getUsers({ role: 'student', limit: 500 }))

  const courses  = coursesData?.data ?? []
  const teachers = usersData?.data   ?? []
  const students = studentsData?.data ?? []

  const handleCreate = async (form) => {
    setFormLoading(true); setFormError('')
    try {
      await createCourse(form)
      setCreateOpen(false)
      refetch()
    } catch (e) {
      setFormError(e.response?.data?.error ?? 'Failed to create course')
    } finally { setFormLoading(false) }
  }

  if (loading) return <LoadingState message="Loading courses…" />
  if (error)   return <ErrorState message={error} onRetry={refetch} />

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="page-header">
        <div>
          <h1 className="page-title">Courses</h1>
          <p className="page-subtitle">{courses.length} active courses</p>
        </div>
        <button onClick={() => { setFormError(''); setCreateOpen(true) }} className="btn-primary">+ New Course</button>
      </div>

      {courses.length === 0
        ? <EmptyState icon="◉" title="No courses yet" message="Create a course to get started." />
        : (
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
            {courses.map(c => (
              <div key={c.id} className="card hover:shadow-panel transition-shadow duration-200">
                <div className="flex items-start justify-between mb-3">
                  <span className="badge badge-blue font-mono">{c.code}</span>
                  <span className="text-xs text-slate-400">{c._count?.sessions ?? 0} sessions</span>
                </div>
                <h3 className="font-display font-bold text-navy text-base mb-1">{c.name}</h3>
                {c.description && (
                  <p className="text-xs text-slate-500 mb-3 line-clamp-2">{c.description}</p>
                )}
                <div className="flex items-center justify-between mt-4 pt-4 border-t border-slate-100">
                  <div>
                    <span className="text-xs text-slate-400">Teacher: </span>
                    <span className="text-xs font-medium text-slate-700">{c.teacher?.fullName ?? '—'}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-xs text-slate-400">{c._count?.enrollments ?? 0} enrolled</span>
                    <button
                      onClick={() => setEnrollCourse(c)}
                      className="btn-secondary py-1.5 px-3 text-xs"
                    >
                      Manage
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )
      }

      {/* Create modal */}
      <Modal open={createOpen} onClose={() => setCreateOpen(false)} title="Create Course">
        <CourseForm teachers={teachers} onSubmit={handleCreate} loading={formLoading} error={formError} />
      </Modal>

      {/* Enroll modal */}
      <EnrollModal
        course={enrollCourse}
        students={students}
        open={!!enrollCourse}
        onClose={() => setEnrollCourse(null)}
        onRefetch={refetch}
      />
    </div>
  )
}
