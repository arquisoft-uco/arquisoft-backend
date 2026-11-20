package com.arquisoft.entregables.application.entregableproyectogrado.command.result;

import java.util.UUID;

public sealed interface RegistroResultadoEvaluacionEntregableProyectoGradoResult {

    record Registrado(UUID entregableProyectoGrado, String estadoEntregable)
            implements RegistroResultadoEvaluacionEntregableProyectoGradoResult {}

    record Descartado(UUID entregableProyectoGrado, String estadoActual, String estadoProyecto)
            implements RegistroResultadoEvaluacionEntregableProyectoGradoResult {}
}
