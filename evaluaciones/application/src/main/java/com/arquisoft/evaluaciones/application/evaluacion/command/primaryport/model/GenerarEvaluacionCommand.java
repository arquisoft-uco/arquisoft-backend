package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.model.AgregarEntregableCommand;

public record GenerarEvaluacionCommand(AgregarEntregableCommand entregable) {

    public static GenerarEvaluacionCommand crear(
            String entregable, String proyectoGrado, String proyecto, Integer version) {
        return new GenerarEvaluacionCommand(
                AgregarEntregableCommand.crear(entregable, proyectoGrado, proyecto, version));
    }
}
