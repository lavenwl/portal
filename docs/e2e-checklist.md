# E2E Integration Checklist

## 1. Environment Ready

1. Backend service started at `http://localhost:8080`.
2. Frontend service started at `http://localhost:5173`.
3. Bootstrap admin exists (`admin` / `Admin@123456`) or overridden by environment variables.

## 2. Auth Flow

1. Open login page and sign in with admin.
2. Verify redirected to dashboard.
3. Refresh browser and verify session remains valid.
4. Logout and verify route guard redirects to login.

## 3. RBAC Core

1. Open user management page and create a test user.
2. Open role management and create a test role.
3. Open permission management and create a test permission.
4. Bind test permission to test role.
5. Bind test role to test user.
6. Login as test user and verify only expected pages/actions are available.

## 4. Logs

1. Trigger one successful login and one failed login.
2. Verify `Login Logs` page contains both records.
3. Perform create/update/delete operations on user/role/permission.
4. Verify `Operation Logs` page contains operation records with module/action/result.

## 5. API Spot Checks

1. `GET /api/health` returns `ok`.
2. `GET /api/users` returns `code=0` with admin token.
3. `GET /api/roles` returns `code=0` with admin token.
4. `GET /api/permissions` returns `code=0` with admin token.
5. `GET /api/logins` and `GET /api/operations` return `code=0` with admin token.

## 6. Regression Gate

1. Backend: `bash ./scripts/backend.sh -q test`.
2. Frontend: `cd frontend && npm run build`.
3. CI workflow is green on target branch.
