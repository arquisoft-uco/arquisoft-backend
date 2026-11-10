-- HT-022 — Tabla réplica local de version_artefacto (dueño: contexto artefactos): artefacto comparte el id de la versión y puede estar en el paquete de varios entregables.
-- uri_archivo es la ruta dentro del paquete de cada entregable, así que pertenece al vínculo y no al artefacto.
ALTER TABLE artefacto_entregable ADD COLUMN uri_archivo VARCHAR(2000) NOT NULL;
ALTER TABLE artefacto DROP COLUMN uri_archivo;
