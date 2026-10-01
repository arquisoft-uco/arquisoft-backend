package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesFichaPerfilRepresentanteQuery(
        UUID fichaPerfil,
        UUID representanteComite
) {

    public static ConsultarEvaluacionesFichaPerfilRepresentanteQuery crear(UUID fichaPerfil, UUID representanteComite) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL,
                FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA, result);

        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.EvaluacionFichaPerfil.REPRESENTANTE_COMITE,
                FichasCodes.EvaluacionFichaPerfil.REPRESENTANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesFichaPerfilRepresentanteQuery(fichaPerfil, representanteComite);
    }
}
