#!/usr/bin/env bash
set -euo pipefail
: "${CAFEGEST_DB_NAME:=cafegest}"
: "${CAFEGEST_DB_HOST:=localhost}"
: "${CAFEGEST_DB_PORT:=5432}"
: "${CAFEGEST_DB_USER:=postgres}"
: "${CAFEGEST_BACKUP_DIR:=$HOME/cafegest-backups}"
command -v pg_dump >/dev/null || { echo "ERROR: falta pg_dump" >&2; exit 1; }
mkdir -p "$CAFEGEST_BACKUP_DIR"
chmod 700 "$CAFEGEST_BACKUP_DIR"
tmp=$(mktemp "$CAFEGEST_BACKUP_DIR/.backup.XXXXXXXX")
trap 'rm -f "$tmp"' EXIT
PGHOST="$CAFEGEST_DB_HOST" PGPORT="$CAFEGEST_DB_PORT" PGUSER="$CAFEGEST_DB_USER" pg_dump -Fc -f "$tmp" "$CAFEGEST_DB_NAME"
file="$CAFEGEST_BACKUP_DIR/cafegest_$(date +%Y%m%d_%H%M%S).dump"
mv "$tmp" "$file"
chmod 600 "$file"
trap - EXIT
find "$CAFEGEST_BACKUP_DIR" -maxdepth 1 -name 'cafegest_*.dump' -type f -mtime +14 -delete
echo "Backup creado: $file"
