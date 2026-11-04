package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record RegistrarObservacionItemJuradoCommand(
        UUID evaluacionCuantitativaJurado, String descripcion) {

    public static RegistrarObservacionItemJuradoCommand crear(
            UUID evaluacionCuantitativaJurado, String descripcion) {
        var result = new ValidationResult();
        var descripcionRecortada = UtilTexto.aplicarTrim(descripcion);

        ValidatorObjeto.noNulo(evaluacionCuantitativaJurado,
                EvaluacionesFields.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO,
                EvaluacionesCodes.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO_REQUERIDA, result);

        if (ValidatorTexto.noEnBlanco(descripcionRecortada,
                EvaluacionesFields.ObservacionItemJurado.DESCRIPCION,
                EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(descripcionRecortada,
                    EvaluacionesLimits.ObservacionItemJurado.DESCRIPCION_MAX,
                    EvaluacionesFields.ObservacionItemJurado.DESCRIPCION,
                    EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DEMASIADO_LARGA, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarObservacionItemJuradoCommand(evaluacionCuantitativaJurado, descripcionRecortada);
    }
}
