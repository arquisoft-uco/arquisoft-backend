package com.arquisoft.entregables.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record ReactivarArtefactoCommand(UUID artefacto, Instant ocurridoEn) {

    public static ReactivarArtefactoCommand crear(String artefacto, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(artefacto,
                EntregablesFields.Artefacto.ID,
                EntregablesCodes.Artefacto.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                EntregablesFields.Artefacto.OCURRIDO_EN,
                EntregablesCodes.Artefacto.OCURRIDO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new ReactivarArtefactoCommand(UtilUUID.generarUUIDDesdeTexto(artefacto), ocurridoEn);
    }
}
