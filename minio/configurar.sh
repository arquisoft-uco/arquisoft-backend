#!/bin/sh
#
# Prepara el almacenamiento de objetos: crea los buckets del repositorio de artefactos (HU-275) y de
# artefactos (HU-043) y aplica a cada uno el ciclo de vida que expira los archivos pendientes de
# confirmar. Es idempotente: se puede correr cuantas veces haga falta y el resultado es siempre el mismo.
#
# Los buckets los crea este script y nunca el backend: en la nube la cuenta del backend no tiene
# ListBucket ni CreateBucket, y un bucket creado "al vuelo" ocultaria que falta configurarlo.
#
# 'mcli ilm rule import' reemplaza la configuracion completa del bucket, asi que reejecutarlo no
# acumula reglas (a diferencia de 'ilm rule add'). Sin regla de abortar multipart incompleto: MinIO ya
# expira los multipart abandonados por defecto (api stale_uploads_expiry, 24 h) y el POST con politica
# del navegador no es multipart.
#
#   MINIO_ENDPOINT                obligatorio, URL del servidor (p. ej. http://minio:9000)
#   MINIO_ROOT_USER               obligatorio, credencial con permiso de administracion
#   MINIO_ROOT_PASSWORD           obligatorio
#   BUCKET_REPOSITORIO_ARTEFACTOS opcional, por defecto $BUCKET o repositorio-artefactos
#   BUCKET_ARTEFACTOS             opcional, por defecto artefactos
#   MINIO_DIR                     opcional, directorio de los JSON de ciclo de vida (por defecto, el del
#                                 propio script)
#
# Uso:  MINIO_ENDPOINT=... MINIO_ROOT_USER=... MINIO_ROOT_PASSWORD=... sh minio/configurar.sh

set -eu

BUCKET_REPOSITORIO_ARTEFACTOS="${BUCKET_REPOSITORIO_ARTEFACTOS:-${BUCKET:-repositorio-artefactos}}"
BUCKET_ARTEFACTOS="${BUCKET_ARTEFACTOS:-artefactos}"
MINIO_DIR="${MINIO_DIR:-$(dirname "$0")}"
CICLO_VIDA_REPOSITORIO_ARTEFACTOS="$MINIO_DIR/ciclo-vida-repositorio-artefactos.json"
CICLO_VIDA_ARTEFACTOS="$MINIO_DIR/ciclo-vida-artefactos.json"

if [ -z "${MINIO_ENDPOINT:-}" ] || [ -z "${MINIO_ROOT_USER:-}" ] || [ -z "${MINIO_ROOT_PASSWORD:-}" ]; then
    echo "ERROR: MINIO_ENDPOINT, MINIO_ROOT_USER y MINIO_ROOT_PASSWORD son obligatorios. Sin ellos el" >&2
    echo "       bucket podria configurarse en el servidor equivocado, y ese error es silencioso." >&2
    exit 1
fi

if ! command -v mcli > /dev/null 2>&1; then
    echo "ERROR: mcli no esta en el PATH. En local:  docker compose up minio-configurador" >&2
    exit 1
fi

for ciclo_vida in "$CICLO_VIDA_REPOSITORIO_ARTEFACTOS" "$CICLO_VIDA_ARTEFACTOS"; do
    if [ ! -f "$ciclo_vida" ]; then
        echo "ERROR: no existe $ciclo_vida" >&2
        exit 1
    fi
done

mcli alias set local "$MINIO_ENDPOINT" "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" > /dev/null

configurar_bucket() {
    bucket="$1"
    ciclo_vida="$2"

    # Privado por defecto: ningun objeto se sirve sin credenciales ni formulario firmado.
    mcli mb --ignore-existing "local/$bucket"

    mcli ilm rule import "local/$bucket" < "$ciclo_vida"

    echo "Bucket '$bucket' listo. Reglas de ciclo de vida:"
    mcli ilm rule ls "local/$bucket"
}

configurar_bucket "$BUCKET_REPOSITORIO_ARTEFACTOS" "$CICLO_VIDA_REPOSITORIO_ARTEFACTOS"
configurar_bucket "$BUCKET_ARTEFACTOS" "$CICLO_VIDA_ARTEFACTOS"
