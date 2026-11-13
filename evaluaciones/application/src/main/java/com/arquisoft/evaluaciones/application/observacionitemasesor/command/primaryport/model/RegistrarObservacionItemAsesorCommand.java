package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record RegistrarObservacionItemAsesorCommand(
        UUID evaluacionCuantitativaAsesor, UUID asesor, String descripcion) {

    public static RegistrarObservacionItemAsesorCommand crear(
            UUID evaluacionCuantitativaAsesor, UUID asesor, String descripcion) {
        var result = new ValidationResult();
        var descripcionRecortada = UtilTexto.aplicarTrim(descripcion);

        ValidatorObjeto.noNulo(evaluacionCuantitativaAsesor,
                EvaluacionesFields.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR_REQUERIDA, result);
        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.ObservacionItemAsesor.ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(descripcionRecortada,
                EvaluacionesFields.ObservacionItemAsesor.DESCRIPCION,
                EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(descripcionRecortada,
                    EvaluacionesLimits.ObservacionItemAsesor.DESCRIPCION_MAX,
                    EvaluacionesFields.ObservacionItemAsesor.DESCRIPCION,
                    EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DEMASIADO_LARGA, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarObservacionItemAsesorCommand(evaluacionCuantitativaAsesor, asesor, descripcionRecortada);
    }
}
