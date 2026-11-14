package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.EstadoOmisionEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaEvaluacionesCuantitativasAsesorRegistradas;

public interface OmitirEvaluacionesCuantitativasAsesorValidator {

    void validar(
            ExistenciaEvaluacionAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            EstadoOmisionEvaluacionesCuantitativasAsesor estado,
            ExistenciaEvaluacionesCuantitativasAsesorRegistradas registradas);
}
