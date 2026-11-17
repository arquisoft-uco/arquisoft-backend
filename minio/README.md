# MinIO — configuración de los buckets

`configurar.sh` deja listos tres buckets, privados y creados si no existen: `repositorio-artefactos` (HU-275,
`ciclo-vida-repositorio-artefactos.json`), `artefactos` (HU-043, `ciclo-vida-artefactos.json`) y `entregables`
(HT-023). Los dos primeros tienen un ciclo de vida que expira a 1 día todo lo que quede bajo `pendientes/` (cargas
que nunca se confirmaron o que se rechazaron); `versiones/` no expira. `entregables` no lleva ciclo de vida: el
contexto `entregables` guarda ahí la copia de cada artefacto cerrado bajo `artefactos/` y el ZIP congelado de cada
entregable bajo `paquetes/`, y nada queda pendiente de confirmar. Biblioteca lo consumirá después (solo lectura de
`paquetes/`). Los nombres salen de `BUCKET_REPOSITORIO_ARTEFACTOS`, `BUCKET_ARTEFACTOS` y `BUCKET_ENTREGABLES`.

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
(`mcli mb --ignore-existing` para los tres buckets y `mcli ilm rule import` con cada JSON en los dos que lo llevan).

## Qué queda fuera

El versionado de bucket y la papelera, y la cuenta de servicio de mínimo privilegio, son otra HU. La
cuenta del backend en la nube solo tiene `GetObject`, `PutObject`, `DeleteObject` y `GetBucketLocation`
sobre `repositorio-artefactos/*` y `artefactos/*`; sobre `entregables/*` necesita `GetObject`, `PutObject` y
`DeleteObject` (en `artefactos/*` el contexto `entregables` solo lee). El código no puede listar ni consultar si
el bucket existe.
