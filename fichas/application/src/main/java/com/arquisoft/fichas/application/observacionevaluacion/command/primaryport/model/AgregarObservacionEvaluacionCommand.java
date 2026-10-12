package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record AgregarObservacionEvaluacionCommand(UUID evaluacionFichaPerfil, String observacion,
                                                  UUID representanteComite) {

    public AgregarObservacionEvaluacionCommand {
        observacion = UtilTexto.aplicarTrim(observacion);
    }

    public static AgregarObservacionEvaluacionCommand crear(UUID evaluacionFichaPerfil, String observacion,
                                                            UUID representanteComite) {
        var result = new ValidationResult();
        var recortada = UtilTexto.aplicarTrim(observacion);

        ValidatorObjeto.noNulo(evaluacionFichaPerfil,
                FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA, result);

        if (ValidatorTexto.noEnBlanco(recortada,
                FichasFields.ObservacionEvaluacion.OBSERVACION,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(recortada, FichasLimits.ObservacionEvaluacion.OBSERVACION_MAX,
                    FichasFields.ObservacionEvaluacion.OBSERVACION,
                    FichasCodes.ObservacionEvaluacion.OBSERVACION_DEMASIADO_LARGA, result);
        }

        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.ObservacionEvaluacion.REPRESENTANTE_COMITE,
                FichasCodes.ObservacionEvaluacion.REPRESENTANTE_COMITE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarObservacionEvaluacionCommand(evaluacionFichaPerfil, recortada, representanteComite);
    }
}
