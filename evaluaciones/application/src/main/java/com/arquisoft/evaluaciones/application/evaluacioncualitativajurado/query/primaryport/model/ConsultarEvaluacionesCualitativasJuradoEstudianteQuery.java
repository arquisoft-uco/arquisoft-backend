package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record ConsultarEvaluacionesCualitativasJuradoEstudianteQuery(
        UUID evaluacionJurado,
        UUID estudiante
) {

    public static ConsultarEvaluacionesCualitativasJuradoEstudianteQuery crear(
            UUID evaluacionJurado, String estudianteSubject) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(estudianteSubject,
                EvaluacionesFields.EvaluacionCualitativaJurado.ESTUDIANTE,
                EvaluacionesCodes.EvaluacionCualitativaJurado.ESTUDIANTE_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(estudianteSubject,
                    EvaluacionesFields.EvaluacionCualitativaJurado.ESTUDIANTE,
                    EvaluacionesCodes.EvaluacionCualitativaJurado.ESTUDIANTE_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCualitativasJuradoEstudianteQuery(
                evaluacionJurado, UtilUUID.generarUUIDDesdeTexto(estudianteSubject));
    }
}
