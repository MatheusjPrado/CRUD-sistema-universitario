import { useEffect, useState } from 'react'
import { api } from '../api'

const emptyMateria = { cursoId: '', codigo: '', nome: '', cargaHoraria: 60, semestreSugerido: 1 }

export default function CursosPage() {
  const [cursos, setCursos] = useState([])
  const [materias, setMaterias] = useState([])
  const [cursoId, setCursoId] = useState('')
  const [form, setForm] = useState(emptyMateria)
  const [error, setError] = useState('')

  async function load(selectedId) {
    const [cs, ms] = await Promise.all([api('/api/cursos'), api('/api/materias')])
    setCursos(cs)
    setMaterias(ms)
    const id = selectedId || (cs[0] ? String(cs[0].id) : '')
    setCursoId(id)
    setForm((prev) => ({ ...prev, cursoId: id }))
  }

  useEffect(() => {
    load().catch((e) => setError(e.message))
  }, [])

  const filtradas = materias.filter((m) => String(m.cursoId) === cursoId)

  async function criarMateria(e) {
    e.preventDefault()
    setError('')
    try {
      await api('/api/materias', {
        method: 'POST',
        body: JSON.stringify({
          cursoId: Number(form.cursoId || cursoId),
          codigo: form.codigo,
          nome: form.nome,
          cargaHoraria: Number(form.cargaHoraria),
          semestreSugerido: Number(form.semestreSugerido),
        }),
      })
      setForm({ ...emptyMateria, cursoId })
      await load(cursoId)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">Grade</p>
          <h1>Cursos e matérias</h1>
        </div>
      </header>
      {error && <div className="alert">{error}</div>}

      <div className="grid" style={{ marginBottom: '1rem' }}>
        {cursos.map((c) => (
          <button
            key={c.id}
            type="button"
            className="tile"
            onClick={() => {
              setCursoId(String(c.id))
              setForm((prev) => ({ ...prev, cursoId: String(c.id) }))
            }}
            style={String(c.id) === cursoId ? { outline: '2px solid var(--accent)' } : undefined}
          >
            <h2>{c.nome}</h2>
            <p>{c.codigo}</p>
          </button>
        ))}
      </div>

      <form className="panel form-grid" onSubmit={criarMateria}>
        <h2 style={{ gridColumn: '1 / -1', margin: 0, fontSize: '1.1rem' }}>Nova matéria</h2>
        <select
          value={form.cursoId || cursoId}
          onChange={(e) => setForm({ ...form, cursoId: e.target.value })}
          required
        >
          <option value="">Curso</option>
          {cursos.map((c) => (
            <option key={c.id} value={c.id}>{c.nome}</option>
          ))}
        </select>
        <input
          placeholder="Código (ex: CALC1)"
          value={form.codigo}
          onChange={(e) => setForm({ ...form, codigo: e.target.value })}
          required
        />
        <input
          placeholder="Nome da matéria"
          value={form.nome}
          onChange={(e) => setForm({ ...form, nome: e.target.value })}
          required
        />
        <input
          type="number"
          placeholder="Carga horária"
          value={form.cargaHoraria}
          onChange={(e) => setForm({ ...form, cargaHoraria: e.target.value })}
        />
        <input
          type="number"
          placeholder="Semestre"
          value={form.semestreSugerido}
          onChange={(e) => setForm({ ...form, semestreSugerido: e.target.value })}
        />
        <button className="btn primary" type="submit">Criar matéria</button>
      </form>

      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Código</th><th>Matéria</th><th>Carga</th><th>Semestre</th></tr>
          </thead>
          <tbody>
            {filtradas.map((m) => (
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
    </section>
  )
}
