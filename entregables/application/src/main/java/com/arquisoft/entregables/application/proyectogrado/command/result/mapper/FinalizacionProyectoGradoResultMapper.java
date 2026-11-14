package com.arquisoft.entregables.application.proyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;

public final class FinalizacionProyectoGradoResultMapper {

    private FinalizacionProyectoGradoResultMapper() {}

    public static FinalizacionProyectoGradoResult.Finalizado toResultFinalizado(
            FinalizacionProyectoGradoDomain finalizacion, PurgaArtefactosProyectoGradoResult purgaArtefactos) {
        return new FinalizacionProyectoGradoResult.Finalizado(
                finalizacion.getProyectoGrado(), purgaArtefactos.eliminadas());
    }

    public static FinalizacionProyectoGradoResult.Duplicado toResultDuplicado(
            FinalizacionProyectoGradoDomain finalizacion, PurgaArtefactosProyectoGradoResult purgaArtefactos) {
        return new FinalizacionProyectoGradoResult.Duplicado(
                finalizacion.getProyectoGrado(), purgaArtefactos.eliminadas());
    }
}
