package com.arquisoft.fichas.application.revisionitem.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarRevisionesItemAsesorQuery(
        UUID asesorFicha,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarRevisionesItemAsesorQuery crear(
            UUID asesorFicha, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.RevisionItem.ASESOR_FICHA,
                FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarRevisionesItemAsesorQuery(asesorFicha, criterio);
    }
}
