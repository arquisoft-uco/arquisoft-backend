# Despliegue local con Docker

## 0. Preparar el archivo de variables de entorno

Copiar la plantilla y completar con los valores reales:

```bash
cp .env.example .env
```

El archivo `.env` nunca se sube al repositorio (está en `.gitignore`).

## 1. Generar imagen

```bash
docker build -t arquisoft-backend:local .
```

La imagen es **agnóstica al perfil** — el mismo artefacto sirve para dev y prod.
El perfil se inyecta en tiempo de ejecución en el archivo `.env` (`SPRING_PROFILES_ACTIVE`).

## 2. Ejecutar contenedor

```bash
docker run -d --rm \
  --name arquisoft-backend \
  --network host \
  --env-file .env \
  arquisoft-backend:local
```

El perfil activo (`dev` o `prod`) se controla con la variable `SPRING_PROFILES_ACTIVE` dentro de `.env`.

| Valor | Comportamiento |
|---|---|
| `dev` | Logs DEBUG, SQL visible, defaults tolerantes a variables faltantes |
| `prod` | Rate limiting (60 req/min), Swagger deshabilitado, todas las variables son obligatorias |

## 3. Verificar que está corriendo

```bash
docker ps --filter "name=arquisoft-backend"
```

```bash
curl http://localhost:8080/api/actuator/health
```

Respuesta esperada: `"status":"UP"`

## 4. Detener el contenedor

```bash
docker stop arquisoft-backend
```

---

## 5. Límites de recursos en producción (Coolify)

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
