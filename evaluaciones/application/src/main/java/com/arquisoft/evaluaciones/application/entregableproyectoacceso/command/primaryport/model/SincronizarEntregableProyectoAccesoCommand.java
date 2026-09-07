package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record SincronizarEntregableProyectoAccesoCommand(
        UUID entregable,
        UUID proyecto,
        int versionEntregable,
        Instant ocurridoEn
) {

    public static SincronizarEntregableProyectoAccesoCommand crear(
            String entregable, String proyecto, int versionEntregable, Instant ocurridoEn) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(entregable,
                EvaluacionesFields.EntregableProyectoAcceso.ENTREGABLE,
                EvaluacionesCodes.EntregableProyectoAcceso.ENTREGABLE_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(entregable,
                    EvaluacionesFields.EntregableProyectoAcceso.ENTREGABLE,
                    EvaluacionesCodes.EntregableProyectoAcceso.ENTREGABLE_REQUERIDO, result);
        }

        if (ValidatorTexto.noEnBlanco(proyecto,
                EvaluacionesFields.EntregableProyectoAcceso.PROYECTO,
                EvaluacionesCodes.EntregableProyectoAcceso.PROYECTO_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(proyecto,
                    EvaluacionesFields.EntregableProyectoAcceso.PROYECTO,
                    EvaluacionesCodes.EntregableProyectoAcceso.PROYECTO_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new SincronizarEntregableProyectoAccesoCommand(
                UtilUUID.generarUUIDDesdeTexto(entregable),
                UtilUUID.generarUUIDDesdeTexto(proyecto),
                versionEntregable,
                ocurridoEn);
    }
}
