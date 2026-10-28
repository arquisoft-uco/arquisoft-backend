package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record GenerarEntregableProyectoGradoCommand(UUID proyectoGrado, UUID asesor) {

    public static GenerarEntregableProyectoGradoCommand crear(UUID proyectoGrado, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.EntregableProyectoGrado.PROYECTO_GRADO,
                EntregablesCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                EntregablesFields.EntregableProyectoGrado.ASESOR,
                EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new GenerarEntregableProyectoGradoCommand(proyectoGrado, asesor);
    }
}
