package com.arquisoft.fichas.application.revisionitem.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarRevisionesItemEstudianteQuery(
        UUID estudiante,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarRevisionesItemEstudianteQuery crear(
            UUID estudiante, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.RevisionItem.ESTUDIANTE,
                FichasCodes.RevisionItem.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarRevisionesItemEstudianteQuery(estudiante, criterio);
    }
}
