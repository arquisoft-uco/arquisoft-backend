package com.arquisoft.artefactos.application.proyectogrado.command.result.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.artefactos.domain.proyectogrado.FinalizacionProyectoGradoDomain;

public final class FinalizacionProyectoGradoResultMapper {

    private FinalizacionProyectoGradoResultMapper() {}

    public static FinalizacionProyectoGradoResult.Finalizado toResultFinalizado(FinalizacionProyectoGradoDomain finalizacion) {
        return new FinalizacionProyectoGradoResult.Finalizado(finalizacion.getProyectoGrado());
    }

    public static FinalizacionProyectoGradoResult.YaFinalizado toResultYaFinalizado(FinalizacionProyectoGradoDomain finalizacion) {
        return new FinalizacionProyectoGradoResult.YaFinalizado(finalizacion.getProyectoGrado());
    }
}
