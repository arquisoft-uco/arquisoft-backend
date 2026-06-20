-- ==============================================================================
-- Contexto: usuarios | Base de datos: usuarios
-- ==============================================================================

-- Tabla principal de usuarios del sistema.
-- Los roles son gestionados en Keycloak; aqui solo se almacena la identidad local.
CREATE TABLE usuario (
    id     UUID         NOT NULL,
    email  VARCHAR(255) NOT NULL UNIQUE,
    rol    VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id)
);
