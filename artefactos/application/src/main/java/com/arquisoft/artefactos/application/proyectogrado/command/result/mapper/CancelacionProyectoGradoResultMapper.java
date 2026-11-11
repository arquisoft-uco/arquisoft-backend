package com.arquisoft.artefactos.application.proyectogrado.command.result.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.artefactos.domain.proyectogrado.CancelacionProyectoGradoDomain;

public final class CancelacionProyectoGradoResultMapper {

    private CancelacionProyectoGradoResultMapper() {}

    public static CancelacionProyectoGradoResult.Cancelado toResultCancelado(CancelacionProyectoGradoDomain cancelacion) {
        return new CancelacionProyectoGradoResult.Cancelado(cancelacion.getProyectoGrado());
    }

    public static CancelacionProyectoGradoResult.YaCancelado toResultYaCancelado(CancelacionProyectoGradoDomain cancelacion) {
        return new CancelacionProyectoGradoResult.YaCancelado(cancelacion.getProyectoGrado());
    }
}
