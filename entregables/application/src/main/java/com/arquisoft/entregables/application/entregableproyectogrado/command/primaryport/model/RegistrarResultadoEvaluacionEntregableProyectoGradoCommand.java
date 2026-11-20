package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record RegistrarResultadoEvaluacionEntregableProyectoGradoCommand(
        UUID entregableProyectoGrado, String estadoEntregable) {

    public static RegistrarResultadoEvaluacionEntregableProyectoGradoCommand crear(
            String entregableProyectoGrado, String estadoEntregable) {
        var result = new ValidationResult();
        var estadoRecortado = UtilTexto.aplicarTrim(estadoEntregable);

        if (ValidatorUUID.uuidValido(entregableProyectoGrado,
                EntregablesFields.EntregableProyectoGrado.ID,
                EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO, result)
                && UtilUUID.esPorDefecto(UtilUUID.generarUUIDDesdeTexto(entregableProyectoGrado))) {
            result.agregarError(EntregablesFields.EntregableProyectoGrado.ID,
                    EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
        }
        ValidatorTexto.noEnBlanco(estadoRecortado,
                EntregablesFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarResultadoEvaluacionEntregableProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(entregableProyectoGrado), estadoRecortado);
    }
}
