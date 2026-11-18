package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record CambiarCriterioEvaluacionCualitativaAsesorCommand(
        UUID evaluacionCualitativaAsesor,
        UUID asesor,
        UUID criterio) {

    public static CambiarCriterioEvaluacionCualitativaAsesorCommand crear(
            UUID evaluacionCualitativaAsesor, String criterio, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionCualitativaAsesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_CUALITATIVA_ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_CUALITATIVA_REQUERIDA, result);

        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO, result);

        var criterioValido = ValidatorTexto.noEnBlanco(criterio,
                EvaluacionesFields.EvaluacionCualitativaAsesor.CRITERIO,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO, result)
                && ValidatorUUID.uuidValido(criterio,
                EvaluacionesFields.EvaluacionCualitativaAsesor.CRITERIO,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_INVALIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarCriterioEvaluacionCualitativaAsesorCommand(
                evaluacionCualitativaAsesor,
                asesor,
                criterioValido ? UtilUUID.generarUUIDDesdeTexto(criterio) : UtilUUID.obtenerUUIDPorDefecto());
    }
}
