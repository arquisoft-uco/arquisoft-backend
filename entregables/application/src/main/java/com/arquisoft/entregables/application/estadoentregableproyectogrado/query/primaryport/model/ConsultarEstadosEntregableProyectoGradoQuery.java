package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEstadosEntregableProyectoGradoQuery(UUID entregableProyectoGrado, UUID asesor) {

    public static ConsultarEstadosEntregableProyectoGradoQuery crear(UUID entregableProyectoGrado, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.EntregableProyectoGrado.ID,
                EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                EntregablesFields.EntregableProyectoGrado.ASESOR,
                EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstadosEntregableProyectoGradoQuery(entregableProyectoGrado, asesor);
    }
}
