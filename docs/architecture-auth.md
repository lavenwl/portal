# Authentication and Authorization Design

## 1. Authentication

1. Login with username/email + password.
2. Password uses BCrypt hash only.
3. On success, return access token (JWT) and refresh token.
4. Access token is short-lived; refresh token is longer-lived.
5. Logout invalidates refresh token (blacklist or storage delete).

## 2. Authorization (RBAC)

1. User -> multiple roles.
2. Role -> multiple permissions.
3. Permission identified by `code` (example: `user:read`, `role:assign`).
4. Backend API checks permission code.
5. Frontend uses permission list for UI visibility only.

## 3. Security Baseline

1. Login failure lock strategy (threshold + cooldown).
2. Global exception handling without leaking internals.
3. Sensitive fields masked in logs.
4. Operation logs collected through AOP annotation.

## 4. Token Flow

1. `/api/auth/login` returns `accessToken`, `refreshToken`.
2. Access protected endpoints with `Authorization: Bearer <token>`.
3. `/api/auth/refresh` rotates tokens.
4. `/api/auth/logout` revokes refresh token.

