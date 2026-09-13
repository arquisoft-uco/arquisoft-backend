#!/usr/bin/env bash
# Valida el versionamiento Flyway del repositorio.
# Uso: scripts/validar-migraciones.sh [rama-base]   (por defecto: origin/main)
set -uo pipefail

BASE="${1:-origin/main}"
ERRORES=0

error() { echo "::ERROR:: $*" >&2; ERRORES=$((ERRORES + 1)); }
ok()    { echo "  ok  $*"; }

RAICES=$(find . -path ./.git -prune -o -path "*/.claude/*" -prune -o -path "*/build/*" -prune -o -path "*/src/main/resources/db/migration" -type d -print | sort)
[ -z "$RAICES" ] && { echo "No se encontró ningún db/migration"; exit 1; }

echo "== 1. Ningún .sql suelto en db/migration/ (rompería el Flyway de todos los contextos)"
for raiz in $RAICES; do
  sueltos=$(find "$raiz" -maxdepth 1 -name '*.sql')
  [ -n "$sueltos" ] && error "SQL fuera de db/migration/{contexto}/: $sueltos"
done
[ "$ERRORES" -eq 0 ] && ok "todas las migraciones viven en su subcarpeta de contexto"

echo "== 2. Nomenclatura V{yyyyMMddHHmmss}__descripcion.sql"
TODAS=$(find $RAICES -mindepth 2 -name '*.sql' | sort)
for f in $TODAS; do
  rel="${f##*/db/migration/}"
  base=$(basename "$f")
  if [[ ! "$base" =~ ^V([0-9]{14})__[a-z0-9]+(_[a-z0-9]+)*\.sql$ ]]; then
    error "nombre inválido: $rel (esperado V{yyyyMMddHHmmss}__descripcion_en_snake_case.sql)"
    continue
  fi
  ts="${BASH_REMATCH[1]}"
  if ! date -u -d "${ts:0:4}-${ts:4:2}-${ts:6:2} ${ts:8:2}:${ts:10:2}:${ts:12:2}" >/dev/null 2>&1; then
    error "timestamp no es una fecha válida: $rel"
    continue
  fi
  if [ "$ts" -gt "$(date -u +%Y%m%d%H%M%S)" ]; then
    error "timestamp en el futuro: $rel"
    continue
  fi
  ok "$rel"
done

echo "== 3. Versión y descripción únicas por contexto"
for raiz in $RAICES; do
  for ctx in "$raiz"/*/; do
    [ -d "$ctx" ] || continue
    nombre=$(basename "$ctx")
    dupv=$(find "$ctx" -name '*.sql' -printf '%f\n' | sed -E 's/^V([0-9.]+)__.*/\1/' | sort | uniq -d)
    [ -n "$dupv" ] && error "versión duplicada en '$nombre': $dupv"
    dupd=$(find "$ctx" -name '*.sql' -printf '%f\n' | sed -E 's/^V[0-9.]+__(.*)\.sql$/\1/' | sort | uniq -d)
    [ -n "$dupd" ] && error "descripción duplicada en '$nombre': $dupd"
  done
done

echo "== 4. Migraciones ya integradas: inmutables y sin retroceder el timestamp"
if git rev-parse --verify "$BASE" >/dev/null 2>&1; then
  cambios=$(git diff --name-status "$BASE"...HEAD -- '*/db/migration/*.sql')
  while IFS=$'\t' read -r estado ruta resto; do
    [ -z "${estado:-}" ] && continue
    rel="${ruta##*/db/migration/}"
    ctx="${rel%%/*}"
    # Las migraciones legado (sin timestamp) quedan fuera de la comparación histórica.
    [[ "$(basename "$ruta")" =~ ^V[0-9]{14}__ ]] || continue
    case "$estado" in
      M) error "migración ya integrada modificada: $rel (crea una nueva en su lugar)" ;;
      D) error "migración ya integrada eliminada: $rel" ;;
      R*) error "migración ya integrada renombrada: $rel -> ${resto##*/db/migration/}" ;;
      A)
        base_ts=$(git ls-tree -r --name-only "$BASE" \
          | grep "/db/migration/$ctx/" | sed -E 's#.*/V([0-9]{14})__.*#\1#' \
          | grep -E '^[0-9]{14}$' | sort | tail -1)
        nuevo_ts=$(basename "$ruta" | sed -E 's/^V([0-9]{14})__.*/\1/')
        if [ -n "$base_ts" ] && [[ "$nuevo_ts" =~ ^[0-9]{14}$ ]] && [ "$nuevo_ts" -le "$base_ts" ]; then
          error "timestamp retrocedido en '$ctx': $rel <= $base_ts ya en $BASE"
        else
          ok "nueva migración $rel"
        fi
        ;;
    esac
  done <<< "$cambios"
else
  echo "  (aviso) rama base '$BASE' no disponible; se omite la comparación histórica"
fi

echo
if [ "$ERRORES" -gt 0 ]; then
  echo "FALLO: $ERRORES problema(s) de versionamiento Flyway"
  exit 1
fi
echo "OK: versionamiento Flyway válido"
