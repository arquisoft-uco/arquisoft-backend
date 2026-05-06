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
