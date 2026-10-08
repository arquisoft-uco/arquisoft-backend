package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesEvaluacionEstudianteQuery(
        UUID evaluacionFichaPerfil,
        UUID estudiante
) {

    public static ConsultarObservacionesEvaluacionEstudianteQuery crear(
            UUID evaluacionFichaPerfil, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionFichaPerfil,
                FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA, result);
        ValidatorObjeto.noNulo(estudiante,
                FichasFields.ObservacionEvaluacion.ESTUDIANTE,
                FichasCodes.ObservacionEvaluacion.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesEvaluacionEstudianteQuery(evaluacionFichaPerfil, estudiante);
    }
}
