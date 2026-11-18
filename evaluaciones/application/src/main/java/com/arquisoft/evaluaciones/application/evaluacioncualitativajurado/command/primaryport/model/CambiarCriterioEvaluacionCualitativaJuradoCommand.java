package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record CambiarCriterioEvaluacionCualitativaJuradoCommand(
        UUID evaluacionCualitativaJurado, UUID criterio, UUID jurado) {

    public static CambiarCriterioEvaluacionCualitativaJuradoCommand crear(
            UUID evaluacionCualitativaJurado, String criterio, UUID jurado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionCualitativaJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.ID,
                EvaluacionesCodes.EvaluacionCualitativaJurado.ID_REQUERIDO, result);

        var criterioValido = ValidatorTexto.noEnBlanco(criterio,
                EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO, result)
                && ValidatorUUID.uuidValido(criterio,
                EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_INVALIDO, result);

        ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.JURADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarCriterioEvaluacionCualitativaJuradoCommand(
                evaluacionCualitativaJurado,
                criterioValido ? UtilUUID.generarUUIDDesdeTexto(criterio) : UtilUUID.obtenerUUIDPorDefecto(),
                jurado);
    }
}
