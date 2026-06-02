-- =============================================================================
-- init-db.sql
-- Inicialización de bases de datos Arquisoft — Múltiples DataSources
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

CREATE DATABASE usuarios OWNER arquisoft_user;
CREATE DATABASE fichas_perfil OWNER arquisoft_user;
CREATE DATABASE artefactos OWNER arquisoft_user;
CREATE DATABASE repositorio_artefactos OWNER arquisoft_user;
CREATE DATABASE proyectos_grado OWNER arquisoft_user;
CREATE DATABASE entregables OWNER arquisoft_user;
CREATE DATABASE evaluaciones OWNER arquisoft_user;

-- ==================== BASE DE DATOS KEYCLOAK ====================

CREATE DATABASE keycloak OWNER arquisoft;

-- =============================================================================
-- PERMISOS DE SCHEMA PUBLIC (crítico en PostgreSQL 15+)
-- En PG15+ el schema public ya no otorga CREATE por defecto,
-- lo que rompe las migraciones automáticas de Keycloak y Flyway/Liquibase.
-- =============================================================================

-- Contexto 1: Seguridad e identidad
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

-- Contexto 2: Fichas de Perfil de Trabajo de Grado
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

-- Contexto 3: Artefactos (documentos del proyecto)
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

-- Contexto 4: Repositorio de Artefactos (plantillas institucionales)
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

-- Contexto 5: Proyectos de Grado
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

-- Contexto 6: Entregables de Proyectos de Grado
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

-- Contexto 7: Evaluaciones Definitivas
GRANT ALL ON SCHEMA public TO arquisoft_user;
ALTER SCHEMA public OWNER TO arquisoft_user;

-- Keycloak: Para Autenticación y Autorización
GRANT ALL ON SCHEMA public TO arquisoft;
ALTER SCHEMA public OWNER TO arquisoft;