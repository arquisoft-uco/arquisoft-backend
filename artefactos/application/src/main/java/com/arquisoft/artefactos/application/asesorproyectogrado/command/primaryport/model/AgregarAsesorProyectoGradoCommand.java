package com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarAsesorProyectoGradoCommand(UUID asesor, UUID proyectoGrado, Instant ocurridoEn) {

    public static AgregarAsesorProyectoGradoCommand crear(String asesor, String proyectoGrado, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(asesor,
                ArtefactosFields.AsesorProyectoGrado.ASESOR,
                ArtefactosCodes.AsesorProyectoGrado.ASESOR_REQUERIDO, result);
        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                ArtefactosCodes.AsesorProyectoGrado.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                ArtefactosFields.AsesorProyectoGrado.OCURRIDO_EN,
                ArtefactosCodes.AsesorProyectoGrado.OCURRIDO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarAsesorProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(asesor), UtilUUID.generarUUIDDesdeTexto(proyectoGrado), ocurridoEn);
    }
}
