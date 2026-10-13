package com.arquisoft.fichas.application.observacionitem.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesItemAsesorQuery(
        UUID asesorFicha,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarObservacionesItemAsesorQuery crear(
            UUID asesorFicha, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.ObservacionItem.ASESOR_FICHA,
                FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesItemAsesorQuery(asesorFicha, criterio);
    }
}
