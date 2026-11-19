package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.LocalDate;
import java.util.UUID;

public record ReanudarProyectoGradoCommand(UUID proyectoGrado, LocalDate fechaFin, LocalDate hoy) {

    public static ReanudarProyectoGradoCommand crear(String proyectoGrado, LocalDate fechaFin, LocalDate hoy) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(fechaFin,
                ProyectosFields.ProyectoGrado.FECHA_FIN,
                ProyectosCodes.ProyectoGrado.FECHA_FIN_REQUERIDA, result);
        ValidatorObjeto.noNulo(hoy,
                ProyectosFields.ProyectoGrado.HOY,
                ProyectosCodes.ProyectoGrado.HOY_REQUERIDA, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new ReanudarProyectoGradoCommand(UtilUUID.generarUUIDDesdeTexto(proyectoGrado), fechaFin, hoy);
    }
}
