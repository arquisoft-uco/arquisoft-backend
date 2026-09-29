-- HU-172 — Tabla réplica local de proyecto_grado (dueño: contexto proyectos).
-- Aún sin productor: proyectos no publica proyecto_grado.*; la poblará el consumidor de la HU que
-- lo introduzca (y esa HU añadirá ocurrido_en / eliminado_en con DEFAULT).
-- Desviaciones del MER de mapas_ruta, alineadas con mer/07_tablas_proyectos_grado.sql:
--   coordinador_id UUID (MER mapas_ruta: VARCHAR(30)) — se compara con el sub del JWT.
--   estado_proyecto_grado_id VARCHAR(60) con el id del enum (ADR-012)
--   (MER mapas_ruta: estado_proyecto_grado_nombre VARCHAR(16)).
CREATE TABLE proyecto_grado (
    id                       UUID          PRIMARY KEY,
    estado_proyecto_grado_id VARCHAR(60)   NOT NULL,
    coordinador_id           UUID          NOT NULL,
    ficha_perfil_id          UUID          NOT NULL,
    titulo_proyecto          VARCHAR(100)  NOT NULL
);
