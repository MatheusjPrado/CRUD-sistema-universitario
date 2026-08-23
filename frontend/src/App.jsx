import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './auth'
import LoginPage from './pages/LoginPage'
import AdminHome from './pages/AdminHome'
import AlunosPage from './pages/AlunosPage'
import ProfessoresPage from './pages/ProfessoresPage'
import CursosPage from './pages/CursosPage'
import TurmasPage from './pages/TurmasPage'
import MatriculaCursoPage from './pages/MatriculaCursoPage'
import MatriculaMateriaPage from './pages/MatriculaMateriaPage'
import ProfessorHome from './pages/ProfessorHome'
import AlunoHome from './pages/AlunoHome'
import Layout from './components/Layout'

function PrivateRoute({ children, roles }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="center">Carregando...</div>
  if (!user) return <Navigate to="/login" replace />
  if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />
  return children
}

function HomeRedirect() {
  const { user, loading } = useAuth()
  if (loading) return <div className="center">Carregando...</div>
  if (!user) return <Navigate to="/login" replace />
  if (user.role === 'ADMIN') return <Navigate to="/admin" replace />
  if (user.role === 'PROFESSOR') return <Navigate to="/professor" replace />
  return <Navigate to="/aluno" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<HomeRedirect />} />

      <Route path="/admin" element={<PrivateRoute roles={['ADMIN']}><Layout /></PrivateRoute>}>
        <Route index element={<AdminHome />} />
        <Route path="alunos" element={<AlunosPage />} />
        <Route path="professores" element={<ProfessoresPage />} />
        <Route path="cursos" element={<CursosPage />} />
        <Route path="turmas" element={<TurmasPage />} />
        <Route path="matricula-curso" element={<MatriculaCursoPage />} />
        <Route path="matricula-materia" element={<MatriculaMateriaPage />} />
      </Route>

      <Route path="/professor" element={<PrivateRoute roles={['PROFESSOR']}><Layout /></PrivateRoute>}>
        <Route index element={<ProfessorHome />} />
      </Route>

      <Route path="/aluno" element={<PrivateRoute roles={['ALUNO']}><Layout /></PrivateRoute>}>
        <Route index element={<AlunoHome />} />
      </Route>
    </Routes>
  )
}
