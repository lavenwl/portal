# Architecture

## 1. High-Level

1. Frontend (SPA) calls backend APIs.
2. Backend validates JWT and permissions.
3. Backend stores business data and logs in MySQL.

## 2. Backend Layers

1. Controller
2. Service
3. Repository
4. Security
5. Audit/Logging

## 3. Permission Model

RBAC:

1. User-Role mapping
2. Role-Permission mapping
3. Permission code enforced in backend

## 4. Logging

1. Login log: authentication success/failure events
2. Operation log: business action events via AOP + annotation
