# Sincronización de réplicas entre bases (HT-027)

Cada contexto guarda réplicas locales de datos que son de otro contexto y las mantiene con eventos de RabbitMQ.
Si un evento no llegó, la réplica queda incompleta. Estas funciones de Postgres reconcilian las 30 réplicas contra
su dueño leyendo directo de su base con `postgres_fdw`, sin pasar por la aplicación ni por RabbitMQ.

- Lo que falta en la réplica se crea; si el dueño lo tiene dado de baja, se inserta ya anulado (lápida).
- Lo que existe se actualiza solo si el origen es más nuevo (`ocurrido_en`) y cambió algún dato básico.
- Nunca hay `DELETE`. Las columnas locales (`artefactos.proyecto_grado.entregado_en`, los `id` de los vínculos) no se tocan.
- No emite eventos ni notificaciones. Devuelve solo conteos, nunca filas (nombre, correo e identificador son datos personales).

Fuera de alcance: `@Scheduled` y endpoint de administrador (requieren su recurso PDP y client role, otra HT),
las réplicas derivadas `entregables.artefacto` y `biblioteca.entregable_proyecto_grado`, y
`entregables.paquete_entregable` (dejó de ser réplica en HT-023). Nada de esto pasa por Flyway.

## Instalar (una vez por entorno)

Lo ejecuta un administrador de base de datos con el superusuario `postgres`, **después** de que la aplicación haya
arrancado y Flyway haya creado las tablas. Se repite al editar un `{contexto}.sql` o al añadir tablas a un origen.

```bash
export SYNC_LECTOR_PASSWORD='...'        # contraseña del rol sync_lector; nunca va al repo
export SYNC_HOST=arquisoft-postgres      # o el alias `postgres` o la IP 172.16.1.10; NUNCA loopback
export SYNC_PSQL='docker exec -i arquisoft-postgres psql -U postgres -X -q -v ON_ERROR_STOP=1'
scripts/sincronizacion/preparar/preparar.sh
```

| Variable | Obligatoria | Por defecto |
|---|---|---|
| `SYNC_LECTOR_PASSWORD` | sí | sin comilla simple, barra invertida ni saltos de línea |
| `SYNC_HOST` | sí | `arquisoft-postgres`, el alias `postgres` o la IP estática `172.16.1.10`; nunca loopback |
| `SYNC_PORT` | no | `5432` |
| `SYNC_PSQL` | no | `psql -U postgres -X -q -v ON_ERROR_STOP=1` |
| `SYNC_DESTINOS` | no | todos; lista de contextos separados por espacio o coma (`fichas,proyectos`) |

`preparar.sh` crea el rol `sync_lector` (solo `SELECT` sobre las tablas de la matriz), un `SERVER sync_{origen}` y un
esquema `sync_{origen}` por origen en cada base destino, instala la función de cada contexto y da permisos a
`arquisoft_user`. En cada ejecución recrea servidores y esquemas foráneos, así que es idempotente.

**El error de loopback.** Producción y la imagen oficial aplican `trust` a socket, `127.0.0.1` y `::1`, y
`scram-sha-256` al resto. `postgres_fdw` exige contraseña efectiva a un rol no superusuario; con `localhost` la
conexión entra por `trust` y falla con `password or GSSAPI delegated credentials required`. Por eso `SYNC_HOST` debe
ser el alias de red y `preparar.sh` rechaza `localhost`, `127.*`, `::1` y sockets.

`destino.sql` crea y borra un `USER MAPPING FOR CURRENT_USER` solo durante el `IMPORT FOREIGN SCHEMA`, porque el
import corre como `postgres`. Si el import falla, ese mapeo queda hasta la siguiente ejecución de `preparar.sh`, que
recrea el servidor y lo elimina.

Reinicializar o eliminar el volumen `arquisoft-postgres-data` pierde lo instalado (roles, servidores, esquemas y
funciones) y exige repetir `preparar.sh`.

La matriz de qué origen importa cada destino vive solo en `preparar.sh`. Para el paso a paso operativo, ver
[GUIA.md](GUIA.md).

## Ejecutar

```sql
SELECT * FROM sync.sincronizar_fichas();
SELECT * FROM sync.sincronizar_proyectos();
SELECT * FROM sync.sincronizar_evaluaciones();
SELECT * FROM sync.sincronizar_fichas('estudiante');                       -- una sola réplica
SELECT * FROM sync.sincronizar_artefactos('estudiante_proyecto_grado', 60, false);
```

```bash
docker exec -i arquisoft-postgres psql -U arquisoft_user -d fichas_perfil -c "SELECT * FROM sync.sincronizar_fichas()"
```

Firma común: `sync.sincronizar_{contexto}(p_replica text DEFAULT NULL, p_margen_segundos integer DEFAULT 60,
p_cerrar_ausentes boolean DEFAULT true)`. Se invoca igual por JDBC (sin metacomandos ni variables de psql).

- `p_replica`: nombre de la tabla destino; un nombre desconocido falla con la lista válida.
- `p_margen_segundos`: el corte es `transaction_timestamp() - margen`; es el `ocurrido_en` de toda fila cuyo origen no
  guarda fecha. Un evento en vivo más reciente que el corte no se pisa.
- `p_cerrar_ausentes`: solo afecta a los vínculos estudiante-proyecto (`estudiante_proyecto_grado` en `artefactos`,
  `entregables`, `evaluaciones` y `biblioteca`). Con `true`, el vínculo vigente que `proyectos` ya borró recibe lápida,
  siempre que su proyecto aún exista en el origen; con `false` queda intacto.

Contextos y réplicas (la base de `fichas` es `fichas_perfil` y la de `proyectos` es `proyectos_grado`):

| Función | Réplicas |
|---|---|
| `sincronizar_fichas` | `estudiante`, `asesor_ficha`, `representante_comite_curriculum` |
| `sincronizar_proyectos` | `coordinador`, `asesor`, `estudiante`, `entregable_proyecto_grado` |
| `sincronizar_artefactos` | `asesor`, `estudiante`, `version_repositorio_artefacto`, `proyecto_grado`, `estudiante_proyecto_grado`, `asesor_proyecto_grado` |
| `sincronizar_entregables` | `asesor`, `proyecto_grado`, `estudiante_proyecto_grado`, `asesor_proyecto_grado` |
| `sincronizar_evaluaciones` | `jurado`, `asesor`, `estudiante_proyecto_grado`, `coordinador_proyecto_grado`, `entregable` |
| `sincronizar_solicitudes` | `usuario` |
| `sincronizar_biblioteca` | `bibliotecario`, `autor`, `proyecto_grado`, `estudiante_proyecto_grado` |
| `sincronizar_repositorio_artefactos` | `administrador` |
| `sincronizar_mapas_ruta` | `asesor`, `proyecto_grado` |

**Orden entre contextos:** ejecuta `sync.sincronizar_proyectos()` antes que `sync.sincronizar_evaluaciones()`. Las
réplicas `estudiante_proyecto_grado` y `coordinador_proyecto_grado` de `evaluaciones` leen el contacto de las réplicas
`estudiante` y `coordinador` de `proyectos`, como lo publica `proyectos` en sus eventos. El resto no depende del orden.

Para que `postgres_fdw` use una sola instantánea remota entre réplicas, llama dentro de
`BEGIN ISOLATION LEVEL REPEATABLE READ; ...; COMMIT;`. El margen cubre la diferencia entre el inicio de la transacción
y la primera lectura.

### Leer el resultado

Una fila por réplica: `replica, creadas, actualizadas, omitidas, cerradas`.

- `creadas`: filas nuevas (incluye lápidas insertadas).
- `actualizadas`: filas existentes con datos básicos distintos y `ocurrido_en` anterior al del origen, o reactivadas.
- `omitidas`: candidatas sin cambio, protegidas por un `ocurrido_en` posterior al corte, o sin padre local (FK).
- `cerradas`: vínculos con lápida por `p_cerrar_ausentes`.

Una segunda llamada sin cambios en el origen devuelve `creadas = actualizadas = cerradas = 0`. Una llamada es una
transacción: si una réplica falla se revierte todo, y no corren dos llamadas a la vez de la misma función
(`pg_advisory_xact_lock`).

`mapas_ruta.asesor` no tiene `ocurrido_en` ni consumidor, así que se actualiza sin guarda; cuando una HU se lo agregue,
su bloque debe pasar al patrón de las demás réplicas de rol.

## Pruebas

```bash
scripts/sincronizacion/pruebas/probar.sh
```

Levanta `postgres:18-alpine` en una red docker, crea las bases con `init-db.sql`, aplica las migraciones reales de cada
contexto, corre `preparar.sh` con la regla de autenticación por defecto de la imagen (idéntica a producción) y ejecuta
las pruebas. Borra contenedor y red al terminar (`SYNC_PRUEBA_CONSERVAR=1` los conserva) y sale con código distinto de
0 si algo falla. Requiere docker; no forma parte de `./gradlew check`.
