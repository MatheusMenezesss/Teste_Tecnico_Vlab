// Tela de autenticacao que cobre login e cadastro rapido de usuarios.
import { useEffect, useState } from 'react'
import api from '../../services/api'
import './style.css'
import Vite from '../../assets/react.svg'
import Logo from '../../assets/logo_escura.png'
import { setAuth } from '../../services/authStorage'
import { useNavigate } from 'react-router-dom'

function Login() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showRegister, setShowRegister] = useState(false)

  const [users, setUsers] = useState([])
  const [error, setError] = useState(null)

  // States do cadastro (estavam faltando)
  const [registerName, setRegisterName] = useState('')
  const [registerEmail, setRegisterEmail] = useState('')
  const [registerPassword, setRegisterPassword] = useState('')
  const [registerError, setRegisterError] = useState(null)
  const [registerLoading, setRegisterLoading] = useState(false)

  // asincrono, sem o await, para evitar erros de renderização
  async function fetchUsers() {
    const res = await api.get('/usuarios')
    setUsers(res.data)
  }

  async function handleLogin() {
    /**
     * Limpa o erro antes de tentar logar, para evitar mensagens antigas
     * 
     */
    setError(null)
    try {
      const res = await api.post('/auth/login', { email, senha: password })
      setAuth(res.data)
      await fetchUsers()
      navigate('/home', { replace: true })
      

      return
    } catch (e) {
      const status = e?.response?.status
      const url = e?.config?.url
      const msg = e?.response?.data?.message ?? e?.message

      setError(`Erro ${status ?? ''} em ${url ?? 'requisição'}: ${msg ?? 'Falha na requisição'}`)
      // eslint-disable-next-line no-console
      console.error('Login error:', { status, url, data: e?.response?.data, headers: e?.response?.headers })
    }
    
  }

  function openRegister() {
    setRegisterError(null)
    setRegisterName('')
    setRegisterEmail('')
    setRegisterPassword('')
    setShowRegister(true)
  }

  function closeRegister() {
    setShowRegister(false)
    setRegisterError(null)
  }

  async function handleRegister() {
    setRegisterError(null)

    if (!registerName.trim() || !registerEmail.trim() || !registerPassword) {
      setRegisterError('Preencha nome, email e senha.')
      return
    }
    if (registerPassword.length < 6) {
      setRegisterError('A senha deve ter pelo menos 6 caracteres.')
      return
    }

    setRegisterLoading(true)
    try {
      const payload = {
        nome: registerName,
        email: registerEmail,
        senha: registerPassword,
      }

      const res = await api.post('/auth/register', payload)

      const token = res.data?.token
      if (token) {
        localStorage.setItem('access_token', token)
        setAuth(res.data)
        await fetchUsers()
      }

      closeRegister()
      navigate('/home', { replace: true })
      return
    } catch (e) {
      const status = e?.response?.status
      const url = e?.config?.url
      const msg = e?.response?.data?.message ?? e?.message
      setRegisterError(`Erro ${status ?? ''} em ${url ?? 'requisição'}: ${msg ?? 'Falha no cadastro'}`)
      // eslint-disable-next-line no-console
      console.error('Register error:', { status, url, data: e?.response?.data })
      return
    } finally {
      setRegisterLoading(false)
    }
  }

  useEffect(() => {
    if (localStorage.getItem('access_token')) {
      fetchUsers().catch(() => {})
    }
  }, [])

  return (
    <div className="container">
      <div className="left-panel">
        <img src={Logo} alt="Logo" className="logo" />
      </div>

      <div className="right-panel">
        <form className="login-form">
          <h1>Welcome to VLab</h1>

          <input
            placeholder="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <input
            placeholder="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button type="button" onClick={handleLogin} className="login-button">
            Login
          </button>

          <button type="button" className="register-button" onClick={openRegister}>
            Create Account
          </button>

          {error && <p className="error">{error}</p>}
        </form>

        {showRegister && (
          <div className="modal-overlay" onClick={closeRegister}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
              <h2>Create Account</h2>

              <input
                type="text"
                placeholder="Name"
                value={registerName}
                onChange={(e) => setRegisterName(e.target.value)}
              />

              <input
                type="email"
                placeholder="Email"
                value={registerEmail}
                onChange={(e) => setRegisterEmail(e.target.value)}
              />

              <input
                type="password"
                placeholder="Password"
                value={registerPassword}
                onChange={(e) => setRegisterPassword(e.target.value)}
              />

              {registerError && <p className="error">{registerError}</p>}

              <div className="modal-buttons">
                <button
                  type="button"
                  className="save-button"
                  onClick={handleRegister}
                  disabled={registerLoading}  // corrigido
                >
                  {registerLoading ? 'Registering...' : 'Register'}
                </button>

                <button type="button" className="cancel-button" onClick={closeRegister}>
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default Login