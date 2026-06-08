import api from './axios'

// ── Auth ─────────────────────────────────────────────────────
export const login = (email, password) =>
  api.post('/auth/login', { email, password })

export const getMe = () => api.get('/auth/me')

// ── Users ─────────────────────────────────────────────────────
export const getUsers = (params) => api.get('/users', { params })
export const createUser = (data)  => api.post('/users', data)
export const updateUser = (id, data) => api.patch(`/users/${id}`, data)
export const deleteUser = (id)    => api.delete(`/users/${id}`)
export const getUser = (id)       => api.get(`/users/${id}`)

// ── Courses ───────────────────────────────────────────────────
export const getCourses = ()       => api.get('/courses')
export const createCourse = (data) => api.post('/courses', data)
export const getCourse = (id)      => api.get(`/courses/${id}`)
export const enrollStudent = (courseId, studentId)  => api.post(`/courses/${courseId}/enroll`, { studentId })
export const unenrollStudent = (courseId, studentId) => api.delete(`/courses/${courseId}/enroll`, { data: { studentId } })

// ── Sessions ──────────────────────────────────────────────────
export const getSessions = (params) => api.get('/sessions', { params })
export const getSession  = (id)     => api.get(`/sessions/${id}`)
export const closeSession = (id)    => api.patch(`/sessions/${id}/close`)

// ── Attendance ────────────────────────────────────────────────
export const getAttendance = (params) => api.get('/attendance', { params })

// ── Statistics ────────────────────────────────────────────────
export const getOverview       = () => api.get('/statistics/overview')
export const getAttendanceTrend = (days = 30) => api.get('/statistics/attendance-trend', { params: { days } })
export const getPerCourse      = () => api.get('/statistics/per-course')
export const getStudentRates   = (courseId) => api.get('/statistics/student-rates', { params: courseId ? { courseId } : {} })
export const getMyAttendance   = () => api.get('/statistics/my-attendance')
