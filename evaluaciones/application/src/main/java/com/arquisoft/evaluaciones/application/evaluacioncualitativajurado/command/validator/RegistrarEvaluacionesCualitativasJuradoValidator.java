package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaItemsCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;

public interface RegistrarEvaluacionesCualitativasJuradoValidator {

    void validarAcceso(ExistenciaEvaluacionJurado existencia, PropiedadEvaluacionJurado propiedad);

    void validarContenido(
            ExistenciaItemsCualitativosJurado items,
            ExistenciaCriteriosCualitativosJurado criterios,
            DisponibilidadEvaluacionesCualitativasJurado disponibilidad);
}
