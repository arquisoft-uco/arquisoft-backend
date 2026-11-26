package com.arquisoft.fichas.application.fichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarFichasPerfilDisponiblesEvaluacionQuery(
        UUID representanteComite,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarFichasPerfilDisponiblesEvaluacionQuery crear(
            UUID representanteComite, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.FichaPerfil.REPRESENTANTE_COMITE,
                FichasCodes.FichaPerfil.REPRESENTANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarFichasPerfilDisponiblesEvaluacionQuery(representanteComite, criterio);
    }
}
