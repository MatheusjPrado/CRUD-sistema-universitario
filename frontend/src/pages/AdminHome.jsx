import { Link } from 'react-router-dom'

export default function AdminHome() {
  const cards = [
    ['Alunos', '/admin/alunos', 'Cadastro, busca e edição de alunos'],
    ['Professores', '/admin/professores', 'Gestão do corpo docente'],
    ['Cursos', '/admin/cursos', '3 cursos e criação de matérias'],
    ['Turmas', '/admin/turmas', 'Ofertas de matérias por semestre'],
    ['Matrícula curso', '/admin/matricula-curso', 'Vincular aluno a um curso'],
    ['Matrícula matéria', '/admin/matricula-materia', 'Matricular aluno em matérias do curso'],
  ]

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">Admin</p>
          <h1>Painel administrativo</h1>
        </div>
      </header>
      <div className="grid">
        {cards.map(([title, to, desc]) => (
          <Link key={to} to={to} className="tile">
            <h2>{title}</h2>
            <p>{desc}</p>
          </Link>
        ))}
      </div>
    </section>
  )
}
