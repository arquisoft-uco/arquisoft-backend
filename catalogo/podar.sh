#!/usr/bin/env sh
#
# Poda del catálogo de mensajes en Redis (ADR-013).
#
# 'cargar.sh' sobrescribe clave por clave pero nunca borra: una clave renombrada o eliminada de los
# .properties se queda en Redis para siempre. No rompe nada —nadie la pide, y el fail-fast del
# arranque solo comprueba que no falte ninguna de las declaradas— pero se acumula y confunde a quien
# inspeccione la instancia. Este script es la otra mitad: borra lo que sobra.
#
# Va aparte de cargar.sh a propósito. Cargar es un paso obligatorio del despliegue y debe poder
# ejecutarse sin pensar; borrar es destructivo y merece una decisión explícita.
#
# Solo toca claves cuyo nombre empieza por '<contexto>.' — el espacio del catálogo. Las demás claves
# de esta instancia usan el prefijo 'arquisoft:' (buckets de rate limit, blacklist de jti, caché),
# así que no hay solape posible.
#
#   REDIS_HOST      obligatorio
#   REDIS_PORT      obligatorio
#   REDIS_USER      opcional (Redis 6+ ACL)
#   REDIS_PASSWORD  opcional
#   CATALOGO_DIR    opcional, directorio de los .properties (por defecto, el del propio script)
#   DRY_RUN         opcional, '1' para listar lo que se borraría sin borrar nada
#
# Uso:  REDIS_HOST=... REDIS_PORT=6379 REDIS_PASSWORD=... sh catalogo/podar.sh
#       DRY_RUN=1 ... sh catalogo/podar.sh

set -eu

CONTEXTOS="app fichas seguridad usuarios notificaciones"

CATALOGO_DIR="${CATALOGO_DIR:-$(dirname "$0")}"
DRY_RUN="${DRY_RUN:-0}"

BORRADO_POR_LOTE=100

CR="$(printf '\r')"

if [ -z "${REDIS_HOST:-}" ] || [ -z "${REDIS_PORT:-}" ]; then
    echo "ERROR: REDIS_HOST y REDIS_PORT son obligatorios. Sin ellos el catálogo podría podarse" >&2
    echo "       en la instancia equivocada, y ese error es silencioso." >&2
    exit 1
fi

if ! command -v redis-cli > /dev/null 2>&1; then
    echo "ERROR: redis-cli no está en el PATH." >&2
    echo "       En local:  docker compose --profile mantenimiento up catalogo-podador" >&2
    echo "       Remoto:    MSYS_NO_PATHCONV=1 docker run --rm -v \"\$(pwd)/catalogo:/catalogo\" \\" >&2
    echo "                    -e REDIS_HOST=... -e REDIS_PORT=6379 \\" >&2
    echo "                    -e REDIS_USER=... -e REDIS_PASSWORD=... \\" >&2
    echo "                    redis:7-alpine sh /catalogo/podar.sh" >&2
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

# --- 1. Lo que los .properties declaran -------------------------------------------------------
for contexto in $CONTEXTOS; do
    archivo="$CATALOGO_DIR/$contexto.properties"

    if [ ! -f "$archivo" ]; then
        echo "ERROR: no existe $archivo" >&2
        exit 1
    fi

    while IFS= read -r linea || [ -n "$linea" ]; do
        linea="${linea%"$CR"}"
        case "$linea" in
            '#'* | '' | ' '*) continue ;;
        esac
        printf '%s\n' "${linea%%=*}" >> "$esperadas"
    done < "$archivo"
done

total_esperadas="$(wc -l < "$esperadas" | tr -d ' ')"

# Salvaguarda: si los .properties no aportaron ni una clave, algo va mal con CATALOGO_DIR y sin
# esta comprobación el script interpretaría "no hay nada declarado" como "bórralo todo".
if [ "$total_esperadas" -eq 0 ]; then
    echo "ERROR: los .properties de $CATALOGO_DIR no declaran ninguna clave." >&2
    echo "       Se aborta: continuar borraría el catálogo entero de Redis." >&2
    exit 1
fi

# --- 2. Lo que Redis tiene --------------------------------------------------------------------
# SCAN (via 'redis-cli --scan') y no KEYS: esta instancia es compartida y KEYS la bloquea entera
# mientras recorre el espacio de claves.
for contexto in $CONTEXTOS; do
    redis --scan --pattern "$contexto.*" >> "$en_redis"
done

total_en_redis="$(wc -l < "$en_redis" | tr -d ' ')"

# --- 3. La diferencia -------------------------------------------------------------------------
sort -u "$esperadas" -o "$esperadas"
sort -u "$en_redis" -o "$en_redis"
comm -13 "$esperadas" "$en_redis" > "$sobrantes"

total_sobrantes="$(wc -l < "$sobrantes" | tr -d ' ')"

echo "Podando el catálogo de mensajes en $REDIS_HOST:$REDIS_PORT"
echo "Directorio: $CATALOGO_DIR"
echo ""
echo "  declaradas en los .properties : $total_esperadas"
echo "  presentes en Redis            : $total_en_redis"
echo "  sobrantes                     : $total_sobrantes"
echo ""

if [ "$total_sobrantes" -eq 0 ]; then
    echo "OK: Redis no tiene ninguna clave de catálogo que sobre."
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
total_borradas=0
lote=""
en_lote=0

borrar_lote() {
    if [ "$en_lote" -eq 0 ]; then
        return 0
    fi
    # shellcheck disable=SC2086
    borradas="$(redis DEL $lote)"
    total_borradas=$((total_borradas + borradas))
}

while IFS= read -r clave; do
    lote="$lote $clave"
    en_lote=$((en_lote + 1))

    if [ "$en_lote" -ge "$BORRADO_POR_LOTE" ]; then
        borrar_lote
        lote=""
        en_lote=0
    fi
done < "$sobrantes"

borrar_lote

echo "Borradas: $total_borradas de $total_sobrantes claves sobrantes."

if [ "$total_borradas" -ne "$total_sobrantes" ]; then
    echo "AVISO: alguna clave desapareció entre el listado y el borrado." >&2
fi
