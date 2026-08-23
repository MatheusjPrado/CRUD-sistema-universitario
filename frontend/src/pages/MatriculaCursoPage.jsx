import { useEffect, useState } from 'react'
import { api } from '../api'

export default function MatriculaCursoPage() {
  const [cursos, setCursos] = useState([])
  const [cursoId, setCursoId] = useState('')
  const [semCurso, setSemCurso] = useState([])
  const [alunosCurso, setAlunosCurso] = useState([])
  const [selecionado, setSelecionado] = useState('')
  const [error, setError] = useState('')

  async function trocarCurso(id) {
    setCursoId(id)
    setError('')
    const [sem, com] = await Promise.all([
      api('/api/alunos?semCurso=true'),
      api(`/api/alunos?cursoId=${id}`),
    ])
    setSemCurso(sem)
    setAlunosCurso(com)
  }

  useEffect(() => {
    api('/api/cursos')
      .then(async (cs) => {
        setCursos(cs)
        if (cs[0]) await trocarCurso(String(cs[0].id))
      })
      .catch((e) => setError(e.message))
  }, [])

  async function matricular() {
    if (!selecionado || !cursoId) return
    setError('')
    try {
      await api(`/api/alunos/${selecionado}/matricular-curso`, {
        method: 'POST',
        body: JSON.stringify({ cursoId: Number(cursoId) }),
      })
      setSelecionado('')
      await trocarCurso(cursoId)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">Matrícula</p>
          <h1>Matrícula de curso</h1>
          <p className="muted">Veja quem está em cada curso e matricule quem ainda não tem curso.</p>
        </div>
      </header>
      {error && <div className="alert">{error}</div>}

      <div className="grid" style={{ marginBottom: '1rem' }}>
        {cursos.map((c) => (
          <button
            key={c.id}
            type="button"
            className="tile"
            onClick={() => trocarCurso(String(c.id)).catch((e) => setError(e.message))}
            style={String(c.id) === cursoId ? { outline: '2px solid var(--accent)' } : undefined}
          >
            <h2>{c.nome}</h2>
            <p>{c.codigo}</p>
          </button>
        ))}
      </div>

      <div className="panel form-grid">
        <select value={selecionado} onChange={(e) => setSelecionado(e.target.value)}>
          <option value="">Aluno sem curso</option>
          {semCurso.map((a) => (
            <option key={a.id} value={a.id}>{a.nome} ({a.matricula})</option>
          ))}
        </select>
        <button className="btn primary" type="button" onClick={matricular} disabled={!selecionado}>
          Matricular no curso selecionado
        </button>
      </div>

      <h2 style={{ margin: '0 0 0.75rem', fontSize: '1.2rem' }}>
        Alunos em {cursos.find((c) => String(c.id) === cursoId)?.nome || '…'}
      </h2>
      <div className="table-wrap" style={{ marginBottom: '1.5rem' }}>
        <table>
          <thead>
            <tr><th>Nome</th><th>Matrícula</th><th>E-mail</th></tr>
          </thead>
          <tbody>
            {alunosCurso.map((a) => (
              <tr key={a.id}>
                <td>{a.nome}</td>
                <td>{a.matricula}</td>
                <td>{a.email}</td>
              </tr>
            ))}
            {!alunosCurso.length && (
              <tr><td colSpan={3}>Nenhum aluno neste curso.</td></tr>
            )}
          </tbody>
        </table>
      </div>

      <h2 style={{ margin: '0 0 0.75rem', fontSize: '1.2rem' }}>Sem curso</h2>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Nome</th><th>Matrícula</th><th>E-mail</th></tr>
          </thead>
          <tbody>
            {semCurso.map((a) => (
              <tr key={a.id}>
                <td>{a.nome}</td>
                <td>{a.matricula}</td>
                <td>{a.email}</td>
              </tr>
            ))}
            {!semCurso.length && (
              <tr><td colSpan={3}>Todos os alunos já têm curso.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </section>
  )
}
