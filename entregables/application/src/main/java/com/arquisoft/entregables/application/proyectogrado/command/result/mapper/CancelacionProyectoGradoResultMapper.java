package com.arquisoft.entregables.application.proyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.CancelacionProyectoGradoDomain;

public final class CancelacionProyectoGradoResultMapper {

    private CancelacionProyectoGradoResultMapper() {}

    public static CancelacionProyectoGradoResult.Cancelado toResultCancelado(
            CancelacionProyectoGradoDomain cancelacion, PurgaPaquetesProyectoGradoResult purgaPaquetes,
            PurgaArtefactosProyectoGradoResult purgaArtefactos) {
        return new CancelacionProyectoGradoResult.Cancelado(
                cancelacion.getProyectoGrado(), purgaPaquetes.eliminados(), purgaArtefactos.eliminadas());
    }

    public static CancelacionProyectoGradoResult.Duplicado toResultDuplicado(
            CancelacionProyectoGradoDomain cancelacion, PurgaPaquetesProyectoGradoResult purgaPaquetes,
            PurgaArtefactosProyectoGradoResult purgaArtefactos) {
        return new CancelacionProyectoGradoResult.Duplicado(
                cancelacion.getProyectoGrado(), purgaPaquetes.eliminados(), purgaArtefactos.eliminadas());
    }
}
