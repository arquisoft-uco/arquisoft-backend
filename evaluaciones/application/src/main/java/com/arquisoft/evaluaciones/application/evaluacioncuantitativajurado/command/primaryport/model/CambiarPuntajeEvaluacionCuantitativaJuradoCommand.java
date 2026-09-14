package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record CambiarPuntajeEvaluacionCuantitativaJuradoCommand(
        UUID evaluacionCuantitativaJurado, Integer nuevoPuntaje, UUID jurado) {

    public static CambiarPuntajeEvaluacionCuantitativaJuradoCommand crear(
            UUID evaluacionCuantitativaJurado, Integer nuevoPuntaje, String juradoSubject) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionCuantitativaJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.ID,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(nuevoPuntaje,
                EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(juradoSubject,
                EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(juradoSubject,
                    EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                    EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarPuntajeEvaluacionCuantitativaJuradoCommand(
                evaluacionCuantitativaJurado, nuevoPuntaje, UtilUUID.generarUUIDDesdeTexto(juradoSubject));
    }
}
