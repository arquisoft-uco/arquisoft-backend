-- HU-313: el representante filtra sus fichas por DISPONIBLE_PARA_EVALUACION además de los estados de decisión.
-- estado_ficha_rol guarda lo que un rol puede consultar (ADR-014); no habilita transiciones.
INSERT INTO estado_ficha_rol (estado_ficha_id, rol) VALUES
    ('DISPONIBLE_PARA_EVALUACION', 'REPRESENTANTE_COMITE');
