#!/usr/bin/env sh
#
# Carga las plantillas de correo en Redis.
#
# Van en Redis y no en src/main/resources porque son contenido de despliegue, no codigo: cambiar el
# maquetado de un correo no deberia costar un build. Igual que el catalogo de mensajes, la aplicacion
# las lee al arrancar y no levanta si falta alguna, asi que este script es un paso previo obligatorio
# del despliegue: cargar -> verificar -> desplegar.
#
# Espacio de claves propio: 'plantilla.<nombre>'. No cuelga de '<contexto>.', que es el espacio del
# catalogo de mensajes — catalogo/podar.sh escanea 'notificaciones.*' y borraria la plantilla como
# sobrante en la primera poda.
#
#   REDIS_HOST       obligatorio
#   REDIS_PORT       obligatorio
#   REDIS_USER       opcional (Redis 6+ ACL)
#   REDIS_PASSWORD   opcional
#   PLANTILLAS_DIR   opcional, directorio de los .html (por defecto, el del propio script)
#
# Uso:  REDIS_HOST=... REDIS_PORT=6379 REDIS_PASSWORD=... sh plantillas/cargar.sh

set -eu

# Lista explicita y no un glob sobre *.html: lo que Redis debe tener es lo que el arranque exige,
# asi que se declara, no se deduce de lo que haya en el directorio.
PLANTILLAS="correo-base"

PREFIJO="plantilla."

PLANTILLAS_DIR="${PLANTILLAS_DIR:-$(dirname "$0")}"

if [ -z "${REDIS_HOST:-}" ] || [ -z "${REDIS_PORT:-}" ]; then
    echo "ERROR: REDIS_HOST y REDIS_PORT son obligatorios. Sin ellos las plantillas podrian" >&2
    echo "       cargarse en la instancia equivocada, y ese error es silencioso." >&2
    exit 1
fi

if ! command -v redis-cli > /dev/null 2>&1; then
    echo "ERROR: redis-cli no esta en el PATH." >&2
    echo "       En local:  docker compose up plantillas-loader" >&2
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

echo "Cargando las plantillas de correo en $REDIS_HOST:$REDIS_PORT"
echo "Directorio: $PLANTILLAS_DIR"
echo ""

total=0

for nombre in $PLANTILLAS; do
    archivo="$PLANTILLAS_DIR/$nombre.html"

    if [ ! -f "$archivo" ]; then
        echo "ERROR: no existe $archivo" >&2
        exit 1
    fi

    # El fichero entero entra por stdin: lleva comillas, saltos de linea y llaves {{...}} que
    # cualquier otra via acabaria rompiendo. Nada de escapes, a diferencia del catalogo.
    redis -x SET "$PREFIJO$nombre" < "$archivo" > /dev/null

    if [ "$(redis EXISTS "$PREFIJO$nombre")" != "1" ]; then
        echo "ERROR: $PREFIJO$nombre no quedo escrita en Redis." >&2
        exit 1
    fi

    printf '  %-24s %6d bytes\n' "$PREFIJO$nombre" "$(wc -c < "$archivo" | tr -d ' ')"
    total=$((total + 1))
done

echo ""
echo "OK: $total plantillas presentes en Redis."
