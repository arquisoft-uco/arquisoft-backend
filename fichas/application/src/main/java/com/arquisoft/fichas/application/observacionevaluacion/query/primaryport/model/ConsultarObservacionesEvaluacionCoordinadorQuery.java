package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesEvaluacionCoordinadorQuery(
        UUID fichaPerfil
) {

    public static ConsultarObservacionesEvaluacionCoordinadorQuery crear(UUID fichaPerfil) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.ObservacionEvaluacion.FICHA_PERFIL,
                FichasCodes.ObservacionEvaluacion.FICHA_PERFIL_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesEvaluacionCoordinadorQuery(fichaPerfil);
    }
}
