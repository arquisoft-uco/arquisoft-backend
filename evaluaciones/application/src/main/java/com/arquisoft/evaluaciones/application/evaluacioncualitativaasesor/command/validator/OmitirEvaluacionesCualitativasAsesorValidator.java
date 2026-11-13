package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoOmisionEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionesCualitativasAsesorRegistradas;

public interface OmitirEvaluacionesCualitativasAsesorValidator {

    void validar(
            ExistenciaEvaluacionAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            EstadoOmisionEvaluacionesCualitativasAsesor estado,
            ExistenciaEvaluacionesCualitativasAsesorRegistradas registradas);
}
