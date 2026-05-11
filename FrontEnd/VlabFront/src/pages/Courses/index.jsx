// Tela de listagem e cadastro de cursos com busca e navegacao para detalhes.
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createCourse, listCourses } from '../../services/courseApi'
import './style.css'

export default function Courses() {
  const navigate = useNavigate()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [q, setQ] = useState('')

  // form "criar curso"
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [saving, setSaving] = useState(false)

  function goHome(){
    navigate('/home')
  }

  async function load() {
    setLoading(true)
    setError(null)
    try {
      const data = await listCourses()
      setItems(Array.isArray(data) ? data : [])
    } catch (e) {
      setError(e?.response?.data?.message ?? e?.message ?? 'Falha ao carregar cursos')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const filtered = useMemo(() => {
    const s = q.trim().toLowerCase()
    if (!s) return items
    return items.filter((c) => (c?.name ?? '').toLowerCase().includes(s))
  }, [items, q])

  async function onCreate(e) {
    e.preventDefault()
    setSaving(true)
    setError(null)
    try {
      await createCourse({
        name,
        description: description || null,
        startDate,
        endDate,
      })
      setName('')
      setDescription('')
      setStartDate('')
      setEndDate('')
      await load()
    } catch (e2) {
      setError(e2?.response?.data?.message ?? e2?.message ?? 'Falha ao criar curso')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="courses">
      <header className="courses__header">
      <div className="courses__headerLeft">
          <button type="button" className="courses__backBtn" onClick={goHome}>
            Voltar
          </button>
          <h1>Cursos</h1>
        </div>

        <input
          className="courses__search"
          placeholder="Buscar por nome..."
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
      </header>

      <section className="courses__panel">
        <h2>Novo curso</h2>
        <form className="courses__form" onSubmit={onCreate}>
          <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Nome" required minLength={3} />
          <input value={startDate} onChange={(e) => setStartDate(e.target.value)} type="date" required />
          <input value={endDate} onChange={(e) => setEndDate(e.target.value)} type="date" required />
          <textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Descrição (opcional)" />
          <button type="submit" disabled={saving}>
            {saving ? 'Salvando...' : 'Criar'}
          </button>
        </form>

        {error && <p className="courses__error">{error}</p>}
      </section>

      <main className="courses__list">
        {loading ? (
          <p>Carregando...</p>
        ) : (
          filtered.map((c) => (
            <div
                key={c.idCourse ?? c.id}
                className="courseCard"
                onClick={() => navigate(`/courses/${c.idCourse ?? c.id}`)}
                role="button"
                tabIndex={0}
            >
                <div className="courseCard__title">{c.name}</div>
                <div className="courseCard__meta">
                {c.startDate} → {c.endDate}
                </div>
            </div>
          ))
        )}
      </main>
    </div>
  )
}