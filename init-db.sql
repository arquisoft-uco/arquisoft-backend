-- ==================== CREAR SCHEMAS PARA CADA CONTEXTO ====================

-- Contexto 1: Usuarios
CREATE SCHEMA IF NOT EXISTS usuarios;
GRANT ALL PRIVILEGES ON SCHEMA usuarios TO arquisoft;

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

-- Contexto 6: Mapas de Ruta
CREATE SCHEMA IF NOT EXISTS mapas_ruta;
GRANT ALL PRIVILEGES ON SCHEMA mapas_ruta TO arquisoft;

-- Contexto 7: Biblioteca
CREATE SCHEMA IF NOT EXISTS biblioteca;
GRANT ALL PRIVILEGES ON SCHEMA biblioteca TO arquisoft;

-- Contexto 8: Entregables
CREATE SCHEMA IF NOT EXISTS entregables;
GRANT ALL PRIVILEGES ON SCHEMA entregables TO arquisoft;

-- Contexto 9: Evaluaciones
CREATE SCHEMA IF NOT EXISTS evaluaciones;
GRANT ALL PRIVILEGES ON SCHEMA evaluaciones TO arquisoft;

-- Contexto 10: Solicitudes
CREATE SCHEMA IF NOT EXISTS solicitudes;
GRANT ALL PRIVILEGES ON SCHEMA solicitudes TO arquisoft;

-- Contexto 11: Notificaciones
CREATE SCHEMA IF NOT EXISTS notificaciones;
GRANT ALL PRIVILEGES ON SCHEMA notificaciones TO arquisoft;

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
    SELECT unnest(ARRAY['usuarios', 'fichas', 'proyectos', 'artefactos', 
                        'repositorio_artefactos', 'mapas_ruta', 'biblioteca', 
                        'entregables', 'evaluaciones', 'solicitudes', 'notificaciones'])
  LOOP
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON TABLES TO arquisoft', schema_name);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON SEQUENCES TO arquisoft', schema_name);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON FUNCTIONS TO arquisoft', schema_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA %I TO arquisoft', schema_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA %I TO arquisoft', schema_name);
  END LOOP;
END $$;
