package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record DescartarEvaluacionFichaCommand(UUID evaluacionFichaPerfil, UUID representanteComite) {

    public static DescartarEvaluacionFichaCommand crear(UUID evaluacionFichaPerfil, UUID representanteComite) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionFichaPerfil,
                FichasFields.EstadoEvaluacionFicha.EVALUACION_FICHA_PERFIL,
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_REQUERIDA, result);
        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.EstadoEvaluacionFicha.REPRESENTANTE_COMITE,
                FichasCodes.EstadoEvaluacionFicha.REPRESENTANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new DescartarEvaluacionFichaCommand(evaluacionFichaPerfil, representanteComite);
    }
}
