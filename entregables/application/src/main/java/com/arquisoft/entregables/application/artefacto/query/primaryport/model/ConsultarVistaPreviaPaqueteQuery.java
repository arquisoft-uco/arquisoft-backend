package com.arquisoft.entregables.application.artefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarVistaPreviaPaqueteQuery(UUID proyectoGrado, UUID asesor) {

    public static ConsultarVistaPreviaPaqueteQuery crear(UUID proyectoGrado, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                EntregablesFields.AsesorProyectoGrado.ASESOR,
                EntregablesCodes.AsesorProyectoGrado.ASESOR_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarVistaPreviaPaqueteQuery(proyectoGrado, asesor);
    }
}
