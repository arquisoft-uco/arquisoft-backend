package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

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

        if (DomainValidator.noEnBlanco(evaluacionFichaPerfil,
                FichasFields.EstadoEvaluacionFicha.EVALUACION_FICHA_PERFIL,
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_REQUERIDA, result)) {
            DomainValidator.uuidValido(evaluacionFichaPerfil,
                    FichasFields.EstadoEvaluacionFicha.EVALUACION_FICHA_PERFIL,
                    FichasCodes.EstadoEvaluacionFicha.EVALUACION_REQUERIDA, result);
        }

        if (DomainValidator.noEnBlanco(estadoEvaluacion,
                FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                FichasCodes.EstadoEvaluacionFicha.ESTADO_REQUERIDO, result)) {
            DomainValidator.longitudMaxima(estadoEvaluacion,
                    FichasLimits.EstadoEvaluacionFicha.ESTADO_MAX,
                    FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                    FichasCodes.EstadoEvaluacionFicha.ESTADO_REQUERIDO, result);
        }

        DomainValidator.noNulo(representanteComite,
                FichasFields.EstadoEvaluacionFicha.REPRESENTANTE_COMITE,
                FichasCodes.EstadoEvaluacionFicha.REPRESENTANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEstadoEvaluacionFichaCommand(
                UtilUUID.generarUUIDDesdeTexto(evaluacionFichaPerfil),
                estadoEvaluacion,
                representanteComite);
    }
}
