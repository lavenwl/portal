#!/usr/bin/env bash
set -euo pipefail

API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"
ADMIN_ACCOUNT="${ADMIN_ACCOUNT:-admin}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-Admin@123456}"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required" >&2
  exit 1
fi

if ! command -v node >/dev/null 2>&1; then
  echo "node is required for JSON parsing" >&2
  exit 1
fi

json_field() {
  local json="$1"
  local path="$2"
  node -e '
const payload = JSON.parse(process.argv[1]);
const keys = process.argv[2].split(".");
let cur = payload;
for (const k of keys) {
  if (cur == null) { console.log(""); process.exit(0); }
  cur = cur[k];
}
console.log(cur == null ? "" : String(cur));
' "$json" "$path"
}

echo "[1/7] Health check..."
HEALTH=$(curl -sS "${API_BASE_URL}/api/health")
if [[ "$HEALTH" != "ok" ]]; then
  echo "Health check failed: ${HEALTH}" >&2
  exit 1
fi

echo "[2/7] Login as admin..."
LOGIN_RES=$(curl -sS -X POST "${API_BASE_URL}/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"account\":\"${ADMIN_ACCOUNT}\",\"password\":\"${ADMIN_PASSWORD}\"}")
LOGIN_CODE=$(json_field "$LOGIN_RES" "code")
if [[ "$LOGIN_CODE" != "0" ]]; then
  echo "Login failed: ${LOGIN_RES}" >&2
  exit 1
fi
ACCESS_TOKEN=$(json_field "$LOGIN_RES" "data.accessToken")
REFRESH_TOKEN=$(json_field "$LOGIN_RES" "data.refreshToken")
if [[ -z "$ACCESS_TOKEN" || -z "$REFRESH_TOKEN" ]]; then
  echo "Token extraction failed: ${LOGIN_RES}" >&2
  exit 1
fi

auth_get() {
  local path="$1"
  curl -sS "${API_BASE_URL}${path}" -H "Authorization: Bearer ${ACCESS_TOKEN}"
}

echo "[3/7] Validate core APIs..."
for endpoint in /api/users /api/roles /api/permissions /api/logins /api/operations; do
  RES=$(auth_get "$endpoint")
  CODE=$(json_field "$RES" "code")
  if [[ "$CODE" != "0" ]]; then
    echo "API failed ${endpoint}: ${RES}" >&2
    exit 1
  fi
  echo "  - ${endpoint}: ok"
done

echo "[4/7] Create smoke user..."
SUFFIX=$(date +%s)
SMOKE_USER="smoke_${SUFFIX}"
SMOKE_EMAIL="smoke_${SUFFIX}@example.com"
CREATE_USER_RES=$(curl -sS -X POST "${API_BASE_URL}/api/users" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d "{\"username\":\"${SMOKE_USER}\",\"email\":\"${SMOKE_EMAIL}\",\"password\":\"Pass@123456\"}")
CREATE_USER_CODE=$(json_field "$CREATE_USER_RES" "code")
if [[ "$CREATE_USER_CODE" != "0" ]]; then
  echo "Create user failed: ${CREATE_USER_RES}" >&2
  exit 1
fi

echo "[5/7] Refresh token..."
REFRESH_RES=$(curl -sS -X POST "${API_BASE_URL}/api/auth/refresh" \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"${REFRESH_TOKEN}\"}")
REFRESH_CODE=$(json_field "$REFRESH_RES" "code")
if [[ "$REFRESH_CODE" != "0" ]]; then
  echo "Refresh failed: ${REFRESH_RES}" >&2
  exit 1
fi

NEW_REFRESH_TOKEN=$(json_field "$REFRESH_RES" "data.refreshToken")
if [[ -z "$NEW_REFRESH_TOKEN" ]]; then
  echo "Refresh token rotation failed: ${REFRESH_RES}" >&2
  exit 1
fi

echo "[6/7] Logout..."
LOGOUT_RES=$(curl -sS -X POST "${API_BASE_URL}/api/auth/logout" \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"${NEW_REFRESH_TOKEN}\"}")
LOGOUT_CODE=$(json_field "$LOGOUT_RES" "code")
if [[ "$LOGOUT_CODE" != "0" ]]; then
  echo "Logout failed: ${LOGOUT_RES}" >&2
  exit 1
fi

echo "[7/7] Verify login log endpoint after smoke..."
LOG_RES=$(auth_get "/api/logins")
LOG_CODE=$(json_field "$LOG_RES" "code")
if [[ "$LOG_CODE" != "0" ]]; then
  echo "Login log query failed: ${LOG_RES}" >&2
  exit 1
fi

echo "Smoke E2E passed."
