-- Resumen cualitativo, cuantitativo y nota acumulada (0 a 5) de la evaluación de cada jurado y del asesor.
-- Nulo significa sin calcular: las evaluaciones anteriores no se retrocalculan.
ALTER TABLE evaluacion_jurado
    ADD COLUMN resumen_cualitativo NUMERIC(4,2) NULL,
    ADD COLUMN resumen_cuantitativo NUMERIC(4,2) NULL,
    ADD COLUMN nota_acumulada NUMERIC(4,2) NULL;

ALTER TABLE evaluacion_asesor
    ADD COLUMN resumen_cualitativo NUMERIC(4,2) NULL,
    ADD COLUMN resumen_cuantitativo NUMERIC(4,2) NULL,
    ADD COLUMN nota_acumulada NUMERIC(4,2) NULL;
