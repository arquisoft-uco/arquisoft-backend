package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoCambioCriterioEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;

public interface CambiarCriterioEvaluacionCualitativaAsesorValidator {

    void validar(
            ExistenciaEvaluacionCualitativaAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            EstadoCambioCriterioEvaluacionCualitativaAsesor estado,
            ExistenciaCriteriosCualitativosAsesor criterio);
}
