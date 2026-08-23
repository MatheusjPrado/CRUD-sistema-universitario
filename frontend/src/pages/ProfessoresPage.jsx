import { useEffect, useState } from 'react'
import { api } from '../api'

const empty = { nome: '', email: '', senha: '', departamento: '' }

export default function ProfessoresPage() {
  const [items, setItems] = useState([])
  const [form, setForm] = useState(empty)
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState('')

  async function load() {
    setItems(await api('/api/professores'))
  }

  useEffect(() => { load().catch((e) => setError(e.message)) }, [])

  async function onSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      if (editingId) {
        await api(`/api/professores/${editingId}`, { method: 'PUT', body: JSON.stringify(form) })
      } else {
        await api('/api/professores', { method: 'POST', body: JSON.stringify(form) })
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
    setForm({ nome: item.nome, email: item.email, senha: '', departamento: item.departamento || '' })
  }

  async function remove(id) {
    if (!confirm('Remover professor?')) return
    await api(`/api/professores/${id}`, { method: 'DELETE' })
    await load()
  }

  return (
    <section>
      <header className="page-header">
        <div>
          <p className="eyebrow">CRUD</p>
          <h1>Professores</h1>
        </div>
      </header>
      {error && <div className="alert">{error}</div>}
      <form className="panel form-grid" onSubmit={onSubmit}>
        <input placeholder="Nome" value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} required />
        <input placeholder="E-mail" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
        <input placeholder="Senha (opcional na edição)" type="password" value={form.senha} onChange={(e) => setForm({ ...form, senha: e.target.value })} />
        <input placeholder="Departamento" value={form.departamento} onChange={(e) => setForm({ ...form, departamento: e.target.value })} />
        <div className="row">
          <button className="btn primary" type="submit">{editingId ? 'Salvar' : 'Cadastrar'}</button>
          {editingId && <button type="button" className="btn ghost" onClick={() => { setEditingId(null); setForm(empty) }}>Cancelar</button>}
        </div>
      </form>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Nome</th><th>E-mail</th><th>Departamento</th><th></th></tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.nome}</td>
                <td>{item.email}</td>
                <td>{item.departamento}</td>
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
