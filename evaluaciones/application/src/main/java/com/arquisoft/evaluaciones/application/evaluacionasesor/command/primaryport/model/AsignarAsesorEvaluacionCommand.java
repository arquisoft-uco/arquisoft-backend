package com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AsignarAsesorEvaluacionCommand(UUID evaluacion, UUID asesor) {

    public static AsignarAsesorEvaluacionCommand crear(UUID evaluacion, String asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.EvaluacionAsesor.EVALUACION,
                EvaluacionesCodes.EvaluacionAsesor.EVALUACION_REQUERIDO, result);

        var asesorValido = ValidatorTexto.noEnBlanco(asesor,
                EvaluacionesFields.EvaluacionAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionAsesor.ASESOR_REQUERIDO, result)
                && ValidatorUUID.uuidValido(asesor,
                EvaluacionesFields.EvaluacionAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionAsesor.ASESOR_INVALIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarAsesorEvaluacionCommand(
                evaluacion,
                asesorValido ? UtilUUID.generarUUIDDesdeTexto(asesor) : UtilUUID.obtenerUUIDPorDefecto());
    }
}
