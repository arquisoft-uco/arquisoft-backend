package com.arquisoft.fichas.application.observacionitem.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesItemEstudianteQuery(
        UUID estudiante,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarObservacionesItemEstudianteQuery crear(
            UUID estudiante, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.ObservacionItem.ESTUDIANTE,
                FichasCodes.ObservacionItem.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesItemEstudianteQuery(estudiante, criterio);
    }
}
