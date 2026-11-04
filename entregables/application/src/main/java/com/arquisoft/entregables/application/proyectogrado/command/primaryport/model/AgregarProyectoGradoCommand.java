package com.arquisoft.entregables.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AgregarProyectoGradoCommand(UUID proyectoGrado, String nombre) {

    public static AgregarProyectoGradoCommand crear(String proyectoGrado, String nombre) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre,
                EntregablesFields.ProyectoGrado.NOMBRE,
                EntregablesCodes.ProyectoGrado.NOMBRE_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarProyectoGradoCommand(UtilUUID.generarUUIDDesdeTexto(proyectoGrado), nombre);
    }
}
