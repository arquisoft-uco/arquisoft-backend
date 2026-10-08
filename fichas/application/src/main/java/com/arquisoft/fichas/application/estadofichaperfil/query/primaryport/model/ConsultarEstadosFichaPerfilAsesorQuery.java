package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEstadosFichaPerfilAsesorQuery(
        UUID asesorFicha,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarEstadosFichaPerfilAsesorQuery crear(
            UUID asesorFicha, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.EstadoFichaPerfil.ASESOR_FICHA,
                FichasCodes.EstadoFichaPerfil.ASESOR_FICHA_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstadosFichaPerfilAsesorQuery(asesorFicha, criterio);
    }
}
