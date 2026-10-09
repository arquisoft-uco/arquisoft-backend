package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesFichaPerfilCoordinadorQuery(
        UUID fichaPerfil
) {

    public static ConsultarEvaluacionesFichaPerfilCoordinadorQuery crear(UUID fichaPerfil) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL,
                FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesFichaPerfilCoordinadorQuery(fichaPerfil);
    }
}
