#!/usr/bin/env sh
#
# Carga datos de prueba (solo dev) en la base de un contexto con psql.
#
# No es parte del despliegue ni del arranque: el backend no la invoca y Flyway no la ve. Requiere
# que la migración del contexto ya haya corrido (arranca el backend una vez antes).
#
# Conexión sin valor por defecto: un 'localhost' implícito es la forma más fácil de cargar datos de
# prueba en la instancia equivocada.
#
#   PGHOST      obligatorio
#   PGPORT      obligatorio
#   PGUSER      obligatorio
#   PGPASSWORD  obligatorio
#   PGDATABASE  opcional (por defecto: biblioteca)
#   SEMILLAS_DIR opcional, directorio de los .sql (por defecto, el del propio script)
#
# Uso:  PGHOST=localhost PGPORT=5432 PGUSER=... PGPASSWORD=... sh semillas/cargar.sh

set -eu

SEMILLAS_DIR="${SEMILLAS_DIR:-$(dirname "$0")}"
PGDATABASE="${PGDATABASE:-biblioteca}"
export PGDATABASE

if [ -z "${PGHOST:-}" ] || [ -z "${PGPORT:-}" ] || [ -z "${PGUSER:-}" ] || [ -z "${PGPASSWORD:-}" ]; then
    echo "ERROR: PGHOST, PGPORT, PGUSER y PGPASSWORD son obligatorios. Sin ellos la semilla podría" >&2
    echo "       cargarse en la instancia equivocada, y ese error es silencioso." >&2
    exit 1
fi

migrada="$(psql -tAc "SELECT to_regclass('documentacion')")"
if [ -z "$migrada" ]; then
    echo "ERROR: la tabla documentacion no existe en $PGDATABASE. Arranca el backend primero para que" >&2
    echo "       Flyway aplique las migraciones y vuelve a correr este script." >&2
    exit 1
fi

psql -v ON_ERROR_STOP=1 -f "$SEMILLAS_DIR/biblioteca.sql"
echo "Semilla de biblioteca cargada en $PGDATABASE."
