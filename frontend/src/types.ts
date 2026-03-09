export type ApiResponse<T> = {
  code: number;
  message: string;
  data: T;
};

export type PageResponse<T> = {
  records: T[];
  total: number;
  page: number;
  size: number;
};

export type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
};

export type User = {
  id: number;
  username: string;
  email: string;
  nickname?: string;
  phone?: string;
  status: number;
  createdAt?: string;
  updatedAt?: string;
};

export type Role = {
  id: number;
  code: string;
  name: string;
  description?: string;
  status: number;
};

export type Permission = {
  id: number;
  code: string;
  name: string;
  type?: string;
  resource?: string;
  method?: string;
  description?: string;
};

export type LoginLog = {
  id: number;
  userId?: number;
  username?: string;
  eventType: string;
  success: number;
  reason?: string;
  ip?: string;
  userAgent?: string;
  loginAt: string;
};

export type OperationLog = {
  id: number;
  userId?: number;
  username?: string;
  module: string;
  action: string;
  requestPath?: string;
  requestMethod?: string;
  requestParams?: string;
  responseCode?: string;
  success: number;
  errorMessage?: string;
  durationMs?: number;
  traceId?: string;
  operatedAt: string;
};
