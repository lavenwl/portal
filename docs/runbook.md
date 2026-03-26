# Runbook

## 1. Backend Local Run

1. `bash ./scripts/backend.sh spring-boot:run`
2. API base: `http://localhost:8080`

## 2. Frontend Local Run

1. `cd frontend`
2. `npm install`
3. `npm run dev`
4. UI base: `http://localhost:5173`

## 3. Default Bootstrap Admin

1. Username: `admin`
2. Password: `Admin@123456`
3. Change immediately in non-dev environments.

## 4. Key Environment Variables

1. `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`
2. `JWT_SECRET`, `JWT_ACCESS_EXPIRE`, `JWT_REFRESH_EXPIRE`
3. `BOOTSTRAP_ENABLED`, `BOOTSTRAP_ADMIN_USERNAME`, `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PASSWORD`

## 5. Smoke Check

1. `GET /api/health` returns `ok`
2. Login with bootstrap admin
3. Call `/api/users`, `/api/roles`, `/api/permissions`, `/api/logins`, `/api/operations`
