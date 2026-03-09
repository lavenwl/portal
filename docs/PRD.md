# PRD - RBAC Platform

## 1. Scope

This project includes:

1. Registration and login
2. User management
3. Permission control
4. Role control
5. Login logs
6. Operation logs

## 2. Roles

1. Super Admin
2. Admin
3. Normal User

## 3. Core User Stories

1. As a visitor, I can register and log in.
2. As an admin, I can manage users.
3. As an admin, I can manage roles and assign permissions.
4. As an admin, I can view login logs and operation logs.

## 4. Non-Functional Baseline

1. Backend: Spring Boot 3
2. Frontend: React + TypeScript + Vite (placeholder, can be changed by architect)
3. Security: JWT + RBAC
4. Database: MySQL (to be integrated in next steps)

## 5. Acceptance Baseline

1. All 6 modules are available end-to-end.
2. Permission checks are enforced on backend APIs.
3. Login and operation logs are queryable.
