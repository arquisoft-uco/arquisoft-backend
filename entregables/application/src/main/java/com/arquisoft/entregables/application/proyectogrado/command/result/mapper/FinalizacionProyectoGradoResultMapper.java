package com.arquisoft.entregables.application.proyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;

public final class FinalizacionProyectoGradoResultMapper {

    private FinalizacionProyectoGradoResultMapper() {}

    public static FinalizacionProyectoGradoResult.Finalizado toResultFinalizado(
            FinalizacionProyectoGradoDomain finalizacion) {
        return new FinalizacionProyectoGradoResult.Finalizado(finalizacion.getProyectoGrado());
    }

    public static FinalizacionProyectoGradoResult.Duplicado toResultDuplicado(
            FinalizacionProyectoGradoDomain finalizacion) {
        return new FinalizacionProyectoGradoResult.Duplicado(finalizacion.getProyectoGrado());
    }
}
