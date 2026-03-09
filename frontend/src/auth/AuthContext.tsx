import { createContext, useContext, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { apiRequest, ApiError } from '../lib/api';
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '../lib/storage';
import type { LoginResponse } from '../types';

type AuthContextValue = {
  isAuthenticated: boolean;
  login: (account: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(Boolean(getAccessToken()));

  const value = useMemo<AuthContextValue>(() => ({
    isAuthenticated,
    async login(account: string, password: string) {
      const data = await apiRequest<LoginResponse>('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ account, password })
      });
      setTokens(data.accessToken, data.refreshToken);
      setIsAuthenticated(true);
    },
    async register(username: string, email: string, password: string) {
      await apiRequest<void>('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify({ username, email, password })
      });
    },
    async logout() {
      const refreshToken = getRefreshToken();
      if (refreshToken) {
        try {
          await apiRequest<void>('/api/auth/logout', {
            method: 'POST',
            body: JSON.stringify({ refreshToken })
          });
        } catch (error) {
          if (!(error instanceof ApiError)) {
            throw error;
          }
        }
      }
      clearTokens();
      setIsAuthenticated(false);
    }
  }), [isAuthenticated]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return ctx;
}
