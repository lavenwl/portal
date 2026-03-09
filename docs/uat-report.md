# UAT Report (v0.1.0)

## 1. Scope

Validated modules:

1. Registration and login
2. User management
3. Role management
4. Permission management
5. Login logs
6. Operation logs

## 2. Environment

1. Backend: Spring Boot 2.7.18, Java 11
2. Frontend: React + TypeScript + Vite 4
3. Database: H2 (local), MySQL-ready schema

## 3. Test Evidence

1. Backend automated tests passed (`mvn -q test`)
2. Frontend build passed (`npm run build`)
3. Smoke E2E script passed (`./scripts/smoke_e2e.sh`)

## 4. Key Verification Results

1. Admin login successful and token issued.
2. User/Role/Permission APIs accessible with valid authority.
3. Refresh token rotation works correctly.
4. Login success/failure records written to login logs.
5. Create/update/delete/bind operations written to operation logs.

## 5. Risks and Follow-ups

1. Production deployment still requires real MySQL and externalized secrets.
2. Recommend replacing default bootstrap admin password immediately after first deployment.
3. Recommend adding API integration tests for bind/unbind edge cases and pagination assertions.

## 6. UAT Conclusion

Result: **PASS** for MVP release baseline (v0.1.0).
