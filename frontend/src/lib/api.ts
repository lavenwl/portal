import { clearTokens, getAccessToken, getRefreshToken, setTokens } from './storage';
import type { ApiResponse, LoginResponse } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export class ApiError extends Error {
  code: number;

  constructor(code: number, message: string) {
    super(message);
    this.code = code;
  }
}

async function parseResponse<T>(res: Response): Promise<ApiResponse<T>> {
  const payload = (await res.json()) as ApiResponse<T>;
  if (!res.ok || payload.code !== 0) {
    throw new ApiError(payload.code ?? res.status, payload.message ?? 'Request failed');
  }
  return payload;
}

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    return null;
  }
  const res = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken })
  });
  if (!res.ok) {
    clearTokens();
    return null;
  }
  const payload = (await res.json()) as ApiResponse<LoginResponse>;
  if (payload.code !== 0 || !payload.data?.accessToken) {
    clearTokens();
    return null;
  }
  setTokens(payload.data.accessToken, payload.data.refreshToken);
  return payload.data.accessToken;
}

export async function apiRequest<T>(path: string, init?: RequestInit, retry = true): Promise<T> {
  const token = getAccessToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...((init?.headers as Record<string, string> | undefined) ?? {})
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE_URL}${path}`, { ...init, headers });

  if (res.status === 401 && retry) {
    const nextToken = await refreshAccessToken();
    if (nextToken) {
      return apiRequest<T>(path, init, false);
    }
  }

  const payload = await parseResponse<T>(res);
  return payload.data;
}
