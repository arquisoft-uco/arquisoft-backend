#!/bin/sh
#
# Prepara el almacenamiento de objetos del repositorio de artefactos (HU-275): crea el bucket y aplica
# el ciclo de vida que expira los archivos pendientes de confirmar. Es idempotente: se puede correr
# cuantas veces haga falta y el resultado es siempre el mismo.
#
# Los buckets los crea este script y nunca el backend: en la nube la cuenta del backend no tiene
# ListBucket ni CreateBucket, y un bucket creado "al vuelo" ocultaria que falta configurarlo.
#
# 'mcli ilm rule import' reemplaza la configuracion completa del bucket, asi que reejecutarlo no
# acumula reglas (a diferencia de 'ilm rule add'). Sin regla de abortar multipart incompleto: MinIO ya
# expira los multipart abandonados por defecto (api stale_uploads_expiry, 24 h) y el POST con politica
# del navegador no es multipart.
#
#   MINIO_ENDPOINT        obligatorio, URL del servidor (p. ej. http://minio:9000)
#   MINIO_ROOT_USER       obligatorio, credencial con permiso de administracion
#   MINIO_ROOT_PASSWORD   obligatorio
#   BUCKET                opcional, por defecto repositorio-artefactos
#   MINIO_DIR             opcional, directorio de ciclo-vida-repositorio-artefactos.json (por defecto,
#                         el del propio script)
#
# Uso:  MINIO_ENDPOINT=... MINIO_ROOT_USER=... MINIO_ROOT_PASSWORD=... sh minio/configurar.sh

set -eu

BUCKET="${BUCKET:-repositorio-artefactos}"
MINIO_DIR="${MINIO_DIR:-$(dirname "$0")}"
CICLO_VIDA="$MINIO_DIR/ciclo-vida-repositorio-artefactos.json"

if [ -z "${MINIO_ENDPOINT:-}" ] || [ -z "${MINIO_ROOT_USER:-}" ] || [ -z "${MINIO_ROOT_PASSWORD:-}" ]; then
    echo "ERROR: MINIO_ENDPOINT, MINIO_ROOT_USER y MINIO_ROOT_PASSWORD son obligatorios. Sin ellos el" >&2
    echo "       bucket podria configurarse en el servidor equivocado, y ese error es silencioso." >&2
    exit 1
fi

if ! command -v mcli > /dev/null 2>&1; then
    echo "ERROR: mcli no esta en el PATH. En local:  docker compose up minio-configurador" >&2
    exit 1
fi

if [ ! -f "$CICLO_VIDA" ]; then
    echo "ERROR: no existe $CICLO_VIDA" >&2
    exit 1
fi

mcli alias set local "$MINIO_ENDPOINT" "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" > /dev/null

# Privado por defecto: ningun objeto se sirve sin credenciales ni formulario firmado.
mcli mb --ignore-existing "local/$BUCKET"

mcli ilm rule import "local/$BUCKET" < "$CICLO_VIDA"

echo "Bucket '$BUCKET' listo. Reglas de ciclo de vida:"
mcli ilm rule ls "local/$BUCKET"
