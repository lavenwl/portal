# Release Notes v0.1.0

Date: 2026-03-09

## Features

1. Backend auth module with register/login/refresh/logout (JWT + refresh token persistence).
2. User management APIs with CRUD baseline, status control, and logical delete.
3. RBAC model implementation (user-role, role-permission, method-level authorization).
4. Login log persistence and query API.
5. Operation log AOP collection and query API.
6. Frontend admin console pages for auth, users, roles, permissions, and logs.

## Engineering and Delivery

1. OpenAPI contract and project docs completed.
2. CI workflow added for backend tests and frontend build.
3. Bootstrap seed data added for first-run admin and permissions.
4. Smoke E2E script and integration checklist added.

## Breaking/Important Notes

1. CORS is configured for local frontend origins (`http://127.0.0.1:5173`, `http://localhost:5173`).
2. Default bootstrap admin credentials are for initial setup only and must be rotated in deployment.
3. If bootstrap is not desired in production, set `BOOTSTRAP_ENABLED=false`.
