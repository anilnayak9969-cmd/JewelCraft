import { createContext, useCallback, useEffect, useMemo, useState } from 'react'
import { tokenStore } from '../services/api'
import authService from '../services/authService'

export const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  // Only show a loading state if there is a saved token to check.
  const [loading, setLoading] = useState(() => Boolean(tokenStore.get()))

  useEffect(() => {
    if (!tokenStore.get()) return
    authService
      .me()
      .then(setUser)
      .catch(() => {
        tokenStore.clear()
        setUser(null)
      })
      .finally(() => setLoading(false))
  }, [])

  // api.js fires this when the server rejects the token (expired / revoked).
  useEffect(() => {
    const onLogout = () => setUser(null)
    window.addEventListener('jc:logout', onLogout)
    return () => window.removeEventListener('jc:logout', onLogout)
  }, [])

  const startSession = useCallback((res) => {
    tokenStore.set(res.token)
    setUser(res.user)
    return res.user
  }, [])

  const login = useCallback((credentials) => authService.login(credentials).then(startSession), [startSession])

  const logout = useCallback(() => {
    authService.logout()
    tokenStore.clear()
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({
      user,
      loading,
      isAuthenticated: Boolean(user),
      login,
      // Used after email verification, which returns a token just like login.
      startSession,
      logout,
      setUser,
      hasRole: (...roles) => Boolean(user) && roles.includes(user.role),
    }),
    [user, loading, login, startSession, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
