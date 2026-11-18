-- HU-307 — Tabla réplica local de proyecto_grado (dueño: contexto proyectos). Poblada por EstudiantesProyectoGradoAsignadosConsumer.
-- Sin FK: otro contexto es dueño del dato. ocurrido_en es metadato de replicación (no está en el MER).
CREATE TABLE proyecto_grado (
    id UUID PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    ocurrido_en TIMESTAMPTZ NOT NULL
);
