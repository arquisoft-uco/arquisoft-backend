package com.arquisoft.entregables.application.entregableproyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.result.RegistroResultadoEvaluacionEntregableProyectoGradoResult;

import java.util.UUID;

public final class RegistroResultadoEvaluacionEntregableProyectoGradoResultMapper {

    private RegistroResultadoEvaluacionEntregableProyectoGradoResultMapper() {}

    public static RegistroResultadoEvaluacionEntregableProyectoGradoResult.Registrado toResultRegistrado(
            UUID entregableProyectoGrado, String estadoEntregable) {
        return new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Registrado(
                entregableProyectoGrado, estadoEntregable);
    }

    public static RegistroResultadoEvaluacionEntregableProyectoGradoResult.Descartado toResultDescartado(
            UUID entregableProyectoGrado, String estadoActual, String estadoProyecto) {
        return new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Descartado(
                entregableProyectoGrado, estadoActual, estadoProyecto);
    }
}
