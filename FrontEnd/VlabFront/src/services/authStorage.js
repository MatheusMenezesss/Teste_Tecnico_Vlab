// Gerencia o token e os dados basicos do usuario no localStorage.
export function setAuth(authResponse) {
    const token = authResponse?.token ?? authResponse?.accessToken
    if (token) localStorage.setItem('access_token', token)
  
    const user = {
      email: authResponse?.email ?? null,
      idUsuario: authResponse?.idUsuario ?? null,
    }
    localStorage.setItem('auth_user', JSON.stringify(user))
  }
  
  export function getToken() {
    return localStorage.getItem('access_token')
  }
  
  export function getUser() {
    try {
      const raw = localStorage.getItem('auth_user')
      return raw ? JSON.parse(raw) : null
    } catch {
      return null
    }
  }
  
  export function clearAuth() {
    localStorage.removeItem('access_token')
    localStorage.removeItem('auth_user')
  }