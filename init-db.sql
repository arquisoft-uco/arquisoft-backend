-- ==================== CREAR SCHEMAS PARA CADA CONTEXTO ====================

-- Contexto 1: Seguridad (Usuarios, Roles, Permisos)
CREATE SCHEMA IF NOT EXISTS seguridad;
GRANT ALL PRIVILEGES ON SCHEMA seguridad TO arquisoft;

-- Contexto 2: Fichas
CREATE SCHEMA IF NOT EXISTS fichas;
GRANT ALL PRIVILEGES ON SCHEMA fichas TO arquisoft;

-- Contexto 3: Proyectos
CREATE SCHEMA IF NOT EXISTS proyectos;
GRANT ALL PRIVILEGES ON SCHEMA proyectos TO arquisoft;

-- Contexto 4: Artefactos
CREATE SCHEMA IF NOT EXISTS artefactos;
GRANT ALL PRIVILEGES ON SCHEMA artefactos TO arquisoft;

-- Contexto 5: Repositorio de Artefactos
CREATE SCHEMA IF NOT EXISTS repositorio_artefactos;
GRANT ALL PRIVILEGES ON SCHEMA repositorio_artefactos TO arquisoft;

-- Contexto 6: Entregables
CREATE SCHEMA IF NOT EXISTS entregables;
GRANT ALL PRIVILEGES ON SCHEMA entregables TO arquisoft;

-- Contexto 7: Evaluaciones
CREATE SCHEMA IF NOT EXISTS evaluaciones;
GRANT ALL PRIVILEGES ON SCHEMA evaluaciones TO arquisoft;

-- ==================== CREAR USUARIO PARA KEYCLOAK ====================

CREATE USER IF NOT EXISTS keycloak WITH PASSWORD 'keycloak123';
CREATE DATABASE keycloak OWNER keycloak;

-- ==================== PERMISOS FINALES ====================

GRANT CONNECT ON DATABASE arquisoft TO arquisoft;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO arquisoft;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO arquisoft;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO arquisoft;

-- Hacer lo mismo para cada schema
DO $$
DECLARE
  schema_name TEXT;
BEGIN
  FOR schema_name IN 
    SELECT unnest(ARRAY['seguridad', 'fichas', 'proyectos', 'artefactos', 
                        'repositorio_artefactos', 'entregables', 'evaluaciones'])
  LOOP
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON TABLES TO arquisoft', schema_name);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON SEQUENCES TO arquisoft', schema_name);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON FUNCTIONS TO arquisoft', schema_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA %I TO arquisoft', schema_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA %I TO arquisoft', schema_name);
  END LOOP;
END $$;
