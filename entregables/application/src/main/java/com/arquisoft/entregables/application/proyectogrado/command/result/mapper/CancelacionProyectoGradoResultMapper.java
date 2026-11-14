package com.arquisoft.entregables.application.proyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.CancelacionProyectoGradoDomain;

public final class CancelacionProyectoGradoResultMapper {

    private CancelacionProyectoGradoResultMapper() {}

    public static CancelacionProyectoGradoResult.Cancelado toResultCancelado(
            CancelacionProyectoGradoDomain cancelacion) {
        return new CancelacionProyectoGradoResult.Cancelado(cancelacion.getProyectoGrado());
    }

    public static CancelacionProyectoGradoResult.Duplicado toResultDuplicado(
            CancelacionProyectoGradoDomain cancelacion) {
        return new CancelacionProyectoGradoResult.Duplicado(cancelacion.getProyectoGrado());
    }
}
