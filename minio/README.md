# MinIO — configuración del bucket de plantillas

`configurar.sh` deja listo el bucket `repositorio-artefactos` (HU-275): lo crea si no existe, privado,
y aplica `ciclo-vida-repositorio-artefactos.json`, que expira a 1 día todo lo que quede bajo
`pendientes/` (cargas que nunca se confirmaron o que se rechazaron). `versiones/` no expira.

Es idempotente y los buckets los crea este script, nunca el backend.

## Local

`docker compose up` lo ejecuta solo, con el servicio `minio-configurador`, antes de levantar el backend
(`service_completed_successfully`).

## Contra la nube (suelto)

```bash
MSYS_NO_PATHCONV=1 docker run --rm -v "$(pwd)/minio:/minio:ro" \
  -e MINIO_ENDPOINT=https://s3.<dominio> \
  -e MINIO_ROOT_USER=... -e MINIO_ROOT_PASSWORD=... \
  --entrypoint /bin/sh pgsty/minio:RELEASE.2026-04-17T00-00-00Z /minio/configurar.sh
```

Usa la misma imagen que `minio` (trae `mcli`) y requiere credenciales con permiso de administración,
no las del backend. En la nube el `minio-init` de `arquisoft-infra` debe hacer lo mismo
(`mcli mb --ignore-existing` + `mcli ilm rule import` con este JSON).

## Qué queda fuera

El versionado de bucket y la papelera, y la cuenta de servicio de mínimo privilegio, son otra HU. La
cuenta del backend en la nube solo tiene `GetObject`, `PutObject`, `DeleteObject` y `GetBucketLocation`
sobre `repositorio-artefactos/*`: el código no puede listar ni consultar si el bucket existe.
