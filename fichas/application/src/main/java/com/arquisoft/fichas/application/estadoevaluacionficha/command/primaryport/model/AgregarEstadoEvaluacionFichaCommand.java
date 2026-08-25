package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AgregarEstadoEvaluacionFichaCommand(
        UUID evaluacionFichaPerfil,
        String estadoEvaluacion,
        UUID representanteComite) {

    public AgregarEstadoEvaluacionFichaCommand {
        estadoEvaluacion = UtilTexto.aplicarTrim(estadoEvaluacion);
    }

    public static AgregarEstadoEvaluacionFichaCommand crear(
            String evaluacionFichaPerfil, String estadoEvaluacion, UUID representanteComite) {

        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(evaluacionFichaPerfil,
                FichasFields.EstadoEvaluacionFicha.EVALUACION_FICHA_PERFIL,
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_REQUERIDA, result)) {
            ValidatorUUID.uuidValido(evaluacionFichaPerfil,
                    FichasFields.EstadoEvaluacionFicha.EVALUACION_FICHA_PERFIL,
                    FichasCodes.EstadoEvaluacionFicha.EVALUACION_REQUERIDA, result);
        }

        if (ValidatorTexto.noEnBlanco(estadoEvaluacion,
                FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                FichasCodes.EstadoEvaluacionFicha.ESTADO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(estadoEvaluacion,
                    FichasLimits.EstadoEvaluacionFicha.ESTADO_MAX,
                    FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                    FichasCodes.EstadoEvaluacionFicha.ESTADO_REQUERIDO, result);
        }

        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.EstadoEvaluacionFicha.REPRESENTANTE_COMITE,
                FichasCodes.EstadoEvaluacionFicha.REPRESENTANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEstadoEvaluacionFichaCommand(
                UtilUUID.generarUUIDDesdeTexto(evaluacionFichaPerfil),
                estadoEvaluacion,
                representanteComite);
    }
}
