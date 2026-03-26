#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"

if [[ -z "${JAVA_HOME:-}" ]]; then
  if [[ "$(uname -s)" == "Darwin" ]] && [[ -x "/usr/libexec/java_home" ]]; then
    if JAVA_HOME_CANDIDATE="$(/usr/libexec/java_home -v 11 2>/dev/null)"; then
      export JAVA_HOME="$JAVA_HOME_CANDIDATE"
    elif command -v java >/dev/null 2>&1; then
      JAVA_BIN="$(command -v java)"
      export JAVA_HOME="$(cd "$(dirname "$JAVA_BIN")/.." && pwd)"
    fi
  elif command -v java >/dev/null 2>&1; then
    JAVA_BIN="$(readlink -f "$(command -v java)" 2>/dev/null || command -v java)"
    export JAVA_HOME="$(cd "$(dirname "$JAVA_BIN")/.." && pwd)"
  fi
fi

if [[ -z "${JAVA_HOME:-}" ]] || [[ ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "Unable to resolve JAVA_HOME. Set JAVA_HOME to a JDK 11 installation and retry." >&2
  exit 1
fi

cd "$BACKEND_DIR"
exec mvn "$@"
