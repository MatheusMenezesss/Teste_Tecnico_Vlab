// Detalha um curso, exibe suas aulas e permite criar novas aulas no mesmo contexto.
import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { getCourse } from '../../services/courseApi'
import { createLesson, listLessonsByCourse } from '../../services/lessonApi'
import './style.css'

export default function CourseDetails() {
  const navigate = useNavigate()
  const { id } = useParams()

  const courseId = useMemo(() => Number(id), [id])

  const [course, setCourse] = useState(null)
  const [lessons, setLessons] = useState([])

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // form lesson
  const [title, setTitle] = useState('')
  const [status, setStatus] = useState('DRAFT')
  const [videoUrl, setVideoUrl] = useState('')
  const [saving, setSaving] = useState(false)

  function goBack() {
    navigate('/courses')
  }

  async function loadAll() {
    setLoading(true)
    setError(null)
    try {
      const [c, ls] = await Promise.all([
        getCourse(courseId),
        listLessonsByCourse(courseId),
      ])
      setCourse(c)
      setLessons(Array.isArray(ls) ? ls : [])
    } catch (e) {
      setError(e?.response?.data?.message ?? e?.message ?? 'Falha ao carregar dados do curso')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!Number.isFinite(courseId)) {
      setError('ID de curso inválido')
      setLoading(false)
      return
    }
    loadAll()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [courseId])

  async function onCreateLesson(e) {
    e.preventDefault()
    setSaving(true)
    setError(null)
    try {
      await createLesson(courseId, {
        title,
        status, // DRAFT | PUBLISHED (seu backend usa enum)
        videoUrl,
      })
      setTitle('')
      setStatus('DRAFT')
      setVideoUrl('')
      // recarrega lessons
      const ls = await listLessonsByCourse(courseId)
      setLessons(Array.isArray(ls) ? ls : [])
    } catch (e2) {
      setError(e2?.response?.data?.message ?? e2?.message ?? 'Falha ao criar aula')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="courseDetails">
      <header className="courseDetails__header">
        <button type="button" className="courseDetails__backBtn" onClick={goBack}>
          Voltar
        </button>
        <div>
          <h1 className="courseDetails__title">Detalhe do curso</h1>
          <p className="courseDetails__subtitle">Curso #{id}</p>
        </div>
      </header>

      {loading ? (
        <p className="courseDetails__muted">Carregando...</p>
      ) : (
        <>
          {error && <p className="courseDetails__error">{error}</p>}

          {course && (
            <section className="courseDetails__panel">
              <h2 className="courseDetails__panelTitle">{course.name}</h2>
              {course.description ? (
                <p className="courseDetails__desc">{course.description}</p>
              ) : (
                <p className="courseDetails__muted">Sem descrição.</p>
              )}

              <div className="courseDetails__dates">
                <span>Início: {course.startDate}</span>
                <span>Fim: {course.endDate}</span>
              </div>
            </section>
          )}

          <section className="courseDetails__grid">
            <div className="courseDetails__panel">
              <h2 className="courseDetails__panelTitle">Aulas</h2>

              <div className="lessonList" aria-label="Lista de aulas do curso">
                {lessons.length === 0 ? (
                  <p className="courseDetails__muted">Nenhuma aula cadastrada.</p>
                ) : (
                  lessons.map((l) => (
                    <div key={l.idLesson ?? l.id} className="lessonRow">
                      <div className="lessonRow__title">{l.title}</div>
                      <div className="lessonRow__meta">
                        <span className="pill">{l.status}</span>
                        {l.videoUrl ? (
                          <a className="lessonRow__link" href={l.videoUrl} target="_blank" rel="noreferrer">
                            vídeo
                          </a>
                        ) : (
                          <span className="courseDetails__muted">sem vídeo</span>
                        )}
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            <div className="courseDetails__panel">
              <h2 className="courseDetails__panelTitle">Adicionar aula</h2>

              <form className="lessonForm" onSubmit={onCreateLesson}>
                <input
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Título"
                  required
                  minLength={3}
                />

                <select value={status} onChange={(e) => setStatus(e.target.value)}>
                  <option value="DRAFT">DRAFT</option>
                  <option value="PUBLISHED">PUBLISHED</option>
                </select>

                <input
                  value={videoUrl}
                  onChange={(e) => setVideoUrl(e.target.value)}
                  placeholder="Video URL (https://...)"
                />

                <button type="submit" disabled={saving}>
                  {saving ? 'Salvando...' : 'Adicionar'}
                </button>
              </form>
            </div>
          </section>
        </>
      )}
    </div>
  )
}