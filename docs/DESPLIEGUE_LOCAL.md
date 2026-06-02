# Despliegue local con Docker

## 0. Preparar el archivo de variables de entorno

Copiar la plantilla y completar con los valores reales:

```bash
cp .env.example .env
```

Abrir `.env` y verificar la sección **CONFIGURACIÓN DE CONEXIÓN**. Elegir según plataforma:

| Plataforma | Host para servicios (DB, RabbitMQ, Redis, Keycloak) | Flag de red |
|---|---|---|
| **Linux** | `localhost` | `--network host` |
| **Windows / macOS** (Docker Desktop) | `host.docker.internal` | `-p 8080:8080` |

> En `.env.example` la opción por defecto activa es la de **Windows/macOS**.
> Para Linux, comentar las líneas de `host.docker.internal` y descomentar las de `localhost`.

El archivo `.env` nunca se sube al repositorio (está en `.gitignore`).

---

## 1. Generar imagen

```bash
docker build -t arquisoft-backend:local .
```

La imagen es **agnóstica al perfil** — el mismo artefacto sirve para dev y prod.
El perfil se inyecta en tiempo de ejecución con `SPRING_PROFILES_ACTIVE` en `.env`.

---

## 2. Ejecutar contenedor

### Linux

```bash
docker run -d --rm \
  --name arquisoft-backend \
  --network host \
  --env-file .env \
  arquisoft-backend:local
```

`--network host` comparte la red del host con el contenedor. `localhost` dentro
del contenedor equivale a `localhost` del host.

### Windows / macOS (Docker Desktop)

```bash
docker run -d --rm \
  --name arquisoft-backend \
  -p 8080:8080 \
  --env-file .env \
  arquisoft-backend:local
```

Docker Desktop corre dentro de una VM. `--network host` no funciona igual que
en Linux. Por eso se usa `-p 8080:8080` para exponer el puerto y
`host.docker.internal` en `.env` para que el contenedor alcance los servicios
del host.

---

## 3. Perfil activo (dev vs prod)

Se controla con `SPRING_PROFILES_ACTIVE` en el archivo `.env`.

| Valor | Comportamiento |
|---|---|
| `dev` | Rate limiting deshabilitado o tolerante, Swagger habilitado, defaults flexibles |
| `prod` | Rate limiting (60 req/min global, 3 req/min login), Swagger deshabilitado, variables obligatorias |

---

## 4. Verificar que está corriendo

```bash
docker ps --filter "name=arquisoft-backend"
```

```bash
curl http://localhost:8080/api/actuator/health
```

Respuesta esperada: `"status":"UP"`

Swagger UI (solo perfil `dev`):
```
http://localhost:8080/api/swagger-ui/index.html
```

---

## 5. Detener el contenedor

```bash
docker stop arquisoft-backend
```

---

## 6. Límites de recursos en producción (Coolify)

> Esta sección aplica únicamente al despliegue en producción via Coolify.
> En local (`dev`) no es necesario configurar límites.

### Por qué son necesarios

El servidor de 4GB aloja múltiples servicios (Coolify, Keycloak, Grafana, Prometheus, etc.).
Sin límites, un redespliegue puede agotar la RAM mientras el contenedor nuevo y el viejo coexisten,
causando que `kswapd0` sature la CPU al 100% intentando liberar memoria.

### Configuración en Coolify → Resource Limits

| Campo | Valor recomendado | Descripción |
|---|---|---|
| **Number of CPUs** | `1.5` | Evita saturar ambos vCPU durante el arranque del JVM |
| **Soft Memory Limit** | `500` | El kernel intenta mantener el contenedor bajo este umbral si hay presión |
| **Swappiness** | `0` | El JVM nunca debe usar swap; si se queda sin RAM es mejor reinicio rápido que lentitud por disco |
| **Maximum Memory Limit** | `700` | Techo duro — el kernel reinicia el contenedor si lo supera |
| **Maximum Swap Limit** | `700` | Igual al máximo → sin swap para este contenedor |

### Variable de entorno JVM (Coolify → Environment Variables)

Configura también en **Environment Variables** del servicio (Runtime ON, Build OFF):

```
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=70.0 -XX:InitialRAMPercentage=40.0
```

Java 21 es _container-aware_: lee el `Maximum Memory Limit` de Docker y calcula el heap como porcentaje de ese valor.

| Porcentaje | Con límite 700MB | Propósito |
|---|---|---|
| `MaxRAMPercentage=70` | Heap máximo ≈ 490MB | Deja 210MB para metaspace, JIT, Virtual Threads y buffers de red |
| `InitialRAMPercentage=40` | Heap inicial ≈ 280MB | Evita que la JVM reserve toda la memoria al arrancar |

> **No usar `-Xmx` hardcodeado.** El enfoque de porcentajes escala automáticamente si se cambia
> el límite del contenedor sin necesidad de modificar variables.

### Swap del servidor (no del contenedor)

Si el servidor no tiene swap configurado, agrégalo una vez de forma permanente:

```bash
fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
```

El swap del servidor actúa como red de seguridad para el OS y otros servicios,
independientemente del `Swappiness=0` del contenedor del backend.

### Limpieza de imágenes acumuladas

Cada redespliegue genera una nueva imagen Docker. Sin limpieza periódica el disco se llena.
Ejecutar manualmente cuando sea necesario:

```bash
# Elimina imágenes no usadas por ningún contenedor activo
docker image prune -a --filter "until=24h"

# Ver espacio ocupado por Docker
docker system df
```

---

## 7. Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| `connect ECONNREFUSED 127.0.0.1:8080` desde Windows | `--network host` no funciona en Docker Desktop | Usar `-p 8080:8080` y `host.docker.internal` en `.env` |
| `Migration checksum mismatch` | Una migración Flyway se modificó después de aplicarse | Reparar con `flyway repair` o recrear la DB afectada |
| `requests-per-minute debe ser >= 1` al arrancar | No hay perfil activo y no se definieron defaults de rate-limit | Agregar `SPRING_PROFILES_ACTIVE=dev` al `.env` |
| `./gradlew: not found` durante `docker build` en Windows | `gradlew` tiene finales de línea CRLF | El Dockerfile ya incluye `sed -i 's/\r$//' gradlew` para corregirlo automáticamente |
