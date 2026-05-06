# Despliegue local con Docker

## 1. Generar imagen

```bash
docker build -t arquisoft-backend:local .
```

La imagen es **agnóstica al perfil** — el mismo artefacto sirve para dev y prod.
El perfil se inyecta en tiempo de ejecución con `-e SPRING_PROFILES_ACTIVE`.

## 2. Ejecutar contenedor

### Perfil dev (desarrollo local)

```bash
docker run -d --rm \
  --name arquisoft-backend \
  --network host \
  -e SPRING_PROFILES_ACTIVE=dev \
  -v $(pwd)/.env.properties:/app/.env.properties:ro \
  arquisoft-backend:local
```

Activa: logs DEBUG, SQL visible, valores con defaults tolerantes a variables faltantes.

### Perfil prod (producción)

```bash
docker run -d --rm \
  --name arquisoft-backend \
  --network host \
  -e SPRING_PROFILES_ACTIVE=prod \
  -v $(pwd)/.env.properties:/app/.env.properties:ro \
  arquisoft-backend:local
```

Activa: rate limiting (60 req/min), Swagger deshabilitado, **todas las variables de entorno son obligatorias** (sin defaults). El arranque falla con error claro si falta alguna.

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
