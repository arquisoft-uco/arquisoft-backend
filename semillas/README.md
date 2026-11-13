# Semillas de desarrollo

Datos de prueba para dev. **No son parte del despliegue**: no están en el classpath, Flyway no las
ve, el backend no las carga y los tests tampoco. Se corren a mano, y nunca contra prod.

| Contexto | Archivo | Qué siembra |
|---|---|---|
| `biblioteca` | `biblioteca.sql` | 1 bibliotecario, 4 entregables, 3 autores, 4 documentaciones (una por estado más una `PUBLICADO` extra) y sus vínculos en `autor_documentacion` |

Cada archivo es idempotente (`INSERT ... ON CONFLICT DO NOTHING`, UUID fijos) y exige que la
migración del contexto ya haya corrido: arranca el backend una vez antes.

## Correrlas

Con el `docker-compose` local (tras el primer arranque del backend):

```bash
docker compose --profile semillas run --rm semillas-loader
```

Contra otra base, con el script suelto (las variables `PG*` no tienen valor por defecto):

```bash
PGHOST=localhost PGPORT=5432 PGUSER=postgres PGPASSWORD=... PGDATABASE=biblioteca sh semillas/cargar.sh
```

## Agregar la semilla de otro contexto

1. Crea `semillas/{contexto}.sql`, idempotente y con la cabecera "Solo dev; no ejecutar en prod".
2. Agrégala a `cargar.sh` (hoy carga solo `biblioteca.sql`) y, si la base no es `biblioteca`,
   pasa `PGDATABASE`.
