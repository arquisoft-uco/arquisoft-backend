# Guía rápida: instalar y ejecutar la sincronización de réplicas (HT-027)

Resumen: se instala una vez por entorno con `preparar.sh` y después se ejecutan las funciones `sync.sincronizar_*`
desde DBeaver (o psql) conectado como `arquisoft_user`.

## 0. Requisitos antes de empezar

| Requisito | Para qué |
|---|---|
| Postgres 18 en el contenedor `arquisoft-postgres`, desplegado por `arquisoft-infra` | Aloja todas las bases de contexto |
| La aplicación arrancó al menos una vez (Flyway ya creó las tablas) | Las funciones leen y escriben tablas que deben existir |
| Acceso SSH al servidor (`ssh oracle`) y Docker en él | Se ejecuta con `docker exec` como superusuario `postgres` |
| Bash en tu máquina y este repo clonado | `preparar.sh` corre en tu máquina y envía el SQL por SSH |
| Una contraseña nueva para el rol `sync_lector` | Sin comilla simple, barra invertida ni saltos de línea |
| DBeaver con una conexión por base destino, con usuario `arquisoft_user` | Para ejecutar las funciones |

No se modifica `arquisoft-infra` ni Terraform: `postgres_fdw` ya viene en la imagen oficial y todo lo que se crea
vive en el volumen de datos.

## 1. Instalar (una vez por entorno)

**1.0 Genera la contraseña de `sync_lector`.** Es una contraseña nueva: no existe todavía en ningún sitio y la eliges tú.
`preparar.sh` la usa para crear el rol y para el mapeo de usuario del servidor foráneo.
```bash
openssl rand -hex 24
```
Imprime 48 caracteres hexadecimales aleatorios, por ejemplo `9f2c...`. Es la opción recomendada: al ser solo letras y
dígitos, no choca con las restricciones del script (sin comilla simple, barra invertida ni saltos de línea).

Alternativa si no tienes `openssl`:
```bash
head -c 32 /dev/urandom | base64 | tr -dc 'A-Za-z0-9' | head -c 32
```
Toma bytes aleatorios y se queda con 32 letras y dígitos.

Guárdala en tu gestor de contraseñas, **no en el repo ni en un `.env` versionado**. La necesitarás de nuevo solo si
repites la instalación; si la pierdes, genera otra y vuelve a ejecutar `preparar.sh`, que actualiza el rol (`ALTER ROLE`).
Para no escribirla en el historial de la terminal, genérala directo en la variable:
```bash
export SYNC_LECTOR_PASSWORD="$(openssl rand -hex 24)"
echo "$SYNC_LECTOR_PASSWORD"   # cópiala a tu gestor antes de cerrar la terminal
```
En ese caso, en el paso 1.2 omite la línea que define `SYNC_LECTOR_PASSWORD`.

**1.1 Verifica que la extensión está disponible** (solo lectura).
```bash
ssh oracle 'docker exec arquisoft-postgres psql -U postgres -Atc "select name from pg_available_extensions where name = '"'"'postgres_fdw'"'"'"'
```
Debe imprimir `postgres_fdw`. Confirma que la imagen incluye la extensión.

**1.2 Define las variables** en tu terminal.
```bash
export SYNC_LECTOR_PASSWORD='cambia-esta-contraseña'
export SYNC_HOST=arquisoft-postgres
export SYNC_PSQL='ssh oracle docker exec -i arquisoft-postgres psql -U postgres -X -q -v ON_ERROR_STOP=1'
```
- `SYNC_LECTOR_PASSWORD`: contraseña con la que se creará el rol de solo lectura `sync_lector`.
- `SYNC_HOST`: nombre con el que el servidor se llama a sí mismo por la red de Docker. Sirven también `postgres` o
  `172.16.1.10`. **Nunca `localhost` ni `127.0.0.1`**: ahí Postgres aplica `trust` y `postgres_fdw` falla.
- `SYNC_PSQL`: cómo enviar el SQL al servidor. El script lee los archivos de tu máquina y los pasa por SSH.

**1.3 (Opcional) prueba con un solo contexto.**
```bash
SYNC_DESTINOS=fichas scripts/sincronizacion/preparar/preparar.sh
```
Instala solo `sync.sincronizar_fichas` y lo que necesita. Sirve para validar antes de instalar todo.

**1.4 Instala todo.**
```bash
scripts/sincronizacion/preparar/preparar.sh
```
Hace, en este orden:
- Crea o actualiza el rol `sync_lector`.
- Le da `SELECT` solo sobre las tablas origen necesarias.
- En cada base destino, crea la extensión, el servidor foráneo, el esquema de tablas importadas y la función.
- Da permisos a `arquisoft_user`.

Se puede repetir sin riesgo; recrea servidores y esquemas foráneos. Repítelo si editas un `{contexto}.sql`.
Termina con `preparar.sh: listo`.

## 2. Ejecutar la sincronización

Conéctate en DBeaver con **`arquisoft_user`** a la base destino (no con `postgres`: el mapeo de usuario se creó para
`arquisoft_user`).

**2.1 Activa el modo manual de commit** (barra de herramientas, "Auto/Manual"). Así puedes revisar y hacer `ROLLBACK`.

**2.2 Ejecuta primero `proyectos`, después el resto.** Dos réplicas de `evaluaciones` leen datos de `proyectos`.

| Orden | Base destino (conexión) | Llamada |
|---|---|---|
| 1 | `proyectos_grado` | `SELECT * FROM sync.sincronizar_proyectos();` |
| 2 | `fichas_perfil` | `SELECT * FROM sync.sincronizar_fichas();` |
| 3 | `artefactos` | `SELECT * FROM sync.sincronizar_artefactos();` |
| 4 | `entregables` | `SELECT * FROM sync.sincronizar_entregables();` |
| 5 | `evaluaciones` | `SELECT * FROM sync.sincronizar_evaluaciones();` |
| 6 | `solicitudes` | `SELECT * FROM sync.sincronizar_solicitudes();` |
| 7 | `biblioteca` | `SELECT * FROM sync.sincronizar_biblioteca();` |
| 8 | `repositorio_artefactos` | `SELECT * FROM sync.sincronizar_repositorio_artefactos();` |
| 9 | `mapas_ruta` | `SELECT * FROM sync.sincronizar_mapas_ruta();` |

Cada llamada es una transacción: si una réplica falla, se revierte todo lo de esa llamada.

**2.3 Revisa el resultado.** Devuelve una fila por réplica con `replica, creadas, actualizadas, omitidas, cerradas`.
Si es lo esperado, haz `COMMIT`; si no, `ROLLBACK`.

**2.4 Opcionales.**
```sql
SELECT * FROM sync.sincronizar_fichas('estudiante');                    -- una sola réplica
SELECT * FROM sync.sincronizar_artefactos(NULL, 120, false);            -- margen de 120 s, sin cerrar ausentes
```
- El primer argumento elige una réplica por nombre de tabla.
- El segundo es el margen en segundos para no pisar eventos recientes (60 por defecto).
- El tercero indica si se marcan con lápida los vínculos estudiante-proyecto que ya no existen en el origen.

**2.5 Comprueba la idempotencia.** Una segunda llamada seguida debe devolver `creadas`, `actualizadas` y `cerradas`
en 0 si no cambió nada en el origen.

## 3. Cuándo repetir la instalación

- Editas un `{contexto}.sql` o agregas tablas a un origen.
- Se recrea el volumen `arquisoft-postgres-data` (todo lo instalado se pierde con él).
- Cambias la contraseña de `sync_lector` o la dirección de `SYNC_HOST`.

## 4. Problemas frecuentes

| Síntoma | Causa y solución |
|---|---|
| `password or GSSAPI delegated credentials required` | El servidor foráneo apunta a loopback. Reinstala con `SYNC_HOST=arquisoft-postgres`. |
| `preparar.sh: SYNC_HOST no puede ser loopback` | Usaste `localhost` o `127.*`. Cambia el valor. |
| `permission denied for table` | Corre `preparar.sh` otra vez; faltan permisos de `sync_lector`. |
| `relation "..." does not exist` en el origen | La aplicación no ha creado las tablas. Arranca el backend primero. |
| Función no encontrada | No se instaló ese destino, o estás conectado a otra base. |

## 5. Pruebas locales (opcional)

```bash
scripts/sincronizacion/pruebas/probar.sh
```
Levanta un Postgres temporal en Docker, aplica las migraciones reales, instala y prueba las funciones. No toca tu
servidor. Requiere Docker.
