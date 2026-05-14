import { createContext, ReactNode, useCallback, useEffect, useMemo, useState } from 'react';
import { TOKEN_STORAGE_KEY } from '../services/apiClient';
import type { UserSummary } from '../types/auth';

const USER_STORAGE_KEY = 'devmatch_user';

interface AuthContextValue {
  token: string | null;
  user: UserSummary | null;
  isAuthenticated: boolean;
  login: (token: string, user: UserSummary) => void;
  logout: () => void;
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function readStoredUser(): UserSummary | null {
  const raw = localStorage.getItem(USER_STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as UserSummary;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(TOKEN_STORAGE_KEY));
  const [user, setUser] = useState<UserSummary | null>(() => readStoredUser());

  const login = useCallback((newToken: string, newUser: UserSummary) => {
    localStorage.setItem(TOKEN_STORAGE_KEY, newToken);
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(newUser));
    setToken(newToken);
    setUser(newUser);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    localStorage.removeItem(USER_STORAGE_KEY);
    setToken(null);
    setUser(null);
  }, []);

  // O apiClient dispara este evento quando recebe 401.
  useEffect(() => {
    const handle = () => logout();
    window.addEventListener('devmatch:unauthorized', handle);
    return () => window.removeEventListener('devmatch:unauthorized', handle);
  }, [logout]);

  const value = useMemo<AuthContextValue>(
    () => ({
      token,
      user,
      isAuthenticated: Boolean(token),
      login,
      logout,
    }),
    [token, user, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
