-- HU-007 — Tabla réplica local de asesor (dueño: contexto usuarios), copiada de mer/06_tablas_mapas_ruta.sql.
-- Aún sin consumidor: la poblará la HU que introduzca el de usuarios.asesor.agregado (y esa HU añadirá
-- ocurrido_en con DEFAULT), igual que proyecto_grado en HU-172.
CREATE TABLE asesor (
    id            UUID         PRIMARY KEY,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    eliminado_en  TIMESTAMPTZ  NULL
);
