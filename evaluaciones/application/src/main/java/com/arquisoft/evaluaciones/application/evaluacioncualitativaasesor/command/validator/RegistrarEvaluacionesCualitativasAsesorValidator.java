package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.DisponibilidadEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaItemsCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;

public interface RegistrarEvaluacionesCualitativasAsesorValidator {

    void validar(
            ExistenciaEvaluacionAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            ExistenciaItemsCualitativosAsesor items,
            ExistenciaCriteriosCualitativosAsesor criterios,
            DisponibilidadEvaluacionesCualitativasAsesor disponibilidad);
}
