import { useEffect, useMemo, useState } from 'react'
import { api } from '../api'

const empty = { nome: '', email: '', senha: '', cursoId: '' }

export default function AlunosPage() {
  const [items, setItems] = useState([])
  const [cursos, setCursos] = useState([])
  const [form, setForm] = useState(empty)
  const [editingId, setEditingId] = useState(null)
  const [busca, setBusca] = useState('')
  const [error, setError] = useState('')

  async function load(q = '') {
    const query = q.trim() ? `?q=${encodeURIComponent(q.trim())}` : ''
    const [alunos, opcoes] = await Promise.all([
      api(`/api/alunos${query}`),
      api('/api/cursos'),
    ])
    setItems(alunos)
    setCursos(opcoes)
  }

  useEffect(() => { load().catch((e) => setError(e.message)) }, [])

  const filtrados = useMemo(() => items, [items])

  async function onBusca(e) {
    e.preventDefault()
    setError('')
    try {
      await load(busca)
    } catch (err) {
      setError(err.message)
    }
  }

  async function onSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      const payload = {
        nome: form.nome,
        email: form.email,
        senha: form.senha,
        cursoId: form.cursoId ? Number(form.cursoId) : null,
      }
      if (editingId) {
        await api(`/api/alunos/${editingId}`, { method: 'PUT', body: JSON.stringify(payload) })
      } else {
        await api('/api/alunos', { method: 'POST', body: JSON.stringify(payload) })
      }
      setForm(empty)
      setEditingId(null)
      await load(busca)
    } catch (err) {
      setError(err.message)
    }
  }

  function edit(item) {
    setEditingId(item.id)
    setForm({
      nome: item.nome,
      email: item.email,
      senha: '',
      cursoId: item.cursoId ? String(item.cursoId) : '',
    })
  }

  async function remove(id) {
    if (!confirm('Remover aluno?')) return
    await api(`/api/alunos/${id}`, { method: 'DELETE' })
    await load(busca)
  }

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">CRUD</p>
          <h1>Alunos</h1>
        </div>
      </header>
      {error && <div className="alert">{error}</div>}

      <form className="panel form-grid" onSubmit={onBusca}>
        <input
          placeholder="Buscar por nome ou matrícula"
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
        <div className="row">
          <button className="btn primary" type="submit">Buscar</button>
          <button
            type="button"
            className="btn ghost"
            onClick={() => { setBusca(''); load().catch((e) => setError(e.message)) }}
          >
            Limpar
          </button>
        </div>
      </form>

      <form className="panel form-grid" onSubmit={onSubmit}>
        <input placeholder="Nome" value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} required />
        <input placeholder="E-mail" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
        <input placeholder="Senha (opcional na edição)" type="password" value={form.senha} onChange={(e) => setForm({ ...form, senha: e.target.value })} />
        <select value={form.cursoId} onChange={(e) => setForm({ ...form, cursoId: e.target.value })}>
          <option value="">Sem curso (matricular depois)</option>
          {cursos.map((curso) => (
            <option key={curso.id} value={curso.id}>{curso.nome}</option>
          ))}
        </select>
        {editingId ? (
          <input value={items.find((a) => a.id === editingId)?.matricula || ''} disabled readOnly />
        ) : (
          <p className="muted" style={{ margin: 0, alignSelf: 'center' }}>Matrícula gerada automaticamente</p>
        )}
        <div className="row">
          <button className="btn primary" type="submit">{editingId ? 'Salvar' : 'Cadastrar'}</button>
          {editingId && <button type="button" className="btn ghost" onClick={() => { setEditingId(null); setForm(empty) }}>Cancelar</button>}
        </div>
      </form>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Nome</th><th>E-mail</th><th>Matrícula</th><th>Curso</th><th></th></tr>
          </thead>
          <tbody>
            {filtrados.map((item) => (
              <tr key={item.id}>
                <td>{item.nome}</td>
                <td>{item.email}</td>
                <td>{item.matricula}</td>
                <td>{item.cursoNome || '—'}</td>
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
