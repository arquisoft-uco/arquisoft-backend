package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record CongelarProyectoGradoCommand(UUID proyectoGrado, Instant ocurridoEn) {

    public static CongelarProyectoGradoCommand crear(String proyectoGrado, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                ArtefactosFields.ProyectoGrado.ENTREGADO_EN,
                ArtefactosCodes.ProyectoGrado.ENTREGADO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new CongelarProyectoGradoCommand(UtilUUID.generarUUIDDesdeTexto(proyectoGrado), ocurridoEn);
    }
}
