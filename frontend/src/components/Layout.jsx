import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth'

export default function Layout() {
  const { user, logout } = useAuth()

  const links = user?.role === 'ADMIN'
    ? [
        ['/admin', 'Início'],
        ['/admin/alunos', 'Alunos'],
        ['/admin/professores', 'Professores'],
        ['/admin/cursos', 'Cursos'],
        ['/admin/turmas', 'Turmas'],
        ['/admin/matricula-curso', 'Matrícula curso'],
        ['/admin/matricula-materia', 'Matrícula matéria'],
      ]
    : user?.role === 'PROFESSOR'
      ? [['/professor', 'Minhas turmas']]
      : [['/aluno', 'Minhas matérias']]

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">Uni</span>
          <strong>Sistema</strong>
        </div>
        <nav>
          {links.map(([to, label]) => (
            <NavLink key={to} to={to} end={to === '/' || to === '/admin' || to === '/professor' || to === '/aluno'}>
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-foot">
          <div>
            <strong>{user?.nome}</strong>
            <small>{user?.role}</small>
          </div>
          <button type="button" className="btn ghost" onClick={logout}>Sair</button>
        </div>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  )
}
