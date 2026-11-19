package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.model.AgregarEntregableCommand;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record GenerarEvaluacionCommand(AgregarEntregableCommand entregable, UUID asesor) {

    public static GenerarEvaluacionCommand crear(
            String entregable, String proyectoGrado, String proyecto, Integer version, String asesor) {
        var entregableValidado = AgregarEntregableCommand.crear(entregable, proyectoGrado, proyecto, version);
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(asesor, EvaluacionesFields.EvaluacionAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new GenerarEvaluacionCommand(entregableValidado, UtilUUID.generarUUIDDesdeTexto(asesor));
    }
}
