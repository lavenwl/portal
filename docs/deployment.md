# Deployment Guide

## 1. Pre-Deploy Checklist

1. Backend tests pass: `cd backend && mvn -q test`
2. Frontend build passes: `cd frontend && npm run build`
3. Set production `JWT_SECRET`
4. Set production database credentials
5. Decide whether bootstrap should be enabled (`BOOTSTRAP_ENABLED`)

## 2. Build Artifacts

1. Backend jar: `cd backend && mvn -q package`
2. Frontend assets: `cd frontend && npm run build` (output in `frontend/dist`)

## 3. Release Steps

1. Deploy backend jar
2. Deploy frontend static files
3. Run health check `/api/health`
4. Validate login and user list access
5. Validate log query APIs

## 4. Rollback Strategy

1. Keep previous backend jar and frontend dist artifact
2. Roll back both backend and frontend together when API schema changed
3. Re-verify `/api/health` and login flow after rollback
