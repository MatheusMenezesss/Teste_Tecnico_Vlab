import { useEffect, useMemo, useState } from 'react'
import './style.css'
import { getUser, clearAuth } from '../../services/authStorage'
import { useNavigate } from 'react-router-dom'
import { listCourses } from '../../services/courseApi'

function Home() {
  const navigate = useNavigate()
  const user = getUser()

  // cursos vindos do backend
  const [courses, setCourses] = useState([])
  const [loadingCourses, setLoadingCourses] = useState(true)
  const [coursesError, setCoursesError] = useState(null)

  // modal (agora mostra curso)
  const [selected, setSelected] = useState(null)

  async function loadCourses() {
    setLoadingCourses(true)
    setCoursesError(null)
    try {
      const data = await listCourses()
      setCourses(Array.isArray(data) ? data : [])
    } catch (e) {
      setCoursesError(e?.response?.data?.message ?? e?.message ?? 'Falha ao carregar cursos')
    } finally {
      setLoadingCourses(false)
    }
  }

  useEffect(() => {
    loadCourses()
  }, [])

  function openCourse(course) {
    setSelected(course)
  }

  function closeModal() {
    setSelected(null)
  }

  function logout() {
    clearAuth()
    navigate('/login', { replace: true })
  }

  function goToCourses() {
    navigate('/courses')
  }

  // só para manter o mesmo visual dos "pills" (mapeia datas para texto curto)
  const courseDurationLabel = useMemo(() => {
    return (c) => {
      if (!c?.startDate || !c?.endDate) return '—'
      return `${c.startDate} → ${c.endDate}`
    }
  }, [])

  return (
    <div className="home">
      <header className="home__header">
        <div className="home__headerText">
          <h1 className="home__title">VLab — Home</h1>
          <p className="home__welcome">{user?.email ? `Logado como ${user.email}` : 'Logado'}</p>
          <p className="home__subtitle">
            Plataforma educacional: escolha um curso para ver a descrição.
          </p>

          <div className="home__actions">
            <button type="button" className="home__coursesBtn" onClick={goToCourses}>
              Ver cursos
            </button>
          </div>
        </div>

        <div className="home__searchWrap" aria-label="Área de busca (visual)">
          <input className="home__search" placeholder="Buscar (apenas visual)" type="search" />
        </div>
      </header>

      <main className="home__content">
        <section className="home__section">
          <h2 className="home__sectionTitle">Cursos disponíveis</h2>

          {loadingCourses ? (
            <p className="home__subtitle">Carregando cursos...</p>
          ) : coursesError ? (
            <p className="home__subtitle">{coursesError}</p>
          ) : courses.length === 0 ? (
            <p className="home__subtitle">Nenhum curso cadastrado.</p>
          ) : (
            <div className="lessonGrid">
              {courses.map((course) => (
                <button
                  key={course.idCourse ?? course.id}
                  type="button"
                  className="lessonCard"
                  onClick={() => openCourse(course)}
                >
                  <div className="lessonCard__top">
                    <span className="pill pill--level">Curso</span>
                    <span className="pill pill--time">{courseDurationLabel(course)}</span>
                  </div>

                  <h3 className="lessonCard__title">{course.name}</h3>

                  <p className="lessonCard__hint">Clique para ver a descrição</p>
                </button>
              ))}
            </div>
          )}
        </section>
      </main>

      <div className="home__bottomBar">
        <button type="button" className="home__logoutBtn" onClick={logout}>
          Logout
        </button>
      </div>

      {selected && (
        <div
          className="modalOverlay"
          role="dialog"
          aria-modal="true"
          aria-label={`Descrição do curso: ${selected.name}`}
          onClick={closeModal}
        >
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal__header">
              <div className="modal__titleWrap">
                <h3 className="modal__title">{selected.name}</h3>
                <p className="modal__meta">
                  <span className="pill pill--time">{courseDurationLabel(selected)}</span>
                </p>
              </div>

              <button
                type="button"
                className="modal__close"
                onClick={closeModal}
                aria-label="Fechar"
                title="Fechar"
              >
                ×
              </button>
            </div>

            <div className="modal__body">
              <p className="modal__description">
                {selected.description ? selected.description : 'Sem descrição.'}
              </p>
            </div>

            <div className="modal__footer">
              <button
                type="button"
                className="btn btn--primary"
                onClick={() => navigate(`/courses/${selected.idCourse ?? selected.id}`)}
              >
                Abrir curso
              </button>
              <button type="button" className="btn btn--ghost" onClick={closeModal}>
                Voltar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Home