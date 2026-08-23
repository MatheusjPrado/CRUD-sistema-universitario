import { useState } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth'

export default function LoginPage() {
  const { user, login } = useAuth()
  const [email, setEmail] = useState('admin@uni.local')
  const [senha, setSenha] = useState('admin123')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user) {
    if (user.role === 'ADMIN') return <Navigate to="/admin" replace />
    if (user.role === 'PROFESSOR') return <Navigate to="/professor" replace />
    return <Navigate to="/aluno" replace />
  }

  async function onSubmit(e) {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      await login(email, senha)
    } catch (err) {
      setError(err.message || 'Falha no login')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={onSubmit}>
        <p className="eyebrow">Universidade</p>
        <h1>UniSistema</h1>
        <p className="muted">Acesse com admin, professor ou aluno.</p>
        {error && <div className="alert">{error}</div>}
        <label>
          E-mail
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
        </label>
        <label>
          Senha
          <input value={senha} onChange={(e) => setSenha(e.target.value)} type="password" required />
        </label>
        <button className="btn primary" disabled={busy} type="submit">
          {busy ? 'Entrando...' : 'Entrar'}
        </button>
        <div className="hints">
          <p><code>admin@uni.local</code> / admin123</p>
          <p><code>prof@uni.local</code> / prof123</p>
          <p><code>aluno@uni.local</code> / aluno123</p>
        </div>
      </form>
    </div>
  )
}
