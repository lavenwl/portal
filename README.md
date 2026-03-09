# RBAC Platform

Frontend and backend separated RBAC platform scaffold.

## Structure

- `backend`: Spring Boot API service
- `frontend`: Frontend app scaffold (React + TypeScript + Vite)
- `docs`: project docs (requirements, architecture, design, standards, milestones)

## Quick Start

### Backend

1. `cd backend`
2. `mvn spring-boot:run`

### Frontend

1. `cd frontend`
2. `npm install`
3. `npm run dev`

### Default Bootstrap Admin

1. Username: `admin`
2. Password: `Admin@123456`

## Integration Smoke Test

1. Start backend (`cd backend && mvn spring-boot:run`)
2. Run smoke script (`./scripts/smoke_e2e.sh`)
3. Full checklist: `docs/e2e-checklist.md`
