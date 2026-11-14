package com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record FinalizarAsesorProyectoGradoCommand(UUID asesor, UUID proyectoGrado, Instant ocurridoEn) {

    public static FinalizarAsesorProyectoGradoCommand crear(String asesor, String proyectoGrado, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(asesor,
                EntregablesFields.AsesorProyectoGrado.ASESOR,
                EntregablesCodes.AsesorProyectoGrado.ASESOR_REQUERIDO, result);
        ValidatorUUID.uuidValido(proyectoGrado,
                EntregablesFields.AsesorProyectoGrado.PROYECTO_GRADO,
                EntregablesCodes.AsesorProyectoGrado.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                EntregablesFields.AsesorProyectoGrado.OCURRIDO_EN,
                EntregablesCodes.AsesorProyectoGrado.OCURRIDO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new FinalizarAsesorProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(asesor), UtilUUID.generarUUIDDesdeTexto(proyectoGrado), ocurridoEn);
    }
}
