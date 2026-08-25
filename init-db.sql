-- =============================================================================
-- init-db.sql
-- Inicialización de bases de datos Arquisoft — Múltiples DataSources
--
-- Ejecutado por el entrypoint de Postgres (docker-entrypoint-initdb.d) UNA sola
-- vez, cuando el volumen de datos está vacío. Editar este archivo no tiene
-- efecto sobre un contenedor ya inicializado: hay que borrar el volumen.
--
-- El entrypoint lo corre con psql, por eso `\c` está disponible. Es necesario:
-- GRANT y ALTER SCHEMA aplican a la base de datos conectada, y sin cambiar de
-- conexión todos los permisos recaerían sobre la base de mantenimiento.
-- =============================================================================

-- ==================== USUARIOS DE APLICACIÓN ====================

DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'arquisoft_user') THEN
    CREATE USER arquisoft_user WITH PASSWORD 'arquisoft123';
END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'arquisoft') THEN
    CREATE USER arquisoft WITH PASSWORD 'arquisoft123';
END IF;
END $$;

-- ==================== BASES DE DATOS POR BOUNDED CONTEXT ====================
-- El contexto `seguridad` no aparece: se apoya en Keycloak + Redis, sin BD propia.

CREATE DATABASE usuarios OWNER arquisoft_user;
CREATE DATABASE fichas_perfil OWNER arquisoft_user;
CREATE DATABASE artefactos OWNER arquisoft_user;
CREATE DATABASE repositorio_artefactos OWNER arquisoft_user;
CREATE DATABASE proyectos_grado OWNER arquisoft_user;
CREATE DATABASE entregables OWNER arquisoft_user;
CREATE DATABASE evaluaciones OWNER arquisoft_user;
CREATE DATABASE notificaciones OWNER arquisoft_user;

-- ==================== BASE DE DATOS KEYCLOAK ====================

CREATE DATABASE keycloak OWNER arquisoft;

-- =============================================================================
-- PERMISOS DE SCHEMA PUBLIC (crítico en PostgreSQL 15+)
--
-- En PG15+ el schema public ya no otorga CREATE a PUBLIC por defecto, lo que
-- rompería las migraciones automáticas de Keycloak y Flyway. Su dueño pasó a ser
-- pg_database_owner, así que el OWNER de cada CREATE DATABASE de arriba ya
-- alcanzaría; estos GRANT lo dejan explícito para no depender de ese default.
--
-- Cada bloque exige su propio `\c`: sin él, los ocho recaerían sobre la misma
-- base y ninguna de las de contexto quedaría con permisos.
-- =============================================================================

\c usuarios
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

\c fichas_perfil
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

\c artefactos
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

\c repositorio_artefactos
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

\c proyectos_grado
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

\c entregables
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

\c evaluaciones
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

\c notificaciones
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

\c keycloak
GRANT ALL ON SCHEMA public TO arquisoft;
ALTER SCHEMA public OWNER TO arquisoft;
