package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CambiarEstadoEntregableProyectoGradoCommand(UUID entregableProyectoGrado, String estadoEntregable) {

    public static CambiarEstadoEntregableProyectoGradoCommand crear(
            UUID entregableProyectoGrado, String estadoEntregable) {
        var result = new ValidationResult();
        var estadoRecortado = UtilTexto.aplicarTrim(estadoEntregable);

        ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.EntregableProyectoGrado.ID,
                EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(estadoRecortado,
                EntregablesFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarEstadoEntregableProyectoGradoCommand(entregableProyectoGrado, estadoRecortado);
    }
}
