import { useEffect, useMemo, useState } from 'react'
import { api } from '../api'

export default function MatriculaMateriaPage() {
  const [alunos, setAlunos] = useState([])
  const [materias, setMaterias] = useState([])
  const [matriculas, setMatriculas] = useState([])
  const [alunoId, setAlunoId] = useState('')
  const [materiaId, setMateriaId] = useState('')
  const [error, setError] = useState('')

  const aluno = useMemo(
    () => alunos.find((a) => String(a.id) === alunoId),
    [alunos, alunoId]
  )

  const materiasDoCurso = useMemo(() => {
    if (!aluno?.cursoId) return []
    return materias.filter((m) => m.cursoId === aluno.cursoId)
  }, [materias, aluno])

  async function load() {
    const [as, ms, mats] = await Promise.all([
      api('/api/alunos'),
      api('/api/materias'),
      api('/api/matriculas'),
    ])
    setAlunos(as.filter((a) => a.cursoId))
    setMaterias(ms)
    setMatriculas(mats)
  }

  useEffect(() => {
    load().catch((e) => setError(e.message))
  }, [])

  async function matricular(e) {
    e.preventDefault()
    setError('')
    try {
      await api('/api/matriculas/materia', {
        method: 'POST',
        body: JSON.stringify({
          alunoId: Number(alunoId),
          materiaId: Number(materiaId),
        }),
      })
      setMateriaId('')
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  async function remover(id) {
    if (!confirm('Remover matrícula de matéria?')) return
    await api(`/api/matriculas/${id}`, { method: 'DELETE' })
    await load()
  }

  const matriculasFiltradas = alunoId
    ? matriculas.filter((m) => String(m.alunoId) === alunoId)
    : matriculas

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">Matrícula</p>
          <h1>Matrícula de matéria</h1>
          <p className="muted">Aluno só pode se matricular em matérias do próprio curso.</p>
        </div>
      </header>
      {error && <div className="alert">{error}</div>}

      <form className="panel form-grid" onSubmit={matricular}>
        <select
          value={alunoId}
          onChange={(e) => {
            setAlunoId(e.target.value)
            setMateriaId('')
          }}
          required
        >
          <option value="">Aluno (com curso)</option>
          {alunos.map((a) => (
            <option key={a.id} value={a.id}>
              {a.nome} ({a.matricula}) — {a.cursoNome}
            </option>
          ))}
        </select>
        <select value={materiaId} onChange={(e) => setMateriaId(e.target.value)} required disabled={!alunoId}>
          <option value="">Matéria do curso</option>
          {materiasDoCurso.map((m) => (
            <option key={m.id} value={m.id}>
              {m.codigo} — {m.nome}
            </option>
          ))}
        </select>
        <button className="btn primary" type="submit" disabled={!alunoId || !materiaId}>
          Matricular
        </button>
      </form>

      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Aluno</th><th>Matrícula</th><th>Matéria</th><th>Curso</th><th>Período</th><th>Nota</th><th></th></tr>
          </thead>
          <tbody>
            {matriculasFiltradas.map((item) => (
              <tr key={item.id}>
                <td>{item.alunoNome}</td>
                <td>{item.matricula}</td>
                <td>{item.materiaNome}</td>
                <td>{item.cursoNome}</td>
                <td>{item.ano}/{item.semestre}</td>
                <td>{item.nota ?? '—'}</td>
                <td className="actions">
                  <button type="button" className="btn danger" onClick={() => remove(item.id)}>Excluir</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}
