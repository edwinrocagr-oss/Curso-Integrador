#!/usr/bin/env bash
set -euo pipefail
if [[ $# -ne 2 || "$2" != "--confirmar-restauracion" ]]; then
  echo "Uso: $0 archivo.dump --confirmar-restauracion" >&2; exit 2
fi
[[ -f "$1" ]] || { echo "No existe el respaldo" >&2; exit 2; }
: "${CAFEGEST_DB_NAME:=cafegest}"
: "${CAFEGEST_DB_HOST:=localhost}"
: "${CAFEGEST_DB_PORT:=5432}"
: "${CAFEGEST_DB_USER:=postgres}"
echo "ATENCION: restauración sobre $CAFEGEST_DB_NAME. Se recomienda restaurar en una BD vacía de prueba."
PGHOST="$CAFEGEST_DB_HOST" PGPORT="$CAFEGEST_DB_PORT" PGUSER="$CAFEGEST_DB_USER" pg_restore --exit-on-error --no-owner --no-privileges -d "$CAFEGEST_DB_NAME" "$1"
