#!/usr/bin/env bash
set -euo pipefail
: "${CAFEGEST_DB_HOST:=localhost}"
: "${CAFEGEST_DB_PORT:=5432}"
: "${CAFEGEST_DB_USER:=postgres}"
: "${CAFEGEST_DB_NAME:=cafegest}"
command -v pg_isready >/dev/null || { echo "ERROR: falta pg_isready" >&2; exit 2; }
pg_isready -h "$CAFEGEST_DB_HOST" -p "$CAFEGEST_DB_PORT" -U "$CAFEGEST_DB_USER" -d "$CAFEGEST_DB_NAME"
