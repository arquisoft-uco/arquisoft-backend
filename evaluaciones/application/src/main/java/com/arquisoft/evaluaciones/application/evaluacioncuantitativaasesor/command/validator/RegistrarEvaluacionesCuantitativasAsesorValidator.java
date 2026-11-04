package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.DisponibilidadEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaItemsCuantitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajesVsValoresMaximosAsesor;

public interface RegistrarEvaluacionesCuantitativasAsesorValidator {

    void validar(
            ExistenciaEvaluacionAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            ExistenciaItemsCuantitativosAsesor items,
            PuntajesVsValoresMaximosAsesor puntajes,
            DisponibilidadEvaluacionesCuantitativasAsesor disponibilidad);
}
