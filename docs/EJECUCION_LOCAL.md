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

El script `init-db.sql` crea automáticamente todas las bases de datos al iniciar el contenedor de Postgres, incluyendo `arquisoft_events` (usada por Spring Modulith para el Outbox Pattern).

**3. Correr el proyecto**

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
| `DB_ARQUISOFT_EVENTS_URL` | JDBC URL de `arquisoft_events` (Outbox Pattern) |
| `DB_ARQUISOFT_EVENTS_USERNAME` | Usuario de `arquisoft_events` |
| `DB_ARQUISOFT_EVENTS_PASSWORD` | Contraseña de `arquisoft_events` |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | Conexión al broker |
| `KEYCLOAK_URL` / `KEYCLOAK_REALM` | Servidor de autenticación |
