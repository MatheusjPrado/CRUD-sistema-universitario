import { useEffect, useState } from 'react'
import { api } from '../api'
import { useAuth } from '../auth'

export default function AlunoHome() {
  const { user } = useAuth()
  const [aluno, setAluno] = useState(null)
  const [materias, setMaterias] = useState([])
  const [matriculas, setMatriculas] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    if (!user?.alunoId) return
    Promise.all([
      api(`/api/alunos/${user.alunoId}`),
      api(`/api/matriculas?alunoId=${user.alunoId}`),
    ])
      .then(async ([a, mats]) => {
        setAluno(a)
        setMatriculas(mats)
        if (a.cursoId) {
          setMaterias(await api(`/api/materias?cursoId=${a.cursoId}`))
        } else {
          setMaterias([])
        }
      })
      .catch((e) => setError(e.message))
  }, [user])

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">Aluno</p>
          <h1>Minhas matérias</h1>
          {aluno && (
            <p className="muted">
              {aluno.cursoNome
                ? `Curso: ${aluno.cursoNome} · Matrícula ${aluno.matricula}`
                : `Sem curso · Matrícula ${aluno.matricula}`}
            </p>
          )}
        </div>
      </header>
      {error && <div className="alert">{error}</div>}

      {!aluno?.cursoId && (
        <div className="alert">Você ainda não está matriculado em um curso.</div>
      )}

      {aluno?.cursoId && (
        <>
          <h2 style={{ margin: '0 0 0.75rem', fontSize: '1.2rem' }}>Grade do curso</h2>
          <div className="table-wrap" style={{ marginBottom: '1.5rem' }}>
            <table>
              <thead>
                <tr><th>Código</th><th>Matéria</th><th>Carga</th><th>Semestre</th></tr>
              </thead>
              <tbody>
                {materias.map((m) => (
                  <tr key={m.id}>
                    <td>{m.codigo}</td>
                    <td>{m.nome}</td>
                    <td>{m.cargaHoraria}h</td>
                    <td>{m.semestreSugerido}º</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      <h2 style={{ margin: '0 0 0.75rem', fontSize: '1.2rem' }}>Turmas matriculadas</h2>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Matéria</th><th>Período</th><th>Nota</th></tr>
          </thead>
          <tbody>
            {matriculas.map((item) => (
              <tr key={item.id}>
                <td>{item.materiaNome}</td>
                <td>{item.ano}/{item.semestre}</td>
                <td>{item.nota ?? '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}
