# Ejecución local con Gradle

## Prerrequisitos

- Java 21 instalado
- Archivo `.env` en la raíz del proyecto (copiar desde `.env.example`)

## Pasos

**1. Dar permisos al wrapper (solo la primera vez)**

```bash
chmod +x gradlew
```

**2. Levantar infraestructura**

```bash
docker-compose up postgres rabbitmq redis keycloak
```

El script `init-db.sql` crea automáticamente todas las bases de datos al iniciar el contenedor de Postgres. Cada contexto tiene su propia BD — no existe una BD centralizada para el Outbox Pattern.

**3. Cargar el catálogo de mensajes en Redis**

```bash
REDIS_HOST=localhost REDIS_PORT=6379 REDIS_PASSWORD=default123 sh catalogo/cargar.sh
```

No es opcional. La aplicación valida al arrancar que **todas** las claves que declara existan en Redis, y si falta una sola no levanta (ADR-013 v1.1). El script es idempotente: re-ejecutarlo no tiene efecto secundario.

Si haces un `FLUSHDB` manual sobre esa instancia, **recarga el catálogo antes de reiniciar el backend** o no arrancará. Por la misma razón el script nunca hace `FLUSHDB` él mismo: esa base comparte espacio con los buckets de rate limit y los tokens invalidados de `seguridad`.

Con `docker-compose up` (todo el stack) este paso no hace falta: el servicio `catalogo-loader` lo ejecuta y el backend espera a que termine con éxito.

**4. Correr el proyecto**

```bash
./gradlew bootRun
```

Spring Boot carga automáticamente el `.env` al arrancar. El perfil activo se define dentro del archivo con `spring.profiles.active`.

La aplicación queda disponible en `http://localhost:8080/api`.

## Variables de entorno requeridas

Copia `.env.example` a `.env`. Las variables clave son:

| Variable | Descripción |
|---|---|
| `DB_SEGURIDAD_URL` | JDBC URL de la BD del contexto seguridad |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | Conexión al broker |
| `KEYCLOAK_URL` / `KEYCLOAK_REALM` | Servidor de autenticación |
