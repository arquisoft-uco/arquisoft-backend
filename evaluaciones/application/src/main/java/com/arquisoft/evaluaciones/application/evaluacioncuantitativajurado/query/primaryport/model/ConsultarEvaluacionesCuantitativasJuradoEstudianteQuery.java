package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery(
        UUID evaluacionJurado,
        UUID estudiante
) {

    public static ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery crear(
            UUID evaluacionJurado, String estudianteSubject) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(estudianteSubject,
                EvaluacionesFields.EvaluacionCuantitativaJurado.ESTUDIANTE,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ESTUDIANTE_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(estudianteSubject,
                    EvaluacionesFields.EvaluacionCuantitativaJurado.ESTUDIANTE,
                    EvaluacionesCodes.EvaluacionCuantitativaJurado.ESTUDIANTE_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery(
                evaluacionJurado, UtilUUID.generarUUIDDesdeTexto(estudianteSubject));
    }
}
