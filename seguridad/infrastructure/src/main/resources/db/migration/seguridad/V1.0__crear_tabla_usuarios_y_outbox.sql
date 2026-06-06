-- ==============================================================================
-- Contexto: seguridad | Base de datos: usuarios
-- ==============================================================================

-- Tabla principal de usuarios del sistema.
-- Los roles son gestionados en Keycloak; aquí solo se almacena la identidad local.
CREATE TABLE usuario (
    id     UUID         NOT NULL,
    email  VARCHAR(255) NOT NULL UNIQUE,
    rol    VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id)
);

-- Nota: la tabla event_publication (Outbox Pattern de Spring Modulith) ya NO reside
-- en esta base de datos. Fue movida a la base de datos arquisoft_events para
-- centralizar todos los eventos del sistema. Ver ArquisoftEventsDataSourceConfig.
