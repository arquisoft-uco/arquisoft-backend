-- Trazabilidad de la consolidación: nota del asesor, promedio de los jurados vigentes, fracciones de peso
-- aplicadas (suman 1) y cantidad de jurados con la que se calcularon. Nulos hasta que la evaluación se consolide.
ALTER TABLE evaluacion
    ADD COLUMN nota_asesor NUMERIC(4,2) NULL,
    ADD COLUMN nota_jurados NUMERIC(4,2) NULL,
    ADD COLUMN peso_asesor NUMERIC(5,4) NULL,
    ADD COLUMN peso_jurados NUMERIC(5,4) NULL,
    ADD COLUMN cantidad_jurados INTEGER NULL;
