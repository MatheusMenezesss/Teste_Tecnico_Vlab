// Protege rotas privadas e redireciona para login quando nao ha token salvo.
import { Navigate, Outlet } from 'react-router-dom'
import { getToken } from '../services/authStorage'

export default function PrivateRoute() {
  const token = getToken()
  return token ? <Outlet /> : <Navigate to="/login" replace />
}

/**
 * PrivateRoute é um componente de rota protegida que verifica se o usuário está autenticado (verificando a presença de um token).
 * Se o token estiver presente, ele renderiza os componentes filhos (Outlet), permitindo o acesso à rota protegida.
 * Caso contrário, ele redireciona o usuário para a página de login usando o componente Navigate do React Router.
 */