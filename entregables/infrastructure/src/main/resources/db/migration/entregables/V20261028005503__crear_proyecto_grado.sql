-- HU-147 — Tabla réplica local de proyecto_grado (dueño: contexto proyectos).
-- Poblada por ProyectoGradoRegistradoConsumer al consumir proyectos.proyecto_grado.registrado.
CREATE TABLE proyecto_grado (
    id     UUID         PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL
);
