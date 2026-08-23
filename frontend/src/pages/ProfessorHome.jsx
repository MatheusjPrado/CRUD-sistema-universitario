import { useEffect, useState } from 'react'
import { api } from '../api'
import { useAuth } from '../auth'

export default function ProfessorHome() {
  const { user } = useAuth()
  const [turmas, setTurmas] = useState([])
  const [selected, setSelected] = useState(null)
  const [matriculas, setMatriculas] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    if (!user?.professorId) return
    api(`/api/turmas?professorId=${user.professorId}`)
      .then(setTurmas)
      .catch((e) => setError(e.message))
  }, [user])

  async function openTurma(turma) {
    setSelected(turma)
    setMatriculas(await api(`/api/matriculas?turmaId=${turma.id}`))
  }

  async function salvarNota(id, nota) {
    setError('')
    try {
      await api(`/api/matriculas/${id}/nota`, {
        method: 'PATCH',
        body: JSON.stringify({ nota: Number(nota) }),
      })
      setMatriculas(await api(`/api/matriculas?turmaId=${selected.id}`))
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">Professor</p>
          <h1>Minhas turmas</h1>
        </div>
      </header>
      {error && <div className="alert">{error}</div>}
      <div className="grid">
        {turmas.map((t) => (
          <button key={t.id} type="button" className="tile" onClick={() => openTurma(t)}>
            <h2>{t.materiaNome}</h2>
            <p>{t.cursoNome} · {t.ano}/{t.semestre} · {t.materiaCodigo}</p>
          </button>
        ))}
      </div>

      {selected && (
        <div className="panel" style={{ marginTop: '1.5rem' }}>
          <h2>Alunos — {selected.materiaNome}</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Aluno</th><th>Matrícula</th><th>Nota</th><th></th></tr>
              </thead>
              <tbody>
                {matriculas.map((m) => (
                  <NotaRow key={m.id} item={m} onSave={salvarNota} />
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </section>
  )
}

function NotaRow({ item, onSave }) {
  const [nota, setNota] = useState(item.nota ?? '')
  return (
    <tr>
      <td>{item.alunoNome}</td>
      <td>{item.matricula}</td>
      <td>
        <input
          type="number"
          min="0"
          max="10"
          step="0.1"
          value={nota}
          onChange={(e) => setNota(e.target.value)}
          style={{ width: '5rem' }}
        />
      </td>
      <td>
        <button type="button" className="btn primary" onClick={() => onSave(item.id, nota)}>Salvar</button>
      </td>
    </tr>
  )
}
