-- HU-178 — Ubicación del ZIP del paquete en la réplica entregable_proyecto_grado.
-- Espejo de paquete_entregable (MER 08_tablas_entregables.sql). Las tres columnas son NULAS porque la tabla
-- ya tiene filas de semillas; pasarán a NOT NULL cuando el consumidor del evento de aprobación esté activo.
ALTER TABLE entregable_proyecto_grado
    ADD COLUMN bucket VARCHAR(63) NULL,
    ADD COLUMN clave_objeto VARCHAR(2000) NULL,
    ADD COLUMN nombre_archivo VARCHAR(255) NULL;
