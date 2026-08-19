#!/usr/bin/env sh
#
# Carga el catálogo de mensajes en Redis (ADR-013).
#
# Con el fail-fast de arranque, la aplicación no levanta si a Redis le falta una sola de las claves
# que el código declara. Este script es por tanto un paso previo obligatorio del despliegue, no una
# tarea opcional: cargar -> verificar conteo -> desplegar.
#
# Host y puerto salen de las mismas variables que lee RedisConfig, sin valor por defecto: un
# 'localhost' implícito es la forma más fácil de cargar el catálogo en la instancia equivocada.
# El mismo script sirve para la instancia en la nube (ejecutándolo suelto) y para el contenedor
# local de docker-compose (servicio catalogo-loader).
#
#   REDIS_HOST      obligatorio
#   REDIS_PORT      obligatorio
#   REDIS_USER      opcional (Redis 6+ ACL)
#   REDIS_PASSWORD  opcional
#   CATALOGO_DIR    opcional, directorio de los .properties (por defecto, el del propio script)
#
# Uso:  REDIS_HOST=... REDIS_PORT=6379 REDIS_PASSWORD=... sh catalogo/cargar.sh

set -eu

# Los contextos, en el mismo orden que ContextosCatalogo.TODOS. La lista es explícita y no un glob
# sobre *.properties: el contenido de Redis es lo que el fail-fast del arranque exige, así que tiene
# que estar declarado, no deducido de lo que haya en el directorio.
CONTEXTOS="app fichas seguridad usuarios notificaciones"

# El script vive junto a los datos que carga, así que por defecto lee su propio directorio: la carga
# funciona igual desde la raíz del repositorio, desde catalogo/ o desde el contenedor, sin que el
# directorio de trabajo cambie el resultado.
#
# Son datos de despliegue, no recursos de la aplicación: por eso no viven en src/main/resources. Los
# tests leen estos mismos archivos a través del artefacto de test fixtures de shared:message, así que
# hay una sola fuente de verdad.
CATALOGO_DIR="${CATALOGO_DIR:-$(dirname "$0")}"

VERIFICACION_POR_LOTE=100

# Los .properties del repositorio tienen finales de línea mezclados (CRLF y LF). Un \r arrastrado
# al valor se escribiría en Redis y saldría en la respuesta al cliente sin que nada lo delate, así
# que se recorta línea a línea.
CR="$(printf '\r')"

if [ -z "${REDIS_HOST:-}" ] || [ -z "${REDIS_PORT:-}" ]; then
    echo "ERROR: REDIS_HOST y REDIS_PORT son obligatorios. Sin ellos el catálogo podría cargarse" >&2
    echo "       en la instancia equivocada, y ese error es silencioso." >&2
    exit 1
fi

# Redis no publica binarios nativos para Windows, así que en local lo normal es no tener redis-cli.
# La vía prevista es el contenedor: 'docker compose up catalogo-loader' para la instancia local, o
# 'docker run --rm redis:7-alpine' montando este directorio para una instancia remota.
if ! command -v redis-cli > /dev/null 2>&1; then
    echo "ERROR: redis-cli no está en el PATH." >&2
    echo "       En local:  docker compose up catalogo-loader" >&2
    echo "       Remoto:    MSYS_NO_PATHCONV=1 docker run --rm -v \"\$(pwd)/catalogo:/catalogo\" \\" >&2
    echo "                    -e REDIS_HOST=... -e REDIS_PORT=6379 \\" >&2
    echo "                    -e REDIS_USER=... -e REDIS_PASSWORD=... \\" >&2
    echo "                    redis:7-alpine sh /catalogo/cargar.sh" >&2
    exit 1
fi

# UTF-8 explícito: sin esto las tildes del catálogo llegan corruptas a Redis. Es el mismo fallo que
# obligó a declarar el encoding al leer estos archivos desde Java.
LC_ALL=C.UTF-8
LANG=C.UTF-8
export LC_ALL LANG

# Con ACL de Redis 6+ hacen falta usuario Y contraseña: pasar solo --user deja la autenticación
# incompleta y el servidor responde NOAUTH, que aquí se vería como "Redis no responde".
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

echo "Cargando el catálogo de mensajes en $REDIS_HOST:$REDIS_PORT"
echo "Directorio: $CATALOGO_DIR"
echo ""

# Nunca FLUSHDB: esta instancia comparte espacio con los buckets de rate limit y los tokens
# invalidados de seguridad. El script sobrescribe clave por clave y es re-ejecutable.

claves_esperadas="$(mktemp)"
total_cargadas=0
trap 'rm -f "$claves_esperadas"' EXIT

for contexto in $CONTEXTOS; do
    archivo="$CATALOGO_DIR/$contexto.properties"

    if [ ! -f "$archivo" ]; then
        echo "ERROR: no existe $archivo" >&2
        exit 1
    fi

    cargadas_contexto=0

    # El valor viaja por stdin ('redis-cli -x SET clave'), nunca como argumento: los textos llevan
    # espacios, comillas y marcadores %s que cualquier otra vía acabaría rompiendo.
    while IFS= read -r linea || [ -n "$linea" ]; do
        linea="${linea%"$CR"}"

        case "$linea" in
            '#'* | '' | ' '*) continue ;;
        esac

        # Partir por el PRIMER '=': los valores contienen '=' (por ejemplo 'id={}').
        clave="${linea%%=*}"
        valor="${linea#*=}"

        if [ "$clave" = "$linea" ]; then
            echo "ERROR: línea sin '=' en $archivo: $linea" >&2
            exit 1
        fi

        printf '%s' "$valor" | redis -x SET "$clave" > /dev/null
        echo "$clave" >> "$claves_esperadas"
        cargadas_contexto=$((cargadas_contexto + 1))
    done < "$archivo"

    total_cargadas=$((total_cargadas + cargadas_contexto))
    printf '  %-16s %4d claves\n' "$contexto" "$cargadas_contexto"
done

echo ""
echo "Cargadas: $total_cargadas claves"

# La verificación es la compuerta que el fail-fast del arranque necesita. Sin ella, una carga
# parcial no se nota hasta que el despliegue no levanta, y el diagnóstico llega tarde.
#
# Se comprueba con EXISTS sobre las claves que acabamos de escribir, y no con un SCAN por prefijo:
# el SCAN recorrería también las claves de rate limit y de tokens invalidados que viven en esta
# misma instancia, y contaría de más.
echo "Verificando..."

total_verificadas=0
lote=""
en_lote=0

verificar_lote() {
    if [ "$en_lote" -eq 0 ]; then
        return 0
    fi
    # shellcheck disable=SC2086
    existentes="$(redis EXISTS $lote)"
    total_verificadas=$((total_verificadas + existentes))
}

while IFS= read -r clave; do
    lote="$lote $clave"
    en_lote=$((en_lote + 1))

    if [ "$en_lote" -ge "$VERIFICACION_POR_LOTE" ]; then
        verificar_lote
        lote=""
        en_lote=0
    fi
done < "$claves_esperadas"

verificar_lote

if [ "$total_verificadas" -ne "$total_cargadas" ]; then
    echo "ERROR: verificación fallida. Escritas $total_cargadas, presentes $total_verificadas." >&2
    echo "       NO desplegar: con el fail-fast de arranque la aplicación no levantaría." >&2
    exit 1
fi

echo "OK: $total_verificadas de $total_cargadas claves presentes en Redis."
