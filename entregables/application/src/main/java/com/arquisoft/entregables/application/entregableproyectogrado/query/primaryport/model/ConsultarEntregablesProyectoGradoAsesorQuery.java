package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEntregablesProyectoGradoAsesorQuery(
        UUID asesor,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarEntregablesProyectoGradoAsesorQuery crear(UUID asesor, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesor,
                EntregablesFields.EntregableProyectoGrado.ASESOR,
                EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEntregablesProyectoGradoAsesorQuery(asesor, criterio);
    }
}
