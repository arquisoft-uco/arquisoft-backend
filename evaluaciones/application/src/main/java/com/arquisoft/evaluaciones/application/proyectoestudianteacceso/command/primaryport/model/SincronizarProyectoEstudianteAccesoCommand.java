package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record SincronizarProyectoEstudianteAccesoCommand(
        UUID proyecto,
        UUID estudiante,
        boolean activo,
        Instant ocurridoEn
) {

    public static SincronizarProyectoEstudianteAccesoCommand crear(
            String proyecto, String estudiante, boolean activo, Instant ocurridoEn) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(proyecto,
                EvaluacionesFields.ProyectoEstudianteAcceso.PROYECTO,
                EvaluacionesCodes.ProyectoEstudianteAcceso.PROYECTO_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(proyecto,
                    EvaluacionesFields.ProyectoEstudianteAcceso.PROYECTO,
                    EvaluacionesCodes.ProyectoEstudianteAcceso.PROYECTO_REQUERIDO, result);
        }

        if (ValidatorTexto.noEnBlanco(estudiante,
                EvaluacionesFields.ProyectoEstudianteAcceso.ESTUDIANTE,
                EvaluacionesCodes.ProyectoEstudianteAcceso.ESTUDIANTE_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(estudiante,
                    EvaluacionesFields.ProyectoEstudianteAcceso.ESTUDIANTE,
                    EvaluacionesCodes.ProyectoEstudianteAcceso.ESTUDIANTE_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new SincronizarProyectoEstudianteAccesoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyecto),
                UtilUUID.generarUUIDDesdeTexto(estudiante),
                activo,
                ocurridoEn);
    }
}
