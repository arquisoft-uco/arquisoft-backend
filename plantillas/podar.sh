#!/usr/bin/env sh
#
# Poda de las plantillas de correo en Redis.
#
# 'cargar.sh' sobrescribe pero nunca borra: una plantilla renombrada o retirada se queda en Redis
# para siempre. No rompe nada —nadie la pide— pero se acumula. Este script es la otra mitad.
#
# Va aparte de cargar.sh a proposito, como en el catalogo: cargar es un paso obligatorio del
# despliegue y debe poder ejecutarse sin pensar; borrar es destructivo y merece una decision.
#
# Solo toca claves 'plantilla.*'. El catalogo de mensajes vive en '<contexto>.' y el resto de la
# instancia bajo 'arquisoft:', asi que no hay solape posible.
#
#   REDIS_HOST       obligatorio
#   REDIS_PORT       obligatorio
#   REDIS_USER       opcional (Redis 6+ ACL)
#   REDIS_PASSWORD   opcional
#   PLANTILLAS_DIR   opcional, directorio de los .html (por defecto, el del propio script)
#   DRY_RUN          opcional, '1' para listar lo que se borraria sin borrar nada
#   PODA_TOTAL       opcional, '1' para borrar TODAS las plantillas, no solo las sobrantes
#
# Uso:  REDIS_HOST=... REDIS_PORT=6379 REDIS_PASSWORD=... sh plantillas/podar.sh
#       DRY_RUN=1 ... sh plantillas/podar.sh
#
# PODA_TOTAL=1 deja el espacio vacio y la aplicacion no vuelve a arrancar hasta ejecutar cargar.sh.
# Existe para el ensayo manual de degradacion, no para mantenimiento.

set -eu

PLANTILLAS="correo-base"

PREFIJO="plantilla."

PLANTILLAS_DIR="${PLANTILLAS_DIR:-$(dirname "$0")}"
DRY_RUN="${DRY_RUN:-0}"
PODA_TOTAL="${PODA_TOTAL:-0}"

if [ -z "${REDIS_HOST:-}" ] || [ -z "${REDIS_PORT:-}" ]; then
    echo "ERROR: REDIS_HOST y REDIS_PORT son obligatorios. Sin ellos las plantillas podrian" >&2
    echo "       podarse en la instancia equivocada, y ese error es silencioso." >&2
    exit 1
fi

if ! command -v redis-cli > /dev/null 2>&1; then
    echo "ERROR: redis-cli no esta en el PATH." >&2
    echo "       En local:  docker compose --profile mantenimiento up plantillas-podador" >&2
    exit 1
fi

LC_ALL=C.UTF-8
LANG=C.UTF-8
export LC_ALL LANG

redis() {
    if [ -n "${REDIS_USER:-}" ]; then
        redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" \
            --user "$REDIS_USER" --pass "${REDIS_PASSWORD:-}" --no-auth-warning "$@"
    elif [ -n "${REDIS_PASSWORD:-}" ]; then
        redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" -a "$REDIS_PASSWORD" --no-auth-warning "$@"
    else
        redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" "$@"
    fi
}

if [ "$(redis PING 2>/dev/null || true)" != "PONG" ]; then
    echo "ERROR: Redis no responde en $REDIS_HOST:$REDIS_PORT." >&2
    exit 1
fi

esperadas="$(mktemp)"
en_redis="$(mktemp)"
sobrantes="$(mktemp)"
trap 'rm -f "$esperadas" "$en_redis" "$sobrantes"' EXIT

# --- 1. Lo que se declara ---------------------------------------------------------------------
for nombre in $PLANTILLAS; do
    archivo="$PLANTILLAS_DIR/$nombre.html"

    if [ ! -f "$archivo" ]; then
        echo "ERROR: no existe $archivo" >&2
        exit 1
    fi

    printf '%s\n' "$PREFIJO$nombre" >> "$esperadas"
done

total_esperadas="$(wc -l < "$esperadas" | tr -d ' ')"

# Misma salvaguarda que en el catalogo: sin declaraciones, "no hay nada" se leeria como "borralo
# todo". Solo puede darse con un PLANTILLAS_DIR equivocado, que es justo el caso a atrapar.
if [ "$total_esperadas" -eq 0 ]; then
    echo "ERROR: $PLANTILLAS_DIR no declara ninguna plantilla." >&2
    echo "       Se aborta: continuar borraria todas las plantillas de Redis." >&2
    exit 1
fi

# --- 2. Lo que Redis tiene --------------------------------------------------------------------
# SCAN y no KEYS: la instancia es compartida y KEYS la bloquea entera mientras recorre.
redis --scan --pattern "$PREFIJO*" >> "$en_redis"

total_en_redis="$(wc -l < "$en_redis" | tr -d ' ')"

# --- 3. La diferencia -------------------------------------------------------------------------
sort -u "$esperadas" -o "$esperadas"
sort -u "$en_redis" -o "$en_redis"
if [ "$PODA_TOTAL" = "1" ]; then
    cp "$en_redis" "$sobrantes"
else
    comm -13 "$esperadas" "$en_redis" > "$sobrantes"
fi

total_sobrantes="$(wc -l < "$sobrantes" | tr -d ' ')"

if [ "$PODA_TOTAL" = "1" ]; then
    echo "PODA TOTAL: se borran TODAS las plantillas, no solo las sobrantes."
    echo "            Tras esto la aplicacion no arranca hasta volver a ejecutar cargar.sh."
    echo ""
fi

echo "Podando las plantillas de correo en $REDIS_HOST:$REDIS_PORT"
echo "Directorio: $PLANTILLAS_DIR"
echo ""
echo "  declaradas        : $total_esperadas"
echo "  presentes en Redis: $total_en_redis"
echo "  a borrar          : $total_sobrantes"
echo ""

if [ "$total_sobrantes" -eq 0 ]; then
    echo "OK: no hay ninguna plantilla que borrar."
    exit 0
fi

while IFS= read -r clave; do
    echo "  - $clave"
done < "$sobrantes"
echo ""

if [ "$DRY_RUN" = "1" ]; then
    echo "DRY_RUN=1: no se ha borrado nada."
    exit 0
fi

# --- 4. Borrado --------------------------------------------------------------------------------
lote=""
while IFS= read -r clave; do
    lote="$lote $clave"
done < "$sobrantes"

# shellcheck disable=SC2086
total_borradas="$(redis DEL $lote)"

echo "Borradas: $total_borradas de $total_sobrantes plantillas sobrantes."

if [ "$total_borradas" -ne "$total_sobrantes" ]; then
    echo "AVISO: alguna plantilla desaparecio entre el listado y el borrado." >&2
fi
