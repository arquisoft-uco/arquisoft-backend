package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesFichaPerfilAsesorQuery(
        UUID asesorFicha,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarEvaluacionesFichaPerfilAsesorQuery crear(
            UUID asesorFicha, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.EvaluacionFichaPerfil.ASESOR_FICHA,
                FichasCodes.EvaluacionFichaPerfil.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesFichaPerfilAsesorQuery(asesorFicha, criterio);
    }
}
