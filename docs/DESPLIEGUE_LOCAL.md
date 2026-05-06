# Despliegue local con Docker

## 1. Generar imagen

```bash
docker build -t arquisoft-backend:local .
```

## 2. Ejecutar contenedor

```bash
docker run --rm \
  --name arquisoft-backend \
  --network host \
  -v $(pwd)/.env.properties:/app/.env.properties:ro \
  arquisoft-backend:local
```

## 3. Verificar que está corriendo

```bash
curl http://localhost:8080/api/actuator/health
```

Respuesta esperada: `"status":"UP"`

## 4. Detener el contenedor

```bash
docker stop arquisoft-backend
```
