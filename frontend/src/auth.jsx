import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { api } from './api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('user')
    return raw ? JSON.parse(raw) : null
  })
  const [loading, setLoading] = useState(!!localStorage.getItem('token'))

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      setLoading(false)
      return
    }
    api('/api/auth/me')
      .then((me) => {
        const next = {
          usuarioId: me.usuarioId,
          nome: me.nome,
          email: me.email,
          role: me.role,
          alunoId: me.alunoId,
          professorId: me.professorId,
        }
        setUser(next)
        localStorage.setItem('user', JSON.stringify(next))
      })
      .catch(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        setUser(null)
      })
      .finally(() => setLoading(false))
  }, [])

  const value = useMemo(() => ({
    user,
    loading,
    async login(email, senha) {
      const data = await api('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, senha }),
      })
      localStorage.setItem('token', data.token)
      const me = await api('/api/auth/me')
      const next = {
        usuarioId: me.usuarioId,
        nome: me.nome,
        email: me.email,
        role: me.role,
        alunoId: me.alunoId,
        professorId: me.professorId,
      }
      localStorage.setItem('user', JSON.stringify(next))
      setUser(next)
      return next
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      setUser(null)
    },
  }), [user, loading])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
