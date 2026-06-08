import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { login as apiLogin } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser]       = useState(null)
  const [token, setToken]     = useState(null)
  const [loading, setLoading] = useState(true)

  // Rehydrate from localStorage on mount
  useEffect(() => {
    const savedToken = localStorage.getItem('tapin_token')
    const savedUser  = localStorage.getItem('tapin_user')
    if (savedToken && savedUser) {
      setToken(savedToken)
      setUser(JSON.parse(savedUser))
    }
    setLoading(false)
  }, [])

  const login = useCallback(async (email, password) => {
    const res = await apiLogin(email, password)
    const { token: jwt, user: userData } = res.data
    localStorage.setItem('tapin_token', jwt)
    localStorage.setItem('tapin_user', JSON.stringify(userData))
    setToken(jwt)
    setUser(userData)
    return userData
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('tapin_token')
    localStorage.removeItem('tapin_user')
    setToken(null)
    setUser(null)
  }, [])

  const isAdmin   = user?.role === 'admin'
  const isTeacher = user?.role === 'teacher'
  const isStudent = user?.role === 'student'

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, isAdmin, isTeacher, isStudent }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
