import { useEffect, useState } from 'react'
import { api } from '../api'

const empty = { materiaId: '', professorId: '', semestre: '1', ano: new Date().getFullYear() }

export default function TurmasPage() {
  const [items, setItems] = useState([])
  const [materias, setMaterias] = useState([])
  const [professores, setProfessores] = useState([])
  const [form, setForm] = useState(empty)
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState('')

  async function load() {
    const [turmas, ms, ps] = await Promise.all([
      api('/api/turmas'),
      api('/api/materias'),
      api('/api/professores'),
    ])
    setItems(turmas)
    setMaterias(ms)
    setProfessores(ps)
  }

  useEffect(() => { load().catch((e) => setError(e.message)) }, [])

  async function onSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      const payload = {
        materiaId: Number(form.materiaId),
        professorId: Number(form.professorId),
        semestre: form.semestre,
        ano: Number(form.ano),
      }
      if (editingId) {
        await api(`/api/turmas/${editingId}`, { method: 'PUT', body: JSON.stringify(payload) })
      } else {
        await api('/api/turmas', { method: 'POST', body: JSON.stringify(payload) })
      }
      setForm(empty)
      setEditingId(null)
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  function edit(item) {
    setEditingId(item.id)
    setForm({
      materiaId: String(item.materiaId),
      professorId: String(item.professorId),
      semestre: item.semestre,
      ano: item.ano,
    })
  }

  async function remove(id) {
    if (!confirm('Remover turma?')) return
    await api(`/api/turmas/${id}`, { method: 'DELETE' })
    await load()
  }

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">CRUD</p>
          <h1>Turmas</h1>
        </div>
      </header>
      {error && <div className="alert">{error}</div>}
      <form className="panel form-grid" onSubmit={onSubmit}>
        <select value={form.materiaId} onChange={(e) => setForm({ ...form, materiaId: e.target.value })} required>
          <option value="">Matéria</option>
          {materias.map((m) => (
            <option key={m.id} value={m.id}>{m.cursoNome} — {m.codigo} {m.nome}</option>
          ))}
        </select>
        <select value={form.professorId} onChange={(e) => setForm({ ...form, professorId: e.target.value })} required>
          <option value="">Professor</option>
          {professores.map((p) => <option key={p.id} value={p.id}>{p.nome}</option>)}
        </select>
        <input placeholder="Semestre" value={form.semestre} onChange={(e) => setForm({ ...form, semestre: e.target.value })} required />
        <input placeholder="Ano" type="number" value={form.ano} onChange={(e) => setForm({ ...form, ano: e.target.value })} required />
        <div className="row">
          <button className="btn primary" type="submit">{editingId ? 'Salvar' : 'Cadastrar'}</button>
          {editingId && <button type="button" className="btn ghost" onClick={() => { setEditingId(null); setForm(empty) }}>Cancelar</button>}
        </div>
      </form>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Matéria</th><th>Curso</th><th>Professor</th><th>Semestre</th><th>Ano</th><th></th></tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.materiaCodigo} — {item.materiaNome}</td>
                <td>{item.cursoNome}</td>
                <td>{item.professorNome}</td>
                <td>{item.semestre}</td>
                <td>{item.ano}</td>
                <td className="actions">
                  <button type="button" className="btn ghost" onClick={() => edit(item)}>Editar</button>
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
