-- =============================================================================
-- init-db.sql
-- Inicialización de la base de datos Arquisoft — FASE 1 (MVP)
--
-- Estrategia: una sola base de datos "arquisoft" con 7 schemas, uno por
-- bounded context. Sin foreign keys cruzados entre schemas.
-- Comunicación entre contextos exclusivamente via RabbitMQ.
--
-- Fuente: ADR-002 (base-datos-postgresql) — sección "Fase 1 MVP"
--         ADR-003 (autenticacion-keycloak)
-- =============================================================================

-- ==================== CREAR SCHEMAS — 7 CONTEXTOS FASE 1 ====================

-- Contexto 1: Usuarios (seguridad e identidad)
CREATE SCHEMA IF NOT EXISTS usuarios;
GRANT ALL PRIVILEGES ON SCHEMA usuarios TO arquisoft;

-- Contexto 2: Fichas de Perfil de Trabajo de Grado
CREATE SCHEMA IF NOT EXISTS fichas_perfil;
GRANT ALL PRIVILEGES ON SCHEMA fichas_perfil TO arquisoft;

-- Contexto 3: Artefactos (documentos del proyecto)
CREATE SCHEMA IF NOT EXISTS artefactos;
GRANT ALL PRIVILEGES ON SCHEMA artefactos TO arquisoft;

-- Contexto 4: Repositorio de Artefactos (plantillas institucionales)
CREATE SCHEMA IF NOT EXISTS repositorio_artefactos;
GRANT ALL PRIVILEGES ON SCHEMA repositorio_artefactos TO arquisoft;

-- Contexto 5: Proyectos de Grado
CREATE SCHEMA IF NOT EXISTS proyectos_grado;
GRANT ALL PRIVILEGES ON SCHEMA proyectos_grado TO arquisoft;

-- Contexto 6: Entregables de Proyectos de Grado
CREATE SCHEMA IF NOT EXISTS entregables;
GRANT ALL PRIVILEGES ON SCHEMA entregables TO arquisoft;

-- Contexto 7: Evaluaciones Definitivas
CREATE SCHEMA IF NOT EXISTS evaluaciones;
GRANT ALL PRIVILEGES ON SCHEMA evaluaciones TO arquisoft;

-- ==================== BASE DE DATOS KEYCLOAK (ADR-003) ====================

DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'keycloak') THEN
    CREATE USER keycloak WITH PASSWORD 'keycloak123';
  END IF;
END $$;
CREATE DATABASE keycloak OWNER keycloak;

-- ==================== PERMISOS FINALES ====================

GRANT CONNECT ON DATABASE arquisoft TO arquisoft;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO arquisoft;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO arquisoft;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO arquisoft;

-- Aplicar permisos sobre los 7 schemas de Fase 1
DO $$
DECLARE
  schema_name TEXT;
BEGIN
  FOR schema_name IN
    SELECT unnest(ARRAY['usuarios', 'fichas_perfil', 'artefactos',
                        'repositorio_artefactos', 'proyectos_grado',
                        'entregables', 'evaluaciones'])
  LOOP
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON TABLES TO arquisoft', schema_name);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON SEQUENCES TO arquisoft', schema_name);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON FUNCTIONS TO arquisoft', schema_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA %I TO arquisoft', schema_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA %I TO arquisoft', schema_name);
  END LOOP;
END $$;
